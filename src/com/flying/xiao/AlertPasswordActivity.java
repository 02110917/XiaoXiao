package com.flying.xiao;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flying.xiao.app.AppContext;
import com.flying.xiao.app.AppException;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.Base;
import com.flying.xiao.http.HttpUtil;

public class AlertPasswordActivity extends BaseActivity {

	private TextView mTvError;
	private EditText oldPassword;
	private EditText newPassword;
	private EditText confirmPassword;
	private LinearLayout mLinLayoutLogin;
	private ProgressBar mPbLogin;
	private AppContext appContext;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_password);
		appContext = (AppContext) getApplication();
		initHeadView();
		initView();
	}

	@Override
	protected void initHeadView() {
		// TODO Auto-generated method stub
		super.initHeadView();
		mHeadRightView.setVisibility(View.GONE);
		mHeadTitle.setText("修改密码");
	}

	private void initView() {

		mTvError = (TextView) findViewById(R.id.text_error);
		oldPassword = (EditText) findViewById(R.id.edit_old_password);
		newPassword = (EditText) findViewById(R.id.new_password_edit);
		confirmPassword = (EditText) findViewById(R.id.new_password_again);
		mPbLogin = (ProgressBar) findViewById(R.id.progress_login);
		mLinLayoutLogin = (LinearLayout) findViewById(R.id.layout_login);
		mLinLayoutLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkInput(oldPassword.getText().toString(), newPassword
						.getText().toString(), confirmPassword.getText()
						.toString())) {
					mPbLogin.setVisibility(View.VISIBLE);
					NetControl.getShare(AlertPasswordActivity.this)
							.alertPassword(
									oldPassword.getText().toString(),
									newPassword.getText().toString(),
									String.valueOf(appContext.getUserInfo()
											.getId()), handler);

				}
			}
		});
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				mPbLogin.setVisibility(View.GONE);
				if (msg.what == Constant.HandlerMessageCode.LOGIN_SUCCESS) {
					Base base = (Base) msg.obj;
					if (base != null) {
						// 清空原先cookie
						HttpUtil.cleanCookie();
						// 提示登陆成功
						UIHelper.ToastMessage(AlertPasswordActivity.this,
								R.string.msg_alertps_success);
						AlertPasswordActivity.this.finish();
					}
				} else if (msg.what == Constant.ErrorCode.USER_NOT_LOGIN) {
					UIHelper.showLoginDialog(AlertPasswordActivity.this);
				} else if (msg.what == Constant.HandlerMessageCode.LOGIN_FAILD) {
					UIHelper.ToastMessage(AlertPasswordActivity.this,
							getString(R.string.msg_alertps_fail));
				} else if (msg.what == Constant.HandlerMessageCode.NET_THROW_EXCEPTION) {
					((AppException) msg.obj)
							.makeToast(AlertPasswordActivity.this);
				}
			}
		};
	}

	public void delOnclick(View v) {
		switch (v.getId()) {
		case R.id.button_old_password_del:
			oldPassword.getText().clear();
			break;
		case R.id.button_new_password_del:
			newPassword.getText().clear();
			break;
		case R.id.button_new_password_again_del:
			confirmPassword.getText().clear();
			break;

		default:
			break;
		}

	}

	private boolean checkInput(String oldPassword, String newPassword,
			String newPasswordConfirm) {
		if (StringUtils.isEmpty(oldPassword)
				|| StringUtils.isEmpty(newPasswordConfirm)) {
			mTvError.setVisibility(View.VISIBLE);
			mTvError.setText("密码|新密码输入不能为空!");
			return false;
		} else if (!newPassword.equals(newPasswordConfirm)) {
			mTvError.setVisibility(View.VISIBLE);
			mTvError.setText("两次输入新密码不相同，请重新输入!");
			return false;
		}
		mTvError.setVisibility(View.INVISIBLE);
		mTvError.setText("");
		return true;
	}

}