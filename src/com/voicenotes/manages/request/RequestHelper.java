/*
 * 封装请求http报*/

package com.voicenotes.manages.request;

import com.google.gson.Gson;
import com.voicenotes.manages.common.ConfigContant;
import com.voicenotes.manages.entitys.CreateConfInfoEntity;
import com.voicenotes.manages.http.DHHttpRequest;
import com.voicenotes.manages.request.SetConfigRequest.AudioChannel;

public class RequestHelper {

	private static RequestHelper requestHelper;
	private Gson gson;
	private int mSequence;
	
	public static RequestHelper instance() {
		synchronized (RequestHelper.class) {
			if (requestHelper == null) {
				requestHelper = new RequestHelper();
			}
		}
		return requestHelper;
	}
	
	public RequestHelper() {
		mSequence = 0;
		gson = new Gson();
	}
	
	/*
	 *  登录请求http报
	 *  userName:用户名
	 *  pwd：密码
	 *  返回: http登录请求包
	 *  */
	public DHHttpRequest loginRequest(String userName, String pwd) {
		LoginRequest info = new LoginRequest();
		info.setUserName(userName);
		info.setPassword(pwd);
		String content = gson.toJson(info);  //http报正文内容

		DHHttpRequest httpPacket = getHttpRequest("/post/login", content);
		return httpPacket;
	}
	
	/*
	 * 心跳包
	 * */
	public DHHttpRequest heartBeat() {
		HeartBeatRequest request = new HeartBeatRequest();
		
		String content = gson.toJson(request);

		DHHttpRequest httpPacket = getHttpRequest("/post/heartbeat", content);
		return httpPacket;
	}
	
	/*
	 * 登出
	 * */
	public DHHttpRequest logoutRequest() {
		LogoutRequest request = new LogoutRequest();
		request.setLogout(true);
		String content = gson.toJson(request);

		DHHttpRequest httpPacket = getHttpRequest("/post/login", content);
		return httpPacket;
	}
	
	/*
	 * 获取当前状态
	 * */
	public DHHttpRequest getCurStatus() {
		GetCurStatusRequest request = new GetCurStatusRequest();
		request.setSequence(mSequence);
		String content = gson.toJson(request);
		
		DHHttpRequest httpPacket = getHttpRequest("/post/conf/getCurState", content);
		return httpPacket;
	}
	
	/*
	 * 创建会议
	 * */
	public DHHttpRequest creatConf(CreateConfInfoEntity info) {
		CreatConfRequest request = new CreatConfRequest();
		request.setName(info.getConfName());			//会议名称
		String confType = String.valueOf(info.getConfType());
		request.setConftype(confType);		//会议类型
		request.setStartTime(info.getConfStartTime());
		request.setAlayout(info.getAlayout());
		request.setOlayout(info.getOlayout());
		
		request.setSequence(mSequence);
		String content = gson.toJson(request);
		
		DHHttpRequest httpPacket = getHttpRequest("/post/conf/confCreate", content);
		return httpPacket;
	}
	
	/*
	 * 删除会议
	 * */
	public DHHttpRequest deleteConf(String confId) {
		DeleteConfRequest request = new DeleteConfRequest();
		request.setConfid(confId);
		request.setSequence(mSequence);
		String content = gson.toJson(request);

		DHHttpRequest httpPacket = getHttpRequest("/post/conf/confDel", content);
		return httpPacket;
		
	}
	
	/*
	 * 获取分页信息
	 * */
	public DHHttpRequest getConfInfo() {
		GetConfInfoRequest request = new GetConfInfoRequest();
		request.setPageCur(1);
		request.setPageNum(20);
		request.setPageSort(0);
		request.setConftype(1);
		request.setName("");
		request.setDevid("");
		
		request.setSequence(mSequence);
		String content = gson.toJson(request);
		
		DHHttpRequest httpPacket = getHttpRequest("/post/conf/confDel", content);
		return httpPacket;
	}
	
