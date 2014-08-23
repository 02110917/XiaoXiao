package com.flying.xiao;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.flying.xiao.common.UIHelper;

public class SettingActivity extends BaseActivity
{


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_activity);
		initHeadView();
		initView();
	}

	@Override
	protected void initHeadView()
	{
		// TODO Auto-generated method stub
		super.initHeadView();
		mHeadRightView.setVisibility(View.GONE);
		mHeadTitle.setText("设置");
	}

	private void initView()
	{
	}

	public void settingClick(View v)
	{
		switch (v.getId())
		{
		case R.id.account_manager:// 账号管理

			break;
		case R.id.person_info:// 我的资料
			UIHelper.showMyInfo(this);
			break;
		case R.id.msg_remind:// 消息提醒

			break;
		case R.id.sign_remind:// 签到提醒

			break;
		case R.id.position_paper:// 意见反馈

			break;
		case R.id.clear_cash:// 清除缓存

			break;
		case R.id.about_tieba:// 关于我们

			break;

		default:
			break;
		}
	}
}
