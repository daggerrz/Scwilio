package scwilio

import op._
import xml._
import dispatch._

object Twilio {

  var accountSid = System.getenv("SCWILIO_ACCOUNT_SID")
  var authToken = System.getenv("SCWILIO_AUTH_TOKEN")
  val API_VERSION = "2010-04-01"

  lazy val restClient = new RestClient(accountSid, authToken)
  lazy val client = new TwilioClient(restClient)
  def apply() = client
}

/**
 * Holds configuration data for the Twilio API.
 */
trait HttpConfig {
  val accountSid: String
  val authToken: String
  lazy val TWILIO_BASE = :/("api.twilio.com").secure as (accountSid, authToken)
  lazy val API_BASE =  TWILIO_BASE / Twilio.API_VERSION / "Accounts" / accountSid
  val http = new Http
}

/**
 * Low level client for the Twilio API. Works with TwilioOperation instances.
 */
class RestClient(val accountSid: String, val authToken: String) extends HttpConfig with util.Logging {
  import scala.xml._

  def execute[R](op: TwilioOperation[R]) : R  = {
    val req = op.request(this)
    log.info("Sending req {}", req)
    try {
      http(req <> { res =>
        log.debug("Twilio response:\n{}", res)
        op.parser.apply(res)
      })
    } catch {
      case e : dispatch.StatusCode =>
        val res: Elem = XML.loadString(e.contents)
        if (!(res \ "RestException").isEmpty) {
          throw RestClient.parseException(res)
        } else {
          throw e
        }

    }
  }
}

object RestClient {
  def parseException(res: NodeSeq) = {
    val error = res \ "RestException"
    new TwilioException((error \ "Code").text, (error \ "Message").text)
  }
}

/**
 * Client service interface hiding REST operation details.
 */
class TwilioClient(private val restClient: RestClient) {

  def dial( from: Phonenumber,
              to: Phonenumber,
              callbackUrl: String,
              statusCallbackUrl: Option[String] = None,
              timeout: Int = 30
          ) = restClient.execute(DialOperation(from, to, callbackUrl, statusCallbackUrl, timeout))

  def listAvailableNumber(countryCode: String) = restClient.execute(ListAvailableNumbers(countryCode))

  def getConferenceState(cid: String) = {
    val (state, uris) = restClient.execute(GetConferenceParticipantURIs(cid))

    val participants = uris.map ( uri => restClient.execute(GetConferenceParticipantInfo(uri)) )
    ConferenceState(cid, state, participants)
  }
}

class TwilioException(val code: String, val message: String) extends RuntimeException("Error code " + code + ". " + message)
