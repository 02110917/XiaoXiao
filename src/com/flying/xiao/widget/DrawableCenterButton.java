package com.flying.xiao.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * drawableRight���ı�һ�������ʾ
 * 
 * 
 */
public class DrawableCenterButton extends Button {

	private boolean isSelect=false ;
	public boolean isSelect()
	{
		return isSelect;
	}

	public void setSelect(boolean isSelect)
	{
		this.isSelect = isSelect;
	}

	public DrawableCenterButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DrawableCenterButton(Context context, AttributeSet attrs,
	int defStyle) {
		super(context, attrs, defStyle);
	}

	public DrawableCenterButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		Drawable[] drawables = getCompoundDrawables();
		if (drawables != null) {
			Drawable drawableRight = drawables[2];
				if (drawableRight != null) {
			
				float textWidth = getPaint().measureText(getText().toString());
				int drawablePadding = getCompoundDrawablePadding();
				int drawableWidth = 0;
				drawableWidth = drawableRight.getIntrinsicWidth();
				float bodyWidth = textWidth + drawableWidth + drawablePadding;
				setPadding(0, 0, (int)(getWidth() - bodyWidth), 0);
				canvas.translate((getWidth() - bodyWidth) / 2, 0);
			}
		}
		super.onDraw(canvas);
	}
}
