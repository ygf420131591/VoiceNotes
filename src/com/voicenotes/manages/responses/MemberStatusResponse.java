package com.voicenotes.manages.responses;

import java.lang.reflect.Member;

public class MemberStatusResponse extends BaseResponse {

	private String msg;
	private MemberStates data;
	
	public class MemberStates {
		private MemberState[] members;
		private String conferenceID;
	}
	
	public class MemberState {
		private Boolean isPresenting;
		private Boolean isAnonymous;
		private Boolean isMeetingOpen;
		private Boolean isOwner;
		private Boolean hasAttended;
		private Boolean isRegistered;
		private Boolean isSpeakerOpen;
		private String clientID;
		private Boolean isMicOpen;
		private String name;
	}
	
}
