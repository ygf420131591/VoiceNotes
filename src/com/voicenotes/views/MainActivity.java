package com.voicenotes.views;

import com.voicenotes.fragment.MyFragment;
import com.voicenotes.fragment.VideoFragment;
import com.voicenotes.fragment.VoiceFragment;
import com.voicenotes.views.utils.NavTabButton;
import com.voicenotes.views.utils.TopBarView;
import com.voicenotes.views.utils.TopBarView.onClickButtonLister;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.widget.TabHost.TabSpec;;

public class MainActivity extends FragmentActivity {
	
	private FragmentTabHost mTabHost;
	private TopBarView mTopBarView;
	
	private NavTabButton[] navButton; 
	private Class mFragmentArray[] = {VideoFragment.class, VoiceFragment.class, MyFragment.class};
	private String mTextArray[] = {"Video", "Voice", "My"};
	private int mImageArray_s[] = {R.drawable.common_tab_schedule_s, R.drawable.common_tab_record_s, R.drawable.common_tab_my_s};
	private int[] mImageArray_n = {R.drawable.common_tab_schedule_n, R.drawable.common_tab_record_n, R.drawable.common_tab_my_n};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
	}
	
	private void initView() {
		
		mTopBarView = (TopBarView) findViewById(R.id.TabBarView);
		mTopBarView.setTitleText(mTextArray[0]);
		
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		
		int count = mFragmentArray.length;
		navButton = new NavTabButton[count];
		for (int i = 0; i < count; i++) {
			navButton[i] = new NavTabButton(this, i);
			navButton[i].setTitle(mTextArray[i]);
			if (i == 0) {
				navButton[i].setImageView(mImageArray_s[i]);
			} else {
				navButton[i].setImageView(mImageArray_n[i]);
			}
			TabSpec tabSpec = mTabHost.newTabSpec(mTextArray[i]).setIndicator(navButton[i]);
			mTabHost.addTab(tabSpec, mFragmentArray[i], null);
		}
	}
	
	public void setSelectedIndicator(int index) {
		mTabHost.setCurrentTab(index);			//切换到相应的tab;
		mTopBarView.setTitleText(mTextArray[index]);  //重置顶部标题
		if (index == 1) {
			mTopBarView.setNextButton(true, new onClickButtonLister() {
				
				@Override
				public void onClickButton() {
					// TODO Auto-generated method stub
					Intent intent = new Intent(MainActivity.this, VoiceRecordActivity.class);
					startActivity(intent);
				}
			});
		} else {
			mTopBarView.setNextButton(false, null);
		}
		
		int count = mFragmentArray.length;
		for (int i = 0; i < count; i++) {
			navButton[i].setImageView(mImageArray_n[i]);
		}
		navButton[index].setImageView(mImageArray_s[index]);
	}
	

}
