package scwilio
package twiml

/**
 * Defines a response to an incoming voice call.
 */
case class VoiceResponse(verbs: Verb*)

object EmptyVoiceResponse extends VoiceResponse()

object RejectResponse extends VoiceResponse(Reject.Rejected)

/**
 * Trait for all TwiML Verbs.
 */
sealed trait Verb

object Verb {
  implicit def singleVerb2VoiceResponse(v: Verb) : VoiceResponse = VoiceResponse(v)
}

trait NestableVerb extends Verb

/**
 * Dials a number. Can be used both as a op and a response.
 */
case class Dial(
   from: Phonenumber,
   to: Phonenumber,
   onEnd: Option[String] = None,
   timeout: Int = 30
 ) extends Verb

/**
 * Connects a call to a conference.
 */
case class ConnectToConference(
    cid: String,
    onLeave: Option[String] = None,
    onWait: Option[String] = None,
    muted: Boolean = false,
    startOnEnter: Boolean = true,
    endOnExit: Boolean = false
  ) extends Verb

/**
 * Say something to the caller using TTS.
 */
case class Say(what: String, language: Language = Language.English, voice: Voice = Voice.Man, loop: Int = 1) extends NestableVerb

/**
 * Play an audio recording to the caller.
 */
case class Play(audioUrl: String, loop: Int = 1) extends NestableVerb

case class Pause(seconds: Int = 1) extends NestableVerb

case class Redirect(to: String) extends Verb

case object Hangup extends Verb

case class Gather(
    numDigits: Int = Integer.MAX_VALUE,
    finishOnKey: Char = '#',
    onGathered: Option[String] = None,
    nestedVerbs: List[NestableVerb] = List(),
    timeout: Int = 5
  ) extends Verb

abstract case class Reject(reason: String) extends Verb

object Reject {
  object Busy extends Reject("busy")
  object Rejected extends Reject("rejected")
}

abstract case class Voice(value: String)

abstract case class Language(value: String)

object Voice {
  object Man extends Voice("man")
  object Woman extends Voice("woman")
}

object Language {
  object English extends Language("en")
  object Spanish extends Language("es")
  object French extends Language("fr")
  object German extends Language("de")
}
