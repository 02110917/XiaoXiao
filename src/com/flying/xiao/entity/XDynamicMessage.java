package com.flying.xiao.entity;

import java.util.List;

import com.flying.xiao.constant.Constant;

public class XDynamicMessage extends XDynamic
{
	private List<XMessage> messages ;

	public XDynamicMessage(){
		setType(Constant.DynamicType.DYNAMIC_TYPE_MESSAGE);
	}
	
	public List<XMessage> getMessages()
	{
		return messages;
	}

	public void setMessages(List<XMessage> messages)
	{
		this.messages = messages;
	}
	
}
