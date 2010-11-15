package scwilio
package op

import xml._

/**
 * Trait for all Twilio REST operation. Each operation is responsible
 * for creating the Twilio HTTP Request and parsing the results (if the
 * operation was successful).
 */
trait TwilioOperation[R] {
  /**
   * Creates the HTTP op.
   */
  def request(conf: HttpConfig): dispatch.Request

  /**
   * Parses the XML response.
   */
  def parse(nodes: NodeSeq): R
}


/**
 * Defines Twilio REST operations.
 */
case class ListAvailableNumbers(countryCode: String) extends TwilioOperation[Seq[Phonenumber]] {
  def request(conf: HttpConfig) = conf.API_BASE / "AvailablePhoneNumbers" / countryCode / "Local"

  def parse(nodes: NodeSeq) = {
    for (num <- nodes \\ "AvailablePhoneNumber" \ "PhoneNumber")
    yield {
      Phonenumber(num.text)
    }
  }
}

case class DialOperation(dial: Dial) extends TwilioOperation[CallInfo] {

  def request(conf: HttpConfig) = {
    var params = Map(
      "From" -> dial.from.toStandardFormat,
      "To" -> dial.to.toStandardFormat
    )
    dial.callbackUrl.map ( params += "Url" -> _ )

    conf.API_BASE / "Calls" << params
  }

  def parse(res: NodeSeq) = {
    val call = res \ "Call"
    CallInfo(
      (call \ "Sid").text,
      Phonenumber((call \ "From").text),
      Phonenumber.parse((call \ "To").text),
      (call \ "Uri").text
    )
  }
}

/**
 * Gets the URIs for the participants resources in a conference
 */
case class GetConferenceParticipantURIs(cid: String) extends TwilioOperation[Tuple2[String, Seq[String]]] {
  def request(conf: HttpConfig) = conf.API_BASE / "Conferences" / cid

  def parse(res: NodeSeq) = {
    val conf = res \ "Conference"

    (
      (conf \ "Status").text ->
        (conf \ "SubresourceUris" \ "Participants").map{
          _.text
        }
      )
  }
}

case class GetConferenceParticipantInfo(uri: String) extends TwilioOperation[Participant] {
  def request(conf: HttpConfig) = conf.TWILIO_BASE / uri

  def parse(res: NodeSeq) = {
    val part = res \ "Participant"
    Participant((part \ "CallSid").text, if ("true" == (part \ "Muted").text) true else false)
  }
}