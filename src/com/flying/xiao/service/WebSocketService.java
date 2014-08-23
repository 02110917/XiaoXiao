package com.flying.xiao.service;

import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.flying.xiao.app.AppContext;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.constant.Constant.WebsocketCode;
import com.flying.xiao.db.DBHelper;
import com.flying.xiao.entity.ChatMessage;
import com.flying.xiao.websocket.OfflineMessage;
import com.flying.xiao.websocket.WFriend;
import com.flying.xiao.websocket.WFriends;
import com.flying.xiao.websocket.WMessage;
import com.flying.xiao.websocket.WOfflineMessages;
import com.google.gson.Gson;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

public class WebSocketService extends Service
{
	private AppContext appContext;
	private String wsUri;
	private DBHelper dbHelper;
	private final IBinder binder = new MyBinder();

//	private boolean isOnline = false;
	private boolean threadHeartFlag=false ;
	private boolean threadReconnectFalg=false ;
	private boolean isSendtoHeart = false; // �����Ƿ��͵���
	private boolean isDestoryServer ; 
	private int heartReSendTime = 0; // �����ط�����

	private static final int HANLER_RECONNECT = 1;
	private static final int HANLER_SEND_HEART = 2;

	private ReConnectThread reContentThread=null;

	private HeartThread hearttThread=null;

	@Override
	public IBinder onBind(Intent intent)
	{
		return binder;
	}

	private Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what)
			{
			case HANLER_SEND_HEART:
				WMessage message = new WMessage();
				message.setCode(WebsocketCode.WEBSOCKET_SEND_HEART);
				message.setSendTime(new Date(System.currentTimeMillis()));
				message.setMessage("����");
				// msg.setUserSendId(userInfo.getId());
				// msg.setUserReceiveId(userInfo.getId());
				WebSocketService.this.sendMessage(message);
				break;
			case HANLER_RECONNECT:
				recontent();
				break;
			default:
				break;
			}
		}

	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		System.out.println("WebSocketService������......");
