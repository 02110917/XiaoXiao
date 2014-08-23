package com.flying.xiao.entity;

import java.sql.Timestamp;
import java.util.List;

public class XMessage extends Base
{
	 private Integer msgId;
     private XUserInfo userInfoByMsgFromUserId; //某人发
     private long userInfoByMsgUserId; //给某人
     private String msgInfo;
     private Timestamp msgSendTime;
     private XMessage msgReply; //回复这条留言  
     private List<XMessage> replys;
     
	public List<XMessage> getReplys()
	{
		return replys;
	}
	public void setReplys(List<XMessage> replys)
	{
		this.replys = replys;
	}
	public Integer getMsgId()
	{
		return msgId;
	}
	public void setMsgId(Integer msgId)
	{
		this.msgId = msgId;
	}
	
	
	public XUserInfo getUserInfoByMsgFromUserId()
	{
		return userInfoByMsgFromUserId;
	}
	public void setUserInfoByMsgFromUserId(XUserInfo userInfoByMsgFromUserId)
	{
		this.userInfoByMsgFromUserId = userInfoByMsgFromUserId;
	}
	public long getUserInfoByMsgUserId()
	{
		return userInfoByMsgUserId;
	}
	public void setUserInfoByMsgUserId(long userInfoByMsgUserId)
	{
		this.userInfoByMsgUserId = userInfoByMsgUserId;
	}
	public String getMsgInfo()
	{
		return msgInfo;
	}
	public void setMsgInfo(String msgInfo)
	{
		this.msgInfo = msgInfo;
	}
	public Timestamp getMsgSendTime()
	{
		return msgSendTime;
	}
	public void setMsgSendTime(Timestamp msgSendTime)
	{
		this.msgSendTime = msgSendTime;
	}
	public XMessage getMsgReply()
	{
		return msgReply;
	}
	public void setMsgReply(XMessage msgReply)
	{
		this.msgReply = msgReply;
	}
	
}
