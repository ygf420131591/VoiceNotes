package com.voicenotes.manages.qrcode;

import java.io.IOException;

import com.zijunlin.Zxing.Demo.view.ViewfinderView;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.view.SurfaceHolder;

public class QRCodeCapture implements PreviewCallback{

	private Camera mCamera = null; 
	private QRCodeDecoder mQrCodeDecoder;
	
	private Context mContext;
	private ViewfinderView view;
	
	private int mWidth;
	private int mHeight;
	
	public QRCodeCapture(Context context, ViewfinderView view, Handler handler) {
		mWidth = 1280;
		mHeight = 960;
		
		mQrCodeDecoder = new QRCodeDecoder(view, handler);
		
		mContext = context;
	}
	
	public void destoryCamera() {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	
	public void initCamera(SurfaceHolder surfaceHolder) {
		if (null == mCamera) {
			mCamera = Camera.open();
		}
		try {
			mCamera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
			e.printStackTrace();
		}
		if (mCamera != null) {
			Camera.Parameters parameters = mCamera.getParameters();
			Size size = parameters.getPreviewSize();
			parameters.setPreviewSize(mWidth, mHeight);
//			parameters.setRotation(90);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//			parameters.setPreviewFormat(ImageFormat.NV21);
			int[] range = new int[2];
			parameters.getPreviewFpsRange(range);
			parameters.setPreviewFpsRange(range[0], range[1]);
			mCamera.setParameters(parameters);
			
			mCamera.setDisplayOrientation(90);
			mCamera.setPreviewCallback((PreviewCallback) this);
			mCamera.startPreview();

		}
	
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {

		mQrCodeDecoder.decode(data, mWidth, mHeight);
	}
	
}

