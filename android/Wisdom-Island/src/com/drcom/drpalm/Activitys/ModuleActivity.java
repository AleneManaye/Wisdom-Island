/*
 * File         : DrPalmActivity
 * Date         : 2012-03-20
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : Android DrPalmActivity
 */
package com.drcom.drpalm.Activitys;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.DownloadProgressUtils;
import com.drcom.drpalm.View.controls.TipsHelper;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.wisdom.island.R;
import com.drcom.ui.View.controls.DrawCloseActivity.BaseDrawColseActivity;
import com.drcom.ui.View.controls.Gif.TypegifView;

public abstract class ModuleActivity extends BaseDrawColseActivity {
	//
	private boolean isShowTitlebarRightButton = false;
	private View.OnClickListener mBackOnclickListener;
	public static Bitmap mBitmapTitleLogo;
	private GroupReceiverExit mGroupReceiverExit;	// Receiver

	// 控件
//	private RelativeLayout mLayout_BG;
	private LinearLayout mLayout_title;
	private LinearLayout mLayout_Toobar;
	// private LinearLayout mLayout_title_right;
	private Button Button_back;
	private Button Button_Right;
	private ImageView ImageView_Logo;
	private TextView TextView_Title;
	// private ProgressBar mProgressBar;
	public LinearLayout mLayout_body;
	private LinearLayout mLayout_title_Right;
	private LinearLayout mLayout_toolbar_Left;
	private LinearLayout mLayout_toolbar_Right;
	private TypegifView mGifviewLoading;
	private ProgressDialog progressDlg; 
	private LinearLayout mLayout_Welcome;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//一定要放在第一行
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.module_view);
		super.SetView(R.layout.module_view);

		//监听软键盘状态
//		mLayout_BG = (RelativeLayout)findViewById(R.id.Layout_BG);
//		mLayout_BG.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//		    @Override
//		    public void onGlobalLayout() {
//		        int heightDiff = mLayout_BG.getRootView().getHeight() - mLayout_BG.getHeight();
//		        if (heightDiff > 150) {
//					setTheme(android.R.style.Theme);
//
//				} else {
//					setTheme(android.R.style.Theme_Translucent);
//				}
//		     }
//		});

		
		mLayout_title = (LinearLayout) findViewById(R.id.Layout_title);
		mLayout_Toobar = (LinearLayout) findViewById(R.id.Layout_toolbar);
		mLayout_title_Right = (LinearLayout) findViewById(R.id.Layoutright_title_btn);
		Button_back = (Button) findViewById(R.id.title_button_left);
		// Button_Right = (Button) findViewById(R.id.title_button_right);
		ImageView_Logo = (ImageView) findViewById(R.id.title_imageView);
		TextView_Title = (TextView)findViewById(R.id.title_textview); 
		// mProgressBar = (ProgressBar) findViewById(R.id.title_progressBar);
		mGifviewLoading = (TypegifView) findViewById(R.id.title_gifViewloading);
		mGifviewLoading.setSrc(R.raw.loading);	//设置图片资源
		mGifviewLoading.setDelta(1);			//动画速度
		mGifviewLoading.setStop();				//停止播放
		
		mLayout_body = (LinearLayout) findViewById(R.id.Layout_body);
		// mLayout_title_Right = (LinearLayout)
		// findViewById(R.id.Layoutright_title);
		mLayout_toolbar_Left = (LinearLayout) findViewById(R.id.Layout_toolbar_Left);
		mLayout_toolbar_Right = (LinearLayout) findViewById(R.id.Layout_toolbar_Right);
		mLayout_Welcome = (LinearLayout)findViewById(R.id.Layout_welcome);

		Button_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finishDraw();
			}
		});

