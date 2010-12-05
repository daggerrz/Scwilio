package scwilio
package uf

import callback._
import twiml._
import unfiltered.response._
import unfiltered.request._
import TwiMLResponse._
import Helpers._
import util.Logging
import functions._

/**
 * An implementation of a Phone device using the Unfiltered Jetty implementation.
 */
class UnfilteredPhone(val absoluteUrlBase: String, val port: Int) extends Phone with InMemoryCallbackManager with Logging {

  private val http = unfiltered.jetty.Http(port).filter(CallbackPlan()).start

  /**
   * Unfiltered plan for handling callbacks
   */
  object CallbackPlan {
    def apply() = unfiltered.filter.Planify(intent)

    def intent : unfiltered.filter.Plan.Intent = {
      case POST(Path(Seg("incoming" :: "voice" :: Nil), Params(p, _))) =>
        handleIncomingCall(ActiveCall.parse(p))

      case POST(Path(Seg("callback" :: "noparam" :: fid :: Nil), _)) =>
        handleNoParam(fid)

      case POST(Path(Seg("callback" :: "callconnected" :: fid :: Nil), Params(p, _))) =>
        handleCallStatus(fid, ActiveCall.parse(p))

      case POST(Path(Seg("callback" :: "callended" :: fid :: Nil), Params(p, _))) =>
        handleCallEnded(fid, DialOutcome.parse(p))
        Ok

    }
  }

  protected def makeUrl(relativeUrl: String) = Some(absoluteUrlBase + relativeUrl)

  /**
   * Implicit conversions for converting callback functions into URLs which
   * match the Unfiltered Plan.
   */
  object URLMaker {

    implicit def noParamCallFunc2UrlOpt(f: () => VoiceResponse) : Option[String] = {
      def noParams(f: () => VoiceResponse)(x: ActiveCall) = f.apply
      val callback = noParams(f) _
      makeUrl("/callback/noparam/" + register(callback))
    }

    implicit def noParamCallFunc2Url(f: () => VoiceResponse) : String = {
      noParamCallFunc2UrlOpt(f).get
    }

    implicit def activeCallFunc2Url(f: ActiveCallFunc) : Option[String] = {
      makeUrl("/callback/callconnected/" + register(f))
    }

    implicit def dialOutcomeFunc2Url(f: DialOutcomeFunc) : Option[String] = f match {
        case g: DialOutcomeFunc => makeUrl("/callback/callended/" + register(g))
        case null => None
    }
  }
}


