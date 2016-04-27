package com.voicenotes.manages.responses;

public class GetConfigResponse extends BaseResponse{

	private String msg;
	private Config data;
	
	public class Config {
		private Image image;
		private Encode encode;
		private Sip sip;
		private H323 H323;
	}
	
	public class Image {
		private int brightness;
		private int contrast;
		private int saturation;
	}
	public class Encode {
		private int streamType;
		private String encodeMode;
		private int fps;
		private String resolution;
		private int bitrate;
		private String streamCtrl;
		
	}
	public class Sip {
		private String localMcu;
		private String deviceID;
		private String serverIP;
		private int port;
		private String password;
		private String userID;
	}
	public class H323 {
		private String deviceName;
		private String enableGK;
		private String GKIP;
		private String GKPort;
	}
}
