package com.voicenotes.manages.request;

import java.util.List;

import com.voicenotes.manages.entitys.CreateConfInfoEntity.DeviceID;
import com.voicenotes.manages.entitys.CreateConfInfoEntity.ConfLayout;

import android.R.integer;

public class CreatConfRequest extends BaseRequest {

	private String name;		//会议名称
	private String conftype;		//会议类型
	private String starttime;	//开始时间
	private String endtime;		//结束时间
	private List<DeviceID> attendee;	//与会人
//	private String ssrc;
//	private String interval;
	private ConfLayout olayout;		//主席多画面布局
	private ConfLayout alayout;		//与会人多画面布局
	private String owner;		//主席
	private String confid;	//	会议ID
	private String mode;	//	会议模式
	private String password;	//会议密码
	private int isAllowAnonymous;	//是否允许匿名
	private int capacity;	//	最大与会人数
	private String bandwidth;	//	与会人视频带宽
	private List<String> fusionStreams;	//融合流参数
	private String remind;	//	会议提醒
	private String creator;	//	会议创建者
	
	public CreatConfRequest() {
		owner = "30005689";
		confid = "1723";
		mode = "1";
		password = "";
		isAllowAnonymous = 1;
		capacity = 16;
		bandwidth = "8192";
//		fusionStreams = 
		remind = "1";
		creator = "1";
	}
	
	public void setName(String confName) {
		name = confName;
	}
	
	public void setConftype(String confType) {
		conftype = confType;
	}
	
	public void setStartTime(String startTime) {
		starttime = startTime;
	}
	
	public void setEndTime(String endTime) {
		endtime = endTime;
	}
	
	public void setAttendee(List<DeviceID> attenDee) {
		attendee = attenDee;
	}
	
	public void setOlayout(ConfLayout oLayout) {
		olayout = oLayout;
	}
	
	public void setAlayout(ConfLayout aLayout) {
		alayout = aLayout;
	}
	
	public void setOwner(String confOwner) {
		owner = confOwner;
	}
	
	public void setConfid(String confId) {
		confid = confId;
	}
	
	public void setMode(String confMode) {
		mode = confMode;
	}
	
	public void setPassword(String passWord) {
		password = passWord;
	}
	
	public void setIsAllowAnonymous(int isAllowAnonymOus) {
		isAllowAnonymous = isAllowAnonymOus;
	}
	
	public void setCapacity(int capaCity) {
		capacity = capaCity;
	}
	
	public void setBandwidth(String bandWidth) {
		bandwidth = bandWidth;
	}
	
	public void setFusionStreams(List<String> confFusionStreams) {
		fusionStreams = confFusionStreams;
	}
	
	public void setRemind(String reMind) {
		remind = reMind;
	}

	public void setCreator(String confCreator) {
		creator = confCreator;
	}
	
}
