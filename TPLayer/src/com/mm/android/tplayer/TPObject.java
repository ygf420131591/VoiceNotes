/**
 * 
 */
package com.mm.android.tplayer;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.util.Log;


/**
 * @author 13098
 *
 */

/**
 * 线程信息
 *  @author 13098
 */
class ThreadInfo{
	public Selector				m_selector;			//线程需要处理的Selector
	public Object				m_selectorLock;		//线程需要处理的Selector的锁
	public Thread 				m_thread;			//线程对象
	public GlobalTplayerData	m_gTplayerData;		//全局数据中心
	
	public ThreadInfo(GlobalTplayerData g_data){
		m_selector = null;
		m_thread = null;
		m_gTplayerData = g_data;
	}
}

/**
 * 全局信息
 * @author 13098
 *
 */
class GlobalTplayerData{
	public Event  			m_ExitEvent;		//所有线程需要等待的退出事件
	public int 	   			m_nThreadNumber;	//线程的个数
	public List<ThreadInfo> m_lstAllThreadInfo; //所有的线程信息
	public INetStatisticsListener m_netstatisticsListener; // 网络数据统计接口
	public GlobalTplayerData(){
		m_ExitEvent = new Event(false);
		m_nThreadNumber = 0;
		m_netstatisticsListener = null;
		m_lstAllThreadInfo = new LinkedList<ThreadInfo>();
	}
	
	public void reset(){
		m_ExitEvent.resetEvent();
		m_nThreadNumber = 0;
		
		m_lstAllThreadInfo.clear();
		
	}
}


public class TPObject{
	
	private static GlobalTplayerData g_dataCenter = new GlobalTplayerData();//全局数据
	public static List<Selector> m_lstSelector = null;//多个Selector,每个分别由一个线程执行
	public static Object	  m_lstSelectorLock = null;//lstSelector的锁
	public final static	int	 m_detectDisConnTime = 90; //unit is Second
	
	 private static boolean m_bStart = false;
	/**
	 * 
	 */
	protected		boolean		m_isDetectDisconnect = false;//是否进行断线检测
	protected		boolean		m_isReconnectEnable  = false;//是否断线重连
	
	
	public TPObject(){
		
	}
	
	public final static void setNetStatisticsListener( INetStatisticsListener listener )
	{
		g_dataCenter.m_netstatisticsListener = listener;
	}
	
