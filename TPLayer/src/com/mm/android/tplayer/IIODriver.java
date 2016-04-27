/**
 * 
 */
package com.mm.android.tplayer;

import java.nio.channels.SelectionKey;

/**
 * @author 13098
 *
 */
public interface IIODriver {
	public int processSocket(SelectionKey selKey, INetStatisticsListener netstatisticListener);
}
