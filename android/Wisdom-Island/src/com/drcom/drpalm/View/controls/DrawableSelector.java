package com.drcom.drpalm.View.controls;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;

public class DrawableSelector extends View{
	
	private Context mContext = null; 
	
	public DrawableSelector(Context context)
	{
		super(context);
		mContext = context;
	}

	
	 public StateListDrawable setBackgroundDrawable(Drawable normal, Drawable pressed, Drawable focused,Drawable disable) 
	 { 
		 StateListDrawable background = new StateListDrawable(); 
		 
		 background.addState(View.PRESSED_ENABLED_STATE_SET, pressed);
		 background.addState(View.ENABLED_FOCUSED_STATE_SET, focused);
		 background.addState(View.ENABLED_STATE_SET, normal);
		 background.addState(View.FOCUSED_STATE_SET, focused);
		 background.addState(View.EMPTY_STATE_SET, disable);
		 
		 return background;
	 }
}
