package com.flying.xiao;

import java.util.ArrayList;
import java.util.List;

import com.flying.xiao.adapter.GridImageAdapter;
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

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 内容详情
 */
public class DiaryDetail extends BaseActivity{

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
	
	private LinearLayout pubcommentLin;
	private GridImageAdapter gridImageAdapter;
	private List<XPraise> xpraises;
	private XContent con;
	
	private Context context ;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_detail);
		context=this;
		if(getIntent().getSerializableExtra("contentObject")!=null){
			con=(XContent) getIntent().getSerializableExtra("contentObject");
		}
		else
		{
			int index=getIntent().getIntExtra("content", 0);
			con = appContext.listManager.getDiaryContentList().get(index);
		}
		mHandler = new Handler()
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
					break;
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					xp = (XPraise) msg.obj;
					con.getPraiseList().remove(xp);
					UIHelper.ToastMessage(context, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(context);
					break;
				default:
					break;

				}
			}
		};
		
		initHeadView();
		initView();
	}

	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadRightView.setVisibility(View.GONE);
		mHeadTitle.setText("新鲜事");
	}
	
	private void initView(){
		pubcommentLin=(LinearLayout)findViewById(R.id.diary_footer);
		userface = (ImageView) findViewById(R.id.diary_listitem_userface);
		username = (TextView) findViewById(R.id.diary_listitem_username);
		content = (TextView) findViewById(R.id.diary_listitem_content);
		gridView = (GridView) findViewById(R.id.diary_listitem_grid);
		date = (TextView) findViewById(R.id.diary_listitem_date);
		btncomment = (TextView) findViewById(R.id.diary_listitem_comment);
		relies = (LinearLayout) findViewById(R.id.diary_listitem_relies);
		praiseLin = (LinearLayout) findViewById(R.id.diary_listitem_praise_lin);
		praiseImage = (ImageView) findViewById(R.id.diary_listitem_praise_image);
		praiseText = (TextView) findViewById(R.id.diary_listitem_praise);
		
		
		List<XImage> ilist=con.getImages();
		int size=0;
		if(ilist!=null&&(size=ilist.size())>0){
			ArrayList<String>  imageList=new ArrayList<String>();
			gridView.setVisibility(View.VISIBLE);
			for(XImage image:ilist){
				imageList.add(URLs.HOST+image.getImageUrl());
			}
			gridImageAdapter = new GridImageAdapter(this, imageList);
			gridView.setAdapter(gridImageAdapter);
		}else{
			gridView.setVisibility(View.GONE);
		}
		username.setText(con.getUserInfo().getUserRealName());
		username.setTag(con);// 设置隐藏参数(实体类)

		content.setText(con.getConTitle());
		content.setTag(con);// 设置隐藏参数(实体类)
		// content.setOnClickListener(linkViewClickListener);

		date.setText(StringUtils.friendly_time(con.getConPubTime().toString()));
		String faceURL = con.getConImageUrl();
		if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL))
		{
			userface.setImageResource(R.drawable.widget_dface);
		} else
		{
			ImageManager2.from(this).displayImage(userface, URLs.HOST + faceURL,
					R.drawable.widget_dface);
		}
		userface.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				UIHelper.showUserInfo(DiaryDetail.this, con.getUserInfo());
				
			}
		});
		userface.setTag(URLs.HOST + faceURL);
		relies.setVisibility(View.GONE);
		relies.removeAllViews();// 先清空
		btncomment.setOnClickListener(onclickListener);
		xpraises = con.getPraiseList();
		if (xpraises != null && xpraises.size() > 0)
		{
			View view2 = LayoutInflater.from(context).inflate(R.layout.comment_reply, null);
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

				tv2.append(UIHelper.getClickableSpan(context, xp.getUserInfo()));
				if (i != len - 1)
				{
					tv2.append("、");
				}
				// tv2.setText(xp.getUserInfo().getUserRealName()+"赞");
			}
			tv2.append("  觉得很赞");
			relies.addView(view2);
		}
		if (con.isMeIsPraise())
		{
			praiseText.setText("已赞");
		} else
		{
			praiseText.setText("赞");
		}
	
		praiseLin.setOnClickListener( onclickListener);
		List<XComment> listComments = con.getComments();
		if (listComments != null && listComments.size() > 0)
		{
			// 评论内容
			for (final XComment reply : listComments)
			{
				View view2 = LayoutInflater.from(context).inflate(R.layout.comment_reply, null);
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
				relies.addView(view2);
				tv2.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						if (appContext.isLogin())
						{
							pubcommentLin.setVisibility(View.VISIBLE);
//							if (rePubListener != null)
//								rePubListener.onReCommentClick(position, reply.getPlId());
						} else
						{
							UIHelper.ToastMessage(context, "您还未登陆,请先登录...");
							UIHelper.showLoginDialog(context);
						}
					}
				});
			}

			relies.setVisibility(View.VISIBLE);
		}
		
	}
	
	private OnClickListener onclickListener=new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.diary_listitem_comment:
				if (appContext.isLogin())
				{
					pubcommentLin.setVisibility(View.VISIBLE);
				} else
				{
					UIHelper.ToastMessage(context, "您还未登陆,请先登录...");
					UIHelper.showLoginDialog(context);
				}
				break;
			case R.id.diary_listitem_praise_lin:
				if (appContext.isLogin())
				{
					XPraise praise = new XPraise();
					if (con.isMeIsPraise())
					{ // 如果已经赞了
						for (XPraise xp : xpraises)
						{
							if ( xp.getUserInfo().getId().equals(appContext.getUserInfo().getId()))
							{
								praise.copy(xp);
								con.getPraiseList().remove(xp);
								break;
							}
						}
					} else
					{ // 没有赞
						praise.setUserInfo(appContext.getUserInfo());
						praise.setContentId(con.getId());
						if (con.getPraiseList() == null)
						{
							List<XPraise> list = new ArrayList<XPraise>();
							con.setPraiseList(list);
						}
						con.getPraiseList().add(0, praise);
					}
					NetControl.getShare(context).praiseOpreate(praise, con.isMeIsPraise(), mHandler);
				} else
				{
					UIHelper.ToastMessage(context, "您还未登陆,请先登录...");
					UIHelper.showLoginDialog(context);
				}
				break ;
			default:
				break;
			}
		}
	};
}
