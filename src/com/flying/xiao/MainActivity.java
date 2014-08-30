package com.flying.xiao;

import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.flying.xiao.adapter.FragmentTabAdapter;
import com.flying.xiao.adapter.FragmentTabAdapter.OnRgsExtraCheckedChangedListener;
import com.flying.xiao.app.AppContext;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.fragment.CommunityFragment;
import com.flying.xiao.fragment.LifeFragment;
import com.flying.xiao.fragment.MainDiary;
import com.flying.xiao.fragment.MainFragment;
import com.flying.xiao.fragment.MeFragment;
import com.tencent.a.a.a;

public class MainActivity extends BaseActivity
{
	public static final int QUICKACTION_PUB_DIARY = 0;
	public static final int QUICKACTION_PUB_LOST = 1;
	public static final int QUICKACTION_PUB_MARKET = 2;
	public static final int QUICKACTION_PUB_ZIXUN = 3;

	private Fragment mainFragment;
	private Fragment lifeFragment;
	private Fragment communityFragment; // 社区
	private Fragment meFragment;
	private RadioGroup mRadioGroup;
	private RadioButton mRBtnMain ;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	private ImageView fbSetting;
	private ImageView mainHeadLogo;
	private TextView mainHeadTitle;
	private ImageButton mRightBtn;
	private QuickActionWidget mGrid;// 快捷栏控件
	
	private AppContext appContext;// 全局Context
	private String[] titles = new String[]
	{ "首页", "广场", "社区", "个人" };
	private int[]headImages=new int[]{R.drawable.main,R.drawable.square,R.drawable.community,R.drawable.personal};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		appContext = (AppContext) getApplication();
		// 网络连接判断
		if (!appContext.isNetworkConnected())
			UIHelper.ToastMessage(this, R.string.network_not_connected);
		initFootBar();
		initQuickActionGrid();
		initMain();
		
	}

	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("MainActivity:----onDestroy");
	}


	/**
	 * 初始化底部栏
	 */
	private void initFootBar()
	{
		mainHeadLogo=(ImageView)findViewById(R.id.main_head_logo);
		mRBtnMain=(RadioButton)findViewById(R.id.footbar_main);
		mRBtnMain.setChecked(true);
		
		fbSetting = (ImageView) findViewById(R.id.main_footbar_pub);
		fbSetting.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// 展示快捷栏&判断是否登录&是否加载文章图片
//				UIHelper.showSettingLoginOrLogout(MainActivity.this, mGrid.getQuickAction(0));
				mGrid.show(v);
				
			}
		});
	}

	private void initMain()
	{
		mRightBtn = (ImageButton) findViewById(R.id.main_head_search);
		mainHeadTitle = (TextView) findViewById(R.id.main_head_title);
		mRadioGroup = (RadioGroup) findViewById(R.id.footbargroup);
		mainFragment = new MainFragment();
		lifeFragment = new MainDiary();
		communityFragment = new CommunityFragment();
		meFragment = new MeFragment();
		fragments.add(mainFragment);
		fragments.add(lifeFragment);
		fragments.add(communityFragment);
		fragments.add(meFragment);
		FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments, R.id.main_scrolllayout,
				mRadioGroup);
		tabAdapter.setOnRgsExtraCheckedChangedListener(new OnRgsExtraCheckedChangedListener()
		{

			@Override
			public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index)
			{
				mainHeadTitle.setText(titles[index]);
				mainHeadLogo.setImageResource(headImages[index]);
				if(index==3){
					mRightBtn.setBackgroundResource(R.drawable.widget_bar_set);
				}else{
					mRightBtn.setBackgroundResource(R.drawable.frame_icon_search);
				}
			}
		});

		mRightBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				switch (mRadioGroup.getCheckedRadioButtonId())
				{
				case R.id.footbar_main:
				case R.id.footbar_life:
				case R.id.footbar_community:
					UIHelper.showSearchActivity(MainActivity.this);
					break ;
				case R.id.footbar_me:
					UIHelper.showSetting(MainActivity.this);
					break;
				default:
					break;
				}
			}
		});
	}


	
	/**
	 * 初始化快捷栏
	 */
	private void initQuickActionGrid()
	{
		mGrid = new QuickActionGrid(this);
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.pub_diary, R.string.main_menu_diary));
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.pub_lost, R.string.main_menu_pub_lost));
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.pub_market, R.string.main_menu_pub_market));
		mGrid.addQuickAction(new MyQuickAction(this, R.drawable.pub_news, R.string.main_menu_ask));
		mGrid.setOnQuickActionClickListener(mActionListener);
	}

	/**
	 * 快捷栏item点击事件
	 */
	private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener()
	{
		@Override
		public void onQuickActionClicked(QuickActionWidget widget, int position)
		{
			switch (position)
			{
//			case QUICKACTION_LOGIN_OR_LOGOUT:// 用户登录-注销
//				UIHelper.loginOrLogout(MainActivity.this);
//				if(fragments.get(3).isAdded())
//					fragments.get(3).onResume();
//				break;
			 case QUICKACTION_PUB_DIARY:// 我的资料
				 if(appContext.isLogin())
					 UIHelper.showPubDiary(MainActivity.this);
				 else
					 UIHelper.showLoginDialog(MainActivity.this);
			 break;
			 case QUICKACTION_PUB_LOST:// 开源软件
				 if(appContext.isLogin())
					 UIHelper.showPubLost(MainActivity.this);
				 else
					 UIHelper.showLoginDialog(MainActivity.this);
			 break;
			 case QUICKACTION_PUB_MARKET:// 搜索
				 if(appContext.isLogin())
					 UIHelper.showPubMarket(MainActivity.this);
				 else
					 UIHelper.showLoginDialog(MainActivity.this);
			 break;
			 case QUICKACTION_PUB_ZIXUN:// 设置
				 if(appContext.isLogin())
					 UIHelper.showPubContent(MainActivity.this,Constant.ContentType.CONTENT_TYPE_ASK,Constant.WzType.WZTYPE_ZRWL);
				 else
					 UIHelper.showLoginDialog(MainActivity.this);
			// case QUICKACTION_EXIT:// 退出
			// UIHelper.Exit(MainActivity.this);
			// break;
			}
		}
	};

	/**
	 * 监听返回--是否退出程序
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		boolean flag = true;
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			// 是否退出应用
			UIHelper.Exit(this);
		} else
		{
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}

}
