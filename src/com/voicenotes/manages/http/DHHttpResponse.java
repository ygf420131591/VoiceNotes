/*
 * http 响应类
 * */

package com.voicenotes.manages.http;

import java.util.HashMap;

public class DHHttpResponse extends DHHttpBase{

	private String mHttpStatusCode;
	private String mHttpReasonPhrase;
	
	private String mStatusLine;   // 状态行
	private String mContent;		//消息正文
	
	public DHHttpResponse() {
		
		mHttpParserError = DHHttpContant.HTTP_PARSER_DATA_SUCCESS;
		mHttpMsgType = DHHttpContant.HTTP_MSG_TYPE_RESPONSE;
		
		mHttpVersion = DHHttpContant.HTTP_VERSION_DEFAULT;
		mHttpStatusCode = "200";
		mHttpReasonPhrase = "ok";
		
		if (mHeadParams == null) {
			mHeadParams = new HashMap<String, String>();
		}
		
	}
	
	//组装http响应包
	public String packet() {
		mStatusLine = String.format("%s %s %s\r\n", mHttpVersion, mHttpStatusCode, mHttpReasonPhrase);
		packetHead();
		
		StringBuffer httpBuffer = new StringBuffer();
		httpBuffer.append(mStatusLine);
		httpBuffer.append(mHttpHead);
		if (mContent != null) {
			httpBuffer.append(mContent);
		}
		return httpBuffer.toString();
		
	}
	
	public void setHttpStatusCode(String statusCode) {
		mHttpStatusCode = statusCode;
	}
	
	public void setHttpReasonPhrase(String reasonPhrase) {
		mHttpReasonPhrase = reasonPhrase;
	}
	
	public String getHttpStatusCode() {
		return mHttpStatusCode;
	}
	
	public String getHttpReasonPhrase() {
		return mHttpReasonPhrase;
	}
}
