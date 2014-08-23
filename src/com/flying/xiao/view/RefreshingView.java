package com.flying.xiao.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

import com.flying.xiao.R;
import com.flying.xiao.util.Util;

public class RefreshingView extends View
{
	private final float aXs = 0.95F;
	private final int aXt = (int) (0.5F + 5.5F * Util.DENSITY);
	private final int aXu = (int) (0.5F + 27 * Util.DENSITY);
	private float aXv = 0.0F;
	private float aXw = 0.0F;
	private final int aXx;
	private final List aXy;
	private final Animation aXz;

	public RefreshingView(Context paramContext)
	{
		super(paramContext);
		setDrawingCacheEnabled(false);
		setLayoutParams(new AbsListView.LayoutParams(-1, 0));
		this.aXx = getResources().getDimensionPixelSize(R.dimen.refresh_bar_height);
		this.aXy = new ArrayList();
		int[] arrayOfInt =
		{ 0xff0000, 0x00ff00, 0x0000ff };
		float[] arrayOfFloat =
		{ 0.7F, 0.7F, 0.8F };
		int j;
		for (int i = 0;; i++)
		{
			j = 0;
			if (i >= 3)
				break;
			this.aXy.add(new A(this, arrayOfInt[(i % 3)], arrayOfFloat[(i % 3)]));
		}
		while (j < 3)
		{
			this.aXy.add(this.aXy.get(j));
			j++;
		}
		this.aXz = new z(this);
		this.aXz.setDuration(2000L);
		this.aXz.setInterpolator(new LinearInterpolator());
		this.aXz.setRepeatCount(-1);
	}

	public final void d(float paramFloat)
	{
		this.aXw = Math.min(1.0F, paramFloat);
		this.aXv = 0.0F;
		invalidate();
	}

	public final void dy(boolean paramBoolean)
	{
		if (paramBoolean)
		{
			setVisibility(0);
			this.aXw = 1.0F;
			startAnimation(this.aXz);
			return;
		}
		setVisibility(4);
		clearAnimation();
	}

	@Override
	protected void onDraw(Canvas paramCanvas)
	{
		int i = getWidth();
		int j = this.aXx;
		float f2 = 0;
		A localA = null; // label48:
		float f3 = 0;
		int k = 0;
		while (true)
		{
			if (this.aXw == 1.0F)
			{
				paramCanvas.translate(i / 2, j / 2);
				Iterator localIterator = this.aXy.iterator();
				f2 = this.aXv;
				if (!localIterator.hasNext())
					return;
				localA = (A) localIterator.next();
				localA.aXB = (((float) Math.cos(f2) - 1.0F) / 2.0F);
				if (localA.aXB > -0.46D)
				{
					localA.aXC = ((float) Math.sin(f2) * this.aXu);
					paramCanvas.save();
					paramCanvas.translate(localA.aXC * (1.0F + 0.95F * localA.aXB) * this.aXw, 0.0F);
					f3 = this.aXt * (1.0F + 0.95F * localA.aXB);
					k = localA.aPO.getAlpha();
					if (localA.aXB >= -0.4D)
						return;// break label326;
					localA.aPO.setAlpha(Math.max(0,
							(int) (k * (1.0D + 2.3D * (0.95F * localA.aXB)) * this.aXw)));
				}
				paramCanvas.drawCircle(0.0F, 0.0F, f3, localA.aPO);
				localA.aPO.setAlpha(k);
				paramCanvas.restore();
				f2 = (float) (f2 + 6.283185307179586D / this.aXy.size());
				// break label48;
				float f1 = j / 2 * this.aXw;
				if (f1 < this.aXt)
					f1 -= this.aXt - f1;
				paramCanvas.translate(i / 2, f1);
			}
			
			// break;
			// label326: localA.aPO.setAlpha((int)(k * (1.0F + 0.95F *
			// localA.aXB) * this.aXw));
		}
	}

	final class A
	{
		public Paint aPO = new Paint();
		public float aXB;
		public float aXC;

		public A(RefreshingView paramQMRefreshingView, int paramInt, float paramFloat)
		{
			this.aPO.setAntiAlias(true);
			this.aPO.setColor(paramInt);
			this.aPO.setStyle(Paint.Style.FILL);
			this.aPO.setAlpha((int) (255.0F * paramFloat));
		}
	}

	final class z extends Animation
	{
		z(RefreshingView paramQMRefreshingView)
		{
		}

		@Override
		protected final void applyTransformation(float paramFloat, Transformation paramTransformation)
		{
			d(1.0F);
			d((float) (1.98D * (3.141592653589793D * paramFloat)));
			invalidate();
		}
	}
}
