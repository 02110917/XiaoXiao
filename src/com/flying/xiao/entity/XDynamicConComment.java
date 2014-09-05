package com.flying.xiao.entity;

import java.util.List;

import com.flying.xiao.constant.Constant;

public class XDynamicConComment extends XDynamic
{
	private long contentId;
	private String contentTitle;
	private long contentUserId;
	private String contentUserName;
	/**
	 * 我参与的评论
	 */
	private List<XComment> contentCommentWithMe;

	public XDynamicConComment()
	{
		setType(Constant.DynamicType.DYNAMIC_TYPE_CONTENT_COMMENT);
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

	public List<XComment> getContentCommentWithMe()
	{
		return contentCommentWithMe;
	}

	public void setContentCommentWithMe(List<XComment> contentCommentWithMe)
	{
		this.contentCommentWithMe = contentCommentWithMe;
	}

}
