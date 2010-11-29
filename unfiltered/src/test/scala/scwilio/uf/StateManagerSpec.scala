package scwilio
package uf

import callback._

import org.specs.Specification

class StateManagerSpec extends Specification {

  val sm = new InMemoryCallbackManager
  val printer = (oc: DialOutcome) => { println(oc) }

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