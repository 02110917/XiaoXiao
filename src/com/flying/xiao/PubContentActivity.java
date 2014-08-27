package com.flying.xiao;

import java.io.File;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.flying.xiao.adapter.GridImageAdapter;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.RContent;
import com.flying.xiao.util.MD5;

public class PubContentActivity extends BaseActivity
{

	private EditText mEtTitle;
	private EditText mEtSummary;
	private EditText mEtInfo;

	private GridView gridView;
	private ArrayList<String> dataList;
	private GridImageAdapter gridImageAdapter;
	private ProgressDialog mProgress;
	private Spinner mSpinner;
	private ArrayAdapter<String> adapter;
	private int type;

	private static final int MENU_ITEM1 = Menu.FIRST;
	private static final int MENU_ITEM2 = Menu.FIRST + 1;
	private String imageUrl = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
	String userMd5="";
	// private int wzType;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pub_content);
		userMd5=MD5.Md5(appContext.getUserInfo().getUserName());
		type = getIntent().getIntExtra("type", 0);
		// wzType=getIntent().getIntExtra("wztype", 0);
		this.initHeadView();
		initView();
		initListener();
	}

	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadTitle.setText("发布内容");
		// if(type==Constant.ContentType.CONTENT_TYPE_ASK)
		// mHeadTitle.setText("发布问答");
		// else if(type==Constant.ContentType.CONTENT_TYPE_NEWS){
		//
		// }
		mHeadRightView.setVisibility(View.GONE);
		mHeadRightBtn.setVisibility(View.VISIBLE);
		mHeadRightBtn.setText("发布");
		mHeadRightBtn.setOnClickListener(listener);
	}

	private void initView()
	{
		mEtTitle = (EditText) findViewById(R.id.pub_content_title);
		mEtSummary = (EditText) findViewById(R.id.pub_content_summary);
		mEtInfo = (EditText) findViewById(R.id.pub_content_info);
		gridView = (GridView) findViewById(R.id.pub_content_grid);
		mSpinner = (Spinner) findViewById(R.id.pub_content_type);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
				getResources().getStringArray(R.array.content_type_array));
		mSpinner.setAdapter(adapter);
		dataList = new ArrayList<String>();
		dataList.add("camera_default");
		gridImageAdapter = new GridImageAdapter(this, dataList);
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
					UIHelper.ToastMessage(PubContentActivity.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(PubContentActivity.this);
					break;
				case Constant.HandlerMessageCode.PUB_DIARY_FAIL:
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(PubContentActivity.this, "保存数据出错...");
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
		}else if(requestCode==1){
			if(resultCode==RESULT_OK){
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
			String _title = mEtTitle.getText().toString().trim();
			String _summary = mEtSummary.getText().toString().trim();
			String _info = mEtInfo.getText().toString().trim();
			if (StringUtils.isEmpty(_title) || StringUtils.isEmpty(_info))
			{
				UIHelper.ToastMessage(PubContentActivity.this, "输入信息不能为空...", Toast.LENGTH_SHORT);
				return;
			}
			RContent content = new RContent();
			content.setTitle(_title);
			content.setInfo(_info);
			content.setSummary(_summary);
			content.setType(getWzType((String) mSpinner.getSelectedItem())[0]);
			content.setContentType(getWzType((String) mSpinner.getSelectedItem())[1]);
			dataList.remove("camera_default");
			mProgress = ProgressDialog.show(v.getContext(), null, "保存中···", true, true);
			NetControl.getShare(PubContentActivity.this).pubDiaryOrLost(content.toJson(), dataList, mHandler,
					type);
		}
	};

	/**
	 * 根据spinner的内容获取文章类型
	 * 
	 * @param str
	 * @return
	 */
	private int[] getWzType(String str)
	{
		int[] r = new int[2];
		if (str == null)
		{
			r[0] = 1;
			r[1] = 1;
		}
		if (str.equals("问答"))
		{
			r[0] = Constant.WzType.WZTYPE_ZRWL;
			r[1] = Constant.ContentType.CONTENT_TYPE_ASK;
		} else if (str.equals("招新招聘"))
		{
			r[0] = Constant.WzType.WZTYPE_ZXZP;
			r[1] = Constant.ContentType.CONTENT_TYPE_NEWS;
		} else if (str.equals("公告信息"))
		{
			r[0] = Constant.WzType.WZTYPE_WJTZ;
			r[1] = Constant.ContentType.CONTENT_TYPE_NEWS;
		}
		return r;
	}

	public void setType(int type)
	{
		this.type = type;
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
			Intent intent = new Intent(PubContentActivity.this, AlbumActivity.class);
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
		imageUrl+=(userMd5+System.currentTimeMillis()+ ".jpg");
		return Uri.fromFile(new File(imageUrl));
	}
}