	/**
	 * @des   初始化
	 * @param nThreadNum 线程数
	 * @return
	 */
	public final static int startup(int nThreadNum){
		Log.d("startup", "isStart: " + m_bStart);
		if(m_bStart){
			return 0;  //已初始化直接返回成功（用于在一个工程中可以同时使用平台与直连的SDK）
			//return -1;
		}
		
		m_lstSelectorLock = new byte[0];
		
		//创建SELECT
		if(m_lstSelector == null){
			m_lstSelector = new LinkedList<Selector>();
		}
		for(int i = 0; i < nThreadNum; i++){
			Selector sel = null;
			try {
				java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
				sel = Selector.open();
				m_lstSelector.add(sel);
			} catch (IOException e) {
				for(Selector selTmp:m_lstSelector){
					try {
						selTmp.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				return -1;
			}
		}
		
		//创建处理线程
		g_dataCenter.m_nThreadNumber = nThreadNum;
		for(int i = 0; i < nThreadNum; i++){
			ThreadInfo ti = new ThreadInfo(g_dataCenter);
			g_dataCenter.m_lstAllThreadInfo.add(ti);
			
			ProcessThread pt = new ProcessThread();
			pt.setThreadInfo(ti);
			pt.setThreadName("DHNetWorkThread_" + String.valueOf(i));
			Thread t = new Thread(pt);
			ti.m_thread = t;
			ti.m_selector = TPObject.m_lstSelector.get(i);
			ti.m_selectorLock = new byte[0];
			t.start();
		}
		
		m_bStart = true;
		return 0;
	}
	
	public final static int cleanup(){
		if(!m_bStart){
			return 0;
		}
		
		//设置事件，这样工作线程将退出
		synchronized(g_dataCenter.m_ExitEvent){
			g_dataCenter.m_ExitEvent.setEvent();
			g_dataCenter.m_ExitEvent.notifyAll();
		}
		
		//等待所有的工作线程退出
		for(int i = 0; i < g_dataCenter.m_nThreadNumber; i++){
			try {
				g_dataCenter.m_lstAllThreadInfo.get(0).m_thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//清理全局数据
		g_dataCenter.reset();
		
		//清除Selector数组(未清除会导致第二次初始化的时候出现数组越位的问题)
		m_lstSelector.clear();
		m_lstSelector = null;
		
		m_lstSelectorLock = null;
		//强制垃圾回收
		System.gc();
		
		m_bStart = false;
		return 0;
	}
	
	
	/**
	 * @describe 将一个SocketChannel对象添加到当前需要处理的SocketChannel对象最少的线程中去
	 * @param socketChn
	 * @return
	 */
	public boolean addSocketToThread(SocketChannel socketChn){
		//
		//获取所有Selector，注册socket最少的那个Selector,将新的socket注册到该select中
		//
		Selector selectorTmp = null;
		int nMinKeyCountThreadNo = 0;
		synchronized(TPObject.m_lstSelectorLock){
				if(TPObject.m_lstSelector.size() == 0){
					return false;
				}
				int nMinKeyCount = 0;
				synchronized(TPObject.g_dataCenter.m_lstAllThreadInfo.get(0).m_selectorLock){
					nMinKeyCount = TPObject.m_lstSelector.get(0).keys().size();
				}
				int nSize = TPObject.m_lstSelector.size();
				for(int i = 0; i < nSize; i++){
					synchronized(TPObject.g_dataCenter.m_lstAllThreadInfo.get(i).m_selectorLock){
						if(TPObject.m_lstSelector.get(i).keys().size() < nMinKeyCount){
							nMinKeyCount = TPObject.m_lstSelector.get(i).keys().size();
							nMinKeyCountThreadNo = i;
						}
					}
				}
				selectorTmp = TPObject.m_lstSelector.get(nMinKeyCountThreadNo);
			}
		
		//注册感兴趣的事件到key最少的Selector中
		synchronized(TPObject.g_dataCenter.m_lstAllThreadInfo.get(nMinKeyCountThreadNo).m_selectorLock){
			try {
				socketChn.register(selectorTmp, 
									SelectionKey.OP_READ | SelectionKey.OP_WRITE,
									this);
			} catch (ClosedChannelException e) {
				e.printStackTrace();
				try {
					socketChn.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return false;
			}
		}
			return true;
	}
	
	/**
	 * socket关闭的时候不需要删除，自动会从Selector的keys中删除
	 * @param socketChn
	 * @return
	 */
	public boolean deleteSocketFromThread(SocketChannel socketChn){
		int socketChnThreadNo = 0;
		boolean bFind = false;
		synchronized(TPObject.m_lstSelectorLock){
			if(TPObject.m_lstSelector.size() == 0){
				return false;
			}
			
			for(Selector select : TPObject.m_lstSelector){
				for(int i = 0; i < TPObject.m_lstSelector.size(); i++)
					synchronized(TPObject.g_dataCenter.m_lstAllThreadInfo.get(i).m_selectorLock){
						Set<SelectionKey> keys  = select.keys();
						for(SelectionKey key : keys){
							TPTCPClient tcpClient = (TPTCPClient)key.attachment();
							if(tcpClient.getSocketChn() == socketChn){
								bFind = true;
								break;
							}
						}
					}
				if(bFind){
					break;
				}
				socketChnThreadNo++;
			}
		}
		
		if(!bFind){
			return false;
		}
		synchronized(TPObject.g_dataCenter.m_lstAllThreadInfo.get(socketChnThreadNo).m_selectorLock){
			if(socketChn != null){
				try {
					socketChn.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
	
	public void setDetectDisconnect(boolean isDetectDisconnect) {
		this.m_isDetectDisconnect = isDetectDisconnect;
	}

	public void setReconnectEnable(boolean isReconnectEnable) {
		this.m_isReconnectEnable = isReconnectEnable;
	}

	
}

class ProcessThread implements Runnable{
	public ThreadInfo threadInfo = null; //线程信息
	public String threadName = "";		 //线程名字，只是用来测试
	
	public void run() {
		boolean bSleep = false;//如果没有可读的数据，并且没有可写的数据，那么可以sleep一定的时间，防止CPU过高
		while(true){
			//Thread.yield();
			
			//如果暂时没有需要处理的数据，那么休息一会
			if(bSleep){
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//等待退出事件
			synchronized(this.threadInfo.m_gTplayerData.m_ExitEvent){
				try {
					this.threadInfo.m_gTplayerData.m_ExitEvent.wait(1);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				if(this.threadInfo.m_gTplayerData.m_ExitEvent.bSet){
					return;
				}
			}
			 long t1 = System.currentTimeMillis();
			synchronized(TPObject.m_lstSelectorLock){
			//select
			synchronized(this.threadInfo.m_selectorLock)
			{
				long t2 = System.currentTimeMillis();
				if(t2 - t1 > 20){
					Log.d("tplayer", "sync cost: " + (t2 - t1));
				}
				
				try {
					if(this.threadInfo.m_selector.select(10)==0){
						continue;
					   }
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				//取得迭代器selectedKey()中包含了每个准备好某一操作信道的selectionKey
				bSleep = true;
				Iterator<SelectionKey> it = this.threadInfo.m_selector.selectedKeys().iterator();
				while(it.hasNext()){
				   SelectionKey key = (SelectionKey) it.next();
				   IIODriver driver = (IIODriver)key.attachment();
				   //处理Socket对象的读写
				   try{
					   //xyg
					   long sTime = System.currentTimeMillis();
					   int nRet = driver.processSocket(key, this.threadInfo.m_gTplayerData.m_netstatisticsListener);
					   // 有一个socket不能休息，就不休息
					   if(nRet != 1){
						   bSleep = false;
					   }
					   
					   long eTime = System.currentTimeMillis();
					   long nSpan = eTime - sTime;
					   if(nSpan > 30){
						   Log.d("tplayer", "processSocket cost time: " + nSpan);
					   }
				   }
				   catch(Exception e){
					   e.printStackTrace();
					   it.remove();
					   this.threadInfo.m_selector.selectedKeys().clear();
					   continue;
				    }
				   it.remove();
				}//while
				this.threadInfo.m_selector.selectedKeys().clear();
			}//synchronized
			}//synchronized
			
			
			
			
			
		}//while
	}//run
	
	
	public ThreadInfo getThreadInfo() {
		return threadInfo;
	}

	public void setThreadInfo(ThreadInfo threadInfo) {
		this.threadInfo = threadInfo;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public ProcessThread(){
	}
}
