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
	
	public final static int MAX_SEND_LIST_COUNT = 1000;//发送的数据单元的最大个数
	
	private ITPListener 	tpListener;					//ITPListener，通过该接口来实现数据外送
	
	private SocketChannel 	socketChn;					//SocketChannel对象
	
	public SocketChannel getSocketChn() {
		return socketChn;
	}
	public void setSocketChn(SocketChannel socketChn) {
		this.socketChn = socketChn;
	}

	private List<SendItem> 	lstSendItems;				//数据单元发送队列
	private byte[]			lstSendItemsLock;			//数据单元发送队列的锁
	
	private byte[]			keepLiveBuf;				//心跳包的内容，由上层来设置
	private long			lastKeepLiveTime;			//最后一次发送心跳包的时间
	private long			keepLiveSpan;				//心跳包的间隔（单位：秒）
	

	private long			lastReceiveTime;			//最后一次收到数据的时间
														//(判断设备掉线不是通过多少时间没收到心跳包，
														//而是判断多少时间没收到数据包)
	private boolean			isOnline;					//SocketChannl是否处于连接状态
	
	private String			serverIP;					//服务器IP
	private int				serverPort;					//服务器端口
	
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
	 * 由上层的应用来驱动这个方法。该方法定时发送心跳包，并且检查是否有长时间没有收到数据而断线
	 * @return
	 */
	public static boolean isPrintLog = false;
	public int heartbeat(){
		int nRet = 0;
		if(this.isOnline){
			/**
			 * 超时断线处理
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
				
				//关闭SocketChannel
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
			 * 发送心跳处理
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
		 * 断线情况下的断线重连, not implement yet
		 */
		}else if(super.m_isReconnectEnable){
			
		}
		return nRet;
	}
	/**
	 * 
	 * @param nFlg -1的时候表示重要的数据，不管是否超过MAX_SEND_LIST_COUNT，都要添加
	 * 				到队列中，一般表示心跳包
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
	 * 设置ITPListener，数据或断线，重连等通过ITPListener向外面传送
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
	 * -1 : 系统错误
	 * -2 : 地址转换错误
	 * -3 : 连接超时
	 */
	public int connect(String strIP, int nPort, int nConTimeout){
		//记录连接信息
		this.serverIP = strIP;
		this.serverPort = nPort;
		//打开套接字
		try {
			socketChn = SocketChannel.open();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;//打开套接字失败
		}
		//构造InetSocketAddress对象
		InetSocketAddress isa = null;
		try
		{
		  isa = new InetSocketAddress(strIP, nPort);
		}catch(Exception e)
		{
			return -2;
		}
		//连接服务器并设置SOCKET为非阻塞模式
		try {
			Socket socket = socketChn.socket();
			socket.connect(isa, nConTimeout);	//连接服务器
			socketChn.configureBlocking(false);//设置为非阻塞
		} catch (IOException e) {
			e.printStackTrace();
			//关闭SocketChannel
			try {
				socketChn.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return -3;
		}
		
		//将socket添加到处理线程中
		boolean bRet = super.addSocketToThread(socketChn);
		if(!bRet){
			return -1;
		}
		
		//设置标志
		this.isOnline = true;
		return 0;
	}
	
	/**
	 * 关闭连接
	 * @return
	 */
	public int close(){
		return super.deleteSocketFromThread(socketChn) ? 0 : -1;
	}

	
	/**
	 * 如果有需要处理的数据返回 0
	 * 如果没有需要处理的数据返回 1，表示目前是比较是空闲的。这样TPObject中的线程可以sleep
	 * 如果有异常，返回-1
	 */
	public int processSocket(SelectionKey selKey, INetStatisticsListener netstatisticListener) {
		int nRet = 0;
		//接受数据
		
		//long t1 = System.currentTimeMillis();
		
		if(selKey.isValid() && selKey.isReadable()){
				 //读取的数据交给ITPListener的onData处理，onData返回剩下的没有处理的数据.
				 //需要把剩余的数据拷贝到buffer的前面，
				 int nLeftData = 0;//这次剩余的长度
				 int nDataLen = 0;//Socket读取的长度
			     try {
			    	 nDataLen = socketChn.read(bytebuf);
			    	 if(nDataLen == 0){
			    		 this.tpListener.onReconnect(0);
			    	 }
			    	 
				} catch (Exception e) {//网络出现问题，如网线断开等导致断线
					Log.d("TPLAYER", "Socket read NG");
					nDataLen = 0;
				}
				//正常读取了数据
				if(nDataLen > 0){
					//Log.d("TPLAYER", "Socket read OK " + String.valueOf(nDataLen));
					this.lastReceiveTime = System.currentTimeMillis();//更新最后一次收到数据的时间
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
					if(nLeftData != 0){//如果有剩余的数据没有处理，把剩余的数据拷贝到bytebuf的前面
						bytebuf.put(byteArray, nTotalLen - nLeftData, nLeftData);
					}
					nRet = 0;
				}
				//读取数据异常，进行断线回调
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
			
		   //发送数据
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
