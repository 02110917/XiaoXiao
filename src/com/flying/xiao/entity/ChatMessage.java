package com.flying.xiao.entity;

import java.util.Date;

import android.content.Context;

import com.flying.xiao.app.AppContext;
import com.flying.xiao.db.DBHelper;
import com.flying.xiao.websocket.WMessage;

public class ChatMessage
{
	private long messageId;
	private long userSendId ;
	private long userReceiveId;
	private String userImageHeadUrl;
	private boolean isTo ; //是不是发送的  to true  from false
	private String message ;
	private Date time ; //发送 |接收时间
	private int type ; //消息类型  纯文本  图片  ...
	private boolean isSee =false;  //是否查看
	private boolean isSendTo=false ; //是否发送到达
	private boolean isSendError=false ; //是否发送失败
	
	public static ChatMessage getInstance(WMessage wm,boolean isTo,AppContext context){
		
		ChatMessage cm=new ChatMessage();
		cm.setMessage(wm.getMsg());
		cm.setSee(false);
		cm.setTime(wm.getSendTime());
		cm.setUserSendId(wm.getUserSendId());
		cm.setUserReceiveId(wm.getUserReceiveId());
		cm.setMessageId(wm.getMessageId());
		if(!isTo)
			cm.setUserImageHeadUrl(DBHelper.getDbHelper(context).getUserInfoById(cm.getUserSendId()).getUserHeadImageUrl());
		else
			cm.setUserImageHeadUrl(context.getUserInfo().getUserHeadImageUrl());
		cm.setTo(isTo);
		return cm ;
	}
	
	public boolean isSendTo()
	{
		return isSendTo;
	}

	public void setSendTo(boolean isSendTo)
	{
		this.isSendTo = isSendTo;
	}

	public boolean isSendError()
	{
		return isSendError;
	}

	public void setSendError(boolean isSendError)
	{
		this.isSendError = isSendError;
	}

	public long getMessageId()
	{
		return messageId;
	}

	public void setMessageId(long messageId)
	{
		this.messageId = messageId;
	}

	public String getUserImageHeadUrl()
	{
		return userImageHeadUrl;
	}
	public void setUserImageHeadUrl(String userImageHeadUrl)
	{
		this.userImageHeadUrl = userImageHeadUrl;
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
	public boolean isTo()
	{
		return isTo;
	}
	public void setTo(boolean isTo)
	{
		this.isTo = isTo;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	
	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	public int getType()
	{
		return type;
	}
	public void setType(int type)
	{
		this.type = type;
	}
	public boolean isSee()
	{
		return isSee;
	}
	public void setSee(boolean isSee)
	{
		this.isSee = isSee;
	}
	
	
}
