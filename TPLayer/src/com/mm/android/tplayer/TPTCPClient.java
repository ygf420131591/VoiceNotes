/**
 * 
 */
package com.mm.android.tplayer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

//import android.util.Log;

class SendItem{
	public byte[] 			buffer;
	public int	  			nFlg;
	public SocketChannel 	socketChannel;
}



/**
 * @author 13098
 *
 */
public class TPTCPClient extends TPObject implements IIODriver{
	
	public final static int MAX_SEND_LIST_COUNT = 1000;//���͵����ݵ�Ԫ��������
	
	private ITPListener 	tpListener;					//ITPListener��ͨ���ýӿ���ʵ����������
	
	private SocketChannel 	socketChn;					//SocketChannel����
	
	public SocketChannel getSocketChn() {
		return socketChn;
	}
	public void setSocketChn(SocketChannel socketChn) {
		this.socketChn = socketChn;
	}

	private List<SendItem> 	lstSendItems;				//���ݵ�Ԫ���Ͷ���
	private byte[]			lstSendItemsLock;			//���ݵ�Ԫ���Ͷ��е���
	
	private byte[]			keepLiveBuf;				//�����������ݣ����ϲ�������
	private long			lastKeepLiveTime;			//���һ�η�����������ʱ��
	private long			keepLiveSpan;				//�������ļ������λ���룩
	

	private long			lastReceiveTime;			//���һ���յ����ݵ�ʱ��
														//(�ж��豸���߲���ͨ������ʱ��û�յ���������
														//�����ж϶���ʱ��û�յ����ݰ�)
	private boolean			isOnline;					//SocketChannl�Ƿ�������״̬
	
	private String			serverIP;					//������IP
	private int				serverPort;					//�������˿�
	
	private ByteBuffer 		bytebuf; 
	
	public TPTCPClient(){
		socketChn = null;
		tpListener = null;
		lstSendItems = new ArrayList<SendItem>();
		lstSendItemsLock = new byte[0];
		keepLiveBuf = null;
		lastKeepLiveTime = 0;
		isOnline = false;
		lastReceiveTime = System.currentTimeMillis();
		keepLiveSpan = 10;
		
		serverIP = null;
		serverPort = 0;
		
		bytebuf = ByteBuffer.allocate(1024*100);//100K
	}
	
