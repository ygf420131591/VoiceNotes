/* Http协议的封装发送和接受分发
 * 服务器通知消息接受处理 */

package com.voicenotes.manages.http;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.renderscript.Sampler.Value;
import android.text.TextUtils;
import android.util.Log;

import com.mm.android.tplayer.Event;
import com.mm.android.tplayer.ITPListener;
import com.mm.android.tplayer.TPObject;
import com.mm.android.tplayer.TPTCPClient;
import com.voicenotes.manages.common.ErrorCode;
import com.voicenotes.manages.request.BaseRequest;
import com.voicenotes.manages.request.RequestHelper;
import com.voicenotes.manages.responses.BaseResponse;
import com.voicenotes.manages.responses.ResponseHelper;

public class DHHttpClient extends TPTCPClient implements ITPListener{

	//断线消息
	public interface OnDisConnectRepLister {
		void onDisConnectRepLister();
	}
	
	//链接socket回调
	public interface OnConnectRepLister {
		void onConnectRepLister(int error);
	}
	
	//发送socket包回调
	public interface OnExecuteRepLister {
		void onExecuteRepLister(DHHttpResponse response);
	}
	
	//服务器通知消息
	public interface OnNotifyLister {
		void onNotifyLister(DHHttpRequest request);
	}
	
	private String mHost;
	private int mPort;
//	private String mRequestURL;
	private static DHHttpClient ins;
	private OnNotifyLister mNotifyLister;
	private OnDisConnectRepLister mDisConnectRepLister;
	
	private Map<String, EventValue> mExecuteRepLister; //Sequence和回调绑定，返回时通过Sequence确定具体的回调函数
	
	public static DHHttpClient instance() {
		synchronized (DHHttpClient.class) {
			if (ins == null) {
				ins = new DHHttpClient();
			}
		}
		return ins;
	}
	
	public DHHttpClient() {
		mExecuteRepLister = new HashMap<String, EventValue>();
		TPObject.startup(1);
		this.setListener(this);
	}
	
	public interface OnRepLister {
		void onRepLister(BaseResponse response);
	}
	
	public class EventValue {
		
		public Event event;
		public DHHttpResponse response;
		public OnExecuteRepLister repLister;
	}
	
	//清理回调缓存
	public void clearNotityLister() {
		mExecuteRepLister.clear();
	}
	
	/*
	 * 设置断线回调*/
	public void setDisConnectRepLister(OnDisConnectRepLister lister) {
		this.setDetectDisconnect(true);  //允许断线检查
		mDisConnectRepLister = lister;
	}
	
	/*
	 * 发送心跳
	 * request: 
	 * liveTime:心跳包时间
	 */
	public void heartBeat(DHHttpRequest request) {
		byte[] httpRequest = request.packet().getBytes();
		this.setKeepLiveBuf(httpRequest);
//		super.setKeepLiveSpan(liveTime);
		this.heartbeat();   //发送心跳包
	}
	
