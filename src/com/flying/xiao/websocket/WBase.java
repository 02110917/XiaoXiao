package com.flying.xiao.websocket;

import com.flying.xiao.entity.Base;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class WBase
{
	private int code;
	private String message ;
	public int getCode()
	{
		return code;
	}
	public void setCode(int code)
	{
		this.code = code;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	public String toJson() {
		Gson gson = new Gson();
		String json = gson.toJson(this);
		return json;
	}

	
}
