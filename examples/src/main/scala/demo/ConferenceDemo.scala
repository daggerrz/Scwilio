package demo

import scwilio._
import twiml._
import uf._
import callback._
import util.Logging

object ConferenceDemo extends Application with Logging {


  val port = 8080
  val urlBase = "http://" + IPTool.publicIp + ":" + port
  val tw = new TwilioClient(new RestClient(DemoConfig.accountSid, DemoConfig.authToken))
  val server = new UnfilteredPhone(urlBase, port)

  val CONF_ID = "the-conference"
  import server.URLMaker._

  def waitInformation: () => VoiceResponse = {
    var redirects = 0
    def waitInfo() : VoiceResponse = {
      redirects += 1
      VoiceResponse(
        Say("Please wait for others to join. You have waited " + redirects + " times", voice = Voice.Woman),
        twiml.Redirect(waitInfo _)
      )
    }
    waitInfo _
  }

  def selectConference(call: ActiveCall) : VoiceResponse = {
    VoiceResponse(
      Say("Welcome. Please enter your conference pin, followed by hash."),
      Gather(4, onGathered = connectToConference _ )
    )
  }

  def connectToConference(call: ActiveCall) : VoiceResponse = {
    val confId = call.digits.get
    VoiceResponse(
      Say("Connecting you to conference " + confId.mkString(" ")),
      ConnectToConference(confId,
        (call : DialOutcome) => log.info("Caller " + call + " left the conference"),
        { waitInformation }
      )
    )
  }

  server.incomingCallHandler = Some(selectConference _)

/*    tw.dial("+13477078794", "+4790055383",
      onConnect = (call: ActiveCall) => call.answeredBy match {
        case Some(Machine) =>
          log.info("Argh! Machine! Hanging up.")
          VoiceResponse(Hangup)
        case _ => connectToConference(call)
        },
      onEnd = (outcome : DialOutcome) => log.info("Call " + outcome + " ended after " + outcome.duration + " seconds")
  )*/


}