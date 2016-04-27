/*µÇÂ¼ÇëÇó*/
package com.voicenotes.manages.request;

public class LoginRequest extends BaseRequest{
	
	private String userName;
	private String password;
	private int sourceType;
		
	public LoginRequest() {
		sourceType = 3;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String name) {
		userName = name;
	}
		
	public String getPassword() {
		return password;
	}
		
	public void setPassword(String pass) {
		password = pass;
	}
	
	public void setSourceType(int source) {
		sourceType = source;
	}
	
	public int getSourceType() {
		return sourceType;
	}
}
