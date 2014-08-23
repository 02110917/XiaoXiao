package com.flying.xiao.entity;

import java.util.List;

public class XGoodType extends Base
{
	private Integer esGoodsTypeId;
	private String esGoodsTypeName;
	private List<XMarketTypeSecond> typeSecondList;
    
	public List<XMarketTypeSecond> getTypeSecondList()
	{
		return typeSecondList;
	}
	public void setTypeSecondList(List<XMarketTypeSecond> typeSecondList)
	{
		this.typeSecondList = typeSecondList;
	}
	public Integer getEsGoodsTypeId()
	{
		return esGoodsTypeId;
	}
	public void setEsGoodsTypeId(Integer esGoodsTypeId)
	{
		this.esGoodsTypeId = esGoodsTypeId;
	}
	public String getEsGoodsTypeName()
	{
		return esGoodsTypeName;
	}
	public void setEsGoodsTypeName(String esGoodsTypeName)
	{
		this.esGoodsTypeName = esGoodsTypeName;
	}
}
