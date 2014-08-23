package com.flying.xiao;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.flying.xiao.adapter.ListViewMyChatAdapter;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.db.DBHelper;
import com.flying.xiao.entity.MyChat;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.widget.PullDownListView;

public class MyChatActivity extends BaseActivity implements PullDownListView.OnRefreshListioner
{

	private List<MyChat> myChatList;
	private ListViewMyChatAdapter adapter;
	
	private PullDownListView mPullDownListview;
	private ListView mListView;
	private DBHelper dbHelper ;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_chat);
		dbHelper=DBHelper.getDbHelper(this);
		myChatList=dbHelper.getMyChats();
		initHeadView();
		initView();
	}

	@Override
	protected void initHeadView()
	{
		// TODO Auto-generated method stub
		super.initHeadView();
		mHeadTitle.setText("ÎÒµÄÁÄÌì");
	}
	private void initView()
	{
		mPullDownListview = (PullDownListView) findViewById(R.id.my_chat_listview);
		mListView = mPullDownListview.mListView;
		mPullDownListview.setRefreshListioner(this);
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				MyChat chat=myChatList.get(position-1);
				XUserInfo userInfo=new XUserInfo();
				userInfo.setId(chat.getFriendUserId());
				userInfo.setUserHeadImageUrl(chat.getImageUrl());
				userInfo.setUserRealName(chat.getFriendName());
				UIHelper.showChat(MyChatActivity.this, userInfo);
			}
		});
		mHandler=new Handler(){

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				mPullDownListview.onRefreshComplete();
				
			}};
		adapter = new ListViewMyChatAdapter(this, myChatList, R.layout.activity_my_chat_list_item);
		mListView.setAdapter(adapter);
		
	}
	private void notifyDataSetChanged(){
		myChatList.clear();
		myChatList.addAll(dbHelper.getMyChats());
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		notifyDataSetChanged();
	}

	

	@Override
	public void onRefresh()
	{
		notifyDataSetChanged();
		mHandler.sendEmptyMessage(0);
	}

	@Override
	public void onLoadMore()
	{
	}
}
