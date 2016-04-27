/*配置改变通知*/

package com.voicenotes.manages.notifies;

public class ConfigChangeNotify extends BaseNotify{

	private String eventtype;
	private String configName; // "audio" or "video" or "meeting"
	private String subType; //"sip" or "h323"
}
