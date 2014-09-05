package com.flying.xiao.entity;

import java.util.List;

import com.flying.xiao.constant.Constant;

public class XDynamicPraise extends XDynamic
{
	private long contentId;
	private String contentTitle;
	private long contentUserId;
	private String contentUserName;
	private List<XPraise> contentPraise;

	public XDynamicPraise()
	{
		setType(Constant.DynamicType.DYNAMIC_TYPE_PRAISE_ME);
	}

	public long getContentId()
	{
		return contentId;
	}

	public void setContentId(long contentId)
	{
		this.contentId = contentId;
	}

	public String getContentTitle()
	{
		return contentTitle;
	}

	public void setContentTitle(String contentTitle)
	{
		this.contentTitle = contentTitle;
	}

	public long getContentUserId()
	{
		return contentUserId;
	}

	public void setContentUserId(long contentUserId)
	{
		this.contentUserId = contentUserId;
	}

	public String getContentUserName()
	{
		return contentUserName;
	}

	public void setContentUserName(String contentUserName)
	{
		this.contentUserName = contentUserName;
	}

	public List<XPraise> getContentPraise()
	{
		return contentPraise;
	}

	public void setContentPraise(List<XPraise> contentPraise)
	{
		this.contentPraise = contentPraise;
	}

}
