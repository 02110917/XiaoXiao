package com.flying.xiao.common;

import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickAction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.flying.xiao.AlertPasswordActivity;
import com.flying.xiao.ChatActivity;
import com.flying.xiao.CommentPub;
import com.flying.xiao.ContentDetail;
import com.flying.xiao.DiaryDetail;
import com.flying.xiao.FullScreenPictureActivity;
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
import com.flying.xiao.MyMessageDetail;
import com.flying.xiao.PubContentActivity;
import com.flying.xiao.PubDiaryActivity;
import com.flying.xiao.PubLostActivity;
import com.flying.xiao.PubLostAndMarketComment;
import com.flying.xiao.PubMarketActivity;
import com.flying.xiao.R;
import com.flying.xiao.SearchActivity;
import com.flying.xiao.SettingActivity;
import com.flying.xiao.Suggestion;
import com.flying.xiao.UserInfoDetail;
import com.flying.xiao.UserInfoManage;
import com.flying.xiao.UserLoginActivity;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.app.AppManager;
import com.flying.xiao.asmack.XmppControl;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.constant.Constant.WebsocketCode;
import com.flying.xiao.db.DBHelper;
import com.flying.xiao.entity.ChatMessage;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.entity.XMessage;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.http.HttpUtil;
import com.flying.xiao.service.WebSocketService;
import com.flying.xiao.websocket.WPushUpdate;

