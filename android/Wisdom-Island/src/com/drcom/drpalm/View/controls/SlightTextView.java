package com.drcom.drpalm.View.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.wisdom.island.R;

public class SlightTextView extends TextView implements Runnable {
	
	private int mHeight = 40;
	private int mWidth;
	private boolean mStartRefresh = false;
	
	private Text mText;  
	private Handler mHandler = null;
	private Context mContext = null;	
	
	public SlightTextView(Context context) {  
        super(context);    
        Init(context);
    }  
	public SlightTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);   
        Init(context);
    }
	public void Init(Context context){
		mContext = context;
		this.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,mHeight));
			if(null == mText){
			mText = new Text();			
		}
		if(null == mHandler){
			mHandler = new Handler();
		}		
	}
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mText.width = this.getMeasuredWidth();   
        mText.height = this.getMeasuredHeight();  
//        if (mHeaderView != null) {
//            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
//            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
//            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
//        }
    }
	public void setTextString(String content){
		mText.content = content;
	}	
	public void moveIn() { 
		mText.y = mText.height + mText.size;
		mText.alpha = 0;
		mText.mIsMoveIn = true;
		mStartRefresh = true;
		mHandler.postDelayed(this, 50);
	}  
	public void moveOut(){	
		mText.alpha = 255;
		mText.mIsMoveIn = false;
		mStartRefresh = true;
		mHandler.postDelayed(this, 50);
	}
	public boolean isMoving(){
		return mStartRefresh;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(mStartRefresh){
			postInvalidate();
			if(mText.move()){
				mStartRefresh = false;				
			}
			else{				
				mHandler.postDelayed(this, 50);
			}
		}		
	}	
	@Override  
    protected void onDraw(Canvas canvas) {  
		super.onDraw(canvas);        
		mText.draw(canvas);  
    }
	
	private class Text {  
		public String content = "";
		public int color = mContext.getResources().getColor(R.color.sky_blue);
		public int size =(int)mContext.getResources().getDimension(R.dimen.SlightBarTextSize);
		//public int size =  22;
		public int alpha = 0;
		
		public boolean mIsMoveIn = false;
		
		public int y = 0;		
		public int x = 0;
		public int width = 0;
		public int height = 50;
		public int step = 5;
		
	    private Paint paint;	    
	    private float contentWidth; 
	    
	    public Text() {
	        paint = new Paint();  
	        paint.setColor(color);  
	        paint.setTextSize(size);  
	        paint.setAntiAlias(true);
	        x = 0;
	        y = height + size;
	    }	  
	    public boolean move() { 
	    	y -= step;    		
    		paint.setAlpha(alpha);
    		
	    	if(mIsMoveIn){
	    		if(y > (height + size)/2){		    			
	    			alpha += 2 * (float)step/(height + size) * 255;
	    			if(alpha>255){
	    				alpha = 255;
	    			}	
		    		return false;
		    	}
		    	else{
		    		return true;
		    	}
	    	}else{
	    		if(y > - size - 10){
	    			alpha -= 2 * (float)step/(height + size + 10) * 255;
	    			if(0 < alpha){
	    				alpha = 0;
	    			}
	    			return false;
	    		}
	    		else{
	    			return true;
	    		}
	    	}	    	
	    } 	  
	    public void draw(Canvas canvas) { 
	    	contentWidth = paint.measureText(content); 
	    	x = (int) ((width - contentWidth)/2);
	        canvas.drawText(content, x, y, paint);  
	    }  
	}  

}
