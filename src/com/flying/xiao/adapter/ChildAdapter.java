package com.flying.xiao.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.flying.xiao.R;
import com.flying.xiao.util.NativeImageLoader;
import com.flying.xiao.util.NativeImageLoader.NativeImageCallBack;
import com.flying.xiao.view.MyImageView;
import com.flying.xiao.view.MyImageView.OnMeasureListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class ChildAdapter extends BaseAdapter {
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	/**
	 * 用来存储图片的选中情况
	 */
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private GridView mGridView;
	private List<String> list;
	private List<String> listSelected;
	protected LayoutInflater mInflater;

	public ChildAdapter(Context context, List<String> list,List<String> listSelected, GridView mGridView) {
		this.list = list;
		this.listSelected=listSelected;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String path = list.get(position);
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.grid_child_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
			
			//用来监听ImageView的宽和高
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		viewHolder.mImageView.setTag(path);
		if(listSelected.contains(path)){
			viewHolder.mCheckBox.setChecked(true);
			mSelectMap.put(position, true);
		}
		viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//如果是未选中的CheckBox,则添加动画
				if(!mSelectMap.containsKey(position) || !mSelectMap.get(position)){
					addAnimation(viewHolder.mCheckBox);
				}
				mSelectMap.put(position, isChecked);
			}
		});
		
		viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);
		viewHolder.mImageView.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				boolean ischeck=viewHolder.mCheckBox.isChecked();
				if(!mSelectMap.containsKey(position) || !mSelectMap.get(position)){
					addAnimation(viewHolder.mCheckBox);
				}
				viewHolder.mCheckBox.setChecked(!ischeck);
				mSelectMap.put(position, !ischeck);
			}
		});
		//利用NativeImageLoader类加载本地图片
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint,viewHolder.mImageView, new NativeImageCallBack() {
			
			@Override
			public void onImageLoader(Bitmap bitmap, String path,ImageView imageView) {
				if(bitmap != null && imageView != null){
					imageView.setImageBitmap(bitmap);
				}
			}
		});
		
		if(bitmap != null){
			viewHolder.mImageView.setImageBitmap(bitmap);
		}else{
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		return convertView;
	}
	
	/**
	 * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画 
	 * @param view
	 */
	private void addAnimation(View view){
		float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules), 
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
				set.setDuration(150);
		set.start();
	}
	
	
	/**
	 * 获取选中的Item的path
	 * @return
	 */
	public ArrayList<String> getSelectItems(){
		ArrayList<String> mList = new ArrayList<String>();
		for(Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();){
			Map.Entry<Integer, Boolean> entry = it.next();
			if(entry.getValue()){
				mList.add(list.get(entry.getKey()) );
			}
		}
		
		return mList;
	}
	
	
	public static class ViewHolder{
		public MyImageView mImageView;
		public CheckBox mCheckBox;
	}



}
