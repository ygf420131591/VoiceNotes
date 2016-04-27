/*
 * 创会用的会议信息
 * */

package com.voicenotes.manages.entitys;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes.Name;

import android.R.integer;

import com.voicenotes.manages.common.ConfigContant;

public class CreateConfInfoEntity {

	private String mConfName;
	private int mConfType;
	private String mConfStartTime;
	private String mConfTime;
	private List<DeviceID> mDeviceID;
	private ConfLayout mOlayout;
	private ConfLayout mAlayout;
	
	public CreateConfInfoEntity() {
		mConfName = "会议";
		mConfType = ConfigContant.DH_CONF_TYPE_APPOINTMENT;
		mConfStartTime = "2016-04-22";
		mConfTime = "2小时";
		mDeviceID = new ArrayList<CreateConfInfoEntity.DeviceID>();
		mOlayout = new ConfLayout();
		mOlayout = new ConfLayout();
	}
	
	
	public void setDeviceID(DeviceID deviceId) {
		if (mDeviceID == null) {
			mDeviceID = new ArrayList<CreateConfInfoEntity.DeviceID>();
		}
		mDeviceID.add(deviceId);
	}
	
	public void setOLayout(ConfLayout oLayout) {
		mOlayout = oLayout;
	}
	
	public void setALayout(ConfLayout aLayout) {
		mAlayout = aLayout;
	}
	
	public void setConfName(String confName) {
		mConfName = confName;
	}
	
	public void setConfType(int confType) {
		mConfType = confType;
	}
	
	public void setConfStartTime(String confStartTime) {
		mConfStartTime = confStartTime;
	}
	
	public void setConfTime(String confTime) {
		mConfTime = confTime;
	}
	
	public String getConfName() {
		return mConfName;
	}
	
	public int getConfType() {
		return mConfType;
	}
	
	public String getConfStartTime() {
		return mConfStartTime;
	}
	
	public String getConfTime() {
		return mConfTime;
	}
	
	public ConfLayout getAlayout() {
		return mAlayout;
	}
	
	public ConfLayout getOlayout() {
		return mOlayout;
	}
	
	public class DeviceID {
		private String devid;
		
		public void setDevid(String devId) {
			devid = devId;
		}
	}
	
	
	public class ConfLayout {
		private String id;
		private List<List<Float>> layout;
		private List<Integer> ssrc;
		private List<Integer> interval;
		
		public void setId(String devId) {
			id = devId;
		}
		
		public void setLayout(List<Float> layOut) {
			if (layout == null) {
				layout = new ArrayList<List<Float>>();
			}
			layout.add(layOut);
		}
		
		public void setSsrc(List<Integer> Ssrc) {
			ssrc = Ssrc;
		}
		
		public void setInterval(List<Integer> interVal) {
			interval = interVal;
		}
	}
	
}
