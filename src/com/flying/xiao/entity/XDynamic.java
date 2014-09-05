package com.flying.xiao.entity;

import java.sql.Timestamp;

/**
 * 我的动态
 * 
 * @author zhangmin
 *
 */
public class XDynamic extends Base
{
	// type 赞了我 回复我content 给我留言 回复我给别人的评论
	private int type;
	private XUserInfo userInfo;
	private Timestamp time;
	private Boolean isSee;
	private String msg;
	// private long contentId;
	// private String contentTitle ;
	// private long contentUserId;
	// private String contentUserName ;

	private XContent content;

	private XMessage message;

	private long commentId;

	public long getCommentId()
	{
		return commentId;
	}

	public void setCommentId(long commentId)
	{
		this.commentId = commentId;
	}

	public XMessage getMessage()
	{
		return message;
	}

	public void setMessage(XMessage message)
	{
		this.message = message;
	}

	public XContent getContent()
	{
		return content;
	}

	public void setContent(XContent content)
	{
		this.content = content;
	}

	// public long getContentId()
	// {
	// return contentId;
	// }
	// public void setContentId(long contentId)
	// {
	// this.contentId = contentId;
	// }
	// public String getContentTitle()
	// {
	// return contentTitle;
	// }
	// public void setContentTitle(String contentTitle)
	// {
	// this.contentTitle = contentTitle;
	// }
	// public long getContentUserId()
	// {
	// return contentUserId;
	// }
	// public void setContentUserId(long contentUserId)
	// {
	// this.contentUserId = contentUserId;
	// }
	// public String getContentUserName()
	// {
	// return contentUserName;
	// }
	// public void setContentUserName(String contentUserName)
	// {
	// this.contentUserName = contentUserName;
	// }
	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public XUserInfo getUserInfo()
	{
		return userInfo;
	}

	public void setUserInfo(XUserInfo userInfo)
	{
		this.userInfo = userInfo;
	}

	public Timestamp getTime()
	{
		return time;
	}

	public void setTime(Timestamp time)
	{
		this.time = time;
	}

	public Boolean getIsSee()
	{
		return isSee;
	}

	public void setIsSee(Boolean isSee)
	{
		this.isSee = isSee;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

}
