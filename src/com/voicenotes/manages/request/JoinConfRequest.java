package com.voicenotes.manages.request;

public class JoinConfRequest extends BaseRequest {

	private String confid;
	private int jointype;
	
	public void setConfid(String confId) {
		confid = confId;
	}
	
	public void setJointype(int joinType) {
		jointype = joinType;
	}
}
