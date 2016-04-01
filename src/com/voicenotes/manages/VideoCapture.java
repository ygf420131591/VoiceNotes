package com.voicenotes.manages;

import java.io.IOException;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;

public class VideoCapture implements PreviewCallback{

	private Camera mCamera = null; 
	private MediaEncoder mMediaeEncoder = null;
	private MediaDecoder mMediaDecoder = null;
	
	private byte[] yuvByte = new byte[1000000];
	
	public VideoCapture() {
		mMediaeEncoder = new MediaEncoder();
		mMediaeEncoder.initMediaEncoder(640, 480);
		
		mMediaDecoder = new MediaDecoder();
		mMediaDecoder.initMediaDecoder(640, 320);
	}
	
	public void initCamera(SurfaceHolder surfaceHolder) {
		if (null == mCamera) {
			mCamera = Camera.open(1);
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
			parameters.setPreviewSize(640, 480);
			parameters.setPreviewFormat(ImageFormat.NV21);
			mCamera.setParameters(parameters);
			
			mCamera.setDisplayOrientation(90);
			mCamera.setPreviewCallback((PreviewCallback) this);
			mCamera.startPreview();
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		
		int result = mMediaeEncoder.encoder(data);
		if (result > 0) {
			mMediaDecoder.decoder(mMediaeEncoder.outBuffer, yuvByte, result);	
		}
	}
	
}