//		if (this.mBitmapTitleLogo != null) {
//			setTitleLogo(this.mBitmapTitleLogo);
//		}else{
//			ImageView_Logo.setImageBitmap( ((BitmapDrawable) getResources().getDrawable(R.drawable.defaulttitlelogo)).getBitmap());
//		}

		
		initReceiver();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mGroupReceiverExit);
		super.onDestroy();
	}
	
	/**
	 * 设置Title图标
	 */
	public void setTitleLogo(Bitmap bm) {
		// Bitmap bm = null; // 从数据库读取相应学校的图标
//		if(bm != null){
//			this.mBitmapTitleLogo = bm;
//			ImageView_Logo.setImageBitmap(this.mBitmapTitleLogo);
//			
//			ImageView_Logo.setVisibility(View.VISIBLE);
//			TextView_Title.setVisibility(View.GONE);
//		}
	}

	/**
	 * 设置Title 文字
	 * @param text
	 */
	public void setTitleText(String text){
		TextView_Title.setText(text);
		
		TextView_Title.setVisibility(View.VISIBLE);
		ImageView_Logo.setVisibility(View.GONE);
	}
	
	public void hideBackButton() {
		Button_back.setVisibility(View.GONE);
	}

	/**
	 * 隐藏标题
	 */
	public void hideTitle() {
		mLayout_title.setVisibility(View.GONE);
	}

	/**
	 * 隐藏工具栏
	 */
	public void hideToolbar() {
		mLayout_Toobar.setVisibility(View.GONE);
	}
	
	/**
	 * 获取工具栏对象
	 */
	public LinearLayout getToolbar(){
		return mLayout_Toobar;
	}
	/**
	 * 设置工具栏背景色
	 * 
	 * @param color
	 *            Color.?
	 */
	public void setToolbarBgColor(int color) {
		mLayout_Toobar.setBackgroundColor(color);
	}

	/**
	 * 设置Title背景色
	 * 
	 * @param color
	 *            Color.?
	 */
	public void setTitlebarBgColor(int color) {
		mLayout_title.setBackgroundColor(color);
	}

	/**
	 * 返回按钮设置事件
	 * 
	 * @param l
	 */
	public void SetBackBtnOnClickListener(View.OnClickListener l) {
		Button_back.setOnClickListener(l);
		mBackOnclickListener = l;
	}

	/**
	 * 设置返回按钮背景图
	 * 
	 * @param resid
	 */
	public void SetBackBtnBackgroundResource(int resid) {
		Button_back.setBackgroundResource(resid);
	}
	
	/**
	 * 设置返回按钮文字
	 * 
	 * @param resid
	 */
	public void SetBackBtnBackgroundText(String text) {
		Button_back.setText(text);
	}
	
	/**
	 * 设置返回按钮样式
	 * 
	 * @param resid
	 */
	public void SetBackBtnBackgroundStyle(int resid) {
		Button_back.setTextAppearance(ModuleActivity.this,resid);
	}

	/**
	 * 显示Loading
	 */
	public void showProgressBar() {
		mGifviewLoading.setVisibility(View.VISIBLE);
		mGifviewLoading.setStart();
		// if(Button_Right != null)
		// Button_Right.setVisibility(View.GONE);

		mLayout_title_Right.setVisibility(View.GONE);
	}

	/**
	 * 隐藏Loading
	 */
	public void hideProgressBar() {
		mGifviewLoading.setStop();
		mGifviewLoading.setVisibility(View.GONE);
		// if(Button_Right != null && isShowTitlebarRightButton)
		// Button_Right.setVisibility(View.VISIBLE);

		if (isShowTitlebarRightButton)
			mLayout_title_Right.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置itleBar右边的按钮
	 * 
	 * @param b
	 */
	public void setTitleRightButton(Button b) {
		// Button_Right = b;
		// mLayout_title_Right.addView(Button_Right);
		// isShowTitlebarRightButton = true;

		mLayout_title_Right.addView(b);
		isShowTitlebarRightButton = true;
	}

	// /**
	// * 显示TitleBar右边的按钮
	// */
	// public void showTitleRightButton(){
	// Button_Right.setVisibility(View.VISIBLE);
	// isShowTitlebarRightButton = true;
	// }

	/**
	 * 显示TitleBar右边的按钮
	 */
	public void hideTitleRightButton() {
		Button_Right.setVisibility(View.GONE);
		isShowTitlebarRightButton = false;
	}

	// /**
	// * 显示TitleBar右边的按钮点击事件
	// * @param l
	// */
	// public void setTitleRightButtonOnClickListener(View.OnClickListener l){
	// Button_Right.setOnClickListener(l);
	// }

	/**
	 * setbody
	 * 
	 * @param v
	 */
	public void setBodyView(View v) {
		mLayout_body.addView(v);
	}

	/**
	 * 底部工具条(左边)添加按钮
	 * 
	 * @param b
	 */
	public void ToolbarAddLeftButton(View b) {
		mLayout_toolbar_Left.addView(b);
	}

	/**
	 * 底部工具条(右边)添加按钮
	 * 
	 * @param b
	 */
	public void ToolbarAddRightButton(View b) {
		mLayout_toolbar_Right.addView(b);
	}

	/**
	 * 截取返回按钮事件
	 */
	public boolean onKeyDown(int keyCoder, KeyEvent event) {
		if (keyCoder == KeyEvent.KEYCODE_BACK) {
			if (mBackOnclickListener != null)
				mBackOnclickListener.onClick(Button_back);
			else
				finishDraw();
			return false;
		}

		return false;
	}

	public ImageLoader getmSchoolImageLoader() {

		Log.i("xpf", "xpf getSchll");
		return DownloadProgressUtils.getmSchoolImageLoader();
	}

	public ImageLoader getmClassImageLoader() {

		Log.i("xpf", "xpf getClaass");
		return DownloadProgressUtils.getmClassImageLoader();
	}

	
	/**
	 * 弹出LOADING对话框
	 */
	public void ShowLoadingDialog() {
		progressDlg = new ProgressDialog(ModuleActivity.this);
		progressDlg.setMessage(getString(R.string.pleasewait));
		progressDlg.setCancelable(true);
		progressDlg.show();
	}
	
	/**
	 * 隐藏LOADING对话框
	 */
	public void HideLoadingDialog(){
		if(progressDlg != null){
			progressDlg.dismiss();
			progressDlg = null;
		}
		
	}
	
	/**
	 * 显示欢迎框
	 */
	public void ShowWelcome(String username){
		TextView txt = (TextView) mLayout_Welcome.findViewById(R.id.txtview_welcome);
		txt.setText(getString(R.string.welcomeback) + username);
		TipsHelper tipsHelper=new TipsHelper(mLayout_Welcome, this);
		tipsHelper.run();
	}
	
	/**
	 * 是否滑动关闭窗体
	 * @param b
	 */
	protected void setHasGestureToClose(boolean b){
		setIsDrawClose(b);
	}
	
	@Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		super.startActivity(intent);
		//打开Activity动画
		overridePendingTransition(R.anim.push_right_in,0);
	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
		//打开Activity动画
		overridePendingTransition(R.anim.push_right_in,0);
	}
	

