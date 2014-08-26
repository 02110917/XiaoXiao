package com.flying.xiao.common;
import java.io.Serializable;

public class URLs implements Serializable {
	
//	public final static String HOST = "http://xiaolife.net";
//	public final static String HOST = "http://zhangmin.web.myjhost.net/";
//	public final static String HOST = "http://192.168.3.103:8080";
	public final static String HOST = "http://115.29.79.84";
//	public final static String HOST = "http://192.168.1.105:8080";
//	public final static String HOST = "http://192.168.43.186:8080";
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	
	private final static String URL_SPLITTER = "/";
	private final static String URL_UNDERLINE = "_";
	public final static String WSURI = "ws://115.29.79.84/XiaoServer/websocket";
//	public final static String WSURI = "ws://xiaolife.net/XiaoServer/websocket";
//	public final static String WSURI = "ws://zhangmin.web.myjhost.net/XiaoServer/websocket";
//	public final static String WSURI = "ws://192.168.43.186:8080/XiaoServer/websocket";
	public final static String URL_MAIN_GET_CONTENT =HOST+ "/XiaoServer/servlet/GetContent";
//	public final static String URL_MAIN_NEWS =HOST+ "/XiaoServer/servlet/GetContent?type=news";
//	public final static String URL_MAIN_LOST =HOST+ "/XiaoServer/servlet/GetContent?type=lost";
//	public final static String URL_MAIN_DIARY =HOST+ "/XiaoServer/servlet/GetContent?type=diary";
//	public final static String URL_MAIN_MARKET =HOST+ "/XiaoServer/servlet/GetContent?type=market";
//	public final static String URL_MAIN_ASK =HOST+ "/XiaoServer/servlet/GetContent?type=ask";
	public final static String URL_LOGIN =HOST+ "/XiaoServer/servlet/UserServlet";
	public final static String URL_GET_CONTENT_DETAIL =HOST+ "/XiaoServer/servlet/GetContentDetail";
	public final static String URL_PUB_COMMENT =HOST+ "/XiaoServer/servlet/PubComment";
	public final static String URL_PUB_MESSAGE =HOST+ "/XiaoServer/servlet/PubMessage";
	public final static String URL_PRAISE_OPERATE =HOST+ "/XiaoServer/servlet/PraiseOperate";//?contentid=23&userId=4&isCancel=false
	public final static String URL_GET_MARKET_TYPE =HOST+ "/XiaoServer/servlet/GetMarketType";
	public final static String URL_CollectionOperate =HOST+ "/XiaoServer/servlet/CollectionOperate";
	public final static String URL_GETCOMMENTS =HOST+ "/XiaoServer/servlet/GetComments";
	public final static String URL_GETUSERINFOS =HOST+ "/XiaoServer/servlet/GetUserInfos";
	public final static String URL_ADD_FRIEND =HOST+ "/XiaoServer/servlet/AddFriend";
	public final static String URL_CHANGE_USER_INFO =HOST+ "/XiaoServer/servlet/ChangeUserInfo";
	public final static String URL_GET_MY_FRIENDS =HOST+ "/XiaoServer/servlet/GetMyFriends";
	public final static String URL_GET_MY_DYNAMIC =HOST+ "/XiaoServer/servlet/GetDynamics";
	public final static String URL_PUB_DIARY =HOST+ "/XiaoServer/servlet/PubDiary";
	public final static String URL_PUB_LOST =HOST+ "/XiaoServer/servlet/PubLost";
	public final static String URL_PUB_MARKET =HOST+ "/XiaoServer/servlet/PubMarket";
	public final static String URL_PUB_CONTENT =HOST+ "/XiaoServer/servlet/PubContent";
	public final static String URL_GET_MY_MESSAGE =HOST+ "/XiaoServer/servlet/GetMyMessage";
	
	
	}