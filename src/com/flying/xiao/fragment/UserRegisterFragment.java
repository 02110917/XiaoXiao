package com.flying.xiao.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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

public class UserRegisterFragment extends Fragment
{

	private TextView mTvError;
	private EditText mEtUserName;
	private EditText mEtUserPsd;
	private EditText mEtPhone;
	private EditText mEtName; //名称
	private Spinner mUserType ;
	private LinearLayout mLinLayoutLogin;
	private ProgressBar mPbLogin;
	private UserLoginActivity context;
	private AppContext appContext;
	private String[] userTypes=new String[]{"个人","部门","商家"};
	private Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			mPbLogin.setVisibility(View.GONE);
			if (msg.what == Constant.HandlerMessageCode.REGISTER_SUCCESS)
			{
				XUserInfo user = (XUserInfo) msg.obj;
				if (user != null)
				{
					// 清空原先cookie
					HttpUtil.cleanCookie();
					// 提示登陆成功
					UIHelper.ToastMessage(context, "注册成功!");
					// 跳转
					appContext.initLoginInfo();
					XmppControl.getShare(context).deleteAccount();
					context.getmWebSocketService().setXmppLogin(false);
					context.getmWebSocketService().loginToXmpp();
					UIHelper.showMyInfo(context);
					context.finish();
				}
			} else if (msg.what == Constant.HandlerMessageCode.REGISTER_FAILD)
			{
				UIHelper.ToastMessage(context, "注册失败!");
			}else if (msg.what == Constant.HandlerMessageCode.REGISTER_FAILD_USERNAME_HAVED)
			{
				mTvError.setVisibility(View.VISIBLE);
				mTvError.setText("用户名已存在!!");
			} else if (msg.what == Constant.HandlerMessageCode.NET_THROW_EXCEPTION)
			{
				((AppException) msg.obj).makeToast(context);
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = (UserLoginActivity) getActivity();
		appContext = (AppContext) getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v=inflater.inflate(R.layout.user_regiest_fragment, null);
		initView(v);
		return v;
	}
	
	private void initView(View v)
	{
		mTvError = (TextView) v.findViewById(R.id.text_error);
		mUserType=(Spinner)v.findViewById(R.id.user_type);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, userTypes);
		mUserType.setAdapter(adapter);
		mEtUserName = (EditText) v.findViewById(R.id.edit_user_name);
		mEtUserPsd = (EditText) v.findViewById(R.id.edit_psw);
		mEtPhone = (EditText) v.findViewById(R.id.edit_phone);
		mEtName = (EditText) v.findViewById(R.id.edit_name);
		mPbLogin = (ProgressBar) v.findViewById(R.id.progress_login);
		mLinLayoutLogin = (LinearLayout) v.findViewById(R.id.layout_login);
		mLinLayoutLogin.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				String uName = mEtUserName.getText().toString().trim();
				String uPsd = mEtUserPsd.getText().toString().trim();
				String uPhone=mEtPhone.getText().toString().trim();
				String uRealname=mEtName.getText().toString().trim();
				if (checkInput(uName, uPsd,uPhone,uRealname))
				{
					mPbLogin.setVisibility(View.VISIBLE);
					XUserInfo userInfo=new XUserInfo();
					userInfo.setUserName(uName);
					userInfo.setUserPsd(uPsd);
					userInfo.setUserPhone(uPhone);
					userInfo.setUserTypeId(mUserType.getSelectedItemPosition()+1);
					userInfo.setUserRealName(uRealname);
					userInfo.setUserFuns(0);
					userInfo.setUserGerenshuoming("");
					userInfo.setUserHeadImageUrl("/XiaoServer/head_image/546107362.jpg");
					mPbLogin.setVisibility(View.VISIBLE);
					NetControl.getShare(context).register(userInfo, handler);
				}
			}
		});
	}
	
	private boolean checkInput(String uName, String uPsd,String uPhone,String uRealName)
	{
		if (StringUtils.isEmpty(uName) || StringUtils.isEmpty(uPsd)||StringUtils.isEmpty(uPhone)||StringUtils.isEmpty(uRealName))
		{
			mTvError.setVisibility(View.VISIBLE);
			mTvError.setText("输入不能为空!");
			return false;
		} else if (!StringUtils.isEmail(uName))
		{
			mTvError.setVisibility(View.VISIBLE);
			mTvError.setText("邮箱格式不正确!");
			return false;
		}else if(!StringUtils.isPhone(uPhone)){
			mTvError.setVisibility(View.VISIBLE);
			mTvError.setText("手机号格式不正确");
			return false;	
		}
		mTvError.setVisibility(View.INVISIBLE);
		mTvError.setText("");
		return true;
	}

	public void showPsdOnclick(View v)
	{	
		Button btn=(Button) v ;
		if(mEtUserPsd.getInputType()==0x81)
		{
			mEtUserPsd.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
			btn.setText("隐藏");
		}
		else
		{
			mEtUserPsd.setInputType(0x81);
			btn.setText("显示");
		}
	}
}
