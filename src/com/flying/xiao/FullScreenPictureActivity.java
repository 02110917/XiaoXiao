package com.flying.xiao;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;

import com.flying.xiao.adapter.FullScreenPictureAdapter;

public class FullScreenPictureActivity extends BaseActivity
{

	private ViewPager viewPager;
	private TextView currentPositionText;

	private int initPosition;
	private int pictureSize;

	private ArrayList<String> pictureUrls;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_full_screen);
		Intent intent = getIntent();
		initPosition = intent.getIntExtra("currentPosition", 0);
		pictureUrls = intent.getStringArrayListExtra("pictureurls");
		pictureSize = pictureUrls.size();
		initView();
	}

	private void initView()
	{
		viewPager = (ViewPager) findViewById(R.id.picture_pager);
		currentPositionText = (TextView) findViewById(R.id.current_position);
		currentPositionText.setText(initPosition + 1 + "/" + pictureSize);
		viewPager.setAdapter(new FullScreenPictureAdapter(pictureUrls, this));
		viewPager.setCurrentItem(initPosition);
		viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int position)
			{
				// TODO Auto-generated method stub
				currentPositionText.setText(position + 1 + "/" + pictureSize);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{
				// TODO Auto-generated method stub

			}
		});
	}
}
