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
		mHeadTitle.setText("����");
	}

	private void initView()
	{
	}

	public void settingClick(View v)
	{
		switch (v.getId())
		{
		case R.id.account_manager:// �˺Ź���

			break;
		case R.id.person_info:// �ҵ�����
			UIHelper.showMyInfo(this);
			break;
		case R.id.msg_remind:// ��Ϣ����

			break;
		case R.id.sign_remind:// ǩ������

			break;
		case R.id.position_paper:// �������

			break;
		case R.id.clear_cash:// �������

			break;
		case R.id.about_tieba:// ��������

			break;

		default:
			break;
		}
	}
}
