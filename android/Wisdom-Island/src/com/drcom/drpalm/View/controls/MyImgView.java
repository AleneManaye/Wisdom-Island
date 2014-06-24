package com.drcom.drpalm.View.controls;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 可拉伸的ImageView
 * (XML要设置这个属性才能缩放操作:android:scaleType="matrix")
 * @author zhaojunjie
 *
 */
public class MyImgView extends ImageView {
	private static final int TOUCH_SLOP = 20;   // 移动的阈值
	
	private Activity mActivity;
	private Matrix mMatrix;						//
	public Matrix savedMatrix = new Matrix();	//保存变化状态Matrix
	private int mBitmapW = 0;
	private int mBitmapH = 0;
	private OnClickListener mOnClickListener;
	private int mMinScaleH = 0 ;
	
	private float mClickX0 = -1;		//记录webview点击时X坐标(用于判断是否要保存图片)
	private float mClickY0 = -1;
	private boolean isMoved;			// 是否移动了
	private boolean isNeedChange = true;	//能否缩放(为不让缩到无穷小)
	private boolean isCanMove = true;	//是否能缩放图片
	

	public MyImgView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mActivity = (Activity) context;
		this.setOnTouchListener(new MulitPointTouchListener());
	}
	
	@Override
	public void setImageBitmap(Bitmap bm){
		super.setImageBitmap(bm);
		mBitmapW = bm.getWidth();
		mBitmapH = bm.getHeight();
		isNeedChange = true;
	}

	@Override
	public void setBackgroundDrawable(Drawable d){
		super.setBackgroundDrawable(d);
		mBitmapW = d.getIntrinsicWidth();
		mBitmapH = d.getIntrinsicHeight();
		isNeedChange = true;
	}

	@Override
	public void setBackgroundResource(int resid){
		super.setBackgroundResource(resid);
		Drawable d = mActivity.getResources().getDrawable(resid);
		mBitmapW = d.getIntrinsicWidth();
		mBitmapH = d.getIntrinsicHeight();
		isNeedChange = true;
	}

	@Override
	public void setImageDrawable(Drawable drawable){
		super.setImageDrawable(drawable);
		mBitmapW = drawable.getIntrinsicWidth();
		mBitmapH = drawable.getIntrinsicHeight();
		isNeedChange = true;
	}


	
	/**
	 * 横向、纵向居中
	 * (给控件设置好图片后再调用) 
	 */
	public boolean center(boolean horizontal, boolean vertical){//,int bitmapW,int bitmapH) {
		if(mMatrix == null)
			mMatrix = new Matrix();
//		DisplayMetrics dm = new DisplayMetrics();
		boolean ret = false;
		Matrix m = new Matrix();
		m.set(mMatrix);
		RectF rect = new RectF(0, 0, mBitmapW, mBitmapH);
		m.mapRect(rect);

		int height = (int) rect.height();
		int width = (int) rect.width();

		int deltaX = 0, deltaY = 0;

//		mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		if (vertical) {
			// 图片小于控件大小，则居中显示。大于控件，上方留空则往上移，下方留空则�?���?
			int screenHeight = this.getHeight();// .getLayoutParams().height;// dm.heightPixels;
//			Log.i("zjj", "screenHeight:" + screenHeight);
			if (height < screenHeight) {
				deltaY = (int) ((screenHeight - height) / 2 - rect.top);
			} else if (rect.top > 0) {
				deltaY = (int) -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = (int) (screenHeight - rect.bottom);
			}
		}

		if (horizontal) {
			int screenWidth = this.getWidth();// dm.widthPixels;
//			Log.i("zjj", "screenHeight:" + screenWidth);
			if (width < screenWidth) {
				deltaX = (int) ((screenWidth - width) / 2 - rect.left);
				ret = true;
			} else if (rect.left > 0) {
				ret = true;
				deltaX = (int) -rect.left;
			} else if (rect.right < screenWidth) {
				ret = true;
				deltaX = (int) (screenWidth - rect.right);
			}
		}
		mMatrix.postTranslate(deltaX, deltaY);
		
		this.setImageMatrix(mMatrix);
		
		return ret;
	}
	
	/**
	 * 以高为准拉伸
	 * (给控件设置好图片后再调用)
	 * @param scalef 经控件高的百分比缩放比例(如:1=100% 0.5=50%)
	 */
	public void Scale2Height(float scalef){
		if(scalef <= 0)
			scalef = 0.1f;
		if(mMatrix == null)
			mMatrix = new Matrix();
		float scale = this.getHeight()*scalef / mBitmapH;
		mMatrix.setScale(scale, scale,this.getWidth()/2, this.getHeight()/2);
		
		//默认向左靠
		//缩放后的Bitmap的尺寸
		RectF mTempRect = new RectF(0, 0, mBitmapW, mBitmapH);
		mMatrix.mapRect(mTempRect);
		mMatrix.postTranslate(-mTempRect.left, 0);
		mMatrix.postTranslate(0, -mTempRect.top);
		
//		Log.i("zjj", "this.getHeight():" + this.getHeight() + ",bitmapH:" + mBitmapH + ",scale:" + scale);
		this.setImageMatrix(mMatrix);
		
	}
	
	/**
	 * 
	 * @param l
	 */
    public void setMyOnClickListener(OnClickListener l) {
        if (!isClickable()) {
            setClickable(true);
        }
        mOnClickListener = l;
    }
	
	class MulitPointTouchListener implements OnTouchListener {
		private static final String TAG = "Touch";
		// These matrices will be used to move and zoom image
//		public Matrix matrix = new Matrix();
//		public Matrix savedMatrix = new Matrix();
		float tempLeft = 0f;
		float tempRight = 0f;

		// We can be in one of these 3 states
		static final int NONE = 0;
		static final int DRAG = 1;
		static final int ZOOM = 2;
		int mode = NONE;

		// Remember some things for zooming
		PointF start = new PointF();
		PointF mid = new PointF();
		float oldDist = 1f;
		float scale = 1f;
		RectF mTempRect;
		float moveX = 0f;
		float moveY = 0f;
		
//		public MulitPointTouchListener(){
//			dm = new DisplayMetrics();
//			mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//			float scale = 5;//newDist / oldDist;
//			matrix.postScale(scale, scale, mid.x, mid.y);
//	
//		}
		
		public boolean onTouch(View v, MotionEvent event) {
			if(!isCanMove)
				return true;
			
			ImageView view = (ImageView) v;
			// Log.e("view_width",
			// view.getImageMatrix()..toString()+"*"+v.getWidth());
			// Dump touch event to log
		//	dumpEvent(event);

			// Handle touch events here...
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:

				mMatrix.set(view.getImageMatrix());
				savedMatrix.set(mMatrix);
				start.set(event.getX(), event.getY());
				// Log.d(TAG, "mode=DRAG");
				mode = DRAG;

				// Log.d(TAG, "mode=NONE");
				mClickX0 = event.getRawX();
				mClickY0 = event.getRawY();
				isMoved = false;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				// Log.d(TAG, "oldDist=" + oldDist);
				if (oldDist > 10f) {
					savedMatrix.set(mMatrix);
					midPoint(mid, event);
					mode = ZOOM;
					// Log.d(TAG, "mode=ZOOM");
				}
				
				break;
			case MotionEvent.ACTION_UP:
				mode = NONE;
				
				if (!isMoved){
					if(mOnClickListener!=null)
						mOnClickListener.onClick(v);
				}
				break;
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				// Log.e("view.getWidth", view.getWidth() + "");
				// Log.e("view.getHeight", view.getHeight() + "");
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					moveX = event.getX() - start.x;
					moveY = event.getY() - start.y;
					//禁止图片移出控件显示范围
					mTempRect = new RectF(0, 0, mBitmapW, mBitmapH);
					mMatrix.mapRect(mTempRect);
					if(mTempRect.left < ((mTempRect.width() - mTempRect.width()/6) * -1f)){
						if(moveX<0){
//							Log.i("zjj", "2 left return true");
							return true;
						}
					}else if(mTempRect.left > mTempRect.width() - mTempRect.width()/8){
						if(moveX>0){
//							Log.i("zjj", "2 right return true");
							return true;
						}
					}
					
					if(mTempRect.top < ((mTempRect.height() - mTempRect.height()/6) * -1f)){
						if(moveY<0){
//							Log.i("zjj", "2 top return true");
							return true;
						}
					}else if(mTempRect.top > mTempRect.height() - mTempRect.height()/8){
						if(moveY>0){
//							Log.i("zjj", "2 bottom return true");
							return true;
						}
					}
					
//					Log.i("zjj",  "event.getX():" + mTempRect.left + "," + (event.getX() - start.x) );
					// ...
					mMatrix.set(savedMatrix);
					mMatrix.postTranslate(moveX, moveY);
				} else if (mode == ZOOM) {
					float newDist = spacing(event);
					// Log.d(TAG, "newDist=" + newDist);
					if (newDist > 10f) {
						scale = newDist / oldDist;
						
						//缩放后的Bitmap的尺寸
						mTempRect = new RectF(0, 0, mBitmapW, mBitmapH);
						mMatrix.mapRect(mTempRect);
						//不能缩小小于原图大小
//						float scale2 = 1f;//= view.getHeight()*1.0f / mBitmapH;
						if(mBitmapH < view.getHeight()){
							mMinScaleH = mBitmapH;
						}else{
							mMinScaleH = view.getHeight();
						}
						
						if(mTempRect.height() < mMinScaleH){
							isNeedChange = false;
						}
						
						if(scale > 1f){
							isNeedChange = true;
						}
						
						if(isNeedChange){
							mMatrix.set(savedMatrix);
							mMatrix.postScale(scale, scale, mid.x, mid.y);
						}
						
//						//缩放后的Bitmap的尺寸
//						RectF mTempRect = new RectF(0, 0, mBitmapW, mBitmapH);
//						mMatrix.mapRect(mTempRect);
//						//不能缩小小于原图大小
//						float scale2 = 1f;//= view.getHeight()*1.0f / mBitmapH;
//						if(mBitmapW < view.getWidth()){
//							mMinScaleH = mBitmapH;
//						}else{
//							mMinScaleH = view.getHeight();
//						}
//						
//						if(mTempRect.width() < mMinScaleH){
////							if(mBitmapW < view.getWidth()){
////								scale2 = mBitmapH / mTempRect.height();
////							}else{
////								scale2 = view.getHeight() / mTempRect.height();
////							}
////							Log.i("zjj", "scale2:" + scale2);
////							
////							savedMatrix.reset();
//////							float scale2 = view.getHeight()*1.0f / mBitmapH;
////							savedMatrix.setScale(scale2, scale2,mid.x, mid.y);
////////							savedMatrix.postTranslate(mid.x - mBitmapW/2, mid.y - mBitmapH/2);
////							mMinMatrix.reset();
//							mMinMatrix.postTranslate(tempLeft, tempRight);
////							savedMatrix.set(mMinMatrix);
//							mMatrix.set(mMinMatrix);
//						}else{
//							tempLeft = mTempRect.left;
//							tempRight = mTempRect.top;
//						}
						
					}
				}
				
				if (isMoved)
					break;
				if (Math.abs(mClickX0 -  event.getRawX()) > TOUCH_SLOP
						|| Math.abs(mClickY0 - event.getRawY()) > TOUCH_SLOP) {
					// 移动超过阈值，则表示移动了
					isMoved = true;
				}
				break;
			}

			view.setImageMatrix(mMatrix);
			return true; // indicate event was handled
		}

		private float spacing(MotionEvent event) {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}

		private void midPoint(PointF point, MotionEvent event) {
			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}
	}
	
	/**
	 * 是否能缩放图片
	 * @return
	 */
	public boolean isCanMove() {
		return isCanMove;
	}

	/**
	 * 是否能缩放图片
	 * @return
	 */
	public void setCanMove(boolean isCanMove) {
		this.isCanMove = isCanMove;
	}
}
