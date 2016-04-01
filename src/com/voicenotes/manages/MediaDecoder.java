package com.voicenotes.manages;

import java.nio.ByteBuffer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

public class MediaDecoder {

	private MediaCodec mMediaDecoder;
	private int mHeight;
	private int mWidth;
	private long mFrameCount;
	private int mFrameRate;
	
	public void initMediaDecoder(int width, int height) {
		mWidth = width;
		mHeight = height;
		mFrameCount = 0;
		mFrameRate = 10;
		
		mMediaDecoder = MediaCodec.createDecoderByType("video/avc");
		MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", mWidth, mHeight);
		mMediaDecoder.configure(mediaFormat, null, null, 0);
		mMediaDecoder.start();
	}
	
	public void decoder(byte[] input, byte[] output, int length) {
		ByteBuffer[] inputBuffers= mMediaDecoder.getInputBuffers();
		ByteBuffer[] outputBuffers = mMediaDecoder.getOutputBuffers();
		int inputBuffersIndex = mMediaDecoder.dequeueInputBuffer(0);
		if (inputBuffersIndex >= 0) {
			ByteBuffer inputBuffer = inputBuffers[inputBuffersIndex];
			inputBuffer.clear();
			inputBuffer.put(input);
			inputBuffer.flip();
			long presentationTimeUs = 1000000 * mFrameCount / mFrameRate;
			mFrameCount ++;
			mMediaDecoder.queueInputBuffer(inputBuffersIndex, 0, length, presentationTimeUs, 0);
		}
		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		int outputBuffersIndex = mMediaDecoder.dequeueOutputBuffer(bufferInfo, -1);
		Log.d("MediaDecoder", "outputBuffersIndex = " + outputBuffersIndex);
		switch (outputBuffersIndex) {
			case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
				break;
			case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
				MediaFormat mediaFormat = mMediaDecoder.getOutputFormat();
				mWidth = mediaFormat.getInteger("width");
				mHeight = mediaFormat.getInteger("height");
				break;
			default:
				ByteBuffer outBuffer = outputBuffers[outputBuffersIndex];
				byte[] outData = new byte[bufferInfo.size]; 
				outBuffer.get(outData);
				outBuffer.clear();
				mMediaDecoder.releaseOutputBuffer(outputBuffersIndex, false);
				break;
		}
	}
	
}
