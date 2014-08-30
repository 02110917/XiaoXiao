package com.flying.xiao;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.Intents.UI;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flying.xiao.adapter.ListViewCommunityAdapter;
import com.flying.xiao.adapter.ListViewMainContentAdapter;
import com.flying.xiao.app.AppException;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XContent;
import com.flying.xiao.entity.XUserInfo;

/**
 * 搜索
 */
public class SearchActivity extends BaseActivity{
	private Button mSearchBtn;
	private EditText mSearchEditer;
	private ProgressBar mProgressbar;
	
	private Button search_catalog_lost;
	private Button search_catalog_market;
	private Button search_catalog_user;
	private Button search_catalog_ask;
	private Button search_catalog_news;
	
	private ListView mlvSearch;
	private List lvSearchContentData = new ArrayList();
	private List lvSearchUserData = new ArrayList();
	private View lvSearch_footer;
	private TextView lvSearch_foot_more;
	private ProgressBar lvSearch_foot_progress;
    private int lvSumData;
    private ListViewMainContentAdapter mContentAdapter;
    private ListViewMainContentAdapter mMarketAdapter;
    private ListViewCommunityAdapter mUserAdapter ;
	private int curLvDataState;
	private String curSearchContent = "";
    
	private InputMethodManager imm;
	private int type=1;
	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        this.initView();
        
