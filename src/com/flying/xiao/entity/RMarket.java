package com.flying.xiao.entity;

/**
 * 发布商品实体类
 * @author zhangmin
 *
 */
public class RMarket extends Base
{
	private String name ;
	private String price ;
	private String priceNew ;
	private String userPhone ;
	private String userName ;
	private String chengSe ;
	private String info ;
	private int typeId;
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getPrice()
	{
		return price;
	}
	public void setPrice(String price)
	{
		this.price = price;
	}
	public String getPriceNew()
	{
		return priceNew;
	}
	public void setPriceNew(String priceNew)
	{
		this.priceNew = priceNew;
	}
	public String getUserPhone()
	{
		return userPhone;
	}
	public void setUserPhone(String userPhone)
	{
		this.userPhone = userPhone;
	}
	public String getUserName()
	{
		return userName;
	}
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	public String getChengSe()
	{
		return chengSe;
	}
	public void setChengSe(String chengSe)
	{
		this.chengSe = chengSe;
	}
	public String getInfo()
	{
		return info;
	}
	public void setInfo(String info)
	{
		this.info = info;
	}
	public int getTypeId()
	{
		return typeId;
	}
	public void setTypeId(int typeId)
	{
		this.typeId = typeId;
	}
	
	
}
