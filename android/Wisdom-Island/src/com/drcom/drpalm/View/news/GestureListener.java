package com.drcom.drpalm.View.news;

import android.app.Activity;
import android.content.Context;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class GestureListener implements OnGestureListener {
	private int nWidth;
	private int nHeight;
	private float speed;
	private Activity activity;

	public GestureListener(Context context, int nWidth, int nHeight) {
		super();
		this.nWidth = nWidth;
		this.nHeight = nHeight;
		this.activity = (Activity) context;
		this.speed = 150;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (e2.getX() - e1.getX() > nWidth / 3 && Math.abs(velocityX) > speed && Math.abs(e1.getY() - e2.getY()) < nHeight / 6) {
			activity.finish();
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}
