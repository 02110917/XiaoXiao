package com.flying.xiao;

import java.util.List;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.flying.xiao.adapter.ListViewMyFriendAdapter;
import com.flying.xiao.boardcastreceive.WebSocketMsgReceive;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.db.DBHelper;
import com.flying.xiao.entity.XUserInfo;

public class MyFriends extends BaseActivity
{

	private ListView mLv;
	private List<XUserInfo> myFriendList;
	private ListViewMyFriendAdapter adapter;
	private DBHelper dbHelper;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		dbHelper=DBHelper.getDbHelper(this);
//		appContext.listManager.setMyFriendOnline();
//		myFriendList = appContext.listManager.getMyFriendList();
		myFriendList=dbHelper.selectFriends();
		setContentView(R.layout.activity_my_friends);
		initHeadView();
		initView();
		initData();
	}

	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadTitle.setText("我的好友");
		mHeadRightView.setVisibility(View.GONE);
	}

	private void initView()
	{

		mLv = (ListView) findViewById(R.id.my_friend_list_view);
		adapter = new ListViewMyFriendAdapter(this, myFriendList, R.layout.activity_my_friends_list_item);

		mLv.setAdapter(adapter);
		mLv.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				UIHelper.showChat(MyFriends.this, myFriendList.get(position));
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
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					UIHelper.ToastMessage(MyFriends.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(MyFriends.this);
					break;
				case Constant.HandlerMessageCode.GET_MY_FRIENDS_FAIL:
					UIHelper.ToastMessage(MyFriends.this, "获取数据出错...");
					break;
				case Constant.HandlerMessageCode.GET_MY_FRIENDS_SUCCESS:
					List<XUserInfo> list = (List<XUserInfo>) msg.obj;
//					dbHelper.insertFriends(list);
					myFriendList.clear();
					myFriendList.addAll(dbHelper.selectFriends());
//					myFriendList.clear();
//					myFriendList.addAll(list);
//					appContext.listManager.setMyFriendOnline();
					adapter.notifyDataSetChanged();
					break;

				default:
					break;
				}
			}
		};
//		getmWebSocketService().getAllFriends();//获取所有好友列表
//		NetControl.getShare(this).getMyFriends(mHandler);

	}


	public void changeFriendState()
	{
		System.out.println("----changeFriendState");
//		appContext.listManager.setMyFriendOnline();
		myFriendList.clear();
		myFriendList.addAll(dbHelper.selectFriends());
		adapter.notifyDataSetChanged();
	}
	

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		changeFriendState();
	}
	
}
