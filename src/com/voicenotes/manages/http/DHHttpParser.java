/*
 * ������
 * HTTP Э�����
 * */

package com.voicenotes.manages.http;


public class DHHttpParser {

	private static DHHttpParser ins;
	private StringBuffer buffer;
	
	static DHHttpParser instance() {
		synchronized (DHHttpParser.class) {
			if (ins == null) {
				ins = new DHHttpParser();
			}
		}
		return ins;
	}
	
	public DHHttpParser() {
		buffer = new StringBuffer();
	}
	
	/*
	 * ����ģʽ
	 *  ����Http�ַ���
	 *  httpMsg��http�ַ���
	 *  ���أ�������� */
	public DHHttpBase Parser(String httpMsg) {
		synchronized (this) {
			if (buffer == null) {
				buffer = new StringBuffer();
			}
			buffer.append(httpMsg);   // �ַ������뻺�����
			
			DHHttpBase result = parserStartLine();
			if (result.getHttpParserError() != DHHttpContant.HTTP_PARSER_DATA_SUCCESS) {
				buffer.delete(0, buffer.length());
				return result;
			}
		
			//����ͷ
			int res = parserHeadParams(result);
			if (res == DHHttpContant.HTTP_PARSER_DATA_SUCCESS) {
				//��������
				res = parserContent(result);
			
			} 
			if (res != DHHttpContant.HTTP_PARSER_DATA_INCOMPLETE) {  //������ɻ����������ɾ������
				buffer.delete(0, buffer.length());
			}
			return result;
		}
	
	}
	
	//������һ��
	private DHHttpBase parserStartLine() {
		String firstWord = null, secondWord = null, thridWord = null;
		int firstWordEnd, secondWordEnd, thridWordEnd;
		int error = DHHttpContant.HTTP_PARSER_DATA_SUCCESS;
		
		firstWordEnd = buffer.indexOf(" ");  //��һ���ؼ��ʵ�λ��
		if(firstWordEnd == -1) {
			error = DHHttpContant.HTTP_PARSER_DATA_ERROR;  //http��װ���������﷨
		} else {
			firstWord = buffer.substring(0, firstWordEnd);   //���е�һ���ֶ�
		}
		
		secondWordEnd = buffer.indexOf(" ", firstWordEnd + 1); //�ڶ����ؼ��ʵ�λ��
		if (secondWordEnd == -1) {
			error = DHHttpContant.HTTP_PARSER_DATA_ERROR;  //http��װ���������﷨
		} else {
			secondWord = buffer.substring(firstWordEnd + 1, secondWordEnd);
		}
		
		thridWordEnd = buffer.indexOf("\n", secondWordEnd + 1); //�������ؼ��ʵ�λ��
		if (thridWordEnd == -1) {
			error = DHHttpContant.HTTP_PARSER_DATA_ERROR;  //http��װ���������﷨
		} else {
			thridWord = buffer.substring(secondWordEnd + 1, thridWordEnd);
		}
		if (error == DHHttpContant.HTTP_PARSER_DATA_SUCCESS) {		//��ȷ����
			if (!secondWord.contains("/post/notify/event")) {   //��Ӧ��Ϣ
				DHHttpResponse response = new DHHttpResponse();
				response.setHttpParserError(error);
//				response.setHttpMsgType(DHHttpContant.HTTP_MSG_TYPE_RESPONSE);
				response.setHttpVersion(firstWord);    //version
				response.setHttpStatusCode(secondWord);  //http״̬��
				response.setHttpReasonPhrase(thridWord);//http״̬����
				return response;
			} else {  //֪ͨ��Ϣ
				DHHttpRequest request = new DHHttpRequest();
				request.setHttpParserError(error);
//				request.setHttpMsgType(DHHttpContant.HTTP_MSG_TYPE_REQUEST);
				request.setHttpMethod(firstWord);  //����
				request.setHttpRequestUrl(secondWord); //url
				request.setHttpVersion(thridWord); //version
				return request;
			}
		} else {
			DHHttpBase result = new DHHttpBase() {
			};
			result.setHttpParserError(error);
			return result;
		}
	}
	
	//��Ϣͷ���Ľ���
	private int parserHeadParams(DHHttpBase result) {
		
		int headStringStart = buffer.indexOf("\n");
		int headStringEnd = buffer.indexOf("\n\n"); //��ͷ������λ��
		if (headStringStart == -1 || headStringEnd == -1) {
			return DHHttpContant.HTTP_PARSER_DATA_ERROR;  //http��װ���������﷨
		}
		String headParamsString = buffer.substring(headStringStart + 1, headStringEnd + 1);
		
		int nextParamStart = 0;
		while (nextParamStart < headParamsString.length()) {
			 int paramKeyEnd = headParamsString.indexOf(": ", nextParamStart);
			 int paramValueEnd = headParamsString.indexOf("\n", paramKeyEnd);
			 if (paramKeyEnd == -1 || paramValueEnd == -1) {
				 return DHHttpContant.HTTP_PARSER_DATA_ERROR;
			 }
			 String key = headParamsString.substring(nextParamStart, paramKeyEnd);
			 String value = headParamsString.substring(paramKeyEnd + 2, paramValueEnd);
			 result.setHttpHeadParam(key, value);
			 nextParamStart = paramValueEnd + 1;
		}
		
		return DHHttpContant.HTTP_PARSER_DATA_SUCCESS;
	}
	//��Ϣ���Ľ���
	private int parserContent(DHHttpBase result) {
		int headStringEnd = buffer.indexOf("\n\n");
		int contentLength = result.getContentLength();
		if (contentLength == 0) {  //����Ϣû��content
			return DHHttpContant.HTTP_PARSER_DATA_SUCCESS;
		}
		int currentContentLength = buffer.length() - (headStringEnd + 2) ;
		if (contentLength > currentContentLength) {   //��������û�н������
			return DHHttpContant.HTTP_PARSER_DATA_INCOMPLETE; 
		}
		String content = buffer.substring(headStringEnd + 2, headStringEnd + contentLength + 2);
		result.setHttpBady(content);
		return DHHttpContant.HTTP_PARSER_DATA_SUCCESS;
	}
	
}
