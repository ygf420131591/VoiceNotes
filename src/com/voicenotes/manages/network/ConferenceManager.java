/*
 * 会议控制管理
 * */

package com.voicenotes.manages.network;

import java.util.ArrayList;
import java.util.List;

import com.voicenotes.manages.entitys.CreateConfInfoEntity;
import com.voicenotes.manages.entitys.CreateConfInfoEntity.ConfLayout;
import com.voicenotes.manages.entitys.CreateConfInfoEntity.DeviceID;
import com.voicenotes.manages.http.DHHttpRequest;
import com.voicenotes.manages.network.BaseManager.OnRepLister;
import com.voicenotes.manages.request.ControlConfAttendeeRequest;
import com.voicenotes.manages.request.RequestHelper;
import com.voicenotes.manages.responses.ConfNetStatusResponse;
import com.voicenotes.manages.responses.ControlConfAttendeeResponse;
import com.voicenotes.manages.responses.CreatConfResponse;
import com.voicenotes.manages.responses.DeleteConfResponse;
import com.voicenotes.manages.responses.GetConfInfoResponse;
import com.voicenotes.manages.responses.GetCurStatusResponse;
import com.voicenotes.manages.responses.JoinConfResponse;
import com.voicenotes.manages.responses.MemberStatusResponse;
import com.voicenotes.manages.responses.OverConfResponse;

public class ConferenceManager extends BaseManager {
	
	private static ConferenceManager ins;
	private CreateConfInfoEntity mConfInfoEntity;
	
	public static ConferenceManager instance() {
		synchronized (ConferenceManager.class) {
			if (ins == null) {
				ins = new ConferenceManager();
			}
		}
		return ins;
	}
	
	public ConferenceManager() {
		super.initManager();
		mConfInfoEntity = new CreateConfInfoEntity();
	}
	
	
//	//设置会议名称
//	public void setConfName(String confName) {
//		mConfInfoEntity.setConfName(confName);
//	}
//	//设置会议类型
//	public void setConfType(int confType) {
//		mConfInfoEntity.setConfType(confType);
//	}
//	//设置会议开始时间
//	public void setConfStartTime(String confStartTime) {
//		mConfInfoEntity.setConfStartTime(confStartTime);
//	}
//	//设置会议时间
//	public void setConfTime(String confTime) {
//		mConfInfoEntity.setConfTime(confTime);
//	}
//	
	
	/**
	 * 创建会议
	 * */
	public void createConf(OnRepLister lister) {
		
		//--------------测试代码------------------------------------//
		//TODO 增加DevicID 和Layout
		DeviceID deviceID = mConfInfoEntity.new DeviceID();
		deviceID.setDevid("30004733");
		mConfInfoEntity.setDeviceID(deviceID);
		
		deviceID = mConfInfoEntity.new DeviceID();
		deviceID.setDevid("30004672");
		mConfInfoEntity.setDeviceID(deviceID);
		
		ConfLayout alayout = mConfInfoEntity.new ConfLayout();
		ConfLayout olayout = mConfInfoEntity.new ConfLayout();
		
		List<Float> layout1 = new ArrayList<Float>();
		layout1.add((float) 0.5);
		layout1.add((float) 0.25);
		layout1.add((float) 0.0);
		layout1.add((float) 0.5);
		layout1.add((float) 0.5);
		
		List<Float> layout2 = new ArrayList<Float>();
		layout2.add((float) 1.5);
		layout2.add((float) 0.25);
		layout2.add((float) 0.0);
		layout2.add((float) 0.5);
		layout2.add((float) 0.5);
		
		alayout.setLayout(layout1);
		alayout.setLayout(layout2);
		
		List<Integer> ssrc = new ArrayList<Integer>();
		ssrc.add(300008520);
		ssrc.add(300004821);
		alayout.setSsrc(ssrc);
		
		List<Integer> interval = new ArrayList<Integer>();
		interval.add(0);
		interval.add(0);
		alayout.setInterval(interval);
		
		olayout.setLayout(layout1);
		olayout.setLayout(layout2);
	
		olayout.setSsrc(ssrc);

		olayout.setInterval(interval);
		
		mConfInfoEntity.setOLayout(olayout);
		mConfInfoEntity.setALayout(alayout);
		//--------------测试代码------------------------------------//
		
		DHHttpRequest request = mRequestHelper.creatConf(mConfInfoEntity);
		CreatConfResponse response = new CreatConfResponse();
		mLister.put(response, lister);
		
		sendRequest(request, response, 30);
	}
	
	/**
	 * 删除会议
	 * */
	public void deleteConf(OnRepLister lister) {
		DHHttpRequest request = RequestHelper.instance().deleteConf("2222");
		DeleteConfResponse response = new DeleteConfResponse();
		mLister.put(response, lister);
		sendRequest(request, response, 30);
	}
	
	/**
	 * 获取当前硬终状态
	 * @param lister 
	 * 
	 */
	public void getCurConfState(OnRepLister lister) {
		DHHttpRequest request = mRequestHelper.getCurStatus();
		GetCurStatusResponse response = new GetCurStatusResponse();
		mLister.put(response, lister);
		sendRequest(request, response, 30);
	}
	
	/**
	 * 获取会议信息
	 */
	public void getConfInfo(OnRepLister lister) {
		DHHttpRequest request = mRequestHelper.getConfInfo();
		GetConfInfoResponse response = new GetConfInfoResponse();
		mLister.put(response, lister);
		sendRequest(request, response, 30);
	}
	
	/**
	 * 加入会议
	 * */
	public void joinConf(String confId, int joinType, OnRepLister lister) {
		DHHttpRequest request = mRequestHelper.joinConf(confId, joinType);
		JoinConfResponse response = new JoinConfResponse();
		mLister.put(response, lister);
		sendRequest(request, response, 30);
	}
	
	/**
	 * 结束会议
	 * */
	public void byeConf(String confId, OnRepLister lister) {
		DHHttpRequest request = mRequestHelper.overConf(confId);
		OverConfResponse response = new OverConfResponse();
		mLister.put(response, lister);
		sendRequest(request, response, 30);
	}
	
	/**
	 * 会议控制
	 * */
	public void controlConfAttendee(String confId, OnRepLister lister) {
		DHHttpRequest request = mRequestHelper.controlAttendee(confId);
		ControlConfAttendeeResponse response = new ControlConfAttendeeResponse();
		mLister.put(response, lister);
		sendRequest(request, response, 30);
	}
	
	/**
	 * 获取与会人状态
	 * */
	public void getMemberStatus(String confId, OnRepLister lister) {
		DHHttpRequest request = mRequestHelper.getMemberStatus(confId);
		MemberStatusResponse response = new MemberStatusResponse();
		mLister.put(response, lister);
		sendRequest(request, response, 30);
	}
	
	/**
	 * 会议网络状态
	 * */
	public void confNetStatus(String confId, OnRepLister lister) {
		DHHttpRequest request = mRequestHelper.getConfNetStatus(confId);
		ConfNetStatusResponse response = new ConfNetStatusResponse();
		mLister.put(response, lister);
		sendRequest(request, response, 30);
	}
}
