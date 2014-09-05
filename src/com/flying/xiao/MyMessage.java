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

import com.flying.xiao.adapter.ListViewMessageAdapter;
import com.flying.xiao.adapter.ListViewMessageAdapter.IReplyMessage;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XMessage;
import com.flying.xiao.widget.PullDownListView;

public class MyMessage extends BaseActivity implements PullDownListView.OnRefreshListioner
{

	private List<XMessage> myMessageList;
	private ListViewMessageAdapter adapter;

	private PullDownListView mPullDownListview;
	private ListView mListView;
	public Button btnPubComment;
	public EditText etEditComment;
	private ProgressDialog mProgress;
	private LinearLayout mFooterLayout;
	private LinearLayout mOutsiteLayout;
	private int page = 0;
	private int replyId = 0; // 回复 listview position

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		myMessageList = appContext.listManager.getMyMessageList();
		setContentView(R.layout.activity_my_message);
		this.initHeadView();
		initView();
		initData();
	}

	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadTitle.setText("我的留言");
		mHeadRightView.setVisibility(View.GONE);
	}

	private void initView()
	{
		mPullDownListview = (PullDownListView) findViewById(R.id.my_message_listview);
		mListView = mPullDownListview.mListView;
		mFooterLayout = (LinearLayout) findViewById(R.id.diary_footer);
		mOutsiteLayout = (LinearLayout) findViewById(R.id.my_message_outsite);
		mOutsiteLayout.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				mFooterLayout.setVisibility(View.GONE);
				return false;
			}
		});
		mListView.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				mFooterLayout.setVisibility(View.GONE);
				// TODO Auto-generated method stub
				return false;
			}
		});
		mPullDownListview.setRefreshListioner(this);
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// XMessage message=myMessageList.get(position-1);
				// if(message.getType()==Constant.DynamicType.DYNAMIC_TYPE_PRAISE_ME||message.getType()==Constant.DynamicType.DYNAMIC_TYPE_CONTENT_COMMENT){
				// XContent content=message.getContent();
				// // content.setId(dynamic.getContentId());
				// // content.setConTitle(dynamic.getContentTitle());
				// UIHelper.showContentInfo(MyMessage.this, content,
				// content.getConTypeId());
				// }
			}
		});
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
					UIHelper.ToastMessage(v.getContext(), "请输入回复内容");
					return;
				}

				if (!appContext.isLogin())
				{
					UIHelper.showLoginDialog(MyMessage.this);
					return;
				}
				// if(isRecomment){
				// replyId=recommentId;
				// }
				NetControl.getShare(MyMessage.this).pubMessage(appContext.getUserInfo().getId(), _commentStr,
						myMessageList.get(replyId).getMsgId(), myMessageList.get(replyId).getMsgId(),
						mHandler);
				mProgress = ProgressDialog.show(v.getContext(), null, "发表中···", true, true);
			}
		});
		adapter = new ListViewMessageAdapter(this, myMessageList, R.layout.activity_my_message_listitem,
				mFooterLayout, new IReplyMessage()
				{

					@Override
					public void replyMessage(int id)
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
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					UIHelper.ToastMessage(MyMessage.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(MyMessage.this);
					break;
				case Constant.HandlerMessageCode.GET_MY_MESSAGE_FAIL:
					UIHelper.ToastMessage(MyMessage.this, "获取数据出错...");
					break;
				case Constant.HandlerMessageCode.GET_MY_MESSAGE_SUCCESS:
					List<XMessage> list = (List<XMessage>) msg.obj;
					if (page == 0)
						myMessageList.clear();

					if (list.size() == Constant.MAX_PAGE_COUNT)
						mPullDownListview.setMore(true);
					else if (list.size() < Constant.MAX_PAGE_COUNT)
						mPullDownListview.setMore(false);

					myMessageList.addAll(list);
					adapter.notifyDataSetChanged();
					break;
				case Constant.HandlerMessageCode.PUB_MESSAGE_ERROR: // 发布评论失败
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(MyMessage.this, R.string.pub_comment_error);
					break;
				case Constant.HandlerMessageCode.PUB_MESSAGE_SUCCESS:// //发表评论成功
					if (mProgress != null)
						mProgress.dismiss();
					// 恢复初始底部栏
					XMessage com = (XMessage) msg.obj;
					myMessageList.add(com);
					etEditComment.setText("");
					mFooterLayout.setVisibility(View.GONE);
					adapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
			}
		};
		NetControl.getShare(this).getMyMessage(mHandler, page);

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

}
