package com.flying.xiao.entity;

/**
 * 发布失物实体类
 * 
 * @author zhangmin
 *
 */
public class RLost extends Base
{
	private boolean isLost;
	private String name;
	private String place;
	private String time;
	private String phone;
	private String info;

	public boolean isLost()
	{
		return isLost;
	}

	public void setLost(boolean isLost)
	{
		this.isLost = isLost;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPlace()
	{
		return place;
	}

	public void setPlace(String place)
	{
		this.place = place;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getInfo()
	{
		return info;
	}

	public void setInfo(String info)
	{
		this.info = info;
	}

}
