package com.drcom.drpalm.Tool.Imagetool;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.drcom.drpalm.Tool.breakpointDownload.impl.SmartDownloadProgressListener;
import com.drcom.drpalm.Tool.breakpointDownload.impl.SmartFileDownloader;
import com.drcom.drpalm.View.controls.myinterface.UICallBack;

/**
 * 附件 图片下载管理工具类
 * @author zhaojunjie
 *
 */
public class ImageDownloadManager {
	/** SD卡的路径**/ 
    private String SDPATH; 
    /** 当前资源包的路径**/ 
    private String FILESPATH = "/DrEbaby/Pictures/";
    //下载标识
    private boolean isNotifyUpgrade = false;
    //下载器
    private SmartFileDownloader loader;
    //通知栏变量
    private final int NF_ID=1111;
	private Notification nf ;
	private NotificationManager nm ;
	private int mFileLenght = 0;	//更新包大小
	
	public ImageDownloadManager(){
		SDPATH = Environment.getExternalStorageDirectory().getPath(); 
	}
	
	/**
    *
    * @param path  下载的目标地址
    * @param dir   存放的目标地址
    */
   public void download(final Context context, final String url, final UICallBack callBack){
	   new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Log.i("zjj", "下载图片:" + url);
					loader = new SmartFileDownloader(context, url, new File(SDPATH + FILESPATH), 1);
					mFileLenght = loader.getFileSize();//获取文件的长度
//					downloadbar.setMax(length);//将文件的总长度设置为进度条的总长度
					loader.download(new SmartDownloadProgressListener(){
						@Override
						public void onDownloadSize(int size) {//可以实时得到文件下载的长度
							Message msg = new Message();
							msg.what = 1;
							msg.getData().putInt("size", size);
							msg.obj = callBack;
							handler.sendMessage(msg);
						}});
				} catch (Exception e) {
					Message msg = new Message();//信息提示
					msg.what = -1;
					msg.obj = callBack;
//					msg.getData().putString("error", getResources().getString(R.string.download_faile));//如果下载错误，显示提示失败！
					handler.sendMessage(msg);
				}
			}
		}).start();//开始
   }
   
   /**
	 * 处理下载中的信息
	 */
   private Handler handler = new Handler(){

		@Override//信息
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				int size = msg.getData().getInt("size");
//				nf.contentView.setProgressBar(R.id.ProgressBar01, mFileLenght, size, false);
				//下载完成
				if(size == mFileLenght){
//					//Toast提示
//					GlobalVariables.toastShow(getResources().getString(R.string.upgrade_notify_finishandsetup_text));
////					Toast.makeText(DrPalmActivity.this, R.string.upgrade_notify_finishandsetup_text, Toast.LENGTH_LONG).show();
//					//设置点击通知栏安装的事件
//					//取消通知栏的X标志
//					nf.contentView.setTextViewText(R.id.upgrade_context_textview, getResources().getString(R.string.upgrade_notify_finish_text));
//					nf.contentView.setImageViewResource(R.id.upgrade_cancel_imgview, R.drawable.transparent);
//					//打开安装包
//					Intent i = new Intent(Intent.ACTION_VIEW);
//					i.setDataAndType(Uri.parse("file://" + dir + "/" + apkname),"application/vnd.android.package-archive");
//					nf.contentIntent=PendingIntent.getActivity( DrPalmActivity.this, 0, i ,0);
//					nm.cancel(NF_ID);
					
					//解压
//					UnzipResourceWithZip();
					//回调
					((UICallBack)msg.obj).callBack(null);
				}
//				nm.notify(NF_ID, nf);
				break;

			case -1:
//				GlobalVariables.toastShow(msg.getData().getString("error"));
//				Toast.makeText(DrPalmActivity.this, msg.getData().getString("error"), 1).show();
				break;
			}

		}
   };
}
