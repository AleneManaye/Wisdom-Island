package com.drcom.drpalm.View.controls;
import com.wisdom.island.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class TipsHelper {
	private final int SET_AINM_HOLD = 1;
	private View layout;
	private Context myContext;
	private final Handler myHandler = new HoldHandler();
	private Animation animation;
	public TipsHelper(View lo,Context ct){
		layout=lo;
		myContext=ct;
	}
	public void run(){
		layout.setOnTouchListener(new MyTouchListener());
		layout.setVisibility(LinearLayout.VISIBLE);
		animation=AnimationUtils.loadAnimation(myContext, R.anim.show_tips_in);
		animation.setFillAfter(true);                     
        animation.setFillEnabled(true);//停留在结束位置  
		layout.startAnimation(animation);
		animation.setAnimationListener(new MyAnimListener());
	}
	class MyAnimListener implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			myHandler.sendMessageDelayed(
					myHandler.obtainMessage(SET_AINM_HOLD), 3000);

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

	}
	class MyAnimListener2 implements AnimationListener{
		@Override
		public void onAnimationEnd(Animation animation) {
			layout.setVisibility(LinearLayout.GONE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}

	}
	class MyTouchListener implements OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				myHandler.removeMessages(SET_AINM_HOLD);
				animation=AnimationUtils.loadAnimation(myContext, R.anim.show_tips_out);
				layout.startAnimation(animation);
				animation.setAnimationListener(new MyAnimListener2());
				Log.i("onTouch",1+"");
		        return true;
			}
			return false;
		}
		
	}
	class HoldHandler extends Handler { // handler类的实现
		public void handleMessage(Message msg) { // 事件处理函数
			switch (msg.what) {
			case SET_AINM_HOLD: // 选择事件ID
				animation=AnimationUtils.loadAnimation(myContext, R.anim.show_tips_out);
				layout.startAnimation(animation);
				animation.setAnimationListener(new MyAnimListener2());
				break;
			}
		}
	}
}
