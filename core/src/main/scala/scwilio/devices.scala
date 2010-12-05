package scwilio

import callback._
import twiml._
import util._

/**
 * Represents a phone device. Must be mixed with a CallbackManager to handle
 * callback functions. See scwilio.uf.Server for an example.
 */
trait Phone { self: CallbackManager with Logging =>
  /**
   * Called when an incoming call arrives.
   */
  def handleIncomingCall(call: ActiveCall) : VoiceResponse = {
    log.debug("Incoming call " + call)
    incomingCallHandler match {
      case Some(f) => f.apply(call)
      case _ => Say("Hello, thanks for calling, but incoming calls are not supported by this server.")
    }
  }

  var incomingCallHandler: Option[(ActiveCall) => VoiceResponse] = None

  /**
   * Called when a no-param callback is invoked. An example is waitUrl for conference calls.
   */
  def handleNoParam(fid: String) : VoiceResponse = {
    getAndRemove[ActiveCall, VoiceResponse](fid) match {
      case Some(callback) => callback.apply(null)
      case _ => Say("Sorry, an error has occured. Do not know how to handle this callback.")
    }
  }

  /**
   * Called when an outgoing call connects.
   */
  def handleCallStatus(fid: String, call: ActiveCall) : VoiceResponse = {
    log.debug("Call status " + call)
    getAndRemove[ActiveCall, VoiceResponse](fid) match {
      case Some(callback) => callback.apply(call)
      case _ => Say("Sorry, an error has occured. Do not know how to handle this call.")
    }
  }

  /**
   * Called when an outgoing call ends.
   */
  def handleCallEnded(fid: String, outcome: DialOutcome) : Unit = {
    getAndRemove[DialOutcome, VoiceResponse](fid) match {
      case Some(callback) => callback.apply(outcome)
      case _ => log.warn("No handler for call end " + fid)
    }
  }

}