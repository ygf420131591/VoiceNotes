package com.voicenotes.views;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.voicenotes.manages.common.ActionContant;
import com.voicenotes.manages.common.ErrorCode;
import com.voicenotes.manages.common.SysContant;
import com.voicenotes.manages.network.BaseManager.OnRepLister;
import com.voicenotes.manages.network.LoginManager;
import com.voicenotes.manages.responses.BaseResponse;
import com.voicenotes.views.utils.DHBroadcastHelper;
import com.voicenotes.views.utils.DHBroadcastHelper.OnBroadcastLister;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener, OnBroadcastLister {

	public final static int SCAN_QR_CODE = 0;
	
	private TextView mUserNameTextView;
	private TextView mPassWordTextView;
	
	private ImageView mLoginImageView;
	private ImageView mScanQRImageView;
	private ImageView mCreateQRImageView;
	
	private SweetAlertDialog mSweetAlertDialog;
	
	private Handler mHandelr;
	private DHBroadcastHelper mBroadcastHelper = new DHBroadcastHelper();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		//通过broadcast回调
		List<String> actions = new ArrayList<String>();
		actions.add(ActionContant.DH_ACTION_LOGIN);
		mBroadcastHelper.registerBroadcast(getApplicationContext(), actions, this);
		LoginManager.instance().setContext(this);
		
		mUserNameTextView = (TextView) findViewById(R.id.userNameeditText);
		mPassWordTextView = (TextView) findViewById(R.id.passWordeditText);
		
		mUserNameTextView.setText("30004733");
		mPassWordTextView.setText("123456");
		
		mLoginImageView = (ImageView) findViewById(R.id.LoginimageView);
		mLoginImageView.setOnClickListener(this);
		
		mScanQRImageView = (ImageView) findViewById(R.id.scanQRimageView);
		mScanQRImageView.setOnClickListener(this);
		
		mCreateQRImageView = (ImageView) findViewById(R.id.createQRImageView);
		mCreateQRImageView.setOnClickListener(this);
		
		mHandelr = new Handler();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.LoginimageView:
			attempLogin("http://172.10.1.109:9090");
			break;
		case R.id.scanQRimageView:
			scanQRCode();
			break;
		case R.id.createQRImageView:
		 	createQRCode();
		 	break;
		default:
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == SCAN_QR_CODE) {
			if (resultCode == RESULT_OK) {
				String url = data.getStringExtra("url");
				attempLogin(url);
			}
		}
	}

	private void createQRCode() {
		Intent intent = new Intent(LoginActivity.this, CreateQRCodeActivity.class);
		startActivity(intent);
	}
	
	//扫描二维码
	private void scanQRCode() {
		Intent intent = new Intent(LoginActivity.this, ScanQRCodeActivity.class);
		startActivityForResult(intent, SCAN_QR_CODE);
	}
	//直接登录
	private void attempLogin(String url) {
		
		String userNameString = mUserNameTextView.getText().toString();
		String passWordString = mPassWordTextView.getText().toString();
	
		
		if (TextUtils.isEmpty(passWordString) || TextUtils.isEmpty(userNameString)) {
			Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_LONG).show();
			return;
		}
		
		startProgress();
		
//		LoginManager.instance().connect("172.10.1.109:9090", userNameString, passWordString, ActionContant.DH_ACTION_LOGIN);
		
		LoginManager.instance().connect(url, userNameString, passWordString, new OnRepLister() {
			
			@Override
			public void onRepLister(int err, BaseResponse rep) {
				// TODO Auto-generated method stub
				if (mSweetAlertDialog != null) {
					mSweetAlertDialog.dismiss();
					mSweetAlertDialog = null;
				}
				final String res;
				if (err == ErrorCode.DH_LOGIN_CONNECT_ERR || err == ErrorCode.DH_LOGIN_OVERTIME) {
					if (err == ErrorCode.DH_LOGIN_CONNECT_ERR) {
						res = "连接失败";	
					} else {
						res = "登录超时";
					}
					mHandelr.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(LoginActivity.this, res, Toast.LENGTH_LONG).show();

							
//							new AlertDialog.Builder(LoginActivity.this).
//							setTitle(res).
//							create().
//							show();
						}
					});
				}
				if (err == ErrorCode.DH_LOGIN_SUCCESS) {
					mHandelr.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Intent intent = new Intent(LoginActivity.this, MainActivity.class);
							startActivity(intent);
						}
					});
				}
			}
		});
		
		
//		LoginManager.instance().login(userNameString, passWordString);
	
	}

	private void startProgress() {
		if (mSweetAlertDialog == null) {
			mSweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
		}
		mSweetAlertDialog.show();
		
	}
	
	//通过broadcast回调
	@Override
	public void onAction(String action, Intent intent) {
		// TODO Auto-generated method stub
		int err = intent.getIntExtra("error", ErrorCode.DH_LOGIN_OVERTIME);
		if (err == ErrorCode.DH_LOGIN_SUCCESS) {
			BaseResponse rep = (BaseResponse) intent.getSerializableExtra("BaseResponse");
			mHandelr.post(new Runnable() {
			
				@Override
				public void run() {		
					// TODO Auto-generated method stub
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
				}
			});
		}
	}


	
}
