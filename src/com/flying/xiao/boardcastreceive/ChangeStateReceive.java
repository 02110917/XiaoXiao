package com.flying.xiao.boardcastreceive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flying.xiao.ChatActivity;
import com.flying.xiao.MyFriends;
import com.flying.xiao.constant.Constant;
import com.google.gson.Gson;
public class ChangeStateReceive extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		int type=intent.getIntExtra("type", 0);
		Gson gson=new Gson();
		if(context instanceof MyFriends){
			System.out.println("WebSocketMsgReceive  --- content instanceof MyFriends");
			if(type==Constant.BroadCastReceiveType.BROAD_RECEIVE_CHANGE_FRIENDS_STATE)
				((MyFriends)context).changeFriendState();
		}
		if(context instanceof ChatActivity){
			if(type==Constant.BroadCastReceiveType.BROAD_RECEIVE_CHANGE_CHAT_STATE)
				((ChatActivity)context).notifyDataSetChanged();
		}
	}
	
	
	

}
