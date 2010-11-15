package scwilio
package twiml

import org.specs.Specification
import scala.xml._


object TwiMLSpec extends Specification {

  "TwiML" should {
    "generate correct XML for a VoiceResponse" in {
      val r = VoiceResponse(
          Say("Hello, world"),
          Pause(),
          Play("http://foo.com/cowbell.mp3"),
          Say("Goodbye"),
          Dial(from = Phonenumber("+1999"), to = Phonenumber("+1888"), callbackUrl = Some("http://test")),
          ConnectToConference("cid", Some("http://callback"), Some("http://wait"), muted = false, startOnEnter = false, endOnExit = false )
      )

      val ml = TwiML(r)

      Utility.trim(ml) must_== Utility.trim(
        <Response>
           <Say voice="man" language="en" loop="1">Hello, world</Say>
           <Pause length="1"/>
           <Play loop="1">http://foo.com/cowbell.mp3</Play>
           <Say voice="man" language="en" loop="1">Goodbye</Say>
           <Dial callerId="+1999" action="http://test" timeout="30">+1888</Dial>
           <Dial action="http://callback">
             <Conference waitUrl="http://wait" muted="false" startConferenceOnEnter="false" endConferenceOnExit="false">cid</Conference>
           </Dial>
        </Response>)

    }
  }

}