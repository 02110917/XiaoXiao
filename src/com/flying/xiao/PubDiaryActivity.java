package com.flying.xiao;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.flying.xiao.adapter.GridImageAdapter;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;

public class PubDiaryActivity extends BaseActivity
{

	private EditText mEtInput ;
	private GridView gridView;
	private ArrayList<String> dataList ;
	private GridImageAdapter gridImageAdapter;
	 private ProgressDialog mProgress;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pub_diary);
		this.initHeadView();
		initView();
		initListener();
	}
	
	
	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadTitle.setText("������");
		mHeadRightView.setVisibility(View.GONE);
		mHeadRightBtn.setVisibility(View.VISIBLE);
		mHeadRightBtn.setText("����");
		mHeadRightBtn.setOnClickListener(listener);
	}


	private void initView() {
		mEtInput=(EditText) findViewById(R.id.diary_input);
		gridView = (GridView) findViewById(R.id.myGrid);
		dataList = new ArrayList<String>();
		dataList.add("camera_default");
		gridImageAdapter = new GridImageAdapter(this, dataList);
		gridView.setAdapter(gridImageAdapter);
		mHandler=new Handler(){

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(PubDiaryActivity.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(PubDiaryActivity.this);
					break;
				case Constant.HandlerMessageCode.PUB_DIARY_FAIL:
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(PubDiaryActivity.this, "�������ݳ���...");
					break;
				case Constant.HandlerMessageCode.PUB_DIARY_SUCCESS:
					if (mProgress != null)
						mProgress.dismiss();
					finish();
					break ;
				default:
					break;
				}
			}};
	}
	
	private void initListener() {

		gridView.setOnItemClickListener(new GridView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == dataList.size() - 1) {

					Intent intent = new Intent(PubDiaryActivity.this,
							AlbumActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("dataList",
							getIntentArrayList(dataList));
					intent.putExtras(bundle);
//					startActivity(intent);
					startActivityForResult(intent, 0);

				}

			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				ArrayList<String> tDataList = (ArrayList<String>)bundle.getSerializable("dataList");
				if (tDataList != null) {
					tDataList.add("camera_default");
					dataList.clear();
					dataList.addAll(tDataList);
					gridImageAdapter.notifyDataSetChanged();
				}
			}
		}
		
	}

	private ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {

		ArrayList<String> tDataList = new ArrayList<String>();

		for (String s : dataList) {
			if (!s.contains("default")) {
				tDataList.add(s);
			}
		}

		return tDataList;

	}
	
	private OnClickListener listener=new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String _content=mEtInput.getText().toString().trim();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(PubDiaryActivity.this, "������Ϣ����Ϊ��...", Toast.LENGTH_SHORT);
				return ;
			}
			dataList.remove("camera_default");
			mProgress = ProgressDialog.show(v.getContext(), null, "�����С�����",true,true);
			NetControl.getShare(PubDiaryActivity.this).pubDiaryOrLost(_content, dataList, mHandler,Constant.ContentType.CONTENT_TYPE_DIARY);
		}
	};
}