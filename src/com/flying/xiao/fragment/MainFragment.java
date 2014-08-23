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
import com.flying.xiao.app.AppContext;
import com.flying.xiao.constant.Constant;

public class MainFragment extends Fragment
{
	private Button mBtnNews;
	private Button mBtnLost;
//	private Button mBtnDiary;
	private Button mBtnMarket;
	private Button mBtnAsk;
	private ViewPager mVp;
	private int mCurIndex ; 
	private List<Fragment> mainFragmentList ;
	
	private AppContext appContext;// È«¾ÖContext
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		appContext=(AppContext) getActivity().getApplication();
		mainFragmentList=new ArrayList<Fragment>();
		Fragment mainNews=new MainContentFragment() ;
		((MainContentFragment) mainNews).setConType(Constant.ContentType.CONTENT_TYPE_NEWS);
		Fragment mainLost=new MainContentFragment() ;
		((MainContentFragment) mainLost).setConType(Constant.ContentType.CONTENT_TYPE_LOST);
//		Fragment mainDiary=new MainDiary() ;
		Fragment mainAsk=new MainContentFragment() ;
		((MainContentFragment) mainAsk).setConType(Constant.ContentType.CONTENT_TYPE_ASK);
		Fragment mainMarket=new MainContentFragment() ;
		((MainContentFragment) mainMarket).setConType(Constant.ContentType.CONTENT_TYPE_MARKET);
		mainFragmentList.add(mainNews);
		mainFragmentList.add(mainLost);
//		mainFragmentList.add(mainDiary);
		mainFragmentList.add(mainMarket);
		mainFragmentList.add(mainAsk);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v=inflater.inflate(R.layout.frame_main, null);
		initView(v);
		return v;
	}

	private void initView(View v){
		mBtnNews=(Button)v.findViewById(R.id.frame_btn_main_news);
		mBtnLost=(Button)v.findViewById(R.id.frame_btn_main_lost);
//		mBtnDiary=(Button)v.findViewById(R.id.frame_btn_main_diary);
		mBtnAsk=(Button)v.findViewById(R.id.frame_btn_main_ask);
		mBtnMarket=(Button)v.findViewById(R.id.frame_btn_main_market);
		mCurIndex=0;
		mBtnNews.setEnabled(false);
		mBtnNews.setOnClickListener(new BtnClickListener(0));
		mBtnLost.setOnClickListener(new BtnClickListener(1));
//		mBtnDiary.setOnClickListener(new BtnClickListener(2));
		mBtnMarket.setOnClickListener(new BtnClickListener(2));
		mBtnAsk.setOnClickListener(new BtnClickListener(3));
		mBtnNews.setTag(0);
		mBtnLost.setTag(1);
//		mBtnDiary.setTag(2);
		mBtnMarket.setTag(2);
		mBtnAsk.setTag(3);
		mVp=(ViewPager)v.findViewById(R.id.viewpager);
		MyFragmentPaperAdapter adapter=new MyFragmentPaperAdapter(getChildFragmentManager(),mainFragmentList);
		mVp.setAdapter(adapter);
		mVp.setCurrentItem(mCurIndex);
		mVp.setOnPageChangeListener(new OnPageChangeListener()
		{
			
			@Override
			public void onPageSelected(int arg0)
			{
				mCurIndex=arg0;
				setBtnEnabled(arg0);
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
	
	private class BtnClickListener implements OnClickListener{
		int index=0 ;
		public BtnClickListener(int i){
			index=i;
		}
		@Override
		public void onClick(View v)
		{
			setBtnEnabled((Integer)v.getTag());
			mVp.setCurrentItem(index);
		}
		
	}
	private void setBtnEnabled(int tag){
		if(tag==0){
			mBtnNews.setEnabled(false);
		}else{
			mBtnNews.setEnabled(true);
		}
		if(tag==1){
			mBtnLost.setEnabled(false);
		}else{
			mBtnLost.setEnabled(true);
		}
//		if(tag==2){
//			mBtnDiary.setEnabled(false);
//		}else{
//			mBtnDiary.setEnabled(true);
//		}
		if(tag==2){
			mBtnMarket.setEnabled(false);
		}else{
			mBtnMarket.setEnabled(true);
		}
		if(tag==3){
			mBtnAsk.setEnabled(false);
		}else{
			mBtnAsk.setEnabled(true);
		}
	}

}
