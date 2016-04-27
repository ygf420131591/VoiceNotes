package com.voicenotes.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.voicenotes.manages.voiceengine.AudioPlay;
import com.voicenotes.manages.voiceengine.VoiceEngine;
import com.voicenotes.views.utils.TopBarView;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class VoicePlayActivity extends BaseActivity implements OnClickListener {
	private byte[] mTempBuffer = new byte[640];
	
	private ImageButton mPlayImButton;
	private VoiceEngine mVoiceEngine;
	
	private TopBarView mTopBarView;
	private Handler handler;
	private TextView mContentTextView;
	
	private String mCurrentFileName;
	private AudioPlay mAudioPlay;
	private Boolean bIsStartPlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_play);

		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		am.setSpeakerphoneOn(false);

		mTopBarView = (TopBarView) findViewById(R.id.TabBarViewPlay);
		mTopBarView.setBackButton(true, new TopBarView.onClickButtonLister() {
			
			@Override
			public void onClickButton() {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		mContentTextView = (TextView) findViewById(R.id.ContentEditText);
		
		Intent intent = getIntent();
		String fileName = intent.getExtras().getString("FileName");
		Log.d("VoicePlayActivity", "fileName = " + fileName);
		if (fileName != null) {
			mCurrentFileName = fileName;
			mContentTextView.setText(mCurrentFileName);
		}
		
		mVoiceEngine = new VoiceEngine();
		mAudioPlay = new AudioPlay(16000);
		handler = new Handler();
		bIsStartPlay = false;
		
		mPlayImButton = (ImageButton) findViewById(R.id.StartPlay);
		mPlayImButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.StartPlay:
			//LoginManager.instance();
			if (!bIsStartPlay) {
				bIsStartPlay = true;
				Thread threadPlay = new Thread(new PlayRunnable());
				threadPlay.start();
				mPlayImButton.setImageResource(android.R.drawable.ic_media_pause);
			} else {
				bIsStartPlay = false;
			}
			break;
		case R.id.NextPlay:
			break;
		case R.id.LastPlay:
			break;
		default:
			break;
		} 
	
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	private class PlayRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			FileInputStream fileIn = null;
			if (mCurrentFileName != null) {
				File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + mCurrentFileName);
				try {
					fileIn = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			if (mCurrentFileName == null || fileIn == null) {
				Log.d("VoicePlayActivity", "文件不存在");
				//切换到主线程执行
				handler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						VoicePlayActivity.this.mPlayImButton.setImageResource(android.R.drawable.ic_media_play);
					}
				});
				bIsStartPlay = false;
				return;
			}
			
			int currentPosition = 0;
			
			mAudioPlay.startPlay();
			while (true) {
				if (!bIsStartPlay) {
					break;
				}
				int bufferReadLength = 0;
				if (currentPosition != 0) {
					System.arraycopy(mTempBuffer, currentPosition, mTempBuffer, 0, 320 - currentPosition);
					try {
						bufferReadLength = fileIn.read(mTempBuffer, 320 - currentPosition, currentPosition);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					currentPosition = 0;
				} else {
					try {
						bufferReadLength = fileIn.read(mTempBuffer, 0, 320);
					} catch (IOException e) {	
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
				if (bufferReadLength <= 0) {
					break;
				}
				
				while(true) {
					int opusLength = mTempBuffer[currentPosition + 0];
					byte[] buffer = new byte[opusLength];
					byte[] out = new byte[320];
					System.arraycopy(mTempBuffer, currentPosition + 1, buffer, 0, opusLength);
					
					int pcmLength = mVoiceEngine.opusDecode(buffer, out, opusLength);
					mAudioPlay.playBack(out, pcmLength);
					
					currentPosition += opusLength + 1;
					if ((currentPosition + (int)mTempBuffer[currentPosition] + 1) > bufferReadLength) {
						break;
					}
				}

			}
			
			//切换到主线程执行
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					VoicePlayActivity.this.mPlayImButton.setImageResource(android.R.drawable.ic_media_play);
				}
			});
			mAudioPlay.stopPlay();
			bIsStartPlay = false;
		}
		
	}
	
}
