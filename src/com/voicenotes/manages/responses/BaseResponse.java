package com.voicenotes.manages.responses;

import java.io.Serializable;

public abstract class BaseResponse implements Serializable{
	protected int sequence;
	protected int result;
	
	public int getSequence() {
		return sequence;
	}
	
	public void setResult(int result) {
		this.result = result;
	}
	
	public int getResult() {
		return result;
	}
}
