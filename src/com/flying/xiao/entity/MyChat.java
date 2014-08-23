package com.flying.xiao.entity;

import java.util.Date;

/**
 * 我的聊天实体类
 * @author zhangmin
 *
 */
public class MyChat
{
	private long friendUserId ;
	private String friendName ;
	private String imageUrl ;
	private Date time ;
	private String lastMsg ;
	private int newMsgCount ; //未读消息数
	
	public String getFriendName()
	{
		return friendName;
	}
	public void setFriendName(String friendName)
	{
		this.friendName = friendName;
	}
	public long getFriendUserId()
	{
		return friendUserId;
	}
	public void setFriendUserId(long friendUserId)
	{
		this.friendUserId = friendUserId;
	}
	public String getImageUrl()
	{
		return imageUrl;
	}
	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}
	public Date getTime()
	{
		return time;
	}
	public void setTime(Date time)
	{
		this.time = time;
	}
	public String getLastMsg()
	{
		return lastMsg;
	}
	public void setLastMsg(String lastMsg)
	{
		this.lastMsg = lastMsg;
	}
	public int getNewMsgCount()
	{
		return newMsgCount;
	}
	public void setNewMsgCount(int newMsgCount)
	{
		this.newMsgCount = newMsgCount;
	}
	
}
