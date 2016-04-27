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
 * �߳���Ϣ
 *  @author 13098
 */
class ThreadInfo{
	public Selector				m_selector;			//�߳���Ҫ������Selector
	public Object				m_selectorLock;		//�߳���Ҫ������Selector����
	public Thread 				m_thread;			//�̶߳���
	public GlobalTplayerData	m_gTplayerData;		//ȫ����������
	
	public ThreadInfo(GlobalTplayerData g_data){
		m_selector = null;
		m_thread = null;
		m_gTplayerData = g_data;
	}
}

/**
 * ȫ����Ϣ
 * @author 13098
 *
 */
class GlobalTplayerData{
	public Event  			m_ExitEvent;		//�����߳���Ҫ�ȴ����˳��¼�
	public int 	   			m_nThreadNumber;	//�̵߳ĸ���
	public List<ThreadInfo> m_lstAllThreadInfo; //���е��߳���Ϣ
	public INetStatisticsListener m_netstatisticsListener; // ��������ͳ�ƽӿ�
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
	
	private static GlobalTplayerData g_dataCenter = new GlobalTplayerData();//ȫ������
	public static List<Selector> m_lstSelector = null;//���Selector,ÿ���ֱ���һ���߳�ִ��
	public static Object	  m_lstSelectorLock = null;//lstSelector����
	public final static	int	 m_detectDisConnTime = 90; //unit is Second
	
	 private static boolean m_bStart = false;
	/**
	 * 
	 */
	protected		boolean		m_isDetectDisconnect = false;//�Ƿ���ж��߼��
	protected		boolean		m_isReconnectEnable  = false;//�Ƿ��������
	
	
	public TPObject(){
		
	}
	
	public final static void setNetStatisticsListener( INetStatisticsListener listener )
	{
		g_dataCenter.m_netstatisticsListener = listener;
	}
	
	/**
	 * @des   ��ʼ��
	 * @param nThreadNum �߳���
	 * @return
	 */
	public final static int startup(int nThreadNum){
		Log.d("startup", "isStart: " + m_bStart);
		if(m_bStart){
			return 0;  //�ѳ�ʼ��ֱ�ӷ��سɹ���������һ�������п���ͬʱʹ��ƽ̨��ֱ����SDK��
			//return -1;
		}
		
		m_lstSelectorLock = new byte[0];
		
		//����SELECT
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
		
		//���������߳�
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
		
		//�����¼������������߳̽��˳�
		synchronized(g_dataCenter.m_ExitEvent){
			g_dataCenter.m_ExitEvent.setEvent();
			g_dataCenter.m_ExitEvent.notifyAll();
		}
		
		//�ȴ����еĹ����߳��˳�
		for(int i = 0; i < g_dataCenter.m_nThreadNumber; i++){
			try {
				g_dataCenter.m_lstAllThreadInfo.get(0).m_thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//����ȫ������
		g_dataCenter.reset();
		
		//���Selector����(δ����ᵼ�µڶ��γ�ʼ����ʱ���������Խλ������)
		m_lstSelector.clear();
		m_lstSelector = null;
		
		m_lstSelectorLock = null;
		//ǿ����������
		System.gc();
		
		m_bStart = false;
		return 0;
	}
	
	
	/**
	 * @describe ��һ��SocketChannel�������ӵ���ǰ��Ҫ������SocketChannel�������ٵ��߳���ȥ
	 * @param socketChn
	 * @return
	 */
	public boolean addSocketToThread(SocketChannel socketChn){
		//
		//��ȡ����Selector��ע��socket���ٵ��Ǹ�Selector,���µ�socketע�ᵽ��select��
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
		
		//ע�����Ȥ���¼���key���ٵ�Selector��
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
	 * socket�رյ�ʱ����Ҫɾ�����Զ����Selector��keys��ɾ��
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
	public ThreadInfo threadInfo = null; //�߳���Ϣ
	public String threadName = "";		 //�߳����֣�ֻ����������
	
	public void run() {
		boolean bSleep = false;//���û�пɶ������ݣ�����û�п�д�����ݣ���ô����sleepһ����ʱ�䣬��ֹCPU����
		while(true){
			//Thread.yield();
			
			//�����ʱû����Ҫ���������ݣ���ô��Ϣһ��
			if(bSleep){
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//�ȴ��˳��¼�
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
				//ȡ�õ�����selectedKey()�а�����ÿ��׼����ĳһ�����ŵ���selectionKey
				bSleep = true;
				Iterator<SelectionKey> it = this.threadInfo.m_selector.selectedKeys().iterator();
				while(it.hasNext()){
				   SelectionKey key = (SelectionKey) it.next();
				   IIODriver driver = (IIODriver)key.attachment();
				   //����Socket����Ķ�д
				   try{
					   //xyg
					   long sTime = System.currentTimeMillis();
					   int nRet = driver.processSocket(key, this.threadInfo.m_gTplayerData.m_netstatisticsListener);
					   // ��һ��socket������Ϣ���Ͳ���Ϣ
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