package scwilio
package callback

import scwilio.twiml._

object functions {
  type CallbackFunc[T <: CallbackEvent, R] = (T) => R
  type ActiveCallFunc = (ActiveCall) => VoiceResponse
  type CallOutcomeFunc = (CompletedCall) => Unit
  type OutgoingDialOutcomeFunc = (CompletedOutgoingDial) => VoiceResponse

  // TODO: This is a hack. Used for waitUrl in ConnectToConfernce (which gives no params)
  // Should have a noop or CID-only callback
  type NoParamCallbackFunc = (ActiveCall) => VoiceResponse

}

/**
 * Manages references between uniquely generated URLs and functions to manage
 * Twilio callback references.
 */
trait CallbackManager {
  import functions._

  def register(f: CallbackFunc[_,_]) : String

  def getAndRemove[T <: CallbackEvent, R](url: String) : Option[CallbackFunc[T, R]]
}

trait InMemoryCallbackManager extends CallbackManager {
  import functions._

  private val callbacks = new java.util.concurrent.ConcurrentHashMap[String, CallbackFunc[_,_]]

  private val counter = new java.util.concurrent.atomic.AtomicLong(System.currentTimeMillis)

  private def generateUrl : String = counter.addAndGet(1).toString

  def register(f: CallbackFunc[_,_]) : String = {
    val url = generateUrl
    callbacks.put(url, f)
    url
  }

  def getAndRemove[T <: CallbackEvent, R](url: String) : Option[CallbackFunc[T, R]]  = {
    callbacks.remove(url) match {
      case cb: CallbackFunc[T, R] => Some(cb)
      case null => None
    }
  }

}
