package com.voicenotes.views;

import com.voicenotes.manages.VideoCapture;
import com.voicenotes.manages.qrcode.QRCodeCapture;
import com.voicenotes.views.handlers.ScanQRCodeHandler;
import com.zijunlin.Zxing.Demo.view.ViewfinderView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class ScanQRCodeActivity extends BaseActivity implements Callback {

	private QRCodeCapture mQRCapture;
	private SurfaceView mSurfaceView;
	private ViewfinderView mViewfinderView;
	private ScanQRCodeHandler mScanQRHandler;
	private AlertDialog  mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_qrcode);
		
		mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinderView1);
		mScanQRHandler = new ScanQRCodeHandler(this);
		
		mSurfaceView = (SurfaceView) findViewById(R.id.scanQRCodesurfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.addCallback(this);
		
		mQRCapture = new QRCodeCapture(this, mViewfinderView, mScanQRHandler);
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mQRCapture.destoryCamera();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mQRCapture.destoryCamera();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mQRCapture.initCamera(holder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	//处理二维码扫描结果
	public void onScanQRCodeResult(String result) {
		Intent intent = new Intent();
		intent.putExtra("url", result);
		this.setResult(RESULT_OK, intent);
		finish();
//		if (mDialog == null) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			mDialog = builder.create();
//		}
//		mDialog.setTitle(result);
//		if(!mDialog.isShowing()) {
//			mDialog.show();
//		}
	}
}