	/*
	 * 加入会议
	 * confid:会议id
	 * jointype:入会类型
	 * */
	public DHHttpRequest joinConf(String confid, int jointype) {
		JoinConfRequest request = new JoinConfRequest();
		request.setConfid(confid);
		request.setJointype(jointype);
		request.setSequence(mSequence);
		String content = gson.toJson(request);
	
		DHHttpRequest httpPacket = getHttpRequest("/post/conf/joinConf", content);
		return httpPacket;
	}
	
	/*
	 * 结束会议
	 * */
	public DHHttpRequest overConf(String confid) {
		OverConfRequest request = new OverConfRequest();
		request.setConfid(confid);
		request.setSequence(mSequence);
		String content = gson.toJson(request);
		
		DHHttpRequest httpPacket = getHttpRequest("/post/conf/byeConf", content);
		return httpPacket;
	}
	
	/*
	 * 会议控制
	 * */
	public DHHttpRequest controlAttendee(String confid) {
		ControlConfAttendeeRequest request = new ControlConfAttendeeRequest();
		request.setConfid(confid);
		request.setOprType(ConfigContant.DH_CONF_OPERATION_HANGUP);
		String[] devid = new String[2];
		devid[0] = "30005655";
		devid[1] = "30005654";
		request.setDevid(devid);
		request.setEndtime("2016-04-25 00:00:00");
		request.setSequence(mSequence);
		String content = gson.toJson(request);
		
		DHHttpRequest httpPacket = getHttpRequest("/post/conf/confControlAttendee", content);
		return httpPacket;
	}
	
	/*
	 * 获取与会人状态
	 * */
	public DHHttpRequest getMemberStatus(String confid) {
		MemberStatusRequest request = new MemberStatusRequest();
		request.setConfid(confid);
		request.setSequence(mSequence);
		String content = gson.toJson(request);
		
		DHHttpRequest httpPacket = getHttpRequest("/post/conf/getMemberStatus", content);
		return httpPacket;
	}
	
	/*
	 * 会议网络状态
	 * */
	public DHHttpRequest getConfNetStatus(String confid) {
		ConfNetStatusRequest request = new ConfNetStatusRequest();
		request.setConfId(confid);
		request.setSequence(mSequence);
		String content = gson.toJson(request);
		
		DHHttpRequest httpPacket = getHttpRequest("/post/conf/confNetStatus", content);
		return httpPacket;
	}
	
	/*
	 * 系统操作
	 * operater 操作类型 0：系统重启
	 * */
	public DHHttpRequest sysOpeRequest(int operater) {
		SysOperateRequest request = new SysOperateRequest();
//		request.setConfid(confid);
		request.setSequence(mSequence);
		String content = gson.toJson(request);
		
		DHHttpRequest httpPacket = getHttpRequest("/post/sys/sysOperate", content);
		return httpPacket;
	}

	/*
	 * 获取服务配置
	 * */
	public DHHttpRequest getConfig(String name) {
		GetConfigRequest  request = new GetConfigRequest();
		request.setName(name);
		request.setSequence(mSequence);
		String content = gson.toJson(request);
		
		DHHttpRequest httpPacket = getHttpRequest("/post/sys/getConfig", content);
		return httpPacket;
	}
	

	/*
	 * 设置服务配置
	 * */
	public DHHttpRequest setConfig() {
		SetConfigRequest  request = new SetConfigRequest();
		request.setName("audio");
		AudioChannel audioChannel = request.new AudioChannel();
		audioChannel.setEchoEnable("true");
		audioChannel.setEchoDelay(1);
		audioChannel.setLoopBack("true");
		audioChannel.setOutput(0);
		audioChannel.setInput(0);
		request.setAudioChannel(audioChannel);
		
		request.setSequence(mSequence);
		String content = gson.toJson(request);
		
		DHHttpRequest httpPacket = getHttpRequest("/post/sys/setConfig", content);
		return httpPacket;
	}
	
	
	private DHHttpRequest getHttpRequest(String url, String content) {
		DHHttpRequest httpPacket = new DHHttpRequest();
		httpPacket.setHttpRequestUrl(url);		//http url
		httpPacket.setHttpSequence(mSequence);	//http sequence
		httpPacket.setHttpBady(content);
		mSequence ++;
		return httpPacket; 
	}
	
}
