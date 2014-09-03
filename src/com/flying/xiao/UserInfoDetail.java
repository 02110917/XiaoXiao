package com.flying.xiao;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.flying.xiao.adapter.MyFragmentPaperAdapter;
import com.flying.xiao.asmack.XmppControl;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.db.DBHelper;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.fragment.MainDiary;
import com.flying.xiao.fragment.UserInfoDetailFragment;
import com.flying.xiao.util.ImageManager2;

public class UserInfoDetail extends BaseActivity
{

	/**
	 * 顶部
	 */

	PopupWindow mPopupWindow;
	private ImageView mHeadImage; // 部门头像
	private TextView mName; // 部门名称
	private TextView mDescribe; // 描述
	private Button mAddfriend; // 添加关注

	private ViewPager mViewPaper;
	private List<Fragment> mFragmentList;
	private Button mBtnBmjs; // 部门介绍
	private Button mBtnGg; // 公告
	private Button mBtnZx; // 招新
	private Button mBtnDt; // 动态
	List<Button> mTabBtn;
	private XUserInfo userInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.department_detail);
		initHeadView();
		initView();
		initData();
	}


	private void initView()
	{

	
		userInfo=(XUserInfo) getIntent().getSerializableExtra("userinfo");
		if(userInfo==null)
		{
			int index = getIntent().getIntExtra("index", 0);
			int userType = getIntent().getIntExtra("userType", 1);
			if (userType == Constant.UserType.User_TYPE_BUSINESS)
			{
				userInfo = appContext.listManager.getBusinessList().get(index);
			} else if (userType == Constant.UserType.User_TYPE_DEPARTMENT)
			{
				userInfo = appContext.listManager.getDepartmentList().get(index);
			}
			userInfo.setMeFriend(DBHelper.getDbHelper(this).isHaveThisUser(userInfo.getId()));
		}
		/**
		 * 顶部
		 */
		mHeadTitle.setText(userInfo.getUserRealName());
		mHeadRightView.setVisibility(View.VISIBLE);
		mHeadRightView.setImageResource(R.drawable.btn_more);
		mHeadRightView.setOnClickListener(clickListener);
		mTabBtn = new ArrayList<Button>();
		mHeadImage = (ImageView) findViewById(R.id.department_head_image);
		mName = (TextView) findViewById(R.id.department_name);
		mDescribe = (TextView) findViewById(R.id.department_describe);
		mAddfriend = (Button) findViewById(R.id.department_add_friend);
		if (userInfo.isMeFriend())
		{
			mAddfriend.setText("已关注");
		}
		String url = URLs.HOST + userInfo.getUserHeadImageUrl();
		ImageManager2.from(this).displayImage(mHeadImage, url, -1);
		mHeadImage.setTag(url);
		mName.setText(userInfo.getUserRealName());
		mDescribe.setText(userInfo.getUserGerenshuoming());

		mAddfriend.setOnClickListener(clickListener);

		/**
		 * tab fragment
		 */
		mBtnBmjs = (Button) findViewById(R.id.department_detail_bmjs);
		mBtnGg = (Button) findViewById(R.id.department_detail_gg);
		mBtnZx = (Button) findViewById(R.id.department_detail_product);
		mBtnDt = (Button) findViewById(R.id.department_detail_dt);
		mFragmentList = new ArrayList<Fragment>();
		if (userInfo.getUserTypeId() != Constant.UserType.User_TYPE_PESONAL)
		{
			mBtnBmjs.setTag(0);
			mBtnDt.setTag(1);
			mBtnGg.setTag(2);
			mBtnZx.setTag(3);

			mTabBtn.add(mBtnBmjs);
			mTabBtn.add(mBtnGg);
			mTabBtn.add(mBtnZx);
			mTabBtn.add(mBtnDt);
			mBtnBmjs.setEnabled(false);
			mBtnGg.setOnClickListener(clickListener);
			mBtnBmjs.setOnClickListener(clickListener);
			mBtnZx.setOnClickListener(clickListener);
			mBtnDt.setOnClickListener(clickListener);
			BmjsFragment bmjs = new BmjsFragment();
			UserInfoDetailFragment detail1 = new UserInfoDetailFragment();
			detail1.setType(Constant.WzType.WZTYPE_WJTZ);
			detail1.setUserInfo(userInfo);
			UserInfoDetailFragment detail2 = new UserInfoDetailFragment();
			if (userInfo.getUserTypeId() == Constant.UserType.User_TYPE_DEPARTMENT)
			{
				detail2.setType(Constant.WzType.WZTYPE_ZXZP);
				mBtnZx.setText("招新");
			} else
			{
				detail2.setType(Constant.WzType.WZTYPE_WP);
				mBtnZx.setText("商品");

			}
			detail2.setUserInfo(userInfo);
			MainDiary diary = new MainDiary();
			diary.setUserInfo(userInfo);
			mFragmentList.add(bmjs);
			mFragmentList.add(diary);
			mFragmentList.add(detail1);
			mFragmentList.add(detail2);
		}else{
			mBtnBmjs.setVisibility(View.GONE);
			mBtnGg.setVisibility(View.GONE);
			mBtnDt.setTag(0);
			mBtnZx.setTag(1);
			mTabBtn.add(mBtnDt);
			mTabBtn.add(mBtnZx);
			mBtnZx.setOnClickListener(clickListener);
			mBtnDt.setOnClickListener(clickListener);
			UserInfoDetailFragment detail2 = new UserInfoDetailFragment();
			detail2.setType(Constant.WzType.WZTYPE_WP);
			detail2.setUserInfo(userInfo);
			mBtnZx.setText("商品");
			MainDiary diary = new MainDiary();
			diary.setUserInfo(userInfo);
			mFragmentList.add(diary);
			mFragmentList.add(detail2);
		}
		MyFragmentPaperAdapter adapter = new MyFragmentPaperAdapter(getSupportFragmentManager(),
				mFragmentList);
		mViewPaper = (ViewPager) findViewById(R.id.viewpager);
		mViewPaper.setAdapter(adapter);
		mViewPaper.setCurrentItem(0);
		mViewPaper.setOnPageChangeListener(new OnPageChangeListener()
		{

			@Override
			public void onPageSelected(int arg0)
			{
				for (Button btn : mTabBtn)
				{
					if (((Integer) btn.getTag()) == arg0)
					{
						btn.setEnabled(false);
					} else
					{
						btn.setEnabled(true);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{

			}

			@Override
			public void onPageScrollStateChanged(int arg0)
			{

			}
		});
		// mInfo=(WebView)findViewById(R.id.department_detail_webview);

	}

	private void initData()
	{
		mHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					UIHelper.ToastMessage(UserInfoDetail.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(UserInfoDetail.this);
					break;
				case Constant.HandlerMessageCode.ADD_FRIEND_FAIL:
					UIHelper.ToastMessage(UserInfoDetail.this, "添加失败");
					break;
				case Constant.HandlerMessageCode.ADD_FRIEND_IS_YOUR_FRIEND_ALERADY:
					UIHelper.ToastMessage(UserInfoDetail.this, "您已经添加他为好友啦,不能重复添加");
					mAddfriend.setText("已关注");
					break;
				case Constant.HandlerMessageCode.ADD_FRIEND_SUCCESS:
					mAddfriend.setText("已关注");
					userInfo.setMeFriend(true);
					break;
				case Constant.XmppHandlerMsgCode.HANDLER_ADD_PRIEND_FAILD: //xmpp add friend faild
					UIHelper.ToastMessage(appContext, "添加好友失败,请重试...");
					break ;
				case Constant.XmppHandlerMsgCode.HANDLER_ADD_PRIEND_SUCCESS: //
//					UIHelper.ToastMessage(appContext, "添加好友成功");
					DBHelper.getDbHelper(UserInfoDetail.this).insertFriend(userInfo);
					mAddfriend.setText("已关注");
					userInfo.setMeFriend(true);
				default:
					break;
				}
			}
		};
	}

	/**
	 * add friend
	 */
	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.department_add_friend:
				if (!appContext.isLogin())
				{
					mHandler.sendEmptyMessage(Constant.HandlerMessageCode.USER_NOT_LOGIN);
				}
				if (userInfo.isMeFriend())
				{
					mHandler.sendEmptyMessage(Constant.HandlerMessageCode.ADD_FRIEND_IS_YOUR_FRIEND_ALERADY);
				}
				if(getmWebSocketService().isXmppLogin())
					XmppControl.getShare(UserInfoDetail.this).addFriend(userInfo.getUserName(), userInfo.getUserRealName(), mHandler);
//				NetControl.getShare(UserInfoDetail.this).addFriend(userInfo.getId(), mHandler);
				break;
			case R.id.department_detail_gg:
			case R.id.department_detail_bmjs:
			case R.id.department_detail_product:
			case R.id.department_detail_dt:
				int tag = (Integer) v.getTag();
				mViewPaper.setCurrentItem(tag);
				v.setEnabled(false);
				for (Button btn : mTabBtn)
				{
					if (btn != v)
					{
						btn.setEnabled(true);
					}
				}
				break;
			case R.id.head_right_view: // more
				if (mPopupWindow == null)
				{
					initPopuMenu();
				}
				if (mPopupWindow.isShowing())
				{
					mPopupWindow.dismiss();
				} else
				{
					mPopupWindow.showAsDropDown(v, 0, 5);
				}
				break;
			case R.id.more_ly:
				UIHelper.showMessagePub(UserInfoDetail.this, userInfo.getId());
				break ;
			case R.id.more_sx:
				UIHelper.showChat(UserInfoDetail.this, userInfo);
				break ;
			case R.id.more_dx:
				UIHelper.showSendMsg(UserInfoDetail.this, userInfo.getUserPhone());
				break ;
			case R.id.more_dh:
				UIHelper.showCallPhone(UserInfoDetail.this, userInfo.getUserPhone());
				break ;
			default:
				break;
			}
		}
	};

	private void initPopuMenu()
	{

		View view = LayoutInflater.from(this).inflate(R.layout.more_popupmenu, null);
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		float density = dm.density;
		mPopupWindow = new PopupWindow(view, (int) (100 * density), (int) (155 * density));
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		TextView tvLy=(TextView)view.findViewById(R.id.more_ly);
		TextView tvSx=(TextView)view.findViewById(R.id.more_sx);
		TextView tvDx=(TextView)view.findViewById(R.id.more_dx);
		TextView tvDh=(TextView)view.findViewById(R.id.more_dh);
		tvLy.setOnClickListener(clickListener);
		tvSx.setOnClickListener(clickListener);
		tvDx.setOnClickListener(clickListener);
		tvDh.setOnClickListener(clickListener);
	}

	@SuppressLint("ValidFragment")
	public class BmjsFragment extends Fragment
	{
		private WebView mInfo; // 详细介绍

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View v = inflater.inflate(R.layout.department_fragment_bmjs, null);
			mInfo = (WebView) v.findViewById(R.id.department_detail_webview);
			String body = UIHelper.WEB_STYLE + userInfo.getUserInfoDetail()
					+ "<div style=\"margin-bottom: 80px\" />";
			body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
			body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
			mInfo.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
			return v;
		}

	}
}
