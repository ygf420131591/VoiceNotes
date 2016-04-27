package com.voicenotes.manages.request;

public class SetConfigRequest extends BaseRequest {

	private String name;
	private AudioChannel audioChannel;
	
	public void setName(String name) {
		this.name = name;
	}
	public void setAudioChannel(AudioChannel audioChannel) {
		this.audioChannel = audioChannel;
	}
	
	public class AudioChannel {
		private String echoEnable;
		private int echoDelay;
		private String loopBack;
		private int output;
		private int input;
		
		public void setEchoEnable(String echoEnable) {
			this.echoEnable = echoEnable;
		}
		
		public void setEchoDelay(int echoDelay) {
			this.echoDelay = echoDelay;
		}
		
		public void setLoopBack(String loopBack) {
			this.loopBack = loopBack;
		}
		
		public void setOutput(int output) {
			this.output = output;
		}
		public void setInput(int input) {
			this.input = input;
		}
	}
}
