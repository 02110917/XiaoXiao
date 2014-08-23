package com.flying.xiao.boardcastreceive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flying.xiao.ChatActivity;
import com.flying.xiao.MyFriends;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant.WebsocketCode;
import com.flying.xiao.websocket.WMessage;
import com.google.gson.Gson;
public class WebSocketMsgReceive extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String message=intent.getStringExtra("message");
		Gson gson=new Gson();
		try
		{
			WMessage msg=gson.fromJson(message, WMessage.class);
			long sendId=msg.getUserSendId();
			long receive=msg.getUserReceiveId();
			switch (msg.getCode())
			{
//			case WebsocketCode.WEBSOCKET_CODE_FRIEND_LIST:
//				WFriends friends=gson.fromJson(message, WFriends.class);
////				listManager.getMyOnlineFriend().addAll(friends.getFriends());
//				for(WFriend wf:friends.getFriends()){
//					dbHelper.updateUserOnlineOrOffLine(wf.getFriendUserId(), true);
//				}
//				break ;
//			case WebsocketCode.WEBSOCKET_CODE_FRIEND_ONLINE:
//				dbHelper.updateUserOnlineOrOffLine(sendId, true);
////				WFriend wf=new WFriend();
////				wf.setFriendUserId(sendId);
////				listManager.getMyOnlineFriend().add(wf);
////				ListManager.getContentMangerShare().setMyFriendOnline(sendId);
//				break ;
//			case WebsocketCode.WEBSOCKET_CODE_FRIEND_OFFLINE:
//				dbHelper.updateUserOnlineOrOffLine(sendId, false);
////				listManager.removeOnlineFriendById(sendId);
////				ListManager.getContentMangerShare().setMyFriendOffLine(sendId);
//				break ;
			case WebsocketCode.WEBSOCKET_CODE_ONCLOSE:
//				UIHelper.ToastMessage(context, "websocket---¶Ï¿ªÁ¬½Ó",true);
				break ;
			default:
				break;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		if(context instanceof MyFriends){
			System.out.println("WebSocketMsgReceive  --- content instanceof MyFriends");
			((MyFriends)context).changeFriendState();
		}
		if(context instanceof ChatActivity){
			((ChatActivity)context).notifyDataSetChanged();
		}
	}
	
	
	

}
