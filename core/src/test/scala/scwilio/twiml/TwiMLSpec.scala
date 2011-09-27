package scwilio
package twiml

import org.specs.Specification
import scala.xml._


object TwiMLSpec extends Specification {

  "TwiML" should {

    "generate correct XML for Dial verb" in { 
      val r = Dial(from = Phonenumber("+1999"), to = Phonenumber("+1888"))
      val ml = XML.loadString(TwiML(r).toString) // reload XML to remove whitespace
      val expected = XML.loadString("""<Dial callerId="+1999">+1888</Dial>""")
      ml must_== expected
    }

    "generate correct XML for Dial verb - with action url" in { 
      val r = Dial(from = Phonenumber("+1999"), to = Phonenumber("+1888"), onConnect=Some("http://url"))
      val ml = XML.loadString(TwiML(r).toString) // reload XML to remove whitespace
      val expected = XML.loadString("""<Dial action="http://url" callerId="+1999">+1888</Dial>""")
      ml must_== expected
    }

    "generate correct XML for a complex VoiceResponse" in {
      val r = VoiceResponse(
          Say("Hello, world"),
          Pause(),
          Play("http://foo.com/cowbell.mp3"),
          Say("Goodbye"),
          Dial(from = Phonenumber("+1999"), to = Phonenumber("+1888"), onConnect = Some("http://test")),
          ConnectToConference("cid", Some("http://callback"), Some("http://wait"), muted = false, startOnEnter = false, endOnExit = false ),
          Gather(timeout = 30, finishOnKey = '*', numDigits = 4, onGathered = Some("http://digits")),
          Redirect("http://foo.com/")
      )

      val ml = TwiML(r)

      Utility.trim(ml) must_== Utility.trim(
        <Response>
           <Say voice="man" language="en" loop="1">Hello, world</Say>
           <Pause length="1"/>
           <Play loop="1">http://foo.com/cowbell.mp3</Play>
           <Say voice="man" language="en" loop="1">Goodbye</Say>
           <Dial callerId="+1999" action="http://test">+1888</Dial>
           <Dial action="http://callback">
             <Conference waitUrl="http://wait" muted="false" startConferenceOnEnter="false" endConferenceOnExit="false">cid</Conference>
           </Dial>
           <Gather action="http://digits" finishOnKey="*" numDigits="4" timeout="30"/>
           <Redirect>http://foo.com/</Redirect>
        </Response>)
    }
  }
}
