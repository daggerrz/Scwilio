package scwilio.op
import scwilio._

import xml._

/**
 * Trait for all Twilio REST operation. Each operation is responsible
 * for creating the Twilio HTTP Request and parsing the results (if the
 * operation was successful).
 *
 * One might argue that the ops themselves should not be responsible for the plumbing,
 * but this is simple and easy to understand, so we'll keep it like this for now.
 */
trait TwilioOperation[R] {

  /**
   * Create a dispatch request for the op.
   */
  def request(conf: HttpConfig): dispatch.Request

  /**
   * Return a function to parse the result for the op.
   */
  def parser: NodeSeq => R
}

/**
 * Implicit conversions to ease Twilio XML parsing.
 */
protected object XmlPredef {

  /**
   * Let a NodeSeq's text be converted to a Option[String]. Returns None if the
   * text is empty.
   */
  implicit def nodeSeq2StringOption(nodes: NodeSeq) : Option[String] = nodes.text match {
    case "" => None
    case s: String => Some(s)
  }

  /**
   * Let a NodeSeq's text be converted to a String. Returns an empty String if the
   * text is empty.
   */
  implicit def nodeSeq2String(nodes: NodeSeq) : String = nodes.text

}

/**
 * List available phone numbers for purchase in a given country.
 */
case class ListAvailableNumbers(countryCode: String) extends TwilioOperation[Seq[Phonenumber]] {
  def request(conf: HttpConfig) = conf.API_BASE / "AvailablePhoneNumbers" / countryCode / "Local"

  val parser = { nodes: NodeSeq =>
    for (num <- nodes \\ "AvailablePhoneNumber" \ "PhoneNumber")
    yield {
      Phonenumber(num.text)
    }
  }
}

/**
 * Dial a phone number.
 *
 * <b>Note:<b> We do not support IfMachine -> Hangup as this breaks
 * our state management. If Hangup were allowed,
 * we would get "active call" callbacks with state Completed
 * which is weird, and we would also not get a callback when the call
 * is completed which is inconsistent. If machine detection is used,
 * the response function must check the answeredBy field and rather
 * issue a Hangup manually.
 */
case class DialOperation(
    from: Phonenumber,
    to: Phonenumber,
    callbackUrl: Option[String],
    statusCallbackUrl: Option[String] = None,
    timeout: Int = 30,
    machineDetection: Boolean = false
  ) extends TwilioOperation[CallInfo] {

  def request(conf: HttpConfig) = {
    val params = Map(
      "From" -> from.toStandardFormat,
      "To" -> to.toStandardFormat
    ) ++
      callbackUrl.map("Url" -> _) ++
      statusCallbackUrl.map("StatusCallback" -> _) ++
      (if (machineDetection) Some("IfMachine" -> "Continue") else None)

    conf.API_BASE / "Calls" << params
  }

  def parser = DialOperation.parse _
}

object DialOperation {
  import XmlPredef._

  def parse(res: NodeSeq) = {
    val call = res \ "Call"
    CallInfo(
      call \ "Sid",
      Phonenumber(call \ "From"),
      Phonenumber.parse(call \ "To"),
      call \ "Uri"
    )
  }
}

case class SendSms(from: Phonenumber, to: Phonenumber, body: String) extends TwilioOperation[SmsInfo] {
  def request(conf: HttpConfig) = {
    val params = Map(
      "From" -> from.toStandardFormat,
      "To" -> to.toStandardFormat,
      "Body" -> body
    )
    conf.API_BASE / "SMS" / "Messages" << params
  }

  def parser = SendSms.parse _
}

object SendSms {
  import XmlPredef._

  def parse(res: NodeSeq) = {
    val msg = res \ "SMSMessage"
    SmsInfo(
      msg \ "Sid",
      Phonenumber(msg \ "From"),
      Phonenumber.parse(msg \ "To"),
      msg \ "Uri"
    )

  }

}

/**
 *  Update the configuration for an incoming phone number.
 */
case class UpdateIncomingNumberConfig(sid: String, config: IncomingNumberConfig) extends TwilioOperation[IncomingNumber] {
  def request(conf: HttpConfig) = {

    val options = List(
      (config.friendlyName -> "FriendlyName"),
      (config.voiceUrl ->"VoiceUrl"),
      (config.statusCallbackUrl -> "StatusCallbackUrl"),
      (config.smsUrl -> "SmsUrl"),
      (config.smsFallbackUrl -> "SmsFallbackUrl")
    )

    val params = Map(
      "ApiVersion" -> Twilio.API_VERSION,
      "VoiceMethod" -> "POST",
      "VoiceFallbackMethod" -> "POST",
      "StatusCallbackMethod" -> "POST",
      "SmsMethod" -> "POST",
      "SmsFallbackMethod" -> "POST"
    ) ++ options.flatMap{ case (value, optionName) => value.map(optionName -> _) }

    conf.API_BASE / "IncomingPhoneNumbers" / sid << params
  }

  def parser = IncomingNumbersParser.parse _ andThen { _.head }
}

object IncomingNumbersParser {
  import XmlPredef._
  def parse(nodes: NodeSeq) : Seq[IncomingNumber] = {
    (nodes \\ "IncomingPhoneNumber").map { n =>
      IncomingNumber(
        n \ "Sid",
        Phonenumber(n \ "PhoneNumber"),
        IncomingNumberConfig(
          n \ "FriendlyName",
          n \ "VoiceUrl",
          n \ "VoiceFallbackUrl",
          n \ "StatusCallbackUrl",
          n \ "SmsUrl",
          n \ "SmsFallbackUrl"
        )
      )
    }
  }
}

/**
 * List all available incoming phone numbers.
 */
case object ListIncomingNumbers extends TwilioOperation[Seq[IncomingNumber]] {
  def request(conf: HttpConfig) = conf.API_BASE / "IncomingPhoneNumbers"
  def parser = IncomingNumbersParser.parse _
}

/**
 * Get the URIs for the participants resources in a conference.
 */
case class GetConferenceParticipantURIs(cid: String) extends TwilioOperation[(String, Seq[String])] {
  def request(conf: HttpConfig) = conf.API_BASE / "Conferences" / cid

  def parser = { res: NodeSeq =>
    val conf = res \ "Conference"
    (conf \ "Status").text -> (conf \ "SubresourceUris" \ "Participants").map{ _.text }
  }

}

/**
 * Get info about a particular conference participant.
 */
case class GetConferenceParticipantInfo(uri: String) extends TwilioOperation[ConferenceParticipant] {
  def request(conf: HttpConfig) = conf.TWILIO_BASE / uri

  def parser = { res: NodeSeq =>
    val part = res \ "Participant"
    ConferenceParticipant((part \ "CallSid").text, if ("true" == (part \ "Muted").text) true else false)
  }
}
