package com.flying.xiao;

import com.flying.xiao.common.UIHelper;

import android.os.Bundle;
import android.view.View;

public class UserInfoManage extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo_manage);
		initHeadView();
		initView();

	}

	@Override
	protected void initHeadView() {
		// TODO Auto-generated method stub
		super.initHeadView();
		mHeadRightView.setVisibility(View.GONE);
		mHeadTitle.setText("账号管理");
	}

	private void initView() {

	}

	public void settingClick(View v) {
		switch (v.getId()) {
		case R.id.change_user:// 更换用户
			UIHelper.showLoginOrRegiste(this, true);
			break;
		case R.id.alert_password:// 修改密码
			if (!appContext.isLogin()) {
				UIHelper.showLoginOrRegiste(this, true);
			} else {
				UIHelper.showAlertPassword(this);
			}
			break;
		case R.id.logout:// 注销
			UIHelper.loginOrLogout(UserInfoManage.this);
			UIHelper.showHome(this);
			UserInfoManage.this.finish();
			break;

		default:
			break;
		}
	}
}
