package com.flying.xiao.asmack;

import java.util.Timer;
import java.util.TimerTask;

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
	 * 登录到openfire服务器  登录失败重试五次
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
				if(i==5){ //登录失败
					handler.sendEmptyMessage(Constant.XmppHandlerMsgCode.HANDLER_CODE_LOGIN_FAILED);
				}else{
					handler.sendEmptyMessage(Constant.XmppHandlerMsgCode.HANDLER_CODE_LOGIN_SUCCESS);
				}
			}
		}).start();
	}
	
	public void sendMessage(final ChatMessage chatMessage,final Handler handler){
		Timer timer=new Timer();
		final SendMsgTherad thread=new SendMsgTherad(chatMessage, handler,timer);
		thread.start();
		TimerTask t=new TimerTask()
		{
			
			@Override
			public void run()
			{
				Message msg=new Message();
				msg.obj=chatMessage;
				msg.what=Constant.XmppHandlerMsgCode.HANDLER_CODE_SEND_MESSAGE_FAILED;
				handler.sendMessage(msg);
				if(thread.isFalg()){
					thread.setFalg(false);
				}
				
			}
		};
		timer.schedule(t, 5000);

	}
	class SendMsgTherad extends Thread{
		private ChatMessage chatMessage;
		private Handler handler;
		private Timer timer ;
		private boolean falg=true;
		public SendMsgTherad( ChatMessage chatMessage, Handler handler,Timer timer){
			this.chatMessage=chatMessage;
			this.handler=handler;
			this.timer=timer ;
		}
		@Override
		public void run()
		{
			
			int i=0;
			while(falg&&!XmppConnection.getInstance(appContext).sendMsg(chatMessage.getTo(), chatMessage.getBody())&&i<5){
				i++;
			}
			Message msg=new Message();
			if(i==5){ //发送失败
				msg.what=Constant.XmppHandlerMsgCode.HANDLER_CODE_SEND_MESSAGE_FAILED;
			}else{
				msg.what=Constant.XmppHandlerMsgCode.HANDLER_CODE_SEND_MESSAGE_SUCCESS;
			}
			if(timer!=null){
				timer.cancel();
				timer=null;
			}
			falg=false ;
			msg.obj=chatMessage;
			handler.sendMessage(msg);
		}
		public void setFalg(boolean falg)
		{
			this.falg = falg;
		}
		public boolean isFalg()
		{
			return falg;
		}
		
	}
	/**
	 * 添加好友
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
				if(i==2){ //发送失败
					msg.what=Constant.XmppHandlerMsgCode.HANDLER_ADD_PRIEND_FAILD;
				}else{
					msg.what=Constant.XmppHandlerMsgCode.HANDLER_ADD_PRIEND_SUCCESS;
				}
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/**
	 * 获取我的所有好友列表
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
	 * 注销当前用户
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
