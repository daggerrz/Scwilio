package scwilio
package callback


import org.specs.Specification

class CallbackManagerSpec extends Specification {

  val sm = new AnyRef with InMemoryCallbackManager
  val printer = (oc: CompletedCall) => { println(oc) }

  "StateManager" should {
    "Register a callback function and return a (unique) URL" in {
      val url = sm.register(printer)
      url must notBeNull
    }
    "Return and remove the callback function on request" in {
      val url = sm.register(printer)
      sm.getAndRemove(url) must_== Some(printer)
      sm.getAndRemove(url) must_== None
    }
  }
}