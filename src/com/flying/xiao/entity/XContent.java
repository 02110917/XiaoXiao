package com.flying.xiao.entity;

import java.sql.Timestamp;
import java.util.List;

public class XContent extends Base
{
	private Long id;
//	private Long userId;
	private String conImageUrl ;
//	private String userRealNama ;
	private XUserInfo userInfo ;
	private List<XPraise> praiseList ;
	private double price ;
	private boolean isMeIsPraise; //我是否点赞  【在获取到User session的情况下返回】
	private boolean isMeCollecte ; //我是否收藏【在获取到User session的情况下返回】
	private List<XImage> images;
	private Timestamp conPubTime;
	private Integer conZan;
	private Integer conHot;
	private String conTitle;
	private Integer conPls;
	private Integer conTypeId;
	private List<XComment> comments;
	private String conSummary;
	
	private boolean isLost=true ;
	
//	@Override
//	public <T> void copy(T t)
//	{
//		super.copy(t);
////		this.setUserId(((Content)t).getUserInfo().getId());
//		if (this.getConImageUrl() == null)
//			this.setConImageUrl(((Content)t).getUserInfo().getUserHeadImageUrl());
////		this.setUserRealNama(((Content)t).getUserInfo().getUserRealName());
//		XUserInfo user=new XUserInfo();
//		user.copy(((Content)t).getUserInfo());
//		this.setUserInfo(user);
//	}
	
	public List<XImage> getImages()
	{
		return images;
	}
	
	public boolean isLost()
	{
		return isLost;
	}

	public void setLost(boolean isLost)
	{
		this.isLost = isLost;
	}

	public void setImages(List<XImage> images)
	{
		this.images = images;
	}
	public boolean isMeIsPraise()
	{
		return isMeIsPraise;
	}
	public void setMeIsPraise(boolean isMeIsPraise)
	{
		this.isMeIsPraise = isMeIsPraise;
	}
	
	public boolean isMeCollecte()
	{
		return isMeCollecte;
	}
	public void setMeCollecte(boolean isMeCollecte)
	{
		this.isMeCollecte = isMeCollecte;
	}
	public List<XPraise> getPraiseList()
	{
		return praiseList;
	}
	public void setPraiseList(List<XPraise> praiseList)
	{
		this.praiseList = praiseList;
	}
//	public String getUserRealNama()
//	{
//		return userRealNama;
//	}
//	public void setUserRealNama(String userRealNama)
//	{
//		this.userRealNama = userRealNama;
//	}
	public String getConSummary()
	{
		return conSummary;
	}

	public void setConSummary(String conSummary)
	{
		this.conSummary = conSummary;
	}
	public List<XComment> getComments()
	{
		return comments;
	}
	public void setComments(List<XComment> comments)
	{
		this.comments = comments;
	}
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	
//	public Long getUserId()
//	{
//		return userId;
//	}
//	public void setUserId(Long userId)
//	{
//		this.userId = userId;
//	}
	
	
	public String getConImageUrl()
	{
		return conImageUrl;
	}
	public void setConImageUrl(String conImageUrl)
	{
		this.conImageUrl = conImageUrl;
	}
	public Timestamp getConPubTime()
	{
		return conPubTime;
	}
	public void setConPubTime(Timestamp conPubTime)
	{
		this.conPubTime = conPubTime;
	}
	public Integer getConZan()
	{
		return conZan;
	}
	public void setConZan(Integer conZan)
	{
		this.conZan = conZan;
	}
	public Integer getConHot()
	{
		return conHot;
	}
	public void setConHot(Integer conHot)
	{
		this.conHot = conHot;
	}
	public String getConTitle()
	{
		return conTitle;
	}
	public void setConTitle(String conTitle)
	{
		this.conTitle = conTitle;
	}
	public Integer getConPls()
	{
		return conPls;
	}
	public void setConPls(Integer conPls)
	{
		this.conPls = conPls;
	}
	public Integer getConTypeId()
	{
		return conTypeId;
	}
	public void setConTypeId(Integer conTypeId)
	{
		this.conTypeId = conTypeId;
	}
	public double getPrice()
	{
		return price;
	}
	public void setPrice(double price)
	{
		this.price = price;
	}
	public XUserInfo getUserInfo()
	{
		return userInfo;
	}
	public void setUserInfo(XUserInfo userInfo)
	{
		this.userInfo = userInfo;
	}
	
}
