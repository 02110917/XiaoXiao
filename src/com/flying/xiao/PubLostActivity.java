package com.flying.xiao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.flying.xiao.adapter.GridImageAdapter;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.RLost;
import com.flying.xiao.util.MD5;

public class PubLostActivity extends BaseActivity
{

	private RadioButton mRadioBtnType;
	private EditText mEtName;
	private EditText mEtPlace;
	private EditText mEtPhone;
	private EditText mEtInfo;
	private EditText mEtTime;

	private GridView gridView;
	private ArrayList<String> dataList;
	private GridImageAdapter gridImageAdapter;
	private ProgressDialog mProgress;

	private static final int MENU_ITEM1 = Menu.FIRST;
	private static final int MENU_ITEM2 = Menu.FIRST + 1;
	private String imageUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	String userMd5 = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pub_lost);
		userMd5 = MD5.Md5(appContext.getUserInfo().getUserName());
		this.initHeadView();
		initView();
		initListener();
	}

	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadTitle.setText("发布失物");
		mHeadRightView.setVisibility(View.GONE);
		mHeadRightBtn.setVisibility(View.VISIBLE);
		mHeadRightBtn.setText("发布");
		mHeadRightBtn.setOnClickListener(listener);
	}

	private void initView()
	{
		mRadioBtnType = (RadioButton) findViewById(R.id.pub_lost_radio_btn_lost);
		mEtName = (EditText) findViewById(R.id.pub_lost_name);
		mEtPlace = (EditText) findViewById(R.id.pub_lost_place);
		mEtPhone = (EditText) findViewById(R.id.pub_lost_phone);
		mEtPhone.setText(appContext.getUserInfo().getUserPhone());
		mEtInfo = (EditText) findViewById(R.id.pub_lost_info);
		mEtTime = (EditText) findViewById(R.id.pub_lost_time);
		mEtTime.setText(StringUtils.dateToString(new Date(System.currentTimeMillis())));
		gridView = (GridView) findViewById(R.id.pub_lost_grid);
		dataList = new ArrayList<String>();
		dataList.add("camera_default");
		gridImageAdapter = new GridImageAdapter(this, dataList, gridView);
		gridView.setAdapter(gridImageAdapter);
		registerForContextMenu(gridView);
		mHandler = new Handler()
		{

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(PubLostActivity.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(PubLostActivity.this);
					break;
				case Constant.HandlerMessageCode.PUB_DIARY_FAIL:
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(PubLostActivity.this, "保存数据出错...");
					break;
				case Constant.HandlerMessageCode.PUB_DIARY_SUCCESS:
					if (mProgress != null)
						mProgress.dismiss();
					finish();
					break;
				default:
					break;
				}
			}
		};
	}

	private void initListener()
	{

		gridView.setOnItemClickListener(new GridView.OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				if (position == dataList.size() - 1)
				{
					view.performLongClick();
				}

			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		if (requestCode == 0)
		{
			if (resultCode == RESULT_OK)
			{
				Bundle bundle = data.getExtras();
				ArrayList<String> tDataList = (ArrayList<String>) bundle.getSerializable("dataList");
				if (tDataList != null)
				{
					tDataList.add("camera_default");
					dataList.clear();
					dataList.addAll(tDataList);
					gridImageAdapter.notifyDataSetChanged();
				}
			}
		} else if (requestCode == 1)
		{
			if (resultCode == RESULT_OK)
			{
				dataList.add(0, imageUrl);
				gridImageAdapter.notifyDataSetChanged();
			}
		}

	}

	private ArrayList<String> getIntentArrayList(ArrayList<String> dataList)
	{

		ArrayList<String> tDataList = new ArrayList<String>();

		for (String s : dataList)
		{
			if (!s.contains("default"))
			{
				tDataList.add(s);
			}
		}

		return tDataList;

	}

	private OnClickListener listener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String _name = mEtName.getText().toString().trim();
			boolean _isLost = mRadioBtnType.isChecked();
			String _place = mEtPlace.getText().toString().trim();
			String _time = mEtTime.getText().toString().trim();
			String _phone = mEtPhone.getText().toString().trim();
			String _info = mEtInfo.getText().toString().trim();
			if (StringUtils.isEmpty(_name) || StringUtils.isEmpty(_place) || StringUtils.isEmpty(_time)
					|| StringUtils.isEmpty(_phone) || StringUtils.isEmpty(_info))
			{
				UIHelper.ToastMessage(PubLostActivity.this, "输入信息不能为空...", Toast.LENGTH_SHORT);
				return;
			} else if (dataList.size() <= 1)
			{
				UIHelper.ToastMessage(PubLostActivity.this, "请至少选择一张图片上传...", Toast.LENGTH_SHORT);
				return;
			}
			RLost rLost = new RLost();
			rLost.setName(_name);
			rLost.setPlace(_place);
			rLost.setPhone(_phone);
			rLost.setTime(_time);
			rLost.setInfo(_info);
			rLost.setLost(_isLost);
			dataList.remove("camera_default");
			mProgress = ProgressDialog.show(v.getContext(), null, "保存中···", true, true);
			NetControl.getShare(PubLostActivity.this).pubDiaryOrLost(rLost.toJson(), dataList, mHandler,
					Constant.ContentType.CONTENT_TYPE_LOST);
		}
	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		menu.setHeaderTitle("操作");
		menu.add(0, MENU_ITEM1, 0, "相册");
		menu.add(0, MENU_ITEM2, 0, "拍照");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case MENU_ITEM1:
			Intent intent = new Intent(PubLostActivity.this, AlbumActivity.class);
			Bundle bundle = new Bundle();
			bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
			intent.putExtras(bundle);
			// startActivity(intent);
			startActivityForResult(intent, 0);
			break;
		case MENU_ITEM2:
			UIHelper.showCamera(this, getImageUri());
			break;
		default:
			break;
		}
		return true;
	}

	public Uri getImageUri()
	{
		imageUrl += (userMd5 + System.currentTimeMillis() + ".jpg");
		return Uri.fromFile(new File(imageUrl));
	}
}
