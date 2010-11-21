package demo
import scwilio._
import scwilio.uf.TwiMLResponse._

import unfiltered.request._

object IncomingEventsDemo extends Application {

  val tw = new TwilioClient(new RestClient(DemoConfig.accountSid, DemoConfig.authToken))

  val url = AbsoluteUrl("/greet")
  tw.listIncomingNumbers().foreach { n =>
    tw.updateIncomingNumberConfig(n.sid, IncomingNumberConfig(voiceUrl = url))
    println(n.number + " is now routed to " + url.get)
  }

  unfiltered.jetty.Http(DemoConfig.port).filter(unfiltered.filter.Planify {
       case Path("/greet", req) =>
          Say("Hello, thanks for calling")
  }).start
}