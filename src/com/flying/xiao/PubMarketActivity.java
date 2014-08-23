package com.flying.xiao;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flying.xiao.adapter.GridImageAdapter;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.RLost;
import com.flying.xiao.entity.RMarket;
import com.flying.xiao.entity.XGoodType;
import com.flying.xiao.entity.XMarketTypeSecond;

public class PubMarketActivity extends BaseActivity
{

	private Spinner mSpType1;
	private Spinner mSpType2;
	private Spinner mSpChengse; // 成色

	private EditText mEtMarketName;
	private EditText mEtMarketPrice;
	private EditText mEtMarketPriceNew;
	private EditText mEtUserName;
	private EditText mEtUserPhone;
	private EditText mEtMarketInfo;
	private GridView gridView;
	private ArrayList<String> dataList;
	private GridImageAdapter gridImageAdapter;
	private ProgressDialog mProgress;

	private ArrayAdapter<String> chengseAdapter;
	private ArrayAdapter<String> marketType1Adapter;
	private MySpinnerAdapter marketType2Adapter;
	// private SimpleAdapter marketType2Adapter;

	private List<XGoodType> mTypeList;
	private List<String> mTypeStrList = new ArrayList<String>();
	private List<XMarketTypeSecond> mTypeSecondList = new ArrayList<XMarketTypeSecond>();

