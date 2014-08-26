package com.flying.xiao.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flying.xiao.R;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XComment;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.entity.XImage;
import com.flying.xiao.entity.XPraise;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.util.ImageManager2;

public class ListViewMainDiaryAdapter extends BaseAdapter
{
	private Context context;// 运行上下文
	private List<XContent> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private View pubcommentLin;
	private OnPubBtnClickListener pubBtnListener;
	private OnRePubCommentCliclListener rePubListener;

	public void setRePubListener(OnRePubCommentCliclListener rePubListener)
	{
		this.rePubListener = rePubListener;
	}

	public void setPubBtnListener(OnPubBtnClickListener pubBtnListener)
	{
		this.pubBtnListener = pubBtnListener;
	}

	static class ListItemView
	{ // 自定义控件集合
		public ImageView userface;
		public TextView username;
		public TextView date;
		public TextView content;
		public TextView client;
		public TextView btncomment;
		public GridView gridView;
		public LinearLayout relies;
		public LinearLayout praiseLin;
		public ImageView praiseImage;
		public TextView praiseText;

	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 * @param pubcommentLin
	 */
	public ListViewMainDiaryAdapter(Context context, View pubcommentLin, List<XContent> data, int resource)
	{
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		this.pubcommentLin = pubcommentLin;
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
	 * ListView Item设置
	 */
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		// Log.d("method", "getView");

		// 自定义视图
		final ListItemView listItemView;
		final AppContext ac = (AppContext) context.getApplicationContext();
		GridImageAdapter gridImageAdapter;
		if (convertView == null)
		{
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.userface = (ImageView) convertView.findViewById(R.id.diary_listitem_userface);
			listItemView.username = (TextView) convertView.findViewById(R.id.diary_listitem_username);
			listItemView.content = (TextView) convertView.findViewById(R.id.diary_listitem_content);
			listItemView.gridView = (GridView) convertView.findViewById(R.id.diary_listitem_grid);
			listItemView.date = (TextView) convertView.findViewById(R.id.diary_listitem_date);
			listItemView.btncomment = (TextView) convertView.findViewById(R.id.diary_listitem_comment);
			listItemView.relies = (LinearLayout) convertView.findViewById(R.id.diary_listitem_relies);
			listItemView.praiseLin = (LinearLayout) convertView.findViewById(R.id.diary_listitem_praise_lin);
			listItemView.praiseImage = (ImageView) convertView.findViewById(R.id.diary_listitem_praise_image);
			listItemView.praiseText = (TextView) convertView.findViewById(R.id.diary_listitem_praise);
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else
		{
			listItemView = (ListItemView) convertView.getTag();
		}

		// 设置文字和图片
		final XContent con = listItems.get(position);
		List<XImage> ilist=con.getImages();
		int size=0;
		if(ilist!=null&&(size=ilist.size())>0){
			ArrayList<String>  imageList=new ArrayList<String>();
			listItemView.gridView.setVisibility(View.VISIBLE);
			for(XImage image:ilist){
				imageList.add(URLs.HOST+image.getImageUrl());
			}
			gridImageAdapter = new GridImageAdapter(context, imageList);
//			if(size<=3){
//				listItemView.gridView.setNumColumns(size);
//			}else{
//				listItemView.gridView.setNumColumns(3);
//			}
			listItemView.gridView.setAdapter(gridImageAdapter);
		}else{
			listItemView.gridView.setVisibility(View.GONE);
		}
		listItemView.username.setText(con.getUserInfo().getUserRealName());
		listItemView.username.setTag(con);// 设置隐藏参数(实体类)

		listItemView.content.setText(con.getConTitle());
		listItemView.content.setTag(con);// 设置隐藏参数(实体类)
		// listItemView.content.setOnClickListener(linkViewClickListener);

		listItemView.date.setText(StringUtils.friendly_time(con.getConPubTime().toString()));
		String faceURL = con.getConImageUrl();
		if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL))
		{
			listItemView.userface.setImageResource(R.drawable.widget_dface);
		} else
		{
			ImageManager2.from(context).displayImage(listItemView.userface, URLs.HOST + faceURL,
					R.drawable.widget_dface);
		}
		listItemView.userface.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				UIHelper.showUserInfo(context, con.getUserInfo());
				
			}
		});
		listItemView.userface.setTag(URLs.HOST + faceURL);
		listItemView.relies.setVisibility(View.GONE);
		listItemView.relies.removeAllViews();// 先清空
		listItemView.btncomment.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (ac.isLogin())
				{
					pubcommentLin.setVisibility(View.VISIBLE);
					if (pubBtnListener != null)
						pubBtnListener.onPubCommentBtnClick(position);
				} else
				{
					UIHelper.ToastMessage(context, "您还未登陆,请先登录...");
					UIHelper.showLoginDialog(context);
				}
			}
		});
		final List<XPraise> xpraises = con.getPraiseList();
		if (xpraises != null && xpraises.size() > 0)
		{
			View view2 = listContainer.inflate(R.layout.comment_reply, null);
			TextView tv2 = (TextView) view2.findViewById(R.id.comment_reply_content);
			tv2.setCompoundDrawablesWithIntrinsicBounds(
					context.getResources().getDrawable(R.drawable.widget_comment_count_icon), null, null,
					null);
			tv2.setClickable(true);
			tv2.setMovementMethod(LinkMovementMethod.getInstance());
			int len = xpraises.size();
			for (int i = 0; i < len; i++)
			{
				XPraise xp = xpraises.get(i);

//				if (ac.isLogin() && xp.getUserInfo().getId().equals(ac.getUserInfo().getId()))
//				{
//					con.setMeIsPraise(true);
//				}

				tv2.append(UIHelper.getClickableSpan(context, xp.getUserInfo()));
				if (i != len - 1)
				{
					tv2.append("、");
				}
				// tv2.setText(xp.getUserInfo().getUserRealName()+"赞");
			}
			tv2.append("  觉得很赞");
			listItemView.relies.addView(view2);
			listItemView.relies.setVisibility(View.VISIBLE);
		}
		if (con.isMeIsPraise())
		{
			listItemView.praiseText.setText("已赞");
			listItemView.praiseImage.setImageResource(R.drawable.praise_already);
		} else
		{
			listItemView.praiseText.setText("赞");
			listItemView.praiseImage.setImageResource(R.drawable.praise);
		}
		final Handler handler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				XPraise xp;
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.PRAISE_OPERATE_SUCCESS:
					xp = (XPraise) msg.obj;
					if (!con.isMeIsPraise())
					{
						for (XPraise x : con.getPraiseList())
						{
							if (x.getUserInfo().getId().equals(xp.getUserInfo().getId()))
							{
								x.setId(xp.getId());
							}
						}
						con.setMeIsPraise(true);
					} else
					{
						con.setMeIsPraise(false);
					}
					notifyDataSetChanged();
					break;
				case Constant.HandlerMessageCode.PRAISE_OPERATE_ERROR:
					xp = (XPraise) msg.obj;
					if (con.isMeIsPraise())
					{
						con.getPraiseList().add(xp);
					} else
					{
						con.getPraiseList().remove(xp);
					}
					UIHelper.ToastMessage(context, "操作失败...");
					notifyDataSetChanged();
					break;
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					xp = (XPraise) msg.obj;
					con.getPraiseList().remove(xp);
					UIHelper.ToastMessage(context, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(context);
					notifyDataSetChanged();
					break;
				default:
					break;

				}
			}
		};
		listItemView.praiseLin.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (ac.isLogin())
				{
					XPraise praise = new XPraise();
					if (con.isMeIsPraise())
					{ // 如果已经赞了
						for (XPraise xp : xpraises)
						{
							if (ac.isLogin() && xp.getUserInfo().getId().equals(ac.getUserInfo().getId()))
							{
								praise.copy(xp);
								con.getPraiseList().remove(xp);
								break;
							}
						}
					} else
					{ // 没有赞
						praise.setUserInfo(ac.getUserInfo());
						praise.setContentId(con.getId());
						if (con.getPraiseList() == null)
						{
							List<XPraise> list = new ArrayList<XPraise>();
							con.setPraiseList(list);
						}
						con.getPraiseList().add(0, praise);
					}
					NetControl.getShare(context).praiseOpreate(praise, con.isMeIsPraise(), handler);
					notifyDataSetChanged();
				} else
				{
					UIHelper.ToastMessage(context, "您还未登陆,请先登录...");
					UIHelper.showLoginDialog(context);
				}
			}
		});
		List<XComment> listComments = con.getComments();
		if (listComments != null && listComments.size() > 0)
		{
			// 评论内容
			for (final XComment reply : listComments)
			{
				View view2 = listContainer.inflate(R.layout.comment_reply, null);
				TextView tv2 = (TextView) view2.findViewById(R.id.comment_reply_content);
				// String text = "";
				if (reply.getReplyCommentId() == 0)
				{ // 不是回复别人的
					tv2.append(UIHelper.getClickableSpan(context, reply.getXuserInfo()));
					tv2.append("(" + StringUtils.friendly_time(reply.getPlTime().toString()) + ")：");
					// text = reply.getXuserInfo().getUserRealName() + "("
					// + StringUtils.friendly_time(reply.getPlTime().toString())
					// + ")：";
				} else
				{
					long recommentid = reply.getReplyCommentId();
					XUserInfo reUser = null;
					for (XComment r : listComments)
					{
						if (r.getPlId() == recommentid)
						{
							reUser = r.getXuserInfo();
							break;
						}
					}
					tv2.append(UIHelper.getClickableSpan(context, reply.getXuserInfo()));
					tv2.append("(" + StringUtils.friendly_time(reply.getPlTime().toString()) + ")回复");
					if (reUser != null)
						tv2.append(UIHelper.getClickableSpan(context, reUser));
					tv2.append(":");
					// text=reply.getXuserInfo().getUserRealName()+"("+StringUtils.friendly_time(reply.getPlTime().toString())+")回复"+(reUser==null?"":reUser.getUserRealName())+":";
				}
				tv2.append(reply.getPlInfo());
				// text+=reply.getPlInfo();
				// tv2.setText(text);
				listItemView.relies.addView(view2);
				tv2.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						if (ac.isLogin())
						{
							pubcommentLin.setVisibility(View.VISIBLE);
							if (rePubListener != null)
								rePubListener.onReCommentClick(position, reply.getPlId());
						} else
						{
							UIHelper.ToastMessage(context, "您还未登陆,请先登录...");
							UIHelper.showLoginDialog(context);
						}
					}
				});
			}

			listItemView.relies.setVisibility(View.VISIBLE);
		}
		// String imgSmall = tweet.getImgSmall();
		// if(!StringUtils.isEmpty(imgSmall)) {
		// bmpManager.loadBitmap(imgSmall, listItemView.image,
		// BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.image_loading));
		// listItemView.image.setOnClickListener(imageClickListener);
		// listItemView.image.setTag(tweet.getImgBig());
		// listItemView.image.setVisibility(ImageView.VISIBLE);
		// }else{
		// listItemView.image.setVisibility(ImageView.GONE);
		// }

		return convertView;
	}

//	private View.OnClickListener faceClickListener = new View.OnClickListener()
//	{
//		public void onClick(View v)
//		{
//			XContent con = (XContent) v.getTag();
//			// UIHelper.showUserCenter(v.getContext(), tweet.getAuthorId(),
//			// tweet.getAuthor());
//		}
//	};

	// private View.OnClickListener imageClickListener = new
	// View.OnClickListener(){
	// public void onClick(View v) {
	// UIHelper.showImageDialog(v.getContext(), (String)v.getTag());
	// }
	// };
	public interface OnPubBtnClickListener
	{
		public void onPubCommentBtnClick(int position);
	}

	public interface OnRePubCommentCliclListener
	{
		public void onReCommentClick(int position, long commentId);
	}
}