        this.initData();
	}
	
    /**
     * 头部按钮展示
     * @param type
     */
    private void headButtonSwitch(int type) {
    	switch (type) {
    	case DATA_LOAD_ING:
    		mSearchBtn.setClickable(false);
			mProgressbar.setVisibility(View.VISIBLE);
			break;
		case DATA_LOAD_COMPLETE:
			mSearchBtn.setClickable(true);
			mProgressbar.setVisibility(View.GONE);
			break;
		}
    }
	
	//初始化视图控件
    private void initView()
    {
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    	
    	mSearchBtn = (Button)findViewById(R.id.search_btn);
    	mSearchEditer = (EditText)findViewById(R.id.search_editer);
    	mProgressbar = (ProgressBar)findViewById(R.id.search_progress);
    	
    	mSearchBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				mSearchEditer.clearFocus();
				curSearchContent = mSearchEditer.getText().toString();
				loadLvSearchData(type,0,mHandler, UIHelper.LISTVIEW_ACTION_INIT);
//				loadLvSearchData(curSearchCatalog, 0, mSearchHandler, UIHelper.LISTVIEW_ACTION_INIT);
			}
		});
    	mSearchEditer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){  
					imm.showSoftInput(v, 0);  
		        }  
		        else{  
		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
		        }  
			}
		}); 
    	mSearchEditer.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SEARCH) {
					if(v.getTag() == null) {
						v.setTag(1);
						mSearchEditer.clearFocus();
						curSearchContent = mSearchEditer.getText().toString();
						loadLvSearchData(type,0,mHandler, UIHelper.LISTVIEW_ACTION_INIT);
//						loadLvSearchData(curSearchCatalog, 0, mSearchHandler, UIHelper.LISTVIEW_ACTION_INIT);						
					}else{
						v.setTag(null);
					}
					return true;
				}
				return false;
			}
		});
    	
    	search_catalog_lost = (Button)findViewById(R.id.search_catalog_lost);
    	search_catalog_market = (Button)findViewById(R.id.search_catalog_market);
    	search_catalog_user = (Button)findViewById(R.id.search_catalog_user);
    	search_catalog_ask = (Button)findViewById(R.id.search_catalog_ask);
    	search_catalog_news = (Button)findViewById(R.id.search_catalog_news);
    	
    	search_catalog_lost.setOnClickListener(this.searchBtnClick(search_catalog_lost,Constant.ContentType.CONTENT_TYPE_LOST));
    	search_catalog_market.setOnClickListener(this.searchBtnClick(search_catalog_market,Constant.ContentType.CONTENT_TYPE_MARKET));
    	search_catalog_user.setOnClickListener(this.searchBtnClick(search_catalog_user,-1));
    	search_catalog_ask.setOnClickListener(this.searchBtnClick(search_catalog_ask,Constant.ContentType.CONTENT_TYPE_ASK));
    	search_catalog_news.setOnClickListener(this.searchBtnClick(search_catalog_news,Constant.ContentType.CONTENT_TYPE_NEWS));
    	
    	search_catalog_news.setEnabled(false);
    	
    	lvSearch_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
    	lvSearch_foot_more = (TextView)lvSearch_footer.findViewById(R.id.listview_foot_more);
    	lvSearch_foot_progress = (ProgressBar)lvSearch_footer.findViewById(R.id.listview_foot_progress);
    	mContentAdapter=new ListViewMainContentAdapter(this, lvSearchContentData, R.layout.main_fragment_news_listitem);
    	mMarketAdapter=new ListViewMainContentAdapter(this, lvSearchContentData, R.layout.main_fragment_market_listitem,true);
    	mUserAdapter=new ListViewCommunityAdapter(this, lvSearchUserData, R.layout.community_fragment_listitem);
    	mlvSearch = (ListView)findViewById(R.id.search_listview);
    	mlvSearch.setVisibility(ListView.GONE);
    	mlvSearch.addFooterView(lvSearch_footer);//添加底部视图  必须在setAdapter前
    	mlvSearch.setAdapter(mContentAdapter); 
    	mlvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//点击底部栏无效
        		if(view == lvSearch_footer) return;
        		
        		if(type==-1){
        			UIHelper.showUserInfo(SearchActivity.this, (XUserInfo)lvSearchUserData.get(position));
        		}else{
        			UIHelper.showContentInfo(SearchActivity.this, (XContent)lvSearchContentData.get(position), type);
        		}
        	}
		});
    	mlvSearch.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {				
				//数据为空--不用继续下面代码了
				if(lvSearchContentData.size() == 0) return;
				
				//判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvSearch_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				if(scrollEnd && curLvDataState==1)
				{
					mlvSearch.setTag(1);
					lvSearch_foot_more.setText(R.string.load_ing);
					lvSearch_foot_progress.setVisibility(View.VISIBLE);
					//当前pageIndex
					int pageIndex = lvSumData/20;
					loadLvSearchData(type,pageIndex,mHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
//					loadLvSearchData(curSearchCatalog, pageIndex, mSearchHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
			}
		});
    }
    
    //初始化控件数据
  	private void initData()
  	{			
		mHandler = new Handler()
		{
			public void handleMessage(Message msg) {
				
				headButtonSwitch(DATA_LOAD_COMPLETE);

				if(msg.what >= 0){						
					List list = (List) msg.obj;
					//处理listview数据
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
					case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
						lvSumData = msg.what;
						if(msg.arg2==-1)
						{
							lvSearchUserData.clear();
							lvSearchUserData.addAll(list);
						}else
						{
							lvSearchContentData.clear();//先清除原有数据
							lvSearchContentData.addAll(list);
						}
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						lvSumData += msg.what;
						if(msg.arg2==-1){
							lvSearchUserData.addAll(list);
						}else{
							lvSearchContentData.addAll(list);
						}
						
						break;
					}	
					
					if(msg.what < 20){
						curLvDataState = UIHelper.LISTVIEW_DATA_FULL;
						notifyDataSetChanged(msg.arg2);
						lvSearch_foot_more.setText(R.string.load_full);
					}else if(msg.what == 20){					
						curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
						notifyDataSetChanged(msg.arg2);
						lvSearch_foot_more.setText(R.string.load_more);
					}
				}
				else if(msg.what == -1){
					//有异常--显示加载出错 & 弹出错误消息
					curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
					lvSearch_foot_more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(SearchActivity.this);
				}
				if(lvSearchContentData.size()==0){
					curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
					lvSearch_foot_more.setText(R.string.load_empty);
				}
				lvSearch_foot_progress.setVisibility(View.GONE);
				if(msg.arg1 != UIHelper.LISTVIEW_ACTION_SCROLL){
					mlvSearch.setSelection(0);//返回头部
				}
			}
		};
  	}
  	
  	private void notifyDataSetChanged(int type){
  		if(type==-1){
  			mUserAdapter.notifyDataSetChanged();
  		}else if(type==Constant.ContentType.CONTENT_TYPE_MARKET){
  			mContentAdapter.notifyDataSetChanged();
  		}else{
  			mMarketAdapter.notifyDataSetChanged();
  		}
  	}
  	
    /**
     * 查找
     * @param type -1用户  
     * @param pageIndex 当前页数
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvSearchData(final int type,final int pageIndex,final Handler handler,int action){  
		if(StringUtils.isEmpty(curSearchContent)){
			UIHelper.ToastMessage(SearchActivity.this, "请输入搜索内容");
			return;
		}
		
		headButtonSwitch(DATA_LOAD_ING);
		mlvSearch.setVisibility(ListView.VISIBLE);
		NetControl.getShare(this).loadLvSearchData(curSearchContent,type, pageIndex, handler, action);
	} 
	
	private View.OnClickListener searchBtnClick(final Button btn,final int type){
    	return new View.OnClickListener() {
			public void onClick(View v) {
		    	if(btn == search_catalog_ask)
		    		search_catalog_ask.setEnabled(false);
		    	else
		    		search_catalog_ask.setEnabled(true);
		    	if(btn == search_catalog_user)
		    		search_catalog_user.setEnabled(false);
		    	else
		    		search_catalog_user.setEnabled(true);	
		    	if(btn == search_catalog_news)
		    		search_catalog_news.setEnabled(false);
		    	else
		    		search_catalog_news.setEnabled(true);
		    	if(btn == search_catalog_market)
		    		search_catalog_market.setEnabled(false);
		    	else
		    		search_catalog_market.setEnabled(true);
		    	if(btn == search_catalog_lost)
		    		search_catalog_lost.setEnabled(false);
		    	else
		    		search_catalog_lost.setEnabled(true);
				
				//开始搜索
				mSearchEditer.clearFocus();
				curSearchContent = mSearchEditer.getText().toString();
				SearchActivity.this.type=type;
				lvSearchUserData.clear();
				lvSearchContentData.clear();
				if(type==Constant.ContentType.CONTENT_TYPE_MARKET){
					mlvSearch.setAdapter(mMarketAdapter);
				}else if(type==-1){
					mlvSearch.setAdapter(mUserAdapter);
				}else{
					mlvSearch.setAdapter(mContentAdapter);
				}
				loadLvSearchData(type,0,mHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
//				loadLvSearchData(catalog, 0, mSearchHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);		    	
			}
		};
    }
}
