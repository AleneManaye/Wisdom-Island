package com.drcom.drpalm.Tool;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.wisdom.island.R;

public class DownloadProgressUtils {
	private static ProgressDialog progressDlg;

	// 图片缓存
	private static ImageLoader mImageLoader;

	/**
	 * 显示正在下载的进度条
	 * 
	 * @param context
	 */
	public static void showProgressDialog(Context context) {
		progressDlg = new ProgressDialog(context);
		progressDlg.setMessage(context.getResources().getString(R.string.album_downloading));
		progressDlg.setCancelable(false);
		progressDlg.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int i) {
				d.cancel();
			}
		});
		Log.i("xpf", "ppp显示");
		progressDlg.show();
	}

	/**
	 * 隐藏正在下载的进度条
	 * 
	 * @param context
	 */
	public static void hideProgressDialog() {
		Log.i("xpf", "ppp隐藏");
		if (progressDlg != null && progressDlg.isShowing())
			progressDlg.cancel();
	}

	/**
	 * 下载完成对话框
	 * 
	 * @param context
	 */
	public static void finishedProgressDialog(Context context) {
		reloadSdcard(context);
		Log.i("xpf", "下载完成");
		Activity activity = (Activity) context;
		while (activity.getParent() != null) {
			activity = activity.getParent();
		}

		final Dialog lDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
		lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		lDialog.setContentView(R.layout.r_normaldialogview);
		((TextView) lDialog.findViewById(R.id.dialog_title)).setVisibility(View.GONE);
		((TextView) lDialog.findViewById(R.id.dialog_message)).setText(activity.getString(R.string.album_downfinish) + "\n" + GlobalVariables.ALBUM_SAVEDIRECT_PATH);
		Button btn_ok = (Button) lDialog.findViewById(R.id.ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// write your code to do things after users clicks OK
				lDialog.dismiss();
			}
		});

		Button btn_cancel = (Button) lDialog.findViewById(R.id.cancel);
		btn_cancel.setVisibility(View.GONE);
		lDialog.show();
	}

	
	/**
	 * 下载完成对话框 并提示是否打开文件
	 * 
	 * @param context
	 */
	public static void finishedProgressDialog(Context context,final String filename) {
		reloadSdcard(context);
		Log.i("xpf", "下载完成");
		Activity activity = (Activity) context;
		while (activity.getParent() != null) {
			activity = activity.getParent();
		}

		final Dialog lDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
		lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		lDialog.setContentView(R.layout.r_normaldialogview);
		((TextView) lDialog.findViewById(R.id.dialog_title)).setVisibility(View.GONE);
		((TextView) lDialog.findViewById(R.id.dialog_message)).setText(activity.getString(R.string.album_downfinish)
				+ "\n" + GlobalVariables.ALBUM_SAVEDIRECT_PATH
				+ "\n" + activity.getString(R.string.album_downfinish_openit));
		Button btn_ok = (Button) lDialog.findViewById(R.id.ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// write your code to do things after users clicks OK
				lDialog.dismiss();
				GlobalVariables.gAppContext.startActivity(FileManagement.openFile(GlobalVariables.ALBUM_SAVEDIRECT_PATH + filename));
			}
		});

		Button btn_cancel = (Button) lDialog.findViewById(R.id.cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// write your code to do things after users clicks OK
				lDialog.dismiss();
			}
		});
		lDialog.show();
	}
	/**
	 * 创建 图片缓存
	 * 
	 * @param mImageLoader
	 */
	public static void setmImageLoader(ImageLoader mImageLoader) {
		DownloadProgressUtils.mImageLoader = mImageLoader;
	}

	/**
	 * 获取图片缓存，改变保存目录为school
	 * 
	 * @param mImageLoader
	 */
	public static ImageLoader getmSchoolImageLoader() {
		mImageLoader.clearMemoryCache();
		GlobalVariables.ALBUM_CACHE = GlobalVariables.ALBUM_SCHOOL_CACHE;
		return mImageLoader;
	}

	/**
	 * 获取图片缓存，改变保存目录为class
	 * 
	 * @param mImageLoader
	 */
	public static ImageLoader getmClassImageLoader() {
		mImageLoader.clearMemoryCache();
		GlobalVariables.ALBUM_CACHE = GlobalVariables.ALBUM_CLASS_CACHE;
		return mImageLoader;
	}

	private static void reloadSdcard(Context context) {
		Log.i("xpf", "重载Sd卡");
		File localFile = Environment.getExternalStorageDirectory();
		Uri localUri = Uri.parse("file://" + localFile.toString());
		Intent localIntent1 = new Intent("android.intent.action.MEDIA_", localUri);
		context.sendBroadcast(localIntent1);
	}
}
