package com.voicenotes.manages.http;

public class DHHttpContant {
	//http 起始行
	public static final String HTTP_VERSION_DEFAULT   =  "http/1.1";
	
	//http 头关键词
	public static final String HTTP_HEAD_CONTENT_LENGTH_KEY = "Content-Length";
	public static final String HTTP_HEAD_CONTENT_TYPE_KEY = "Content-Type";
	public static final String HTTP_HEAD_COOKID_KEY = "cookID";
	public static final String HTTP_HEAD_SEQUENCE_KEY = "Sequence";
	
	//http 头的值
	public static final String HTTP_HEAD_CONTENT_TYPE_VALUE = "application/octet-stream";
	
	//http 消息类型
	public static final int HTTP_MSG_TYPE_REQUEST   =  1; //通知/请求消息
	public static final int HTTP_MSG_TYPE_RESPONSE   =  2; //响应消息
	
	//http 解析结果
	public static final int HTTP_PARSER_DATA_SUCCESS = 0; //解析成功
	public static final int HTTP_PARSER_DATA_INCOMPLETE = 1; //数据不完整，需要下一个数据包组成完成数据
	public static final int HTTP_PARSER_DATA_ERROR = 2; //http封装包不符合语法

}
