package com.flying.xiao.entity;

import java.util.Date;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

import com.flying.xiao.app.AppContext;
import com.flying.xiao.db.DBHelper;

public class ChatMessage
{
	private String from; // 来源
	private String to; // 接受者 userName 形式
	private String userImageHeadUrl;
	private boolean isTo; // 是不是发送的 to true from false
	private String body;
	private Date time; // 发送 |接收时间
	private int type; // 消息类型 纯文本 图片 ...
	private boolean isSee = false; // 是否查看
	private boolean isSendTo = false; // 是否发送到达
	private boolean isSendError = false; // 是否发送失败

	public static ChatMessage getInstance(Message m, boolean isTo, AppContext context)
	{

		ChatMessage cm = new ChatMessage();
		cm.setBody(m.getBody());
		cm.setSee(false);
		cm.setTime(new Date(System.currentTimeMillis()));
		cm.setFrom(StringUtils.parseName(m.getFrom().replace("$", "@")));
		cm.setTo(StringUtils.parseName(m.getTo().replace("$", "@")));
		if (!isTo)
		{
			XUserInfo user = DBHelper.getDbHelper(context).getUserInfoByUserName(cm.getFrom());
			if (user != null)
				cm.setUserImageHeadUrl(user.getUserHeadImageUrl());
		} else
			cm.setUserImageHeadUrl(context.getUserInfo().getUserHeadImageUrl());
		cm.setTo(isTo);
		return cm;
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

	public String getUserImageHeadUrl()
	{
		return userImageHeadUrl;
	}

	public void setUserImageHeadUrl(String userImageHeadUrl)
	{
		this.userImageHeadUrl = userImageHeadUrl;
	}

	public boolean isTo()
	{
		return isTo;
	}

	public void setTo(boolean isTo)
	{
		this.isTo = isTo;
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

	public String getFrom()
	{
		return from;
	}

	public void setFrom(String from)
	{
		this.from = from;
	}

	public String getTo()
	{
		return to;
	}

	public void setTo(String to)
	{
		this.to = to;
	}

	public String getBody()
	{
		return body;
	}

	public void setBody(String body)
	{
		this.body = body;
	}

}
