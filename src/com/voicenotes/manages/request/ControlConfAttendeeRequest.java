package com.voicenotes.manages.request;

public class ControlConfAttendeeRequest extends BaseRequest {

	private String confid;
	private int OprType ;
	private String[] devid;
	private String endtime;
	
	public void setConfid(String confid) {
		this.confid = confid;
	}
	
	public void setOprType(int OprType) {
		this.OprType = OprType;
	}
	
	public void setDevid(String[] devid) {
		this.devid = devid;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
}
