package com.voicenotes.views;

import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class CreateQRCodeActivity extends Activity {

	private ImageView mQRCodeImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_qrcode);
		
		mQRCodeImage = (ImageView) findViewById(R.id.QRCode);
		createQRCode("http://172.10.1.109:9090");
		
	}
	
	private void createQRCode(String url) {
		
		if (url == null) {
			return;
		}
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		try {
			BitMatrix matrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, 200, 200, hints);
			int[] pixels = new int[200 * 200];
			for(int y = 0; y < 200; y++) {
				for(int x = 0; x < 200; x++) {
					if (matrix.get(x, y)) {
						pixels[y * 200 + x] = 0xff000000;   //°×
					} else {
						pixels[y * 200 + x] = 0xffffffff;  //ºÚ
					}
				}
			}
			Bitmap map = Bitmap.createBitmap(200, 200, Config.ARGB_8888);
			map.setPixels(pixels, 0, 200, 0, 0, 200, 200);
			mQRCodeImage.setImageBitmap(map);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
