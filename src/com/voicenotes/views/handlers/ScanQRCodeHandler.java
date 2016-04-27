package com.voicenotes.views.handlers;

import com.voicenotes.views.ScanQRCodeActivity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class ScanQRCodeHandler extends Handler{

	private ScanQRCodeActivity mScanQRActivity;
	
	public ScanQRCodeHandler(ScanQRCodeActivity activity) {
		mScanQRActivity = activity;
	}
	
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		int what = msg.what;
		String resultString = (String) msg.obj;
		mScanQRActivity.onScanQRCodeResult(resultString);
	}

	
}
