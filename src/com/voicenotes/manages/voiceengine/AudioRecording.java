package com.voicenotes.manages.voiceengine;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

public class AudioRecording {

	private AudioRecord mAudioRecord;
	
	public AudioRecording(int sampleRate) {
		if (mAudioRecord == null) {
			int result = initAudioRecord(sampleRate);
			if (result < 0) {
				return;
			}
		}
	}
	
	public int initAudioRecord(int sampleRate) {
		int length = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mAudioRecord = new AudioRecord(AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, length);
		if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
			mAudioRecord = null;
			return -1;
		}
		return length;
	}
	
	public void startRecord() {
		if (mAudioRecord == null) {
			return;
		}
		mAudioRecord.startRecording();
	}
	
	public int recordBack(byte[] buffer, int length) {
		if (mAudioRecord == null) {
			return -1;
		}
		
		int result = mAudioRecord.read(buffer, 0, length);
		if (result <=0 ) {
			return -1;
		}
		return result;
	}
	
}
