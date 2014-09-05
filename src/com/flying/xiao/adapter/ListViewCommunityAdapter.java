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
 * 社区adapter
 * 
 * @author zhangmin
 *
 */
public class ListViewCommunityAdapter extends BaseAdapter
{
	private Context context;// 运行上下文
	private List<XUserInfo> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private Handler handler;
	private AppContext appContext;

	static class ListItemView
	{ // 自定义控件集合
		public ImageView face;
		public TextView name;
		public TextView summary; // 个人说明
		private ImageView collection; // 添加关注
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewCommunityAdapter(Context context, List<XUserInfo> data, int resource)
	{
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		appContext = (AppContext) ((Activity) context).getApplication();
	}

	@Override
	public void notifyDataSetChanged()
	{
		// 通过与我的还有比较 设置user列表是否是我的好友
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
	 * ListView Item设置
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		// 自定义视图
		final ListItemView listItemView;

		if (convertView == null)
		{
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.face = (ImageView) convertView.findViewById(R.id.community_listitem_userface);
			listItemView.name = (TextView) convertView.findViewById(R.id.community_listitem_title);
			listItemView.summary = (TextView) convertView.findViewById(R.id.community_listitem_Summary);
			listItemView.collection = (ImageView) convertView
					.findViewById(R.id.community_listitem_collection);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else
		{
			listItemView = (ListItemView) convertView.getTag();
		}

		// 设置文字和图片
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
		listItemView.face.setOnClickListener(faceClickListener); // 设置点击进入用户详情
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
					UIHelper.ToastMessage(context, "添加失败");
					listItemView.collection.setImageResource(R.drawable.head_favorite);
					break;
				case Constant.HandlerMessageCode.ADD_FRIEND_IS_YOUR_FRIEND_ALERADY:
					UIHelper.ToastMessage(context, "您已经添加他为好友啦,不能重复添加");
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
					UIHelper.ToastMessage(appContext, "添加好友失败,请重试...");
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
					UIHelper.ToastMessage(appContext, "添加好友成功");
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
	 * 点击头像 进入我的页面
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