package com.flying.xiao;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.flying.xiao.util.Util;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class LoginActivity extends Activity
{
	// tencentµÇÂ½Ïà¹Ø
	public static QQAuth mQQAuth;
	private UserInfo mInfo;
	private Tencent mTencent;

	// view
	private ImageView loginBywx;
	private ImageView loginByqq;
	private Button login;
	private EditText userName;
	private EditText passWord;
	private BtnOnClickListener btnListener ;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mQQAuth = QQAuth.createInstance(Util.APP_ID, getApplicationContext());
		mTencent = Tencent.createInstance(Util.APP_ID, this);
		initView();
	}

	private void initView()
	{
		btnListener=new BtnOnClickListener() ;
		loginBywx = (ImageView) findViewById(R.id.wxquick);
		loginByqq = (ImageView) findViewById(R.id.qquick);
		loginBywx.setOnClickListener(btnListener);
		loginByqq.setOnClickListener(btnListener);
		login = (Button) findViewById(R.id.btn_login);
		userName = (EditText) findViewById(R.id.account);
		passWord = (EditText) findViewById(R.id.password);
	}

	class BtnOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.wxquick:

				break;
			case R.id.qquick:
				onClickLogin() ;
				break;
			default:
				break;
			}
		}

	}
	private void onClickLogin() {
		if (!mQQAuth.isSessionValid()) {
			IUiListener listener = new BaseUiListener() {
				@Override
				protected void doComplete(JSONObject values) {
					updateUserInfo();
				}
			}; 
			mTencent.login(this, "all",listener);
		} else {
			mQQAuth.logout(this);
		}
	}
	private void updateUserInfo() {
		if (mQQAuth != null && mQQAuth.isSessionValid()) {
			IUiListener listener = new IUiListener() {
				
				@Override
				public void onError(UiError e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					msg.obj = response;
					msg.what = 0;
					mHandler.sendMessage(msg);
					new Thread(){

						@Override
						public void run() {
							JSONObject json = (JSONObject)response;
							if(json.has("figureurl")){
								Bitmap bitmap = null;
								try {
									bitmap = Util.getbitmap(json.getString("figureurl_qq_2"));
								} catch (JSONException e) {
									
								}
								Message msg = new Message();
								msg.obj = bitmap;
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
						}
						
					}.start();
				}
				
				@Override
				public void onCancel() {
					
				}
			};
			mInfo = new UserInfo(this, mQQAuth.getQQToken());
			mInfo.getUserInfo(listener);
			
		} else {
//			mUserInfo.setText("");
//			mUserInfo.setVisibility(android.view.View.GONE);
//			mUserLogo.setVisibility(android.view.View.GONE);
		}
	}
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				JSONObject response = (JSONObject) msg.obj;
				if (response.has("nickname")) {
					try
					{
						Toast.makeText(getApplicationContext(), response.getString("nickname"), Toast.LENGTH_SHORT).show();
					} catch (JSONException e)
					{
						e.printStackTrace();
					}
				}
			}else if(msg.what == 1){
				Bitmap bitmap = (Bitmap)msg.obj;
//				mUserLogo.setImageBitmap(bitmap);
//				mUserLogo.setVisibility(android.view.View.VISIBLE);
			}
		}

	};
	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			Toast.makeText(LoginActivity.this, response.toString()+" µÇÂ¼³É¹¦", Toast.LENGTH_SHORT).show();
			doComplete((JSONObject)response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			Toast.makeText(LoginActivity.this, e.errorDetail+" µÇÂ¼Ê§°Ü", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(LoginActivity.this, " onCancel", Toast.LENGTH_SHORT).show();
		}
	}
}
