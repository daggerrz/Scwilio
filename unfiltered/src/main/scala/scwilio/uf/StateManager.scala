package scwilio.uf

import java.util.concurrent.ConcurrentHashMap
import scwilio.callback.CallbackEvent

/**
 * Manages references between uniquely generated URLs and functions to manage
 * Twilio callback references.
 */
trait CallbackManager {

  type Callback[T <: CallbackEvent] = (T) => Unit

  def register(f: Callback[_]) : String

  def getAndRemove[T <: CallbackEvent](url: String) : Option[Callback[T]]
}

class InMemoryCallbackManager extends CallbackManager {
  val callbacks = new java.util.concurrent.ConcurrentHashMap[String, Callback[_]]
  private def generateUrl : String = System.currentTimeMillis.toString

  def register(f: Callback[_]) : String = {
    val url = generateUrl
    callbacks.put(url, f)
    url
  }

  def getAndRemove[T <: CallbackEvent](url: String) : Option[Callback[T]]  = {
    callbacks.remove(url) match {
      case cb: Callback[T] => Some(cb)
      case null => None
    }
  }

}