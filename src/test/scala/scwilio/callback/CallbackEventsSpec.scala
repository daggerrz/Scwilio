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
        "ForwardedFrom" -> "+14444"
      )

      IncomingCall.parse(params) must_== IncomingCall("sid", Phonenumber("+4790055383"), Phonenumber("+15555"), Some(Phonenumber("+14444")))
    }
  }
}