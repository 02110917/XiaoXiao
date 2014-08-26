package com.flying.xiao.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
import com.flying.xiao.constant.Constant;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.entity.XDynamic;
import com.flying.xiao.entity.XDynamicConComment;
import com.flying.xiao.entity.XDynamicPraise;
import com.flying.xiao.util.ImageManager2;

public class ListViewMyDynamicAdapter extends BaseAdapter
{
	private Context context;// ����������
	private List<XDynamic> listItems;// ���ݼ���
	private LayoutInflater listContainer;// ��ͼ����
	private int itemViewResource;// �Զ�������ͼԴ
	private View pubcommentLin;
	private IReply reply ;
	static class ListItemView
	{ // �Զ���ؼ�����
		public ImageView userface;
		public TextView username;
		public TextView date;
		public TextView content;
		public TextView btncomment;
		public TextView content_textview;
		public LinearLayout content_lin;
		public TextView dynamicType;

	}

	/**
	 * ʵ����Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 * @param pubcommentLin
	 */
	public ListViewMyDynamicAdapter(Context context, View pubcommentLin, List<XDynamic> data, int resource,IReply reply)
	{
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // ������ͼ����������������
		this.itemViewResource = resource;
		this.listItems = data;
		this.pubcommentLin = pubcommentLin;
		this.reply=reply;
	}

	public int getCount()
	{
		return listItems.size();
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
		final ListItemView listItemView;
		final AppContext ac = (AppContext) context.getApplicationContext();
		if (convertView == null)
		{
			// ��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// ��ȡ�ؼ�����
			listItemView.userface = (ImageView) convertView.findViewById(R.id.my_dynamic_listitem_userface);
			listItemView.username = (TextView) convertView.findViewById(R.id.my_dynamic_listitem_username);
			listItemView.content = (TextView) convertView.findViewById(R.id.my_dynamic_listitem_content);
			listItemView.date = (TextView) convertView.findViewById(R.id.my_dynamic_listitem_date);
			listItemView.btncomment = (TextView) convertView.findViewById(R.id.my_dynamic_listitem_comment);
			listItemView.content_textview = (TextView) convertView.findViewById(R.id.my_dynamic_listitem_content_textview);
			listItemView.dynamicType = (TextView) convertView.findViewById(R.id.my_dynamic_listitem_type);
			listItemView.content_lin = (LinearLayout) convertView.findViewById(R.id.my_dynamic_listitem_content_lin);
			// ���ÿؼ�����convertView
			convertView.setTag(listItemView);
		} else
		{
			listItemView = (ListItemView) convertView.getTag();
		}

		// �������ֺ�ͼƬ
		final XDynamic dynamic = listItems.get(position);
		listItemView.username.setText(dynamic.getUserInfo().getUserRealName());
		listItemView.username.setTag(dynamic);// �������ز���(ʵ����)


		listItemView.date.setText(StringUtils.friendly_time(dynamic.getTime().toString()));
		String faceURL = dynamic.getUserInfo().getUserHeadImageUrl();
		if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL))
		{
			listItemView.userface.setImageResource(R.drawable.widget_dface);
		} else
		{
			ImageManager2.from(context).displayImage(listItemView.userface, URLs.HOST + faceURL,
					R.drawable.widget_dface);
		}
		XContent content=dynamic.getContent();
		switch (dynamic.getType())
		{
		case Constant.DynamicType.DYNAMIC_TYPE_PRAISE_ME:
			listItemView.content_lin.setVisibility(View.VISIBLE);
			listItemView.content.setVisibility(View.GONE);
			listItemView.content_textview.setText(content.getUserInfo().getUserRealName()+" :" +content.getConTitle());
			listItemView.dynamicType.setText("������");
			break;
		case Constant.DynamicType.DYNAMIC_TYPE_CONTENT_COMMENT:
			listItemView.content_lin.setVisibility(View.VISIBLE);
			listItemView.content.setVisibility(View.VISIBLE);
			
			listItemView.content_textview.setText(content.getUserInfo().getUserRealName()+" :" +content.getConTitle());
			listItemView.content.setText(dynamic.getMsg());
			listItemView.dynamicType.setText("���һظ�");
			break ;
		case Constant.DynamicType.DYNAMIC_TYPE_MESSAGE:
			listItemView.content.setVisibility(View.VISIBLE);
			listItemView.content_lin.setVisibility(View.GONE);
			listItemView.content.setText(dynamic.getMsg());
			listItemView.dynamicType.setText("��������");
			break ;
		default:
			break;
		}
		
		listItemView.userface.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				UIHelper.showUserInfo(context, dynamic.getUserInfo());
				
			}
		});
		listItemView.userface.setTag(URLs.HOST + faceURL);
		listItemView.btncomment.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (ac.isLogin())
				{
					pubcommentLin.setVisibility(View.VISIBLE);
					if (reply != null)
						reply.reply(position);
				} else
				{
					UIHelper.ToastMessage(context, "����δ��½,���ȵ�¼...");
					UIHelper.showLoginDialog(context);
				}
			}
		});
		final Handler handler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
			}
		};

		return convertView;
	}

	public interface IReply{
		public void reply(int id);
	}
}