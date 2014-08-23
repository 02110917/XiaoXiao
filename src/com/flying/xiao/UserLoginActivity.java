package com.flying.xiao;

import java.util.ArrayList;
import java.util.List;

import com.flying.xiao.adapter.MyFragmentPaperAdapter;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.fragment.UserLoginFragment;
import com.flying.xiao.fragment.UserRegisterFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserLoginActivity extends BaseActivity
{
	private Button mBtnLogin ;
	private Button mBtnRegiest ;
	private ViewPager mvpChange;
	private List<Fragment> mFragmentList ;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login_activity);
		initHeadView();
		initView();
	}
	
	

	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadRightView.setVisibility(View.GONE);
	}

	private void initView(){
		boolean isLogin=getIntent().getExtras().getBoolean("isLogin");
		mBtnLogin=(Button)findViewById(R.id.user_login);
		mBtnRegiest=(Button)findViewById(R.id.user_regiest);
		mBtnLogin.setTag(0);
		mBtnRegiest.setTag(1);
		mBtnLogin.setOnClickListener(listener);
		mBtnRegiest.setOnClickListener(listener);
		mvpChange=(ViewPager)findViewById(R.id.viewpager);
		mFragmentList=new ArrayList<Fragment>();
		UserLoginFragment loginFragment=new UserLoginFragment();
		UserRegisterFragment registerFragment=new UserRegisterFragment();
		mFragmentList.add(loginFragment);
		mFragmentList.add(registerFragment);
		MyFragmentPaperAdapter adapter=new MyFragmentPaperAdapter(getSupportFragmentManager(), mFragmentList);
		mvpChange.setAdapter(adapter);
		if(isLogin){
			changeTabBtnState(0);
			mvpChange.setCurrentItem(0);
		}else{
			changeTabBtnState(1);
			mvpChange.setCurrentItem(1);
		}
		mvpChange.setOnPageChangeListener(new OnPageChangeListener()
		{
			
			@Override
			public void onPageSelected(int arg0)
			{
				changeTabBtnState(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
				
			}
		});
	}
	private OnClickListener listener=new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			int tag=(Integer) v.getTag();
			changeTabBtnState(tag);
			mvpChange.setCurrentItem(tag);
		}
	};
	
	private void changeTabBtnState(int tag){
		if(tag==0){
			mHeadTitle.setText("用户登陆");
			mBtnLogin.setBackgroundResource(R.drawable.login_tab_pressed);	
			mBtnRegiest.setBackgroundResource(R.drawable.login_tab_normal);
		}
		else{
			mHeadTitle.setText("用户注册");
			mBtnLogin.setBackgroundResource(R.drawable.login_tab_normal);	
			mBtnRegiest.setBackgroundResource(R.drawable.login_tab_pressed);
		}
	}
	public void delOnclick(View v)
	{
		((UserLoginFragment)(mFragmentList.get(0))).delOnclick(v);
	}
	public void showPsd(View v){
		((UserRegisterFragment)(mFragmentList.get(1))).showPsdOnclick(v);
	}
}
