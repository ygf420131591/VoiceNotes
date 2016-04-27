package com.voicenotes.manages.request;

import java.util.List;

import com.voicenotes.manages.entitys.CreateConfInfoEntity.DeviceID;
import com.voicenotes.manages.entitys.CreateConfInfoEntity.ConfLayout;

import android.R.integer;

public class CreatConfRequest extends BaseRequest {

	private String name;		//��������
	private String conftype;		//��������
	private String starttime;	//��ʼʱ��
	private String endtime;		//����ʱ��
	private List<DeviceID> attendee;	//�����
//	private String ssrc;
//	private String interval;
	private ConfLayout olayout;		//��ϯ�໭�沼��
	private ConfLayout alayout;		//����˶໭�沼��
	private String owner;		//��ϯ
	private String confid;	//	����ID
	private String mode;	//	����ģʽ
	private String password;	//��������
	private int isAllowAnonymous;	//�Ƿ���������
	private int capacity;	//	����������
	private String bandwidth;	//	�������Ƶ����
	private List<String> fusionStreams;	//�ں�������
	private String remind;	//	��������
	private String creator;	//	���鴴����
	
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
