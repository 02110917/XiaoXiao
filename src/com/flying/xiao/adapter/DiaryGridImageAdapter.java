package com.flying.xiao.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.flying.xiao.R;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.util.AsyncImageLoader;
import com.flying.xiao.util.ImageManager2;
import com.flying.xiao.util.NativeImageLoader;
import com.flying.xiao.util.NativeImageLoader.NativeImageCallBack;
import com.flying.xiao.view.MyImageView;
import com.flying.xiao.view.MyImageView.OnMeasureListener;

public class DiaryGridImageAdapter extends BaseAdapter
{

	private Context mContext;
	private ArrayList<String> dataList;
	private DisplayMetrics dm;

	public DiaryGridImageAdapter(Context c, ArrayList<String> dataList)
	{

		mContext = c;
		this.dataList = dataList;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);

	}

	@Override
	public int getCount()
	{
		return dataList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	private Point mPoint = new Point(0, 0);// 用来封装ImageView的宽和高的对象

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		MyImageView imageView;
		if (convertView == null)
		{
			imageView = new MyImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,
					dipToPx(65)));
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setOnMeasureListener(new OnMeasureListener()
			{

				@Override
				public void onMeasureSize(int width, int height)
				{
					mPoint.set(width, height);
				}
			});
		} else
		{
			imageView = (MyImageView) convertView;
		}

		String path = "";
		if (dataList != null)
		{
			path = dataList.get(position);
			imageView.setTag(path);
		}
		// imageView.setImageResource(R.drawable.friends_sends_pictures_no);
		// AsyncImageLoader.displayImage((AppContext)mContext.getApplicationContext(),
		// path, imageView, R.drawable.friends_sends_pictures_no);
		ImageManager2.from(mContext).displayImage(imageView, path, R.drawable.friends_sends_pictures_no, 100,
				100);
		return imageView;
	}

	public int dipToPx(int dip)
	{
		return (int) (dip * dm.density + 0.5f);
	}

}
