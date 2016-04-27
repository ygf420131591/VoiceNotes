package com.voicenotes.manages.request;

public class GetConfInfoRequest extends BaseRequest {

	private int pageCur;
	private int pageNum;
	private int pageSort;
	private int conftype;
	private String name;
	private String devid;
	
	public void setPageCur(int pageCurrent) {
		pageCur = pageCurrent;
	}
	
	public void setPageNum(int pageNumCur) {
		pageNum = pageNumCur;
	}
	
	public void setPageSort(int pageSortCur) {
		pageSort = pageSortCur;
	}
	
	public void setConftype(int conftypeCur) {
		conftype = conftypeCur;
	}
	
	public void setName(String nameCur) {
		name = nameCur;
	}
	
	public void setDevid(String devId) {
		devid = devId;
	}
	
}
