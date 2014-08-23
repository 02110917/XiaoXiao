package com.flying.xiao.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPaperAdapter extends FragmentPagerAdapter
{
	private List<Fragment> mainFragmentList;
	public MyFragmentPaperAdapter(FragmentManager fm)
	{
		super(fm);
		// TODO Auto-generated constructor stub
	}
	 public MyFragmentPaperAdapter(FragmentManager fm,List<Fragment> mainFragmentList)
	{
		 this(fm);
		 this.mainFragmentList=mainFragmentList;
	}
	@Override
	public Fragment getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return mainFragmentList.get(arg0);
	}

	@Override
	public int getCount()
	{
		return mainFragmentList.size();
	}
}
