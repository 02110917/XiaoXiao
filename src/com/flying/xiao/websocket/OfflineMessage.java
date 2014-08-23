package com.flying.xiao.websocket;

import java.sql.Timestamp;

public class OfflineMessage
{
	private long id ;
	private long messageId ;
	private long userSendId ;
	private long userReceiveId ;
	private String message ;
	private Timestamp sendTime ;
	
	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}
	public long getMessageId()
	{
		return messageId;
	}
	public void setMessageId(long messageId)
	{
		this.messageId = messageId;
	}
	public long getUserSendId()
	{
		return userSendId;
	}
	public void setUserSendId(long userSendId)
	{
		this.userSendId = userSendId;
	}
	public long getUserReceiveId()
	{
		return userReceiveId;
	}
	public void setUserReceiveId(long userReceiveId)
	{
		this.userReceiveId = userReceiveId;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	public Timestamp getSendTime()
	{
		return sendTime;
	}
	public void setSendTime(Timestamp sendTime)
	{
		this.sendTime = sendTime;
	}
	
}
