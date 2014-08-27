package com.flying.xiao.adapter;

import java.util.ArrayList;

import com.flying.xiao.R;
import com.flying.xiao.common.URLs;
import com.flying.xiao.util.ImageManager2;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class FullScreenPictureAdapter extends PagerAdapter {

	private ArrayList<String> pictureUrls;

	private Context context;

	public FullScreenPictureAdapter(ArrayList<String> pictureUrls,
			Context context) {
		this.pictureUrls = pictureUrls;
		this.context = context;
	}

	@Override
	public int getCount() {
		return pictureUrls.size();
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		PhotoView photoView = new PhotoView(container.getContext());
		String url = pictureUrls.get(position);
		ImageManager2.from(context).displayImage(photoView, url,
				R.drawable.picture_bg);

		// Now just add PhotoView to ViewPager and return it
		container.addView(photoView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		return photoView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}
