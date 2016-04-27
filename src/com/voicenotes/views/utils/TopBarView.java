package com.voicenotes.views.utils;

import com.voicenotes.views.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class TopBarView extends RelativeLayout{
	
	private onClickButtonLister mBackClick;
	private onClickButtonLister mNextClick;
	private ImageView mBackView;
	private ImageView mNextView;
	private TextView mTitleTextView;
	
	public TopBarView(Context context) {
//		super(context);  
		// TODO Auto-generated constructor stub
		this(context, null);
	}
	
	public TopBarView(Context context, AttributeSet attrs) { 
		this(context, attrs,R.style.AppTheme);  
	}  
	  
	public TopBarView(Context context, AttributeSet attrs, int defStyle) {  
		super(context, attrs, defStyle);  
//	    getConfig(context, attrs);    
	    initView(context);  
	}  
	
	private void initView(Context context) {
		View layout = LayoutInflater.from(context).inflate(R.layout.item_navigation, this, true);
		
		mBackView = (ImageView) layout.findViewById(R.id.imageBack);
		mNextView = (ImageView) layout.findViewById(R.id.imageNext);
		mTitleTextView = (TextView) layout.findViewById(R.id.text_title);
		
		mBackView.setVisibility(View.GONE);
		mNextView.setVisibility(View.GONE);
		mTitleTextView.setVisibility(View.GONE);
	}
	
	public void setBackButton(Boolean isShow, onClickButtonLister backClick) {
		if (isShow) {
			mBackClick = backClick;
			mBackView.setVisibility(View.VISIBLE);
			mBackView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mBackClick.onClickButton();
				}
			});
		} else {
			mBackView.setVisibility(View.GONE);
		}
	}
	
	public void setNextButton(Boolean isShow, onClickButtonLister nextClick) {
		if (isShow) {
			mNextView.setVisibility(View.VISIBLE);
			mNextClick = nextClick;
			mNextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mNextClick.onClickButton();
				}
			});
		} else {
			mNextView.setVisibility(View.GONE);
		}
	}
	
	public void setNextButtonImage(int resid) {
		
	}
	
	public void setTitleText(String text) {
		mTitleTextView.setVisibility(View.VISIBLE);
		mTitleTextView.setText(text);
	}

	
	public interface onClickButtonLister {
		public void onClickButton();
	}
	
}
