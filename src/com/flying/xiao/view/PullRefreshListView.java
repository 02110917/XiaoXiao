package com.flying.xiao.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.flying.xiao.R;

public class PullRefreshListView extends ListView implements OnScrollListener
{

	private static final String TAG = "listview";

	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	// ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı���
	private final static int RATIO = 3;

	private LayoutInflater inflater;

	private LinearLayout headView;
	private AnimationDrawable animationDrawable;
	private ImageView iv;

	// ���ڱ�֤startY��ֵ��һ��������touch�¼���ֻ����¼һ��
	private boolean isRecored;

	private int headContentWidth;
	private int headContentHeight;

	private int startY;
	private int firstItemIndex;

	private int state;
	private OnRefreshListener refreshListener;

	private boolean isRefreshable;

	public PullRefreshListView(Context context)
	{
		super(context);
		init(context);
	}

	public PullRefreshListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	private void init(Context context)
	{
		// setCacheColorHint(context.getResources().getColor(R.color.transparent));
		inflater = LayoutInflater.from(context);

		headView = (LinearLayout) inflater.inflate(R.layout.listview_head, null);

		iv = (ImageView) headView.findViewById(R.id.department_detail_head_image);
		iv.setImageResource(R.drawable.refreshing);
		animationDrawable = (AnimationDrawable) iv.getDrawable();
		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		Log.v("size", "width:" + headContentWidth + " height:" + headContentHeight);
		addHeaderView(headView, null, false);
		setOnScrollListener(this);
		state = DONE;
		isRefreshable = false;
	}

	@Override
	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2, int arg3)
	{
		firstItemIndex = firstVisiableItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1)
	{
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		if (isRefreshable)
		{
			switch (event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored)
				{
					isRecored = true;
					startY = (int) event.getY();
					Log.v(TAG, "��downʱ���¼��ǰλ�á�");
				}
				break;

			case MotionEvent.ACTION_UP:

				if (state != REFRESHING && state != LOADING)
				{
					if (state == DONE)
					{
						// ʲô������
					}
					if (state == PULL_To_REFRESH)
					{
						state = DONE;
						changeHeaderViewByState();

						Log.v(TAG, "������ˢ��״̬����done״̬");
					}
					if (state == RELEASE_To_REFRESH)
					{
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();

						Log.v(TAG, "���ɿ�ˢ��״̬����done״̬");
					}
				}

				isRecored = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();

				if (!isRecored && firstItemIndex == 0)
				{
					Log.v(TAG, "��moveʱ���¼��λ��");
					isRecored = true;
					startY = tempY;
				}

				if (state != REFRESHING && isRecored && state != LOADING)
				{

					// ��֤������padding�Ĺ����У���ǰ��λ��һֱ����head������������б�����Ļ�Ļ����������Ƶ�ʱ���б��ͬʱ���й���

					// ��������ȥˢ����
					if (state == RELEASE_To_REFRESH)
					{

						setSelection(0);

						// �������ˣ��Ƶ�����Ļ�㹻�ڸ�head�ĳ̶ȣ����ǻ�û���Ƶ�ȫ���ڸǵĵز�
						if (((tempY - startY) / RATIO < headContentHeight) && (tempY - startY) > 0)
						{
							state = PULL_To_REFRESH;
							changeHeaderViewByState();

							Log.v(TAG, "���ɿ�ˢ��״̬ת�䵽����ˢ��״̬");
						}
						// һ�����Ƶ�����
						else if (tempY - startY <= 0)
						{
							state = DONE;
							changeHeaderViewByState();

							Log.v(TAG, "���ɿ�ˢ��״̬ת�䵽done״̬");
						}
						// �������ˣ����߻�û�����Ƶ���Ļ�����ڸ�head�ĵز�
						else
						{
							// ���ý����ر�Ĳ�����ֻ�ø���paddingTop��ֵ������
						}
					}
					// ��û�е�����ʾ�ɿ�ˢ�µ�ʱ��,DONE������PULL_To_REFRESH״̬
					if (state == PULL_To_REFRESH)
					{

						setSelection(0);

						// ���������Խ���RELEASE_TO_REFRESH��״̬
						if ((tempY - startY) / RATIO >= headContentHeight)
						{
							state = RELEASE_To_REFRESH;
							changeHeaderViewByState();
							Log.v(TAG, "��done��������ˢ��״̬ת�䵽�ɿ�ˢ��");
						}
						// ���Ƶ�����
						else if (tempY - startY <= 0)
						{
							state = DONE;
							changeHeaderViewByState();

							Log.v(TAG, "��DOne��������ˢ��״̬ת�䵽done״̬");
						}
					}

					// done״̬��
					if (state == DONE)
					{
						if (tempY - startY > 0)
						{
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}

					// ����headView��size
					if (state == PULL_To_REFRESH)
					{
						headView.setPadding(0, -1 * headContentHeight + (tempY - startY) / RATIO, 0, 0);

					}

					// ����headView��paddingTop
					if (state == RELEASE_To_REFRESH)
					{
						headView.setPadding(0, (tempY - startY) / RATIO - headContentHeight, 0, 0);
					}

				}

				break;
			}
		}

		return super.onTouchEvent(event);
	}

	// ��״̬�ı�ʱ�򣬵��ø÷������Ը��½���
	private void changeHeaderViewByState()
	{
		switch (state)
		{
		case RELEASE_To_REFRESH:
			iv.setVisibility(View.VISIBLE);
			animationDrawable.stop();
			Log.v(TAG, "��ǰ״̬���ɿ�ˢ��");
			break;
		case PULL_To_REFRESH:
			iv.setVisibility(View.VISIBLE);
			animationDrawable.stop();
			Log.v(TAG, "��ǰ״̬������ˢ��");
			break;

		case REFRESHING:
			headView.setPadding(0, 0, 0, 0);
			iv.setVisibility(View.VISIBLE);
			animationDrawable.start();
			Log.v(TAG, "��ǰ״̬,����ˢ��...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			iv.setVisibility(View.VISIBLE);
			animationDrawable.stop();
			Log.v(TAG, "��ǰ״̬��done");
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener)
	{
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener
	{
		public void onRefresh();
	}

	public void onRefreshComplete()
	{
		state = DONE;
		changeHeaderViewByState();
	}

	private void onRefresh()
	{
		if (refreshListener != null)
		{
			refreshListener.onRefresh();
		}
	}

	// �˷���ֱ���հ��������ϵ�һ������ˢ�µ�demo���˴��ǡ����ơ�headView��width�Լ�height
	private void measureView(View child)
	{
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null)
		{
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0)
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

}