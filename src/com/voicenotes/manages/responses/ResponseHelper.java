/*
 * http��Ӧ������
 * 
 * û���ֶ����ֲ�ͬ����Ӧ
 * */
package com.voicenotes.manages.responses;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.voicenotes.manages.http.DHHttpResponse;
import com.voicenotes.manages.request.SetConfigRequest;

public class ResponseHelper {

	private static ResponseHelper responseHelper;
	private Gson gson;
	
	public static ResponseHelper instance() {
		synchronized (ResponseHelper.class) {
			if (responseHelper == null) {
				responseHelper = new ResponseHelper();
			}
		}
		return responseHelper;
	}
	
	public ResponseHelper() {
		gson = new Gson();
	}
	
	//��http���ݰ�����ȡcontent����ת������Ӧ����Ӧ����
	public BaseResponse changeJson(DHHttpResponse response, BaseResponse bizResponse) {
		BaseResponse result = null;
		if (bizResponse != null && response != null) {
			String data = response.getHttpBady();
			result = gson.fromJson(data, bizResponse.getClass());
		}
		return result;
	}
	
}
