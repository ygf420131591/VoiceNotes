package com.voicenotes.manages.responses;

public class LoginResponse extends BaseResponse {
	
	private String msg;	// �������
	private String cookID;	//httpͷʹ��
	private int htime;		 //�����ı���ʱ��
	private int confStatus;   //�Ƿ��ڻ����� 0:���ڻ��� 1:���ڼ������  2:���ڻ���
	
	public LoginResponse() {
		
	}
	
	public String getMsg() {
		return msg;
	}
	
	public String getCookID() {
		return cookID;
	}
	
	public int getHtime() {
		return htime;
	}
	
	public int getConfStatus() {
		return confStatus;
	}
	
}
