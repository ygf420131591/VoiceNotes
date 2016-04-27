package com.voicenotes.manages;

import java.nio.ByteBuffer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

public class MediaH264Encoder {

	private MediaCodec mMediaEncoder;
	private int mWidth;
	private int mHeight;
	private long mFrameCount;
	
	public byte[] outBuffer;
	public int length;
	
	public void initMediaEncoder(int width, int heigth) {
		mWidth = width;
		mHeight = heigth;
		mFrameCount = 0;
		
		outBuffer = new byte[100000];
		
		mMediaEncoder = MediaCodec.createEncoderByType("video/avc");
//		mMediaEncoder = MediaCodec.createByCodecName("OMX.qcom.video.encoder.avc");
		MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", width, heigth);
		mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1024 * 1024);
		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 10);	
		mMediaEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
		mMediaEncoder.start();
	}

	
	public int encoder(byte[] buffer) {
//		swapNV21toNV12(buffer);
		ByteBuffer[] inputBuffers = mMediaEncoder.getInputBuffers();
		ByteBuffer[] outputBuffers = mMediaEncoder.getOutputBuffers();
		int inputBufferIndex = mMediaEncoder.dequeueInputBuffer(0);
		if (inputBufferIndex >= 0) {
			ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
			inputBuffer.clear();
//			inputBuffer.put;
			long presentationTimeUs = 1000000L * mFrameCount / 30;
			mMediaEncoder.queueInputBuffer(inputBufferIndex, 0, buffer.length, presentationTimeUs, 0);
			mFrameCount ++;
		}
		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		int outputBufferIndex = mMediaEncoder.dequeueOutputBuffer(bufferInfo, 0);
		Log.d("MediaEncoder", "outputBufferIndex = " + outputBufferIndex);
		if (outputBufferIndex >= 0) {
			ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
			byte[] outData = new byte[bufferInfo.size]; 
            outputBuffer.get(outData);
            outputBuffer.clear();
            mMediaEncoder.releaseOutputBuffer(outputBufferIndex, false);
            if (outData[0] == 0x0 && outData[1] == 0x0 && outData[2] == 0x0 && outData[3] == 0x1) {
            	if ((outData[4] & 0x1f) == 0x06 || (outData[4] & 0x1f) == 0x07) {
            		System.arraycopy(outData, 0, outBuffer, 0, outData.length);
            		length = outData.length;
            		return 0;
            	}
            	else if ((outData[4] & 0x1f) == 0x05){
            		System.arraycopy(outData, 0, outBuffer, length, outData.length);
            		length += outData.length;
            		return length;
            	} else {
            		System.arraycopy(outData, 0, outBuffer, 0, outData.length);
            		length = outData.length;
            		return length;
            	}
            } 

		} else if(outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
			outputBuffers = mMediaEncoder.getOutputBuffers();
		} else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//			MediaFormat format = mMediaDecoder.getOutputFormat();
		} else {
//			mMediaDecoder.flush();
		}
		return -1;
	}
	
//	private void swapNV21toNV12(byte[] bytes) {
//		int uv_offset = mHeight * mWidth;
//		int u_size = mHeight * mHeight / 4;
//		for (int i = 0; i < u_size; i++) {
//			byte temp;
//			temp = bytes[uv_offset + i * 2];
//			bytes[uv_offset + i * 2] = bytes[uv_offset + i * 2 + 1];
//			bytes[uv_offset+i*2+1] = temp;
//		}
//	}
}
