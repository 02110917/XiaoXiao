package com.flying.xiao;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.flying.xiao.app.AppException;
import com.flying.xiao.common.URLs;
import com.flying.xiao.http.HttpUtil;
import com.flying.xiao.service.WebSocketService;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 */
public class AppStart extends BaseActivity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.start, null);
		setContentView(view);
		final Handler handler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				redirectTo();
			}
		};
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				handler.sendEmptyMessage(0x01);
			}
		}, 1700);

		// 渐变展示启动屏
		// AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
		// aa.setDuration(2000);
		// view.startAnimation(aa);
		// aa.setAnimationListener(new AnimationListener()
		// {
		// @Override
		// public void onAnimationEnd(Animation arg0) {
		// redirectTo();
		// }
		// @Override
		// public void onAnimationRepeat(Animation animation) {}
		// @Override
		// public void onAnimationStart(Animation animation) {}
		//
		// });

	}

	/**
	 * 跳转到...
	 */
	private void redirectTo()
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
		finish();
	}

}