//	private boolean isfinish = false;
//	@Override
//	/**
//	 * 重写 finish()方法
//	 * 如果界面关闭界面都是调用finishDraw()方法的话,就不用重写这个方法.
//	 * 但之前界面关闭都是调用finish(),为了让界面有关闭时的动画效果,只能重写.
//	 * 若不重写,界面直接调用finish(),是没有关闭动画的
//	 */
//	public void finish() {
//		if(!isfinish){
//			finishDraw();
//			isfinish = true;
//		}else{
//			super.finish();
//		}
//		// TODO Auto-generated method stub
//	}
	
	/**
	 * 带动画效果关闭窗体
	 */
	protected void finishDraw(){
		super.finishDraw();
	}
	
	/**
     * 收退出程序广播
     * initialize receiver
     */
    private void initReceiver(){
    	mGroupReceiverExit = new GroupReceiverExit();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActivityActionDefine.EXITAPP_ACTION);
        registerReceiver(mGroupReceiverExit,filter);
    }
	
	/**
     * ***************
     * 广播接收
     * ***************
     */
    private class GroupReceiverExit extends BroadcastReceiver{
    	@Override
    	public void onReceive(Context context, Intent intent){
    		try{
    			String stringAction = intent.getAction();
    			Log.i("zjj", "stringAction:" + stringAction);
    			
    			if(stringAction.equals(ActivityActionDefine.EXITAPP_ACTION)){
    				finishDraw();
    			}
    		}
    		catch (Exception e) {
				// TODO: handle exception
    				e.printStackTrace();
			}
        }
	}
}
