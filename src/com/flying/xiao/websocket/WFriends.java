package com.flying.xiao.websocket;

import java.util.List;

public class WFriends extends WBase
{
	private List<WFriend> friends;

	public List<WFriend> getFriends()
	{
		return friends;
	}

	public void setFriends(List<WFriend> friends)
	{
		this.friends = friends;
	}
}
