package com.flying.xiao.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Util
{
	public static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
	public static final String APP_ID = "1101686352";
	private static final String TAG = "Xiao.Util";

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getbitmap(String imageUri)
	{
		Log.v(TAG, "getbitmap:" + imageUri);
		// 显示网络上的图片
		Bitmap bitmap = null;
		try
		{
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();

			Log.v(TAG, "image download finished." + imageUri);
		} catch (IOException e)
		{
			e.printStackTrace();
			Log.v(TAG, "getbitmap bmp fail---");
			return null;
		}
		return bitmap;
	}

	public static String getLocalIpAddress()
	{
		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
					.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
						.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress())
					{
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex)
		{
			Log.e("", ex.toString());
		}
		return null;
	}
}
