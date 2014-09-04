package com.flying.xiao.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flying.xiao.R;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.util.ImageManager2;

public class ListViewMainContentAdapter extends BaseAdapter
{
	private Context context;// ����������
	private List<XContent> listItems;// ���ݼ���
	private LayoutInflater listContainer;// ��ͼ����
	private int itemViewResource;// �Զ�������ͼԴ
	private boolean isMarket ;
	static class ListItemView
	{ // �Զ���ؼ�����
		public ImageView face;
		public TextView title;
		public TextView author;
		public TextView date;
		public TextView count;
		public TextView summary ; //����ժҪ
		public TextView price ;
	}

	/**
	 * ʵ����Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewMainContentAdapter(Context context, List<XContent> data, int resource)
	{
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // ������ͼ����������������
		this.itemViewResource = resource;
		this.listItems = data;
		isMarket=false;
	}
	public ListViewMainContentAdapter(Context context, List<XContent> data, int resource,boolean isMarket){
		this(context, data, resource);
		this.isMarket=isMarket;
	}
	@Override
	public int getCount()
	{
		if(listItems!=null)
			return listItems.size();
		return 0;
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
	 * ListView Item����
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// �Զ�����ͼ
		ListItemView listItemView = null;

		if (convertView == null)
		{
			// ��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			// ��ȡ�ؼ�����
			listItemView.face = (ImageView) convertView.findViewById(R.id.main_listitem_userface);
			listItemView.title = (TextView) convertView.findViewById(R.id.main_listitem_title);
			listItemView.author = (TextView) convertView.findViewById(R.id.main_listitem_author);
			listItemView.date = (TextView) convertView.findViewById(R.id.main_listitem_date);
			listItemView.summary = (TextView) convertView.findViewById(R.id.main_listitem_Summary);
			if(isMarket)
				listItemView.price = (TextView) convertView.findViewById(R.id.main_listitem_price);
			else
				listItemView.count = (TextView) convertView.findViewById(R.id.main_listitem_count);
			// ���ÿؼ�����convertView
			convertView.setTag(listItemView);
		} else
		{
			listItemView = (ListItemView) convertView.getTag();
		}

		// �������ֺ�ͼƬ
		final XContent con = listItems.get(position);
		String faceURL = con.getConImageUrl()==null?"":con.getConImageUrl();
		System.out.println("image url---"+faceURL);
		if (faceURL.endsWith(".gif") || StringUtils.isEmpty(faceURL))
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
				UIHelper.showUserInfo(context, con.getUserInfo());
			}
		}); //���õ�������û�����
		listItemView.face.setTag(URLs.HOST+faceURL);
		listItemView.author.setText(con.getUserInfo().getUserRealName());
		listItemView.title.setText(con.getConTitle());
		if(con.getConTypeId()==Constant.ContentType.CONTENT_TYPE_LOST){
			if(con.isLost()){
				listItemView.title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lost, 0, 0, 0);
			}else{
				listItemView.title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.get, 0, 0, 0);
			}
		}
		listItemView.title.setTag(con);// �������ز���(ʵ����)
		
		listItemView.date.setText(StringUtils.friendly_time(con.getConPubTime().toString()));
		if(isMarket)
			listItemView.price.setText(con.getPrice()+"Ԫ");
		else
			listItemView.count.setText(con.getConPls() + "��|" + con.getConHot() + "��");
		if(con.getConSummary()!=null&&(!con.getConSummary().equals(""))){
			listItemView.summary.setVisibility(View.VISIBLE);
			listItemView.summary.setText(con.getConSummary());
		}else{
			listItemView.summary.setVisibility(View.GONE);
		}
		return convertView;
	}

//	private View.OnClickListener faceClickListener = new View.OnClickListener()
//	{
//		
//		@Override
//		public void onClick(View v)
//		{
//			UIHelper.showMyHome(context, userInfo);
////			Post post = (Post) v.getTag();
////			UIHelper.showUserCenter(v.getContext(), post.getAuthorId(), post.getAuthor());
//		}
//	};
}