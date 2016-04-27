package com.voicenotes.manages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;

public class MediaH264Decoder {

	private MediaCodec mMediaDecoder;
	private int mHeight;
	private int mWidth;
	private long mFrameCount;
	private int mFrameRate;
	public byte[] mDecoderOutput;
	
	private FileOutputStream fileOutput;

	public void initMediaDecoder(int width, int height) {
		mWidth = width;
		mHeight = height;
		mFrameCount = 0;
		mFrameRate = 20;
		mDecoderOutput = new byte[width * height * 3 / 2];
		
//		File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/video.h264");
//		try {
//			fileOutput = new FileOutputStream(file);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		mMediaDecoder = MediaCodec.createDecoderByType("video/avc");
		MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", mWidth, mHeight);
		mMediaDecoder.configure(mediaFormat, null, null, 0);
		mMediaDecoder.start();
	}
	
	public int decoder(byte[] input, int length) {
		ByteBuffer[] inputBuffers= mMediaDecoder.getInputBuffers();
		ByteBuffer[] outputBuffers = mMediaDecoder.getOutputBuffers();
		int inputBuffersIndex = mMediaDecoder.dequeueInputBuffer(-1);
		if (inputBuffersIndex >= 0) {
			ByteBuffer inputBuffer = inputBuffers[inputBuffersIndex];
			inputBuffer.clear();
//			inputBuffer.limit(mWidth * mHeight * 3 / 2);
			inputBuffer.put(input);
			inputBuffer.flip();
			long presentationTimeUs = 1000000 * mFrameCount / mFrameRate;
			mFrameCount ++;
			mMediaDecoder.queueInputBuffer(inputBuffersIndex, 0, length, presentationTimeUs, 0);
		}
		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		int outputBuffersIndex = mMediaDecoder.dequeueOutputBuffer(bufferInfo, -1);
		int result = 0;
		Log.d("MediaDecoder", "outputBuffersIndex Decoder = " + outputBuffersIndex);
		switch (outputBuffersIndex) {
			case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
				result = MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED;
				break;
			case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
				MediaFormat mediaFormat = mMediaDecoder.getOutputFormat();
				mWidth = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
				mHeight = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
				mDecoderOutput = new byte[mWidth * mHeight * 3 / 2];
//				mFrameRate = mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE);
				result = MediaCodec.INFO_OUTPUT_FORMAT_CHANGED;
				break;
			default:
				ByteBuffer outBuffer = outputBuffers[outputBuffersIndex];
				byte[] outData = new byte[bufferInfo.size]; 
				outBuffer.get(outData);
				outBuffer.clear();
//				try {
//					fileOutput.write(outData, 0, outData.length);
//				} catch (IOException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				System.arraycopy(outData, 0, mDecoderOutput, 0, outData.length);
				mMediaDecoder.releaseOutputBuffer(outputBuffersIndex, false);
				result = 0;
				break;
		}
		return result;
	}
	
}
