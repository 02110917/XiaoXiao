package com.flying.xiao.fragment;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.Intents.UI;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.flying.xiao.R;
import com.flying.xiao.adapter.ListViewCommunityAdapter;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.manager.ListManager;
import com.flying.xiao.widget.PullDownListView;

/**
 * 社区
 * 
 * @author zhangmin
 *
 */
public class CommunityFragmentTab extends Fragment implements PullDownListView.OnRefreshListioner
{
	private PullDownListView mPullDownListView;
	private ListView mListView;
	private ListViewCommunityAdapter adapter;
	private List<XUserInfo> userInfolist;
	private ListManager manager;
	private int type;
	private int page = 0;

	private Handler mHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			mPullDownListView.onRefreshComplete();
			mPullDownListView.onLoadMoreComplete();
			switch (msg.what)
			{
			case Constant.HandlerMessageCode.GET_USERINFOS_SUCCESS:
				if (msg.arg1 == type)
				{
					if (page == 0)
						userInfolist.clear();
					List<XUserInfo> xusrs = (List<XUserInfo>) msg.obj;

					if (xusrs.size() == Constant.MAX_PAGE_COUNT)
						mPullDownListView.setMore(true);
					else if (xusrs.size() < Constant.MAX_PAGE_COUNT)
						mPullDownListView.setMore(false);

					userInfolist.addAll(xusrs);
					adapter.notifyDataSetChanged();
				}
				break;
			case Constant.HandlerMessageCode.GET_USERINFOS_FAIL:
				UIHelper.ToastMessage(getActivity(), "获取数据出错");
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		manager = ListManager.getContentMangerShare();
		if (type == Constant.UserType.User_TYPE_BUSINESS)
			userInfolist = manager.getBusinessList();
		else if (type == Constant.UserType.User_TYPE_DEPARTMENT)
			userInfolist = manager.getDepartmentList();
		View v = inflater.inflate(R.layout.community_fragment, null);
		initView(v);
		initData();
		return v;
	}

	private void initView(View v)
	{
		mPullDownListView = (PullDownListView) v.findViewById(R.id.fragment_list_view);
		mListView = mPullDownListView.mListView;
		mPullDownListView.setRefreshListioner(this);
		adapter = new ListViewCommunityAdapter(getActivity(), userInfolist,
				R.layout.community_fragment_listitem);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(itemClickListener);
	}

	private void initData()
	{

		NetControl.getShare(getActivity()).getUserInfos(type, page, mHandler);
	}

	public void setType(int type)
	{
		this.type = type;
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			if (type == Constant.UserType.User_TYPE_DEPARTMENT
					|| type == Constant.UserType.User_TYPE_BUSINESS)
			{
				UIHelper.showCommunityInfo(getActivity(), position - 1, type);
			}
		}
	};

	@Override
	public void onRefresh()
	{
		page = 0;
		NetControl.getShare(getActivity()).getUserInfos(type, page, mHandler);
	}

	@Override
	public void onLoadMore()
	{
		page++;
		NetControl.getShare(getActivity()).getUserInfos(type, page, mHandler);
	}

}
