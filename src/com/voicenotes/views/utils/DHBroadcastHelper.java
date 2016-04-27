package com.voicenotes.views.utils;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


public class DHBroadcastHelper {

	public interface OnBroadcastLister {
		public void onAction(String action, Intent intent);
	}
	
	public class DHBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Intent intent = arg1;
			String action = intent.getAction();
			DHBroadcastHelper.this.mLister.onAction(action, intent);
		}
	}
	
	private DHBroadcastReceiver mBroadcastReceiver = new DHBroadcastReceiver();
	private OnBroadcastLister mLister;
	
	public void registerBroadcast(Context ctx, List<String>actions, OnBroadcastLister lister) {
		IntentFilter filter = new IntentFilter();
		for (String action : actions) {
			filter.addAction(action);
		}
		ctx.registerReceiver(mBroadcastReceiver, filter);
		mLister = lister;
	}
}
