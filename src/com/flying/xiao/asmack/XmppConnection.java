package com.flying.xiao.asmack;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.os.Handler;
import android.util.Log;

import com.flying.xiao.app.AppContext;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.entity.ChatMessage;
import com.flying.xiao.entity.XUserInfo;

/**
 * XmppConnection ������
 * 
 * @author zhangmin
 *
 */
public class XmppConnection
{
	private int SERVER_PORT = 5222;
	private String SERVER_HOST = "115.29.79.84";
	private String SERVER_NAME = "115.29.79.84";
	private XMPPConnection connection = null;
//	private String SERVER_NAME = "ubuntuserver4java";
	private static XmppConnection xmppConnection = null;
	private TaxiConnectionListener connectionListener;
	private static AppContext appContxt;
	private static Handler handler;

	public XmppConnection(AppContext appContxt, Handler handler)
	{
		this.appContxt = appContxt;
		this.handler = handler;
	}

	/**
	 * ����ģʽ
	 * 
	 * @return
	 */
	synchronized public static XmppConnection getInstance(AppContext appContxt)
	{
		if (xmppConnection == null)
		{
			xmppConnection = new XmppConnection(appContxt, handler);
		}
		return xmppConnection;
	}

	/**
	 * ��������
	 */
	public XMPPConnection getConnection()
	{
		if (connection == null || (!connection.isConnected()))
		{
			openConnection();
		}
		return connection;
	}

	/**
	 * ������
	 */
	public boolean openConnection()
	{
		try
		{
			if (null == connection || !connection.isAuthenticated())
			{
				XMPPConnection.DEBUG_ENABLED = false;// ����DEBUGģʽ
				// ��������
				ConnectionConfiguration config = new ConnectionConfiguration(SERVER_HOST, SERVER_PORT,
						SERVER_NAME);
				config.setReconnectionAllowed(true);// ��������
				config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
				config.setSendPresence(false); // ״̬��Ϊ���ߣ�Ŀ��Ϊ��ȡ������Ϣ
				config.setSASLAuthenticationEnabled(false); // �Ƿ����ð�ȫ��֤
				config.setTruststorePath("/system/etc/security/cacerts.bks");
				config.setTruststorePassword("changeit");
				config.setTruststoreType("bks");
				connection = new XMPPConnection(config);
				connection.connect();// ���ӵ�������
				// ���ø���Provider����������ã�����޷���������
				configureConnection(ProviderManager.getInstance());

				return true;
			}
		} catch (XMPPException xe)
		{
			xe.printStackTrace();
			connection = null;
		}
		return false;
	}

	/**
	 * �ر�����
	 */
	public void closeConnection()
	{
		if (connection != null)
		{
			// �Ƴ����Ӽ���
			// connection.removeConnectionListener(connectionListener);
			if (connection.isConnected())
				connection.disconnect();
			connection = null;
		}
		Log.i("XmppConnection", "�ر�����");
	}

	/**
	 * ��¼
	 * 
	 * @param account
	 *            ��¼�ʺ�
	 * @param password
	 *            ��¼����
	 * @return
	 */
	public boolean login(String account, String password, final Handler handler)
	{
		try
		{
			if (getConnection() == null)
				return false;
			if (getConnection().isAuthenticated())
				return false;
			getConnection().login(account.replace("@", "$"), password);
			// ��������״̬
			// Presence presence = new Presence(Presence.Type.available);
			// getConnection().sendPacket(presence);
			// ������Ӽ���
			connectionListener = new TaxiConnectionListener(appContxt, handler);
			getConnection().addConnectionListener(connectionListener);
			getConnection().getRoster().addRosterListener(new MyRosterListener(handler)); // ��������״̬
			getHisMessage(handler); // ��ȡ������Ϣ
			getOnLine(handler); // ��ȡ��Ϣ
			getAllFriends(handler);// ��ȡ�����б�
			// �����߳� ��ȡ״̬
//			new Thread(new Runnable()
//			{
//
//				@Override
//				public void run()
//				{
//
//				}
//			}).start();

			return true;
		} catch (XMPPException xe)
		{
			xe.printStackTrace();
		}
		return false;
	}

	private class MyRosterListener implements RosterListener
	{

		private Handler handler;

		public MyRosterListener(Handler handler)
		{
			this.handler = handler;
		}

		@Override
		public void entriesAdded(Collection<String> arg0)
		{
			System.out.println("MyRosterListener---entriesAdded");
			List<XUserInfo> userInfos=new ArrayList<XUserInfo>();
			for(String userName:arg0){
				XUserInfo user = new XUserInfo();
				user.setUserName(StringUtils.parseName(userName).replace("$", "@"));
				userInfos.add(user);
			}
			android.os.Message msg = new android.os.Message();
			msg.what = Constant.XmppHandlerMsgCode.HANDLER_FRIEND_ADD;
			msg.obj = userInfos;
			handler.sendMessage(msg);
		}

