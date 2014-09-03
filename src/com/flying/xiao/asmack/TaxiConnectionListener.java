package com.flying.xiao.asmack;

import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionListener;

import android.os.Handler;
import android.util.Log;

import com.flying.xiao.app.AppContext;

/**
 * ���Ӽ�����
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
		Log.i("TaxiConnectionListener", "���ӹر�");
		// �ر�����
		XmppConnection.getInstance(appContext).closeConnection();
		// ����������
		tExit = new Timer();
		tExit.schedule(new timetask(), logintime);
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		Log.i("TaxiConnectionListener", "���ӹر��쳣");
		// �ж��˺��ѱ���¼
		boolean error = e.getMessage().equals("stream:error (conflict)");
		if (!error) {
			// �ر�����
			XmppConnection.getInstance(appContext).closeConnection();
			// ����������
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
				Log.i("TaxiConnectionListener", "���Ե�¼");
				// ���ӷ�����
				if (XmppConnection.getInstance(appContext).login(username, password,handler)) {
					Log.i("TaxiConnectionListener", "��¼�ɹ�");
				} else {
					Log.i("TaxiConnectionListener", "���µ�¼");
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
