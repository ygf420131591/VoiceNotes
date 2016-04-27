/*
 * 接收服务器主动通知消息
 * */

package com.voicenotes.manages.network;

import android.content.Intent;

import com.voicenotes.manages.http.DHHttpClient;
import com.voicenotes.manages.http.DHHttpClient.OnNotifyLister;
import com.voicenotes.manages.http.DHHttpRequest;
import com.voicenotes.manages.notifies.BaseNotify;
import com.voicenotes.manages.notifies.BaseStatusNotify;
import com.voicenotes.manages.notifies.ConfStatusNotify;
import com.voicenotes.manages.notifies.ConfigChangeNotify;
import com.voicenotes.manages.notifies.NotifyHelper;


public class DispatchManager extends BaseManager implements OnNotifyLister{

	private static DispatchManager ins;
	private NotifyHelper mNotifyHelper;
	
	public static DispatchManager instance() {
		synchronized (DispatchManager.class) {
			if (ins == null) {
				ins = new DispatchManager();
			}
		}
		return ins;
	}
	
	public DispatchManager() {
		mNotifyHelper = NotifyHelper.instance();
	}

	public void startDispatchLister() {
		DHHttpClient.instance().setNotifyLister(this);
	}
	
	public void removeDispatchLister() {
		DHHttpClient.instance().setNotifyLister(null);
	}
	
	@Override
	public void onNotifyLister(DHHttpRequest request) {
		// TODO 
		
		String url = request.getHttpRequestUrl();
		BaseNotify notify = null;
		switch (url) {
		case "/post/notify/event/register":
		case "/post/notify/event/joinconf":
		case "/post/notify/event/network":
			BaseStatusNotify baseNotify = new BaseStatusNotify();
			notify = mNotifyHelper.changeJson(request, baseNotify);
			
			//TODO 返回给上层
			break;
		case "/post/notify/event/confChange":
			ConfStatusNotify statusNotify = new ConfStatusNotify();
			notify = mNotifyHelper.changeJson(request, statusNotify);
			//TODO
			break;
		case "/post/notify/event/configChange":
			ConfigChangeNotify configNotify = new ConfigChangeNotify();
			notify = mNotifyHelper.changeJson(request, configNotify);
			//TODO
			break;
		default:
			break;
		}
		
	}
	
}
