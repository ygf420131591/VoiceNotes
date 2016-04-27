package com.voicenotes.manages.responses;

public class ConfNetStatusResponse extends BaseResponse{
	private String msg;
	private NetStatuss data;
	
	public class NetStatuss {
		private int msStatus;
		private NetStatus[] netstat;
	}
	
	public class NetStatus {
		private int clientID;
		private int[] netInput;
		private int[] netOutput;
	}
}
