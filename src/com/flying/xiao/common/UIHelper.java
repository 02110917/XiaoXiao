package com.flying.xiao.common;

import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickAction;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.flying.xiao.ChatActivity;
import com.flying.xiao.CommentPub;
import com.flying.xiao.ContentDetail;
import com.flying.xiao.DiaryDetail;
import com.flying.xiao.LostDetail;
import com.flying.xiao.MainActivity;
import com.flying.xiao.MarketDetail;
import com.flying.xiao.MessagePub;
import com.flying.xiao.MyChatActivity;
import com.flying.xiao.MyCollect;
import com.flying.xiao.MyDynamic;
import com.flying.xiao.MyFriends;
import com.flying.xiao.MyInfoActivity;
import com.flying.xiao.MyMessage;
import com.flying.xiao.PubContentActivity;
import com.flying.xiao.PubDiaryActivity;
import com.flying.xiao.PubLostActivity;
import com.flying.xiao.PubLostAndMarketComment;
import com.flying.xiao.PubMarketActivity;
import com.flying.xiao.R;
import com.flying.xiao.SettingActivity;
import com.flying.xiao.UserInfoDetail;
import com.flying.xiao.UserLoginActivity;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.app.AppManager;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.service.WebSocketService;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class UIHelper
{

	/** 全局web样式 */
	public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	/**
	 * 显示首页
	 * 
	 * @param activity
	 */
	public static void showHome(Activity activity)
	{
		Intent intent = new Intent(activity, MainActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * 显示登录页面
	 * 
	 * @param activity
	 */
	public static void showLoginDialog(Context context)
	{
		showLoginOrRegiste((Activity) context, true);
	}
	/**
	 * 显示个人资料
	 * @param context
	 */
	public static void showMyInfo(Context context){
		Intent intent=new Intent();
		intent.setClass(context, MyInfoActivity.class);
		context.startActivity(intent);
	}
	
	public static void showMyFriends(Context context){
		Intent intent=new Intent();
		intent.setClass(context, MyFriends.class);
		context.startActivity(intent);
	}
	
	public static void showMyCollect(Context context,int showType){
		Intent intent=new Intent();
		intent.setClass(context, MyCollect.class);
		intent.putExtra("showType", showType);
		context.startActivity(intent);
	}
	
	public static void showMyDynamic(Context context){
		Intent intent=new Intent();
		intent.setClass(context, MyDynamic.class);
		context.startActivity(intent);
	}
	
	public static void showMyMessage(Context context){
		Intent intent=new Intent();
		intent.setClass(context, MyMessage.class);
		context.startActivity(intent);
	}
	public static void showMyChat(Context context){
		Intent intent=new Intent();
		intent.setClass(context, MyChatActivity.class);
		context.startActivity(intent);
	}
	/**
	 * 显示内容详情页面
	 * 
	 * @param context
	 * @param content
	 * @param contentType
	 */
	public static void showContentInfo(Context context, int index, int contentType)
	{
		Intent intent = new Intent();
		if (contentType == Constant.ContentType.CONTENT_TYPE_ASK
				|| contentType == Constant.ContentType.CONTENT_TYPE_NEWS)
		{
			intent.setClass(context, ContentDetail.class);
		} else if (contentType == Constant.ContentType.CONTENT_TYPE_LOST)
		{
			intent.setClass(context, LostDetail.class);
		} else if (contentType == Constant.ContentType.CONTENT_TYPE_MARKET)
		{
			intent.setClass(context, MarketDetail.class);
		} else if (contentType == Constant.ContentType.CONTENT_TYPE_DIARY)
		{
			intent.setClass(context, DiaryDetail.class);
		}
		intent.putExtra("conType", contentType);
		intent.putExtra("content", index);
		context.startActivity(intent);
	}

	public static void showContentInfo(Context context, XContent content, int contentType)
	{
		Intent intent = new Intent();
		if (contentType == Constant.ContentType.CONTENT_TYPE_ASK
				|| contentType == Constant.ContentType.CONTENT_TYPE_NEWS)
		{
			intent.setClass(context, ContentDetail.class);
		} else if (contentType == Constant.ContentType.CONTENT_TYPE_LOST)
		{
			intent.setClass(context, LostDetail.class);
		} else if (contentType == Constant.ContentType.CONTENT_TYPE_MARKET)
		{
			intent.setClass(context, MarketDetail.class);
		} else if (contentType == Constant.ContentType.CONTENT_TYPE_DIARY)
		{
			intent.setClass(context, DiaryDetail.class);
		}
		intent.putExtra("conType", contentType);
		intent.putExtra("contentObject", content);
		context.startActivity(intent);
	}
	
	/**
	 * 显示社区详情页面
	 * @param context  上下文
	 * @param index  索引
	 * @param type  user type
	 */
	public static void showCommunityInfo(Context context,int index,int type){
		Intent intent = new Intent();
		intent.putExtra("index", index);	
		intent.putExtra("userType", type);
		if(type==Constant.UserType.User_TYPE_DEPARTMENT||type==Constant.UserType.User_TYPE_BUSINESS){
			intent.setClass(context, UserInfoDetail.class);
		}
		context.startActivity(intent);
	}
	
	public static void showUserInfo(Context context ,XUserInfo userInfo){
		Intent intent = new Intent();
		intent.putExtra("userinfo", userInfo);
		intent.setClass(context, UserInfoDetail.class);
		context.startActivity(intent);
	}
	
	public static void showPubDiary(Context context){
		Intent intent = new Intent();
		intent.setClass(context, PubDiaryActivity.class);
		context.startActivity(intent);
	}
	
	public static void showPubLost(Context context){
		Intent intent = new Intent();
		intent.setClass(context, PubLostActivity.class);
		context.startActivity(intent);
	}
	
	public static void showPubMarket(Context context){
		Intent intent = new Intent();
		intent.setClass(context, PubMarketActivity.class);
		context.startActivity(intent);
	}
	public static void showPubContent(Context context,int contentType,int wzType)
	{
		Intent intent = new Intent();
		intent.setClass(context, PubContentActivity.class);
		intent.putExtra("type", contentType);
		intent.putExtra("wztype", wzType);
		context.startActivity(intent);
		
	}
	/**
	 * 调用系统相册
	 * @param context
	 * @param userInfo
	 */
	public static void showPicture(Activity context){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		context.startActivityForResult(intent, MyInfoActivity.FROM_ALBUM);
	}
	
	public static void showCamera(Activity context,Uri imageUrl){
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUrl);
//		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		context.startActivityForResult(intent, MyInfoActivity.TAKE_PICTURE);
	}
	/**
	 * 裁剪图像
	 * @param context
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	public static void showCropImage(Activity context,Uri uri,int outputX,int outputY,int requestCode){
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", "1");
		intent.putExtra("aspectY", "1");
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("outputFormat","JPEG");
//		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true);
		context.startActivityForResult(intent, requestCode);
	}
	/**
	 * 快捷栏显示登录与登出
	 * 
	 * @param activity
	 * @param qa
	 */
	public static void showSettingLoginOrLogout(Activity activity, QuickAction qa)
	{
		if (((AppContext) activity.getApplication()).isLogin())
		{
			qa.setIcon(MyQuickAction.buildDrawable(activity, R.drawable.ic_menu_logout));
			qa.setTitle(activity.getString(R.string.main_menu_logout));
		} else
		{
			qa.setIcon(MyQuickAction.buildDrawable(activity, R.drawable.ic_menu_login));
			qa.setTitle(activity.getString(R.string.main_menu_login));
		}
	}

	/**
	 * 用户登录或注销
	 * 
	 * @param activity
	 */
	public static void loginOrLogout(Activity activity)
	{
		AppContext ac = (AppContext) activity.getApplication();
		if (ac.isLogin())
		{
			 ac.Logout();
			 if(activity instanceof MainActivity){
				( (MainActivity)activity).getmWebSocketService().recontent();
			 }
			ToastMessage(activity, "已退出登录");
		} else
		{
			showLoginDialog(activity);
		}
	}

	public static void showChat(Context context ,XUserInfo userInfo){
		Intent intent=new Intent();
		intent.setClass(context, ChatActivity.class);
		intent.putExtra("userInfo", userInfo);
		context.startActivity(intent);
	}
	
	public static void showLostAndMarketCommentPub(Context context,long contentId){
		Intent intent=new Intent();
		intent.setClass(context, PubLostAndMarketComment.class);
		intent.putExtra("contentId", contentId);
		context.startActivity(intent);
	}
	/**
	 * 调用打电话程序
	 * @param phone
	 */
	public static void showCallPhone(Context context ,String phone){
		Intent i=new Intent();
		i.setAction(Intent.ACTION_CALL);
		i.setData(Uri.parse("tel:"+phone));
		context.startActivity(i);
	}
	/**
	 * 调用发短信程序
	 * @param context
	 * @param phone
	 */
	public static void showSendMsg(Context context ,String phone){
		Intent i=new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+phone));
//		i.setAction(Intent.ACTION_VIEW);
//		i.setType("vnd.android.dir/mms-sms");
//		i.setData(Uri.parse("content:mms-sms/conversations/"));
		context.startActivity(i);
	}
	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg)
	{
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}
	public static void ToastMessage(Context cont, String msg,boolean isTest){
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}
	public static void ToastMessage(Context cont, int msg)
	{
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time)
	{
		Toast.makeText(cont, msg, time).show();
	}

	/**
	 * 点击返回监听事件
	 * 
	 * @param activity
	 * @return
	 */
	public static View.OnClickListener finish(final Activity activity)
	{
		return new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				activity.finish();
			}
		};
	}

	/**
	 * 显示评论回复页面
	 * 
	 * @param context
	 * @param commentid
	 *            评论id
	 * @param contentid
	 *            内容id
	 * @param replyid
	 * @param authorid
	 * @param author
	 * @param content
	 */
	public static void showCommentReply(Activity context, long commentid, long contentid, long authorid,
			String author, String content)
	{
		Intent intent = new Intent(context, CommentPub.class);
		intent.putExtra("comment_id", commentid);
		intent.putExtra("content_id", contentid);
		intent.putExtra("user_id", authorid);
		intent.putExtra("user_name", author);
		intent.putExtra("comment_info", content);
		context.startActivityForResult(intent, 1);
		// if (catalog == CommentList.CATALOG_POST)
		// context.startActivityForResult(intent, REQUEST_CODE_FOR_REPLY);
		// else
		// context.startActivityForResult(intent, REQUEST_CODE_FOR_RESULT);
	}

	/**
	 *  跳转到发送留言界面
	 * @param context
	 * @param userId
	 */
	public static void showMessagePub(Activity context,long userId){
		Intent intent = new Intent(context, MessagePub.class);
		intent.putExtra("userId", userId);
		context.startActivity(intent);
	}
	
	/**
	 * 跳转到设置界面
	 * @param context
	 */
	public static void showSetting(Activity context){
		Intent intent = new Intent(context, SettingActivity.class);
		context.startActivity(intent);
	}
	
	/**
	 * 跳转到登陆或者注册页面
	 * @param context
	 * @param isLogin
	 */
	public static void showLoginOrRegiste(Activity context,boolean isLogin){
		Intent intent = new Intent(context, UserLoginActivity.class);
		intent.putExtra("isLogin", isLogin);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	/**
	 * 进入我的主页
	 * 
	 * @param activity
	 * @param userType
	 *            用户类型 1个人 2商家 3部门
	 * @param userInfo
	 */
	public static void showMyHome(Context context, XUserInfo userInfo)
	{

	}
	/**
	 * 绑定服务
	 * @param context
	 */
	public static void bindService(Context context,ServiceConnection connection){
		Intent bindIntent = new Intent(context,
				WebSocketService.class);
		context.bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
	}
	/**
	 * 解绑服务
	 * @param context
	 */
	public static void unBindService(Context context,ServiceConnection connection){
		context.unbindService(connection);
	}
	/**
	 * 组合回复引用文本
	 * 
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableString parseQuoteSpan(String name, String body)
	{
		SpannableString sp = new SpannableString("回复：" + name + "\n" + body);
		// 设置用户名字体加粗、高亮
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 3, 3 + name.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 3, 3 + name.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return sp;
	}

	/**
	 * 退出程序
	 * 
	 * @param cont
	 */
	public static void Exit(final Context cont)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_menu_surelogout);
		builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// 退出
				AppManager.getAppManager().AppExit(cont);
			}
		});
		builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		builder.show();
	}

	private static SpannableStringBuilder setUnderlineAndHightLighr(String str, int start, int end)
	{
		SpannableStringBuilder spannable = new SpannableStringBuilder(str);
		CharacterStyle span_2 = new ForegroundColorSpan(Color.RED);
		spannable.setSpan(span_2, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannable;
	}

	/**
	 * 
	 * @param str
	 * @param start
	 * @param end
	 * @param l
	 * @return
	 */
	public static SpannableString getClickableSpan(final Context activity, final XUserInfo userInfo)
	{
		OnClickListener l = new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				showUserInfo(activity, userInfo);
//				showMyHome(activity, userInfo);
				System.out.println("click text ");
			}

		};
		String str = userInfo.getUserRealName();
		int start = 0;
		int end = str.length();
		SpannableString spanableInfo = new SpannableString(setUnderlineAndHightLighr(str, start, end));
		spanableInfo.setSpan(new Clickable(l, str.substring(start, end)), start, end,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanableInfo;
	}


}

class Clickable extends ClickableSpan implements OnClickListener
{
	private final OnClickListener mListener;
	private final String str;

	public Clickable(OnClickListener l, String str)
	{
		this.mListener = l;
		this.str = str;
	}

	@Override
	public void onClick(View v)
	{
		v.setTag(str);
		mListener.onClick(v);
	}
}