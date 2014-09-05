package com.flying.xiao.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flying.xiao.MainActivity;
import com.flying.xiao.R;
import com.flying.xiao.UserLoginActivity;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.app.AppException;
import com.flying.xiao.asmack.XmppConnection;
import com.flying.xiao.asmack.XmppControl;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.http.HttpUtil;

public class UserLoginFragment extends Fragment
{

	private TextView mTvError;
	private EditText mEtUserName;
	private EditText mEtUserPsd;
	private LinearLayout mLinLayoutLogin;
	private ProgressBar mPbLogin;
	private UserLoginActivity context;
	private AppContext appContext;
	private Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			mPbLogin.setVisibility(View.GONE);
			if (msg.what == Constant.HandlerMessageCode.LOGIN_SUCCESS)
			{
				XUserInfo user = (XUserInfo) msg.obj;
				if (user != null)
				{
					// 清空原先cookie
					HttpUtil.cleanCookie();
					// 提示登陆成功
					UIHelper.ToastMessage(context, R.string.msg_login_success);
					// 跳转--加载用户动态
					// Intent intent = new Intent(context, MainActivity.class);
					// intent.putExtra("LOGIN", true);
					// startActivity(intent);
					appContext.initLoginInfo();

					XmppControl.getShare(context).deleteAccount();
					context.getmWebSocketService().setXmppLogin(false);
					context.getmWebSocketService().loginToXmpp();
					context.finish();
				}
			} else if (msg.what == Constant.HandlerMessageCode.LOGIN_FAILD)
			{
				UIHelper.ToastMessage(context, getString(R.string.msg_login_fail));
			} else if (msg.what == Constant.HandlerMessageCode.NET_THROW_EXCEPTION)
			{
				((AppException) msg.obj).makeToast(context);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = (UserLoginActivity) getActivity();
		appContext = (AppContext) getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.user_login_fragment, null);
		initView(v);
		return v;
	}

	private void initView(View v)
	{
		mTvError = (TextView) v.findViewById(R.id.text_error);
		mEtUserName = (EditText) v.findViewById(R.id.login_edit_account);
		mEtUserPsd = (EditText) v.findViewById(R.id.login_edit_password);
		mPbLogin = (ProgressBar) v.findViewById(R.id.progress_login);
		mLinLayoutLogin = (LinearLayout) v.findViewById(R.id.layout_login);
		initUserInput();
		mLinLayoutLogin.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				String uName = mEtUserName.getText().toString().trim();
				String uPsd = mEtUserPsd.getText().toString().trim();
				if (checkInput(uName, uPsd))
				{
					mPbLogin.setVisibility(View.VISIBLE);
					NetControl.getShare(context).login(uName, uPsd, handler);
				}
			}
		});
	}

	private void initUserInput()
	{
		SharedPreferences share = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
		if (share != null)
		{
			String user = share.getString("user_name", "");
			String psd = share.getString("user_psd", "");
			mEtUserName.setText(user);
			mEtUserPsd.setText(psd);
		}

	}

	private boolean checkInput(String uName, String uPsd)
	{
		if (StringUtils.isEmpty(uName) || StringUtils.isEmpty(uPsd))
		{
			mTvError.setVisibility(View.VISIBLE);
			mTvError.setText("用户名或密码输入不能为空!");
			return false;
		} else if (!StringUtils.isEmail(uName))
		{
			mTvError.setVisibility(View.VISIBLE);
			mTvError.setText("邮箱格式不正确!");
			return false;
		}
		mTvError.setVisibility(View.INVISIBLE);
		mTvError.setText("");
		return true;
	}

	public void delOnclick(View v)
	{
		if (v.getId() == R.id.button_account_del)
		{ // 删除用户名
			mEtUserName.getText().clear();
		} else if (v.getId() == R.id.button_pass_del)
		{ // 删除密码
			mEtUserPsd.getText().clear();
		}
	}
}
