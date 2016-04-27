/**
 * 
 */
package com.mm.android.tplayer;

/**
 * @author 13098
 *
 */
/**
 * 事件对象
 * 首先必须用synchronized(Event)来进入临界区，然后在临界区中调用Event.setEvent();
 */
public class Event{
	public boolean bSet = false;
	
	public Event(boolean bSet){
		synchronized(this){
			this.bSet = bSet;
		}
	}
	
	public void setEvent(){
		synchronized(this){
			this.bSet = true;
		}
	}
	
	public void resetEvent(){
		synchronized(this){
			this.bSet = false;
		}
	}
}