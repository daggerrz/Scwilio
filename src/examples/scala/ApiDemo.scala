package scwilio
import op._

object ApiDemo extends Application {

  Twilio.accountSid = "x"
  Twilio.authToken = "x"

  Twilio().execute(DialOperation(Dial(from=Phonenumber("+4790055383"), to=Phonenumber("+4790055383"))))

    val ourNumber = Phonenumber("+190055383")

    Dial(ourNumber, Phonenumber("+144444444"))

/*    def respond(o : DialOutcome) =
      o.state match {
        case Success =>
          Some(VoiceResponse(
            Say("Hello, forwarding you to +1333333"),
            Dial(ourNumber, Phonenumber("+1333333"))
          ))

        case NoAnswer =>
          println("No answer")
          None

        case Busy =>
          println("Busy")
          None

        case Failed =>
          println("Failed")
          None
      }*/
}