	public void connect(String urlString, OnConnectRepLister connectLister) {
		if (urlString == null) {
			return;
		}
		String headString = urlString.substring(0, 7);
		if (!headString.equals("http://")) {  //没有带http://时补齐
			urlString = "http://" + urlString;
		}
		OnConnectRepLister lister = connectLister;
		try {
			URL url = new URL(urlString);
			mHost = url.getHost();
			mPort = url.getPort();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int result = DHHttpClient.this.connect(mHost, mPort, 0);
		if (result != 0) {             //连接socket错误
			result = ErrorCode.DH_LOGIN_CONNECT_ERR; 
		}
		lister.onConnectRepLister(result);
	}
	
	/* 
	 * request:请求
	 * timeout:超时
	 * executeLister:*/
	
	public void execute(DHHttpRequest request, long timeout, OnExecuteRepLister executeLister) {
		final byte[] httpRequest = request.packet().getBytes();
		//Sequence保存到map
		final String sequence = request.getHttpSequence();
		
		Event event = new Event(false);  //初始状态：false
		
		EventValue values = new EventValue();
		values.repLister = executeLister;
		values.event = event;
		synchronized (this) {
			if (executeLister != null) {
				mExecuteRepLister.put(sequence, values);
			}
		}
		
		this.write(0, httpRequest);
		long timeOut = timeout * 1000;
		synchronized (values.event) {
			try {
				values.event.wait(timeOut);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (values.event.bSet) {
				values.repLister.onExecuteRepLister((DHHttpResponse) values.response);
			} else {
				//TODO 超时
				values.repLister.onExecuteRepLister(null);
			}
			synchronized (this) {
				mExecuteRepLister.remove(sequence);
			}
		}
		
	}
	
	/*
	 * 阻塞发送httpRequest报
	 * request: 请求
	 * timeout: 超时  单位/秒
	 * executeLister: 回调
	 * 
	 * */
//	public void executeBlock(DHHttpRequest request, long timeout, OnExecuteRepLister executeLister) {
//		final byte[] httpRequest = request.packet().getBytes();
//		//Sequence保存到map
//		final String sequence = request.getHttpSequence();
//		
//		Event event = new Event(true);
//		final long timeOut = timeout * 1000;
//		
//		final EventValue values = new EventValue();
//		values.repLister = executeLister;
//		values.event = event;
//		
//		synchronized (this) {
//			if (executeLister != null) {
//				mExecuteRepLister.put(sequence, values);
//			}
//		}	
//		
//		Thread thread = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				synchronized (values.event) {
//					// TODO Auto-generated method stub
//					DHHttpClient.this.write(0, httpRequest);
//					long lastTime = System.currentTimeMillis();
//					try {
//						values.event.wait(timeOut);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					long currentTime = System.currentTimeMillis();
//					if ((currentTime - lastTime) < timeOut) {
//						values.repLister.onExecuteRepLister((DHHttpResponse) values.response);
//					} else {
//						//TODO 超时
//						synchronized (this) {
//							mExecuteRepLister.remove(sequence);
//							values.repLister.onExecuteRepLister(null);
//						}
//					}
//				}
//			}
//		});
//		thread.start();
//	}
	
	
	//发送httpRequest报
//	public void execute(DHHttpRequest request, OnExecuteRepLister executeLister) {
//		final byte[] httpRequest = request.packet().getBytes();
//		//Sequence保存到map
//		String sequence = request.getHttpSequence();
//		synchronized (this) {
//			if (executeLister != null) {
//				mExecuteRepLister.put(sequence, executeLister);
//			}
//		}
//		
//		Thread thread = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				DHHttpClient.this.write(0, httpRequest);
//			}
//		});
//		thread.start();
//	}
	
	public void setNotifyLister(OnNotifyLister lister) {
		mNotifyLister = lister;
	}

	//接受数据
	@Override
	public int onData(int nConnId, byte[] buffer, int nLen) {
		// TODO Auto-generated method stub
		String buff = null;
		try {
			buff = new String(buffer, 0, nLen, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("DHHttpClient", buff);
		
		DHHttpBase result = DHHttpParser.instance().Parser(buff);   //解析出Http协议
		
		if (result == null || result.getHttpParserError() != DHHttpContant.HTTP_PARSER_DATA_SUCCESS) {
				
		}else if (result.getHttpMsgType() == DHHttpContant.HTTP_MSG_TYPE_RESPONSE) {
			DHHttpResponse response = (DHHttpResponse) result;
			String sequence = response.getHttpSequence();
			EventValue values = null;
			synchronized (this) {
				values = mExecuteRepLister.get(sequence);
			}
			if (values != null) {
				synchronized (values.event) {
					values.response = response;
					values.event.setEvent();   //设置是否是响应标志位
					values.event.notify();
				}
			}
				
//			OnExecuteRepLister executeRepLister = mExecuteRepLister.get(sequence);
//			if (executeRepLister != null) {
//				executeRepLister.onExecuteRepLister(response);
//				mExecuteRepLister.remove(sequence);
//			}
		} else if (result.getHttpMsgType() == DHHttpContant.HTTP_MSG_TYPE_REQUEST) {
			DHHttpRequest request = (DHHttpRequest) result;
			if (mNotifyLister != null) {
				mNotifyLister.onNotifyLister(request);
			}
		} 
	
		return 0;
	}

	@Override
	public int onSendDataAck(int nConnId, int nId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int onDisconnect(int nConnId) {
		// TODO Auto-generated method stub
		mDisConnectRepLister.onDisConnectRepLister();
		return 0;
	}

	@Override
	public int onReconnect(int nConnId) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
