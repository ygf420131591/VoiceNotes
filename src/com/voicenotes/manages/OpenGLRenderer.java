package com.voicenotes.manages;

import java.nio.ByteBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

public class OpenGLRenderer implements Renderer {

	private OpenGLesDisplay mOpenGLesDisplay = new OpenGLesDisplay();
	
//	private GLSurfaceView mcurrentSurfaceView;
	private int mVideoWidth = -1, mVideoHeight = -1;
	private ByteBuffer y;
	private ByteBuffer u;
	private ByteBuffer v;
	
	public OpenGLRenderer(GLSurfaceView surfaceView) {
//		mcurrentSurfaceView = surfaceView;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
//		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		synchronized (this) {
			if (y != null) {
				y.position(0);
				u.position(0);
				v.position(0);
				mOpenGLesDisplay.buildTextures(y, u, v, mVideoWidth, mVideoHeight);
				GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
				GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
				mOpenGLesDisplay.drawFrame();
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		GLES20.glViewport(0, 0, width, height);
			
	}

	//初始化创建
	@Override
	public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
		// TODO Auto-generated method stub
		if (!mOpenGLesDisplay.isProgramBuilt()) {
			mOpenGLesDisplay.buildProgram();
		}
//		GLES20.glClearColor(0.1f, 1.0f, 0.0f, 0.1f);
//		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	}
	
	public void update(int width, int height) {
		if (width > 0 && height > 0) {
			if ((width != mVideoWidth) || (height != mVideoHeight)) {
				mVideoWidth = width;
				mVideoHeight = height;
				synchronized (this) {
					y = ByteBuffer.allocateDirect(width * height);
					u = ByteBuffer.allocateDirect(width * height / 4);
					v = ByteBuffer.allocateDirect(width * height / 4);
				}
			}
		}
	}
	
	public void updateYuv(byte[] ydata, byte[] udata, byte[] vdata) {
		synchronized (this) {
			y.clear();
			u.clear();
			v.clear();
			y.put(ydata, 0, ydata.length);
			u.put(udata, 0, udata.length);
			v.put(vdata, 0, vdata.length);
		}
//		mcurrentSurfaceView.requestRender();
	}
}
