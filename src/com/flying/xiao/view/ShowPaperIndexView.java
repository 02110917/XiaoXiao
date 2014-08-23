package com.flying.xiao.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.flying.xiao.R;

public class ShowPaperIndexView extends View
{
	private int allPage;
	private int currectPage;
	private Bitmap backBitmap;
	public ShowPaperIndexView(Context context)
	{
		super(context);
		backBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gallery_bg);
	}

	public ShowPaperIndexView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		backBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gallery_bg);
	}

	public ShowPaperIndexView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		backBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gallery_bg);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		int width=this.getWidth();
		int height=this.getHeight();
//		backBitmap=Bitmap.createBitmap(backBitmap, 0, 0, width, height);
		Paint p=new Paint();
		p.setAlpha(100);
		canvas.drawBitmap(backBitmap, 0, 0, p);
		p.setAlpha(0);
		p.setColor(Color.RED);
		canvas.drawText(currectPage+"", 0.3f*width, 0.5f*height, p);
		p.setColor(Color.WHITE);
		canvas.drawText("/"+allPage,  0.7f*width,0.5f*height, p);
	}

	public int getAllPage()
	{
		return allPage;
	}

	public void setAllPage(int allPage)
	{
		this.allPage = allPage;
	}

	public int getCurrectPage()
	{
		return currectPage;
	}

	public void setCurrectPage(int currectPage)
	{
		invalidate(); 
		this.currectPage = currectPage;
	}

	
}
