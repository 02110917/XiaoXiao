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
 * �û�����Adapter��
 */
public class ListViewCommentAdapter extends BaseAdapter
{
	private Context context;// ����������
	private List<XComment> listItems;// ���ݼ���
	private LayoutInflater listContainer;// ��ͼ����
	private int itemViewResource;// �Զ�������ͼԴ
	private List<XComment> mainCommentList ;
	public List<XComment> getMainCommentList()
	{
		return mainCommentList;
	}

	static class ListItemView
	{ // �Զ���ؼ�����
		public ImageView face;
		public TextView name;
		public TextView date;
		public TextView content;
		public LinearLayout relies;
	}

	/**
	 * ʵ����Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewCommentAdapter(Context context, List<XComment> data, int resource)
	{	
		mainCommentList=new ArrayList<XComment>();
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // ������ͼ����������������
		this.itemViewResource = resource;
		this.listItems = data;
		splitData();
	}

	private void splitData(){
		for(XComment com:listItems){
			if(com.getReplyCommentId()==0){ //���ǻظ����˵� 
				mainCommentList.add(0,com);
			}
		}
		for(XComment mainCom:mainCommentList){
			listItems.remove(mainCom); //ʣ����ǻظ����˵�����
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
	 * ListView Item����
	 */
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// Log.d("method", "getView");

		// �Զ�����ͼ
		ListItemView listItemView = null;

		if (convertView == null)
		{
			// ��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// ��ȡ�ؼ�����
			listItemView.face = (ImageView) convertView.findViewById(R.id.comment_listitem_userface);
			listItemView.name = (TextView) convertView.findViewById(R.id.comment_listitem_username);
			listItemView.date = (TextView) convertView.findViewById(R.id.comment_listitem_date);
			listItemView.content = (TextView) convertView.findViewById(R.id.comment_listitem_content);
			listItemView.relies = (LinearLayout) convertView.findViewById(R.id.comment_listitem_relies);
			// ���ÿؼ�����convertView
			convertView.setTag(listItemView);
		} else
		{
			listItemView = (ListItemView) convertView.getTag();
		}

		// �������ֺ�ͼƬ
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
		listItemView.face.setTag(URLs.HOST+faceURL);// �������ز���(ʵ����)
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
		listItemView.content.setTag(comment);// �������ز���(ʵ����)
		
		listItemView.relies.setVisibility(View.GONE);
		listItemView.relies.removeAllViews();// �����
		
		List<XComment> replies=new ArrayList<XComment>();
		for(XComment reply:listItems){
			if(reply.getReplyCommentId()==comment.getPlId()){ //����лظ��Լ���ƽ· �ӽ���
				replies.add(reply);
			}
		}
		
		if (replies.size() > 0)
		{
			// ������Ŀ
			View view = listContainer.inflate(R.layout.comment_reply, null);
			TextView tv = (TextView) view.findViewById(R.id.comment_reply_content);
			tv.setText(context.getString(R.string.comment_reply_title, replies.size()));
			listItemView.relies.addView(view);
			// ��������
			for (XComment reply : replies)
			{
				View view2 = listContainer.inflate(R.layout.comment_reply, null);
				TextView tv2 = (TextView) view2.findViewById(R.id.comment_reply_content);
				tv2.setText(reply.getXuserInfo().getUserRealName() + "(" + StringUtils.friendly_time(reply.getPlTime().toString()) + ")��"
						+ reply.getPlInfo());
				listItemView.relies.addView(view2);
			}
			listItemView.relies.setVisibility(View.VISIBLE);
		}
//
//		listItemView.refers.setVisibility(View.GONE);
//		listItemView.refers.removeAllViews();// �����
//		if (comment.getRefers().size() > 0)
//		{
//			// ��������
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