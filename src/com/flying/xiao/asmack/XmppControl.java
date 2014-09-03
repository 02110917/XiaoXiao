package com.flying.xiao.asmack;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.flying.xiao.app.AppContext;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.entity.ChatMessage;

public class XmppControl
{
	private static XmppControl control = null;
	private static AppContext appContext = null;

	private XmppControl() {
	}

	public static XmppControl getShare(Context context) {
		if (control == null)
			control = new XmppControl();
		appContext = (AppContext) context.getApplicationContext();
		return control;
	}
	
	/**
	 * ��¼��openfire������  ��¼ʧ���������
	 * @param account
	 * @param password
	 * @param handler
	 */
	public void login(final String account, final String password,final Handler handler){
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				int i=0;
				while(!XmppConnection.getInstance(appContext).login(account, password,handler)&&i<5){
					i++;
					try
					{
						Thread.sleep(5000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				if(i==5){ //��¼ʧ��
					handler.sendEmptyMessage(Constant.XmppHandlerMsgCode.HANDLER_CODE_LOGIN_FAILED);
				}else{
					handler.sendEmptyMessage(Constant.XmppHandlerMsgCode.HANDLER_CODE_LOGIN_SUCCESS);
				}
			}
		}).start();
	}
	
	public void sendMessage(final ChatMessage chatMessage,final Handler handler){
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				
				int i=0;
				while(!XmppConnection.getInstance(appContext).sendMsg(chatMessage.getTo(), chatMessage.getBody())&&i<5){
					i++;
				}
				Message msg=new Message();
				if(i==5){ //����ʧ��
					msg.what=Constant.XmppHandlerMsgCode.HANDLER_CODE_SEND_MESSAGE_FAILED;
				}else{
					msg.what=Constant.XmppHandlerMsgCode.HANDLER_CODE_SEND_MESSAGE_SUCCESS;
				}
				msg.obj=chatMessage;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/**
	 * ��Ӻ���
	 * @param userName
	 * @param nickName
	 * @param handler
	 */
	public void addFriend(final String userName,final String nickName,final Handler handler){
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				int i=0;
				while(!XmppConnection.getInstance(appContext).addUser(userName, nickName)&&i<2){
					i++;
				}
				Message msg=new Message();
				if(i==2){ //����ʧ��
					msg.what=Constant.XmppHandlerMsgCode.HANDLER_ADD_PRIEND_FAILD;
				}else{
					msg.what=Constant.XmppHandlerMsgCode.HANDLER_ADD_PRIEND_SUCCESS;
				}
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/**
	 * ��ȡ�ҵ����к����б�
	 * @param handler
	 */
	public void getAllFriends(final Handler handler){
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				XmppConnection.getInstance(appContext).getAllFriends(handler);
			}
		}).start();
	}
	/**
	 * ע����ǰ�û�
	 */
	public void deleteAccount(){
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				XmppConnection.getInstance(appContext).deleteAccount();
			}
		}).start();
	}
}
