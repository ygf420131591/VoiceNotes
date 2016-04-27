/*
 * 工厂类
 * HTTP 协议解析
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
	 * 工厂模式
	 *  解析Http字符串
	 *  httpMsg：http字符串
	 *  返回：解析结果 */
	public DHHttpBase Parser(String httpMsg) {
		synchronized (this) {
			if (buffer == null) {
				buffer = new StringBuffer();
			}
			buffer.append(httpMsg);   // 字符串放入缓存队列
			
			DHHttpBase result = parserStartLine();
			if (result.getHttpParserError() != DHHttpContant.HTTP_PARSER_DATA_SUCCESS) {
				buffer.delete(0, buffer.length());
				return result;
			}
		
			//解析头
			int res = parserHeadParams(result);
			if (res == DHHttpContant.HTTP_PARSER_DATA_SUCCESS) {
				//解析内容
				res = parserContent(result);
			
			} 
			if (res != DHHttpContant.HTTP_PARSER_DATA_INCOMPLETE) {  //解析完成或解析错误，则删除缓存
				buffer.delete(0, buffer.length());
			}
			return result;
		}
	
	}
	
	//解析第一行
	private DHHttpBase parserStartLine() {
		String firstWord = null, secondWord = null, thridWord = null;
		int firstWordEnd, secondWordEnd, thridWordEnd;
		int error = DHHttpContant.HTTP_PARSER_DATA_SUCCESS;
		
		firstWordEnd = buffer.indexOf(" ");  //第一个关键词的位置
		if(firstWordEnd == -1) {
			error = DHHttpContant.HTTP_PARSER_DATA_ERROR;  //http封装包不符合语法
		} else {
			firstWord = buffer.substring(0, firstWordEnd);   //首行第一个字段
		}
		
		secondWordEnd = buffer.indexOf(" ", firstWordEnd + 1); //第二个关键词的位置
		if (secondWordEnd == -1) {
			error = DHHttpContant.HTTP_PARSER_DATA_ERROR;  //http封装包不符合语法
		} else {
			secondWord = buffer.substring(firstWordEnd + 1, secondWordEnd);
		}
		
		thridWordEnd = buffer.indexOf("\n", secondWordEnd + 1); //第三个关键词的位置
		if (thridWordEnd == -1) {
			error = DHHttpContant.HTTP_PARSER_DATA_ERROR;  //http封装包不符合语法
		} else {
			thridWord = buffer.substring(secondWordEnd + 1, thridWordEnd);
		}
		if (error == DHHttpContant.HTTP_PARSER_DATA_SUCCESS) {		//正确解析
			if (!secondWord.contains("/post/notify/event")) {   //响应消息
				DHHttpResponse response = new DHHttpResponse();
				response.setHttpParserError(error);
//				response.setHttpMsgType(DHHttpContant.HTTP_MSG_TYPE_RESPONSE);
				response.setHttpVersion(firstWord);    //version
				response.setHttpStatusCode(secondWord);  //http状态码
				response.setHttpReasonPhrase(thridWord);//http状态描述
				return response;
			} else {  //通知消息
				DHHttpRequest request = new DHHttpRequest();
				request.setHttpParserError(error);
//				request.setHttpMsgType(DHHttpContant.HTTP_MSG_TYPE_REQUEST);
				request.setHttpMethod(firstWord);  //方法
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
	
	//消息头报文解析
	private int parserHeadParams(DHHttpBase result) {
		
		int headStringStart = buffer.indexOf("\n");
		int headStringEnd = buffer.indexOf("\n\n"); //报头结束的位置
		if (headStringStart == -1 || headStringEnd == -1) {
			return DHHttpContant.HTTP_PARSER_DATA_ERROR;  //http封装包不符合语法
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
	//消息正文解析
	private int parserContent(DHHttpBase result) {
		int headStringEnd = buffer.indexOf("\n\n");
		int contentLength = result.getContentLength();
		if (contentLength == 0) {  //该消息没有content
			return DHHttpContant.HTTP_PARSER_DATA_SUCCESS;
		}
		int currentContentLength = buffer.length() - (headStringEnd + 2) ;
		if (contentLength > currentContentLength) {   //还有内容没有接受完成
			return DHHttpContant.HTTP_PARSER_DATA_INCOMPLETE; 
		}
		String content = buffer.substring(headStringEnd + 2, headStringEnd + contentLength + 2);
		result.setHttpBady(content);
		return DHHttpContant.HTTP_PARSER_DATA_SUCCESS;
	}
	
}
