package scwilio

import op._
import xml._
import dispatch._

object Twilio {

  var accountSid = System.getenv("SCWILIO_ACCOUNT_SID")
  var authToken = System.getenv("SCWILIO_AUTH_TOKEN")


  def apply() = new TwilioClient(accountSid, authToken)
}

trait HttpConfig {
  val accountSid: String
  val authToken: String
  lazy val TWILIO_BASE = :/("api.twilio.com").secure as (accountSid, authToken)
  lazy val API_BASE =  TWILIO_BASE / "2010-04-01" / "Accounts" / accountSid
  val http = new Http
}

class TwilioClient(val accountSid: String, val authToken: String) extends HttpConfig with util.Logging {
  import scala.xml._

  def execute[R](op: TwilioOperation[R]) : R  = {
    val req = op.request(this)
    log.info("Sending req {}", req)
    try {
      http(req <> { res =>
        log.debug("Twilio response:\n{}", res)
        op.parse(res)
      })
    } catch {
      case e : dispatch.StatusCode =>
        val res: Elem = XML.loadString(e.contents)
        if (!(res \ "RestException").isEmpty) {
          throw TwilioClient.parseException(res)
        } else {
          throw e
        }

    }
  }

  def executeAsync[R](req: TwilioOperation[R], handler: (Either[R, scala.Exception]) => Unit) = null.asInstanceOf[R]

  def listAvailableNumber(countryCode: String) = execute(ListAvailableNumbers(countryCode))

  def getConferenceState(cid: String) = {
    val (state, uris) = execute(GetConferenceParticipantURIs(cid))

    val participants = uris.map { uri => execute(GetConferenceParticipantInfo(uri)) }
    ConferenceState(cid, state, participants)
  }


}

object TwilioClient {
  def parseException(res: NodeSeq) = {
    val error = res \ "RestException"
    new TwilioException((error \ "Code").text, (error \ "Message").text)
  }
}

class TwilioException(val code: String, val message: String) extends RuntimeException("Error code " + code + ". " + message)
