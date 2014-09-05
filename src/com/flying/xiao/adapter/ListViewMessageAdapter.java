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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flying.xiao.R;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.entity.XMessage;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.util.ImageManager2;

/**
 * �ҵ�����Adapter��
 */
public class ListViewMessageAdapter extends BaseAdapter
{
	private Context context;// ����������
	private LayoutInflater listContainer;// ��ͼ����
	private int itemViewResource;// �Զ�������ͼԴ
	private List<XMessage> mainMessageList;
	private AppContext appContext;
	private LinearLayout layout;
	private IReplyMessage replyMessage;

	static class ListItemView
	{ // �Զ���ؼ�����
		public ImageView face;
		public TextView name;
		public TextView date;
		public TextView content;
		public LinearLayout relies;
		public TextView reply;
	}

	/**
	 * ʵ����Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewMessageAdapter(Context context, List<XMessage> data, int resource, LinearLayout layout,
			IReplyMessage replyMessage)
	{
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // ������ͼ����������������
		this.itemViewResource = resource;
		this.mainMessageList = data;
		this.layout = layout;
		this.replyMessage = replyMessage;
		appContext = (AppContext) ((Activity) context).getApplication();
	}

	@Override
	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
	}

	public int getCount()
	{
		return mainMessageList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent)
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
			listItemView.face = (ImageView) convertView.findViewById(R.id.my_message_listitem_userface);
			listItemView.name = (TextView) convertView.findViewById(R.id.my_message_listitem_username);
			listItemView.date = (TextView) convertView.findViewById(R.id.my_message_listitem_date);
			listItemView.content = (TextView) convertView.findViewById(R.id.my_message_listitem_content);
			listItemView.relies = (LinearLayout) convertView.findViewById(R.id.my_message_listitem_relies);
			listItemView.reply = (TextView) convertView.findViewById(R.id.my_message_listitem_comment);
			// ���ÿؼ�����convertView
			convertView.setTag(listItemView);
		} else
		{
			listItemView = (ListItemView) convertView.getTag();
		}

		// �������ֺ�ͼƬ
		XMessage message = mainMessageList.get(position);
		final XUserInfo userInfo = message.getUserInfoByMsgFromUserId().getUserName() == null ? appContext
				.getUserInfo() : message.getUserInfoByMsgFromUserId();
		String faceURL = userInfo.getUserHeadImageUrl();
		if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL))
		{
			listItemView.face.setImageResource(R.drawable.widget_dface);
		} else
		{
			ImageManager2.from(context).displayImage(listItemView.face, URLs.HOST + faceURL,
					R.drawable.widget_dface);
		}
		listItemView.face.setTag(URLs.HOST + faceURL);// �������ز���(ʵ����)
		listItemView.face.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				UIHelper.showUserInfo(context, userInfo);
			}
		});
		listItemView.reply.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				layout.setVisibility(View.VISIBLE);
				replyMessage.replyMessage(position);
			}
		});
		listItemView.name.setText(userInfo.getUserRealName());
		listItemView.date.setText(StringUtils.friendly_time(message.getMsgSendTime().toString()));
		listItemView.content.setText(message.getMsgInfo());
		listItemView.content.setTag(message);// �������ز���(ʵ����)

		listItemView.relies.setVisibility(View.GONE);
		listItemView.relies.removeAllViews();// �����

		List<XMessage> replies = message.getReplys();

		if (replies != null && replies.size() > 0)
		{
			// ������Ŀ
			View view = listContainer.inflate(R.layout.comment_reply, null);
			TextView tv = (TextView) view.findViewById(R.id.comment_reply_content);
			// tv.setText(context.getString(R.string.comment_reply_title,
			// replies.size()));
			// listItemView.relies.addView(view);
			// ��������
			for (XMessage reply : replies)
			{
				View view2 = listContainer.inflate(R.layout.comment_reply, null);
				TextView tv2 = (TextView) view2.findViewById(R.id.comment_reply_content);
				XUserInfo userReply = reply.getUserInfoByMsgFromUserId();
				if (userReply.getUserName() == null)
				{
					userReply = appContext.getUserInfo();
				}
				tv2.setText(userReply.getUserRealName() + "("
						+ StringUtils.friendly_time(reply.getMsgSendTime().toString()) + ")��"
						+ reply.getMsgInfo());
				listItemView.relies.addView(view2);
			}
			listItemView.relies.setVisibility(View.VISIBLE);
		}
		//
		// listItemView.refers.setVisibility(View.GONE);
		// listItemView.refers.removeAllViews();// �����
		// if (comment.getRefers().size() > 0)
		// {
		// // ��������
		// for (Refer refer : comment.getRefers())
		// {
		// View view = listContainer.inflate(R.layout.comment_refer, null);
		// TextView title = (TextView)
		// view.findViewById(R.id.comment_refer_title);
		// TextView body = (TextView)
		// view.findViewById(R.id.comment_refer_body);
		// title.setText(refer.refertitle);
		// body.setText(refer.referbody);
		// listItemView.refers.addView(view);
		// }
		// listItemView.refers.setVisibility(View.VISIBLE);
		// }

		return convertView;
	}

	public interface IReplyMessage
	{
		public void replyMessage(int id);
	}

}