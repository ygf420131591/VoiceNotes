package com.voicenotes.manages.request;

public class ConfNetStatusRequest extends BaseRequest {

	private String conferenceID;
	
	public void setConfId(String confId) {
		conferenceID = confId;
	}
}
