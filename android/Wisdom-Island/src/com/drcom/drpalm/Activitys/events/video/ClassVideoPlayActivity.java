package com.drcom.drpalm.Activitys.events.video;

import com.drcom.drpalm.Tool.DownloadProgressUtils;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.wisdom.island.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

/**
 * 视频播放界面
 * @author zjj
 *
 */
public class ClassVideoPlayActivity extends Activity{
	public static String KEY_VIDEOURL = "KEY_VIDEOURL";
	public static String KEY_PERVIEWURL = "KEY_PERVIEWURL";
	
	private VideoView videoView;
	private ProgressBar mProgressBarLoading;
    private ImageView mImageViewPerview ;
    private ImageLoader mImageLoader;
	private MediaController mController;
	
	private String mRUL = "";
	private String mUrlPerview = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.videoplay_view);
		
		// 取参数
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey(KEY_VIDEOURL)) {
			mRUL = extras.getString(KEY_VIDEOURL);
		}
		if (extras.containsKey(KEY_PERVIEWURL)) {
			mUrlPerview = extras.getString(KEY_PERVIEWURL);
		}
		
		// 获取界面上VideoView组件
		videoView = (VideoView) findViewById(R.id.video);
		//封面
        mProgressBarLoading = (ProgressBar)findViewById(R.id.PB_loading);
        mImageViewPerview = (ImageView)findViewById(R.id.image_perview);
        mImageLoader = DownloadProgressUtils.getmClassImageLoader();
		// 创建MediaController对象
		mController = new MediaController(this);
		
		showpic();
		showProgressbar();
		
		videoView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				 paly();
			}
		}, 200);
	}
	
	private void paly(){
		//在线
		Uri uri = Uri.parse(mRUL);
		videoView.setVideoURI(uri);	//<-- URL "http://12.34.45.67/xxxxx.wmv"
		videoView.setMediaController(mController);
		// 设置mController与videoView建立关联
		mController.setMediaPlayer(videoView);
		// 让VideoView获取焦点
		videoView.requestFocus();
		
		videoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				hidepic();
			}
		});
	}
	
	/**
	 * 显示封面图
	 */
	private void showpic(){
		Bitmap bitmap = mImageLoader.getBitmapFromCache(mUrlPerview);
		if (null == bitmap) {
			mImageViewPerview.setBackgroundResource(R.drawable.downloadfaild_pic);
		} else {
			mImageViewPerview.setImageBitmap(bitmap);
		}
	}
	
	/**
	 * 显示LOADING
	 */
	private void showProgressbar(){
		if(mProgressBarLoading.getVisibility() == View.GONE){
			mProgressBarLoading.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 隐藏LOADING 和  封面图
	 */
	private void hidepic(){
		if(mProgressBarLoading.getVisibility() == View.VISIBLE){
			mProgressBarLoading.setVisibility(View.GONE);

			mImageViewPerview.destroyDrawingCache();
			mImageViewPerview.setVisibility(View.GONE);
		}
	}
}
