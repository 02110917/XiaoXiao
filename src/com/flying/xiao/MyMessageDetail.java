package com.flying.xiao;

import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flying.xiao.common.StringUtils;
import com.flying.xiao.common.UIHelper;
import com.flying.xiao.common.URLs;
import com.flying.xiao.constant.Constant;
import com.flying.xiao.control.NetControl;
import com.flying.xiao.entity.XMessage;
import com.flying.xiao.entity.XUserInfo;
import com.flying.xiao.util.ImageManager2;

public class MyMessageDetail extends BaseActivity {

	private XMessage message;

	public Button btnPubComment;
	public EditText etEditComment;
	private ProgressDialog mProgress;
	private LinearLayout mFooterLayout;
	private LinearLayout mOutsiteLayout;

	private ImageView face;
	private TextView name;
	private TextView date;
	private TextView content;
	private LinearLayout relies;
	private TextView reply;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_message_detail);
		message = (XMessage) getIntent().getSerializableExtra("message");
		initHeadView();
		initView();
		initData();
	}

	@Override
	protected void initHeadView() {
		super.initHeadView();
		mHeadTitle.setText(message.getUserInfoByMsgFromUserId().getUserRealName()
				+ "的留言");
		mHeadRightView.setVisibility(View.GONE);
	}

	private void initView() {
		mFooterLayout = (LinearLayout) findViewById(R.id.diary_footer);
		mOutsiteLayout = (LinearLayout) findViewById(R.id.my_message_outsite);
		mOutsiteLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mFooterLayout.setVisibility(View.GONE);
				return false;
			}
		});

		btnPubComment = (Button) findViewById(R.id.diary_foot_pubcomment);
		etEditComment = (EditText) findViewById(R.id.diary_foot_editer);
		btnPubComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String _commentStr = etEditComment.getText().toString();
				if (StringUtils.isEmpty(_commentStr)) {
					UIHelper.ToastMessage(v.getContext(), "请输入回复内容");
					return;
				}

				if (!appContext.isLogin()) {
					UIHelper.showLoginDialog(MyMessageDetail.this);
					return;
				}

				NetControl.getShare(MyMessageDetail.this).pubMessage(
						appContext.getUserInfo().getId(), _commentStr,
						message.getMsgId(), message.getMsgId(), mHandler);
				mProgress = ProgressDialog.show(v.getContext(), null, "发表中···",
						true, true);
			}
		});
	}

	private void initData() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case Constant.HandlerMessageCode.USER_NOT_LOGIN:
					UIHelper.ToastMessage(MyMessageDetail.this,
							R.string.user_login_out_of_date);
					UIHelper.showLoginDialog(MyMessageDetail.this);
					break;
				case Constant.HandlerMessageCode.PUB_MESSAGE_ERROR: // 发布评论失败
					if (mProgress != null)
						mProgress.dismiss();
					UIHelper.ToastMessage(MyMessageDetail.this,
							R.string.pub_comment_error);
					break;
				case Constant.HandlerMessageCode.PUB_MESSAGE_SUCCESS:// //发表评论成功
					if (mProgress != null)
						mProgress.dismiss();
					// 恢复初始底部栏
					XMessage com = (XMessage) msg.obj;
					List<XMessage> replies = message.getReplys();
					replies.add(com);
					notifyDataSetChanged();
					etEditComment.setText("");
					mFooterLayout.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			}
		};
		
		notifyDataSetChanged();
	}

	private void notifyDataSetChanged() {
		face = (ImageView) findViewById(R.id.my_message_listitem_userface);
		name = (TextView) findViewById(R.id.my_message_listitem_username);
		date = (TextView) findViewById(R.id.my_message_listitem_date);
		content = (TextView) findViewById(R.id.my_message_listitem_content);
		relies = (LinearLayout) findViewById(R.id.my_message_listitem_relies);
		reply = (TextView) findViewById(R.id.my_message_listitem_comment);

		// 设置文字和图片
		final XUserInfo userInfo = message.getUserInfoByMsgFromUserId()
				.getUserName() == null ? appContext.getUserInfo() : message
				.getUserInfoByMsgFromUserId();
		String faceURL = userInfo.getUserHeadImageUrl();
		if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)) {
			face.setImageResource(R.drawable.widget_dface);
		} else {
			ImageManager2.from(MyMessageDetail.this).displayImage(face,
					URLs.HOST + faceURL, R.drawable.widget_dface);
		}
		face.setTag(URLs.HOST + faceURL);// 设置隐藏参数(实体类)
		face.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UIHelper.showUserInfo(MyMessageDetail.this, userInfo);
			}
		});
		reply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mFooterLayout.setVisibility(View.VISIBLE);
			}
		});
		name.setText(userInfo.getUserRealName());
		date.setText(StringUtils.friendly_time(message.getMsgSendTime()
				.toString()));
		content.setText(message.getMsgInfo());
		content.setTag(message);// 设置隐藏参数(实体类)

		relies.setVisibility(View.GONE);
		relies.removeAllViews();// 先清空

		List<XMessage> replies = message.getReplys();

		if (replies != null && replies.size() > 0) {

			for (XMessage reply : replies) {
				View view2 = LayoutInflater.from(MyMessageDetail.this).inflate(
						R.layout.comment_reply, null);
				TextView tv2 = (TextView) view2
						.findViewById(R.id.comment_reply_content);
				XUserInfo userReply = reply.getUserInfoByMsgFromUserId();
				if (userReply.getUserName() == null) {
					userReply = appContext.getUserInfo();
				}
				tv2.setText(userReply.getUserRealName()
						+ "("
						+ StringUtils.friendly_time(reply.getMsgSendTime()
								.toString()) + ")：" + reply.getMsgInfo());
				relies.addView(view2);
			}
			relies.setVisibility(View.VISIBLE);
		}

	}

}
