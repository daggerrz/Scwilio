package scwilio
package op

import Phonenumber._

import org.specs.Specification

object OperationsSpec extends Specification {

  "Error parer" should {
    "parse response correctly" in {
      val res = RestClient.parseException(<TwilioResponse><RestException><Status>400</Status><Message>The source phone number provided, +4790055383 is not yet verified for this account.  You may only use caller-id numbers for which you've proven ownership.</Message><Code>21210</Code><MoreInfo>http://www.twilio.com/docs/errors/21210</MoreInfo></RestException></TwilioResponse>)
      res.code must_== "21210"
      res.message must_== "The source phone number provided, +4790055383 is not yet verified for this account.  You may only use caller-id numbers for which you've proven ownership."
    }
  }

  "ListAvailablePhonenumbers handler" should {
    "parse response correctly" in {
      val res = <TwilioResponse>
                  <AvailablePhoneNumbers uri="/2010-04-01/Accounts/AC2b49338c7d9ae05032f5711d8f7f59dc/AvailablePhoneNumbers/US/Local">
                      <AvailablePhoneNumber>
                          <FriendlyName>(978) 522-6522</FriendlyName>
                          <PhoneNumber>+19785226522</PhoneNumber>
                          <Lata>128</Lata>
                          <RateCenter>BEVERLY</RateCenter>
                          <Latitude>42.550000</Latitude>
                          <Longitude>-70.870000</Longitude>
                          <Region>MA</Region>
                          <PostalCode>01960</PostalCode>
                          <IsoCountry>US</IsoCountry>
                      </AvailablePhoneNumber>
                      <AvailablePhoneNumber>
                          <FriendlyName>(240) 587-5274</FriendlyName>
                          <PhoneNumber>+12405875274</PhoneNumber>
                          <Lata>236</Lata>
                          <RateCenter>LEONARDTN</RateCenter>
                          <Latitude>38.883330</Latitude>
                          <Longitude>-77.011190</Longitude>
                          <Region>MD</Region>
                          <PostalCode>20053</PostalCode>
                          <IsoCountry>US</IsoCountry>
                      </AvailablePhoneNumber>
                  </AvailablePhoneNumbers>
                 </TwilioResponse>
      val numbers = new ListAvailableNumbers(null).parse(res)
      numbers.size must_== 2
    }
  }

  "UpdateIncomingPhonenumber" should {
    "parse response correctly" in {
      val res= <TwilioResponse>
                  <IncomingPhoneNumber>
                      <Sid>PN2a0747eba6abf96b7e3c3ff0b4530f6e</Sid>
                      <AccountSid>ACdc5f1e11047ebd6fe7a55f120be3a900</AccountSid>
                      <FriendlyName>My Company Line</FriendlyName>
                      <PhoneNumber>+15105647903</PhoneNumber>
                      <VoiceUrl>http://mycompany.com/handleNewCall.php</VoiceUrl>
                      <VoiceMethod>POST</VoiceMethod>
                      <VoiceFallbackUrl/>
                      <VoiceFallbackMethod>POST</VoiceFallbackMethod>
                      <VoiceCallerIdLookup>false</VoiceCallerIdLookup>
                      <DateCreated>Mon, 16 Aug 2010 23:00:23 +0000</DateCreated>
                      <DateUpdated>Mon, 16 Aug 2010 23:00:23 +0000</DateUpdated>
                      <SmsUrl>http://mycompany.com/handleNewSms.php</SmsUrl>
                      <SmsMethod>POST</SmsMethod>
                      <SmsFallbackUrl/>
                      <SmsFallbackMethod>GET</SmsFallbackMethod>
                      <Capabilities>
                          <Voice>true</Voice>
                          <SMS>true</SMS>
                      </Capabilities>
                      <StatusCallback/>
                      <StatusCallbackMethod/>
                      <ApiVersion>2010-04-01</ApiVersion>
                      <Uri>/2010-04-01/Accounts/ACdc5f1e11047ebd6fe7a55f120be3a900/IncomingPhoneNumbers/PN2a0747eba6abf96b7e3c3ff0b4530f6e</Uri>
                  </IncomingPhoneNumber>
              </TwilioResponse>

      IncomingNumbersParser.parse(res) must_== Seq(IncomingNumber(
        "PN2a0747eba6abf96b7e3c3ff0b4530f6e",
        Phonenumber("+15105647903"),
        IncomingNumberConfig(
          Some("My Company Line"),
          Some("http://mycompany.com/handleNewCall.php"),
          None,
          None,
          Some("http://mycompany.com/handleNewSms.php"),
          None
        )
      ))
    }
  }

  "DialOperation" should {
    "parse response correctly" in {
      val res = <TwilioResponse>
                <Call>
                  <Sid>CA5161d32bc213aa14b729535850754a07</Sid>
                  <DateCreated>Thu, 21 Oct 2010 20:38:22 +0000</DateCreated>
                  <DateUpdated>Thu, 21 Oct 2010 20:38:22 +0000</DateUpdated>
                  <ParentCallSid/>
                  <AccountSid>AC2b49338c7d9ae05032f5711d8f7f59dc</AccountSid>
                  <To>+1111111111</To>
                  <From>+12222222222</From>
                  <PhoneNumberSid>PN349e00efa7d0e743e3283a8170469d84</PhoneNumberSid>
                  <Status>queued</Status>
                  <StartTime/>
                  <EndTime/>
                  <Duration/>
                  <Price/>
                  <Direction>outbound-api</Direction>
                  <AnsweredBy/>
                  <ApiVersion>2010-04-01</ApiVersion>
                  <Annotation/>
                  <ForwardedFrom/>
                  <GroupSid/>
                  <CallerName/>
                  <Uri>/2010-04-01/Accounts/AC2b49338c7d9ae05032f5711d8f7f59dc/Calls/CA5161d32bc213aa14b729535850754a07</Uri>
                  <SubresourceUris>
                      <Notifications>
                          /2010-04-01/Accounts/AC2b49338c7d9ae05032f5711d8f7f59dc/Calls/CA5161d32bc213aa14b729535850754a07/Notifications
                      </Notifications>
                      <Recordings>
                          /2010-04-01/Accounts/AC2b49338c7d9ae05032f5711d8f7f59dc/Calls/CA5161d32bc213aa14b729535850754a07/Recordings
                      </Recordings>
                  </SubresourceUris>
              </Call>
            </TwilioResponse>
      DialOperation.parse(res) must_== CallInfo("CA5161d32bc213aa14b729535850754a07", "+12222222222", "+1111111111", "/2010-04-01/Accounts/AC2b49338c7d9ae05032f5711d8f7f59dc/Calls/CA5161d32bc213aa14b729535850754a07")
    }
  }

  "TWGetConferenceState" should {
    "parse response correctly" in {
      val res = <TwilioResponse>
                  <Conference>
                      <Sid>CFbbe46ff1274e283f7e3ac1df0072ab39</Sid>
                      <AccountSid>AC5ef872f6da5a21de157d80997a64bd33</AccountSid>
                      <FriendlyName>Party Line</FriendlyName>
                      <Status>completed</Status>
                      <DateCreated>Wed, 18 Aug 2010 20:20:06 +0000</DateCreated>
                      <ApiVersion>2010-04-01</ApiVersion>
                      <DateUpdated>Wed, 18 Aug 2010 20:24:32 +0000</DateUpdated>
                      <Uri>/2010-04-01/Accounts/AC5ef872f6da5a21de157d80997a64bd33/Conferences/CFbbe46ff1274e283f7e3ac1df0072ab39</Uri>
                      <SubresourceUris>
                          <Participants>/2010-04-01/Accounts/AC5ef872f6da5a21de157d80997a64bd33/Conferences/CFbbe46ff1274e283f7e3ac1df0072ab39/Participants</Participants>
                      </SubresourceUris>
                  </Conference>
                </TwilioResponse>

      val (state, uris) = new GetConferenceParticipantURIs(null).parse(res)
      state must_== "completed"
      uris.size must_== 1
      uris.head must_== "/2010-04-01/Accounts/AC5ef872f6da5a21de157d80997a64bd33/Conferences/CFbbe46ff1274e283f7e3ac1df0072ab39/Participants"
    }
  }
  "GetConferenceParticipantInfo" should {
    "parse response correctly" in {
      val res = <TwilioResponse>
                  <Participant>
                      <CallSid>CA386025c9bf5d6052a1d1ea42b4d16662</CallSid>
                      <ConferenceSid>CFbbe46ff1274e283f7e3ac1df0072ab39</ConferenceSid>
                      <AccountSid>AC5ef872f6da5a21de157d80997a64bd33</AccountSid>
                      <Muted>true</Muted>
                      <EndConferenceOnExit>true</EndConferenceOnExit>
                      <StartConferenceOnEnter>true</StartConferenceOnEnter>
                      <DateCreated>Wed, 18 Aug 2010 20:20:10 +0000</DateCreated>
                      <DateUpdated>Wed, 18 Aug 2010 20:20:10 +0000</DateUpdated>
                      <Uri>/2010-04-01/Accounts/AC5ef872f6da5a21de157d80997a64bd33/Conferences/CFbbe46ff1274e283f7e3ac1df0072ab39/Participants/CA386025c9bf5d6052a1d1ea42b4d16662</Uri>
                  </Participant>
              </TwilioResponse>

      val p = new GetConferenceParticipantInfo(null).parse(res)
      p must_== Participant("CA386025c9bf5d6052a1d1ea42b4d16662", true)
    }
  }
}