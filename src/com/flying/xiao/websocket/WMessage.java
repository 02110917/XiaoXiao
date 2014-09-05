package com.flying.xiao.websocket;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class WMessage extends WBase
{
	private long userSendId;
	private long userReceiveId;
	private String msg;
	private Date sendTime;
	private long messageId;

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

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public Date getSendTime()
	{
		return sendTime;
	}

	public void setSendTime(Date sendTime)
	{
		this.sendTime = sendTime;
	}

	public static WMessage jsonToBase(String json) throws JsonSyntaxException
	{
		Gson gson = new Gson();
		return gson.fromJson(json, WMessage.class);
	}

}
