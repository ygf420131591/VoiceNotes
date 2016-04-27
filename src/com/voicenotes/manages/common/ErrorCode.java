package com.voicenotes.manages.common;

public class ErrorCode {
	
	//登陆错误管理
	public static final int DH_LOGIN_SUCCESS = 0;  //登陆成功
	public static final int DH_LOGIN_OVERTIME = 1; //超时，服务器没有响应
	public static final int DH_LOGIN_CONNECT_ERR = 2; //连接失败，服务器ip和端口不正确
//	public static final int DH_LOGIN_USER_PWD_ERR = 3; //用户名和密码不正确
	
}
