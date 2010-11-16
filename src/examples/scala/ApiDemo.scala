package scwilio

import op._
import Phonenumber._

import unfiltered.jetty.Server
import unfiltered.request._
import unfiltered.response._

object ApiDemo extends Application {

  Twilio.accountSid = "x"
  Twilio.authToken = "x"

  unfiltered.jetty.Http(8080).filter(unfiltered.filter.Planify {
       case _ => Ok ~> TwiMLResponse(
           VoiceResponse(
             Say("Hello there!"),
             Pause(5),
             Say("Still waiting? kthx, bye"),
             Hangup
           )
         )
  }).start

  val publicIp = IPTool.publicIp

  // Firewall needs to be opened on port 8080, obviously
  val res = Twilio().dial("+13477078794", "+4790055383", "http://" + publicIp + ":8080/", timeout = 60)
  println(res)

}

object IPTool {
  def publicIp = {
    import dispatch._
    import scala.xml._
    val http = new Http
    val req = :/("ip-address.domaintools.com") / "myip.xml"
    http(req <> { _ \\ "ip_address" text } )
  }
}

/** Unfiltered responder for TwiML **/
case class TwiMLResponse(vr: VoiceResponse) extends ChainResponse(ContentType("application/xml") ~> ResponseString(twiml.TwiML(vr).toString))
