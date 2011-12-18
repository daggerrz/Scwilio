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
class UnfilteredPhone(val absoluteUrlBase: String) extends Phone with InMemoryCallbackManager with Logging {


  def callBackPlan = CallbackPlan()
  /**
   * Unfiltered plan for handling callbacks
   */
  object CallbackPlan {
    def apply() = unfiltered.filter.Planify(intent)

    def intent : unfiltered.filter.Plan.Intent = {
      case POST(Path(Seg("incoming" :: "voice" :: Nil)) & Params(p)) =>
        handleIncomingCall(ActiveCall.parse(p))

      case POST(Path(Seg("callback" :: "no-param" :: fid :: Nil))) =>
        handleNoParam(fid)

      case POST(Path(Seg("callback" :: "call-connected" :: fid :: Nil)) & Params(p)) =>
        handleCallStatus(fid, ActiveCall.parse(p))

      case POST(Path(Seg("callback" :: "call-ended" :: fid :: Nil)) & Params(p)) =>
        handleCallEnded(fid, CompletedCall.parse(p))
        Ok

      case POST(Path(Seg("callback" :: "outgoing-dial-ended" :: fid :: Nil)) & Params(p)) =>
        handleOutgoingDialEnded(fid, CompletedOutgoingDial.parse(p))
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
      makeUrl("/callback/no-param/" + register(callback))
    }

    implicit def noParamCallFunc2Url(f: () => VoiceResponse) : String =
      noParamCallFunc2UrlOpt(f).get

    implicit def activeCallFunc2Url(f: ActiveCallFunc) : Option[String] =
      makeUrl("/callback/call-connected/" + register(f))

    implicit def callOutcomeFunc2Url(f: CallOutcomeFunc) : Option[String] =
      makeUrl("/callback/call-ended/" + register(f))

    implicit def redirectedCallOutcomeFunc2Url(f: OutgoingDialOutcomeFunc) : Option[String] = 
      makeUrl("/callback/outgoing-dial-ended/" + register(f))
  }
}


