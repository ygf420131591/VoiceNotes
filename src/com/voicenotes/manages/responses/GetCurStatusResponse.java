package com.voicenotes.manages.responses;

public class GetCurStatusResponse extends BaseResponse {

	private int confStatus;
	private String confID;
	private int presentStatus;
	private int micStatus;
	private int audioStatus;
	private String cameraStatus;
	private int sequence;
	
//	public int getResult() {
//		return result;
//	}
	
	public int getConfStatus() {
		return confStatus;
	}
	
	public String getConfID() {
		return confID;
	}
	
	public int getPresentStatus() {
		return presentStatus;
	}
}
