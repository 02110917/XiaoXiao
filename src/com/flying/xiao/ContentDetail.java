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
 * ��������
 */
public class ContentDetail extends BaseActivity 
{

	private ImageView mFavorite; // �ղ�
	private ScrollView mScrollView;
	private ViewSwitcher mViewSwitcher;

	private BadgeView bv_comment;
	private ImageView mDetail; // ���鰴ť
	private ImageView mCommentList; // ���۰���
	private ImageView mShare; // ����ť

	private TextView mTitle; // ���ݱ���
	private TextView mAuthor; // ����
	private TextView mPubDate; // ����ʱ��
	private TextView mCommentCount; // ������
	private ListView mLvComment;
	private WebView mWebView; // ��ʾ����
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

	private int curCatalog; // �������۷���
	private int curLvDataState;
	private int curLvPosition;// ��ǰlistviewѡ�е�itemλ��

	private ViewSwitcher mFootViewSwitcher;
	private ImageView mFootEditebox;
	private EditText mFootEditer;
	private Button mFootPubcomment;
	private ProgressDialog mProgress;
	private InputMethodManager imm;

	private String _commentStr;// ��������
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
		mHeadTitle.setText("��������");
	}

	// ��ʼ����ͼ�ؼ�
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

		// ��ʾ��ʱ�༭����
		// UIHelper.showTempEditContent(this, mFootEditer, tempCommentKey);
	}

	// ��ʼ���ؼ�����
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
					// ����������ͼ&����
					initCommentView();
					//
					// //�Ƿ��ղ�
					// if(blogDetail.getFavorite() == 1)
					// mFavorite.setImageResource(R.drawable.widget_bar_favorite2);
					// else
					// mFavorite.setImageResource(R.drawable.widget_bar_favorite);
					//
					// //��ʾ������
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
				case Constant.HandlerMessageCode.PUB_COMMENT_ERROR: // ��������ʧ��
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
				case Constant.HandlerMessageCode.PUB_COMMENT_SUCCESS:// //�������۳ɹ�
					if (mProgress != null)
						mProgress.dismiss();
					// �ָ���ʼ�ײ���
					mFootViewSwitcher.setDisplayedChild(0);
					mFootEditer.clearFocus();
					mFootEditer.setText("");
					mFootEditer.setVisibility(View.GONE);
					// ���������б�
					viewSwitch(VIEWSWITCH_TYPE_COMMENTS);
					// ���������б�
					XComment com = (XComment) msg.obj;
					lvCommentData.add(0, com);
					lvCommentAdapter.notifyDataSetChanged();
					// ��ʾ������
					int count = lvCommentAdapter.getMainCommentList().size();
					bv_comment.setText(count + "");
					bv_comment.show();
					con.setConPls(con.getConPls()+1);
					mCommentCount.setText(con.getConPls()+"");
					break;
				case Constant.HandlerMessageCode.COLLECTION_OPERATE_FAIL:
					UIHelper.ToastMessage(ContentDetail.this, "����ʧ��...");
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
	 * �ײ����л�
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
			mHeadTitle.setText("��������");
			mViewSwitcher.setDisplayedChild(0);
			break;
		case VIEWSWITCH_TYPE_COMMENTS:
			mDetail.setEnabled(true);
			mCommentList.setEnabled(false);
			mHeadTitle.setText("�����б�");
			mViewSwitcher.setDisplayedChild(1);
			break;
		}
	}

	/**
	 * ͷ����ťչʾ
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
			// //����
			// UIHelper.showShareDialog(ContentDetail.this,
			// blogDetail.getTitle(), blogDetail.getUrl());
		}
	};

	private View.OnClickListener detailClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// �л�������
			viewSwitch(VIEWSWITCH_TYPE_DETAIL);
		}
	};

	private View.OnClickListener commentlistClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			// �л�������
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

	// ��ʼ����ͼ�ؼ�
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
				// ��ת--�ظ����۽���
				if(appContext.isLogin()){
					UIHelper.showCommentReply(ContentDetail.this, com.getPlId(), con.getId(), appContext
							.getUserInfo().getId(), appContext.getUserInfo().getUserRealName(), com.getPlInfo());
				}else{
					UIHelper.ToastMessage(ContentDetail.this, "����δ��½,���ȵ�¼...");
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

		viewSwitch(VIEWSWITCH_TYPE_COMMENTS);// ���������б�

		XComment comm = (XComment) data.getSerializableExtra("COMMENT_SERIALIZABLE");
		lvCommentData.add(0, comm);
		lvCommentAdapter.notifyDataSetChanged();
		// ��ʾ������
		int count = lvCommentAdapter.getMainCommentList().size();
		bv_comment.setText(count + "");
		bv_comment.show();
	}

	/**
	 * �������۰�ť
	 */
	private View.OnClickListener commentpubClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{

			_commentStr = mFootEditer.getText().toString();
			if (StringUtils.isEmpty(_commentStr))
			{
				UIHelper.ToastMessage(v.getContext(), "��������������");
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
			mProgress = ProgressDialog.show(v.getContext(), null, "�����С�����", true, true);

		}
	};

}
