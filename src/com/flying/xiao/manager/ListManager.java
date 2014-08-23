package com.flying.xiao.manager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import com.flying.xiao.constant.Constant;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.entity.XDynamic;
import com.flying.xiao.entity.XGoodType;
import com.flying.xiao.entity.XMessage;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.websocket.WFriend;


public class ListManager
{
	public static ListManager contentManager ;
	
	/**
	 * Main Fragment
	 */
	private List<XContent> newsContentList;
	private List<XContent> lostContentList;
	private List<XContent> diaryContentList;
	private List<XContent> marketContentList;
	private List<XContent> askContentList;
	/**
	 * Community Fragment
	 */
	private List<XUserInfo> departmentList ;
	private List<XUserInfo> businessList ;
	
	/**
	 * 我的好友
	 */
	private List<XUserInfo> myFriendList ;
	
	/**
	 * 我的动态
	 */
	
	private List<XDynamic> myDynamicList ;
	
	
	private List<XMessage> myMessageList;
	
	
	/**
	 * market type list
	 */
	private List<XGoodType>marketTypeList;
	
	
	/**
	 * 我的在线好友
	 */
	
	private List<WFriend> myOnlineFriend;
	
	/**
	 * all list
	 */
	private List list ;
	private ListManager(){
		newsContentList=new ArrayList<XContent>();
		lostContentList=new ArrayList<XContent>();
		diaryContentList=new ArrayList<XContent>();
		marketContentList=new ArrayList<XContent>();
		askContentList=new ArrayList<XContent>();
		departmentList=new ArrayList<XUserInfo>();
		businessList=new ArrayList<XUserInfo>();
		myFriendList=new ArrayList<XUserInfo>();
		myDynamicList=new ArrayList<XDynamic>();
		marketTypeList=new ArrayList<XGoodType>();
		myMessageList=new ArrayList<XMessage>();
		
		myOnlineFriend=new ArrayList<WFriend>();
	}
	private void initList(){
		if(newsContentList==null)
			newsContentList=new ArrayList<XContent>();
		if(lostContentList==null)
			lostContentList=new ArrayList<XContent>();
		if(diaryContentList==null)
			diaryContentList=new ArrayList<XContent>();
		if(marketContentList==null)
			marketContentList=new ArrayList<XContent>();
		if(askContentList==null)
			askContentList=new ArrayList<XContent>();
		if(departmentList==null)
			departmentList=new ArrayList<XUserInfo>();
		if(businessList==null)
			businessList=new ArrayList<XUserInfo>();
	}
	public static ListManager getContentMangerShare(){
		if(contentManager==null)
			contentManager=new ListManager();
//		contentManager.initList();
		return contentManager;
	}
	
	
	public List<WFriend> getMyOnlineFriend()
	{
		return myOnlineFriend;
	}
	public void setMyOnlineFriend(List<WFriend> myOnlineFriend)
	{
		this.myOnlineFriend = myOnlineFriend;
	}
	public List<XContent> getContentListByType(int conType){
		switch (conType)
		{
		case Constant.ContentType.CONTENT_TYPE_NEWS:
			return getNewsContentList();
		case Constant.ContentType.CONTENT_TYPE_LOST:
			return getLostContentList();
		case Constant.ContentType.CONTENT_TYPE_DIARY:
			return contentManager.getDiaryContentList();
		case Constant.ContentType.CONTENT_TYPE_MARKET:
			return contentManager.getMarketContentList();
		case Constant.ContentType.CONTENT_TYPE_ASK:
			return contentManager.getAskContentList();
		default:
			return null;
		}
	}
	
	public List<XMessage> getMyMessageList()
	{
		return myMessageList;
	}
	public void setMyMessageList(List<XMessage> myMessageList)
	{
		this.myMessageList = myMessageList;
	}
	public List<XDynamic> getMyDynamicList()
	{
		return myDynamicList;
	}
	public void setMyDynamicList(List<XDynamic> myDynamicList)
	{
		this.myDynamicList = myDynamicList;
	}
	public List<XContent> getNewsContentList()
	{
		return newsContentList;
	}
	public void setNewsContentList(List<XContent> newsContentList)
	{
		this.newsContentList = newsContentList;
	}
	public List<XContent> getLostContentList()
	{
		return lostContentList;
	}
	public void setLostContentList(List<XContent> lostContentList)
	{
		this.lostContentList = lostContentList;
	}
	public List<XContent> getDiaryContentList()
	{
		return diaryContentList;
	}
	public void setDiaryContentList(List<XContent> diaryContentList)
	{
		this.diaryContentList = diaryContentList;
	}
	public List<XContent> getMarketContentList()
	{
		return marketContentList;
	}
	public void setMarketContentList(List<XContent> marketContentList)
	{
		this.marketContentList = marketContentList;
	}
	public List<XContent> getAskContentList()
	{
		return askContentList;
	}
	public void setAskContentList(List<XContent> askContentList)
	{
		this.askContentList = askContentList;
	}
	
