package scwilio
package twiml

import org.specs.Specification
import scala.xml._
import in.org.patel.xmldiff._

object TwiMLSpec extends Specification {

  val comp = new Comparison

  "TwiML" should {

    "generate correct XML for Dial verb" in {
      val r = Dial(from = Phonenumber("+1999"), to = Phonenumber("+1888"))
      val generated = TwiML(r)
      val expected = <Dial callerId="+1999" timeout="30">+1888</Dial>
      comp(expected, generated) must_== NoDiff
    }

    "generate correct XML for Dial verb - with action url" in {
      val r = Dial(from = Phonenumber("+1999"), to = Phonenumber("+1888"), onEnd = Some("http://url"))
      val generated = TwiML(r)
      val expected = <Dial action="http://url" callerId="+1999" timeout="30">+1888</Dial>
      comp(expected, generated) must_== NoDiff
    }

    "generate correct XML for Gather verb" in {
      val r = Gather(timeout = 30, finishOnKey = '*', numDigits = 4, onGathered = Some("http://digits"))
      val generated = TwiML(r)
      val expected = <Gather action="http://digits" finishOnKey="*" numDigits="4" timeout="30"></Gather>
      comp(expected, generated) must_== NoDiff
    }

    "generate correct XML for a complex VoiceResponse" in {
      val r = VoiceResponse(
        Say("Hello, world"),
        Pause(),
        Play("http://foo.com/cowbell.mp3"),
        Say("Goodbye"),
        Dial(from = Phonenumber("+1999"), to = Phonenumber("+1888"), onEnd = Some("http://test")),
        ConnectToConference("cid", Some("http://callback"), Some("http://wait"), muted = false, startOnEnter = false, endOnExit = false),
        Gather(timeout = 30, finishOnKey = '*', numDigits = 4, onGathered = Some("http://digits")),
        Redirect("http://foo.com/"))

      val generated = TwiML(r)
      val expected =
        <Response>
          <Say voice="man" language="en" loop="1">Hello, world</Say>
          <Pause length="1"/>
          <Play loop="1">http://foo.com/cowbell.mp3</Play>
          <Say voice="man" language="en" loop="1">Goodbye</Say>
          <Dial callerId="+1999" action="http://test" timeout="30">+1888</Dial>
          <Dial action="http://callback">
            <Conference waitUrl="http://wait" muted="false" startConferenceOnEnter="false" endConferenceOnExit="false">cid</Conference>
          </Dial>
          <Gather action="http://digits" finishOnKey="*" numDigits="4" timeout="30"/>
          <Redirect>http://foo.com/</Redirect>
        </Response>

      comp(expected, generated) must_== NoDiff
    }
  }
}
