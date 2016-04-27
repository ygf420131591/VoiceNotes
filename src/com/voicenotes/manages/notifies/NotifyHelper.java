package com.voicenotes.manages.notifies;

import com.google.gson.Gson;
import com.voicenotes.manages.http.DHHttpRequest;

public class NotifyHelper {

	private static NotifyHelper ins;
	private Gson gson;
	
	public static NotifyHelper instance() {
		synchronized (NotifyHelper.class) {
			if (ins == null) {
				ins = new NotifyHelper();
			}
		}
		return ins;
	}
	
	public NotifyHelper() {
		gson = new Gson();
	}
	
	public BaseNotify changeJson(DHHttpRequest request, BaseNotify bizNotify) {
		BaseNotify result = null;
		if (request != null && bizNotify != null) {
			String data = request.getHttpBady();
			result = gson.fromJson(data, bizNotify.getClass());
		}
		return result;
	}
	
}
