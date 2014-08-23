package com.flying.xiao;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.flying.xiao.app.AppContext;
import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XMessage;

/**
 * ʧ����г����Ը�������
 */
public class PubLostAndMarketComment extends BaseActivity{

	private EditText mContent;
    private ProgressDialog mProgress;
	private long contentId;
	
	String _content;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_pub);
		this.initHeadView();
		this.initView();
		
	}
	
    @Override
	protected void initHeadView()
	{
		super.initHeadView();
		mHeadRightBtn.setVisibility(View.VISIBLE);
    	mHeadRightBtn.setText("����");
    	mHeadTitle.setText("����");
    	mHeadProgressBar.setVisibility(View.GONE);
    	mHeadRightView.setVisibility(View.GONE);
	}

	//��ʼ����ͼ�ؼ�
    private void initView()
    {
    	contentId = getIntent().getLongExtra("contentId", 0);
    	
    	mContent = (EditText)findViewById(R.id.message_pub_content);
    	mHeadRightBtn.setOnClickListener(publishClickListener);    	
    	
    	mHandler=new Handler(){

			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch (msg.what)
				{
				case Constant.HandlerMessageCode.PUB_COMMENT_ERROR: // ��������ʧ��
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(PubLostAndMarketComment.this, R.string.pub_comment_error);
					break;
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(PubLostAndMarketComment.this, R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(PubLostAndMarketComment.this);
					break;
				case Constant.HandlerMessageCode.PUB_COMMENT_SUCCESS:// //�������۳ɹ�
					if (mProgress != null)
						mProgress.dismiss();
					// �ָ���ʼ�ײ���
//					XMessage com = (XMessage) msg.obj;
					// ���֮ǰ����ı༭����
					mContent.setText("");
//					Intent intent = new Intent();
//					intent.putExtra("COMMENT_SERIALIZABLE", com);
//					setResult(RESULT_OK, intent);
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
				UIHelper.ToastMessage(v.getContext(), "��������������");
				return;
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLoginDialog(PubLostAndMarketComment.this);
				return;
			}						
	    	mProgress = ProgressDialog.show(v.getContext(), null, "�����С�����",true,true); 			
	    	NetControl.getShare(PubLostAndMarketComment.this).pubComment(appContext.getUserInfo().getId(), contentId,
					_content, 0,mHandler);
		}
	};
}