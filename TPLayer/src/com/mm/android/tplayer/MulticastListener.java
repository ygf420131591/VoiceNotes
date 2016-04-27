package com.mm.android.tplayer;

public interface MulticastListener {
	public int onData(byte[] buffer, int nLen, int offset);
	
	/**
	 * ËÑË÷½áÊø
	 */
	public void onSearchFinish(int id);
}
