package com.voicenotes.manages.http;

import java.util.Map;

public abstract class DHHttpBase {

	protected int mHttpMsgType;
	protected int mHttpParserError;
	protected String mHttpVersion;  //http�汾��
	protected String mHttpHead;		//��Ϣ��ͷ
	
	protected Map<String, String> mHeadParams; //��Ϣ��ͷ����
	protected String mContent;		//��Ϣ����
	
	//��װhttp��Ϣ��ͷ
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
	//��ȡhttp��ͷֵ
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
	
	//���õ�ǰhttp������
	public void setHttpMsgType(int type) {
		mHttpMsgType = type;
	}
	
	//���õ�ǰhttp���Ƿ�����ɹ�
	public void setHttpParserError(int error) {
		mHttpParserError = error;
	}
	
	//��ȡhttp�����ͣ�request/response
	public int getHttpMsgType() {
		return mHttpMsgType;
	}
	
	//���õ�ǰhttp���Ƿ�����ɹ�
	public int getHttpParserError() {
		return mHttpParserError;
	}
	
	//����httpͷSequence
	public void setHttpSequence(int sequence) {
		String seqString = String.valueOf(sequence);
		setHttpHeadParam(DHHttpContant.HTTP_HEAD_SEQUENCE_KEY, seqString);
	}
	//��ȡhttpͷSequence
	public String getHttpSequence() {
		String seq = getHttpHeadValue(DHHttpContant.HTTP_HEAD_SEQUENCE_KEY);
		return seq;
	}
	
	//��ȡhttpͷcontent-length
	public int getContentLength() {
		String lengthString = getHttpHeadValue(DHHttpContant.HTTP_HEAD_CONTENT_LENGTH_KEY);
		int length = 0;
		if (lengthString != null) {
			length = Integer.parseInt(lengthString);
		}
		return length;
	}
	
}
