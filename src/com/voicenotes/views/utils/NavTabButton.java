package com.voicenotes.views.utils;


import com.voicenotes.views.MainActivity;
import com.voicenotes.views.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NavTabButton extends LinearLayout {

	private ImageView mImageView;
	private TextView mTextView;
	private LinearLayout mContainer;
	
	private int mIndex = 0;
	private Context mContext = null;
	
	
	public NavTabButton(Context context, int id) {
		this(context, null);
		mIndex = id;
		// TODO Auto-generated constructor stub
	}
	
    public NavTabButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public NavTabButton(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	
    	initView(context);
    }
    
    private void initView(Context context) {
    	View layout = LayoutInflater.from(context).inflate(R.layout.tab_item_view, this, true);
    	
    	OnClickListener clickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity mainActivity = (MainActivity)mContext;
				mainActivity.setSelectedIndicator(mIndex);
			}
		};
    	
		mContainer = (LinearLayout) layout.findViewById(R.id.container);
		mContainer.setOnClickListener(clickListener);
    	mImageView = (ImageView) layout.findViewById(R.id.imageview);
		mTextView = (TextView) layout.findViewById(R.id.textview);
		mContext = context;
    }

    
    public void setImageView(int resId) {
    	mImageView.setImageResource(resId);
    }
    
    public void setTitle(String text) {
    	mTextView.setText(text);
    }
    
}
