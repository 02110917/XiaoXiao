package com.flying.xiao.entity;

/**
 * ��������ʵ����
 * 
 * @author zhangmin
 *
 */
public class RContent extends Base
{
	private String title;
	private String summary;// ժҪ
	private String info;
	private int type;
	private int contentType;

	public int getContentType()
	{
		return contentType;
	}

	public void setContentType(int contentType)
	{
		this.contentType = contentType;
	}

	public String getSummary()
	{
		return summary;
	}

	public void setSummary(String summary)
	{
		this.summary = summary;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getInfo()
	{
		return info;
	}

	public void setInfo(String info)
	{
		this.info = info;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

}
