package com.drcom.drpalm.View.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.drcom.drpalm.GlobalVariables;

public class MyWebview extends WebView {

	private boolean isCanHSolle = true;	//浏览器是否能向横滚动
	private int tempsollerx = 0 ;	//记录webview滚动条上一时刻X坐标(用于判断是否要滑屏)
	private boolean isCount = false;
	private boolean isToLeft = true;
	public boolean isToLeft() {
		return isToLeft;
	}

	public void setToLeft(boolean isToLeft) {
		this.isToLeft = isToLeft;
	}

	public boolean isToRight() {
		return isToRight;
	}

	public void setToRight(boolean isToRight) {
		this.isToRight = isToRight;
	}

	private boolean isToRight = true;
	
	
	public boolean isCanHSolle() {
		return isCanHSolle;
	}

	public void setCanHSolle(boolean isCanHSolle) {
		this.isCanHSolle = isCanHSolle;
	}

	/**
	 * 取横向滚动条范围
	 * @return
	 */
	public int getHorizontalScrollRange(){
		return computeHorizontalScrollRange();
	}
	
	public MyWebview(Context context) {
		super(context);
	}

	public MyWebview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyWebview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void computeScroll () {
//		System.out.println("^^^^^^^^^^^^^^" + getWidth() + "^^^^^^" + computeHorizontalScrollRange());
//		Log.i("zjj", "^^^^^^^^^^^^^^" + computeVerticalScrollRange());
		int offset = GlobalVariables.nDisplayWidth / 60;
		int HScrollWidth = getWidth() - offset + 10;
		if(computeHorizontalScrollOffset() == 0 && computeHorizontalScrollOffset() + HScrollWidth >= computeHorizontalScrollRange()){
			setToLeft(true);
			setToRight(true);
		}else if(computeHorizontalScrollOffset() == 0 && computeHorizontalScrollOffset() + HScrollWidth < computeHorizontalScrollRange()){
			setToLeft(true);
			setToRight(false);
		}else if(computeHorizontalScrollOffset() > 0 && computeHorizontalScrollOffset() + HScrollWidth >= computeHorizontalScrollRange()){
			setToLeft(false);
			setToRight(true);
		}else{
			setToLeft(false);
			setToRight(false);
		}
		super.computeScroll();
	}
	
}
