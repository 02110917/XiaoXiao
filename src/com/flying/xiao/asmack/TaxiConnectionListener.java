package com.flying.xiao.asmack;

import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionListener;

import android.os.Handler;
import android.util.Log;

import com.flying.xiao.app.AppContext;

/**
 * 连接监听类
 * 
 * @author Administrator
 * 
 */
public class TaxiConnectionListener implements ConnectionListener {
	private Timer tExit;
	private String username;
	private String password;
	private int logintime = 2000;
	private AppContext appContext;;
	private Handler handler ;
	public TaxiConnectionListener(AppContext appContext,Handler hanlder)
	{
		this.appContext = appContext;
		this.handler=hanlder;
	}

	@Override
	public void connectionClosed() {
		Log.i("TaxiConnectionListener", "连接关闭");
		// 关闭连接
		XmppConnection.getInstance(appContext).closeConnection();
		// 重连服务器
		tExit = new Timer();
		tExit.schedule(new timetask(), logintime);
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		Log.i("TaxiConnectionListener", "连接关闭异常");
		// 判断账号已被登录
		boolean error = e.getMessage().equals("stream:error (conflict)");
		if (!error) {
			// 关闭连接
			XmppConnection.getInstance(appContext).closeConnection();
			// 重连服务器
			tExit = new Timer();
			tExit.schedule(new timetask(), logintime);
		}
	}

	class timetask extends TimerTask {
		@Override
		public void run() {
			username =appContext.getUserInfo().getUserName();
			password = appContext.getUserInfo().getUserPsd();
			if (username != null && password != null) {
				Log.i("TaxiConnectionListener", "尝试登录");
				// 连接服务器
				if (XmppConnection.getInstance(appContext).login(username, password,handler)) {
					Log.i("TaxiConnectionListener", "登录成功");
				} else {
					Log.i("TaxiConnectionListener", "重新登录");
					tExit.schedule(new timetask(), logintime);
				}
			}
		}
	}

	@Override
	public void reconnectingIn(int arg0) {
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
	}

	@Override
	public void reconnectionSuccessful() {
	}

}
