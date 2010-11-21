package scwilio

case class CallInfo(callId: String, from: Phonenumber, to: Phonenumber, uri: String)
case class ConferenceState(confId: String, state: String, participants: Seq[Participant])
case class Participant(callId: String, muted: Boolean)

case class IncomingPhonenumber(sid: String, config: IncomingPhonenumberConfig)

case class IncomingPhonenumberConfig(friendlyName: Option[String],
                             voiceUrl: Option[String],
                             voiceFallbackUrl: Option[String],
                             statusCallbackUrl: Option[String],
                             smsUrl: Option[String],
                             smsFallbackUrl: Option[String]
                             )

