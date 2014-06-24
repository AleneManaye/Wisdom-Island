package com.drcom.drpalm.View.controls;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class OnOffImageView extends ImageView{
//	private int nOnResId = 0;  
//	private int nOffResId = 0;
	
	private Drawable mDrawableOn = null;
	private Drawable mDrawableOff = null;
	
	private boolean bStatus = false;
	
	public void InitStatus(boolean bOn)
	{
		if(mDrawableOn==null ||
				mDrawableOff == null)
			return;
		this.setImageDrawable(bOn?mDrawableOn:mDrawableOff);
		//this.setImageResource(bOn?nOnResId:nOffResId);
		bStatus = bOn;
	}
	
	public void Clicked()
	{
		bStatus = !bStatus;
		if(bStatus)
		{
			this.setImageDrawable(mDrawableOn);
		}
		else
		{
			this.setImageDrawable(mDrawableOff);
		}
		//this.setImageDrawable(bStatus?mDrawableOn:mDrawableOff);
		//this.setImageResource(bStatus?nOnResId:nOffResId);
	}

	
	public OnOffImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
//	public void SetResId(int nOnResId,int nOffResId)
//	{
//		this.nOnResId = nOnResId;
//		this.nOffResId = nOffResId;
//	}
	
	public void SetDrawable(Drawable drawableoOn,Drawable drawableOff)
	{
		this.mDrawableOn = drawableoOn;
		this.mDrawableOff = drawableOff;
	}
	
//	public void SetStatus(boolean bOn)
//	{
//		this.setImageResource(bOn?nOnResId:nOffResId);
//	}
}
