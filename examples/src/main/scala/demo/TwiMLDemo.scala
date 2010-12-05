package demo

import unfiltered.request._
import unfiltered.response._

import scwilio._
import scwilio.twiml._
import scwilio.uf.TwiMLResponse._
import scwilio.Phonenumber._


object TwiMLDemo extends Application {

  Twilio.accountSid = DemoConfig.accountSid
  Twilio.authToken = DemoConfig.authToken

  implicit def string2Option(s: String) : Option[String] = Some(s)

  unfiltered.jetty.Http(DemoConfig.port).filter(unfiltered.filter.Planify {
       case Path("/outgoing", req) =>
           VoiceResponse(
             Say("Hello there! Please enter your 4 digit secret code followed by star"),
             Gather(numDigits = 4,  finishOnKey = '*', timeout = 5, onGathered = AbsoluteUrl("/digits")),
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
  val res = Twilio().dial("+13477078794", "+4790055383", AbsoluteUrl("/outgoing"), timeout = 60)
  println(res)

}





