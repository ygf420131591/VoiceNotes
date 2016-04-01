package com.voicenotes.fragment;

import java.io.IOException;

import com.voicenotes.manages.MediaDecoder;
import com.voicenotes.manages.MyGLSurfaceView;
import com.voicenotes.manages.VideoCapture;
import com.voicenotes.views.R;
import android.support.v4.app.Fragment;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class VideoFragment extends Fragment implements Callback{
	
	private SurfaceView mSurfaceView = null;
	private SurfaceHolder mSurfaceHolder = null;
	
	private GLSurfaceView mGlSurfaceView = null;
	private RelativeLayout mRemoteRelativeLayout;
	private VideoCapture videoCapture;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_video, container, false);
		
		mSurfaceView = (SurfaceView) view.findViewById(R.id.localSurfaceView);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback((Callback) this);
		
		mRemoteRelativeLayout = (RelativeLayout) view.findViewById(R.id.remoteRelativeLayout);
		mGlSurfaceView = new MyGLSurfaceView(this.getActivity());
//		mGlSurfaceView.setEGLContextClientVersion(2);
		mRemoteRelativeLayout.addView(mGlSurfaceView);
		
		videoCapture = new VideoCapture();
		
		return view;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		videoCapture.initCamera(mSurfaceHolder);
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
