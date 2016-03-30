package com.voicenotes.views;

import java.util.ArrayList;
import java.util.List;

import com.voicenotes.fragment.VideoFragment;
import com.voicenotes.fragment.VoiceFragment;

import android.R.anim;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends FragmentActivity{
	
	private FragmentTabHost mTabHost;
	private LayoutInflater mLayoutInflater;
	private Class mFragmentArray[] = {VideoFragment.class, VoiceFragment.class};
	private String mTextArray[] = {"Video", "Voice"};
	private int mImageArray[] = {R.drawable.common_tab_my_s, R.drawable.common_tab_record_s};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
	}
	
	
	private void initView() {
		mLayoutInflater = LayoutInflater.from(this);
		
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		
		int count = mFragmentArray.length;
		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec(mTextArray[i]).setIndicator(getTabItemView(i));
			
			mTabHost.addTab(tabSpec, mFragmentArray[i], null);
		}
	}
	
	private View getTabItemView(int index) {
		View view = mLayoutInflater.inflate(R.layout.tab_item_view, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageArray[index]);
		
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextArray[index]);
		
		return view;
	}
	
}
