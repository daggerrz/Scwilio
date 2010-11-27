package demo
import scwilio._
import callback._
import scwilio.uf.TwiMLResponse._

import unfiltered.request._

object IncomingEventsDemo extends Application {

  val tw = new TwilioClient(new RestClient(DemoConfig.accountSid, DemoConfig.authToken))

  val url = AbsoluteUrl("/incoming/voice")

  tw.listIncomingNumbers().foreach { n =>
    tw.updateIncomingNumberConfig(n.sid, IncomingNumberConfig(voiceUrl = url))
    println(n.number + " is now routed to " + url.get)
  }

  import scwilio.uf.Params._
  unfiltered.jetty.Http(DemoConfig.port).filter(unfiltered.filter.Planify {
       case POST(Path("/incoming/voice", Params(p, _))) =>
         val call = IncomingCall.parse(p)
         Say("Hello, thanks for calling")
  }).start
}