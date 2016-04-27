package com.voicenotes.manages.responses;

public class LoginResponse extends BaseResponse {
	
	private String msg;	// 结果描述
	private String cookID;	//http头使用
	private int htime;		 //心跳的报活时间
	private int confStatus;   //是否在会议中 0:不在会议 1:正在加入会议  2:正在会议
	
	public LoginResponse() {
		
	}
	
	public String getMsg() {
		return msg;
	}
	
	public String getCookID() {
		return cookID;
	}
	
	public int getHtime() {
		return htime;
	}
	
	public int getConfStatus() {
		return confStatus;
	}
	
}
