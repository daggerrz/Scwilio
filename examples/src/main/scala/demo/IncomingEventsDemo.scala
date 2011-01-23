package demo
import scwilio._
import twiml._
import callback._
import scwilio.uf.TwiMLResponse._

import unfiltered.request._

object IncomingEventsDemo extends Application {

  val client = new TwilioClient(new RestClient(DemoConfig.accountSid, DemoConfig.authToken))

  val url = AbsoluteUrl("/incoming/voice")

  // Dangerous operation. Commented out for safety.
//  client.listIncomingNumbers().foreach { n =>
 //   client.updateIncomingNumberConfig(n.sid, IncomingNumberConfig(voiceUrl = url))
 //   println(n.number + " is now routed to " + url.get)
 // }

  import scwilio.uf.Helpers._
  unfiltered.jetty.Http(DemoConfig.port).filter(unfiltered.filter.Planify {
       case POST(Path("/incoming/voice") & Params(p)) =>
         val call = ActiveCall.parse(p)
         println(call)
         VoiceResponse(Say("Hello, thanks for calling"),
         Dial(from = "+13477078794" , to = "+19179817381"))
  }).start
}