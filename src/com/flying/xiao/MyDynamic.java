package com.flying.xiao;

import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.flying.xiao.adapter.ListViewMyDynamicAdapter;
import com.flying.xiao.adapter.ListViewMyDynamicAdapter.IReply;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XComment;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.entity.XDynamic;
import com.flying.xiao.entity.XMessage;
import com.flying.xiao.widget.PullDownListView;

public class MyDynamic extends BaseActivity implements PullDownListView.OnRefreshListioner
{

	private List<XDynamic> myDynamicList;
	private ListViewMyDynamicAdapter adapter;

	private PullDownListView mPullDownListview;
	private ListView mListView;
	public LinearLayout pubCommentEditLin;
	public LinearLayout pubCommentOutSiteLine;
	public Button btnPubComment;
	public EditText etEditComment;
	private ProgressDialog mProgress;
	private int replyId = 0;
	private int page = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		myDynamicList = appContext.listManager.getMyDynamicList();
		setContentView(R.layout.activity_my_dynamic);
		this.initHeadView();
		initView();
		initData();
	}

	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadTitle.setText("我的动态");
		mHeadRightView.setVisibility(View.GONE);
	}

	private void initView()
	{
		mPullDownListview = (PullDownListView) findViewById(R.id.my_dynamic_listview);
		mListView = mPullDownListview.mListView;
		mPullDownListview.setRefreshListioner(this);
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				XDynamic dynamic = myDynamicList.get(position - 1);
				if (dynamic.getType() == Constant.DynamicType.DYNAMIC_TYPE_PRAISE_ME
						|| dynamic.getType() == Constant.DynamicType.DYNAMIC_TYPE_CONTENT_COMMENT)
				{
					XContent content = dynamic.getContent();
					// content.setId(dynamic.getContentId());
					// content.setConTitle(dynamic.getContentTitle());
					UIHelper.showContentInfo(MyDynamic.this, content, content.getConTypeId());
				}
			}
		});
		pubCommentEditLin = (LinearLayout) findViewById(R.id.diary_footer);
		pubCommentOutSiteLine = (LinearLayout) findViewById(R.id.my_dynamic_lin_outsite);
		pubCommentOutSiteLine.setOnTouchListener(listener);
		mListView.setOnTouchListener(listener);
		btnPubComment = (Button) findViewById(R.id.diary_foot_pubcomment);
		etEditComment = (EditText) findViewById(R.id.diary_foot_editer);
		btnPubComment.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				String _commentStr = etEditComment.getText().toString();
				if (StringUtils.isEmpty(_commentStr))
				{
					UIHelper.ToastMessage(v.getContext(), "请输入评论内容");
					return;
				}

				if (!appContext.isLogin())
				{
					UIHelper.showLoginDialog(MyDynamic.this);
					return;
				}
				XDynamic dynamic = myDynamicList.get(replyId);
				mProgress = ProgressDialog.show(v.getContext(), null, "发表中···", true, true);
				if (dynamic.getType() == Constant.DynamicType.DYNAMIC_TYPE_CONTENT_COMMENT
						|| dynamic.getType() == Constant.DynamicType.DYNAMIC_TYPE_PRAISE_ME)
				{
					XContent content = dynamic.getContent();
					NetControl.getShare(MyDynamic.this).pubComment(appContext.getUserInfo().getId(),
							content.getId(), _commentStr, dynamic.getCommentId(), mHandler);
				} else
				{
					XMessage message = dynamic.getMessage();
					NetControl.getShare(MyDynamic.this).pubMessage(
							appContext.getUserInfo().getId(),
							_commentStr,
							message.getMsgId(),
							message.getMsgReplyMain() == null ? message.getMsgId() : message
									.getMsgReplyMain().getMsgId(), mHandler);
				}
			}
		});
		adapter = new ListViewMyDynamicAdapter(this, pubCommentEditLin, myDynamicList,
				R.layout.activity_my_dynamic_listitem, new IReply()
				{

					@Override
					public void reply(int id)
					{
						replyId = id;
					}
				});
		mListView.setAdapter(adapter);
	}

	private void initData()
	{
		mHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				mPullDownListview.onRefreshComplete();
				mPullDownListview.onLoadMoreComplete();
				if (mProgress != null)
					mProgress.dismiss();
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					UIHelper.ToastMessage(MyDynamic.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(MyDynamic.this);
					break;
				case Constant.HandlerMessageCode.GET_MY_DYNAMIC_FAIL:
					UIHelper.ToastMessage(MyDynamic.this, "获取数据出错...");
					break;
				case Constant.HandlerMessageCode.GET_MY_DYNAMIC_SUCCESS:
					List<XDynamic> list = (List<XDynamic>) msg.obj;
					if (page == 0)
						myDynamicList.clear();

					if (list.size() == Constant.MAX_PAGE_COUNT)
						mPullDownListview.setMore(true);
					else if (list.size() < Constant.MAX_PAGE_COUNT)
						mPullDownListview.setMore(false);

					myDynamicList.addAll(list);
					adapter.notifyDataSetChanged();
					break;
				case Constant.HandlerMessageCode.PUB_MESSAGE_ERROR: // 发布评论失败
					UIHelper.ToastMessage(MyDynamic.this, R.string.pub_comment_error);
					break;
				case Constant.HandlerMessageCode.PUB_MESSAGE_SUCCESS:// //发表评论成功
					UIHelper.ToastMessage(MyDynamic.this, "回复成功");
					break;
				case Constant.HandlerMessageCode.PUB_COMMENT_SUCCESS:// //发表评论成功
					UIHelper.ToastMessage(MyDynamic.this, "回复成功");
					break;
				case Constant.HandlerMessageCode.PUB_COMMENT_ERROR:
					UIHelper.ToastMessage(MyDynamic.this, "操作失败...");
					break;
				default:
					break;
				}
			}
		};
		NetControl.getShare(this).getMyDynamic(mHandler, page);

	}

	@Override
	public void onRefresh()
	{
		page = 0;
		NetControl.getShare(this).getMyDynamic(mHandler, page);
	}

	@Override
	public void onLoadMore()
	{
		page++;
		NetControl.getShare(this).getMyDynamic(mHandler, page);
	}

	private OnTouchListener listener = new OnTouchListener()
	{

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			pubCommentEditLin.setVisibility(View.GONE);
			return false;
		}
	};
}
