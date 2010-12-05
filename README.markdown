# Scwilio

Scwilio is a Scala API for Twilio. It is currently under heavy development, but most of the major Twilio functionality
is supported. The API aims to deliver Twilio functionality in several layers of abstraction:

1. Basic Twilio methods and TwiML generation
2. Higher level "phone devices" where all HTTP and URL plumbing is abstracted away and replace with plain functions
3. An Actor API

1 works well (although a few API methods are missing), 2 is working well but might still be refactored quite a bit.
3 should be easy once 2 is done. :)

## Basic Twilio methods and TwiML generation

To invoke Twilio methods, get an `TwilioClient` instance:

    import scwilio._
    val client = Twilio(ACCOUNT_SID, AUTH_TOKEN)

Then, invoke a method, e.g. for dialing a number or send an SMS:

    client.dial(from = "+13477078794", to = "+4790055383", onConnect = Some("http://url.to.some.twiml"))
    client.sendSms("+13152273664", "+4790055383", "Hello, there!")

To generate some TwiML, put this in a handler of whatever web framework your're using:

    import scwilio.twiml._

    val response = VoiceResponse(
               Say("Hello, world"),
               Pause(10),
               Play("http://foo.com/cowbell.mp3"),
               Say("Dialing 8 8 8 8"),
               Dial(from = Phonenumber("+1999"), to = Phonenumber("+1888"), onConnect = Some("http://test"))
             )
    val stringResponse = TwiML(response).toString
    // Write the response

## Phone devices

Making stateful Twilio services can be a pain. Using `Phone` instances, the HTTP plumbing can
can be removed completely. This allows for code like this:

    val phone = DemoPhone()

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
        onEnd = (outcome : DialOutcome) =>
          println("Call " + outcome + " ended after " + outcome.duration + " seconds")
    )

Pretty neat, right?

This is still work in progress, but here's a more involved example of creating a basic conference
service using a `Phone` implementation using the Unfiltered web framework (see StatefulDemos example):

    object ConferenceDemo extends Application with Logging {

      val phone = DemoPhone()

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
                onLeave = (call : DialOutcome) => println("Caller " + call + " left the conference"),
                onWait = { pleaseWait }
              )
            )
          case None => VoiceResponse(Say("Sorry, dunno how you got here"), Hangup)
        }
      }

      // Say something to the user while waiting for the conference to begin.
      // Demonstrates stateful callbacks.
      def pleaseWait: () => VoiceResponse = {
        var redirects = 0
        def waitInfo() : VoiceResponse = {
          redirects += 1
          VoiceResponse(
            Say("Please wait for others to join. You have waited " + redirects + " times", voice = Voice.Woman),
            Redirect(waitInfo _)
          )
        }
        waitInfo _
      }

      phone.incomingCallHandler = Some(selectConference _)

    }

## Actor API
TBD