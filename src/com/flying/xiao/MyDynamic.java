package com.flying.xiao;

import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.flying.xiao.adapter.ListViewMyDynamicAdapter;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.entity.XDynamic;
import com.flying.xiao.widget.PullDownListView;

public class MyDynamic extends BaseActivity implements PullDownListView.OnRefreshListioner
{

	private List<XDynamic> myDynamicList;
	private ListViewMyDynamicAdapter adapter;
	
	private PullDownListView mPullDownListview;
	private ListView mListView;
	public LinearLayout pubCommentEditLin ;
	public Button btnPubComment ;
	public EditText etEditComment ;
	private ProgressDialog mProgress;
	
	private int page =0;
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
				XDynamic dynamic=myDynamicList.get(position-1);
				if(dynamic.getType()==Constant.DynamicType.DYNAMIC_TYPE_PRAISE_ME||dynamic.getType()==Constant.DynamicType.DYNAMIC_TYPE_CONTENT_COMMENT){
					XContent content=dynamic.getContent();
//					content.setId(dynamic.getContentId());
//					content.setConTitle(dynamic.getContentTitle());
					UIHelper.showContentInfo(MyDynamic.this, content, content.getConTypeId());
				}
			}
		});
		pubCommentEditLin=(LinearLayout)findViewById(R.id.diary_footer);
		btnPubComment=(Button)findViewById(R.id.diary_foot_pubcomment);
		etEditComment=(EditText)findViewById(R.id.diary_foot_editer);
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
				long replyId=0;
//				if(isRecomment){
//					replyId=recommentId;
//				}
//				NetControl.getShare(getActivity()).pubComment(ac.getUserInfo().getId(),mDiaryList.get(pubCommentPosition).getId(),
//						_commentStr, replyId,mHandler);
				mProgress = ProgressDialog.show(v.getContext(), null, "发表中···", true, true);
			}
		});
		adapter = new ListViewMyDynamicAdapter(this,pubCommentEditLin, myDynamicList, R.layout.activity_my_dynamic_listitem);
		mListView.setAdapter(adapter);
		mListView.setOnScrollListener(new OnScrollListener()
		{
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				pubCommentEditLin.setVisibility(View.GONE);
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
				
			}
		});
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
					UIHelper.ToastMessage(MyDynamic.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(MyDynamic.this);
					break;
				case Constant.HandlerMessageCode.GET_MY_DYNAMIC_FAIL:
					UIHelper.ToastMessage(MyDynamic.this, "获取数据出错...");
					break ;
				case Constant.HandlerMessageCode.GET_MY_DYNAMIC_SUCCESS:
					List<XDynamic> list=(List<XDynamic>) msg.obj;
					if(page==0)
						myDynamicList.clear();
					
					if(list.size()==Constant.MAX_PAGE_COUNT)
						mPullDownListview.setMore(true);
					else if(list.size()<Constant.MAX_PAGE_COUNT)
						mPullDownListview.setMore(false);
					
					myDynamicList.addAll(list);
					adapter.notifyDataSetChanged();
					break ;
					
				default:
					break;
				}
			}
		};
		NetControl.getShare(this).getMyDynamic(mHandler,page);

	}

	@Override
	public void onRefresh()
	{
		page=0;
		NetControl.getShare(this).getMyDynamic(mHandler,page);
	}

	@Override
	public void onLoadMore()
	{
		page++;
		NetControl.getShare(this).getMyDynamic(mHandler,page);
	}

}
