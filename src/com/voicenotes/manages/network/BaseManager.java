package com.voicenotes.manages.network;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mm.android.tplayer.Event;
import com.mm.android.tplayer.TPTCPClient;
import com.voicenotes.manages.common.ActionContant;
import com.voicenotes.manages.common.ErrorCode;
import com.voicenotes.manages.http.DHHttpClient;
import com.voicenotes.manages.http.DHHttpClient.OnExecuteRepLister;
import com.voicenotes.manages.http.DHHttpRequest;
import com.voicenotes.manages.http.DHHttpResponse;
import com.voicenotes.manages.request.RequestHelper;
import com.voicenotes.manages.responses.BaseResponse;
import com.voicenotes.manages.responses.LoginResponse;
import com.voicenotes.manages.responses.ResponseHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public abstract class BaseManager extends TPTCPClient{

	protected Context ctx;
	
	protected RequestHelper mRequestHelper;
	protected DHHttpClient mHttpClient;
	protected ResponseHelper mResponseHelper;
	
	protected Map<BaseResponse, OnRepLister> mLister;
	protected Map<BaseResponse, String> mListers;
//	protected OnRepLister mLister;
	
	public interface OnRepLister {
		void onRepLister(int err, BaseResponse rep);
	}
	
	public void setContext(Context context) {
		if (context == null) {
			throw new RuntimeException("context is null");
		}
		ctx = context;
	}

	protected void initManager() {
		mHttpClient = DHHttpClient.instance();
		mRequestHelper = RequestHelper.instance();
		mResponseHelper = ResponseHelper.instance();
		
		mLister = new HashMap<BaseResponse, BaseManager.OnRepLister>();
	}
	
	/*发送封装好的request请求消息*/
	protected void sendRequest(DHHttpRequest request, BaseResponse response, long timeout) {
		
		//不能用全局变量
		final DHHttpRequest requests = request;
		final long timeOut = timeout;
		final BaseResponse bizResponse = response;
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHttpClient.execute(requests, timeOut, new OnExecuteRepLister() {
					
					@Override
					public void onExecuteRepLister(DHHttpResponse response) {
						// TODO Auto-generated method stub
						if (bizResponse != null) {
							BaseResponse result = mResponseHelper.changeJson(response, bizResponse);
							OnRepLister lister = mLister.get(bizResponse);

							lister.onRepLister(0, result);
							mLister.remove(bizResponse);
							
//							Intent intent = new Intent();
//							intent.setAction(ActionContant.DH_ACTION_LOGIN);
//							Bundle bundle = new Bundle();
//							bundle.putSerializable("BaseResponse", (Serializable) result);
//							intent.putExtras(bundle);
//							intent.putExtra("error", err);
//							ctx.sendBroadcast(intent);
						}
					}
				});
				
			}
		});
		thread.start();
		
	}
}
