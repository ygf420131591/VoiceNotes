package com.voicenotes.manages.network;

import com.voicenotes.manages.http.DHHttpRequest;
import com.voicenotes.manages.responses.GetConfigResponse;
import com.voicenotes.manages.responses.SetConfigResponse;
import com.voicenotes.manages.responses.SysOperateResponse;

public class SysOperateManager extends BaseManager {
	
	private static  SysOperateManager ins;
	
	public static SysOperateManager instatce() {
		synchronized (SysOperateManager.class) {
			if (ins == null) {
				ins = new SysOperateManager();
			}
		} 
		return ins;
	}
	
	public SysOperateManager() {
		initManager();
	}
	
	public void sysOperate(OnRepLister lister) {
		DHHttpRequest request = mRequestHelper.sysOpeRequest(0);
		SysOperateResponse response = new SysOperateResponse();
		mLister.put(response, lister);
		sendRequest(request, response, 30);
	}

	public void getConfig(String name, OnRepLister lister) {
		DHHttpRequest request = mRequestHelper.getConfig(name);
		GetConfigResponse response = new GetConfigResponse();
		mLister.put(response, lister);
		sendRequest(request, response, 30);
	}
	
	public void setConfig(OnRepLister lister) {
		DHHttpRequest request = mRequestHelper.setConfig();
		SetConfigResponse response = new SetConfigResponse();
		mLister.put(response, lister);
		sendRequest(request, response, 30);
	}
	
}
