package com.flying.xiao.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flying.xiao.R;
import com.flying.xiao.adapter.ListViewMainContentAdapter.ListItemView;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.db.DBHelper;
import com.flying.xiao.entity.ChatMessage;
import com.flying.xiao.util.ImageManager2;

/**
 * 聊天界面dapter类
 */
public class ListViewChatAdapter extends BaseAdapter
{
	private Context context;// 运行上下文
	private List<ChatMessage> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private DBHelper dbHelper;
	private AppContext appContext;

	static class ListItemView
	{ // 自定义控件集合
		public ImageView face;
		public TextView message;
		public TextView date;
		public ProgressBar progress;
		public ImageView messageSendFail;
		// public TextView content;
		// public LinearLayout relies;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewChatAdapter(Context context, List<ChatMessage> data)
	{
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = data;
		appContext = (AppContext) ((Activity) context).getApplication();
		dbHelper = DBHelper.getDbHelper(context);
	}

	public int getCount()
	{
		return listItems.size();
	}

	public Object getItem(int arg0)
	{
		return listItems.get(arg0);
	}

	public long getItemId(int arg0)
	{
		return 0;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// Log.d("method", "getView");
		final ChatMessage chatMessage = listItems.get(position);
		ChatMessage chatMessageBefore =null;
		if(position>=1){
			chatMessageBefore = listItems.get(position-1);
		}
		// 自定义视图
		ListItemView listItemView = null;
		
//		if(convertView==null){
	
		// 获取list_item布局文件的视图
		if (chatMessage.isTo())
			convertView = listContainer.inflate(R.layout.chatting_item_to, null);
		else
			convertView = listContainer.inflate(R.layout.chatting_item_from, null);

		listItemView = new ListItemView();
		// 获取控件对象
		listItemView.face = (ImageView) convertView.findViewById(R.id.chatting_avatar_iv);
		listItemView.message = (TextView) convertView.findViewById(R.id.chatting_content_itv);
		listItemView.date = (TextView) convertView.findViewById(R.id.chat_item_date);
		listItemView.progress = (ProgressBar) convertView.findViewById(R.id.uploading_pb);
		listItemView.messageSendFail = (ImageView) convertView.findViewById(R.id.msg_send_fail);
		// 设置控件集到convertView
//		convertView.setTag(listItemView);
//		}else{
//			listItemView = (ListItemView) convertView.getTag();
//		}

		String faceURL = chatMessage.getUserImageHeadUrl();

		if(chatMessage.isTo())
		{
			//	err send  P  E
			//	F	F	  V	 G
			//	F	T	  G	 G
			//	T	F	  G	 V
			//	T	T	  G	 G
			//只要isSendTo=true 	progress messageSendFail  都gone	
			if ( chatMessage.isSendTo())
			{ // 信息已送达
				listItemView.progress.setVisibility(View.GONE);
				listItemView.messageSendFail.setVisibility(View.GONE);
			} 
			else if(chatMessage.isSendError()&&!chatMessage.isSendTo()){
				listItemView.progress.setVisibility(View.GONE);
				listItemView.messageSendFail.setVisibility(View.VISIBLE);
			}else{
				listItemView.messageSendFail.setVisibility(View.GONE);
				listItemView.progress.setVisibility(View.VISIBLE);
			}
		}
		if (faceURL==null||faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL))
		{
			listItemView.face.setImageResource(R.drawable.mini_avatar_shadow);
		} else
		{
			ImageManager2.from(context).displayImage(listItemView.face, URLs.HOST + faceURL,
					R.drawable.mini_avatar_shadow);
		}
		listItemView.face.setTag(URLs.HOST + faceURL);// 设置隐藏参数(实体类)
		listItemView.face.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				UIHelper.showUserInfo(context, dbHelper.getUserInfoByUserName(chatMessage.getFrom()));
			}
		});
		listItemView.message.setText(chatMessage.getBody());
		
		if(chatMessageBefore!=null){
			long timeBefore = chatMessageBefore.getTime().getTime();
			long time = chatMessage.getTime().getTime();
			if(time-timeBefore>600000){
				listItemView.date.setText(StringUtils.friendly_time(StringUtils.dateToString(chatMessage.getTime())));
			}
			else{
				listItemView.date.setText("");
			}
		}else{
			listItemView.date.setText(StringUtils.friendly_time(StringUtils.dateToString(chatMessage.getTime())));
		}
		
		// listItemView.content.setText(comment.getPlInfo());
		// listItemView.content.setTag(comment);// 设置隐藏参数(实体类)
		//
		// listItemView.relies.setVisibility(View.GONE);
		// listItemView.relies.removeAllViews();// 先清空

		return convertView;
	}

}