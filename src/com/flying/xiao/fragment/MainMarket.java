package com.flying.xiao.fragment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.flying.xiao.R;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XGoodType;
import com.flying.xiao.widget.DrawableCenterButton;

public class MainMarket extends Fragment
{

	private DrawableCenterButton mHeadBtnType;
	private DrawableCenterButton mHeadBtnPrice;
	private DrawableCenterButton mHeadBtnSale;
	private List<DrawableCenterButton> btnList=new ArrayList<DrawableCenterButton>();
	
	private PopupWindow mPopWindowType ;
	private ListView mTypeListView ;
	private List<XGoodType>mTypeList=new ArrayList<XGoodType>();
	private List<String>mTypeStrList=new ArrayList<String>();
	private ArrayAdapter<String> mTypeadapter;
	private PopupWindow mPopWindowPrice ;
	private ListView mPriceListView ;
	private List<String>mPriceList=new ArrayList<String>();
	private Handler handler ;
	
	private  int width;
	private  int heigth;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		width=getActivity().getWindowManager().getDefaultDisplay().getWidth();
		heigth=getActivity().getWindowManager().getDefaultDisplay().getHeight();
		View view = inflater.inflate(R.layout.main_fragment_market, null);
		initView(view);
		initData();
		return view;
	}

	private void initView(View v)
	{
		mHeadBtnType = (DrawableCenterButton) v.findViewById(R.id.market_head_type);
		mHeadBtnPrice = (DrawableCenterButton) v.findViewById(R.id.market_head_price);
		mHeadBtnSale = (DrawableCenterButton) v.findViewById(R.id.market_head_sale);
		btnList.add(mHeadBtnType);
		btnList.add(mHeadBtnPrice);
		btnList.add(mHeadBtnSale);
		for(Button btn:btnList){
			btn.setOnClickListener(btnListener);
		}
	}
	private void initData(){
		 handler=new Handler(){

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.GET_MARKET_TYPE_ERROR:
					
					break;
				case Constant.HandlerMessageCode.GET_MARKET_TYPE_SUCCESS:
					mTypeList.clear();
					List<XGoodType> list=(List<XGoodType>)msg.obj;
					mTypeList.addAll(list);
					for(XGoodType type:list){
						mTypeStrList.add(type.getEsGoodsTypeName());
					}
					if(mTypeadapter!=null)
						mTypeadapter.notifyDataSetChanged();
					break;

				default:
					break;
				}
			}};
		NetControl.getShare(getActivity()).getMarketTypes(handler);
	}
	private void changeBtnState(DrawableCenterButton btn)
	{
		if (!btn.isSelect())
		{
			btn.setSelect(true);
			btn.setBackgroundResource(R.drawable.sift_btn_bg_pressed);
			btn.setTextColor(0xffff0000);
			Drawable drawable=getActivity().getResources().getDrawable(R.drawable.btn_icon_pressed);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
			btn.setCompoundDrawables(null, null,
					drawable, null);
		} else
		{
			setBtnNormal(btn);
		}
		for(DrawableCenterButton button:btnList){
			if(button!=btn){
				setBtnNormal(button);
			}
		}
		
	}
	private void setBtnNormal(DrawableCenterButton btn){
		btn.setSelect(false);
		btn.setBackgroundResource(R.drawable.sift_btn_bg);
		btn.setTextColor(0xff033a5c);
		Drawable drawable=getActivity().getResources().getDrawable(R.drawable.btn_icon_normal);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); 
		btn.setCompoundDrawables(null, null,
				drawable, null);
	}
	private void getPopWindowInstance(View v){
		if(mPopWindowType!=null&&mPopWindowType.isShowing()){
			mPopWindowType.dismiss();
			mPopWindowType=null;
			return ;
		}else if(mPopWindowType!=null&&(!mPopWindowType.isShowing())){
			mPopWindowType.showAsDropDown(v);
		}else{
			initPopWindow(v);
		}
	}
	private void initPopWindow(View v){
		LayoutInflater inflater=LayoutInflater.from(getActivity());
		View popWindow=inflater.inflate(R.layout.market_btn_type_popwindow, null);
		mTypeListView=(ListView)popWindow.findViewById(R.id.market_type_listview);
		mTypeadapter=new ArrayAdapter<String>(getActivity(),R.layout.market_header_type_list_item,R.id.market_header_type_list_item_type,mTypeStrList);
		mTypeListView.setAdapter(mTypeadapter);
		mPopWindowType=new PopupWindow(popWindow, width/3, heigth);
		mPopWindowType.showAsDropDown(v);
	}
	private OnClickListener btnListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			if(v instanceof DrawableCenterButton)
				changeBtnState((DrawableCenterButton)v);
			switch (v.getId())
			{
			case R.id.market_head_type:
				getPopWindowInstance(v);
				break;
			case R.id.market_head_price:

				break;
			case R.id.market_head_sale:

				break;

			default:
				break;
			}
		}
	};
}
