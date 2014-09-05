package com.flying.xiao.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flying.xiao.BaseActivity;
import com.flying.xiao.MainActivity;
import com.flying.xiao.R;
import com.flying.xiao.SearchActivity;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.asmack.XmppControl;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.db.DBHelper;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.service.WebSocketService;
import com.flying.xiao.util.ImageManager2;

/**
 * ����adapter
 * 
 * @author zhangmin
 *
 */
public class ListViewCommunityAdapter extends BaseAdapter
{
	private Context context;// ����������
	private List<XUserInfo> listItems;// ���ݼ���
	private LayoutInflater listContainer;// ��ͼ����
	private int itemViewResource;// �Զ�������ͼԴ
	private Handler handler;
	private AppContext appContext;

	static class ListItemView
	{ // �Զ���ؼ�����
		public ImageView face;
		public TextView name;
		public TextView summary; // ����˵��
		private ImageView collection; // ��ӹ�ע
	}

	/**
	 * ʵ����Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewCommunityAdapter(Context context, List<XUserInfo> data, int resource)
	{
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // ������ͼ����������������
		this.itemViewResource = resource;
		this.listItems = data;
		appContext = (AppContext) ((Activity) context).getApplication();
	}

	@Override
	public void notifyDataSetChanged()
	{
		// ͨ�����ҵĻ��бȽ� ����user�б��Ƿ����ҵĺ���
		for (XUserInfo user : listItems)
		{
			if (DBHelper.getDbHelper(context).isHaveThisUser(user.getId()))
			{
				user.setMeFriend(true);
			}
		}
		super.notifyDataSetChanged();

	}

	private void setUsersState()
	{

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
	 * ListView Item����
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		// �Զ�����ͼ
		final ListItemView listItemView;

		if (convertView == null)
		{
			// ��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// ��ȡ�ؼ�����
			listItemView.face = (ImageView) convertView.findViewById(R.id.community_listitem_userface);
			listItemView.name = (TextView) convertView.findViewById(R.id.community_listitem_title);
			listItemView.summary = (TextView) convertView.findViewById(R.id.community_listitem_Summary);
			listItemView.collection = (ImageView) convertView
					.findViewById(R.id.community_listitem_collection);
			// ���ÿؼ�����convertView
			convertView.setTag(listItemView);
		} else
		{
			listItemView = (ListItemView) convertView.getTag();
		}

		// �������ֺ�ͼƬ
		final XUserInfo userInfo = listItems.get(position);
		String faceURL = userInfo.getUserHeadImageUrl();
		System.out.println("image url---" + faceURL);
		if (faceURL.endsWith(".gif") || StringUtils.isEmpty(faceURL))
		{
			listItemView.face.setImageResource(R.drawable.widget_dface);
		} else
		{
			ImageManager2.from(context).displayImage(listItemView.face, URLs.HOST + faceURL,
					R.drawable.widget_dface);
		}
		listItemView.face.setOnClickListener(faceClickListener); // ���õ�������û�����
		listItemView.face.setTag(URLs.HOST + faceURL);
		listItemView.name.setText(userInfo.getUserRealName());
		listItemView.summary.setText(userInfo.getUserGerenshuoming());
		if (userInfo.isMeFriend())
		{
			listItemView.collection.setImageResource(R.drawable.head_favorite_y);
		}
		handler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					UIHelper.ToastMessage(context, R.string.user_login_out_of_date);
					listItemView.collection.setImageResource(R.drawable.head_favorite);
					UIHelper.showLoginDialog(context);
					break;
				case Constant.HandlerMessageCode.ADD_FRIEND_FAIL:
					UIHelper.ToastMessage(context, "���ʧ��");
					listItemView.collection.setImageResource(R.drawable.head_favorite);
					break;
				case Constant.HandlerMessageCode.ADD_FRIEND_IS_YOUR_FRIEND_ALERADY:
					UIHelper.ToastMessage(context, "���Ѿ������Ϊ������,�����ظ����");
					listItemView.collection.setImageResource(R.drawable.head_favorite_y);
					break;
				case Constant.HandlerMessageCode.ADD_FRIEND_SUCCESS:
					listItemView.collection.setImageResource(R.drawable.head_favorite_y);
					listItems.get(position).setMeFriend(true);
					notifyDataSetChanged();
					break;
				case Constant.XmppHandlerMsgCode.HANDLER_ADD_PRIEND_FAILD: // xmpp
																			// add
																			// friend
																			// faild
					UIHelper.ToastMessage(appContext, "��Ӻ���ʧ��,������...");
					// listItemView.collection.setImageResource(R.drawable.head_favorite);
					break;
				default:
					break;
				}
			}
		};

		listItemView.collection.setOnClickListener(new MyCollectListener(position));
		return convertView;
	}

	private class MyCollectListener implements OnClickListener
	{
		private int position;

		public MyCollectListener(int position)
		{
			this.position = position;
		}

		private Handler mHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				if (msg.what == Constant.XmppHandlerMsgCode.HANDLER_ADD_PRIEND_SUCCESS)
				{
					UIHelper.ToastMessage(appContext, "��Ӻ��ѳɹ�");
					// listItemView.collection.setImageResource(R.drawable.head_favorite_y);
					// listItems.get(clickposition).setMeFriend(true);
					DBHelper.getDbHelper(context).insertFriend(listItems.get(position));
					notifyDataSetChanged();
				}
			}
		};

		@Override
		public void onClick(View v)
		{
			if (!appContext.isLogin())
			{
				handler.sendEmptyMessage(Constant.HandlerMessageCode.USER_NOT_LOGIN);
				return;
			}
			XUserInfo userInfo = listItems.get(position);
			if (userInfo.isMeFriend())
			{
				handler.sendEmptyMessage(Constant.HandlerMessageCode.ADD_FRIEND_IS_YOUR_FRIEND_ALERADY);
				return;
			}
			// if((Activity)context instanceof MainActivity||(Activity)context
			// instanceof SearchActivity){
			// ((BaseActivity)context).getmWebSocketService().addFriend(userInfo.getUserName(),
			// userInfo.getUserRealName());
			// }
			if (((BaseActivity) context).getmWebSocketService().isXmppLogin())
				XmppControl.getShare(context).addFriend(userInfo.getUserName(), userInfo.getUserRealName(),
						mHandler);
			// NetControl.getShare(context).addFriend(userInfo.getId(),
			// handler);
		}

	}

	/**
	 * ���ͷ�� �����ҵ�ҳ��
	 */
	private View.OnClickListener faceClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// Post post = (Post) v.getTag();
			// UIHelper.showUserCenter(v.getContext(), post.getAuthorId(),
			// post.getAuthor());
		}
	};
}