package com.drcom.drpalm.objs;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.view.View;

public class MainMenuItem implements Serializable {
	private int id;
	private int picResId;
	private String name;
	private String count = "0";	//数量
	private boolean mCanDel = false;	//删除按钮是否显示
	private View.OnClickListener mOnClickDelListener;
	private Bitmap mBitmap = null;
	private boolean show = false;
	
	/**
	 * 是否可视
	 * @return
	 */
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	
	public Bitmap getmBitmap() {
		return mBitmap;
	}
	public void setmBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}
	
	public boolean CanDel() {
		return mCanDel;
	}
	/**
	 * 删除按钮是否显示
	 * @param mCanDel
	 */
	public void setCanDel(boolean mCanDel) {
		this.mCanDel = mCanDel;
	}
	
	public View.OnClickListener getonClickDelListener(){
		return mOnClickDelListener;
	}
	public void setonClickDelListener(View.OnClickListener l){
		mOnClickDelListener = l;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	} 
	
	public String getCount() {
		return count;
	}
	/**
	 * 设置消息数目
	 * @param count
	 */
	public void setCount(String count) {
		this.count = count;
	}
	public int getPicResId() {
		return picResId;
	}
	public void setPicResId(int picResId) {
		this.picResId = picResId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
