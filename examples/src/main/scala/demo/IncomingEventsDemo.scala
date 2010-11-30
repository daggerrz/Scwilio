package demo
import scwilio._
import callback._
import scwilio.uf.TwiMLResponse._

import unfiltered.request._

object IncomingEventsDemo extends Application {

  val tw = new TwilioClient(new RestClient(DemoConfig.accountSid, DemoConfig.authToken))

  val url = AbsoluteUrl("/incoming/voice")

  // Dangerous operation. Commented out for safety.
  tw.listIncomingNumbers().foreach { n =>
    tw.updateIncomingNumberConfig(n.sid, IncomingNumberConfig(voiceUrl = url))
    println(n.number + " is now routed to " + url.get)
  }

  import scwilio.uf.Helpers._
  unfiltered.jetty.Http(DemoConfig.port).filter(unfiltered.filter.Planify {
       case POST(Path("/incoming/voice", Params(p, _))) =>
         val call = ActiveCall.parse(p)
         println(call)
         Say("Hello, thanks for calling")
  }).start
}