package com.flying.xiao.websocket;

/**
 * ��������ͻ������͵ĸ�����Ϣ �ͻ��˸��� �Լ����ڵ�verCode �ж��Ƿ���Ҫ����
 * 
 * @author zhangmin
 *
 */
public class WPushUpdate extends WBase
{

	private String ver; // ���µİ汾��
	private int verCode; //
	private String apkUrl; // ���µ�ַ
	private String updateInfo; // ������Ϣ

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
