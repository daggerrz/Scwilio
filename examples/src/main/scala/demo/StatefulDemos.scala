package demo

import scwilio._
import twiml._
import uf._
import callback._
import util.Logging

object DemoPhone {
  def apply(port: Int) = {
    val urlBase = "http://" + IPTool.publicIp + ":" + port
    new UnfilteredPhone(urlBase)
  }
}

object ConferenceDemo extends Application with Logging {

  val port = 8080
  val phone = DemoPhone(port)
  private val http = unfiltered.jetty.Http(port).filter(phone.callBackPlan).start

  // Import to enable function references to be implicitly converted into
  // Unfiltered HTTP callbacks.
  import phone.URLMaker._

  // Prompt the user for a conference pin. This is really just
  // a conference ID and any four digit pin will do.
  def selectConference(call: ActiveCall) : VoiceResponse = {
    VoiceResponse(
      Say("Welcome. Please enter your four digit conference pin, followed by hash."),
      Gather(4, onGathered = connectToConference _ )
    )
  }

  // Validate the pin and connect the user to a conference.
  def connectToConference(call: ActiveCall) : VoiceResponse = {

    call.digits match {
      case Some(confId) if confId.length != 4 =>
        VoiceResponse(
          Say("Pin should be four digits. Please enter four digits followed by hash."),
          Gather(4, onGathered = connectToConference _)
        )
      case Some(confId) =>
        VoiceResponse(
          Say("Connecting you to conference " + confId.mkString(", ")),
          ConnectToConference(confId,
            onLeave = (call : CompletedCall) => println("Caller " + call + " left the conference"),
            onWait = pleaseWait _
          )
        )
      case None => VoiceResponse(Say("Sorry, dunno how you got here"), Hangup)
    }
  }

  // Say something to the user while waiting for the conference to begin.
  // Demonstrates stateful callbacks.
  def pleaseWait() : VoiceResponse = {
    var redirects = 0
    def waitInfo() : VoiceResponse = {
      redirects += 1
      VoiceResponse(
        Say("Please wait for others to join. You have waited " + redirects + " times", voice = Voice.Woman),
        Redirect(waitInfo _)
      )
    }
    waitInfo
  }

  phone.callHandler = Some(selectConference _)

}

object StatefulDialDemo extends Application {

  val port = 8080
  val phone = DemoPhone(8080)
  private val http = unfiltered.jetty.Http(port).filter(phone.callBackPlan).start

  // Import to enable function references to be implicitly converted into
  // Unfiltered HTTP callbacks.
  import phone.URLMaker._

  val client = new TwilioClient(new RestClient(DemoConfig.accountSid, DemoConfig.authToken))

  client.dial(from = "+13477078794", to = "+4790055383",
    onConnect = (call: ActiveCall) => call.answeredBy match {
      case Some(Machine) =>
        println("Argh! Machine! Hanging up.")
        VoiceResponse(Hangup)
      case _ =>
        VoiceResponse(
          Say("This is an automated call. Nothing to hear here."),
          Pause(10),
          Say("Really!"),
          Hangup
        )
      },
    onEnd = (outcome : CompletedCall) =>
      println("Call " + outcome + " ended after " + outcome.duration + " seconds"),
    machineDetection = true
  )

}