package com.flying.xiao.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.flying.xiao.R;
import com.flying.xiao.adapter.MyFragmentPaperAdapter;
import com.flying.xiao.constant.Constant;
/**
 * ÉçÇø
 * @author zhangmin
 *
 */
public class CommunityFragment extends Fragment
{
	private ViewPager mViewPaper;
	private List<Fragment> mFragmentList ;
	private Button mBtnDepartment ;
	private Button mBtnBusiness ;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		mFragmentList=new ArrayList<Fragment>();
		Fragment fragment=new CommunityFragmentTab();
		Fragment fragment1=new CommunityFragmentTab();
		((CommunityFragmentTab)fragment).setType(Constant.UserType.User_TYPE_DEPARTMENT);
		((CommunityFragmentTab)fragment1).setType(Constant.UserType.User_TYPE_BUSINESS);
		mFragmentList.add(fragment);
		mFragmentList.add(fragment1);
		super.onCreate(savedInstanceState);
		System.out.println("CommunityFragment onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v=inflater.inflate(R.layout.frame_community, null);
		initView(v);
		System.out.println("CommunityFragment onCreateView");
		return v;
	}

	
	
	@Override
	public void onPause()
	{
		System.out.println("CommunityFragment onPause");
		super.onPause();
	}

	@Override
	public void onResume()
	{
		System.out.println("CommunityFragment onResume");
		super.onResume();
	}

	private void initView(View v){
		mViewPaper=(ViewPager)v.findViewById(R.id.viewpager);
		mBtnDepartment=(Button)v.findViewById(R.id.frame_btn_main_community_department);
		mBtnBusiness=(Button)v.findViewById(R.id.frame_btn_main_community_business);
		mBtnDepartment.setEnabled(false);
		mBtnDepartment.setOnClickListener(new MyOnClickListener());
		mBtnBusiness.setOnClickListener(new MyOnClickListener());
		mBtnDepartment.setTag(0);
		mBtnBusiness.setTag(1); 
		MyFragmentPaperAdapter adapter=new MyFragmentPaperAdapter(getChildFragmentManager(), mFragmentList);
		mViewPaper.setAdapter(adapter);
		mViewPaper.setCurrentItem(0);
		mViewPaper.setOnPageChangeListener(new OnPageChangeListener()
		{
			
			@Override
			public void onPageSelected(int arg0)
			{
				mBtnBusiness.setEnabled(!mBtnBusiness.isEnabled());
				mBtnDepartment.setEnabled(!mBtnDepartment.isEnabled());
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
	
	private class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if((Integer)v.getTag()==0){
				mBtnDepartment.setEnabled(true);
				mBtnBusiness.setEnabled(false);
				mViewPaper.setCurrentItem(0);
			}else{
				mBtnDepartment.setEnabled(false);
				mBtnBusiness.setEnabled(true);
				mViewPaper.setCurrentItem(1);
			}
			
		}
		
	}

}
