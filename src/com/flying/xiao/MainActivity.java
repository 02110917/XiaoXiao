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
	private Fragment communityFragment; // ����
	private Fragment meFragment;
	private RadioGroup mRadioGroup;
	private RadioButton mRBtnMain ;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	private ImageView fbSetting;
	private ImageView mainHeadLogo;
	private TextView mainHeadTitle;
	private ImageButton mRightBtn;
	private QuickActionWidget mGrid;// ������ؼ�
	
	private AppContext appContext;// ȫ��Context
	private String[] titles = new String[]
	{ "��ҳ", "�㳡", "����", "����" };
	private int[]headImages=new int[]{R.drawable.main,R.drawable.square,R.drawable.community,R.drawable.personal};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		appContext = (AppContext) getApplication();
		// ���������ж�
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
	 * ��ʼ���ײ���
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
				// չʾ�����&�ж��Ƿ��¼&�Ƿ��������ͼƬ
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
	 * ��ʼ�������
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
	 * �����item����¼�
	 */
	private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener()
	{
		@Override
		public void onQuickActionClicked(QuickActionWidget widget, int position)
		{
			switch (position)
			{
//			case QUICKACTION_LOGIN_OR_LOGOUT:// �û���¼-ע��
//				UIHelper.loginOrLogout(MainActivity.this);
//				if(fragments.get(3).isAdded())
//					fragments.get(3).onResume();
//				break;
			 case QUICKACTION_PUB_DIARY:// �ҵ�����
				 if(appContext.isLogin())
					 UIHelper.showPubDiary(MainActivity.this);
				 else
					 UIHelper.showLoginDialog(MainActivity.this);
			 break;
			 case QUICKACTION_PUB_LOST:// ��Դ���
				 if(appContext.isLogin())
					 UIHelper.showPubLost(MainActivity.this);
				 else
					 UIHelper.showLoginDialog(MainActivity.this);
			 break;
			 case QUICKACTION_PUB_MARKET:// ����
				 if(appContext.isLogin())
					 UIHelper.showPubMarket(MainActivity.this);
				 else
					 UIHelper.showLoginDialog(MainActivity.this);
			 break;
			 case QUICKACTION_PUB_ZIXUN:// ����
				 if(appContext.isLogin())
					 UIHelper.showPubContent(MainActivity.this,Constant.ContentType.CONTENT_TYPE_ASK,Constant.WzType.WZTYPE_ZRWL);
				 else
					 UIHelper.showLoginDialog(MainActivity.this);
			// case QUICKACTION_EXIT:// �˳�
			// UIHelper.Exit(MainActivity.this);
			// break;
			}
		}
	};

	/**
	 * ��������--�Ƿ��˳�����
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		boolean flag = true;
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			// �Ƿ��˳�Ӧ��
			UIHelper.Exit(this);
		} else
		{
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}

}
