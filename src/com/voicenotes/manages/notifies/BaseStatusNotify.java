/*基本状态通知*/
package com.voicenotes.manages.notifies;

public class BaseStatusNotify extends BaseNotify{

//	private String eventtype;  //“register”注册状态， “joinconf”是否与会， “network”网络状态  
	private int status; //0 不在线 1 在线
	
	
	public int getStatus() {
		return status;
	}
	
}
