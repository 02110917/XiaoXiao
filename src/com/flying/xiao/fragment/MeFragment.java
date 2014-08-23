package com.flying.xiao.fragment;

import com.flying.xiao.R;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.util.ImageManager2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 个人
 * 
 * @author zhangmin
 *
 */
public class MeFragment extends Fragment
{

	private AppContext appContext;
	private Button mBtnLogin;
	private Button mBtnRegiste;
	private LinearLayout mNotLoginView;
	private ScrollView mScrollView;

	private XUserInfo userInfo;
	private ImageView mPhoto;
	private TextView mName ;
	private TextView mInfo ; //个人说明
	
	//grid btn
	private LinearLayout mLinFriendDynamic ;//好友动态
	private LinearLayout mLinFriends ;//好友
	private LinearLayout mLinCollect ;//我的收藏
	private LinearLayout mLinMessage ;//留言
	private LinearLayout mLinChat ;//聊天
	private LinearLayout mLinPub ;//我发布的
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		appContext = (AppContext) getActivity().getApplication();
		userInfo=appContext.getUserInfo() ;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.user_center, null);
		initView(v);

		return v;
	}

	private void initView(View v)
	{
		mNotLoginView=(LinearLayout) v.findViewById(R.id.not_login_view);
		mScrollView=(ScrollView) v.findViewById(R.id.scrollView1);
		changeView();
		mBtnLogin = (Button) v.findViewById(R.id.login_btn);
		mBtnRegiste = (Button) v.findViewById(R.id.reg_btn);
		mBtnLogin.setOnClickListener(listener);
		mBtnRegiste.setOnClickListener(listener);

		mPhoto=(ImageView)v.findViewById(R.id.imageView2);
		mName=(TextView)v.findViewById(R.id.user_name);
		mInfo=(TextView)v.findViewById(R.id.user_info);
		showMyinfo();
		mPhoto.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				UIHelper.showMyInfo(getActivity());
			}
		});
		
		//grid 
		
		mLinFriendDynamic=(LinearLayout)v.findViewById(R.id.button_dt);
		mLinFriends=(LinearLayout)v.findViewById(R.id.button_myfriend);
		mLinCollect=(LinearLayout)v.findViewById(R.id.button_my_collect);
		mLinMessage=(LinearLayout)v.findViewById(R.id.button_my_ly);
		mLinPub=(LinearLayout)v.findViewById(R.id.button_my_pub);
		mLinChat=(LinearLayout)v.findViewById(R.id.button_my_message);
		mLinFriendDynamic.setOnClickListener(listener);
		mLinFriends.setOnClickListener(listener);
		mLinCollect.setOnClickListener(listener);
		mLinMessage.setOnClickListener(listener);
		mLinPub.setOnClickListener(listener);
		mLinChat.setOnClickListener(listener);
		
		
		
	}
	
	private void showMyinfo(){
		if(userInfo!=null)
		{
			ImageManager2.from(getActivity()).displayImage(mPhoto,URLs.HOST+ userInfo.getUserHeadImageUrl(),  R.drawable.widget_dface);
			mName.setText(userInfo.getUserRealName());
			mInfo.setText(userInfo.getUserGerenshuoming());
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		changeView();
		showMyinfo();
	}

	private void changeView(){
		if (appContext.isLogin())
		{
			mNotLoginView.setVisibility(View.GONE);
			mScrollView.setVisibility(View.VISIBLE);
		} else
		{
			mNotLoginView.setVisibility(View.VISIBLE);
			mScrollView.setVisibility(View.GONE);
		}
	}
	private OnClickListener listener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.login_btn:
				UIHelper.showLoginOrRegiste(getActivity(), true);
				break;
			case R.id.reg_btn:
				UIHelper.showLoginOrRegiste(getActivity(), false);
				break;
			case R.id.button_dt:
				UIHelper.showMyDynamic(getActivity());
				break ;
			case R.id.button_myfriend:
				UIHelper.showMyFriends(getActivity());
				break ;
			case R.id.button_my_collect:
				UIHelper.showMyCollect(getActivity(),Constant.MainContentFragmentShowType.TYPE_MY_COLLECT);
				break ;
			case R.id.button_my_message: //xiaoxi 
				UIHelper.showMyChat(getActivity());
				break ;
			case R.id.button_my_ly:
				UIHelper.showMyMessage(getActivity());
				break ;
			case R.id.button_my_pub:
				UIHelper.showMyCollect(getActivity(),Constant.MainContentFragmentShowType.TYPE_MY_PUB);
				break ;
			
			default:
				break;
			}
		}
	};

}
