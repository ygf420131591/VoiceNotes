package com.voicenotes.manages.network;

import android.text.TextUtils;

import com.voicenotes.manages.common.ErrorCode;
import com.voicenotes.manages.http.DHHttpClient.OnConnectRepLister;
import com.voicenotes.manages.http.DHHttpClient.OnDisConnectRepLister;
import com.voicenotes.manages.http.DHHttpRequest;
import com.voicenotes.manages.request.RequestHelper;
import com.voicenotes.manages.responses.BaseResponse;
import com.voicenotes.manages.responses.LoginResponse;

public class LoginManager extends BaseManager implements OnDisConnectRepLister{
	
	private static LoginManager ins;
	
	private Boolean bIsHeartBeat;
	
	public static LoginManager instance() {
		
		synchronized (LoginManager.class) {
			if (ins == null) {
				ins = new LoginManager();
			}
		}
		return ins;
	}
	
	public LoginManager() {
		super.initManager();
	}
	
	/**
	 * 连接服务器，同时登陆
	 * @param url: 服务器url
	 * 	username:用户名
	 *  pwd:密码
	 *  lister:监听
	 * */
	public void connect(String url, String userName, String pwd, OnRepLister lister) {
		if (url == null || TextUtils.isEmpty(userName) || TextUtils.isEmpty("pwd")) {
			return;
		}
		final String userNameString = userName;
		final String passWordString = pwd;
		final String urlString = url;
		final OnRepLister listers = lister;
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHttpClient.connect(urlString, new OnConnectRepLister() {
					
					@Override
					public void onConnectRepLister(int error) {
						// TODO Auto-generated method stub
						if (error == ErrorCode.DH_LOGIN_CONNECT_ERR) {  //连接socket失败
							listers.onRepLister(error, null);
						} else {
							login(userNameString, passWordString, listers);
						}
					}
				});
			}
		});
		thread.start();
	}
	
	/*
	 * 登陆
	 * 
	 * */
	public void login(String userName, String pwd, final OnRepLister lister) {
		
		
		if (TextUtils.isEmpty(userName) || TextUtils.isEmpty("pwd")) {
			return;
		}

		DHHttpRequest httpPacket = RequestHelper.instance().loginRequest(userName, pwd);
		
		LoginResponse loginResponse = new LoginResponse();
		
		mLister.put(loginResponse, new OnRepLister(){
			@Override
			public void onRepLister(int err, BaseResponse rep) {
				//TODO
				int error = ErrorCode.DH_LOGIN_SUCCESS;
				if (rep == null) {
					error = ErrorCode.DH_LOGIN_OVERTIME;
				}
				
				DispatchManager.instance().startDispatchLister();   //开始接受服务器主动通知
				startHeartBeat(30);		//30s发送心跳包
				lister.onRepLister(error, rep);
			}
		});
		
		sendRequest(httpPacket, loginResponse, 30);
	
	}
	
	
	/*
	 * 开始心跳包发送
	 * time: 心跳发送时间
	 * */
	private void startHeartBeat(final long time) {
		bIsHeartBeat = true;
		mHttpClient.setDisConnectRepLister(this);
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (bIsHeartBeat) {
					DHHttpRequest httpPacket = RequestHelper.instance().heartBeat();
					mHttpClient.heartBeat(httpPacket);
					
					try {
						Thread.sleep(time * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}
	
	/*
	 * 停止心跳包发送
	 * */
	private void stopHeartBeat() {
		bIsHeartBeat = false;
	}
	
	/*
	 * 登出
	 * 暂时没有回调
	 * */
	public void logout() {
		DHHttpRequest httpPacket = RequestHelper.instance().logoutRequest();
		sendRequest(httpPacket, null, 0);
		stopHeartBeat();			//停止发送心跳包
	}
	


	@Override
	public void onDisConnectRepLister() {
		// TODO 断线回调
		
	}

	

}
