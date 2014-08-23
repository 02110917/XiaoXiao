package com.flying.xiao.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flying.xiao.R;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.util.ImageManager2;
/**
 * 社区adapter
 * @author zhangmin
 *
 */
public class ListViewMyFriendAdapter extends BaseAdapter
{
	private Context context;// 运行上下文
	private List<XUserInfo> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private Handler handler;
	private AppContext appContext ;
	static class ListItemView
	{ // 自定义控件集合
		public ImageView face;
		public TextView name; 
		public TextView summary ; //个人说明
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewMyFriendAdapter(Context context, List<XUserInfo> data, int resource)
	{
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		appContext=(AppContext) ((Activity)context).getApplication();
	}
	@Override
	public int getCount()
	{
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}

	/**
	 * ListView Item设置
	 */
	@Override
	public View getView( int position, View convertView, ViewGroup parent)
	{
		// 自定义视图
		final ListItemView listItemView;

		if (convertView == null)
		{
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.face = (ImageView) convertView.findViewById(R.id.my_friend_listitem_userface);
			listItemView.name = (TextView) convertView.findViewById(R.id.my_friend_listitem_title);
			listItemView.summary = (TextView) convertView.findViewById(R.id.my_friend_listitem_Summary);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else
		{
			listItemView = (ListItemView) convertView.getTag();
		}

		// 设置文字和图片
		final XUserInfo userInfo = listItems.get(position);
		String faceURL="";
		faceURL = userInfo.getUserHeadImageUrl();
		System.out.println("image url---"+faceURL);
		if (faceURL==null||faceURL.endsWith(".gif") || StringUtils.isEmpty(faceURL))
		{
			listItemView.face.setImageResource(R.drawable.widget_dface);
		} else
		{
			ImageManager2.from(context).displayImage(listItemView.face, URLs.HOST+faceURL, R.drawable.widget_dface);
		}
		listItemView.face.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				UIHelper.showUserInfo(context, userInfo);
			}
		}); //设置点击进入用户详情
		listItemView.face.setTag(URLs.HOST+faceURL);
		listItemView.name.setText(userInfo.getUserRealName());
		if(!userInfo.isOnline())
		{
			listItemView.name.setText(userInfo.getUserRealName()+" 离线");
			listItemView.name.setTextColor(Color.RED);
		}
		else
		{
			listItemView.name.setText(userInfo.getUserRealName()+" 在线");
			listItemView.name.setTextColor(Color.BLACK);
		}
		listItemView.summary.setText(userInfo.getUserGerenshuoming());
		return convertView;
	}

	
}