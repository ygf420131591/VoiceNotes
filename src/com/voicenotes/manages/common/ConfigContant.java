package com.voicenotes.manages.common;

public  class ConfigContant {

	//登陆设备类型
	public static final int DH_CONF_SOURCE_TYPE_LOCAL = 0;
	public static final int DH_CONF_SOURCE_TYPE_WEB = 1;
	public static final int DH_CONF_SOURCE_TYPE_WIN = 2;
	public static final int DH_CONF_SOURCE_TYPE_PHONE = 3;
	public static final int DH_CONF_SOURCE_TYPE_OTHER_SDK = 4;
	
	//会议类型
	public static final int DH_CONF_TYPE_IMMEDIATELY = 1;
	public static final int DH_CONF_TYPE_APPOINTMENT = 2;
	
	//操作会议类型
	public static final int DH_CONF_OPERATION_HANGUP = 0;
	public static final int DH_CONF_OPERATION_DAIL = 1;
	public static final int DH_CONF_OPERATION_OPEN_MIC = 2;
	public static final int DH_CONF_OPERATION_CLOSE_MIC = 3;
	public static final int DH_CONF_OPERATION_OPEN_SPEAKER = 4;
	public static final int DH_CONF_OPERATION_CLOSE_SPEAKER = 5;
	public static final int DH_CONF_OPERATION_EXTEND_CONF = 6;
	public static final int DH_CONF_OPERATION_OVER_CONF = 7;
	public static final int DH_CONF_OPERATION_ADD_ATTENDEE = 8;
	public static final int DH_CONF_OPERATION_DELETE_ATTENDEE = 9;
	public static final int DH_CONF_OPERATION_SET_CHAIRMAN = 10;
	public static final int DH_CONF_OPERATION_OPEN_MESSAGE = 11;
	public static final int DH_CONF_OPERATION_CLOSE_MESSAGE = 12;
	public static final int DH_CONF_OPERATION_OPEN_ROLL_CALL = 13;
	public static final int DH_CONF_OPERATION_CLOSE_ROOL_CALL = 14;
	
	//服务配置名称
	public static final String DH_CONF_CONFIG_NAME_AUDIO = "audio";
	public static final String DH_CONF_CONFIG_NAME_VIDEO = "video";
	public static final String DH_CONF_CONFIG_NAME_MEETING = "meeting";
	public static final String DH_CONF_CONFIG_NAME_GENERAL = "general";
	
	//通知事件类型
	public static final String DH_NOTIFY_EVENT_TYPE_REGISTER = "register";
	public static final String DH_NOTIFY_EVENT_TYPE_JOINCONF = "joinconf";
	public static final String DH_NOTIFY_EVENT_TYPE_NETWORK = "network";
	public static final String DH_NOTIFY_EVENT_TYPE_CONF_CHANGE = "confChange";
	public static final String DH_NOTIFY_EVENT_TYPE_CONFIG_CHANGE = "configChange";
	
	public static final String DH_NOTIFY_METHOD_NAME_MEMSTATUS = "memstatus";
	public static final String DH_NOTIFY_METHOD_NAME_REQUEST_OWNER_SHIP = "requestOwnerShip";
	public static final String DH_NOTIFY_METHOD_NAME_BYE_CONFERENCE = "byeConference";
	
	//协议类型
	public static final String DH_CONF_PROT_TYPE_SIP = "sip";
	public static final String DH_CONF_PROT_TYPE_H323 = "H323";
	
	
}
