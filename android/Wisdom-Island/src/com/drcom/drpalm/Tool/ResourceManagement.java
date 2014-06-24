package com.drcom.drpalm.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipException;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.DB.ResourceMsgDB;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.Tool.breakpointDownload.impl.DownloadTask;
import com.drcom.drpalm.Tool.breakpointDownload.impl.SmartFileDownloader;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.Tool.zip.ZipUtils;
import com.drcom.drpalm.View.controls.myinterface.UICallBack;
import com.drcom.drpalm.objs.ToursItem;

public class ResourceManagement {
	/** SD卡是否存在 **/
	private boolean hasSD = false;
	/** SD卡的路径 **/
	private String SDPATH;
	/** 当前资源包的路径 **/
	private static String FILESPATH ;//= "/DrEbaby/" + GlobalVariables.gSchoolKey + "/";
	private String mResourceZipFileName = "";
	private String mResourceDirectory = "";
	private String mResourceOrganizationDirectory = Environment.getExternalStorageDirectory().getPath() + FILESPATH;
	private Options mOptions;
	private ResourceMsgDB mResourceMsgDB;

	// 初始化
//	static private ResourceManagement mResourceManagement = null;
//
//	public static ResourceManagement getResourceManagement() {
//		if (null == mResourceManagement) {
//			mResourceManagement = new ResourceManagement();
//		}
//		
//		FILESPATH = "/DrEbaby/" + GlobalVariables.gSchoolKey + "/";
//		return mResourceManagement;
//	}

	public ResourceManagement() {
		FILESPATH = "/DrEbaby/" + GlobalVariables.gSchoolKey + "/";
		
		hasSD = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		SDPATH = Environment.getExternalStorageDirectory().getPath();
		mResourceMsgDB = ResourceMsgDB.getInstance(GlobalVariables.gAppContext, GlobalVariables.gSchoolKey);
	}
	
//	public static void Destroy(){
//		mResourceManagement = null;
//	}

	public void setOptions(Options options) {
		mOptions = options;
	}

