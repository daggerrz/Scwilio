package scwilio

import callback._
import twiml._
import util._

/**
 * Represents a phone device. Must be mixed with a CallbackManager to handle
 * callback functions. See scwilio.uf.UnfilteredPhone for an example.
 */
trait Phone { self: CallbackManager with Logging =>

  /**
   * Set this to handle incoming calls.
   */
  def incomingCallHandler: (ActiveCall) => VoiceResponse

  /**
   * Called when an incoming call arrives.
   */
  def handleIncomingCall(call: ActiveCall) : VoiceResponse = {
    log.debug("Incoming call: " + call)
    incomingCallHandler(call)
  }

  /**
   * Called when a no-param callback is invoked. An example is waitUrl for conference calls.
   */
  def handleNoParam(fid: String) : VoiceResponse = {
    getAndRemove[ActiveCall, VoiceResponse](fid).map(_.apply(null)).getOrElse(
      Say("Sorry, an error has occured. Do not know how to handle this callback.")
    )
  }

  /**
   * Callback for active calls.
   */
  def handleCallStatus(fid: String, call: ActiveCall) : VoiceResponse = {
    log.debug("Call connected: " + call)
    getAndRemove[ActiveCall, VoiceResponse](fid).map(_.apply(call)).getOrElse(
      Say("Sorry, an error has occured. Do not know how to handle this call.")
    )
  }

  /**
   * Callback when a call ends.
   */
  def handleCallEnded(fid: String, outcome: CompletedCall) : Unit = {
    log.debug("Call ended: " + outcome)
    getAndRemove[CompletedCall, VoiceResponse](fid).foreach(_.apply(outcome))
  }

  /**
   * Callback when an outgoing dial (i.e., via <Dial/>) call ends.
   */
  def handleOutgoingDialEnded(fid: String, outcome: CompletedOutgoingDial) : VoiceResponse = {
    log.debug("Outgoing dial ended: " + outcome)
    getAndRemove[CompletedOutgoingDial, VoiceResponse](fid).map(_.apply(outcome)).getOrElse(
      Say("Sorry, an error has occured. Do not know how to handle this call.")
    )
  }
}
