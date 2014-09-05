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
import com.flying.xiao.util.ImageManager2;
import com.flying.xiao.util.NativeImageLoader;
import com.flying.xiao.util.NativeImageLoader.NativeImageCallBack;
import com.flying.xiao.view.MyImageView;
import com.flying.xiao.view.MyImageView.OnMeasureListener;

public class GridImageAdapter extends BaseAdapter
{

	private Context mContext;
	private ArrayList<String> dataList;
	private DisplayMetrics dm;
	private GridView mGridView;

	public GridImageAdapter(Context c, ArrayList<String> dataList, GridView mGridView)
	{

		mContext = c;
		this.dataList = dataList;
		this.mGridView = mGridView;
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

		String path;
		if (dataList != null && position < dataList.size())
		{
			path = dataList.get(position);
			imageView.setTag(path);
		} else
			path = "camera_default";
		Log.i("path", "path:" + path + "::position" + position);
		if (path.contains("default"))
			imageView.setImageResource(R.drawable.camera_default);
		else
		{
			// 利用NativeImageLoader类加载本地图片
			Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, imageView,
					new NativeImageCallBack()
					{

						@Override
						public void onImageLoader(Bitmap bitmap, String path, ImageView imageView)
						{
							if (bitmap != null && imageView != null)
							{
								imageView.setImageBitmap(bitmap);
							}
						}
					});

			if (bitmap != null)
			{
				imageView.setImageBitmap(bitmap);
			} else
			{
				imageView.setImageResource(R.drawable.friends_sends_pictures_no);
			}
			// ImageManager2.from(mContext).displayImage(imageView,
			// path,R.drawable.camera_default,100,100);
		}
		return imageView;
	}

	public int dipToPx(int dip)
	{
		return (int) (dip * dm.density + 0.5f);
	}

}
