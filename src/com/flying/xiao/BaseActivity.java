package com.flying.xiao;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flying.xiao.app.AppContext;
import com.flying.xiao.app.AppManager;
import com.flying.xiao.common.ShowDialogUtil;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.service.WebSocketService;

/**
 * 应用程序Activity的基类
 */
public class BaseActivity extends FragmentActivity
{

	// 是否允许全屏
	private boolean allowFullScreen = true;

	// 是否允许销毁
	private boolean allowDestroy = true;

	private View view;

	protected Handler mHandler;
	protected AppContext appContext;

	protected ImageView mHeadLeftView;
	protected ImageView mHeadRightView;
	protected TextView mHeadTitle;
	protected Button mHeadRightBtn;
	protected ProgressBar mHeadProgressBar;
	
	
	protected WebSocketService mWebSocketService;
	protected ServiceConnection serviceConnection=new ServiceConnection()
	{
		
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mWebSocketService=null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mWebSocketService=((WebSocketService.MyBinder)service).getWebSocketServiceInstance();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		allowFullScreen = true;
		AppContext.baseActivity = this;
		appContext = (AppContext) getApplication();
		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
		if(activityShoultBindService()){
			UIHelper.bindService(this, serviceConnection);
		}
	}

	protected void initHeadView()
	{

		mHeadLeftView = (ImageView) findViewById(R.id.head_left_view);
		if (mHeadLeftView != null)
		{
			if(this instanceof MyInfoActivity|| this instanceof PubLostActivity|| this instanceof PubDiaryActivity || this instanceof PubContentActivity ||this instanceof PubMarketActivity){
				mHeadLeftView.setOnClickListener(new OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
						ShowDialogUtil.showDialog(BaseActivity.this);
					}
				});
			}else{
				mHeadLeftView.setOnClickListener(UIHelper.finish(this));
			}
		}
			
		mHeadTitle = (TextView) findViewById(R.id.head_title);
		mHeadRightView = (ImageView) findViewById(R.id.head_right_view);
		mHeadProgressBar = (ProgressBar) findViewById(R.id.head_right_progressBar);
		mHeadRightBtn = (Button) findViewById(R.id.head_right_btn);
		mHeadProgressBar.setVisibility(View.GONE);
		mHeadRightBtn.setVisibility(View.GONE);

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		AppContext.baseActivity = this;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
		if(activityShoultBindService()){
			UIHelper.unBindService(this, serviceConnection);
		}
	}

	private boolean activityShoultBindService(){
		return this instanceof MainActivity||this instanceof UserLoginActivity || this instanceof ChatActivity;
	}
	
	public boolean isAllowFullScreen()
	{
		return allowFullScreen;
	}

	/**
	 * 设置是否可以全屏
	 * 
	 * @param allowFullScreen
	 */
	public void setAllowFullScreen(boolean allowFullScreen)
	{
		this.allowFullScreen = allowFullScreen;
	}

	public void setAllowDestroy(boolean allowDestroy)
	{
		this.allowDestroy = allowDestroy;
	}

	public void setAllowDestroy(boolean allowDestroy, View view)
	{
		this.allowDestroy = allowDestroy;
		this.view = view;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && view != null)
		{
			view.onKeyDown(keyCode, event);
			if (!allowDestroy)
			{
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed()
	{
		if(this instanceof MyInfoActivity||this instanceof PubLostActivity||this instanceof PubDiaryActivity || this instanceof PubContentActivity ||this instanceof PubMarketActivity){
			ShowDialogUtil.showDialog(this);
		}else
		{
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
		}
	}

	@Override
	public void startActivity(Intent intent)
	{
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode)
	{
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

	public WebSocketService getmWebSocketService()
	{
		return mWebSocketService;
	}
	
	
}
