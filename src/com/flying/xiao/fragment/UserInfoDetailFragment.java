package com.flying.xiao.fragment;

import java.util.ArrayList;
import java.util.List;

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
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.entity.XUserInfo;

public class UserInfoDetailFragment extends Fragment
{
	private int type;
	private XUserInfo userInfo;
	private List<XContent> contentList;
	private ListView mLv;
	private ListViewMainContentAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		contentList = new ArrayList<XContent>();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.department_detail_fragment, null);
		initView(v);
		initData();
		return v;
	}

	private void initData()
	{
		Handler handler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.DEPARTMENT_DETAIL_LOAD_DATA_FAIL:

					break;
				case Constant.HandlerMessageCode.DEPARTMENT_DETAIL_LOAD_DATA_SUCCESS:
					if (type == msg.arg1)
					{
						List<XContent> list = (List<XContent>) msg.obj;
						contentList.clear();
						contentList.addAll(list);
						adapter.notifyDataSetChanged();
					}
					break;

				default:
					break;
				}
			}
		};
		if (type == Constant.WzType.WZTYPE_WP)
		{
			NetControl.getShare(getActivity()).getMarketData(type, userInfo.getId(), 0, handler);
		} else
		{
			NetControl.getShare(getActivity()).getWzData(type, userInfo.getId(), 0, handler);
		}

	}

	private void initView(View v)
	{
		mLv = (ListView) v.findViewById(R.id.department_detail_fragment_listview);
		if (type == Constant.WzType.WZTYPE_WP)
		{
			adapter = new ListViewMainContentAdapter(getActivity(), contentList,
					R.layout.main_fragment_market_listitem, true);
		} else
		{
			adapter = new ListViewMainContentAdapter(getActivity(), contentList,
					R.layout.main_fragment_news_listitem);
		}
		mLv.setAdapter(adapter);
		mLv.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if (type == Constant.WzType.WZTYPE_WP)
				{
					UIHelper.showContentInfo(getActivity(), contentList.get(position),
							Constant.ContentType.CONTENT_TYPE_MARKET);
				} else
				{
					UIHelper.showContentInfo(getActivity(), contentList.get(position),
							Constant.ContentType.CONTENT_TYPE_NEWS);
				}
			}

		});
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public void setUserInfo(XUserInfo userInfo)
	{
		this.userInfo = userInfo;
	}

}
