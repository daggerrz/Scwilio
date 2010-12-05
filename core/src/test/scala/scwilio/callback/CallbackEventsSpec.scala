package scwilio
package callback

import org.specs.Specification

class CallbackEventsSpec extends Specification {

  "IncomingCall" should {
    "parse correctly" in {
      val params = Map(
        "CallSid" -> "sid",
        "AccountSid" -> "accsid",
        "From" -> "+4790055383",
        "To" -> "+15555",
        "CallStatus" -> "ringing",
        "Direction" -> "inbound",
        "ForwardedFrom" -> "+14444",
        "AnsweredBy" -> "human",
        "Digits" -> "555"
      )

      ActiveCall.parse(params) must_==
        ActiveCall(
          "sid",
          Phonenumber("+4790055383"), Phonenumber("+15555"),
          Ringing,
          Some(Phonenumber("+14444")),
          Some(Human),
          Some("555")
        )
    }
  }
 "IncomingSms" should {
    "parse correctly" in {
      val params = Map(
        "SmsSid" -> "sid",
        "AccountSid" -> "accsid",
        "From" -> "+4790055383",
        "To" -> "+15555",
        "Body" -> "Hello world!"
      )

      IncomingSms.parse(params) must_== IncomingSms("sid", Phonenumber("+4790055383"), Phonenumber("+15555"), "Hello world!")
    }
  }
}