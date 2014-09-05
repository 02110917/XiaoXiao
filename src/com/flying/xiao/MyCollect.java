package com.flying.xiao;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.flying.xiao.adapter.MyFragmentPaperAdapter;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.fragment.MainContentFragment;

/**
 * 我的收藏列表 我发布的内容列表
 * 
 * @author zhangmin
 *
 */
public class MyCollect extends BaseActivity
{
	private Button mBtnNews;
	private Button mBtnLost;
	// private Button mBtnDiary;
	private Button mBtnMarket;
	private Button mBtnAsk;
	private ViewPager mVp;
	private int mCurIndex;
	private List<Fragment> mainFragmentList;
	// private boolean isCollect=true ;//标志是收藏还是我发布的 false:发布 true:收藏
	private int showType;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_collect);
		showType = getIntent().getIntExtra("showType", 1);
		initFragment();
		initHeadView();
		initView();
	}

	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		if (showType == Constant.MainContentFragmentShowType.TYPE_MY_COLLECT)
			mHeadTitle.setText("我的收藏");
		else
			mHeadTitle.setText("我发布的");
		mHeadRightView.setVisibility(View.GONE);
	}

	private void initFragment()
	{
		mainFragmentList = new ArrayList<Fragment>();
		MainContentFragment mainNews = new MainContentFragment();
		mainNews.setConType(Constant.ContentType.CONTENT_TYPE_NEWS);
		mainNews.setShowType(showType);
		MainContentFragment mainLost = new MainContentFragment();
		mainLost.setConType(Constant.ContentType.CONTENT_TYPE_LOST);
		mainLost.setShowType(showType);
		// Fragment mainDiary=new MainDiary() ;
		MainContentFragment mainAsk = new MainContentFragment();
		mainAsk.setConType(Constant.ContentType.CONTENT_TYPE_ASK);
		mainAsk.setShowType(showType);
		MainContentFragment mainMarket = new MainContentFragment();
		mainMarket.setConType(Constant.ContentType.CONTENT_TYPE_MARKET);
		mainMarket.setShowType(showType);
		mainFragmentList.add(mainNews);
		mainFragmentList.add(mainLost);
		// mainFragmentList.add(mainDiary);
		mainFragmentList.add(mainMarket);
		mainFragmentList.add(mainAsk);
	}

	private void initView()
	{
		mBtnNews = (Button) findViewById(R.id.frame_btn_main_news);
		mBtnLost = (Button) findViewById(R.id.frame_btn_main_lost);
		// mBtnDiary=(Button)findViewById(R.id.frame_btn_main_diary);
		mBtnAsk = (Button) findViewById(R.id.frame_btn_main_ask);
		mBtnMarket = (Button) findViewById(R.id.frame_btn_main_market);
		mCurIndex = 0;
		mBtnNews.setEnabled(false);
		mBtnNews.setOnClickListener(new BtnClickListener(0));
		mBtnLost.setOnClickListener(new BtnClickListener(1));
		// mBtnDiary.setOnClickListener(new BtnClickListener(2));
		mBtnMarket.setOnClickListener(new BtnClickListener(3));
		mBtnAsk.setOnClickListener(new BtnClickListener(4));
		mBtnNews.setTag(0);
		mBtnLost.setTag(1);
		// mBtnDiary.setTag(2);
		mBtnMarket.setTag(2);
		mBtnAsk.setTag(3);
		mVp = (ViewPager) findViewById(R.id.viewpager);
		MyFragmentPaperAdapter adapter = new MyFragmentPaperAdapter(getSupportFragmentManager(),
				mainFragmentList);
		mVp.setAdapter(adapter);
		mVp.setCurrentItem(mCurIndex);
		mVp.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int arg0)
			{
				mCurIndex = arg0;
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

	private class BtnClickListener implements OnClickListener
	{
		int index = 0;

		public BtnClickListener(int i)
		{
			index = i;
		}

		@Override
		public void onClick(View v)
		{
			setBtnEnabled((Integer) v.getTag());
			mVp.setCurrentItem(index);
		}

	}

	private void setBtnEnabled(int tag)
	{
		if (tag == 0)
		{
			mBtnNews.setEnabled(false);
		} else
		{
			mBtnNews.setEnabled(true);
		}
		if (tag == 1)
		{
			mBtnLost.setEnabled(false);
		} else
		{
			mBtnLost.setEnabled(true);
		}
		// if(tag==2){
		// mBtnDiary.setEnabled(false);
		// }else{
		// mBtnDiary.setEnabled(true);
		// }
		if (tag == 2)
		{
			mBtnMarket.setEnabled(false);
		} else
		{
			mBtnMarket.setEnabled(true);
		}
		if (tag == 3)
		{
			mBtnAsk.setEnabled(false);
		} else
		{
			mBtnAsk.setEnabled(true);
		}
	}

}
