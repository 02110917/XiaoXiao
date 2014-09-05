package com.flying.xiao.adapter;

import java.util.ArrayList;

import com.flying.xiao.R;
import com.flying.xiao.common.URLs;
import com.flying.xiao.util.ImageManager2;
import com.flying.xiao.util.NativeImageLoader;
import com.flying.xiao.util.NativeImageLoader.NativeImageCallBack;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

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
		if(url.contains("http")){
			ImageManager2.from(context).displayImage(photoView, url,
					R.drawable.picture_bg);
		}else{
			//利用NativeImageLoader类加载本地图片
			Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(url, null,photoView, new NativeImageCallBack() {
				
				@Override
				public void onImageLoader(Bitmap bitmap, String path,ImageView imageView) {
					if(bitmap != null && imageView != null){
						imageView.setImageBitmap(bitmap);
					}
				}
			});
			
			if(bitmap != null){
				photoView.setImageBitmap(bitmap);
			}else{
				photoView.setImageResource(R.drawable.friends_sends_pictures_no);
			}
		}

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
