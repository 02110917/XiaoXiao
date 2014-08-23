package com.flying.xiao;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flying.xiao.app.AppContext;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XComment;

/**
 * 发表评论
 */
public class CommentPub extends BaseActivity{

	private EditText mContent;
	private TextView mQuote;
    private ProgressDialog mProgress;
	
	long commentid;
	long contentid;
	long authorid;
	String author;
	String content;
	
	
	String _content;
	
	//-------对评论回复还需加2变量------
	private int _replyid;//被回复的单个评论id
	private int _authorid;//该评论的原始作者id
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_pub);
		this.initHeadView();
		this.initView();
		
	}
	
    @Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadTitle.setText("评论回复");
		mHeadRightView.setVisibility(View.GONE);
		mHeadRightBtn.setVisibility(View.VISIBLE);
	}

	//初始化视图控件
    private void initView()
    {
    	commentid = getIntent().getLongExtra("comment_id", 0);
    	contentid = getIntent().getLongExtra("content_id", 0);
    	authorid = getIntent().getLongExtra("user_id", 0);
    	author = getIntent().getStringExtra("user_name");
    	content = getIntent().getStringExtra("comment_info");
    	
    	mContent = (EditText)findViewById(R.id.comment_pub_content);
    	mHeadRightBtn.setOnClickListener(publishClickListener);    	
    	
    	mQuote = (TextView)findViewById(R.id.comment_pub_quote);
    	mQuote.setText(UIHelper.parseQuoteSpan(author,content));
//    	mQuote.parseLinkText();
    	mHandler=new Handler(){

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.PUB_COMMENT_ERROR: // 发布评论失败
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(CommentPub.this, R.string.pub_comment_error);
					break;
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(CommentPub.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(CommentPub.this);
					break;
				case Constant.HandlerMessageCode.PUB_COMMENT_SUCCESS:// //发表评论成功
					if (mProgress != null)
						mProgress.dismiss();
					// 恢复初始底部栏
					XComment com = (XComment) msg.obj;
					// 清除之前保存的编辑内容
					mContent.setText("");
					Intent intent = new Intent();
					intent.putExtra("COMMENT_SERIALIZABLE", com);
					setResult(RESULT_OK, intent);
					finish();
					break;

				default:
					break;
				}
			}};
    }
	
	private View.OnClickListener publishClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			_content = mContent.getText().toString();
			if(StringUtils.isEmpty(_content)){
				UIHelper.ToastMessage(v.getContext(), "请输入评论内容");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(CommentPub.this);
				return;
			}						
	    	mProgress = ProgressDialog.show(v.getContext(), null, "发表中···",true,true); 			
	    	NetControl.getShare(CommentPub.this).pubComment(authorid, contentid,
	    			_content,commentid,mHandler);
		}
	};
}
