package com.mm.android.tplayer;

public interface MulticastListener {
	public int onData(byte[] buffer, int nLen, int offset);
	
	/**
	 * ��������
	 */
	public void onSearchFinish(int id);
}
