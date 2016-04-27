package com.voicenotes.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.voicenotes.manages.common.ErrorCode;
import com.voicenotes.manages.network.ConferenceManager;
import com.voicenotes.manages.network.BaseManager.OnRepLister;
import com.voicenotes.manages.network.SysOperateManager;
import com.voicenotes.manages.responses.BaseResponse;
import com.voicenotes.manages.responses.CreatConfResponse;
import com.voicenotes.manages.responses.DeleteConfResponse;
import com.voicenotes.manages.responses.GetCurStatusResponse;
import com.voicenotes.manages.voiceengine.AudioRecording;
import com.voicenotes.manages.voiceengine.VoiceEngine;
import com.voicenotes.views.utils.TopBarView;
import com.voicenotes.views.utils.TopBarView.onClickButtonLister;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class VoiceRecordActivity extends BaseActivity {

	private AudioRecording mAudioRecording;
	private VoiceEngine mVoiceEngine;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_record);
		
		TopBarView recordBarView = (TopBarView) findViewById(R.id.TabBarViewRecord);
		recordBarView.setBackButton(true, new onClickButtonLister() {
			
			@Override
			public void onClickButton() {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		ImageButton recordButton = (ImageButton) findViewById(R.id.imageButtonRecord);
		recordButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				setConfig();
//				if (mAudioRecording == null) {
//					mAudioRecording = new AudioRecording(16000);
//				}
//				if (mVoiceEngine  == null) {
//					mVoiceEngine  = new VoiceEngine();
//				}
//				Thread thread = new Thread(new RecordRunable());
//				thread.start();
			}
		});
	}
	
	//删除会议
	public void deleteConf() {
		ConferenceManager.instance().createConf(new OnRepLister() {
			
			@Override
			public void onRepLister(int err, BaseResponse rep) {
				// TODO 处理数据
				if (err == ErrorCode.DH_LOGIN_SUCCESS) {
					DeleteConfResponse response = (DeleteConfResponse) rep;
				}
			}
		});
	}
	//创建会议
	public void createConf() {
		ConferenceManager.instance().deleteConf(new OnRepLister() {
			
			@Override
			public void onRepLister(int err, BaseResponse rep) {
				// TODO 处理数据
				CreatConfResponse response = (CreatConfResponse) rep;
			}
		});
	}
	//获取当前状态
	public void getCurState() {
		ConferenceManager.instance().getCurConfState(new OnRepLister() {
			
			@Override
			public void onRepLister(int err, BaseResponse rep) {
				// TODO 处理数据
				GetCurStatusResponse response = (GetCurStatusResponse)rep;
				
			}
		});
	}
	//获取分页信息
	public void getConfInfo() {
		ConferenceManager.instance().getConfInfo(new OnRepLister() {
			
			@Override
			public void onRepLister(int err, BaseResponse rep) {
				// TODO 处理数据
				
			}
		});
	}
	//获取会议成员状态
	public void getMemberStatus() {
		ConferenceManager.instance().getMemberStatus("1520", new OnRepLister() {
			
			@Override
			public void onRepLister(int err, BaseResponse rep) {
				// TODO 处理数据
				
			}
		});
	}
	//获取会议网络状态
	public void confNetStatus() {
		ConferenceManager.instance().confNetStatus("1520", new OnRepLister() {
			
			@Override
			public void onRepLister(int err, BaseResponse rep) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void getConfig() {
		SysOperateManager.instatce().getConfig("sip", new OnRepLister() {
			
			@Override
			public void onRepLister(int err, BaseResponse rep) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void setConfig() {
		SysOperateManager.instatce().setConfig(new OnRepLister() {
			
			@Override
			public void onRepLister(int err, BaseResponse rep) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private class RecordRunable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mAudioRecording.startRecord();
			File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + "Record3.opus");
			FileOutputStream fout = null;
			try {
				fout = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (fout == null) {
				Log.d("audioRecord", "fout为空");
				return;
			}
			while (true) {
				byte[] buffer = new byte[320];
				int length = mAudioRecording.recordBack(buffer, 320);
				if (length <= 0) {
					Log.d("audioRecord", "audioRecord failure");
					break;
				}
				byte[] out = new byte[320];
				int len = mVoiceEngine.opusEncode(buffer, out, 320);
				if (len <= 0) {
					break;
				}
				byte[] fbout = new byte[len + 1];  
				fbout[0] = (byte) len;
				System.arraycopy(out, 0, fbout, 1, len);  //第一个字节作为opus的长度
				try {
					fout.write(fbout, 0, len + 1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
}
