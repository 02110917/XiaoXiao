package com.flying.xiao.websocket;

/**
 * 服务器想客户端推送的更新消息 客户端根据 自己现在的verCode 判断是否需要更新
 * 
 * @author zhangmin
 *
 */
public class WPushUpdate extends WBase
{

	private String ver; // 更新的版本号
	private int verCode; //
	private String apkUrl; // 更新地址
	private String updateInfo; // 更新信息

	public int getVerCode()
	{
		return verCode;
	}

	public void setVerCode(int verCode)
	{
		this.verCode = verCode;
	}

	public String getVer()
	{
		return ver;
	}

	public void setVer(String ver)
	{
		this.ver = ver;
	}

	public String getApkUrl()
	{
		return apkUrl;
	}

	public void setApkUrl(String apkUrl)
	{
		this.apkUrl = apkUrl;
	}

	public String getUpdateInfo()
	{
		return updateInfo;
	}

	public void setUpdateInfo(String updateInfo)
	{
		this.updateInfo = updateInfo;
	}

}