	public List<XUserInfo> getDepartmentList()
	{
		return departmentList;
	}
	public void setDepartmentList(List<XUserInfo> departmentList)
	{
		this.departmentList = departmentList;
	}
	public List<XUserInfo> getBusinessList()
	{
		return businessList;
	}
	public void setBusinessList(List<XUserInfo> businessList)
	{
		this.businessList = businessList;
	}
	
	public List<XUserInfo> getMyFriendList()
	{
		return myFriendList;
	}
	public void setMyFriendList(List<XUserInfo> myFriendList)
	{
		this.myFriendList = myFriendList;
	}
	
	public List<XGoodType> getMarketTypeList()
	{
		return marketTypeList;
	}
	public void setMarketTypeList(List<XGoodType> marketTypeList)
	{
		this.marketTypeList = marketTypeList;
	}
	/**
	 * 将对象保存
	 */
	public void writeList(String path){
		list=new ArrayList<List<XContent>>();
		list.add(newsContentList);
		list.add(lostContentList);
		list.add(diaryContentList);
		list.add(marketContentList);
		list.add(askContentList);
		list.add(departmentList);
		list.add(businessList);
		list.add(myFriendList);
		list.add(myDynamicList);
		list.add(marketTypeList);
		list.add(myMessageList);
		ObjectOutputStream objectOut = null;
		try
		{
			objectOut = new ObjectOutputStream(new FileOutputStream(path));
			objectOut.writeObject(list);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (objectOut != null)
			{
				try
				{
					objectOut.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 读取保存的对象
	 */
	public void readList(String path){
		ObjectInputStream objIn = null;
		try
		{
			objIn = new ObjectInputStream(new FileInputStream(path));

			list = (List<List<XContent>>) objIn.readObject();

		} catch (StreamCorruptedException e)
		{
			e.printStackTrace();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		if (objIn != null)
		{
			try
			{
				objIn.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if(list!=null&&list.size()==11){
			newsContentList=(List<XContent>) list.get(0);
			lostContentList=(List<XContent>) list.get(1);
			diaryContentList=(List<XContent>) list.get(2);
			marketContentList=(List<XContent>) list.get(3);
			askContentList=(List<XContent>) list.get(4);
			departmentList=(List<XUserInfo>) list.get(5);
			businessList=(List<XUserInfo>) list.get(6);
			myFriendList=(List<XUserInfo>) list.get(7);
			myDynamicList=(List<XDynamic>) list.get(8);
			marketTypeList=(List<XGoodType>) list.get(9);
			myMessageList=(List<XMessage>) list.get(10);
		}
	}
	
	/**
	 * 根据用户Id获取好友对象
	 * @param userId
	 * @return
	 */
	public XUserInfo getUserInfoById(long userId){
		for(XUserInfo userInfo:myFriendList){
			if(userInfo.getId()==userId){
				return userInfo;
			}
		}
		return null;
	}
	
	/**
	 * 根据用户Id获取在线好友对象
	 * @param userId
	 * @return
	 */
	public WFriend getWfriendById(long userId){
		WFriend friend=null;
		for(WFriend userInfo:myOnlineFriend){
			if( userInfo.getFriendUserId()==userId){
				friend=userInfo;
				break ;
			}
		}
		return friend;
	}
	
	public void removeOnlineFriendById(long id){
		for(WFriend userInfo:myOnlineFriend){
			if(userInfo.getFriendUserId()==id){
				myOnlineFriend.remove(userInfo);
//				return ;
			}
		}
	}
	
//	private void setMyFriendOnlineOrOffLine(long userId,boolean isOnline){
//		for(XUserInfo userInfo:myFriendList){
//			if(userInfo.getId()==userId){
//				userInfo.setOnline(isOnline);
//			}
//		}
//	}
	/**
	 * 将在线好友 状态设为TRUE
	 * @return
	 */
	public void setMyFriendOnline(){
		
		for(XUserInfo userInfo:myFriendList){
			userInfo.setOnline(false);
		}
		
		for(WFriend friend:myOnlineFriend){
		long id=friend.getFriendUserId();
		XUserInfo userInfo=getUserInfoById(id);
		if(userInfo!=null)
			userInfo.setOnline(true);
		
	}
	}
	
	
	
}
