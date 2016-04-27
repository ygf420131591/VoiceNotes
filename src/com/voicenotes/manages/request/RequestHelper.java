/*
 * ��װ����http��*/

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
	 *  ��¼����http��
	 *  userName:�û���
	 *  pwd������
	 *  ����: http��¼�����
	 *  */
	public DHHttpRequest loginRequest(String userName, String pwd) {
		LoginRequest info = new LoginRequest();
		info.setUserName(userName);
		info.setPassword(pwd);
		String content = gson.toJson(info);  //http����������

		DHHttpRequest httpPacket = getHttpRequest("/post/login", content);
		return httpPacket;
	}
	
	/*
	 * ������
	 * */
	public DHHttpRequest heartBeat() {
		HeartBeatRequest request = new HeartBeatRequest();
		
		String content = gson.toJson(request);

		DHHttpRequest httpPacket = getHttpRequest("/post/heartbeat", content);
		return httpPacket;
	}
	
	/*
	 * �ǳ�
	 * */
	public DHHttpRequest logoutRequest() {
		LogoutRequest request = new LogoutRequest();
		request.setLogout(true);
		String content = gson.toJson(request);

		DHHttpRequest httpPacket = getHttpRequest("/post/login", content);
		return httpPacket;
	}
	
	/*
	 * ��ȡ��ǰ״̬
	 * */
	public DHHttpRequest getCurStatus() {
		GetCurStatusRequest request = new GetCurStatusRequest();
		request.setSequence(mSequence);
		String content = gson.toJson(request);
		
		DHHttpRequest httpPacket = getHttpRequest("/post/conf/getCurState", content);
		return httpPacket;
	}
	
	/*
	 * ��������
	 * */
	public DHHttpRequest creatConf(CreateConfInfoEntity info) {
		CreatConfRequest request = new CreatConfRequest();
		request.setName(info.getConfName());			//��������
		String confType = String.valueOf(info.getConfType());
		request.setConftype(confType);		//��������
		request.setStartTime(info.getConfStartTime());
		request.setAlayout(info.getAlayout());
		request.setOlayout(info.getOlayout());
		
		request.setSequence(mSequence);
		String content = gson.toJson(request);
		
		DHHttpRequest httpPacket = getHttpRequest("/post/conf/confCreate", content);
		return httpPacket;
	}
	
	/*
	 * ɾ������
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
	 * ��ȡ��ҳ��Ϣ
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
	 * �������
	 * confid:����id
	 * jointype:�������
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
	 * ��������
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
	 * �������
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
	 * ��ȡ�����״̬
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
	 * ��������״̬
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
	 * ϵͳ����
	 * operater �������� 0��ϵͳ����
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
	 * ��ȡ��������
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
	 * ���÷�������
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
