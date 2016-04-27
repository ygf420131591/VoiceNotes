package com.voicenotes.manages.voiceengine;

public class VoiceEngine {
	
	public VoiceEngine() {
		
	}

	public int opusEncode(byte[] src, byte[] out, int len) {
		return opusEncodeJNI(src, out, len);
	}
	private native int opusEncodeJNI(byte[] src, byte[] out, int len);
	  
	public int opusDecode(byte[] src, byte[] out, int len) {
		 return opusDecodeJNI(src, out, len);
	}
	private native int opusDecodeJNI(byte[] src, byte[] out, int len);
}
