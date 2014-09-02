package com.flying.xiao;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.flying.xiao.adapter.ListViewCommentAdapter;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XComment;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.entity.XContentDetail;
import com.flying.xiao.widget.BadgeView;

/**
 * 内容详情
 */
public class ContentDetail extends BaseActivity 
{

	private ImageView mFavorite; // 收藏
	private ScrollView mScrollView;
	private ViewSwitcher mViewSwitcher;

	private BadgeView bv_comment;
	private ImageView mDetail; // 详情按钮
	private ImageView mCommentList; // 评论安妮
	private ImageView mShare; // 分享按钮

	private TextView mTitle; // 内容标题
	private TextView mAuthor; // 作者
	private TextView mPubDate; // 发布时间
	private TextView mCommentCount; // 评论数
	private ListView mLvComment;
	private WebView mWebView; // 显示内容
	private XContent con;
	private int conType;
	private final static int VIEWSWITCH_TYPE_DETAIL = 0x001;
	private final static int VIEWSWITCH_TYPE_COMMENTS = 0x002;

	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;
	private final static int DATA_LOAD_FAIL = 0x003;

	private List<XComment> lvCommentData = new ArrayList<XComment>();
	private View lvComment_footer;
	private TextView lvComment_foot_more;
	private ProgressBar lvComment_foot_progress;
	private Handler mCommentHandler;
	private ListViewCommentAdapter lvCommentAdapter;
	private int lvSumData;

	private int curCatalog; // 博客评论分类
	private int curLvDataState;
	private int curLvPosition;// 当前listview选中的item位置

	private ViewSwitcher mFootViewSwitcher;
	private ImageView mFootEditebox;
	private EditText mFootEditer;
	private Button mFootPubcomment;
	private ProgressDialog mProgress;
	private InputMethodManager imm;

