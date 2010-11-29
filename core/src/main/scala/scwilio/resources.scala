package scwilio

case class CallInfo(callId: String, from: Phonenumber, to: Phonenumber, uri: String)
case class ConferenceState(confId: String, state: String, participants: Seq[Participant])
case class Participant(callId: String, muted: Boolean)

case class IncomingNumber(sid: String, number: Phonenumber, config: IncomingNumberConfig)

/**
 * Configuration for an incoming phonenumber. When used for changing configuration,
 * non-specified options will remain unchanged.
 */
case class IncomingNumberConfig(friendlyName: Option[String] = None,
                             voiceUrl: Option[String] = None,
                             voiceFallbackUrl: Option[String] = None,
                             statusCallbackUrl: Option[String] = None,
                             smsUrl: Option[String] = None,
                             smsFallbackUrl: Option[String] = None
                             )

