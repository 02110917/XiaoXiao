package com.flying.xiao.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.flying.xiao.R;
import com.flying.xiao.adapter.ListViewMainDiaryAdapter;
import com.flying.xiao.adapter.ListViewMainDiaryAdapter.OnPubBtnClickListener;
import com.flying.xiao.adapter.ListViewMainDiaryAdapter.OnRePubCommentCliclListener;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XComment;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.widget.PullDownListView;
 
public class UserInfoDetailFragmentDiary extends Fragment implements PullDownListView.OnRefreshListioner
{

	private static final String TAG = "UserInfoDetailFragmentDiary";
	private PullDownListView mPullDownListview;
	private ListView mListView;
	public LinearLayout pubCommentEditLin ;
	public Button btnPubComment ;
	public EditText etEditComment ;
	private List<XContent> mDiaryList;
	private ListViewMainDiaryAdapter mAdapter;
	private Handler mHandler;
	private int mCurPage = 0;
	private int pubCommentPosition=0;
	private long recommentId=0;
	private boolean isRecomment ; //是否是回复已有的评论
	private ProgressDialog mProgress;
	private AppContext appContext ;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		appContext=(AppContext) getActivity().getApplication();
		mDiaryList=new ArrayList<XContent>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.main_fragment_diary, null);
		initView(view);
		return view;
	}

	private void initView(View v)
	{
		mPullDownListview = (PullDownListView) v.findViewById(R.id.main_fragment_list_view_diary);
		mListView = mPullDownListview.mListView;
		mPullDownListview.setRefreshListioner(this);
		pubCommentEditLin=(LinearLayout)v.findViewById(R.id.diary_footer);
		btnPubComment=(Button)v.findViewById(R.id.diary_foot_pubcomment);
		etEditComment=(EditText)v.findViewById(R.id.diary_foot_editer);
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

				final AppContext ac = (AppContext) getActivity().getApplication();
				if (!ac.isLogin())
				{
					UIHelper.showLoginDialog(getActivity());
					return;
				}
				long replyId=0;
				if(isRecomment){
					replyId=recommentId;
				}
				NetControl.getShare(getActivity()).pubComment(ac.getUserInfo().getId(),mDiaryList.get(pubCommentPosition).getId(),
						_commentStr, replyId,mHandler);
				mProgress = ProgressDialog.show(v.getContext(), null, "发表中···", true, true);
			}
		});
		mAdapter = new ListViewMainDiaryAdapter(getActivity(),pubCommentEditLin, mDiaryList, R.layout.main_fragment_diary_listitem);
		mListView.setAdapter(mAdapter);
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
		mAdapter.setPubBtnListener(new OnPubBtnClickListener()
		{
			
			@Override
			public void onPubCommentBtnClick(int position)
			{
				isRecomment=false ;
				pubCommentPosition=position;
				etEditComment.requestFocus();
			}
		});
		mAdapter.setRePubListener(new OnRePubCommentCliclListener()
		{
			
			@Override
			public void onReCommentClick(int position,long commentId)
			{
				isRecomment=true;
				recommentId=commentId;
				pubCommentPosition=position;
				etEditComment.requestFocus();
			}
		});
		mHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.MAIN_LOAD_DATA_ERROR:
					mPullDownListview.onRefreshComplete();
					mPullDownListview.onLoadMoreComplete();
					UIHelper.ToastMessage(getActivity(), R.string.main_fragment_load_data_error);
					break;

				case Constant.HandlerMessageCode.MAIN_LOAD_DATA_SUCCESS:
					mPullDownListview.onRefreshComplete();
					mPullDownListview.onLoadMoreComplete();
					if(mCurPage==0) //如果是刷新获得重新加载  则清楚之前的数据
						mDiaryList.clear();
					List<XContent> list=(List<XContent>) msg.obj;
					if(list.size()==Constant.MAX_PAGE_COUNT)
						mPullDownListview.setMore(true);
					else if(list.size()<Constant.MAX_PAGE_COUNT)
						mPullDownListview.setMore(false);
					mDiaryList.addAll(list);
					mAdapter.notifyDataSetChanged();

					break;
				case Constant.HandlerMessageCode.PUB_COMMENT_ERROR: // 发布评论失败
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(getActivity(), R.string.pub_comment_error);
					break;
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(getActivity(), R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(getActivity());
					break;
				case Constant.HandlerMessageCode.PUB_COMMENT_SUCCESS:// //发表评论成功
					if (mProgress != null)
						mProgress.dismiss();
					pubCommentEditLin.setVisibility(View.GONE);
					etEditComment.setText("");
					etEditComment.clearFocus();
					// 更新评论列表
					XComment com = (XComment) msg.obj;
					mDiaryList.get(pubCommentPosition).getComments().add(com);
					mAdapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
			}
		};
		NetControl.getShare(getActivity()).getContentData(Constant.ContentType.CONTENT_TYPE_DIARY, 0, mHandler);

	}

	@Override
	public void onRefresh()
	{
		mCurPage=0;
		NetControl.getShare(getActivity()).getContentData(Constant.ContentType.CONTENT_TYPE_DIARY, 0, mHandler);
		
	}

	@Override
	public void onLoadMore()
	{
		mCurPage++;
		NetControl.getShare(getActivity()).getContentData(Constant.ContentType.CONTENT_TYPE_DIARY, mCurPage, mHandler);		
	}

}
