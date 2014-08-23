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
import com.flying.xiao.entity.MyChat;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.util.ImageManager2;
/**
 * 我的聊天adapter
 * @author zhangmin
 *
 */
public class ListViewMyChatAdapter extends BaseAdapter
{
	private Context context;// 运行上下文
	private List<MyChat> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private Handler handler;
	private AppContext appContext ;
	static class ListItemView
	{ // 自定义控件集合
		public ImageView face;
		public TextView name; 
		public TextView message ; //消息
		public TextView time ;
		
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewMyChatAdapter(Context context, List<MyChat> data, int resource)
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
			listItemView.face = (ImageView) convertView.findViewById(R.id.chat_head);
			listItemView.name = (TextView) convertView.findViewById(R.id.chat_name);
			listItemView.message = (TextView) convertView.findViewById(R.id.last_chat_content);
			listItemView.time = (TextView) convertView.findViewById(R.id.chat_time);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else
		{
			listItemView = (ListItemView) convertView.getTag();
		}

		// 设置文字和图片
		final MyChat chat = listItems.get(position);
		String faceURL="";
		faceURL = chat.getImageUrl();
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
//				UIHelper.showUserInfo(context, chat);
			}
		}); //设置点击进入用户详情
		listItemView.face.setTag(URLs.HOST+faceURL);
		listItemView.name.setText(chat.getFriendName());
		listItemView.message.setText(chat.getLastMsg());
		listItemView.time.setText(StringUtils.friendly_time(StringUtils.dateToString(chat.getTime())));
		return convertView;
	}

	
}