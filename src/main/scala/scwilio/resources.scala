package scwilio

case class CallInfo(callId: String, from: Phonenumber, to: Phonenumber, uri: String)
case class ConferenceState(confId: String, state: String, participants: Seq[Participant])
case class Participant(callId: String, muted: Boolean)