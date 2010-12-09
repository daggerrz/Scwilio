package scwilio
package callback

/**
 * Trait for all Twilio triggered callback events such.
 */
sealed trait CallbackEvent

/**
 * Represent a call, active or completed. As active and completed
 * are quite different, they share this trait instead of concrete
 * implementations instead of having a richer set of states.
 */
trait Call {
  def sid: String
  def from: Phonenumber
  def to: Phonenumber

  /**
   * If machine detection is used for an outgoing call,
   * this field will reflect who answered the call (machine or human)
   */
  def answeredBy: Option[AnsweredBy]
}

/**
 *  Represents an active, ongoing call. In- or out-going.
 */
case class ActiveCall(
     sid: String,
     from: Phonenumber,
     to: Phonenumber,
     status: ActiveCallStatus,
     forwardedFrom: Option[Phonenumber],
     answeredBy: Option[AnsweredBy],
     digits: Option[String]
   ) extends Call with CallbackEvent

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
        },
        p.get("Digits")
      )
  }
}

/**
 * Represents a completed call.
 */
case class CompletedCall(
    sid: String,
    from: Phonenumber,
    to: Phonenumber,
    status: CompletedCallStatus,
    answeredBy: Option[AnsweredBy],
    duration: Int
  ) extends Call with CallbackEvent

object CompletedCall {
  def parse(p: Map[String, String]) = {
       CompletedCall(
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
         },
        p.get("CallDuration") getOrElse("0") toInt
       )
   }
}

/**
 * Status of an active call.
 */
sealed trait ActiveCallStatus
case object Queued extends ActiveCallStatus
case object Ringing extends ActiveCallStatus
case object InProgress extends ActiveCallStatus
case object Ended extends ActiveCallStatus


/**
 * Status of a completed call. For calls which originally were
 * incoming, this will always be just Completed. For outgoing calls
 * all states are relevant.
 */
sealed trait CompletedCallStatus
case object Completed extends CompletedCallStatus
case object Busy extends CompletedCallStatus
case object Failed extends CompletedCallStatus
case object NoAnswer extends CompletedCallStatus

sealed trait AnsweredBy
case object Human extends AnsweredBy
case object Machine extends AnsweredBy

/**
 * Safe-guard in case Twilio extends it's API.
 */
case class Unknown(msg: String) extends ActiveCallStatus with CompletedCallStatus with AnsweredBy


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