	/**
	 * ���ϲ��Ӧ������������������÷�����ʱ���������������Ҽ���Ƿ��г�ʱ��û���յ����ݶ�����
	 * @return
	 */
	public static boolean isPrintLog = false;
	public int heartbeat(){
		int nRet = 0;
		if(this.isOnline){
			/**
			 * ��ʱ���ߴ���
			 */
			
			//debug start
			if(isPrintLog){
				long dif = (System.currentTimeMillis() - this.lastReceiveTime) / 1000;
				Log.d("HeartBeat", "Has no data for " + String.valueOf(dif) + " seconds, " + this.toString());
			}
			//debug end
			
			if( (super.m_isDetectDisconnect)
				&&
				(System.currentTimeMillis() - this.lastReceiveTime > TPObject.m_detectDisConnTime*1000)
				&&
				(this.lastReceiveTime != 0)
			){
				this.isOnline = false;
				
				//�ر�SocketChannel
				try {
					this.socketChn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(this.tpListener != null){
					if(isPrintLog){
						Log.d("HeartBeat", "Has no data from device too long, so connection dissconnect, " + this.toString());
					}
					this.tpListener.onDisconnect(this.socketChn.hashCode());
				}
				
				this.lastReceiveTime = System.currentTimeMillis();
			}
			/**
			 * ������������
			 */
//			else if( (System.currentTimeMillis() - this.lastKeepLiveTime > this.keepLiveSpan*1000) 
//					||
//					(System.currentTimeMillis() - this.lastKeepLiveTime < 0)
//					)
			else {
				//send heart beat data
				this.write(-1, this.keepLiveBuf);
				this.lastKeepLiveTime = System.currentTimeMillis();
				if(isPrintLog){
					Log.d("HeartBeat", "Start to send heartbeat content to device, " + this.toString());
				}
			}
		/**
		 * ��������µĶ�������, not implement yet
		 */
		}else if(super.m_isReconnectEnable){
			
		}
		return nRet;
	}
	/**
	 * 
	 * @param nFlg -1��ʱ���ʾ��Ҫ�����ݣ������Ƿ񳬹�MAX_SEND_LIST_COUNT����Ҫ���
	 * 				�������У�һ���ʾ������
	 * @param buf
	 * @return
	 */
	public  int write(int nFlg, byte[] buf){
		int nRet = 0;
		synchronized(lstSendItemsLock){
			int nCurItemCount = this.lstSendItems.size();
			if(nCurItemCount < MAX_SEND_LIST_COUNT || nFlg == -1){
				SendItem sendItem = new SendItem();
				sendItem.buffer = buf;
				sendItem.nFlg = nFlg;
				sendItem.socketChannel = this.socketChn;
				this.lstSendItems.add(sendItem);
				nRet = 0;
			}
			else{
				nRet = -1;
			}
		}
		return nRet;
	}
	
	
	/**
	 * ����ITPListener�����ݻ���ߣ�������ͨ��ITPListener�����洫��
	 * @param listener
	 */
	public void setListener(ITPListener listener){
		this.tpListener = listener;
	}

	/**
	 * 
	 * @param strIP			IP of server to connect
	 * @param nPort			Port of server to connect
	 * @param nConTimeout	Max time to connect to server[not use now]
	 * @return
	 * -1 : ϵͳ����
	 * -2 : ��ַת������
	 * -3 : ���ӳ�ʱ
	 */
	public int connect(String strIP, int nPort, int nConTimeout){
		//��¼������Ϣ
		this.serverIP = strIP;
		this.serverPort = nPort;
		//���׽���
		try {
			socketChn = SocketChannel.open();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;//���׽���ʧ��
		}
		//����InetSocketAddress����
		InetSocketAddress isa = null;
		try
		{
		  isa = new InetSocketAddress(strIP, nPort);
		}catch(Exception e)
		{
			return -2;
		}
		//���ӷ�����������SOCKETΪ������ģʽ
		try {
			Socket socket = socketChn.socket();
			socket.connect(isa, nConTimeout);	//���ӷ�����
			socketChn.configureBlocking(false);//����Ϊ������
		} catch (IOException e) {
			e.printStackTrace();
			//�ر�SocketChannel
			try {
				socketChn.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return -3;
		}
		
		//��socket��ӵ������߳���
		boolean bRet = super.addSocketToThread(socketChn);
		if(!bRet){
			return -1;
		}
		
		//���ñ�־
		this.isOnline = true;
		return 0;
	}
	
	/**
	 * �ر�����
	 * @return
	 */
	public int close(){
		return super.deleteSocketFromThread(socketChn) ? 0 : -1;
	}

	
	/**
	 * �������Ҫ��������ݷ��� 0
	 * ���û����Ҫ��������ݷ��� 1����ʾĿǰ�ǱȽ��ǿ��еġ�����TPObject�е��߳̿���sleep
	 * ������쳣������-1
	 */
	public int processSocket(SelectionKey selKey, INetStatisticsListener netstatisticListener) {
		int nRet = 0;
		//��������
		
		//long t1 = System.currentTimeMillis();
		
		if(selKey.isValid() && selKey.isReadable()){
				 //��ȡ�����ݽ���ITPListener��onData����onData����ʣ�µ�û�д��������.
				 //��Ҫ��ʣ������ݿ�����buffer��ǰ�棬
				 int nLeftData = 0;//���ʣ��ĳ���
				 int nDataLen = 0;//Socket��ȡ�ĳ���
			     try {
			    	 nDataLen = socketChn.read(bytebuf);
			    	 if(nDataLen == 0){
			    		 this.tpListener.onReconnect(0);
			    	 }
			    	 
				} catch (Exception e) {//����������⣬�����߶Ͽ��ȵ��¶���
					Log.d("TPLAYER", "Socket read NG");
					nDataLen = 0;
				}
				//������ȡ������
				if(nDataLen > 0){
					//Log.d("TPLAYER", "Socket read OK " + String.valueOf(nDataLen));
					this.lastReceiveTime = System.currentTimeMillis();//�������һ���յ����ݵ�ʱ��
					if(this.tpListener != null){
						try{
							nLeftData = this.tpListener.onData(socketChn.hashCode(), bytebuf.array(), bytebuf.position());
							if (netstatisticListener != null)
								netstatisticListener.onRecieveDataLength(nDataLen);
						}catch(Exception e){
							Log.d("TPLAYER", "ITPListener.onData Exception " + String.valueOf(nDataLen));
							e.printStackTrace();
							return 0;
						}
					}
					int nTotalLen = bytebuf.position();
					byte[] byteArray = bytebuf.array();
					
					bytebuf.clear();
					if(nLeftData != 0){//�����ʣ�������û�д�����ʣ������ݿ�����bytebuf��ǰ��
						bytebuf.put(byteArray, nTotalLen - nLeftData, nLeftData);
					}
					nRet = 0;
				}
				//��ȡ�����쳣�����ж��߻ص�
				else
				{
					
					try {
						this.socketChn.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(this.tpListener != null){
						Log.d("socket",this.socketChn.hashCode() +  "  socket read : " + nDataLen);
						this.tpListener.onDisconnect(this.socketChn.hashCode());
					}
					nRet = -1;
					bytebuf.clear();
				}
		   }else{
			   nRet = 1;
		   }
			//long t2 = System.currentTimeMillis();
			
		   //��������
		   if(selKey.isValid()&& selKey.isWritable()){
			  synchronized(this.lstSendItemsLock){
				  int nListSize = this.lstSendItems.size();
				  if(nListSize > 0){
					  SendItem si = this.lstSendItems.get(0);
					  int nTotalLen = si.buffer.length;
					  int nActuallSendLen = 0;
					  while(nActuallSendLen < nTotalLen){
						  ByteBuffer bf = ByteBuffer.wrap(si.buffer, nActuallSendLen, nTotalLen - nActuallSendLen); 
						  try {
							int nCurSend = this.socketChn.write(bf);
							//Log.d("send", "send data");
							if (netstatisticListener != null)
								netstatisticListener.onSendDataLength(nCurSend);
							
							nActuallSendLen += nCurSend;
						} catch (IOException e) {
							e.printStackTrace();
							break;//?
						}
					  }//while
					  this.lstSendItems.remove(0);
					  if(this.tpListener != null && si.nFlg != -1){
						  this.tpListener.onSendDataAck(this.socketChn.hashCode(), si.nFlg);
					  }
				  }else{
					  nRet = nRet == 1 ? 1 : 0;
				  }
			  }//synchronized
		   }else{
			   nRet = nRet == 1 ? 1 : 0;
		   }
		   
		   //long t3 = System.currentTimeMillis();
		  // Log.d("time", "read cost time: " + String.valueOf(t2 - t1));
		  // Log.d("time", "write cost time: " + String.valueOf(t3 - t2));
		   
		return nRet;
	}
	
	public boolean isConnected(){
		if(this.socketChn == null){
			return false;
		}
		return this.socketChn.isConnected();
	}

	/**
	 * 
	 * @param keepLiveBuf
	 */
	public synchronized void setKeepLiveBuf(byte[] keepLiveBuf) {
		this.keepLiveBuf = keepLiveBuf;
	}

	/**
	 * @param keepLiveSpan the keepLiveSpan to set
	 */
	public synchronized void setKeepLiveSpan(long keepLiveSpan) {
		this.keepLiveSpan = keepLiveSpan;
	}
	
	
	public String getServerIP() {
		return serverIP;
	}

	public int getServerPort() {
		return serverPort;
	}


	
	
	
}
