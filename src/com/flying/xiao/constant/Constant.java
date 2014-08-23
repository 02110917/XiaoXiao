package com.flying.xiao.constant;

public class Constant
{
	public static final int MAX_PAGE_COUNT=15;
	public static class ErrorCode{
		public static final int USER_LOGIN_ERROR=0X01;
		public static final int PUB_COMMENT_ERROR=0X02;
		public static final int PARAM_ERROR=0X03;//参数错误
		public static final int USER_NOT_LOGIN=0X04;//用户未登陆 或则session失效
		public static final int PRAISE_OPERATE_ERROR=0X05;//赞出错
		public static final int GET_CONTENT_DETAIL_NO_CONTENT=0X06;
		public static final int GET_MARKET_DETAIL_NO_MARKET=0X07;
		public static final int SAVE_CONTENT_ERROR=0X08;
		public static final int UPDATE_CONTENT_ERROR=0X09;
		public static final int GET_COLLECTION_ERROR=0X0A; 
		public static final int USER_REGIEST_ERROR=0X0B;
		public static final int GET_USERINFOS_ERROR=0X0C;
		public static final int USER_NOT_FOUNT=0X0D;
		public static final int SAVE_ERROR=0X0E;
		public static final int ADD_FRIEND_IS_YOUR_FRIEND_ALERADY=0X0F;
		public static final int USER_REGISTER_USERNAME_HAVE=0X10;
	}
	
	public static class ContentType{
		public static final int CONTENT_TYPE_NEWS=0x01; //资讯
		public static final int CONTENT_TYPE_LOST=0x02; //失物
		public static final int CONTENT_TYPE_DIARY=0x03; //新鲜事
		public static final int CONTENT_TYPE_MARKET=0x04; //市场
		public static final int CONTENT_TYPE_ASK=0x05; // 问答
	}
	
	public static class WzType{ 
		public static final int WZTYPE_ZRWL=1; //  助人为乐
		public static final int WZTYPE_LT=2; //论坛
		public static final int WZTYPE_RJ=3;  //日记
		public static final int WZTYPE_WJTZ=4;  //文件通知
		public static final int WZTYPE_SWZL=5; //失物招领
		public static final int WZTYPE_ZXZP=6; //招新招聘
		public static final int WZTYPE_DJT=7; //大讲堂
		public static final int WZTYPE_WP=8; //商品
		
	}
	
	public static class WenzhangType{
//		public static final int WENZHANG_TYPE_
	}
	public static class UserType{
		public static final int User_TYPE_PESONAL=1;  //个人
		public static final int User_TYPE_DEPARTMENT=2; //部门
		public static final int User_TYPE_BUSINESS=3; // 商家
	}
	public static class HandlerMessageCode{
		
		public static final int NET_THROW_EXCEPTION=-1;
		public static final int LOGIN_FAILD=0;
		public static final int LOGIN_SUCCESS=1;
		public static final int MAIN_LOAD_DATA_ERROR=3;
		public static final int MAIN_LOAD_DATA_SUCCESS=4;
		public static final int CONTENT_DETAIL_LOAD_DATA_SUCCESS=5;
		public static final int CONTENT_DETAIL_LOAD_DATA_ERROR=6;
		public static final int PUB_COMMENT_SUCCESS=7;
		public static final int PUB_COMMENT_ERROR=8;
		public static final int USER_NOT_LOGIN=9;
		public static final int PRAISE_OPERATE_ERROR=10;
		public static final int PRAISE_OPERATE_SUCCESS=11;
		public static final int GET_MARKET_TYPE_SUCCESS=12;
		public static final int GET_MARKET_TYPE_ERROR=13;
		public static final int COLLECTION_OPERATE_SUCCESS=14;
		public static final int COLLECTION_OPERATE_FAIL=15;
		public static final int GET_COMMENTS_FAIL=16;
		public static final int GET_COMMENT_SUCCESS=17;
		public static final int GET_USERINFOS_DEPARTMENT_SUCCESS=18;
		public static final int GET_USERINFOS_SUCCESS=188;
		public static final int GET_USERINFOS_FAIL=19;
		public static final int ADD_FRIEND_FAIL=20;
		public static final int ADD_FRIEND_SUCCESS=21;
		public static final int ADD_FRIEND_IS_YOUR_FRIEND_ALERADY=22;
		public static final int DEPARTMENT_DETAIL_LOAD_DATA_SUCCESS=23; // 部门 加载数据列表 
		public static final int DEPARTMENT_DETAIL_LOAD_DATA_FAIL=24;
		
		public static final int PUB_MESSAGE_SUCCESS=25;
		public static final int PUB_MESSAGE_ERROR=26;
		public static final int REGISTER_FAILD=27;
		public static final int REGISTER_SUCCESS=28;
		public static final int REGISTER_FAILD_USERNAME_HAVED=29;
		
		public static final int CHANGE_USER_INFO_FAIL=30;
		public static final int CHANGE_USER_INFO_SUCCESS=31;
		public static final int GET_MY_FRIENDS_FAIL=32;
		public static final int GET_MY_FRIENDS_SUCCESS=33;
		
		public static final int GET_MY_DYNAMIC_FAIL=34;
		public static final int GET_MY_DYNAMIC_SUCCESS=35;
		
		public static final int PUB_DIARY_FAIL=36;
		public static final int PUB_DIARY_SUCCESS=37;
		
		public static final int GET_MY_MESSAGE_FAIL=38;
		public static final int GET_MY_MESSAGE_SUCCESS=39;
	}
	
	public static class WebsocketCode{
		public static final int WEBSOCKET_CODE_SUCCESS=0;
		public static final int WEBSOCKET_CODE_LOGIN_ERROR =1;
		public static final int WEBSOCKET_CODE_FRIEND_ONLINE =2; //好友上线
		public static final int WEBSOCKET_CODE_FRIEND_OFFLINE =3; //好友下线
		/**
		 * 好友列表
		 */
		public static final int WEBSOCKET_CODE_FRIEND_LIST =4; 
		
		public static final int WEBSOCKET_CODE_ONCLOSE=5;
		
		public static final int WEBSOCKET_SEND_MESSAGE_TEXT=6;
		
		public static final int WEBSOCKET_SEND_TO_SUCCESS=7;
		
		/**
		 * 心跳
		 */
		public static final int WEBSOCKET_SEND_HEART=8;
		
		/**
		 * 接收者离线 消息发送到服务器 
		 */
		public static final int WEBSOCKET_SEND_TO_SERVER=9;
		
		/**
		 * 离线消息
		 */
		public static final int WEBSOCKET_OFFLINE_MESSAGE=10;
	}
	public static class DynamicType{
		public static final int DYNAMIC_TYPE_PRAISE_ME=0;//赞了我
		public static final int DYNAMIC_TYPE_CONTENT_COMMENT=1; // content评论
		public static final int DYNAMIC_TYPE_MESSAGE=2; //留言
		
	}
	
	public static class MainContentFragmentShowType{
		public static final int TYPE_MAIN=0;
		public static final int TYPE_MY_PUB=1;
		public static final int TYPE_MY_COLLECT=2;
		
	}
}
