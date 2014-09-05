package com.flying.xiao.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.flying.xiao.R;
import com.flying.xiao.adapter.ListViewMainContentAdapter;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.widget.PullDownListView;

@SuppressLint("ValidFragment")
public class MainContentFragment extends Fragment implements PullDownListView.OnRefreshListioner
{
	private static final String TAG = "MainNews";
	private PullDownListView mPullDownListview;
	private ListView mListView;
	private ListViewMainContentAdapter mAdapter;
	private List<XContent> contentList ;
	private Handler mHandler;
	private AppContext appContext ;
//	private boolean isGetMyData=false ;// 是否是获取登陆用户的相关信息
	private int showType;
	private long userId=-1 ;
	private int mCurPage = 0;
	private int conType;
	
	private boolean isMyCollect=false;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		appContext=(AppContext) getActivity().getApplication();
		if(showType==Constant.MainContentFragmentShowType.TYPE_MAIN) //获取全局数据
			contentList=appContext.listManager.getContentListByType(conType);
		else // 获取登陆用户数据
		{
			contentList=new ArrayList<XContent>(); 
			userId=appContext.getUserInfo().getId();
			if(showType==Constant.MainContentFragmentShowType.TYPE_MY_COLLECT){
				isMyCollect=true ;
			}
		}
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
					if(msg.arg1==conType){
						mPullDownListview.onRefreshComplete();
						mPullDownListview.onLoadMoreComplete();
						if(mCurPage==0) //如果是刷新获得重新加载  则清楚之前的数据
							contentList.clear();
						List<XContent> list=(List<XContent>) msg.obj;
						if(list.size()==Constant.MAX_PAGE_COUNT)
							mPullDownListview.setMore(true);
						else if(list.size()<Constant.MAX_PAGE_COUNT)
							mPullDownListview.setMore(false);
						contentList.addAll(list);
						mAdapter.notifyDataSetChanged();
					}
					break;
				default:
					break;
				}
			}
		};

		NetControl.getShare(getActivity()).getContentData(conType,userId, 0,isMyCollect, mHandler);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		System.out.println(conType + "----onCreateView-----------");
		
		View view = inflater.inflate(R.layout.main_fragment_news, null);
		initView(view);
		return view;
	}

	private void initView(View v)
	{
		mPullDownListview = (PullDownListView) v.findViewById(R.id.main_fragment_list_view_news);
		mListView = mPullDownListview.mListView;
		
		if(conType==Constant.ContentType.CONTENT_TYPE_MARKET)
			mAdapter= new ListViewMainContentAdapter(getActivity(), contentList, R.layout.main_fragment_market_listitem,true);
		else
			mAdapter = new ListViewMainContentAdapter(getActivity(), contentList, R.layout.main_fragment_news_listitem);
		mListView.setAdapter(mAdapter);
		mPullDownListview.setRefreshListioner(this);
		
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if(showType==Constant.MainContentFragmentShowType.TYPE_MAIN)
					UIHelper.showContentInfo(getActivity(),position-1,conType);
				else
					UIHelper.showContentInfo(getActivity(),contentList.get(position-1) ,conType);
			}
		});
	}
	public void setConType(int conType)
	{
		this.conType = conType;
	}

	@Override
	public void onRefresh()
	{
		mCurPage=0;
		NetControl.getShare(getActivity()).getContentData(conType,userId, 0,isMyCollect, mHandler);
	}

	@Override
	public void onLoadMore()
	{
		mCurPage++;
		NetControl.getShare(getActivity()).getContentData(conType,userId, mCurPage,isMyCollect, mHandler);
	}

	public void setShowType(int showType)
	{
		this.showType = showType;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if(showType==Constant.MainContentFragmentShowType.TYPE_MAIN) //获取全局数据
			contentList=appContext.listManager.getContentListByType(conType);
		else // 获取登陆用户数据
		{
			contentList=new ArrayList<XContent>(); 
			userId=appContext.getUserInfo().getId();
			if(showType==Constant.MainContentFragmentShowType.TYPE_MY_COLLECT){
				isMyCollect=true ;
			}
		}
	}

	
}
