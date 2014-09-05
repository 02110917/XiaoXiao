package com.flying.xiao;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XUserInfo;

public class Suggestion extends BaseActivity {

	private LinearLayout notLogin;
	private LinearLayout notLoginName;
	private EditText info;
	private EditText suggesstions;
	private ProgressDialog mProgress;
	private EditText name;
	
	private XUserInfo userInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggestions);
		initHeadView();
		initView();
	}
	
	@Override
	protected void initHeadView()
	{
		// TODO Auto-generated method stub
		super.initHeadView();
		mHeadRightView.setVisibility(View.GONE);
		mHeadRightBtn.setVisibility(View.VISIBLE);
		mHeadRightBtn.setText("发送");
		mHeadTitle.setText("意见反馈");
	}

	private void initView()
	{
		mHandler=new Handler(){

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.LOGIN_FAILD://反馈失败
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(Suggestion.this, R.string.suggest_fail);
					break;
				case Constant.HandlerMessageCode.LOGIN_SUCCESS://反馈成功
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(Suggestion.this, R.string.suggest_success);
					finish();
					break;
				case Constant.HandlerMessageCode.NET_THROW_EXCEPTION://网络异常
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(Suggestion.this, R.string.suggest_fail);
					break ;
				default:
					break;
				}
			}
			
		};
		mHeadRightBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				boolean isEmail = false;
				boolean isPhone = false;
				if(StringUtils.isEmpty(suggesstions.getText().toString()) ){
					UIHelper.ToastMessage(Suggestion.this,"请填写反馈...");
					return ;
				}
				if(appContext.isLogin()){
				mProgress = ProgressDialog.show(v.getContext(), null, "保存中···",true,true); 	
				NetControl.getShare(Suggestion.this).suggest(null, null, null,String.valueOf(userInfo.getId()) , suggesstions.getText().toString(),mHandler);
				}else{
					if(StringUtils.isEmpty(info.getText().toString())){
						UIHelper.ToastMessage(Suggestion.this,"请填写邮箱或密码...");
						return;
					}else{
						isEmail = StringUtils.isEmail(info.getText().toString());
						isPhone = StringUtils.isPhone(info.getText().toString());
					}
					if(!(isEmail||isPhone)){
						UIHelper.ToastMessage(Suggestion.this, "手机或者邮箱格式有误，请重新填写~！");
						return;
					}
					if(StringUtils.isEmpty(name.getText().toString())){
						UIHelper.ToastMessage(Suggestion.this,"请填写姓名...");
						return;
					}
					mProgress = ProgressDialog.show(v.getContext(), null, "保存中···",true,true);
					if(isEmail)
						NetControl.getShare(Suggestion.this).suggest(info.getText().toString(), null, null,name.getText().toString(), suggesstions.getText().toString(),mHandler);
					else
						NetControl.getShare(Suggestion.this).suggest( null, info.getText().toString(), null,name.getText().toString(), suggesstions.getText().toString(),mHandler);
				}
			}
		});
		
		suggesstions = (EditText)findViewById(R.id.suggesstions);
		notLogin = (LinearLayout)findViewById(R.id.not_login);
		notLoginName = (LinearLayout)findViewById(R.id.not_login_name);
		name = (EditText)findViewById(R.id.name);
		info = (EditText)findViewById(R.id.info);
		if(!appContext.isLogin()){
			notLogin.setVisibility(View.VISIBLE);
			notLoginName.setVisibility(View.VISIBLE);
		}else{
			userInfo = appContext.getUserInfo();
		}
			
	}

	
	
}
