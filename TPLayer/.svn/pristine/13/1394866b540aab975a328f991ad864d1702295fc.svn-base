package com.mm.android.tplayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class MulticastClient implements Runnable{
	private static final String MULTICAST_IP = "239.255.255.251";	//组播地址
	private static final int 	MULTICAST_PORT = 37810;				//组播端口
	private static final int	MAX_LEN = 1024 * 4;					//接受数据的长度
	
	private MulticastSocket multicastSocket;
	private InetAddress group;
	private DatagramPacket sendPacket;								//发送数据的DatagramPacket
	private DatagramPacket receivePacket; 							//接收数据的DatagramPacket
	private MulticastListener mListener;
	private Thread mThread;
	
	private boolean isInit;											//是否初始化完成
	private boolean flag;											//接收数据线程是否停止的标志
	private int		mTimeOut	= 5000;
	private int		mSearchId;
	
	
	public MulticastClient(int timeOut){
		multicastSocket = null;
		sendPacket = null;
		flag = true;
		isInit = false;
		mTimeOut = timeOut;
	}
	
	public void init() {
        try
		{
			multicastSocket = new MulticastSocket(MULTICAST_PORT);
			multicastSocket.setLoopbackMode(false);
			multicastSocket.setSoTimeout(mTimeOut);
			group = InetAddress.getByName(MULTICAST_IP);
			multicastSocket.joinGroup(group);
			byte[] receiveData = new byte[MAX_LEN];
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
		} catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
        isInit = true;
        flag = true;
        mThread = new Thread(this);
        mThread.start();
	}
	
	public void setListener(MulticastListener listener,int id) {
		this.mListener = listener;
		mSearchId = id;
	}
	
	public void setFlag(boolean flag) {
		this.flag = flag;
		clean();
	}

	private void clean() {
		if (multicastSocket != null)
		{
			try
			{
				multicastSocket.leaveGroup(group);
				multicastSocket.close();
				isInit = false;
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 发送，接收命令
	 * @param buf
	 * @return 0 发送成功  -1失败 
	 */
	public int send(byte[] buf) {
		if (!isInit)
		{
			init();
		}
		try
		{
			sendPacket = new DatagramPacket(buf, buf.length, group, MULTICAST_PORT);
			multicastSocket.send(sendPacket);
		} catch (IOException e)
		{
			clean();
			e.printStackTrace();
			return -1;
		}
		
		return 0;
	}
	
	public void run() {
		while (flag)
		{
			try
			{
				receivePacket.setLength(MAX_LEN);
				multicastSocket.receive(receivePacket);
//				String data = new String(receivePacket.getData(), receivePacket.getOffset() + 32, receivePacket.getLength() - 32);
				if (mListener != null)
				{
					mListener.onData(receivePacket.getData(), receivePacket.getLength(),receivePacket.getOffset());
				}
//				Log.d("as", "receive data :" + data);
			} catch (IOException e)
			{
				e.printStackTrace();
				if (mListener != null)
				{
					mListener.onSearchFinish(mSearchId);
				}
			}
		}
		return;
	}
}
