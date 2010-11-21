package scwilio
package uf

import unfiltered.response._
import twiml._

/** Unfiltered responder for TwiML **/
case class TwiMLResponse(vr: VoiceResponse)
  extends ChainResponse(ContentType("application/xml") ~> ResponseString(twiml.TwiML(vr).toString))

object TwiMLResponse {
  implicit def voiceResp2TwimlResponse(vr: VoiceResponse) : TwiMLResponse = TwiMLResponse(vr)
  implicit def singleVerb2TwimlResponse(v: Verb) : TwiMLResponse = TwiMLResponse(VoiceResponse(v))
}