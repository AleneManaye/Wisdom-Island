package com.drcom.drpalm.View.controls;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

import com.drcom.drpalm.Activitys.news.NewsImageAdapter;

public class MyGallery extends Gallery {
	private GestureDetector gestureScanner;
	private float fx;

	public MyGallery(Context context) {
		super(context);
		setUnselectedAlpha(255.0F);
		gestureScanner = new GestureDetector(new MySimpleGestureListener());
		gestureScanner.setOnDoubleTapListener(new OnDoubleTapListener() {

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {

				Log.i("xpf", "点到我了");
				return false;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				return false;
			}

			@Override
			public boolean onDoubleTap(MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				View view = MyGallery.this.getSelectedView();
				NewsImageAdapter.ViewHolder vh = null;
				if (view != null && null != view.getTag())
					vh = (NewsImageAdapter.ViewHolder) view.getTag();
				if (vh != null && vh.img != null && vh.img instanceof MyImageView) {
					MyImageView image = vh.img;
					if (image.isZoomed()) {
						image.setZoomToSmall();
					} else {
						image.setZoomBigTo(x, y);
					}
				}

				Log.i("xpf", "onDoubleTap");
				return false;
			}
		});
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		setUnselectedAlpha(255);
		gestureScanner = new GestureDetector(new MySimpleGestureListener());
		gestureScanner.setOnDoubleTapListener(new OnDoubleTapListener() {

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				Log.i("xpf", "点到我了");
				return false;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				return false;
			}

			@Override
			public boolean onDoubleTap(MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				View view = MyGallery.this.getSelectedView();
				NewsImageAdapter.ViewHolder vh = null;
				if (view != null && null != view.getTag())
					vh = (NewsImageAdapter.ViewHolder) view.getTag();
				if (vh != null && vh.img != null && vh.img instanceof MyImageView) {
					MyImageView image = vh.img;
					if (image.isZoomed()) {
						image.setZoomToSmall();
					} else {
						image.setZoomBigTo(x, y);
					}
				}
				Log.i("xpf", "onDoubleTap");
				return false;
			}
		});
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		View view = getSelectedView();
		NewsImageAdapter.ViewHolder vh = null;
		if (view != null && null != view.getTag())
			vh = (NewsImageAdapter.ViewHolder) view.getTag();
		if (vh != null && vh.img != null && vh.img instanceof MyImageView) {
			MyImageView imageView = vh.img;
			float[] v = new float[9];
			Matrix m = imageView.getImageMatrix();
			m.getValues(v);

			float width = imageView.getImageWidth();
			float height = imageView.getImageHeight();

			if (((int) width <= imageView.getScreenWidth()) && ((int) height <= imageView.getScreeHeight())) {
				super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				float left = v[2];
				float right = left + width;
				Rect r = new Rect();
				imageView.getGlobalVisibleRect(r);
				if ((imageView.isMoveToRight()) || (imageView.isMoveToLeft()))
					return super.onScroll(e1, e2, distanceX, distanceY);

				Log.i("xpf", "r.left=" + r.left + " r.right= " + r.right);

				if (distanceX > 0.0F) {
					if (r.left > 0)
						super.onScroll(e1, e2, distanceX, distanceY);
					else if (right < imageView.getScreenWidth())
						super.onScroll(e1, e2, distanceX, distanceY);
				} else if (distanceX < 0.0F) {
					if (r.right < imageView.getScreenWidth())
						super.onScroll(e1, e2, distanceX, distanceY);
					else if (left > 0.0F)
						super.onScroll(e1, e2, distanceX, distanceY);
				}
			}

		} else {
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		return false;
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return (e2.getX() > e1.getX());
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		int keyCode;
		Log.i("xpf", "MX Vx" + velocityX + " fx " + this.fx);
		if (isScrollingLeft(e1, e2))
			keyCode = 21;
		else
			keyCode = 22;

		onKeyDown(keyCode, null);
		return true;
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.i("xpf", "on dispatchTouchEvent ");
		this.gestureScanner.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	public boolean onTouchEvent(MotionEvent event) {
		Log.i("xpf", "onTouchEvent");
		Log.i("xpf", "onTouch mListner");
		View view = getSelectedView();
		NewsImageAdapter.ViewHolder vh = null;
		if (view != null && null != view.getTag())
			vh = (NewsImageAdapter.ViewHolder) view.getTag();
		if (vh != null && vh.img != null && vh.img instanceof MyImageView) {
			MyImageView imageView = vh.img;
			switch (event.getAction() & 0xFF) {
			
			case MotionEvent.ACTION_DOWN://0
				imageView.onACTION_DOWN(event);
				break;
			case MotionEvent.ACTION_UP:
				Log.i("xpf", "第1个手指弹起");
			case 6:
				imageView.onACTION_POINTER_UP();
				break;
			case 5:
				imageView.onACTION_POINTER_DOWN(event);
				break;
			case 2:
				this.fx = event.getX();
				imageView.onACTION_MOVE(event);
			case 3:
			case 4:
			}
		}
		return super.onTouchEvent(event);
	}

	private class MySimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
	}
}