//		System.out.println("WebSocketService IP:" + Util.getLocalIpAddress());
		dbHelper = DBHelper.getDbHelper(this);
		isDestoryServer=false ;
		if (!mConnection.isConnected())
		{
			appContext = (AppContext) this.getApplication();
			threadHeartFlag=false ;
			threadReconnectFalg=true ;
			if (reContentThread == null || !reContentThread.isAlive())
			{
//				UIHelper.ToastMessage(WebSocketService.this, "�����߳�..contentThread", true);
				reContentThread = new ReConnectThread();
				reContentThread.start();
			}
//			start();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy()
	{
		isDestoryServer=true;
		mConnection.disconnect();
		threadHeartFlag=false ;
		threadReconnectFalg=false ;
		UIHelper.ToastMessage(this, "Service destory ", true);
		super.onDestroy();

	}

	public class MyBinder extends Binder

	{

		public WebSocketService getWebSocketServiceInstance()
		{
			return WebSocketService.this;
		}
	}

	public void recontent(){
		if (mConnection.isConnected())
		{
			mConnection.disconnect();
//			Toast.makeText(WebSocketService.this, "mConnection �Ͽ�����....", 1000).show();
		}
		reContentWebSocket();
	}
	
	public void reContentWebSocket()
	{
		threadHeartFlag=false ;
//		threadReconnectFalg=false ;
		mConnection = new WebSocketConnection();
		start();
//		Toast.makeText(WebSocketService.this, "WebSocket ����....", 1000).show();
	}

	private WebSocket mConnection = new WebSocketConnection();

	private void start()
	{

		if (appContext.isLogin())
		{
			wsUri = URLs.WSURI + "?userId=" + appContext.getUserInfo().getId();
		} else
		{
			wsUri = URLs.WSURI;
		}
		try
		{
			mConnection.connect(wsUri, new WebSocketConnectionHandler()
			{
				@Override
				public void onOpen()
				{
					UIHelper.ToastMessage(WebSocketService.this, "onOpen", true);
					threadHeartFlag=true ;
					threadReconnectFalg=false;
					heartReSendTime=0 ;
					if (hearttThread == null || !hearttThread.isAlive())
					{
						UIHelper.ToastMessage(WebSocketService.this, "�����߳�..hearttThread", true);
						hearttThread = new HeartThread();
						hearttThread.start();
					}
				}

				@Override
				public void onTextMessage(String payload)
				{
//					UIHelper.ToastMessage(WebSocketService.this, payload, true);
					System.out.println("WebSocketService:" + payload);
					dealMessage(payload);
					Intent intent = new Intent();
					intent.setAction("com.flying.xiao.WebSocketMsgReceive");
					intent.putExtra("message", payload);
					sendBroadcast(intent);
				}

				@Override
				public void onClose(int code, String reason)
				{
					UIHelper.ToastMessage(WebSocketService.this, "onClose��"+reason, true);
					dbHelper.updateUserOffLine(); // �Ͽ����� ����������״̬��Ϊfalse
					Intent intent = new Intent();
					WMessage msg = new WMessage();
					msg.setCode(WebsocketCode.WEBSOCKET_CODE_ONCLOSE);
					msg.setMessage("�Ͽ�����...");
					intent.setAction("com.flying.xiao.WebSocketMsgReceive");
					intent.putExtra("message", msg.toJson());
					sendBroadcast(intent);
					threadHeartFlag=false ;
					threadReconnectFalg=true ;
					if (reContentThread == null || !reContentThread.isAlive())
					{
						UIHelper.ToastMessage(WebSocketService.this, "�����߳�..reContentThread", true);
						reContentThread = new ReConnectThread();
						reContentThread.start();
					}

				}
			});
		} catch (WebSocketException e)
		{
			UIHelper.ToastMessage(WebSocketService.this, "error:" + e.getMessage(), true);
		}
	}

	/**
	 * ������յ�d��Ϣ
	 * 
	 * @param message
	 */
	public void dealMessage(String message)
	{
		Gson gson = new Gson();
		try
		{
			WMessage msg = gson.fromJson(message, WMessage.class);
			long sendId = msg.getUserSendId();
			long receive = msg.getUserReceiveId();
			switch (msg.getCode())
			{
			case WebsocketCode.WEBSOCKET_CODE_FRIEND_LIST:
				WFriends friends = gson.fromJson(message, WFriends.class);
				// listManager.getMyOnlineFriend().addAll(friends.getFriends());
				dbHelper.updateUserOffLine();
				if (friends == null || friends.getFriends() == null)
					return;
				for (WFriend wf : friends.getFriends())
				{
					dbHelper.updateUserOnlineOrOffLine(wf.getFriendUserId(), true);
				}
				break;
			case WebsocketCode.WEBSOCKET_CODE_FRIEND_ONLINE:
				dbHelper.updateUserOnlineOrOffLine(sendId, true);
				// WFriend wf=new WFriend();
				// wf.setFriendUserId(sendId);
				// listManager.getMyOnlineFriend().add(wf);
				// ListManager.getContentMangerShare().setMyFriendOnline(sendId);
				break;
			case WebsocketCode.WEBSOCKET_CODE_FRIEND_OFFLINE:
				dbHelper.updateUserOnlineOrOffLine(sendId, false);
				// listManager.removeOnlineFriendById(sendId);
				// ListManager.getContentMangerShare().setMyFriendOffLine(sendId);
				break;
			case WebsocketCode.WEBSOCKET_SEND_MESSAGE_TEXT:
				ChatMessage cm = ChatMessage.getInstance(msg, false, appContext);
				dbHelper.insertMsg(cm);
				WMessage wm = new WMessage();
				wm.setCode(WebsocketCode.WEBSOCKET_SEND_TO_SUCCESS);
				wm.setMessage("���͵���");
				wm.setMessageId(cm.getMessageId());
				// wm.setMsg(cm.getMessageId()+"");
				wm.setUserSendId(receive);
				wm.setUserReceiveId(sendId);
				sendMessage(wm);
				break;
			case WebsocketCode.WEBSOCKET_SEND_TO_SUCCESS: // ��Ϣ���ʹ�
				dbHelper.updateMsgSendTo(msg.getMessageId());
				UIHelper.ToastMessage(appContext, "��Ϣ���ʹ�...", true);
				break;
			case WebsocketCode.WEBSOCKET_SEND_TO_SERVER: // ��Ϣ���͵�������
				dbHelper.updateMsgSendTo(msg.getMessageId());
				UIHelper.ToastMessage(appContext, "��Ϣ���ʹ�...������", true);
				break;
			case WebsocketCode.WEBSOCKET_OFFLINE_MESSAGE: // ������Ϣ
				WOfflineMessages offlineMessage = gson.fromJson(message, WOfflineMessages.class);
				if (offlineMessage != null)
				{
					List<OfflineMessage> list = offlineMessage.getOfflineMessages();
					if (list != null && list.size() > 0)
					{
						dbHelper.insertOfflineMessage(list);
					}
				}

				break;
			case WebsocketCode.WEBSOCKET_SEND_HEART:
				isSendtoHeart = true;
				heartReSendTime = 0;
				break;
			default:
				break;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void sendMessage(WMessage message)
	{
		if (mConnection != null && mConnection.isConnected())
		{
			mConnection.sendTextMessage(message.toJson());
		} else
		{
			UIHelper.ToastMessage(getApplicationContext(), "mConnection is not Connected", true);
		}
	}

	public class HeartThread extends Thread
	{

		@Override
		public void run()
		{
			super.run();
			while (threadHeartFlag && heartReSendTime < 5)
			{
				isSendtoHeart = false;
				handler.sendEmptyMessage(HANLER_SEND_HEART);
				try
				{
					Thread.sleep(10000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				if (!isSendtoHeart)
				{// ���û���ͷ�
					heartReSendTime++;
				}
			}

			threadHeartFlag = false; // ���� ÿ��5sһֱ��������
			threadReconnectFalg=true ;
			if (reContentThread == null || !reContentThread.isAlive())
			{
				reContentThread = new ReConnectThread();
				reContentThread.start();
			}
			// UIHelper.ToastMessage(WebSo cketService.this, "���� --����..");

		}
	}

	public class ReConnectThread extends Thread
	{

		@Override
		public void run()
		{
			super.run();
			while (threadReconnectFalg&&!isDestoryServer)
			{
				heartReSendTime = 0;
				// isOnline=true;
				handler.sendEmptyMessage(HANLER_RECONNECT);
				try
				{
					Thread.sleep(5000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

	}
}
