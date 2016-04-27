/* HttpЭ��ķ�װ���ͺͽ��ַܷ�
 * ������֪ͨ��Ϣ���ܴ��� */

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

	//������Ϣ
	public interface OnDisConnectRepLister {
		void onDisConnectRepLister();
	}
	
	//����socket�ص�
	public interface OnConnectRepLister {
		void onConnectRepLister(int error);
	}
	
	//����socket���ص�
	public interface OnExecuteRepLister {
		void onExecuteRepLister(DHHttpResponse response);
	}
	
	//������֪ͨ��Ϣ
	public interface OnNotifyLister {
		void onNotifyLister(DHHttpRequest request);
	}
	
	private String mHost;
	private int mPort;
//	private String mRequestURL;
	private static DHHttpClient ins;
	private OnNotifyLister mNotifyLister;
	private OnDisConnectRepLister mDisConnectRepLister;
	
	private Map<String, EventValue> mExecuteRepLister; //Sequence�ͻص��󶨣�����ʱͨ��Sequenceȷ������Ļص�����
	
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
	
	//����ص�����
	public void clearNotityLister() {
		mExecuteRepLister.clear();
	}
	
	/*
	 * ���ö��߻ص�*/
	public void setDisConnectRepLister(OnDisConnectRepLister lister) {
		this.setDetectDisconnect(true);  //������߼��
		mDisConnectRepLister = lister;
	}
	
	/*
	 * ��������
	 * request: 
	 * liveTime:������ʱ��
	 */
	public void heartBeat(DHHttpRequest request) {
		byte[] httpRequest = request.packet().getBytes();
		this.setKeepLiveBuf(httpRequest);
//		super.setKeepLiveSpan(liveTime);
		this.heartbeat();   //����������
	}
	
	public void connect(String urlString, OnConnectRepLister connectLister) {
		if (urlString == null) {
			return;
		}
		String headString = urlString.substring(0, 7);
		if (!headString.equals("http://")) {  //û�д�http://ʱ����
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
		if (result != 0) {             //����socket����
			result = ErrorCode.DH_LOGIN_CONNECT_ERR; 
		}
		lister.onConnectRepLister(result);
	}
	
	/* 
	 * request:����
	 * timeout:��ʱ
	 * executeLister:*/
	
	public void execute(DHHttpRequest request, long timeout, OnExecuteRepLister executeLister) {
		final byte[] httpRequest = request.packet().getBytes();
		//Sequence���浽map
		final String sequence = request.getHttpSequence();
		
		Event event = new Event(false);  //��ʼ״̬��false
		
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
				//TODO ��ʱ
				values.repLister.onExecuteRepLister(null);
			}
			synchronized (this) {
				mExecuteRepLister.remove(sequence);
			}
		}
		
	}
	
	/*
	 * ��������httpRequest��
	 * request: ����
	 * timeout: ��ʱ  ��λ/��
	 * executeLister: �ص�
	 * 
	 * */
//	public void executeBlock(DHHttpRequest request, long timeout, OnExecuteRepLister executeLister) {
//		final byte[] httpRequest = request.packet().getBytes();
//		//Sequence���浽map
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
//						//TODO ��ʱ
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
	
	
	//����httpRequest��
//	public void execute(DHHttpRequest request, OnExecuteRepLister executeLister) {
//		final byte[] httpRequest = request.packet().getBytes();
//		//Sequence���浽map
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

	//��������
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
		
		DHHttpBase result = DHHttpParser.instance().Parser(buff);   //������HttpЭ��
		
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
					values.event.setEvent();   //�����Ƿ�����Ӧ��־λ
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
