package com.flying.xiao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.flying.xiao.adapter.ListViewChatAdapter;
import com.flying.xiao.boardcastreceive.WebSocketMsgReceive;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant.WebsocketCode;
import com.flying.xiao.db.DBHelper;
import com.flying.xiao.entity.ChatMessage;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.websocket.WMessage;

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
		mChatMessageList=dbHelper.selectMessages(userInfo.getId());
		initHeadView();
		initView();
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
				WMessage wm=new WMessage();
				wm.setCode(WebsocketCode.WEBSOCKET_SEND_MESSAGE_TEXT);
				wm.setMessage("发送消息");
				wm.setMsg(_msg);
				wm.setSendTime(new Timestamp(System.currentTimeMillis()));
				wm.setUserSendId(appContext.getUserInfo().getId());
				wm.setUserReceiveId(userInfo.getId());
				wm.setMessageId(System.currentTimeMillis());
				//TODO  从intent获取聊天的对象 UserInfo
				ChatMessage cm=ChatMessage.getInstance(wm,true,appContext);
				mChatMessageList.add(cm);
				adapter.notifyDataSetChanged();
				//发送消息
				mWebSocketService.sendMessage(wm);
				dbHelper.insertMsg(cm);
				mLvMsgShow.setSelection(mLvMsgShow.getAdapter().getCount()-1);
			}
		});
	}
	
	public void notifyDataSetChanged(){
		mChatMessageList.clear();
		mChatMessageList.addAll(dbHelper.selectMessages(userInfo.getId()));
		adapter.notifyDataSetChanged();
		mLvMsgShow.setSelection(mLvMsgShow.getAdapter().getCount()-1);
	}
	@Override
	protected void onStart()
	{
		super.onStart();
		receive = new WebSocketMsgReceive();
		filter = new IntentFilter();
		filter.addAction("com.flying.xiao.WebSocketMsgReceive");
		registerReceiver(receive, filter);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		unregisterReceiver(receive);
	}
}
