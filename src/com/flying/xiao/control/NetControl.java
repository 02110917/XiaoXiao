package com.flying.xiao.control;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.flying.xiao.app.AppContext;
import com.flying.xiao.app.AppException;
import com.flying.xiao.common.URLs;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.entity.Base;
import com.flying.xiao.entity.XComment;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.entity.XContentDetail;
import com.flying.xiao.entity.XDynamic;
import com.flying.xiao.entity.XGoodType;
import com.flying.xiao.entity.XLostDetail;
import com.flying.xiao.entity.XMarketDetail;
import com.flying.xiao.entity.XMessage;
import com.flying.xiao.entity.XPraise;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.http.HttpUtil;

public class NetControl
{
	private static NetControl control = null;
	private static AppContext appContext = null;

	private NetControl()
	{
	}

	public static NetControl getShare(Context context)
	{
		if (control == null)
			control = new NetControl();
		appContext = (AppContext) context.getApplicationContext();
		return control;
	}

	/**
	 * 登陆
	 * 
	 * @param userName
	 * @param password
	 * @param handler
	 */
	public void login(final String userName, final String password, final Handler handler)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{

					XUserInfo user = HttpUtil.login(appContext, userName, password);
					if (user == null || user.getErrorCode() != 0)
					{
						msg.what = Constant.HandlerMessageCode.LOGIN_FAILD;
						msg.obj = user;
					} else
					{
						msg.what = Constant.HandlerMessageCode.LOGIN_SUCCESS;// 成功
						msg.obj = user;
						appContext.writeUserInfo(user);
					}
				} catch (AppException e)
				{
					e.printStackTrace();
					msg.what = Constant.HandlerMessageCode.NET_THROW_EXCEPTION;
					msg.obj = e;
				}
				handler.sendMessage(msg);

			}
		}.start();
	}

	/**
	 * 修改密码
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @param handler
	 */
	public void alertPassword(final String oldPassword, final String newPassword, final String userId,
			final Handler handler)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{

					Base base = HttpUtil.alertPassword(appContext, oldPassword, newPassword, userId);
					if (base.getErrorCode() == Constant.ErrorCode.USER_NOT_LOGIN)
					{
						msg.what = Constant.ErrorCode.USER_NOT_LOGIN;
						msg.obj = base;
					} else if (base == null || base.getErrorCode() != 0)
					{
						msg.what = Constant.HandlerMessageCode.LOGIN_FAILD;
						msg.obj = base;
					} else
					{
						msg.what = Constant.HandlerMessageCode.LOGIN_SUCCESS;// 成功
						msg.obj = base;
					}
				} catch (AppException e)
				{
					e.printStackTrace();
					msg.what = Constant.HandlerMessageCode.NET_THROW_EXCEPTION;
					msg.obj = e;
				}
				handler.sendMessage(msg);

			}
		}.start();
	}

	/**
	 * 注册
	 * 
	 * @param userInfo
	 * @param handler
	 */
	public void register(final XUserInfo userInfo, final Handler handler)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{

					XUserInfo user = HttpUtil.register(appContext, userInfo);
					if (user == null || user.getErrorCode() != 0)
					{
						if (user.getErrorCode() == Constant.ErrorCode.USER_REGISTER_USERNAME_HAVE)
						{ // 用户名已存在
							msg.what = Constant.HandlerMessageCode.REGISTER_FAILD_USERNAME_HAVED;
							msg.obj = user;
						} else
						{
							msg.what = Constant.HandlerMessageCode.REGISTER_FAILD;
							msg.obj = user;
						}
					} else
					{
						msg.what = Constant.HandlerMessageCode.REGISTER_SUCCESS;// 成功
						msg.obj = user;
						appContext.writeUserInfo(user);
					}
				} catch (AppException e)
				{
					e.printStackTrace();
					msg.what = Constant.HandlerMessageCode.NET_THROW_EXCEPTION;
					msg.obj = e;
				}
				handler.sendMessage(msg);

			}
		}.start();
	}

	/**
	 * 反馈
	 * 
	 * @param userName
	 * @param password
	 * @param handler
	 */
	public void suggest(final String email, final String phone, final String name, final String userId,
			final String suggestion, final Handler handler)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{

					Base base = HttpUtil.suggest(appContext, email, phone, name, userId);
					if (base == null || base.getErrorCode() != 0)
					{
						msg.what = Constant.HandlerMessageCode.LOGIN_FAILD;// 反馈失败
						msg.obj = base;
					} else
					{
						msg.what = Constant.HandlerMessageCode.LOGIN_SUCCESS;// 反馈成功
						msg.obj = base;
					}
				} catch (AppException e)
				{
					e.printStackTrace();
					msg.what = Constant.HandlerMessageCode.NET_THROW_EXCEPTION;
					msg.obj = e;
				}
				handler.sendMessage(msg);

			}
		}.start();
	}

	public void getContentData(final int type, final long userId, final int page, final boolean isMyCollect,
			final Handler handler)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				Message msg = new Message();
				List<XContent> xConList = HttpUtil
						.getContentData(appContext, type, userId, page, isMyCollect);
				if (xConList == null || xConList.size() == 0)
				{
					msg.what = Constant.HandlerMessageCode.MAIN_LOAD_DATA_ERROR;
					msg.obj = "获取信息出错...";
				} else
				{
					msg.what = Constant.HandlerMessageCode.MAIN_LOAD_DATA_SUCCESS;// 成功
					msg.arg1 = type;
					msg.obj = xConList;
					System.out.println("发送消息--" + type);
				}
				handler.sendMessage(msg);

			}
		}.start();
	}

	/**
	 * 获取主页 内容标题显示
	 * 
	 * @param type
	 * @param page
	 * @param handler
	 */
	public void getContentData(final int type, final int page, final Handler handler)
	{
		getContentData(type, -1, page, false, handler);
	}

	public void getMarketData(final int marketType, final long userId, final int page, final Handler handler)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				Message msg = new Message();
				List<XContent> xConList = HttpUtil.getMarketData(appContext,
						Constant.ContentType.CONTENT_TYPE_MARKET, userId, page);
				if (xConList == null || xConList.size() == 0)
				{
					msg.what = Constant.HandlerMessageCode.DEPARTMENT_DETAIL_LOAD_DATA_FAIL;
					msg.obj = "获取信息出错...";
				} else
				{
					msg.what = Constant.HandlerMessageCode.DEPARTMENT_DETAIL_LOAD_DATA_SUCCESS;// 成功
					msg.arg1 = marketType;
					msg.obj = xConList;
					System.out.println("发送消息--" + marketType);
				}
				handler.sendMessage(msg);

			}
		}.start();
	}

	public void getWzData(final int wzType, final long userId, final int page, final Handler handler)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				Message msg = new Message();
				List<XContent> xConList = HttpUtil.getWzData(appContext,
						Constant.ContentType.CONTENT_TYPE_NEWS, wzType, userId, page);
				if (xConList == null || xConList.size() == 0)
				{
					msg.what = Constant.HandlerMessageCode.DEPARTMENT_DETAIL_LOAD_DATA_FAIL;
					msg.obj = "获取信息出错...";
				} else
				{
					msg.what = Constant.HandlerMessageCode.DEPARTMENT_DETAIL_LOAD_DATA_SUCCESS;// 成功
					msg.arg1 = wzType;
					msg.obj = xConList;
					System.out.println("发送消息--" + wzType);
				}
				handler.sendMessage(msg);

			}
		}.start();
	}

	/**
	 * 获取内容详情
	 * 
	 * @param contentType
	 * @param contentId
	 */
	public void getContentDetail(final int contentType, long contentId, final Handler handler)
	{
		// http://192.168.0.8:8080/XiaoServer/servlet/GetContentDetail?type=1&id=17
		final String url = URLs.URL_GET_CONTENT_DETAIL + "?type=" + contentType + "&id=" + contentId;
		new Thread()
		{
			@Override
			public void run()
			{
				Message msg = new Message();
				String result = HttpUtil.getContentDetail(appContext, url);
				Base conDetail = null;
				if (contentType == Constant.ContentType.CONTENT_TYPE_NEWS
						|| contentType == Constant.ContentType.CONTENT_TYPE_ASK)
				{
					conDetail = new XContentDetail();
				} else if (contentType == Constant.ContentType.CONTENT_TYPE_MARKET)
				{
					conDetail = new XMarketDetail();
				} else if (contentType == Constant.ContentType.CONTENT_TYPE_LOST)
				{
					conDetail = new XLostDetail();
				}
				conDetail = conDetail.jsonToBase(result);
				// Base conDetail =HttpUtil.getContentDetail(appContext,url);
				if (conDetail == null)
				{
					msg.what = Constant.HandlerMessageCode.CONTENT_DETAIL_LOAD_DATA_ERROR;
					msg.obj = "获取信息出错...";
				} else
				{
					msg.what = Constant.HandlerMessageCode.CONTENT_DETAIL_LOAD_DATA_SUCCESS;
					msg.obj = conDetail;
				}
				handler.sendMessage(msg);

			}
		}.start();
	}

	/**
	 * 提交评论
	 * 
	 * @param userId
	 *            发表评论user
	 * @param contentId
	 *            内容id
	 * @param commentInfo
	 *            评论内容
	 * @param replyId
	 *            回复的评论id
	 * @param handler
	 *            ui接收消息
	 */
	public void pubComment(final long userId, final long contentId, final String commentInfo,
			final long replyId, final Handler handler)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{
					XComment base = HttpUtil.pubConmment(appContext, userId, contentId, commentInfo, replyId);
					if (base.getErrorCode() != 0)
					{
						if (base.getErrorCode() == Constant.ErrorCode.USER_NOT_LOGIN)
						{
							msg.what = Constant.HandlerMessageCode.USER_NOT_LOGIN;
						} else
						{
							msg.what = Constant.HandlerMessageCode.PUB_COMMENT_ERROR;
						}
						msg.obj = base.getErrorMsg();
					} else
					{
						msg.what = Constant.HandlerMessageCode.PUB_COMMENT_SUCCESS;
						msg.obj = base;
					}
				} catch (AppException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = Constant.HandlerMessageCode.PUB_COMMENT_ERROR;
					msg.obj = "发表评论失败..";
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	// /**
	// * 提交评论
	// * @param userId 发表评论user
	// * @param contentId 内容id
	// * @param commentInfo 评论内容
	// * @param replyId 回复的评论id
	// */
	// public void pubComment(final long userId,final long contentId,final
	// String commentInfo,final long replyId,final Handler handler){
	// pubComment(userId, contentId, commentInfo, replyId, handler);
	// }

	/**
	 * 发送留言
	 * 
	 * @param userId
	 * @param messageInfo
	 * @param handler
	 */
	public void pubMessage(final long userId, final String messageInfo, final int messageReplyId,
			final int messageMainId, final Handler handler)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{
					XMessage base = HttpUtil.pubMessage(appContext, userId, messageInfo, messageReplyId,
							messageMainId);
					if (base.getErrorCode() != 0)
					{
						if (base.getErrorCode() == Constant.ErrorCode.USER_NOT_LOGIN)
						{
							msg.what = Constant.HandlerMessageCode.USER_NOT_LOGIN;
						} else
						{
							msg.what = Constant.HandlerMessageCode.PUB_MESSAGE_ERROR;
						}
						msg.obj = base.getErrorMsg();
					} else
					{
						msg.what = Constant.HandlerMessageCode.PUB_MESSAGE_SUCCESS;
						msg.obj = base;
					}
				} catch (AppException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = Constant.HandlerMessageCode.PUB_MESSAGE_ERROR;
					msg.obj = "发留言失败..";
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	public void praiseOpreate(final XPraise xp, final boolean isCancel, final Handler handler)
	{
		if (xp == null || xp.getUserInfo() == null)
			return;
		final long userId = xp.getUserInfo().getId();
		final long contentId = xp.getContentId();

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{
					XPraise base = HttpUtil.praiseContent(appContext, userId, contentId, isCancel);
					if (base == null || base.getErrorCode() != 0)
					{
						if (base.getErrorCode() == Constant.ErrorCode.USER_NOT_LOGIN)
						{
							msg.what = Constant.HandlerMessageCode.USER_NOT_LOGIN;
						} else
						{
							msg.what = Constant.HandlerMessageCode.PRAISE_OPERATE_ERROR;
						}
						msg.obj = xp;
					} else
					{
						msg.what = Constant.HandlerMessageCode.PRAISE_OPERATE_SUCCESS;
						msg.obj = base;
					}
				} catch (AppException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = Constant.HandlerMessageCode.PRAISE_OPERATE_ERROR;
					msg.obj = xp;
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取market type
	 * 
	 * @param type
	 * @param page
	 * @param handler
	 */
	public void getMarketTypes(final Handler handler)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				Message msg = new Message();
				List<XGoodType> xtypeList;
				try
				{
					xtypeList = HttpUtil.getMarketType(appContext);
					if (xtypeList == null || xtypeList.size() == 0)
					{
						msg.what = Constant.HandlerMessageCode.GET_MARKET_TYPE_ERROR;
						msg.obj = "获取信息出错...";
					} else
					{
						msg.what = Constant.HandlerMessageCode.GET_MARKET_TYPE_SUCCESS;// 成功
						msg.obj = xtypeList;
					}
				} catch (AppException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = Constant.HandlerMessageCode.GET_MARKET_TYPE_ERROR;
					msg.obj = e;
				}
				handler.sendMessage(msg);

			}
		}.start();
	}

	public void collectOperate(final long contentid, final boolean isCancel, final Handler handler)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{
					Base base = HttpUtil.collectionOperate(appContext, contentid, isCancel);
					if (base == null || base.getErrorCode() != 0)
					{
						if (base.getErrorCode() == Constant.ErrorCode.USER_NOT_LOGIN)
						{
							msg.what = Constant.HandlerMessageCode.USER_NOT_LOGIN;
						} else
						{
							msg.what = Constant.HandlerMessageCode.COLLECTION_OPERATE_FAIL;
							msg.obj = "操作出错";
						}
					} else
					{
						msg.what = Constant.HandlerMessageCode.COLLECTION_OPERATE_SUCCESS;
					}
				} catch (AppException e)
				{
					e.printStackTrace();
					msg.what = Constant.HandlerMessageCode.COLLECTION_OPERATE_FAIL;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 获取评论列表
	 * 
	 * @param contentid
	 * @param page
	 */
	public void getComments(final long contentid, final int page)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{
					List<XComment> commentlist = HttpUtil.getComments(appContext, contentid, page);
					if (commentlist == null)
					{
						msg.what = Constant.HandlerMessageCode.GET_COMMENTS_FAIL;
						msg.obj = "获取数据出错";
						return;
					}
					msg.what = Constant.HandlerMessageCode.GET_COMMENT_SUCCESS;
					msg.obj = commentlist;
				} catch (AppException e)
				{
					msg.what = Constant.HandlerMessageCode.GET_COMMENTS_FAIL;
					msg.obj = e;
					e.printStackTrace();
				}
			}
		}).start();

	}

	public void getUserInfos(final int typeid, final int page, final Handler handler)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{
					List<XUserInfo> userInfos = HttpUtil.getUserInfos(appContext, typeid, page);
					if (userInfos == null)
					{
						msg.what = Constant.HandlerMessageCode.GET_USERINFOS_FAIL;
						msg.obj = "获取数据出错";
						return;
					}

					msg.what = Constant.HandlerMessageCode.GET_USERINFOS_SUCCESS;
					msg.arg1 = typeid;
					msg.obj = userInfos;
				} catch (AppException e)
				{
					msg.what = Constant.HandlerMessageCode.GET_USERINFOS_FAIL;
					msg.obj = e;
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	public void getUserInfosByName(final Handler handler, final String users)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{
					List<XUserInfo> userInfos = HttpUtil.getUserInfosByName(appContext, users);
					if (userInfos == null)
					{
						msg.what = Constant.HandlerMessageCode.GET_MY_USERS_FAILED;
						msg.obj = "获取数据出错";
						return;
					}

					msg.what = Constant.HandlerMessageCode.GET_MY_USERS_SUCCESS;
					msg.obj = userInfos;
				} catch (AppException e)
				{
					if (e.getType() == AppException.USER_NOT_LOGIN)
					{
						msg.what = Constant.HandlerMessageCode.USER_NOT_LOGIN;
						msg.obj = "您还未登陆";
					} else
					{
						msg.what = Constant.HandlerMessageCode.GET_MY_USERS_FAILED;
						msg.obj = e;
					}
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	// public void getMyFriends(final Handler handler) {
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// Message msg = new Message();
	// try {
	// List<XUserInfo> userInfos = HttpUtil
	// .getMyFriends(appContext);
	// if (userInfos == null) {
	// msg.what = Constant.HandlerMessageCode.GET_MY_FRIENDS_FAIL;
	// msg.obj = "获取数据出错";
	// return;
	// }
	//
	// msg.what = Constant.HandlerMessageCode.GET_MY_FRIENDS_SUCCESS;
	// msg.obj = userInfos;
	// } catch (AppException e) {
	// if (e.getType() == AppException.USER_NOT_LOGIN) {
	// msg.what = Constant.HandlerMessageCode.USER_NOT_LOGIN;
	// msg.obj = "您还未登陆";
	// } else {
	// msg.what = Constant.HandlerMessageCode.GET_MY_FRIENDS_FAIL;
	// msg.obj = e;
	// }
	// e.printStackTrace();
	// }
	// handler.sendMessage(msg);
	// }
	// }).start();
	// }

	public void getMyDynamic(final Handler handler, final int page)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{
					List<XDynamic> dynamics = HttpUtil.getMyDynamic(appContext, page);
					if (dynamics == null)
					{
						msg.what = Constant.HandlerMessageCode.GET_MY_DYNAMIC_FAIL;
						msg.obj = "获取数据出错";
						return;
					}

					msg.what = Constant.HandlerMessageCode.GET_MY_DYNAMIC_SUCCESS;
					msg.obj = dynamics;
				} catch (AppException e)
				{
					if (e.getType() == AppException.USER_NOT_LOGIN)
					{
						msg.what = Constant.HandlerMessageCode.USER_NOT_LOGIN;
						msg.obj = "您还未登陆";
					} else
					{
						msg.what = Constant.HandlerMessageCode.GET_MY_DYNAMIC_FAIL;
						msg.obj = e;
					}
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	public void getMyMessage(final Handler handler, final int page)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{
					List<XMessage> dynamics = HttpUtil.getMyMessage(appContext, page);
					if (dynamics == null)
					{
						msg.what = Constant.HandlerMessageCode.GET_MY_MESSAGE_FAIL;
						msg.obj = "获取数据出错";
						return;
					}

					msg.what = Constant.HandlerMessageCode.GET_MY_MESSAGE_SUCCESS;
					msg.obj = dynamics;
				} catch (AppException e)
				{
					if (e.getType() == AppException.USER_NOT_LOGIN)
					{
						msg.what = Constant.HandlerMessageCode.USER_NOT_LOGIN;
						msg.obj = "您还未登陆";
					} else
					{
						msg.what = Constant.HandlerMessageCode.GET_MY_MESSAGE_FAIL;
						msg.obj = e;
					}
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	public void addFriend(final long userId, final Handler handler)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{
					Base base = HttpUtil.addFriend(appContext, userId);
					if (base.getErrorCode() != 0)
					{
						if (base.getErrorCode() == Constant.ErrorCode.USER_NOT_LOGIN)
						{
							msg.what = Constant.HandlerMessageCode.USER_NOT_LOGIN;
							msg.obj = base.getErrorMsg();
						} else if (base.getErrorCode() == Constant.ErrorCode.ADD_FRIEND_IS_YOUR_FRIEND_ALERADY)
						{
							msg.what = Constant.HandlerMessageCode.ADD_FRIEND_IS_YOUR_FRIEND_ALERADY;
							msg.obj = base.getErrorMsg();
						} else
						{
							msg.what = Constant.HandlerMessageCode.ADD_FRIEND_FAIL;
							msg.obj = base.getErrorMsg();
						}
					} else
					{
						msg.what = Constant.HandlerMessageCode.ADD_FRIEND_SUCCESS;
					}
				} catch (AppException e)
				{
					e.printStackTrace();
					msg.what = Constant.HandlerMessageCode.ADD_FRIEND_FAIL;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	public void changeUserInfo(final String userJson, final String headImageFile, final Handler handler)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{
					Base base = HttpUtil.changeUserInfo(appContext, userJson, headImageFile);
					if (base.getErrorCode() != 0)
					{
						if (base.getErrorCode() == Constant.ErrorCode.USER_NOT_LOGIN)
						{
							msg.what = Constant.HandlerMessageCode.USER_NOT_LOGIN;
							msg.obj = base.getErrorMsg();
						} else
						{
							msg.what = Constant.HandlerMessageCode.CHANGE_USER_INFO_FAIL;
							msg.obj = base.getErrorMsg();
						}
					} else
					{
						msg.what = Constant.HandlerMessageCode.CHANGE_USER_INFO_SUCCESS;
					}
				} catch (AppException e)
				{
					e.printStackTrace();
					msg.what = Constant.HandlerMessageCode.CHANGE_USER_INFO_FAIL;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	public void pubDiaryOrLost(final String content, final List<String> files, final Handler handler,
			final int type)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				try
				{
					Base base = HttpUtil.pubDiaryOrLost(appContext, content, files, type);
					if (base == null || base.getErrorCode() != 0)
					{
						if (base.getErrorCode() == Constant.ErrorCode.USER_NOT_LOGIN)
						{
							msg.what = Constant.HandlerMessageCode.USER_NOT_LOGIN;
							msg.obj = base.getErrorMsg();
						} else
						{
							msg.what = Constant.HandlerMessageCode.PUB_DIARY_FAIL;
							msg.obj = base.getErrorMsg();
						}
					} else
					{
						msg.what = Constant.HandlerMessageCode.PUB_DIARY_SUCCESS;
					}
				} catch (AppException e)
				{
					e.printStackTrace();
					msg.what = Constant.HandlerMessageCode.PUB_DIARY_FAIL;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 检索数据
	 * 
	 * @param type
	 *            -1 user
	 * @param pageIndex
	 * @param handler
	 */
	public void loadLvSearchData(final String keywork, final int type, final int pageIndex,
			final Handler handler, final int action)
	{
		new Thread()
		{
			public void run()
			{
				Message msg = new Message();
				try
				{
					List xuserInfos = HttpUtil.loadSearchData(appContext, type, keywork, pageIndex);
					if (xuserInfos == null)
					{
						msg.what = -1;
					} else
					{
						msg.what = xuserInfos.size();
						msg.obj = xuserInfos;
					}

				} catch (AppException e)
				{
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = type;
				handler.sendMessage(msg);
			}
		}.start();
	}
}
