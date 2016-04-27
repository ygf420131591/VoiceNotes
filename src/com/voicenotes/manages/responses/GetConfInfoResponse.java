package com.voicenotes.manages.responses;

import java.util.List;

import com.voicenotes.manages.entitys.CreateConfInfoEntity.ConfLayout;

public class GetConfInfoResponse extends BaseResponse{
	private String msg;
	private int pageAllNum;
	private int confAllNum;
	private ConfInfo[] data;
	
	
	
	public class ConfInfo {
		private String confid;
		private int conftype;
		private String name;
		private String password;
		private String owner;
		private DeviceInfo[] deviceInfos;
		private String startTime;
		private String endTime;
		private String description;
		private int isAllowAnonymous;
		private String subTitiles;
		private ConfLayout olayout;
		private ConfLayout alayout;
		private String operationTime;
		private String creator;
		private int hasfav;
		private int bandwidth;
		private int capacity;
		private int mode;
		private int remind;
		private String fusionStreams;
		private int layoutMode;
		private String votes;
		private String voteResponses;
		private String rollCalls;
		private String mscServiceName;
	}
	public class DeviceInfo {
		private String devid;
		private int status;
		private String name;
	}
}
