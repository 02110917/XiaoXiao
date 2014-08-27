package com.flying.xiao;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.flying.xiao.adapter.ChildAdapter;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;

public class ShowImageActivity extends BaseActivity {
	private GridView mGridView;
	private List<String> list;
	private List<String> dataSelected ; //已选择的图片
	private ChildAdapter adapter;
	
	private Button mBtnSure ;//确定
	private Button mBtnScan ; //预览
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_image);
		initHeadView();
		mGridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");
		dataSelected=getIntent().getStringArrayListExtra("dataSelected");
		adapter = new ChildAdapter(this, list,dataSelected, mGridView);
		mGridView.setAdapter(adapter);
		mBtnSure=(Button)findViewById(R.id.show_image_sure);
		mBtnScan=(Button)findViewById(R.id.show_image_scan);
		mBtnScan.setOnClickListener(listener);
		mBtnSure.setOnClickListener(listener);
	}

	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadTitle.setText("选择图片");
		mHeadRightView.setVisibility(View.GONE);
	}

	private OnClickListener listener=new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.show_image_sure:
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("dataList",adapter.getSelectItems());
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
				break;

			case R.id.show_image_scan:
				ArrayList<String> pictureUrls = adapter.getSelectItems();
				if (pictureUrls != null && pictureUrls.size() > 0) {
					UIHelper.showFullScreenPicture(ShowImageActivity.this,
							pictureUrls, 0, Constant.PictureType.TYPE_NATIVE);
				} else {
					UIHelper.ToastMessage(ShowImageActivity.this,
							R.string.picture_unchecked);
				}
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public void onBackPressed() {
		Toast.makeText(this, "选中 " + adapter.getSelectItems().size() + " item", Toast.LENGTH_LONG).show();
		super.onBackPressed();
	}
	
	

	
}
