package scwilio
package callback

/**
 * Trait for all Twilio callback events.
 */
sealed trait CallbackEvent

/**
 * Represents an active, ongoing call. In- or out-going.
 */
case class ActiveCall(
     sid: String,
     from: Phonenumber,
     to: Phonenumber,
     status: CallStatus,
     forwardedFrom: Option[Phonenumber],
     answeredBy: Option[AnsweredBy]
   ) extends CallbackEvent

object ActiveCall {
  def parse(p: Map[String, String]) = {
      ActiveCall(
        p("CallSid"),
        Phonenumber(p("From")),
        Phonenumber(p("To")),
        p.get("CallStatus") match {
          case Some("in-progress") => InProgress
          case Some("queued") => Queued
          case Some("ringing") => Ringing
          case Some(s) => Unknown(s)
          case None => Unknown("no status")
        },
        Phonenumber(p.get("ForwardedFrom")),
        p.get("AnsweredBy") match {
          case Some("human") => Some(Human)
          case Some("machine") => Some(Machine)
          case Some(s) => Some(Unknown(s))
          case None => None
        }

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
case class DialOutcome(
    sid: String,
    from: Phonenumber,
    to: Phonenumber,
    status: DialOutcomeStatus,
    answeredBy: Option[AnsweredBy]

  ) extends CallbackEvent

object DialOutcome {
  def parse(p: Map[String, String]) = {
       DialOutcome(
         p("CallSid"),
         Phonenumber(p("From")),
         Phonenumber(p("To")),
         p.get("CallStatus") match {
           case Some("completed") => Completed
           case Some("busy") => Busy
           case Some("no-answer") => NoAnswer
           case Some("failed") => Failed
           case Some(s) => Unknown(s)
           case None => Unknown("no status")
         },
         p.get("AnsweredBy") match {
           case Some("human") => Some(Human)
           case Some("machine") => Some(Machine)
           case Some(s) => Some(Unknown(s))
           case None => None
         }
       )
   }
}

/**
 * Status of a call.
 */
sealed trait CallStatus
case object Queued extends CallStatus
case object Ringing extends CallStatus
case object InProgress extends CallStatus

/**
 * Call statuses which can be dial outcomes.
 */
sealed trait DialOutcomeStatus

case object Completed extends DialOutcomeStatus
case object Busy extends DialOutcomeStatus
case object Failed extends DialOutcomeStatus
case object NoAnswer extends DialOutcomeStatus


sealed trait CallDirection
case object Inbound extends CallDirection
case object Outbound extends CallDirection

sealed trait AnsweredBy
case object Human extends AnsweredBy
case object Machine extends AnsweredBy

case class Unknown(msg: String) extends CallStatus with DialOutcomeStatus with AnsweredBy
