package scwilio
package callback

/**
 * Trait for all Twilio callback events.
 */
sealed trait CallbackEvent

/**
 * Represents a op triggered when an incoming call arrives.
 */
case class IncomingCall(
     sid: String,
     from: Phonenumber,
     to: Phonenumber,
     forwardedFrom: Option[Phonenumber]
   ) extends CallbackEvent

object IncomingCall {
  def parse(p: Map[String, String]) = {
      IncomingCall(
        p("CallSid"),
        Phonenumber(p("From")),
        Phonenumber(p("To")),
        Phonenumber(p.get("ForwardedFrom"))
      )
  }
}

/**
 * Represents the result of a outgoing dial operation.
 */
case class DialOutcome(sid: String, from: Phonenumber, to: Phonenumber, state: DialOutcomeState) extends CallbackEvent

/**
 * Status of a call.
 */
sealed trait CallStatus

/**
 * Call statuses which can be dial outcomes.
 */
sealed trait DialOutcomeState extends CallStatus

case object Queued extends CallStatus
case object Ringing extends CallStatus
case object InProgress extends CallStatus

case object Success extends CallStatus with DialOutcomeState
case object Busy extends CallStatus with DialOutcomeState
case object Failed extends CallStatus with DialOutcomeState
case object NoAnswer extends CallStatus with DialOutcomeState

sealed trait CallDirection
case object Inbound extends CallDirection
case object Outbound extends CallDirection
