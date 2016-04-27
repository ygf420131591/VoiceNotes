/*¶þÎ¬Âë½âÂë*/

package com.voicenotes.manages.qrcode;

import java.util.Hashtable;
import java.util.Vector;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.HybridBinarizer;
import com.voicenotes.views.handlers.ScanQRCodeHandler;
import com.zijunlin.Zxing.Demo.camera.PlanarYUVLuminanceSource;
import com.zijunlin.Zxing.Demo.decoding.DecodeFormatManager;
import com.zijunlin.Zxing.Demo.view.ViewfinderResultPointCallback;
import com.zijunlin.Zxing.Demo.view.ViewfinderView;

public class QRCodeDecoder {

	private MultiFormatReader mMultiFormatReader;
	private Hashtable<DecodeHintType, Object> hints;
	
	private Handler mHandler;
	
	public QRCodeDecoder(ViewfinderView view, Handler handler) {
		
		mHandler = handler;
		
		mMultiFormatReader = new MultiFormatReader();
		
		hints = new Hashtable<DecodeHintType, Object>(3);
		Vector<BarcodeFormat> decodeFormats;
		decodeFormats = new Vector<BarcodeFormat>();
  		decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
  		decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
  		decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
  		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
  		
  		ResultPointCallback resultPointCallback = new ViewfinderResultPointCallback(view);
//  		hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
  		hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
  		mMultiFormatReader.setHints(hints);
	}
	
	public void decode(byte[] data, int width, int height) {
		Result resultRaw = null;
		//TODO
		
		byte[] rotatedData = new byte[data.length];  
//	    for (int y = 0; y < height; y++) {  
//	        for (int x = 0; x < width; x++)  
//	            rotatedData[x * height + height - y - 1] = data[x + y * width];  
//	    }  
		
		PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, 355, 383, 569, 193);
		
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		try {
			resultRaw = mMultiFormatReader.decode(bitmap);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			mMultiFormatReader.reset();
		}
		if (resultRaw != null) {
			String result = resultRaw.toString();
			Message message = Message.obtain(mHandler, 0, result);
			message.sendToTarget();
			
			Log.d("result", result);
		}
		
	}
}
