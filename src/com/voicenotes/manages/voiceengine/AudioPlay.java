package com.voicenotes.manages.voiceengine;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioPlay {

	private AudioTrack mAudioTrack;
	private Boolean bIsStartPlay;
	
	public AudioPlay(int sampleRate) {
		if (mAudioTrack == null) {
			bIsStartPlay = false;
			int length = initAudioPlay(sampleRate);
			if (length < 0) {
				return;
			}
		}
	}
	
	private int initAudioPlay(int sampleRate) {
		
		int length = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, length, AudioTrack.MODE_STREAM);
		if (mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
			mAudioTrack = null;
			return -1;
		}
		return length;
	}
	
	public int startPlay() {
		if (mAudioTrack == null) {
			return -1;
		}
		if (!bIsStartPlay) {
			bIsStartPlay = true;
			mAudioTrack.play();
		}
		return 0;
	}
	
	public int stopPlay() {
		if (mAudioTrack == null) {
			return  -1;
		}
		if (bIsStartPlay) {
			bIsStartPlay = false;
			mAudioTrack.stop();
		}
		return 0;
	}
	
	public int playBack(byte[] data, int length) {
		if (mAudioTrack == null) {
			return -1;
		}
		int writeLenght = mAudioTrack.write(data, 0, length);
		if (writeLenght <= 0) {
			return -1;
		}
		return writeLenght;
	}
}
