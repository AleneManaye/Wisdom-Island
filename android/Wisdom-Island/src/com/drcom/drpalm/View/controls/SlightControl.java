package com.drcom.drpalm.View.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.widget.LinearLayout;

public class SlightControl extends LinearLayout {

	private boolean mStart = false;
	private boolean mFlag = true;
	private int mTimeStay = 0;
	private int mCurIndex = -1;
	private SlightTextView mSlightTextView;
	private List<String> mContentList = null;
	private Timer mTimer = null;
	public SlightControl(Context context) {
		super(context);	
		if(null == mTimer){
			mTimer = new Timer(true);
		}
		if(null == mSlightTextView){
			//在 SlightTextView 中的 Text 给字体赋颜色值
			mSlightTextView = new SlightTextView(context);
			mSlightTextView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT));
			this.addView(mSlightTextView);
		}
		if(null == mContentList){
			mContentList = new ArrayList<String>();
		}
	}	

	public void resetAllContent(final List<String> contentsList){
		if(null != mContentList){
			synchronized(this){
				mContentList.clear();
				for(String content:contentsList){
					mContentList.add(content);
				}
				if(!mContentList.isEmpty()){
					mCurIndex = 0;
					mFlag = true;
				}
				else{
					mCurIndex = -1;
				}
			}			
		}		
	}
	public void start(){
		if(null == mTimer){
			mTimer = new Timer(true);
		}
		if(!mStart){
			mStart = true;
			mTimer.schedule(new TimerTask(){
				@Override
				public void run() {
					// TODO Auto-generated method stub		
					if(!mSlightTextView.isMoving()){
						synchronized(this){	
							if(mFlag){
								if(-1 != mCurIndex){
									mSlightTextView.setTextString(mContentList.get(mCurIndex));
									mSlightTextView.moveIn();
									mFlag = !mFlag;									
								}								
							}
							else{
								if(1 < mContentList.size()){
									if(mTimeStay > 5){
										mSlightTextView.moveOut();								
										mCurIndex++;									
										mCurIndex %= mContentList.size();
										mFlag = !mFlag;
										mTimeStay = 0;
									}	
									else
										mTimeStay++;
								}								
							}
						}						
							
					}							
				}			
			}, 0, 1000);
		}
		
	}
	public void stop(){
		if(null == mTimer){
			mTimer = new Timer(true);
		}
		if(mStart){
			mTimer.cancel();
			mStart = false;
		}		
	}
	

}