		@Override
		public void entriesDeleted(Collection<String> arg0)
		{
			System.out.println("MyRosterListener---entriesDeleted");
		}

		@Override
		public void entriesUpdated(Collection<String> arg0)
		{
			System.out.println("MyRosterListener---entriesUpdated");
		}

		@Override
		public void presenceChanged(Presence presence)
		{
			System.out.println("MyRosterListener---presenceChanged"+presence.getFrom()+"  "+presence.isAvailable());
			String user = presence.getFrom();
			Presence bestPresence = getConnection().getRoster().getPresence(user);
			android.os.Message msg = new android.os.Message();
			msg.what = Constant.XmppHandlerMsgCode.HANDLER_FRIEND_STATE_CHANGE;
			msg.obj = bestPresence;
			handler.sendMessage(msg);
		}

	}

	/**
	 * �����û�״̬
	 */
	public void setPresence(int code)
	{
		XMPPConnection con = getConnection();
		if (con == null)
			return;
		Presence presence;
		switch (code)
		{
		case 0:
			presence = new Presence(Presence.Type.available);
			con.sendPacket(presence);
			Log.v("state", "��������");
			break;
		case 1:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.chat);
			con.sendPacket(presence);
			Log.v("state", "����Q�Ұ�");
			break;
		case 2:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.dnd);
			con.sendPacket(presence);
			Log.v("state", "����æµ");
			break;
		case 3:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.away);
			con.sendPacket(presence);
			Log.v("state", "�����뿪");
			break;
		case 4:
			Roster roster = con.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for (RosterEntry entry : entries)
			{
				presence = new Presence(Presence.Type.unavailable);
				presence.setPacketID(Packet.ID_NOT_AVAILABLE);
				presence.setFrom(con.getUser());
				presence.setTo(entry.getUser());
				con.sendPacket(presence);
				Log.v("state", presence.toXML());
			}
			// ��ͬһ�û��������ͻ��˷�������״̬
			presence = new Presence(Presence.Type.unavailable);
			presence.setPacketID(Packet.ID_NOT_AVAILABLE);
			presence.setFrom(con.getUser());
			presence.setTo(StringUtils.parseBareAddress(con.getUser()));
			con.sendPacket(presence);
			Log.v("state", "��������");
			break;
		case 5:
			presence = new Presence(Presence.Type.unavailable);
			con.sendPacket(presence);
			Log.v("state", "��������");
			break;
		default:
			break;
		}
	}

	/**
	 * ��ȡ������
	 * 
	 * @return �����鼯��
	 */
	public List<RosterGroup> getGroups()
	{
		if (getConnection() == null)
			return null;
		List<RosterGroup> grouplist = new ArrayList<RosterGroup>();
		Collection<RosterGroup> rosterGroup = getConnection().getRoster().getGroups();
		Iterator<RosterGroup> i = rosterGroup.iterator();
		while (i.hasNext())
		{
			grouplist.add(i.next());
		}
		return grouplist;
	}

	/**
	 * ��ȡĳ������������к���
	 * 
	 * @param roster
	 * @param groupName
	 *            ����
	 * @return
	 */
	public List<RosterEntry> getEntriesByGroup(String groupName)
	{
		if (getConnection() == null)
			return null;
		List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
		RosterGroup rosterGroup = getConnection().getRoster().getGroup(groupName);
		Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext())
		{
			Entrieslist.add(i.next());
		}
		return Entrieslist;
	}

	/**
	 * ��ȡ���к�����Ϣ
	 * 
	 * @return
	 */
	public void getAllFriends(Handler handler)
	{
		if (getConnection() == null)
			return;
		List<XUserInfo> users = new ArrayList<XUserInfo>();
		Collection<RosterEntry> rosterEntry = getConnection().getRoster().getEntries();
		try
		{
			Thread.sleep(1000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Iterator<RosterEntry> i = rosterEntry.iterator();
		for(RosterEntry entry:rosterEntry){
			Presence p = getConnection().getRoster().getPresence(entry.getUser());
			XUserInfo user = new XUserInfo();
			user.setUserName(StringUtils.parseName(entry.getUser()).replace("$", "@"));
			user.setOnline(p.isAvailable());
			System.out.println("state:"+p.isAvailable());
			users.add(user);
		}
		android.os.Message msg = new android.os.Message();
		msg.obj = users;
		msg.what = Constant.XmppHandlerMsgCode.HANDLER_CODE_GET_ALL_FRIENDS;
		handler.sendMessage(msg);
	}


	
	
	/**
	 * ���һ������
	 * 
	 * @param groupName
	 * @return
	 */
	public boolean addGroup(String groupName)
	{
		if (getConnection() == null)
			return false;
		try
		{
			getConnection().getRoster().createGroup(groupName);
			Log.v("addGroup", groupName + "�����ɹ�");
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ɾ������
	 * 
	 * @param groupName
	 * @return
	 */
	public boolean removeGroup(String groupName)
	{
		return true;
	}

	/**
	 * ��Ӻ��� �޷���
	 * 
	 * @param userName
	 *            - the user. (e.g. johndoe@jabber.org)
	 * @param name
	 *            - the nickname of the user.
	 * @return
	 */
	public boolean addUser(String userName, String name)
	{
		if (getConnection() == null)
			return false;
		try
		{
			getConnection().getRoster().createEntry(userName.replace("@", "$") + Constant.XMPP_SERVER, name,
					null);
			return true;
		} catch (XMPPException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ��Ӻ��� �з���
	 * 
	 * @param userName
	 * @param name
	 * @param groupName
	 * @return
	 */
	public boolean addUser(String userName, String name, String groupName)
	{
		if (getConnection() == null)
			return false;
		try
		{
			Presence subscription = new Presence(Presence.Type.subscribed);
			subscription.setTo(userName);
			userName += "@" + getConnection().getServiceName();
			getConnection().sendPacket(subscription);
			getConnection().getRoster().createEntry(userName, name, new String[]
			{ groupName });
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ɾ������
	 * 
	 * @param userName
	 * @return
	 */
	public boolean removeUser(String userName)
	{
		if (getConnection() == null)
			return false;
		try
		{
			RosterEntry entry = null;
			if (userName.contains("@"))
				entry = getConnection().getRoster().getEntry(userName);
			else
				entry = getConnection().getRoster().getEntry(
						userName + "@" + getConnection().getServiceName());
			if (entry == null)
				entry = getConnection().getRoster().getEntry(userName);
			getConnection().getRoster().removeEntry(entry);

			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ��ѯ�û�
	 * 
	 * @param userName
	 * @return
	 * @throws XMPPException
	 */
	public List<HashMap<String, String>> searchUsers(String userName)
	{
		if (getConnection() == null)
			return null;
		HashMap<String, String> user = null;
		List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
		try
		{
			new ServiceDiscoveryManager(getConnection());

			UserSearchManager usm = new UserSearchManager(getConnection());

			Form searchForm = usm.getSearchForm(getConnection().getServiceName());
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("userAccount", true);
			answerForm.setAnswer("userPhote", userName);
			ReportedData data = usm.getSearchResults(answerForm, "search" + getConnection().getServiceName());

			Iterator<Row> it = data.getRows();
			Row row = null;
			while (it.hasNext())
			{
				user = new HashMap<String, String>();
				row = it.next();
				user.put("userAccount", row.getValues("userAccount").next().toString());
				user.put("userPhote", row.getValues("userPhote").next().toString());
				results.add(user);
				// �����ڣ����з���,UserNameһ���ǿգ����������������裬һ���ǿ�
			}
		} catch (XMPPException e)
		{
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * �޸�����
	 * 
	 * @param connection
	 * @param status
	 */
	public void changeStateMessage(String status)
	{
		if (getConnection() == null)
			return;
		Presence presence = new Presence(Presence.Type.available);
		presence.setStatus(status);
		getConnection().sendPacket(presence);
	}

	/**
	 * �ļ�ת�ֽ�
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private byte[] getFileBytes(File file) throws IOException
	{
		BufferedInputStream bis = null;
		try
		{
			bis = new BufferedInputStream(new FileInputStream(file));
			int bytes = (int) file.length();
			byte[] buffer = new byte[bytes];
			int readBytes = bis.read(buffer);
			if (readBytes != buffer.length)
			{
				throw new IOException("Entire file not read");
			}
			return buffer;
		} finally
		{
			if (bis != null)
			{
				bis.close();
			}
		}
	}

	/**
	 * ɾ����ǰ�û�
	 * 
	 * @return
	 */
	public boolean deleteAccount()
	{
		if (getConnection() == null)
			return false;
		try
		{
			// isAuthenticated()
			// Returns true if currently authenticated by successfully calling
			// the login method.
			if (getConnection().isAuthenticated())
			{
				setPresence(5);
				getConnection().getAccountManager().deleteAccount();
			}
			return true;
		} catch (XMPPException e)
		{
			return false;
		}
	}

	/**
	 * �����ļ�
	 * 
	 * @param user
	 * @param filePath
	 */
	public void sendFile(String user, String filePath)
	{
		if (getConnection() == null)
			return;
		// �����ļ����������
		FileTransferManager manager = new FileTransferManager(getConnection());

		// ����������ļ�����
		OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(user);

		// �����ļ�
		try
		{
			transfer.sendFile(new File(filePath), "You won't believe this!");
		} catch (XMPPException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * ������Ϣ
	 * 
	 * @param to
	 * @param msg
	 */
	public boolean sendMsg(String to, String msg)
	{
		if (getConnection() == null)
			return false;
		Chat chat = getConnection().getChatManager().createChat(to.replace("@", "$") + Constant.XMPP_SERVER,
				null);
		try
		{
			chat.sendMessage(msg);
			return true;
		} catch (XMPPException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ��ȡ������Ϣ
	 * 
	 * @return
	 */
	public void getHisMessage(Handler handler)
	{
		List<ChatMessage> msglist = new ArrayList<ChatMessage>();
		if (getConnection() == null)
			return;
		try
		{
			OfflineMessageManager offlineManager = new OfflineMessageManager(getConnection());
			Iterator<Message> it = offlineManager.getMessages();

			int count = offlineManager.getMessageCount();
			if (count <= 0)
				return;
			while (it.hasNext())
			{
				Message message = it.next();
				ChatMessage cmsg = ChatMessage.getInstance(message, false, appContxt);
				msglist.add(cmsg);
			}
			android.os.Message msg = new android.os.Message();
			msg.obj = msglist;
			msg.what = Constant.XmppHandlerMsgCode.HANDLER_CODE_GET_OFF_LINE_MESSAGE;
			handler.sendMessage(msg);
			offlineManager.deleteMessages();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally{
			setPresence(0); // ����������״̬
		}
		
	}

	/**
	 * ��ȡ������Ϣ
	 * 
	 * @return
	 */
	public void getOnLine(final Handler handler)
	{
		if (getConnection() == null)
			return;
		ChatManager cm = getConnection().getChatManager();
		cm.addChatListener(new ChatManagerListener()
		{
			@Override
			public void chatCreated(Chat chat, boolean able)
			{
				chat.addMessageListener(new MessageListener()
				{
					@Override
					public void processMessage(Chat chat2, Message message)
					{
						Log.d("connectMethod:getOnLine()", message.getBody() + message.getFrom());
						android.os.Message msg = new android.os.Message();
						msg.what = Constant.XmppHandlerMsgCode.HANDLER_CODE_GET_MESSAGE;
						msg.obj = message;
						handler.sendMessage(msg);
					}
				});
			}
		});
	}

	/**
	 * �ж�OpenFire�û���״̬ strUrl : url��ʽ -
	 * http://my.openfire.com:9090/plugins/presence
	 * /status?jid=user1@SERVER_NAME&type=xml ����ֵ : 0 - �û�������; 1 - �û�����; 2 -
	 * �û����� ˵�� ������Ҫ�� OpenFire���� presence �����ͬʱ�����κ��˶����Է���
	 */
	public int IsUserOnLine(String user)
	{
		String url = "http://" + SERVER_HOST + ":9090/plugins/presence/status?" + "jid=" + user + "@"
				+ SERVER_NAME + "&type=xml";
		int shOnLineState = 0; // ������
		try
		{
			URL oUrl = new URL(url);
			URLConnection oConn = oUrl.openConnection();
			if (oConn != null)
			{
				BufferedReader oIn = new BufferedReader(new InputStreamReader(oConn.getInputStream()));
				if (null != oIn)
				{
					String strFlag = oIn.readLine();
					oIn.close();
					System.out.println("strFlag" + strFlag);
					if (strFlag.indexOf("type=\"unavailable\"") >= 0)
					{
						shOnLineState = 2;
					}
					if (strFlag.indexOf("type=\"error\"") >= 0)
					{
						shOnLineState = 0;
					} else if (strFlag.indexOf("priority") >= 0 || strFlag.indexOf("id=\"") >= 0)
					{
						shOnLineState = 1;
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return shOnLineState;
	}

	/**
	 * ����providers�ĺ��� ASmack��/META-INFȱ��һ��smack.providers �ļ�
	 * 
	 * @param pm
	 */
	public void configureConnection(ProviderManager pm)
	{

		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try
		{
			pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e)
		{
			Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
		}

		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());

		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());

		// Chat State
		pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());

		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());

		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

		// Version
		try
		{
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e)
		{
			// Not sure what's happening here.
		}

		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());

		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());

		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}

}