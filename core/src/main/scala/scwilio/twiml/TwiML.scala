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

  private implicit def optInt2optString(v: Option[Int]): Option[String] = v match {
    case Some(i) => Some(i.toString)
    case _ => None
  }

  private def optional(v: Option[String]): Option[xml.Text] = v match {
    case Some(s) => Some(new xml.Text(s))
    case _ => None
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
             action={optional(dial.onConnect)}
             timeout={optional(dial.timeout)}>{dial.to.toStandardFormat}</Dial>

    case conf: ConnectToConference =>
      <Dial action={optional(conf.onLeave)}>
        <Conference waitUrl={optional(conf.onWait)}
          muted={conf.muted.toString}
          startConferenceOnEnter={conf.startOnEnter.toString}
          endConferenceOnExit={conf.endOnExit.toString}>{conf.cid}</Conference>
      </Dial>

    case gather: Gather =>
        <Gather action={optional(gather.onGathered)}
                finishOnKey={gather.finishOnKey.toString}
                numDigits={gather.numDigits.toString} timeout={gather.timeout.toString}/>

    case redirect: Redirect =>
        <Redirect>{redirect.to}</Redirect>
  }
}
