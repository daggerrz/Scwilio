package scwilio
package callback

/**
 * Trait for all Twilio callback events.
 */
sealed trait CallbackEvent

/**
 * Event triggered when an incoming call arrives.
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
 * Event triggered when an incoming SMS is received.
 */
case class IncomingSms(sid: String, from: Phonenumber, to: Phonenumber, body: String) extends CallbackEvent

object IncomingSms {
  def parse(p: Map[String, String]) = {
    IncomingSms(
      p("SmsSid"),
      Phonenumber(p("From")),
      Phonenumber(p("To")),
      p("Body")
    )
  }
}

/**
 * Represents the result of a outgoing dial operation.
 */
case class DialOutcome(sid: String, from: Phonenumber, to: Phonenumber, state: DialOutcomeState) extends CallbackEvent

object DialOutcome {
  def parse(p: Map[String, String]) = {
       DialOutcome(
         p("CallSid"),
         Phonenumber(p("From")),
         Phonenumber(p("To")),
         p.get("CallStatus") match {
           case Some("in-progress") => InProgress
           case Some("completed") => Success
           case Some("busy") => Busy
           case Some("no-answer") => NoAnswer
           case _ => Failed
         }
       )
   }
}

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

case object Success extends DialOutcomeState
case object Busy extends DialOutcomeState
case object Failed extends DialOutcomeState
case object NoAnswer extends DialOutcomeState

sealed trait CallDirection
case object Inbound extends CallDirection
case object Outbound extends CallDirection
