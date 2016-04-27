package com.voicenotes.manages;

import java.io.IOException;
import java.util.TimerTask;


import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

public class VideoCapture implements PreviewCallback{

	private Camera mCamera = null; 
	private MediaH264Encoder mMediaeEncoder = null;
	private MediaH264Decoder mMediaDecoder = null;
	private OpenGLRenderer mOpenGLRenderer;
	
	private Context mContext;
	
	private int mWidth;
	private int mHeight;
	
	public VideoCapture(OpenGLRenderer render) {
		mMediaeEncoder = new MediaH264Encoder();
		mWidth = 1280;
		mHeight = 720;
		mMediaeEncoder.initMediaEncoder(mWidth, mHeight);
		
		mMediaDecoder = new MediaH264Decoder();
		mMediaDecoder.initMediaDecoder(mWidth, mHeight);
		
		mOpenGLRenderer = render;
		
//		mContext = context;
	}
	
	public void initCamera(SurfaceHolder surfaceHolder) {
		if (null == mCamera) {
			mCamera = Camera.open(0);
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
			parameters.setRotation(90);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//			parameters.setPreviewFormat(ImageFormat.NV21);
			int[] range = new int[2];
			parameters.getPreviewFpsRange(range);
			parameters.setPreviewFpsRange(range[0], range[1]);
			mCamera.setParameters(parameters);
			
			mCamera.setDisplayOrientation(90);
			mCamera.setPreviewCallback((PreviewCallback) this);
			mCamera.startPreview();
			
			//自动聚焦
//			Timer timer = new Timer();
//			AutoFocusTask task = new AutoFocusTask();
//			timer.schedule(task, 0, 1500);
		}

	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		
		int length = mMediaeEncoder.encoder(data);
		int result = -1;
		if (length  > 0) {
			result = mMediaDecoder.decoder(mMediaeEncoder.outBuffer, length);	
		}
		if (result == 0) {
			mOpenGLRenderer.update(mWidth, mHeight);
			int yLength = mWidth * mHeight;
			byte[] y = new byte[yLength];
			byte[] u = new byte[yLength / 4];
			byte[] v = new byte[yLength / 4];
			
			for (int i = 0; i < yLength; i++) {
				y[i] = mMediaDecoder.mDecoderOutput[i];
				if (i < yLength / 4) {
					v[i] = mMediaDecoder.mDecoderOutput[yLength + i * 2];
					u[i] = mMediaDecoder.mDecoderOutput[yLength + i * 2 + 1];
				}
			}
			mOpenGLRenderer.updateYuv(y, u, v);
		}
	}

	//自动聚焦
	private class AutoFocusTask extends TimerTask implements AutoFocusCallback {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mCamera.autoFocus(this);
		}

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
