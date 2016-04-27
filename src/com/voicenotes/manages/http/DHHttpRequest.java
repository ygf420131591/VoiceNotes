package com.voicenotes.manages.http;
/*
 * http请求和通知 封装类 
 * 
 */


import java.util.HashMap;

public class DHHttpRequest extends DHHttpBase {

	private String mHttpMethod;	// http方法
	private String mHttpRequestUrl; //http url地址
	
	private String mRequestLine;   // 请求行
	
	public DHHttpRequest() {
		mHttpParserError = DHHttpContant.HTTP_PARSER_DATA_SUCCESS;
		mHttpMsgType = DHHttpContant.HTTP_MSG_TYPE_REQUEST;
		
		mHttpMethod = "GET";
		mHttpRequestUrl = "/";
		mHttpVersion = DHHttpContant.HTTP_VERSION_DEFAULT;
		if (mHeadParams == null) {
			mHeadParams = new HashMap<String, String>();
		}
		mHeadParams.put(DHHttpContant.HTTP_HEAD_CONTENT_TYPE_KEY, DHHttpContant.HTTP_HEAD_CONTENT_TYPE_VALUE);

	}
	
	//组装http请求
	public String packet() {
		mRequestLine = String.format("%s %s %s\r\n", mHttpMethod, mHttpRequestUrl, mHttpVersion);
		packetHead();
		
		StringBuffer httpBuffer = new StringBuffer();
		httpBuffer.append(mRequestLine);
		httpBuffer.append(mHttpHead);
		if (mContent != null) {
			httpBuffer.append(mContent);
		}
		return httpBuffer.toString();
	}
	
	public void setHttpRequestUrl(String requestUrl) {
		mHttpRequestUrl = requestUrl;
	}
	
	public String getHttpRequestUrl() {
		return mHttpRequestUrl;
	}
	
	public void setHttpMethod(String method) {
		mHttpMethod = method;
	}
	
	public String getHttpMethod() {
		return mHttpMethod;
	}
	
}
