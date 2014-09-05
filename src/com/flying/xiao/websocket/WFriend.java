package com.flying.xiao.websocket;

public class WFriend extends WBase
{
	private long friendUserId;
	private String ip;

	public long getFriendUserId()
	{
		return friendUserId;
	}

	public void setFriendUserId(long friendUserId)
	{
		this.friendUserId = friendUserId;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}
}
