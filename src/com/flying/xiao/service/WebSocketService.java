package com.flying.xiao.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.flying.xiao.app.AppContext;
import com.flying.xiao.asmack.XmppControl;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.constant.Constant.WebsocketCode;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.db.DBHelper;
import com.flying.xiao.entity.ChatMessage;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.websocket.WBase;
import com.flying.xiao.websocket.WMessage;
import com.flying.xiao.websocket.WPushUpdate;
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

	// private boolean isOnline = false;
	private boolean threadHeartFlag = false;
	private boolean threadReconnectFalg = false;
	private boolean isSendtoHeart = false; // �����Ƿ��͵���
	private boolean isDestoryServer;
	private int heartReSendTime = 0; // �����ط�����

	private static final int HANLER_RECONNECT = 1;
	private static final int HANLER_SEND_HEART = 2;

	private ReConnectThread reContentThread = null;

	private HeartThread hearttThread = null;
	private static boolean isXmppLogin = false;

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
			Intent intent = new Intent();
			intent.setAction("com.flying.xiao.ChangeStateReceive");
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
			case Constant.XmppHandlerMsgCode.HANDLER_CODE_LOGIN_SUCCESS: // xmpp��¼�ɹ�
				appContext.setXmppLogin(true);
				isXmppLogin = true;
				UIHelper.ToastMessage(appContext, "xmpp��¼�ɹ�", true);
				break;
			case Constant.XmppHandlerMsgCode.HANDLER_CODE_LOGIN_FAILED: // xmpp��¼ʧ��
				appContext.setXmppLogin(false);
				UIHelper.ToastMessage(appContext, "xmpp��¼ʧ��", true);
				break;
			case Constant.XmppHandlerMsgCode.HANDLER_CODE_GET_MESSAGE: // ���յ���Ϣ
				org.jivesoftware.smack.packet.Message xmsg = (org.jivesoftware.smack.packet.Message) msg.obj;
				ChatMessage cm = ChatMessage.getInstance(xmsg, false, appContext);
				dbHelper.insertMsg(cm);
				// ����notification
				UIHelper.sendNotification(WebSocketService.this, cm, null,
						Constant.XmppHandlerMsgCode.HANDLER_CODE_GET_MESSAGE);
				System.out.println("��ʱ��Ϣ��" + cm.getBody());

				UIHelper.ToastMessage(appContext, "xmpp���յ���Ϣ:" + cm.getBody(), true);
				intent.putExtra("type", Constant.BroadCastReceiveType.BROAD_RECEIVE_CHANGE_CHAT_STATE);
				sendBroadcast(intent);
				break;
			case Constant.XmppHandlerMsgCode.HANDLER_CODE_GET_OFF_LINE_MESSAGE: // ���յ�������Ϣ
				List<ChatMessage> msglist = (List<ChatMessage>) msg.obj;
				if (msglist != null && msglist.size() > 0)
				{
					dbHelper.insertOfflineMessage(msglist);
				}
				ChatMessage chatMessage = msglist.get(msglist.size() - 1);
				// ����notification
				UIHelper.sendNotification(WebSocketService.this, chatMessage, null,
						Constant.XmppHandlerMsgCode.HANDLER_CODE_GET_OFF_LINE_MESSAGE);
				System.out.println("������Ϣ�Ĵ�С��" + msglist.size() + "���һ��������Ϣ��" + chatMessage.getBody());

				UIHelper.ToastMessage(appContext, "xmpp���յ�������Ϣ:", true);
				intent.putExtra("type", Constant.BroadCastReceiveType.BROAD_RECEIVE_CHANGE_CHAT_STATE);
				sendBroadcast(intent);
				break;
			case Constant.XmppHandlerMsgCode.HANDLER_CODE_SEND_MESSAGE_SUCCESS: // ��Ϣ���ͳɹ�
				ChatMessage cmsg = (ChatMessage) msg.obj;
				dbHelper.updateMsgSendTo(cmsg.getTime());
				UIHelper.ToastMessage(appContext, "��Ϣ���ʹ�...", true);
				intent.putExtra("type", Constant.BroadCastReceiveType.BROAD_RECEIVE_CHANGE_CHAT_STATE);
				sendBroadcast(intent);
				break;
			case Constant.XmppHandlerMsgCode.HANDLER_CODE_SEND_MESSAGE_FAILED: // ��Ϣ����ʧ��
				ChatMessage cmsg1 = (ChatMessage) msg.obj;
				dbHelper.updateMsgSendError(cmsg1.getTime());
				UIHelper.ToastMessage(appContext, "��Ϣ����ʧ��...", true);
				intent.putExtra("type", Constant.BroadCastReceiveType.BROAD_RECEIVE_CHANGE_CHAT_STATE);
				sendBroadcast(intent);
				break;
			case Constant.XmppHandlerMsgCode.HANDLER_CODE_GET_ALL_FRIENDS: // ��ȡ�����б�
				List<XUserInfo> users = (List<XUserInfo>) msg.obj;
				dbHelper.insertOrUpdateFriends(users);
				UIHelper.ToastMessage(appContext, "��ȡ�����б�", true);
				Gson gson = new Gson();
				NetControl.getShare(WebSocketService.this).getUserInfosByName(handler, gson.toJson(users));
				intent.putExtra("type", Constant.BroadCastReceiveType.BROAD_RECEIVE_CHANGE_FRIENDS_STATE);
				sendBroadcast(intent);
				break;
			case Constant.XmppHandlerMsgCode.HANDLER_FRIEND_STATE_CHANGE: // ����״̬�ı�
				Presence prensence = (Presence) msg.obj;
				dbHelper.updateUserOnlineOrOffLine(
						StringUtils.parseName(prensence.getFrom()).replace("$", "@"), prensence.isAvailable());
				UIHelper.ToastMessage(appContext, "����״̬�ı�:"
						+ StringUtils.parseName(prensence.getFrom()).replace("$", "@"), true);
				intent.putExtra("type", Constant.BroadCastReceiveType.BROAD_RECEIVE_CHANGE_FRIENDS_STATE);
				sendBroadcast(intent);
				break;
			case Constant.HandlerMessageCode.GET_MY_USERS_SUCCESS:// ���������֮���ȡ���ǵĶ�����Ϣ�������ݿ�
				List<XUserInfo> userinfos = (List<XUserInfo>) msg.obj;
				dbHelper.insertFriends(userinfos);
				// ����notification
				UIHelper.sendNotification(WebSocketService.this, null, userinfos,
						Constant.XmppHandlerMsgCode.HANDLER_CODE_GET_OFF_LINE_MESSAGE);

				intent.putExtra("type", Constant.BroadCastReceiveType.BROAD_RECEIVE_CHANGE_FRIENDS_STATE);
				sendBroadcast(intent);
				break;
			case Constant.HandlerMessageCode.GET_MY_USERS_FAILED:
				UIHelper.ToastMessage(appContext, "���º����б����");
				break;
			case Constant.XmppHandlerMsgCode.HANDLER_ADD_PRIEND_FAILD: // xmpp
																		// add
																		// friend
																		// faild
				UIHelper.ToastMessage(appContext, "��Ӻ���ʧ��,������...");
				break;
			case Constant.XmppHandlerMsgCode.HANDLER_ADD_PRIEND_SUCCESS: //
				UIHelper.ToastMessage(appContext, "��Ӻ��ѳɹ�");
				XmppControl.getShare(WebSocketService.this).getAllFriends(handler);

				break;
			case Constant.XmppHandlerMsgCode.HANDLER_FRIEND_ADD:// ���������Ϊ����
				List<XUserInfo> userNames = (List<XUserInfo>) msg.obj;
				dbHelper.insertOrUpdateFriends(userNames);
				UIHelper.ToastMessage(appContext, "��ȡ�����б�", true);
				Gson g = new Gson();
				NetControl.getShare(WebSocketService.this).getUserInfosByName(handler, g.toJson(userNames));
				intent.putExtra("type", Constant.BroadCastReceiveType.BROAD_RECEIVE_CHANGE_FRIENDS_STATE);
				sendBroadcast(intent);
				break;
			case 1000:
				
				break ;
			default:
				break;
			}
		}

	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		System.out.println("WebSocketService������......");
		appContext = (AppContext) this.getApplication();
		loginToXmpp();
		// System.out.println("WebSocketService IP:" +
		// Util.getLocalIpAddress());
		dbHelper = DBHelper.getDbHelper(appContext);
		isDestoryServer = false;
		start();
		return super.onStartCommand(intent, flags, startId);
	}

	public void loginToXmpp()
	{
		// ���http��¼����xmppδ��½
		if (appContext.isLogin() && (!isXmppLogin))
		{
			// ��¼�������� XMPP
			XmppControl.getShare(this).login(appContext.getUserInfo().getUserName(),
					appContext.getUserInfo().getUserPsd(), handler);
		}
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
		isDestoryServer = true;
		mConnection.disconnect();
		threadHeartFlag = false;
		threadReconnectFalg = false;
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

	public void recontent()
	{
		if (mConnection.isConnected())
		{
			mConnection.disconnect();
			// Toast.makeText(WebSocketService.this, "mConnection �Ͽ�����....",
			// 1000).show();
		}
		reContentWebSocket();
	}

	public void reContentWebSocket()
	{
		threadHeartFlag = false;
		// threadReconnectFalg=false ;
		mConnection = new WebSocketConnection();
		start();
		// Toast.makeText(WebSocketService.this, "WebSocket ����....",
		// 1000).show();
	}

	private WebSocket mConnection = new WebSocketConnection();

	private void start()
	{
		if (mConnection != null && mConnection.isConnected())
			return;
		wsUri = URLs.WSURI;
		try
		{
			mConnection.connect(wsUri, new WebSocketConnectionHandler()
			{
				@Override
				public void onOpen()
				{
					// UIHelper.ToastMessage(WebSocketService.this, "onOpen",
					// true);
					threadHeartFlag = true;
					threadReconnectFalg = false;
					heartReSendTime = 0;
					if (hearttThread == null || !hearttThread.isAlive())
					{
						// UIHelper.ToastMessage(WebSocketService.this,
						// "�����߳�..hearttThread", true);
						hearttThread = new HeartThread();
						hearttThread.start();
					}
				}

				@Override
				public void onTextMessage(String payload)
				{
					// UIHelper.ToastMessage(WebSocketService.this, payload,
					// true);
					System.out.println("WebSocketService:" + payload);
					Intent intent1 = new Intent();
					intent1.setAction("com.flying.xiao.WebSocketMsgReceive");
					intent1.putExtra("message", payload);
					sendBroadcast(intent1);
					dealMessage(payload);
				}

				@Override
				public void onClose(int code, String reason)
				{
					// UIHelper.ToastMessage(WebSocketService.this,
					// "onClose��"+reason, true);
					// dbHelper.updateUserOffLine(); // �Ͽ����� ����������״̬��Ϊfalse
					Intent intent = new Intent();
					WMessage msg = new WMessage();
					msg.setCode(WebsocketCode.WEBSOCKET_CODE_ONCLOSE);
					msg.setMessage("�Ͽ�����...");
					intent.setAction("com.flying.xiao.WebSocketMsgReceive");
					intent.putExtra("message", msg.toJson());
					sendBroadcast(intent);
					threadHeartFlag = false;
					threadReconnectFalg = true;
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
	public void dealMessage(final String message)
	{
		Gson gson = new Gson();
		try
		{
			WBase msg = gson.fromJson(message, WBase.class);
			switch (msg.getCode())
			{
			case WebsocketCode.WEBSOCKET_SEND_HEART:
				isSendtoHeart = true;
				heartReSendTime = 0;
				break;
			case WebsocketCode.WEBSOCKET_PUSH_UPDATE:

				break;
			default:
				break;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * WebSocket������Ϣ
	 * 
	 * @param message
	 */
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

	/**
	 * Xmpp������Ϣ
	 * 
	 * @param message
	 */
	public void sendMessage(ChatMessage message)
	{
		XmppControl.getShare(this).sendMessage(message, handler);
	}

	/**
	 * xmpp��Ӻ���
	 * 
	 * @param userName
	 * @param nickName
	 */
	public void addFriend(String userName, String nickName)
	{
		XmppControl.getShare(this).addFriend(userName, nickName, handler);
	}

	public void getAllFriends()
	{
		XmppControl.getShare(this).getAllFriends(handler);
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
			threadReconnectFalg = true;
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
			while (threadReconnectFalg && !isDestoryServer)
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

	public static boolean isXmppLogin()
	{
		return isXmppLogin;
	}

	public static void setXmppLogin(boolean isXmppLogin)
	{
		WebSocketService.isXmppLogin = isXmppLogin;
	}

}