	/**
	 * 解压
	 * 
	 * @param zipName
	 * @param isForce
	 * @return
	 */
	public boolean UnzipResourceWithZip() {// (String zipName, boolean isForce){
		// unzip /files/tourzip/"zipName" to /files/tours/
		// String tourZipPath = GlobalVariables.getResourceZipDirectory();
		// mResourceZipFileName = SDPATH + FILESPATH + zipName;
		// String toursWholePath = GlobalVariables.getResourceDirectory();
		// if(!isEmpty(mResourceDirectory) && !isForce){
		// return true;
		// }
		// clearDirectroy(new File(mResourceDirectory),false);
		String zipName = mResourceMsgDB.getToursItem(GlobalVariables.gSchoolId).name;

		Log.i("zjj", "解压资源包zipName:" + zipName);

		if (zipName.lastIndexOf(".zip") > -1) {
			String resourceDirectory = SDPATH + FILESPATH + zipName.substring(0, zipName.lastIndexOf(".zip"));
			mResourceZipFileName = SDPATH + FILESPATH + zipName;

			File zipFile = new File(mResourceZipFileName);
			try {
				ZipUtils.upZipFile(zipFile, resourceDirectory);
				// deleteAllOtherFile(new File(zipFile));
				// zipFile.delete();
				Log.i("zjj", "解压资源包成功");
				return true;
			} catch (ZipException e) {
				Log.i("zjj", "解压资源包ZipException:" + e.toString());
//				e.printStackTrace();
				return false;
			} catch (IOException e) {
				Log.i("zjj", "解压资源包IOException:" + e.toString());
//				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public Drawable getDrawableByFileName(String fileName) {
		BitmapDrawable bitmapDrawable = null;
		// Picture picture = (Picture)mFileMap.get(fileName);
		Bitmap bitmap;
		// if(null == picture){
		String strFileName = mResourceDirectory + fileName;
		bitmap = getBitmapFromFile(strFileName);
		// if(null != bitmap){
		// picture = new Picture(bitmap);
		// mFileMap.put(fileName, picture);
		// }
		// }
		// else{
		// bitmap = picture.getBitmap();
		// }
		if (null != bitmap) {
			bitmapDrawable = new BitmapDrawable(bitmap);
		}
		return bitmapDrawable;
	}

	/**
	 * 取资源包内图片
	 * 
	 * @param filename
	 * @return
	 */
	public Bitmap getBitmapFromFile(String filename) {
//		if (GlobalVariables.gSchoolId.equals(""))
//			return null;

		String dri = mResourceMsgDB.getToursItem(GlobalVariables.gSchoolId).name;
		String path = "";
		if (isResourceExists(dri + ".zip")) {
			dri = dri.substring(0, dri.lastIndexOf("."));
			path = SDPATH + FILESPATH + dri;
		}

		Log.i("zjj", "取资源包内图片:" + path);

		Bitmap bitmap = null;
		File file = new File(path + filename);
		if (file.exists()) {
			bitmap = BitmapFactory.decodeFile(path + filename, mOptions);
		}
		return bitmap;
	}
	
	/**
	 * 取资源包配置文件
	 * 
	 * @param filename
	 * @return
	 */
	public String getBlockURI(String filename) {
//		if (GlobalVariables.gSchoolId.equals(""))
//			return null;

		String dri = mResourceMsgDB.getToursItem(GlobalVariables.gSchoolId).name;
		String path = "";
		if (isResourceExists(dri + ".zip")) {
			dri = dri.substring(0, dri.lastIndexOf("."));
			path = SDPATH + FILESPATH + dri;
		}

		Log.i("zjj", "取资源包配置文件:" + path);

//		Bitmap bitmap = null;
		File file = new File(path + filename);
//		if (file.exists()) {
//			bitmap = BitmapFactory.decodeFile(path + filename, mOptions);
//		}
		return file.toURI().toString();
	}

	/**
	 * 指定资源包
	 * 
	 * @param zipName
	 */
	public void initResourceName(String zipName) {
		mResourceZipFileName = SDPATH + FILESPATH + zipName;
		mResourceDirectory = SDPATH + FILESPATH + zipName.substring(0, zipName.indexOf(".zip"));
	}

	/**
	 * 检测资源包(解压后)是否存在
	 * 
	 * @param zipName
	 *            资源包名字(***.zip)
	 * @return
	 */
	private boolean isResourceExists(String zipName) {
		// 已解压的资源包路径
		if (zipName.indexOf(".") > -1) {
			mResourceDirectory = SDPATH + FILESPATH + zipName.substring(0, zipName.indexOf(".zip"));
			Log.i("zjj", "isResourceExists--mResourceDirectory:" + mResourceDirectory);
			File file = new File(mResourceDirectory);
			return file.exists();
		} else {
			return false;
		}

	}

	/**
	 * 检测资源包(压缩包)是否存在
	 * 
	 * @param zipName
	 *            资源包名字(***.zip)
	 * @return
	 */
	public boolean isResourceZipExists(String zipName) {
		if (zipName.indexOf(".") > -1) {
			mResourceDirectory = SDPATH + FILESPATH + zipName.substring(0, zipName.indexOf(".zip"));
			Log.i("zjj", "isResourceZipExists--mResourceDirectory:" + mResourceDirectory);
			File file = new File(SDPATH + FILESPATH + zipName);
			return file.exists();
		} else {
			return false;
		}

	}

	/**
	 * 取Tours路径
	 * 
	 * @return
	 */
	public String getToursPath() {
		String dri = mResourceMsgDB.getToursItem(GlobalVariables.gSchoolId).name;
		String path = "";
		if (isResourceExists(dri + ".zip")) {
			dri = dri.substring(0, dri.lastIndexOf("."));
			path = SDPATH + FILESPATH + dri + "/tours/";
		}
		return path;
	}
	
	/**
	 * 取Tours路径(按语言)
	 * 
	 * @return
	 */
	public String getToursPath(LanguageManagement.CurrentLan language) {
		String dri = mResourceMsgDB.getToursItem(GlobalVariables.gSchoolId).name;
		String path = "";
		if (isResourceExists(dri + ".zip")) {
			dri = dri.substring(0, dri.lastIndexOf("."));
			if(language == CurrentLan.COMPLES_CHINESE){
				path = SDPATH + FILESPATH + dri + "/tours_t/";
			}else if(language == CurrentLan.ENGLISH){
				path = SDPATH + FILESPATH + dri + "/tours_en/";
			}else{
				path = SDPATH + FILESPATH + dri + "/tours/";
			}
		}
		return path;
	}

	/**
	 * 取资源包 配置
	 * 
	 * @return
	 */
	public ToursItem getResourceMsgItem() {
		// ResourceMsgItem resourcemsg = new ResourceMsgItem();
		Log.i("zjj", "取资源包 配置:" + SDPATH + FILESPATH + mResourceMsgDB.getToursItem(GlobalVariables.gSchoolId).name);
		Log.i("zjj", "取资源包 更新时间:" + SDPATH + FILESPATH + mResourceMsgDB.getToursItem(GlobalVariables.gSchoolId).lastmdate);
		ToursItem ti = mResourceMsgDB.getToursItem(GlobalVariables.gSchoolId);
		// //从数据库读取本学校资源包名字
		// File file = new File(SDPATH + FILESPATH +
		// mResourceMsgDB.getToursItem(GlobalVariables.gSchoolId).name);
		// //是否有资源包(已解压的)
		// if(file.exists()){
		// File filexml = new File(mResourceDirectory + "//" + "Settings.xml");
		// try {
		// FileInputStream fis = new FileInputStream(filexml);
		//
		// ResourceParser parser = new ResourceParser();
		// resourcemsg = parser.parse(fis);
		//
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }else{
		// resourcemsg.setLastmdate("0");
		// }

		if (!isResourceExists(ti.name)) {
			// resourcemsg.setLastmdate(ti.lastmdate);

			ti.lastmdate = "0";
		}
		// else{
		// resourcemsg.setLastmdate("0");
		// }

		return ti;
	}

	/**
	 * 检测资源包是否要更新
	 */
	public void CheckResourceUpdate() {
		String reszipname = mResourceMsgDB.getToursItem(GlobalVariables.gSchoolId).name;
		Log.i("zjj", "检测资源包是否要更新,从库中读取资源包名字:" + reszipname);
		// 已解压的资源包不存在,ZIP包存在
		// 需要下载新资源包,会发送DOWNLOADTOURS_ACTION广播,并在主界面接收
		// if(!isResourceExists(reszipname) && isResourceZipExists(reszipname)){
		// Log.i("zjj", "已解压的资源包不存在,ZIP包存在");
		// if(UnzipResourceWithZip()){
		// RequestOperationForGateway mRequestOperationForGateway = new
		// RequestOperationForGateway();
		// mRequestOperationForGateway.GetTours(new RequestOperationCallback() {
		//
		// @Override
		// public void onSuccess() {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onError(String err) {
		// // TODO Auto-generated method stub
		// GlobalVariables.toastShow(err);
		// }
		// });
		// return;
		// }
		// }

		// 已解压的资源包存在
		// 或
		// 已解压的资源包不存在,ZIP包不存在,库中文件名存在/不存在/不正确
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationCallback callback = new RequestOperationCallback() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(String err) {
				// TODO Auto-generated method stub

			}
		};
		mRequestOperation.sendGetNeededInfo("GetTours", new Object[] { callback }, callback.getClass().getName());
	}

	// ----------------下载资源包---------------------------\
	// 下载标识
	private boolean isNotifyUpgrade = false;
	// 下载器
	private SmartFileDownloader loader;
	// 通知栏变量
	private final int NF_ID = 1111;
	private Notification nf;
	private NotificationManager nm;
	private int mFileLenght = 0; // 更新包大小

	/**
	 * 
	 * @param path
	 *            下载的目标地址
	 * @param dir
	 *            存放的目标地址
	 * @param callBack
	 *            下载成功回调
	 */
	UICallBack mDownloadCallBack;
	public void download(final Context context, final String url, final UICallBack callBack) {
		Log.i("xpf", " start" + url+" FILESPATH="+FILESPATH);
		mDownloadCallBack = callBack;
		DownloadTask downloadTask = new DownloadTask(handler, url,null, FILESPATH);
		downloadTask.start();
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// Log.i("zjj", "下载资源包:" + url);
		// loader = new SmartFileDownloader(context, url, new File(SDPATH +
		// FILESPATH), 4);
		// mFileLenght = loader.getFileSize();//获取文件的长度
		// // downloadbar.setMax(length);//将文件的总长度设置为进度条的总长度
		// loader.download(new SmartDownloadProgressListener(){
		// @Override
		// public void onDownloadSize(int size) {//可以实时得到文件下载的长度
		// Message msg = new Message();
		// msg.what = 1;
		// msg.getData().putInt("size", size);
		// msg.obj = callBack;
		// handler.sendMessage(msg);
		// }});
		// } catch (Exception e) {
		// Message msg = new Message();//信息提示
		// msg.what = -1;
		// msg.obj = callBack;
		// // msg.getData().putString("error",
		// getResources().getString(R.string.download_faile));//如果下载错误，显示提示失败！
		// handler.sendMessage(msg);
		// }
		// }
		// }).start();//开始
	}

	/**
	 * 处理下载中的信息
	 */
	private Handler handler = new Handler() {
		int size = 0;
		
		@Override
		// 信息
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DownloadTask.DOWN:
				// int size = msg.getData().getInt("size");
				Log.i("xpf", "" + msg.obj);
				size =(Integer) msg.obj;
				if (size == 100) {
					Log.i("zjj", "资源包下载完成,下一步是解压");
					// 解压
					if (UnzipResourceWithZip()) {
						// 回调
						Log.i("zjj", "资源包解压完成,回调到主界面");
						// ((UICallBack) msg.obj).callBack(null);
						if (mDownloadCallBack != null) {
							mDownloadCallBack.callBack(null);
						}
					}
				}
				// nf.contentView.setProgressBar(R.id.ProgressBar01,
				// mFileLenght, size, false);
				// 下载完成
				// if (size == mFileLenght) {
				// // //Toast提示
				// //
				// GlobalVariables.toastShow(getResources().getString(R.string.upgrade_notify_finishandsetup_text));
				// // // Toast.makeText(DrPalmActivity.this,
				// // R.string.upgrade_notify_finishandsetup_text,
				// // Toast.LENGTH_LONG).show();
				// // //设置点击通知栏安装的事件
				// // //取消通知栏的X标志
				// //
				// nf.contentView.setTextViewText(R.id.upgrade_context_textview,
				// //
				// getResources().getString(R.string.upgrade_notify_finish_text));
				// //
				// nf.contentView.setImageViewResource(R.id.upgrade_cancel_imgview,
				// // R.drawable.transparent);
				// // //打开安装包
				// // Intent i = new Intent(Intent.ACTION_VIEW);
				// // i.setDataAndType(Uri.parse("file://" + dir + "/" +
				// // apkname),"application/vnd.android.package-archive");
				// // nf.contentIntent=PendingIntent.getActivity(
				// // DrPalmActivity.this, 0, i ,0);
				// // nm.cancel(NF_ID);
				//
				// Log.i("zjj", "资源包下载完成,下一步是解压");
				// // 解压
				// if (UnzipResourceWithZip()) {
				// // 回调
				// Log.i("zjj", "资源包解压完成,回调到主界面");
				// ((UICallBack) msg.obj).callBack(null);
				// }
				//
				// }
				// nm.notify(NF_ID, nf);
				break;

			case DownloadTask.NOTDOWN:
				// GlobalVariables.toastShow(msg.getData().getString("error"));
				// Toast.makeText(DrPalmActivity.this,
				// msg.getData().getString("error"), 1).show();
				break;
			}

		}
	};

	public boolean initResourceWithZip(String zipName, boolean isForce) {
		// unzip /files/tourzip/"zipName" to /files/tours/
		// String tourZipPath = GlobalVariables.getResourceZipDirectory();
		String toursWholeFileName = GlobalVariables.getResourceZipDirectory() + "/" + zipName;
		// String toursWholePath = GlobalVariables.getResourceDirectory();
		if (!isEmpty(mResourceDirectory) && !isForce) {
			return true;
		}
		// clearDirectroy(new File(mResourceDirectory),false);
		File zipFile = new File(toursWholeFileName);
		try {
			ZipUtils.upZipFile(zipFile, mResourceDirectory);
			// deleteAllOtherFile(new File(zipFile));
			// zipFile.delete();
			return true;
		} catch (ZipException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean initResourceWithZipForOrganization(String zipName, boolean isForce) {
		// unzip /files/tourzip/"zipName" to /files/tours/
		// String tourZipPath = GlobalVariables.getResourceZipDirectory();
		String toursWholeFileName = GlobalVariables.getResourceZipDirectory() + "/" + zipName;
		// String toursWholePath = GlobalVariables.getResourceDirectory();
		if (!isEmpty(mResourceOrganizationDirectory) && !isForce) {
			return true;
		}
		// clearDirectroy(new File(mResourceDirectory),false);
		File zipFile = new File(toursWholeFileName);
		try {
			ZipUtils.upZipFile(zipFile, mResourceOrganizationDirectory);
			// deleteAllOtherFile(new File(zipFile));
			// zipFile.delete();
			return true;
		} catch (ZipException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isEmpty(String path) {
		File dir = new File(path);
		if (dir.exists()) {
			String[] files = dir.list();
			if (0 < files.length)
				return false;
		}
		return true;
	}

	/**
	 * 通过文件名获取之前解压目录下的文件内容
	 * 
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public byte[] getFileContent(String fileName) {
		String path = mResourceOrganizationDirectory + fileName;
		byte[] buffer = null;
		File file = new File(path);

		try {

			FileInputStream fis = new FileInputStream(file);

			// \Test\assets\yan.txt这里有这样的文件存在

			int length = fis.available();
			buffer = new byte[length];
			fis.read(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}
	
	/**
	 * 取学校相交信息
	 * @return
	 */
	public void getSystemMsg(){
		GlobalVariables.gAttendanceUrl =  mResourceMsgDB.GetAttendanceUrl();
	}
}
