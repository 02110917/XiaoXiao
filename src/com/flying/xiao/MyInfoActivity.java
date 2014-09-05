package com.flying.xiao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.util.ImageManager2;
import com.flying.xiao.util.MD5;

/**
 * 我的资料
 * 
 * @author zhangmin
 *
 */
public class MyInfoActivity extends BaseActivity
{

	private ImageView mPhoto;
	private TextView mName;
	private RadioGroup mSexGroup;
	private EditText mInfo; // 个人说明
	private EditText mDetailInfo; // 个人说明

	private RadioButton mRadioBtnMan;
	private RadioButton mRadioBtnWoMen;
	private RelativeLayout mRlInfo;
	private XUserInfo userInfo;
	private ProgressDialog mProgress;
	private static final int MENU_ITEM1 = Menu.FIRST;
	private static final int MENU_ITEM2 = Menu.FIRST + 1;

	public static final int TAKE_PICTURE = 1; // 拍照
	public static final int FROM_ALBUM = 2; // 从相册
	public static final int CROP_PICTURE = 3; // 图像裁剪

	private String md5;
	private String imageUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

	private boolean headImageIsChange = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_info_activity);
		this.initHeadView();
		userInfo = appContext.getUserInfo();
		md5 = MD5.Md5(userInfo.getUserName());
		imageUrl += md5 + ".jpg";
		initView();
	}

	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadRightBtn.setVisibility(View.VISIBLE);
		mHeadRightBtn.setText("保存");
		mHeadTitle.setText("我的资料");
		mHeadRightView.setVisibility(View.GONE);
	}

	private void initView()
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
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(MyInfoActivity.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(MyInfoActivity.this);
					break;
				case Constant.HandlerMessageCode.CHANGE_USER_INFO_FAIL:
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(MyInfoActivity.this, "保存数据出错...");
					break;
				case Constant.HandlerMessageCode.CHANGE_USER_INFO_SUCCESS:
					if (mProgress != null)
						mProgress.dismiss();
					appContext.writeUserInfo(userInfo);
					ImageManager2.from(MyInfoActivity.this).clearCache(
							URLs.HOST + userInfo.getUserHeadImageUrl());
					finish();
					break;
				default:
					break;
				}
			}

		};
		mHeadRightBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (StringUtils.isEmpty(mInfo.getText().toString()))
				{
					Toast.makeText(MyInfoActivity.this, "请填写个人说明...", 1000).show();
					return;
				}
				if (headImageIsChange)
				{
					String headImageUrl = "/XiaoServer/head_image/" + md5 + ".jpg";
					userInfo.setUserHeadImageUrl(headImageUrl);
				} else
				{
					imageUrl = "";
				}
				userInfo.setUserSex(mRadioBtnMan.isChecked());
				userInfo.setUserGerenshuoming(mInfo.getText().toString());
				userInfo.setUserInfoDetail(mDetailInfo.getText().toString());
				mProgress = ProgressDialog.show(v.getContext(), null, "保存中···", true, true);
				NetControl.getShare(MyInfoActivity.this)
						.changeUserInfo(userInfo.toJson(), imageUrl, mHandler);
			}
		});
		mPhoto = (ImageView) findViewById(R.id.photo);
		mName = (TextView) findViewById(R.id.name);
		mSexGroup = (RadioGroup) findViewById(R.id.sexgroup);
		mInfo = (EditText) findViewById(R.id.edit);
		mDetailInfo = (EditText) findViewById(R.id.edit_info);
		mRadioBtnMan = (RadioButton) findViewById(R.id.man);
		mRadioBtnWoMen = (RadioButton) findViewById(R.id.woman);
		mRlInfo = (RelativeLayout) findViewById(R.id.info);
		registerForContextMenu(mRlInfo);
		String url = URLs.HOST + userInfo.getUserHeadImageUrl();
		ImageManager2.from(this).displayImage(mPhoto, url, R.drawable.widget_dface);
		mName.setText(userInfo.getUserRealName());
		if (userInfo.getUserSex() != null)
		{
			if (userInfo.getUserSex())
			{
				mRadioBtnMan.setChecked(true);
				mRadioBtnWoMen.setChecked(false);
			} else
			{
				mRadioBtnMan.setChecked(false);
				mRadioBtnWoMen.setChecked(true);
			}
		} else
		{
			mRadioBtnMan.setChecked(true);
			mRadioBtnWoMen.setChecked(false);
		}
		if (userInfo.getUserGerenshuoming() != null)
		{
			mInfo.getText().append(userInfo.getUserGerenshuoming());
		} else
		{
			mInfo.setHint("请输入您的个人说明");
		}
		if (userInfo.getUserInfoDetail() != null)
		{
			mDetailInfo.setText(userInfo.getUserInfoDetail());
		} else
		{
			mDetailInfo.setHint("请输入您的详细信息,比如部门、商家介绍。");
		}
	}

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
			UIHelper.showPicture(this);
			break;
		case MENU_ITEM2:
			// if (Environment.getExternalStorageState() ==
			// Environment.MEDIA_MOUNTED)
			// {
			UIHelper.showCamera(this, getImageUri());
			// } else
			// {
			// Toast.makeText(this, "没有SD卡...", 1000).show();
			// }

			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK)
		{
			UIHelper.showCropImage(this, getImageUri(), 80, 80, CROP_PICTURE);
		} else if (requestCode == FROM_ALBUM && resultCode == Activity.RESULT_OK)
		{
			Uri uri = data.getData();
			UIHelper.showCropImage(this, uri, 80, 80, CROP_PICTURE);
		} else if (requestCode == CROP_PICTURE && resultCode == Activity.RESULT_OK)
		{
			Bitmap photo = null;
			Bundle extra = data.getExtras();
			if (extra != null)
			{
				photo = (Bitmap) extra.getParcelable("data");
				if (photo != null)
				{
					FileOutputStream baos = null;
					try
					{
						baos = new FileOutputStream(new File(imageUrl));

					} catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					try
					{
						baos.flush();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally
					{
						try
						{
							baos.close();
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					mPhoto.setImageBitmap(photo);
					headImageIsChange = true;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void myInfoClick(View v)
	{
		if (v.getId() == R.id.info)
		{ // 更改头像
			v.performLongClick();
		}
	}

	public Uri getImageUri()
	{
		return Uri.fromFile(new File(imageUrl));
	}

}
