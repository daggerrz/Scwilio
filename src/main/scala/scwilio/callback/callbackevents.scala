package scwilio
package callback

/**
 * Trait for all Twilio callback events.
 */
sealed trait CallbackEvent

/**
 * Represents a op triggered when an incoming call arrives.
 */
case class IncomingCall(sid: String, from: Phonenumber, to: Phonenumber, forwardedFrom: Option[Phonenumber]) extends CallbackEvent

/**
 * Represents the result of a outgoing dial operation.
 */
case class DialOutcome(sid: String, from: Phonenumber, to: Phonenumber, state: DialOutcomeState) extends CallbackEvent


sealed trait CallDirection
case object Inbound extends CallDirection
case object Outbound extends CallDirection

sealed trait DialOutcomeState
case object Success extends DialOutcomeState
case object Busy extends DialOutcomeState
case object Failed extends DialOutcomeState
case object NoAnswer extends DialOutcomeState