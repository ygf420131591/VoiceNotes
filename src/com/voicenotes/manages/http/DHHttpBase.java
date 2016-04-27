package com.voicenotes.manages.http;

import java.util.Map;

public abstract class DHHttpBase {

	protected int mHttpMsgType;
	protected int mHttpParserError;
	protected String mHttpVersion;  //http版本号
	protected String mHttpHead;		//消息包头
	
	protected Map<String, String> mHeadParams; //消息包头参数
	protected String mContent;		//消息正文
	
	//组装http消息包头
	protected void packetHead() {
		StringBuffer headBuffer = new StringBuffer();
		for (Map.Entry<String, String> entry : mHeadParams.entrySet()) {
			String headParam = String.format("%s: %s\r\n",entry.getKey(), entry.getValue());
			headBuffer.append(headParam);
		}
		headBuffer.append("\r\n");
		mHttpHead = headBuffer.toString();
	}
	
	public void setHttpHeadParam(String key, String value) {
		if (mHeadParams == null) {
			
		}
		mHeadParams.put(key, value);
	}
	//获取http报头值
	public String getHttpHeadValue(String key) {
		if (mHeadParams.containsKey(key)) {
			String value = mHeadParams.get(key);
			return value;
		}
		return null;
	}
	
	public void setHttpVersion(String httpVersion) {
		mHttpVersion = httpVersion;
	}
	
	public void setHttpBady(String content) {
		int length = content.length();
		String contentLength = String.valueOf(length);
		mHeadParams.put(DHHttpContant.HTTP_HEAD_CONTENT_LENGTH_KEY, contentLength);
		mContent = new String(content);
	}
	
	public String getHttpBady() {
		return mContent;
	}
	
	//设置当前http报类型
	public void setHttpMsgType(int type) {
		mHttpMsgType = type;
	}
	
	//设置当前http报是否解析成功
	public void setHttpParserError(int error) {
		mHttpParserError = error;
	}
	
	//获取http报类型，request/response
	public int getHttpMsgType() {
		return mHttpMsgType;
	}
	
	//设置当前http报是否解析成功
	public int getHttpParserError() {
		return mHttpParserError;
	}
	
	//设置http头Sequence
	public void setHttpSequence(int sequence) {
		String seqString = String.valueOf(sequence);
		setHttpHeadParam(DHHttpContant.HTTP_HEAD_SEQUENCE_KEY, seqString);
	}
	//获取http头Sequence
	public String getHttpSequence() {
		String seq = getHttpHeadValue(DHHttpContant.HTTP_HEAD_SEQUENCE_KEY);
		return seq;
	}
	
	//获取http头content-length
	public int getContentLength() {
		String lengthString = getHttpHeadValue(DHHttpContant.HTTP_HEAD_CONTENT_LENGTH_KEY);
		int length = 0;
		if (lengthString != null) {
			length = Integer.parseInt(lengthString);
		}
		return length;
	}
	
}
