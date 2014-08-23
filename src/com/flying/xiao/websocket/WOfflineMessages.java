package com.flying.xiao.websocket;

import java.util.List;

public class WOfflineMessages extends WBase
{
	private List<OfflineMessage> offlineMessages ;

	public List<OfflineMessage> getOfflineMessages()
	{
		return offlineMessages;
	}

	public void setOfflineMessages(List<OfflineMessage> offlineMessages)
	{
		this.offlineMessages = offlineMessages;
	}
	
}
