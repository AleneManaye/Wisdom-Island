package com.drcom.drpalm.View.controls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.drcom.drpalm4tianzhujiao.R;

public class PageIndicatorView extends View {
	private String TAG = "PageIndicatorView";
	private int mCurrentPage = -1;
	private int mTotalPage = 0;
	private int iconWidth = 20;
	private int iconHeight = 20;

	public PageIndicatorView(Context context) {
		super(context);
	}

	public PageIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setCount(int count) {
		mTotalPage = count;
		if (mCurrentPage >= mTotalPage)
			mCurrentPage = mTotalPage - 1;
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	public void setCurrentPage(int nPageIndex) {
		if (nPageIndex < 0 || nPageIndex >= mTotalPage)
			return;

		if (mCurrentPage != nPageIndex) {
			mCurrentPage = nPageIndex;
			this.invalidate();
		}
	}

	public void setPointSize(int size) {
		iconWidth = size;
		iconHeight = size;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.d(TAG, "on draw...");
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(Color.BLACK);

		Rect r = new Rect();
		this.getDrawingRect(r);

		int space = 12;

		int x = (r.width() - (iconWidth * mTotalPage + space * (mTotalPage - 1))) / 2;
		int y = (r.height() - iconHeight) / 2;

		for (int i = 0; i < mTotalPage; i++) {

			int resid = R.drawable.page_indicator_focused;

			if (i == mCurrentPage) {
				resid = R.drawable.page_indicator;
			}

			Rect r1 = new Rect();
			r1.left = x;
			r1.top = y;
			r1.right = x + iconWidth;
			r1.bottom = y + iconHeight;

			Bitmap bmp = BitmapFactory.decodeResource(getResources(), resid);
			canvas.drawBitmap(bmp, null, r1, paint);

			x += iconWidth + space;

		}

	}

}
