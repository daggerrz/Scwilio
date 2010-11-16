package demo

import unfiltered.request._
import unfiltered.response._
import unfiltered.jetty.{Http => Server}

import scwilio._
import scwilio.twiml._
import scwilio.uf.TwiMLResponse._


object ApiDemo extends Application {

  Twilio.accountSid = "sid"
  Twilio.authToken = "token"

  Server(8080).filter(unfiltered.filter.Planify {
       case Path("/outgoing", req) =>
           VoiceResponse(
             Say("Hello there! Please enter your 4 digit secret code followed by star"),
             Gather(numDigits = 4,  finishOnKey = '*', timeout = 5, callbackUrl = Some(RelativeUrl("/digits"))),
             Say("Sorry, you are too slow for us. Bye."),
             Hangup
           )
       case Path("/digits", Params(params, req)) =>
         params("Digits") match {
           case Seq(digits) =>
             VoiceResponse(
                Say("You entered, " + digits.toArray.mkString(", ") + ". Good job!"),
                Pause(1),
                Say("Bye!"),
                Hangup
              )
           case _ => Say("Sorry, no digits entered")
         }
  }).start


  // Firewall needs to be opened on port 8080, obviously
  val res = Twilio().dial("+13477078794", "+4790055383", RelativeUrl("/outgoing"), timeout = 60)
  println(res)

}

object RelativeUrl {
  lazy val publicIp = IPTool.publicIp
  def apply(url: String) = "http://" + publicIp + ":8080" + url
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

