package com.flying.xiao.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flying.xiao.R;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.entity.XComment;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.util.ImageManager2;

/**
 * 用户评论Adapter类
 */
public class ListViewCommentAdapter extends BaseAdapter
{
	private Context context;// 运行上下文
	private List<XComment> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private List<XComment> mainCommentList ;
	public List<XComment> getMainCommentList()
	{
		return mainCommentList;
	}

	static class ListItemView
	{ // 自定义控件集合
		public ImageView face;
		public TextView name;
		public TextView date;
		public TextView content;
		public LinearLayout relies;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewCommentAdapter(Context context, List<XComment> data, int resource)
	{	
		mainCommentList=new ArrayList<XComment>();
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		splitData();
	}

	private void splitData(){
		for(XComment com:listItems){
			if(com.getReplyCommentId()==0){ //不是回复别人的 
				mainCommentList.add(0,com);
			}
		}
		for(XComment mainCom:mainCommentList){
			listItems.remove(mainCom); //剩余的是回复别人的评论
		}
	}
	@Override
	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
		splitData();
	}

	public int getCount()
	{
		return mainCommentList.size();
	}

	public Object getItem(int arg0)
	{
		return null;
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

		// 自定义视图
		ListItemView listItemView = null;

		if (convertView == null)
		{
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.face = (ImageView) convertView.findViewById(R.id.comment_listitem_userface);
			listItemView.name = (TextView) convertView.findViewById(R.id.comment_listitem_username);
			listItemView.date = (TextView) convertView.findViewById(R.id.comment_listitem_date);
			listItemView.content = (TextView) convertView.findViewById(R.id.comment_listitem_content);
			listItemView.relies = (LinearLayout) convertView.findViewById(R.id.comment_listitem_relies);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else
		{
			listItemView = (ListItemView) convertView.getTag();
		}

		// 设置文字和图片
		XComment comment = mainCommentList.get(position);
		final XUserInfo userInfo=comment.getXuserInfo();
		String faceURL = userInfo.getUserHeadImageUrl();
		if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL))
		{
			listItemView.face.setImageResource(R.drawable.widget_dface);
		} else
		{
			ImageManager2.from(context).displayImage(listItemView.face, URLs.HOST+faceURL, R.drawable.widget_dface);
		}
		listItemView.face.setTag(URLs.HOST+faceURL);// 设置隐藏参数(实体类)
		listItemView.face.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				UIHelper.showUserInfo(context, userInfo);
			}
		});
		listItemView.name.setText(userInfo.getUserRealName());
		listItemView.date.setText(StringUtils.friendly_time(comment.getPlTime().toString()));
		listItemView.content.setText(comment.getPlInfo());
		listItemView.content.setTag(comment);// 设置隐藏参数(实体类)
		
		listItemView.relies.setVisibility(View.GONE);
		listItemView.relies.removeAllViews();// 先清空
		
		List<XComment> replies=new ArrayList<XComment>();
		for(XComment reply:listItems){
			if(reply.getReplyCommentId()==comment.getPlId()){ //如果有回复自己的平路 加进来
				replies.add(reply);
			}
		}
		
		if (replies.size() > 0)
		{
			// 评论数目
			View view = listContainer.inflate(R.layout.comment_reply, null);
			TextView tv = (TextView) view.findViewById(R.id.comment_reply_content);
			tv.setText(context.getString(R.string.comment_reply_title, replies.size()));
			listItemView.relies.addView(view);
			// 评论内容
			for (XComment reply : replies)
			{
				View view2 = listContainer.inflate(R.layout.comment_reply, null);
				TextView tv2 = (TextView) view2.findViewById(R.id.comment_reply_content);
				tv2.setText(reply.getXuserInfo().getUserRealName() + "(" + StringUtils.friendly_time(reply.getPlTime().toString()) + ")："
						+ reply.getPlInfo());
				listItemView.relies.addView(view2);
			}
			listItemView.relies.setVisibility(View.VISIBLE);
		}
//
//		listItemView.refers.setVisibility(View.GONE);
//		listItemView.refers.removeAllViews();// 先清空
//		if (comment.getRefers().size() > 0)
//		{
//			// 引用内容
//			for (Refer refer : comment.getRefers())
//			{
//				View view = listContainer.inflate(R.layout.comment_refer, null);
//				TextView title = (TextView) view.findViewById(R.id.comment_refer_title);
//				TextView body = (TextView) view.findViewById(R.id.comment_refer_body);
//				title.setText(refer.refertitle);
//				body.setText(refer.referbody);
//				listItemView.refers.addView(view);
//			}
//			listItemView.refers.setVisibility(View.VISIBLE);
//		}

		return convertView;
	}

}