	// private List<String> mTypeSecondStrList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pub_market);
		this.initHeadView();
		initView();
		initListener();
	}

	@Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadTitle.setText("发布商品");
		mHeadRightView.setVisibility(View.GONE);
		mHeadRightBtn.setVisibility(View.VISIBLE);
		mHeadRightBtn.setText("发布");
		mHeadRightBtn.setOnClickListener(listener);
	}

	private void initView()
	{
		mEtMarketName = (EditText) findViewById(R.id.pub_market_name);
		mEtMarketPrice = (EditText) findViewById(R.id.pub_market_price);
		mEtMarketPriceNew = (EditText) findViewById(R.id.pub_market_price_new);
		mEtUserName = (EditText) findViewById(R.id.pub_market_user_name);
		mEtUserName.setText(appContext.getUserInfo().getUserRealName());
		mEtUserPhone = (EditText) findViewById(R.id.pub_market_user_phone);
		mEtUserPhone.setText(appContext.getUserInfo().getUserPhone());
		mEtMarketInfo = (EditText) findViewById(R.id.pub_market_info);
		gridView = (GridView) findViewById(R.id.pub_market_grid);

		mSpType1 = (Spinner) findViewById(R.id.pub_market_type1);
		mSpType2 = (Spinner) findViewById(R.id.pub_market_type2);
		mSpChengse = (Spinner) findViewById(R.id.pub_market_chengse);

		chengseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
				getResources().getStringArray(R.array.cengse_array));
		mSpChengse.setAdapter(chengseAdapter);

		mTypeList = appContext.listManager.getMarketTypeList();
		marketType1Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
				getType1StrList(mTypeList));
		mSpType1.setAdapter(marketType1Adapter);
		mSpType1.setOnItemSelectedListener(new OnItemSelectedListener()
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				mTypeSecondList = mTypeList.get(position).getTypeSecondList();
				marketType2Adapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});
		if (mTypeList != null && mTypeList.size() > 0)
			mTypeSecondList = mTypeList.get(0).getTypeSecondList();
		marketType2Adapter = new MySpinnerAdapter();
		mSpType2.setAdapter(marketType2Adapter);
		dataList = new ArrayList<String>();
		dataList.add("camera_default");
		gridImageAdapter = new GridImageAdapter(this, dataList);
		gridView.setAdapter(gridImageAdapter);
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
					UIHelper.ToastMessage(PubMarketActivity.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(PubMarketActivity.this);
					break;
				case Constant.HandlerMessageCode.PUB_DIARY_FAIL:
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(PubMarketActivity.this, "保存数据出错...");
					break;
				case Constant.HandlerMessageCode.PUB_DIARY_SUCCESS:
					if (mProgress != null)
						mProgress.dismiss();
					finish();
					break;

				case Constant.HandlerMessageCode.GET_MARKET_TYPE_ERROR:

					break;
				case Constant.HandlerMessageCode.GET_MARKET_TYPE_SUCCESS:
					mTypeList.clear();
					List<XGoodType> list = (List<XGoodType>) msg.obj;
					mTypeList.addAll(list);
					mTypeStrList = getType1StrList(list);
					if (marketType1Adapter != null)
						marketType1Adapter.notifyDataSetChanged();
					if (marketType2Adapter != null)
						marketType2Adapter.notifyDataSetChanged();
					break;
				default:
					break;
				}
			}
		};
		NetControl.getShare(this).getMarketTypes(mHandler);
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

					Intent intent = new Intent(PubMarketActivity.this, AlbumActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
					intent.putExtras(bundle);
					// startActivity(intent);
					startActivityForResult(intent, 0);

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
			String _name = mEtMarketName.getText().toString().trim();
			String _price = mEtMarketPrice.getText().toString().trim();
			String _price_new = mEtMarketPriceNew.getText().toString().trim();
			String _user_name = mEtUserName.getText().toString().trim();
			String _user_phone = mEtUserPhone.getText().toString().trim();
			String _info = mEtMarketInfo.getText().toString().trim();
			String _chengse = (String) mSpChengse.getSelectedItem();
			if(mTypeSecondList==null||mTypeSecondList.size()==0){
				UIHelper.ToastMessage(PubMarketActivity.this, "商品分类不能为空...", Toast.LENGTH_SHORT);
				return;
			}
			int type2 = mTypeSecondList.get(mSpType2.getSelectedItemPosition()).getId();
			System.out.println("type2:" + type2);

			if (StringUtils.isEmpty(_name) || StringUtils.isEmpty(_price) || StringUtils.isEmpty(_price_new)
					|| StringUtils.isEmpty(_user_name) || StringUtils.isEmpty(_user_phone)
					|| StringUtils.isEmpty(_info) || StringUtils.isEmpty(_chengse))
			{
				UIHelper.ToastMessage(PubMarketActivity.this, "输入信息不能为空...", Toast.LENGTH_SHORT);
				return;
			}
			RMarket m=new RMarket();
			m.setName(_name);
			m.setTypeId(type2);
			m.setPrice(_price);
			m.setPriceNew(_price_new);
			m.setChengSe(_chengse);
			m.setUserName(_user_name);
			m.setUserPhone(_user_phone);
			m.setInfo(_info);
			dataList.remove("camera_default");
			mProgress = ProgressDialog.show(v.getContext(), null, "保存中···", true, true);
			NetControl.getShare(PubMarketActivity.this).pubDiaryOrLost(m.toJson(), dataList, mHandler,
					Constant.ContentType.CONTENT_TYPE_MARKET);
		}
	};

	private List<String> getType1StrList(List<XGoodType> type1List)
	{
		mTypeStrList.clear();
		int size = 0;
		if (type1List != null && (size = type1List.size()) > 0)
		{

			for (int i = 0; i < size; i++)
			{
				XGoodType type = type1List.get(i);
				mTypeStrList.add(type.getEsGoodsTypeName());
				if (i == 0)
				{
					mTypeSecondList = type.getTypeSecondList();
				}

			}
		}
		return mTypeStrList;
	}

	public class MySpinnerAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return mTypeSecondList.size();
		}

		@Override
		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return mTypeSecondList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View v = LayoutInflater.from(PubMarketActivity.this).inflate(
					android.R.layout.simple_dropdown_item_1line, parent, false);
			TextView tv = (TextView) v.findViewById(android.R.id.text1);
			tv.setText(mTypeSecondList.get(position).getTypeName());
			// TODO Auto-generated method stub
			return v;
		}

	}
}
