package com.flying.xiao.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import com.flying.xiao.BaseActivity;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.db.DBHelper;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.manager.ListManager;
import com.flying.xiao.service.WebSocketService;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 */
public class AppContext extends Application
{

	public static BaseActivity baseActivity;

	public static final int NETTYPE_WIFI = 0x01;

	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	private boolean login = false; // 登录状态
	private boolean isXmppLogin=false ;
	private XUserInfo userInfo = null;

	public  ListManager listManager ;
	public void setUserInfo(XUserInfo userInfo)
	{
		this.userInfo = userInfo;
	}

	private String dirPath ;
	public String headImagePath;
	public String userInfoPath;
	public String contentListPath;
	
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		// 注册App异常崩溃处理器
		//TODO ======
		 Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
		this.dirPath=this.getFilesDir().getAbsolutePath();
		this.headImagePath=dirPath + "/xiao_headimage.png";
		this.userInfoPath=dirPath + "/xiao_userinfo";
		this.contentListPath=dirPath+"/xiao_contentlist";
		initLoginInfo();
		listManager=ListManager.getContentMangerShare();
		listManager.readList(contentListPath);
	}
	
	
	public String readCookie()
	{
		SharedPreferences share = getSharedPreferences("Cookie", Context.MODE_PRIVATE);
		return share.getString("cookie", "");
	}

	public void writeCookie(String cookie)
	{
		SharedPreferences share = getSharedPreferences("Cookie", Context.MODE_PRIVATE);
		Editor edit = share.edit();
		edit.putString("cookie", cookie);
		edit.commit();
	}
	public void clearCookie(){
		SharedPreferences share = getSharedPreferences("Cookie", Context.MODE_PRIVATE);
		Editor edit = share.edit();
		edit.clear();
		edit.commit();
	}
	public void saveUserLoginInfo(XUserInfo userInfo)
	{
		if (userInfo == null || userInfo.getId() <= 0)
		{
			return;
		}
		SharedPreferences share = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
		Editor edit = share.edit();
		edit.putString("user_name", userInfo.getUserName());
		edit.putString("user_psd", userInfo.getUserPsd());
		edit.commit();
	}
	public void writeUserInfo(XUserInfo userInfo)
	{
		this.userInfo=userInfo;
		if (userInfo == null || userInfo.getId() <= 0)
		{
			return;
		}
		ObjectOutputStream objectOut = null;
		try
		{
			objectOut = new ObjectOutputStream(new FileOutputStream(userInfoPath));
			objectOut.writeObject(userInfo);
			saveUserLoginInfo(userInfo);
			this.login=true ;
			this.userInfo=userInfo;

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (objectOut != null)
			{
				try
				{
					objectOut.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public XUserInfo readUserInfo()
	{
		ObjectInputStream objIn = null;
		XUserInfo userInfo = null;
		try
		{
			objIn = new ObjectInputStream(new FileInputStream(userInfoPath));

			userInfo = (XUserInfo) objIn.readObject();

		} catch (StreamCorruptedException e)
		{
			e.printStackTrace();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		if (objIn != null)
		{
			try
			{
				objIn.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return userInfo;
	}

	public void clearUserInfo(){
		File f=new File(userInfoPath);
		f.delete();
	}
	/**
	 * 初始化登陆信息
	 */
	public void initLoginInfo()
	{
		userInfo = readUserInfo();
		if (userInfo != null)
			this.login = true;
		else
			this.login = false;
	}


	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType()
	{
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null)
		{
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE)
		{
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo))
			{
				if (extraInfo.toLowerCase().equals("cmnet"))
				{
					netType = NETTYPE_CMNET;
				} else
				{
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI)
		{
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 用户是否登录
	 * 
	 * @return
	 */
	public boolean isLogin()
	{
		return login;
	}

	public void setLogin(boolean login)
	{
		this.login = login;
	}

	public XUserInfo getUserInfo()
	{
		return userInfo;
	}


	public void Logout()
	{
		login=false ;
		userInfo=null;
		clearCookie();
		clearUserInfo();
	}

	/**
	 * 获取App安装包信息
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}


	public boolean isXmppLogin()
	{
		return isXmppLogin;
	}


	public void setXmppLogin(boolean isXmppLogin)
	{
		this.isXmppLogin = isXmppLogin;
	}
	
	
}