	private String _commentStr;// 评论内容
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_detail);
		this.initHeadView();
		this.initView();
		this.initData();

	}
	
	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadProgressBar.setVisibility(View.VISIBLE);
		mHeadRightView.setVisibility(View.GONE);
		mHeadTitle.setText("内容详情");
	}

	// 初始化视图控件
	private void initView()
	{
		conType = getIntent().getIntExtra("conType", 0);
		if(getIntent().getSerializableExtra("contentObject")!=null){
			con=(XContent) getIntent().getSerializableExtra("contentObject");
		}
		else
		{
			int index=getIntent().getIntExtra("content", 0);
			if(conType==Constant.ContentType.CONTENT_TYPE_NEWS)
				con = appContext.listManager.getNewsContentList().get(index);
			else if(conType==Constant.ContentType.CONTENT_TYPE_ASK)
				con = appContext.listManager.getAskContentList().get(index);
		}
		
		mViewSwitcher = (ViewSwitcher) findViewById(R.id.content_detail_viewswitcher);
		mScrollView = (ScrollView) findViewById(R.id.content_detail_scrollview);

		mDetail = (ImageView) findViewById(R.id.content_detail_footbar_detail);
		mCommentList = (ImageView) findViewById(R.id.content_detail_footbar_commentlist);
		mShare = (ImageView) findViewById(R.id.content_detail_footbar_share);
		mFavorite = (ImageView) findViewById(R.id.content_detail_footbar_favorite);
		mTitle = (TextView) findViewById(R.id.content_detail_title);
		mAuthor = (TextView) findViewById(R.id.content_detail_author);
		mPubDate = (TextView) findViewById(R.id.content_detail_date);
		mCommentCount = (TextView) findViewById(R.id.content_detail_commentcount);

		mDetail.setEnabled(false);

		mWebView = (WebView) findViewById(R.id.department_detail_webview);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDefaultFontSize(15);
		// UIHelper.addWebImageShow(this, mWebView);

		mFavorite.setOnClickListener(favoriteClickListener);
		mHeadRightView.setOnClickListener(refreshClickListener);
		mAuthor.setOnClickListener(authorClickListener);
		mShare.setOnClickListener(shareClickListener);
		mDetail.setOnClickListener(detailClickListener);
		mCommentList.setOnClickListener(commentlistClickListener);

		bv_comment = new BadgeView(this, mCommentList);
		bv_comment.setBackgroundResource(R.drawable.widget_count_bg2);
		bv_comment.setIncludeFontPadding(false);
		bv_comment.setGravity(Gravity.CENTER);
		bv_comment.setTextSize(8f);
		bv_comment.setTextColor(Color.WHITE);

		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		mFootViewSwitcher = (ViewSwitcher) findViewById(R.id.content_detail_foot_viewswitcher);
		mFootPubcomment = (Button) findViewById(R.id.content_detail_foot_pubcomment);
		mFootPubcomment.setOnClickListener(commentpubClickListener);
		mFootEditebox = (ImageView) findViewById(R.id.content_detail_footbar_editebox);
		mFootEditebox.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mFootViewSwitcher.showNext();
				mFootEditer.setVisibility(View.VISIBLE);
				mFootEditer.requestFocus();
				mFootEditer.requestFocusFromTouch();
			}
		});
		mFootEditer = (EditText) findViewById(R.id.content_detail_foot_editer);
		mFootEditer.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus)
				{
					imm.showSoftInput(v, 0);
				} else
				{
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		});
		mFootEditer.setOnKeyListener(new View.OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_BACK)
				{
					if (mFootViewSwitcher.getDisplayedChild() == 1)
					{
						mFootViewSwitcher.setDisplayedChild(0);
						mFootEditer.clearFocus();
						mFootEditer.setVisibility(View.GONE);
					}
					return true;
				}
				return false;
			}
		});

		// 显示临时编辑内容
		// UIHelper.showTempEditContent(this, mFootEditer, tempCommentKey);
	}

	// 初始化控件数据
	private void initData()
	{
		mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.CONTENT_DETAIL_LOAD_DATA_SUCCESS:
					XContentDetail xconDetail = (XContentDetail) msg.obj;
					headButtonSwitch(DATA_LOAD_COMPLETE);
					lvCommentData.clear();
					if(xconDetail.getComments()!=null)
						lvCommentData.addAll(xconDetail.getComments());
					if(con.isMeCollecte()){
						mFavorite.setImageResource(R.drawable.widget_bar_favorite_y);
					}
					mTitle.setText(con.getConTitle());
					mAuthor.setText(con.getUserInfo().getUserRealName());
					mPubDate.setText(StringUtils.friendly_time(con.getConPubTime().toString()));
					mCommentCount.setText(String.valueOf(con.getConPls()));
					con.setConHot(con.getConHot()+1);
					// 加载评论视图&数据
					initCommentView();
					//
					// //是否收藏
					// if(blogDetail.getFavorite() == 1)
					// mFavorite.setImageResource(R.drawable.widget_bar_favorite2);
					// else
					// mFavorite.setImageResource(R.drawable.widget_bar_favorite);
					//
					// //显示评论数
					if (lvCommentAdapter.getMainCommentList().size() > 0)
					{
						bv_comment.setText(lvCommentAdapter.getMainCommentList().size() + "");
						bv_comment.show();
					} else
					{
						bv_comment.setText("");
						bv_comment.hide();
					}

					String body = UIHelper.WEB_STYLE + xconDetail.getContentInfo()
							+ "<div style=\"margin-bottom: 80px\" />";
					body = body.replaceAll("src=\"/XiaoServer/", "src=\""+URLs.HOST+"/XiaoServer/");
					body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
					body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
					//
					mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
					// mWebView.setWebViewClient(UIHelper.getWebViewClient());
					break;
				case Constant.HandlerMessageCode.CONTENT_DETAIL_LOAD_DATA_ERROR:
					headButtonSwitch(DATA_LOAD_FAIL);

					UIHelper.ToastMessage(ContentDetail.this, R.string.msg_load_is_null);
					break;
				case Constant.HandlerMessageCode.PUB_COMMENT_ERROR: // 发布评论失败
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(ContentDetail.this, R.string.pub_comment_error);
					break;
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(ContentDetail.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(ContentDetail.this);
					break;
				case Constant.HandlerMessageCode.PUB_COMMENT_SUCCESS:// //发表评论成功
					if (mProgress != null)
						mProgress.dismiss();
					// 恢复初始底部栏
					mFootViewSwitcher.setDisplayedChild(0);
					mFootEditer.clearFocus();
					mFootEditer.setText("");
					mFootEditer.setVisibility(View.GONE);
					// 跳到评论列表
					viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
					// 更新评论列表
					XComment com = (XComment) msg.obj;
					lvCommentData.add(0, com);
					lvCommentAdapter.notifyDataSetChanged();
					// 显示评论数
					int count = lvCommentAdapter.getMainCommentList().size();
					bv_comment.setText(count + "");
					bv_comment.show();
					con.setConPls(con.getConPls()+1);
					mCommentCount.setText(con.getConPls()+"");
					break;
				case Constant.HandlerMessageCode.COLLECTION_OPERATE_FAIL:
					UIHelper.ToastMessage(ContentDetail.this, "操作失败...");
					break ;
				case Constant.HandlerMessageCode.COLLECTION_OPERATE_SUCCESS:
					con.setMeCollecte(!con.isMeCollecte());
					if(con.isMeCollecte())
						mFavorite.setImageResource(R.drawable.widget_bar_favorite_y);
					else
						mFavorite.setImageResource(R.drawable.widget_bar_favorite);
					break ;
				default:
					break;
				}

			}
		};
		NetControl.getShare(this).getContentDetail(conType, con.getId(),mHandler);
		headButtonSwitch(DATA_LOAD_ING);
	}

	/**
	 * 底部栏切换
	 * 
	 * @param type
	 */
	private void viewSwitch(int type)
	{
		switch (type)
		{
		case VIEWSWITCH_TYPE_DETAIL:
			mDetail.setEnabled(false);
			mCommentList.setEnabled(true);
			mHeadTitle.setText("内容详情");
			mViewSwitcher.setDisplayedChild(0);
			break;
		case VIEWSWITCH_TYPE_COMMENTS:
			mDetail.setEnabled(true);
			mCommentList.setEnabled(false);
			mHeadTitle.setText("评论列表");
			mViewSwitcher.setDisplayedChild(1);
			break;
		}
	}

	/**
	 * 头部按钮展示
	 * 
	 * @param type
	 */
	private void headButtonSwitch(int type)
	{
		switch (type)
		{
		case DATA_LOAD_ING:
			mScrollView.setVisibility(View.GONE);
			mHeadProgressBar.setVisibility(View.VISIBLE);
			mHeadRightView.setVisibility(View.GONE);
			break;
		case DATA_LOAD_COMPLETE:
			mScrollView.setVisibility(View.VISIBLE);
			mHeadProgressBar.setVisibility(View.GONE);
			mHeadRightView.setVisibility(View.VISIBLE);
			break;
		case DATA_LOAD_FAIL:
			mScrollView.setVisibility(View.GONE);
			mHeadProgressBar.setVisibility(View.GONE);
			mHeadRightView.setVisibility(View.VISIBLE);
			break;
		}
	}

	private View.OnClickListener refreshClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			NetControl.getShare(ContentDetail.this).getContentDetail(conType, con.getId(),mHandler);
			headButtonSwitch(DATA_LOAD_ING);
		}
	};

	private View.OnClickListener authorClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// UIHelper.showUserCenter(v.getContext(), blogDetail.getAuthorId(),
			// blogDetail.getAuthor());
		}
	};

	private View.OnClickListener shareClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// if(blogDetail == null){
			// UIHelper.ToastMessage(v.getContext(),
			// R.string.msg_read_detail_fail);
			// return;
			// }
			// //分享到
			// UIHelper.showShareDialog(ContentDetail.this,
			// blogDetail.getTitle(), blogDetail.getUrl());
		}
	};

	private View.OnClickListener detailClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// 切换到详情
			viewSwitch(VIEWSWITCH_TYPE_DETAIL);
		}
	};

	private View.OnClickListener commentlistClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// 切换到评论
			viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
		}
	};

	private View.OnClickListener favoriteClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			NetControl.getShare(ContentDetail.this).collectOperate(con.getId(), con.isMeCollecte(),mHandler);
		}
	};

	// 初始化视图控件
	private void initCommentView()
	{
		lvComment_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		lvComment_foot_more = (TextView) lvComment_footer.findViewById(R.id.listview_foot_more);
		lvComment_foot_progress = (ProgressBar) lvComment_footer.findViewById(R.id.listview_foot_progress);

		lvCommentAdapter = new ListViewCommentAdapter(this, lvCommentData, R.layout.comment_listitem);
		mLvComment = (ListView) findViewById(R.id.comment_list_listview);
		mLvComment.setAdapter(lvCommentAdapter);
		mLvComment.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				XComment com = lvCommentAdapter.getMainCommentList().get(position);
				// 跳转--回复评论界面
				if(appContext.isLogin()){
					UIHelper.showCommentReply(ContentDetail.this, com.getPlId(), con.getId(), appContext
							.getUserInfo().getId(), appContext.getUserInfo().getUserRealName(), com.getPlInfo());
				}else{
					UIHelper.ToastMessage(ContentDetail.this, "您还未登陆,请先登录...");
					UIHelper.showLoginDialog(ContentDetail.this);
				}
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != RESULT_OK)
			return;
		if (data == null)
			return;

		viewSwitch(VIEWSWITCH_TYPE_COMMENTS);// 跳到评论列表

		XComment comm = (XComment) data.getSerializableExtra("COMMENT_SERIALIZABLE");
		lvCommentData.add(0, comm);
		lvCommentAdapter.notifyDataSetChanged();
		// 显示评论数
		int count = lvCommentAdapter.getMainCommentList().size();
		bv_comment.setText(count + "");
		bv_comment.show();
	}

	/**
	 * 发布评论按钮
	 */
	private View.OnClickListener commentpubClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{

			_commentStr = mFootEditer.getText().toString();
			if (StringUtils.isEmpty(_commentStr))
			{
				UIHelper.ToastMessage(v.getContext(), "请输入评论内容");
				return;
			}

			final AppContext ac = (AppContext) getApplication();
			if (!ac.isLogin())
			{
				UIHelper.showLoginDialog(ContentDetail.this);
				return;
			}
			NetControl.getShare(ContentDetail.this).pubComment(appContext.getUserInfo().getId(), con.getId(),
					_commentStr, 0,mHandler);
			mProgress = ProgressDialog.show(v.getContext(), null, "发表中···", true, true);

		}
	};

}