/**
 * Ӧ�ó���UI���߰�����װUI��ص�һЩ����
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class UIHelper
{

	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;

	/** ȫ��web��ʽ */
	public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	/**
	 * ͼƬȫ�����
	 * 
	 * @param activity
	 * @author xzj
	 */
	public static void showFullScreenPicture(Context activity, ArrayList<String> urls, int currentPosition,
			int flag)
	{
		Intent intent = new Intent(activity, FullScreenPictureActivity.class);
		intent.putStringArrayListExtra("pictureurls", urls);
		intent.putExtra("currentPosition", currentPosition);
		intent.putExtra("picturetype", flag);
		activity.startActivity(intent);
	}

	/**
	 * ��ʾ��ҳ
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
	 * ��ʾ��¼ҳ��
	 * 
	 * @param activity
	 */
	public static void showLoginDialog(Context context)
	{
		showLoginOrRegiste((Activity) context, true);
	}

	/**
	 * ��ʾ�޸�����ҳ��
	 * 
	 * @param activity
	 */
	public static void showAlertPassword(Context context)
	{
		Intent intent = new Intent();
		intent.setClass(context, AlertPasswordActivity.class);
		context.startActivity(intent);
	}

	/**
	 * ��ʾ�˺Ź���ҳ��
	 * 
	 * @param activity
	 */
	public static void showUserInfoManage(Context context)
	{
		Intent intent = new Intent();
		intent.setClass(context, UserInfoManage.class);
		context.startActivity(intent);
	}

	/**
	 * ��ʾ����ҳ��
	 * 
	 * @param activity
	 */
	public static void showSuggestion(Context context)
	{
		Intent intent = new Intent();
		intent.setClass(context, Suggestion.class);
		context.startActivity(intent);
	}

	/**
	 * ��ʾ��������
	 * 
	 * @param context
	 */
	public static void showMyInfo(Context context)
	{
		Intent intent = new Intent();
		intent.setClass(context, MyInfoActivity.class);
		context.startActivity(intent);
	}

	public static void showMyFriends(Context context)
	{
		Intent intent = new Intent();
		intent.setClass(context, MyFriends.class);
		context.startActivity(intent);
	}

	public static void showMyCollect(Context context, int showType)
	{
		Intent intent = new Intent();
		intent.setClass(context, MyCollect.class);
		intent.putExtra("showType", showType);
		context.startActivity(intent);
	}

	public static void showMyDynamic(Context context)
	{
		Intent intent = new Intent();
		intent.setClass(context, MyDynamic.class);
		context.startActivity(intent);
	}

	public static void showMyMessage(Context context)
	{
		Intent intent = new Intent();
		intent.setClass(context, MyMessage.class);
		context.startActivity(intent);
	}

	public static void showMyChat(Context context)
	{
		Intent intent = new Intent();
		intent.setClass(context, MyChatActivity.class);
		context.startActivity(intent);
	}

	/**
	 * ��ʾ��������
	 * @param context
	 */
	public static void showMyMessageDetail(Context context,XMessage message)
	{
		Intent intent = new Intent();
		intent.setClass(context, MyMessageDetail.class);
		intent.putExtra("message", message);
		context.startActivity(intent);
	}

	
	/**
	 * ��ʾ��������ҳ��
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
	 * ��ʾ��������ҳ��
	 * 
	 * @param context
	 *            ������
	 * @param index
	 *            ����
	 * @param type
	 *            user type
	 */
	public static void showCommunityInfo(Context context, int index, int type)
	{
		Intent intent = new Intent();
		intent.putExtra("index", index);
		intent.putExtra("userType", type);
		if (type == Constant.UserType.User_TYPE_DEPARTMENT || type == Constant.UserType.User_TYPE_BUSINESS)
		{
			intent.setClass(context, UserInfoDetail.class);
		}
		context.startActivity(intent);
	}

	public static void showUserInfo(Context context, XUserInfo userInfo)
	{
		Intent intent = new Intent();
		intent.putExtra("userinfo", userInfo);
		intent.setClass(context, UserInfoDetail.class);
		context.startActivity(intent);
	}

	public static void showPubDiary(Context context)
	{
		Intent intent = new Intent();
		intent.setClass(context, PubDiaryActivity.class);
		context.startActivity(intent);
	}

	public static void showPubLost(Context context)
	{
		Intent intent = new Intent();
		intent.setClass(context, PubLostActivity.class);
		context.startActivity(intent);
	}

	public static void showPubMarket(Context context)
	{
		Intent intent = new Intent();
		intent.setClass(context, PubMarketActivity.class);
		context.startActivity(intent);
	}

	public static void showPubContent(Context context, int contentType, int wzType)
	{
		Intent intent = new Intent();
		intent.setClass(context, PubContentActivity.class);
		intent.putExtra("type", contentType);
		intent.putExtra("wztype", wzType);
		context.startActivity(intent);

	}

	/**
	 * ����ϵͳ���
	 * 
	 * @param context
	 * @param userInfo
	 */
	public static void showPicture(Activity context)
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		context.startActivityForResult(intent, MyInfoActivity.FROM_ALBUM);
	}

	public static void showCamera(Activity context, Uri imageUrl)
	{
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUrl);
		// intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		context.startActivityForResult(intent, MyInfoActivity.TAKE_PICTURE);
	}

	/**
	 * �ü�ͼ��
	 * 
	 * @param context
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	public static void showCropImage(Activity context, Uri uri, int outputX, int outputY, int requestCode)
	{
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", "1");
		intent.putExtra("aspectY", "1");
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("outputFormat", "JPEG");
		// intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true);
		context.startActivityForResult(intent, requestCode);
	}

	/**
	 * �������ʾ��¼��ǳ�
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
	 * �û���¼��ע��
	 * 
	 * @param activity
	 */
	public static void loginOrLogout(Activity activity)
	{
		AppContext ac = (AppContext) activity.getApplication();
		if (ac.isLogin())
		{
			ac.Logout();
			if (activity instanceof MainActivity)
			{
				// ����getmWebSocketServiceһ��Ҫ��BaseActivity��service
				((MainActivity) activity).getmWebSocketService().setXmppLogin(false);
				XmppControl.getShare(activity).deleteAccount();
			}
			ToastMessage(activity, "���˳���¼");
		} else
		{
			showLoginDialog(activity);
		}
	}

	public static void showChat(Context context, XUserInfo userInfo)
	{
		Intent intent = new Intent();
		intent.setClass(context, ChatActivity.class);
		intent.putExtra("userInfo", userInfo);
		context.startActivity(intent);
	}

	public static void showLostAndMarketCommentPub(Context context, long contentId)
	{
		Intent intent = new Intent();
		intent.setClass(context, PubLostAndMarketComment.class);
		intent.putExtra("contentId", contentId);
		context.startActivity(intent);
	}

	public static void showLostAndMarketChatPub(Context context, XUserInfo userInfo)
	{
		Intent intent = new Intent();
		intent.setClass(context, ChatActivity.class);
		intent.putExtra("userInfo", userInfo);
		context.startActivity(intent);
	}
	
	/**
	 * ���ô�绰����
	 * 
	 * @param phone
	 */
	public static void showCallPhone(Context context, String phone)
	{
		Intent i = new Intent();
		i.setAction(Intent.ACTION_CALL);
		i.setData(Uri.parse("tel:" + phone));
		context.startActivity(i);
	}

	/**
	 * ���÷����ų���
	 * 
	 * @param context
	 * @param phone
	 */
	public static void showSendMsg(Context context, String phone)
	{
		Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
		// i.setAction(Intent.ACTION_VIEW);
		// i.setType("vnd.android.dir/mms-sms");
		// i.setData(Uri.parse("content:mms-sms/conversations/"));
		context.startActivity(i);
	}

	/**
	 * ����Toast��Ϣ
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg)
	{
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, boolean isTest)
	{
		// TODO �رյ���toast
		if (!isTest)
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
	 * ������ؼ����¼�
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
	 * ��ʾ���ۻظ�ҳ��
	 * 
	 * @param context
	 * @param commentid
	 *            ����id
	 * @param contentid
	 *            ����id
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
	 * ��ת���������Խ���
	 * 
	 * @param context
	 * @param userId
	 */
	public static void showMessagePub(Activity context, long userId)
	{
		Intent intent = new Intent(context, MessagePub.class);
		intent.putExtra("userId", userId);
		context.startActivity(intent);
	}

	/**
	 * ��ת�����ý���
	 * 
	 * @param context
	 */
	public static void showSetting(Activity context)
	{
		Intent intent = new Intent(context, SettingActivity.class);
		context.startActivity(intent);
	}

	/**
	 * ��ת����½����ע��ҳ��
	 * 
	 * @param context
	 * @param isLogin
	 */
	public static void showLoginOrRegiste(Activity context, boolean isLogin)
	{
		Intent intent = new Intent(context, UserLoginActivity.class);
		intent.putExtra("isLogin", isLogin);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * �����ҵ���ҳ
	 * 
	 * @param activity
	 * @param userType
	 *            �û����� 1���� 2�̼� 3����
	 * @param userInfo
	 */
	public static void showMyHome(Context context, XUserInfo userInfo)
	{

	}

	public static void showSearchActivity(Context context)
	{
		Intent intent = new Intent(context, SearchActivity.class);
		context.startActivity(intent);
	}

	/**
	 * �󶨷���
	 * 
	 * @param context
	 */
	public static void bindService(Context context, ServiceConnection connection)
	{
		Intent bindIntent = new Intent(context, WebSocketService.class);
		context.bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * ������
	 * 
	 * @param context
	 */
	public static void unBindService(Context context, ServiceConnection connection)
	{
		context.unbindService(connection);
	}

	/**
	 * ��ϻظ������ı�
	 * 
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableString parseQuoteSpan(String name, String body)
	{
		SpannableString sp = new SpannableString("�ظ���" + name + "\n" + body);
		// �����û�������Ӵ֡�����
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 3, 3 + name.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 3, 3 + name.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return sp;
	}

	/**
	 * ����App�쳣��������
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont, final String crashReport)
	{
		// ���ʹ��󱨸�
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setNegativeButton(R.string.sure, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				// �˳�
				AppManager.getAppManager().AppExit(cont);
			}
		});
		builder.show();
	}

	public static void showUpdateDialog(final Context cont, final WPushUpdate update)
	{
		if (getVersionCode(cont) >= update.getVerCode())
			return;
		final SharedPreferences share = cont.getSharedPreferences("update_time", Context.MODE_PRIVATE);
		long time = share.getLong("time", 0);
		if (System.currentTimeMillis() - time < 60 * 60 * 1000)
		{
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("���°汾���Ը���");
		builder.setMessage("���°汾����,��ǰ�汾:" + getVersion(cont) + ",���°汾:" + update.getVer() + "\n��������:"
				+ update.getUpdateInfo());
		builder.setPositiveButton("���ظ���", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				showDownDialog(cont, update);
				share.edit().putLong("time", System.currentTimeMillis()).commit();
			}
		});
		builder.setNegativeButton("�´���˵", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				share.edit().putLong("time", System.currentTimeMillis()).commit();
			}
		});
		builder.show();

	}

	public static void showDownDialog(final Context context, final WPushUpdate update)
	{
		final ProgressDialog pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setCancelable(false);
		pd.show();
		final Handler handler = new Handler()
		{
			int fileLength = 0;

			public void handleMessage(Message msg)
			{
				if (!Thread.currentThread().isInterrupted())
				{
					switch (msg.what)
					{
					case 0:
						fileLength = (Integer) msg.obj;
						pd.setMax(fileLength);
						Log.i("�ļ�����----------->", pd.getMax() + "");
						break;
					case 1:
						int downedFileLength = (Integer) msg.obj;
						pd.setProgress(downedFileLength);
						int x = downedFileLength * 100 / fileLength;
						break;
					case 2:
						Toast.makeText(context, "�������", Toast.LENGTH_LONG).show();
						String path = (String) msg.obj;
						pd.dismiss();
						install(context, path);
						break;
					case 1100:
						ToastMessage(context, "���س���..");
						break;
					default:
						break;
					}
				}
			}

		};
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					HttpUtil.downLoadFile(URLs.HOST + update.getApkUrl(), handler);
				} catch (Exception e)
				{
					e.printStackTrace();
					handler.sendEmptyMessage(1100);
				}
			}
		}).start();
	}

	/**
	 * ��װ
	 * 
	 * @param context
	 * 
	 */
	public static void install(Context context, String url)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(url)), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static int getVersionCode(Context context)
	{
		PackageManager manager = context.getPackageManager();
		try
		{
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
			return 1;
		}
	}

	/**
	 * ��ȡ�汾��
	 * 
	 * @return ��ǰӦ�õİ汾��
	 */
	public static String getVersion(Context context)
	{
		try
		{
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e)
		{
			e.printStackTrace();
			return "1.0";
		}
	}

	/**
	 * �˳�����
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
				// �˳�
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
				// showMyHome(activity, userInfo);
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

	/**
	 * @author xzj
	 * @param context
	 * @param chatMessage
	 * @param userinfos
	 * @param WebSocketCode
	 */
	@SuppressWarnings("deprecation")
	public static void sendNotification(Context context, ChatMessage chatMessage, List<XUserInfo> userinfos,
			int WebSocketCode)
	{
		if(chatMessage==null)
			return ;
		// ����һ��NotificationManager������
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// ����Notification�ĸ�������
		Notification notification = new Notification();
		// ֪ͨͼ��
		notification.icon = R.drawable.ic_launcher;

		// ֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ
		notification.when = System.currentTimeMillis();
		// �������
		notification.defaults |= Notification.DEFAULT_SOUND;
		// �����
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		// ���LED������
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		// ��֪ͨ���ϵ����֪ͨ���Զ������֪ͨ
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// ֪ͨ������
		String contentTitle = null;
		// ֪ͨ������
		String contentText = null;

		Intent notificationIntent = null;

		switch (WebSocketCode)
		{
		// ���ѷ�����Ϣ
		case Constant.XmppHandlerMsgCode.HANDLER_CODE_GET_MESSAGE:
			System.out.println("����������Ϣ~��");
			// �����֪ͨ��Ҫ��ת��Activity
			notificationIntent = new Intent(context, MyChatActivity.class);
			// ���Ҫ�Ը�Intent����һ��Activity��һ��Ҫ���� Intent.FLAG_ACTIVITY_NEW_TASK ��ǡ�
			// Intent.FLAG_ACTIVITY_CLEAR_TOP
			// ������ڵ�ǰTask�У���Ҫ������Activity����ô�Ѹ�Acitivity֮ǰ������Activity���ص������Ѵ�Activity��ǰ�Ա��ⴴ��Activity��ʵ����
			// Intent.FLAG_ACTIVITY_NEW_TASK
			// ��ϵͳ���鵱ǰ�����Ѵ�����Task���Ƿ��и�Ҫ������Activity��Task�����У����ڸ�Task�ϴ���Activity����û�����½����и�Activity���Ե�Task�����ڸ��½���Task�ϴ���Activity��
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

			// ״̬����ʾ��֪ͨ�ı���ʾ
			notification.tickerText = chatMessage.getFrom() + "����һ����Ϣ";
			contentTitle = chatMessage.getFrom();
			contentText = chatMessage.getBody();
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

			break;
		// ���ѷ���������Ϣ
		case Constant.XmppHandlerMsgCode.HANDLER_CODE_GET_OFF_LINE_MESSAGE:
			System.out.println("����������Ϣ~��");
			// �����֪ͨ��Ҫ��ת��Activity
			notificationIntent = new Intent(context, MyChatActivity.class);
			// ���Ҫ�Ը�Intent����һ��Activity��һ��Ҫ���� Intent.FLAG_ACTIVITY_NEW_TASK ��ǡ�
			// Intent.FLAG_ACTIVITY_CLEAR_TOP
			// ������ڵ�ǰTask�У���Ҫ������Activity����ô�Ѹ�Acitivity֮ǰ������Activity���ص������Ѵ�Activity��ǰ�Ա��ⴴ��Activity��ʵ����
			// Intent.FLAG_ACTIVITY_NEW_TASK
			// ��ϵͳ���鵱ǰ�����Ѵ�����Task���Ƿ��и�Ҫ������Activity��Task�����У����ڸ�Task�ϴ���Activity����û�����½����и�Activity���Ե�Task�����ڸ��½���Task�ϴ���Activity��
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
//			System.out.println(chatMessage.getFrom());
			// ״̬����ʾ��֪ͨ�ı���ʾ
			notification.tickerText = chatMessage.getFrom() + "����������������Ϣ";
			// ֪ͨ������
			contentTitle = chatMessage.getFrom();
			// ֪ͨ������
			contentText = chatMessage.getBody();
			System.out.println(contentText);
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

			break;

		// ���������Ϊ����
		case Constant.HandlerMessageCode.GET_MY_USERS_SUCCESS:
			System.out.println("���������~��");
			// �����֪ͨ��Ҫ��ת��Activity
			notificationIntent = new Intent(context, MyFriends.class);
			// ���Ҫ�Ը�Intent����һ��Activity��һ��Ҫ���� Intent.FLAG_ACTIVITY_NEW_TASK ��ǡ�
			// Intent.FLAG_ACTIVITY_CLEAR_TOP
			// ������ڵ�ǰTask�У���Ҫ������Activity����ô�Ѹ�Acitivity֮ǰ������Activity���ص������Ѵ�Activity��ǰ�Ա��ⴴ��Activity��ʵ����
			// Intent.FLAG_ACTIVITY_NEW_TASK
			// ��ϵͳ���鵱ǰ�����Ѵ�����Task���Ƿ��и�Ҫ������Activity��Task�����У����ڸ�Task�ϴ���Activity����û�����½����и�Activity���Ե�Task�����ڸ��½���Task�ϴ���Activity��
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			StringBuffer newFriends = new StringBuffer();
			for (XUserInfo xUserInfo : userinfos)
			{
				newFriends.append(xUserInfo.getUserName() + "��");
			}
			newFriends.deleteCharAt(newFriends.length() - 1);
			newFriends.append("�����Ϊ����Ŷ~��");
			// ״̬����ʾ��֪ͨ�ı���ʾ
			notification.tickerText = "��С��������Ϊ����Ŷ~��";
			// ֪ͨ������
			contentTitle = "��Ӻ���";
			// ֪ͨ������
			contentText = newFriends.toString();
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			break;
		// Ԥ������

		default:
			break;
		}
		// ��Notification���ݸ� NotificationManager
		notificationManager.notify(0, notification);
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