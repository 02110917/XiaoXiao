package com.flying.xiao;

import java.util.Date;
import java.util.List;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.flying.xiao.adapter.ListViewChatAdapter;
import com.flying.xiao.boardcastreceive.WebSocketMsgReceive;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.db.DBHelper;
import com.flying.xiao.entity.ChatMessage;
import com.flying.xiao.entity.XUserInfo;

/**
 * 聊天界面
 * @author zhangmin
 *
 */
public class ChatActivity extends BaseActivity
{
	private EditText mEtmsgInput ;
	private Button mBtnSend;
	private ListView mLvMsgShow;
	private ListViewChatAdapter adapter ;
	private List<ChatMessage> mChatMessageList;
	private XUserInfo userInfo ;
	private DBHelper dbHelper ;
	
	private WebSocketMsgReceive receive;
	private IntentFilter filter;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		dbHelper=DBHelper.getDbHelper(this);
		//TODO 从数据库取 【改正】
		userInfo=(XUserInfo) getIntent().getSerializableExtra("userInfo");
		mChatMessageList=dbHelper.selectMessages(userInfo.getUserName());
		initHeadView();
		initView();
		mHandler=new Handler(){

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				
			}};
	}

	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadTitle.setText("与"+userInfo.getUserRealName()+"聊天");
	} 
	private void initView(){
		mEtmsgInput=(EditText)findViewById(R.id.chat_editer);
		mBtnSend=(Button)findViewById(R.id.chat_send);
		mLvMsgShow=(ListView)findViewById(R.id.chat_list_view);
		adapter=new ListViewChatAdapter(this, mChatMessageList);
		mLvMsgShow.setAdapter(adapter);
		mLvMsgShow.setSelection(mLvMsgShow.getAdapter().getCount()-1);
		mBtnSend.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				String _msg=mEtmsgInput.getText().toString();
				if(StringUtils.isEmpty(_msg)){
					UIHelper.ToastMessage(ChatActivity.this, "输入不能为空");
					return ;
				}
				
				//TODO  从intent获取聊天的对象 UserInfo
				ChatMessage chatMsg=new ChatMessage();
				chatMsg.setBody(_msg);
				chatMsg.setFrom(appContext.getUserInfo().getUserName());
				chatMsg.setTo(userInfo.getUserName());
				chatMsg.setTime(new Date(System.currentTimeMillis()));
				chatMsg.setUserImageHeadUrl(appContext.getUserInfo().getUserHeadImageUrl());
				chatMsg.setTo(true);
				mChatMessageList.add(chatMsg);
				adapter.notifyDataSetChanged();
				//发送消息
				mWebSocketService.sendMessage(chatMsg);
//				XmppControl.getShare(ChatActivity.this).sendMessage(chatMsg, mHandler);
				dbHelper.insertMsg(chatMsg);
				mLvMsgShow.setSelection(mLvMsgShow.getAdapter().getCount()-1);
			}
		});
	}
	
	public void notifyDataSetChanged(){
		mChatMessageList.clear();
		mChatMessageList.addAll(dbHelper.selectMessages(userInfo.getUserName()));
		adapter.notifyDataSetChanged();
		mLvMsgShow.setSelection(mLvMsgShow.getAdapter().getCount()-1);
	}
//	@Override
//	protected void onStart()
//	{
//		super.onStart();
//		receive = new WebSocketMsgReceive();
//		filter = new IntentFilter();
//		filter.addAction("com.flying.xiao.WebSocketMsgReceive");
//		registerReceiver(receive, filter);
//	}
//
//	@Override
//	protected void onStop()
//	{
//		super.onStop();
//		unregisterReceiver(receive);
//	}
}
