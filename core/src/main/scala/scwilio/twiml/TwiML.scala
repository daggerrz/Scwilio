package scwilio
package twiml

import scala.xml._

/**
 * Produces XML from TwiML verbs.
 */
object TwiML {

  def apply(response: VoiceResponse) : Node = {
    <Response>
      { for (verb <- response.verbs) yield TwiML(verb) }
    </Response>
  }

  def apply(verb: Verb) : Node = verb match {
    case say: Say =>
       <Say voice={say.voice.value} loop={say.loop.toString} language={say.language.value}>{say.what}</Say>

    case play: Play =>
        <Play loop={play.loop.toString}>{play.audioUrl}</Play>

    case Pause(length) =>
      <Pause length={length.toString}/>

    case Reject(reason) =>
      <Reject reason={reason}/>

    case Hangup =>
      <Hangup/>

    case dial: Dial =>
       <Dial callerId={dial.from.toStandardFormat}
             action={dial.onConnect.getOrElse("")}
             timeout={dial.timeout.toString}>{dial.to.toStandardFormat}</Dial>

    case conf: ConnectToConference =>
      <Dial action={conf.onLeave.getOrElse("")}>
        <Conference waitUrl={conf.onWait.getOrElse("")}
          muted={conf.muted.toString}
          startConferenceOnEnter={conf.startOnEnter.toString}
          endConferenceOnExit={conf.endOnExit.toString}>{conf.cid}</Conference>
      </Dial>
    case gather: Gather =>
        <Gather action={gather.onGathered.getOrElse("")}
                finishOnKey={gather.finishOnKey.toString}
                numDigits={gather.numDigits.toString} timeout={gather.timeout.toString}/>
    case redirect: Redirect =>
        <Redirect>{redirect.to}</Redirect>
  }

}