package com.drcom.drpalm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.CRC32;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.CrashHandler;
import com.drcom.drpalm.Tool.DownloadProgressUtils;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.PushManager;
import com.drcom.drpalm.Tool.Imagetool.BitmapCache;
import com.drcom.drpalm.Tool.XmlParse.SaxReadxml;
import com.drcom.drpalm.Tool.XmlParse.ShareItemReadxml;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.Tool.service.DrServiceJni;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.login.LoginManager.OnlineStatus;
import com.drcom.drpalm.objs.Block;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.EventDraftItem.Attachment;
import com.drcom.drpalm.objs.PushSettingInfo;
import com.drcom.drpalm.objs.ShareToFriendItem;
import com.wisdom.island.R;

//用于保存全局所用到的变量
public class GlobalVariables extends Application {
	// Mobile Server
	private static final String TAG = "Global";
	private RequestOperation mRequestOperation;

	// Center Information
	// 中心地址
	public static String gCenterDomain = "http://42.121.12.89:8080";//"http://220.232.237.221:8001";//HK正式环境		"http://58.67.148.65:8129";//HK测试环境	 // 		广电"http://192.168.12.214:8004";//广电 //"http://58.67.148.65:8129";//高主教  //"http://drpalm.doctorcom.com:8080";//正式
	public static String gSchoolKey = "";
	public static String gResourceVersion = "";
	// School Information
	public static String gGateawayDomain = ""; // "192.168.42.13:8001/Ebaby";//
	public static String gSchoolId = "";
	public static String gSessionKey = "";
	public static String gSeqid = "";
	public static String gAccUrl = "";
	public static String gAttendanceUrl = "";
	// Application Resource
	public static Context gAppContext;
	public static Resources gAppResource;
	public static String gLanguage;

	public static String gPicPath = "";

	// 不同学校的log地址
	public static String gTitleUrl = "";

	// 组织架构文件名字
	public static String gOrgName = "";

	// 推送设置信息
	public static PushSettingInfo gPushSettingInfo;
	
	//用户可发通告类型
	public static ArrayList<String> UserSendpermisList = new ArrayList<String>(); 

	// ebaby 频道 ip and port
	public static final String URL_IP_PORT_EBABYCHANNEL = "http://220.232.237.222:8001";//"http://58.67.148.65:8128";//"http://ebabychannel.doctorcom.com:8080";// 
	// Ebaby 导航显示的 地址
	public static String URL_NAVIGATION_EBABYCHANNEL ;//= URL_IP_PORT_EBABYCHANNEL + "/Ebaby32/web/channel/topbanner?lang=zh-hant&tokenid=";
	// Ebaby 更多显示的 地址
	public static String URL_EBABYCHANNEL ;	//= URL_IP_PORT_EBABYCHANNEL + "/Ebaby32/web/channel/top?lang=zh-hant&tokenid=";

	// ebaby 集合 ip and port
	public static final String URL_IP_PORT_COLLEACT = "http://ebabyset.doctorcom.com:8080";// "http://58.67.148.65:8126";
	// Ebaby集合地址
	public static String URL_EBABYSET ;

	// Ebaby帮助地址
	public static String URL_HELP = "http://www.astar.so/ebaby/help/app.html";//"http://www.astar.so/help/apphelp/";
	
	//错误页面地址
	public static String URL_ERROR = "file:///android_asset/error/error.html";
	
	//错误页面地址1
	public static String URL_ERROR_1 = "file:///android_asset/error1/error.html";

	// 设备ID
	public static String Devicdid = "";

	/**
	 * 外部启动程序的包名(用作登录时,生成Tokenid,push到外部启动的程序中)
	 */
	public static String mExternalPackageName = "";
	
	// /**
	// * 取设备ID
	// * @return
	// */
	// public String getDevicdid() {
	// return devicdid;
	// }
	// public void setDevicdid(String devicdid) {
	// this.devicdid = devicdid;
	// }

	/**
	 * 班级通知
	 */
	public static final int EVENTSTYPES_NOTICE = 0;
	/**
	 * 班级评语
	 */
	public static final int EVENTSTYPES_COMMENT = 1;
	/**
	 * E宝快照
	 */
	public static final int EVENTSTYPES_SNAPSHOT = 2;
	/**
	 * 最新课件
	 */
	public static final int EVENTSTYPES_COURSEWARE = 3;
	
	/**
	 * 保存文件的目录名
	 */
	public static final String ROOTPATH = "/EBaby";	//所有文件的根目录路径
	public static final String ALBUM_SAVEDIRECT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "EbabyAlbum" + File.separator + "download";
	// 保存文件的目录名
	public static final String ALBUM_SCHOOL_CACHE = "cache" + File.separator + "school";
	public static final String ALBUM_CLASS_CACHE = "cache" + File.separator + "class";
	public static String ALBUM_CACHE = "cache" + File.separator + "school";
	public static int SCREEN_WIDTH = 300;
	public static int SCREEN_HEIGHT = 300;
	// 屏幕状态栏高度，用以让相册详细全屏显示
	public static int STATEBARHEIGHT = 0;
	// //ModuleList
	// public static List<ModuleInfo> gModuleList = null;
	//
	// //NewsCategoryList
	// public static List<NewsCategory> gListNewsCategory = null;
	//
	// //EventsCategoryList
	// public static List<EventsCategory> gListEventsCategory = null;

	// 多语言Map
	public static Map<String, String> gMapTranslate = null;
	// Application View
	public static int nDisplayWidth = 0;
	public static int nDisplayHeight = 0;
	public static int nDensity = 160;
	//应用信息
	public static String nPkgname = "";
	public static String nAppVersion = "";
	
	//
	public static boolean bShowAboutInSettingModule = false;

	private static Toast toast;

	public static String gSelfOrgID = "";

	public static boolean gAppRun = false; // 应用是否已经正式启动（用来区别是否从PUSH进入）,只有MainActivity启动才会为true;
	public static Bitmap no_pic;//图片正在下载时显示的图片
	public static Bitmap wrong_pic;//下载出错时显示的图片
	public static Bitmap null_pic;//没有url时显示的图片
	public static List<Block> blocks;//模块是否显示列表
	public static List<ShareToFriendItem> shareList;
	
	//按钮大小
	public static int btnHeight_Toolbar = 42;	//dp
	public static int btnWidth_Toolbar = 42;	//dp
	
	public static int btnHeight_Titlebar = 36;	//dp
	public static int btnWidth_Titlebar = 52;	//dp
	
	//vlc
	public final static String SLEEP_INTENT = "org.videolan.vlc.SleepIntent";
	
	//帐号关联跳传的帐号密码(使用后清空)
	public static String gTempUsermane = "";
	public static String gTempPW = "";

	@Override
	public void onTerminate() {
		mRequestOperation.unbindService();
		super.onTerminate();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate()");

		GlobalVariables.gAppContext = this.getApplicationContext();
		Resources res = this.getResources();
		GlobalVariables.gAppResource = res;

		CrashHandler crashHandler = CrashHandler.getInstance();
		// 注册crashHandler
		crashHandler.init(GlobalVariables.gAppContext);
//		// push设置初始化
		gPushSettingInfo = new PushSettingInfo();

		mRequestOperation = RequestOperation.getInstance();
		// 绑定Service
		mRequestOperation.bindService(new RequestOperationCallback() {
			@Override
			public void onError(String err) {
				Log.d("RequestOperation bindService onError:", err);
			}

			@Override
			public void onSuccess() {
			}
		}, new RequestOperationCallback() {
			@Override
			public void onError(String err) {
				Log.d("RequestOperation bindService onError:", err);
			}

			@Override
			public void onSuccess() {
			}
		});

		// 取设置ID
		DrServiceJni dsj = new DrServiceJni();
		this.Devicdid = dsj.GetDeviceId();

		Log.i("xpf", "yk 创建缓存");
		DownloadProgressUtils.setmImageLoader(new ImageLoader(GlobalVariables.ALBUM_SCHOOL_CACHE));
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		this.no_pic = BitmapFactory.decodeResource(this.getResources(), R.drawable.album_no_pic, opts);	//图片正在下载时显示的图片
		this.wrong_pic = BitmapFactory.decodeResource(this.getResources(), R.drawable.downloadfaild_pic, opts);
        this.null_pic = BitmapFactory.decodeResource(this.getResources(), R.drawable.no_pic, opts);
		// 解析模块是否显示
		blocks = SaxReadxml.saxXml(this);
		shareList = ShareItemReadxml.saxXml(this);
		
		//测试VLC播放器
//		initFlv();
		
		//变量
		URL_NAVIGATION_EBABYCHANNEL = URL_IP_PORT_EBABYCHANNEL + "/Ebaby4/web/channel/topbanner?lang=" + LanguageManagement.getSysLanguageUrlStr(gAppContext) + "&tokenid=";
		URL_EBABYCHANNEL = URL_IP_PORT_EBABYCHANNEL + "/Ebaby4/web/channel/top?lang=" + LanguageManagement.getSysLanguageUrlStr(gAppContext) + "&tokenid=";
		URL_EBABYSET = URL_IP_PORT_COLLEACT + "/Ebaby3/web/kgcollection/getarealist?lang=" + LanguageManagement.getSysLanguageUrlStr(gAppContext) + "&pid=";
		
		nPkgname = getPackageName();
		nAppVersion = getAppVersion();
		
		initDisplayMetrics();
	}

	/**
     * Called when the overall system is running low on memory
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "System is running low on memory");

        BitmapCache.getInstance().clearCache();
    }
	
	private void initFlv(){
		// Are we using advanced debugging - locale?
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String p = pref.getString("set_locale", "");
        if (p != null && !p.equals("")) {
            Locale locale;
            // workaround due to region code
            if(p.equals("zh-TW")) {
                locale = Locale.TRADITIONAL_CHINESE;
            } else if(p.startsWith("zh")) {
                locale = Locale.CHINA;
            } else if(p.equals("pt-BR")) {
                locale = new Locale("pt", "BR");
            } else if(p.equals("bn-IN") || p.startsWith("bn")) {
                locale = new Locale("bn", "IN");
            } else {
                /**
                 * Avoid a crash of
                 * java.lang.AssertionError: couldn't initialize LocaleData for locale
                 * if the user enters nonsensical region codes.
                 */
                if(p.contains("-"))
                    p = p.substring(0, p.indexOf('-'));
                locale = new Locale(p);
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        // Initialize the database soon enough to avoid any race condition and crash
//        MediaDatabase.getInstance(this);
	}
	
	public static boolean cleanSaveFile() {
		if (GlobalVariables.gAppContext == null)
			return false;
		SharedPreferences sp = GlobalVariables.gAppContext.getSharedPreferences("GlobalVaribles", Activity.MODE_PRIVATE);
		Editor editor = sp.edit();// 获取编辑器
		editor.clear();
		boolean bReturn = editor.commit();
		return bReturn;
	}

	// 保存
	public static boolean save() {
		if (GlobalVariables.gAppContext == null)
			return false;
		SharedPreferences sp = GlobalVariables.gAppContext.getSharedPreferences("GlobalVaribles", Activity.MODE_PRIVATE);
		Editor editor = sp.edit();// 获取编辑器

		editor.putString("CENTERDOMAIN", GlobalVariables.gCenterDomain);
		editor.putString("SCHOOLKEY", GlobalVariables.gSchoolKey);
		editor.putString("GATEWAYDOMAN", GlobalVariables.gGateawayDomain);
		editor.putString("SCHOOLID", GlobalVariables.gSchoolId);
		editor.putString("SESSIONKEY", GlobalVariables.gSessionKey);
		editor.putString("SEGID", GlobalVariables.gSeqid);
		editor.putString("LANGUAGE", GlobalVariables.gLanguage);
		editor.putInt("DISPLAYWIDTH", GlobalVariables.nDisplayWidth);
		editor.putInt("DISPLAYHEIGHT", GlobalVariables.nDisplayHeight);
		editor.putInt("DENSITY", GlobalVariables.nDensity);
		editor.putBoolean("SHOWABOUTINSETTINGMODULE", GlobalVariables.bShowAboutInSettingModule);
		boolean bReturn = editor.commit();// 提交修改
		return bReturn;
	}

	// 读取
	public static boolean get() {
		if (GlobalVariables.gAppContext == null)
			return false;
		SharedPreferences sp = GlobalVariables.gAppContext.getSharedPreferences("GlobalVaribles", Activity.MODE_PRIVATE);
		GlobalVariables.gCenterDomain = sp.getString("CENTERDOMAIN", "");
		GlobalVariables.gSchoolKey = sp.getString("SCHOOLKEY", "");
		GlobalVariables.gGateawayDomain = sp.getString("GATEWAYDOMAN", "");
		GlobalVariables.gSchoolId = sp.getString("SCHOOLID", "");
		GlobalVariables.gSessionKey = sp.getString("SESSIONKEY", "");
		GlobalVariables.gSeqid = sp.getString("SEGID", "");
		GlobalVariables.gLanguage = sp.getString("LANGUAGE", "");
		GlobalVariables.nDisplayWidth = sp.getInt("DISPLAYWIDTH", 0);
		GlobalVariables.nDisplayHeight = sp.getInt("DISPLAYHEIGHT", 0);
		GlobalVariables.nDensity = sp.getInt("DENSITY", 0);
		GlobalVariables.bShowAboutInSettingModule = sp.getBoolean("SHOWABOUTINSETTINGMODULE", false);
		return true;
	}

	// 获取程序是否测试版本
	static public final boolean getAppIsTestversion() {
		if (GlobalVariables.gAppContext.getString(R.string.IsTestVersion).contentEquals("true"))
			return true;
		else
			return false;
	}
	
	// 获取代理商ID
	static public final int getAgentID() {
		int id = 0;
		if(GlobalVariables.gAppContext.getString(R.string.agentid).length()>0)
			id = Integer.valueOf(GlobalVariables.gAppContext.getString(R.string.agentid));
		return id;
	}

	/**
	 * 获取程序是否有指定SCHOOLKEY
	 * 
	 * @return 是否有指定SCHOOLKEY
	 */
	static public final boolean getAppDefaultSchoolKey() {
		String tempschoolkey = GlobalVariables.gAppContext.getString(R.string.SchoolKey);

		if (tempschoolkey.equals("")) {
			return false;
		} else {
			gSchoolKey = tempschoolkey;
			return true;
		}
		
	}

	static public final String getResVersion() {
		String version = "";
		version = GlobalVariables.gAppContext.getString(R.string.ResVersion);
		return version;
	}
	
	static public final String getAppName() {
		return GlobalVariables.gAppContext.getString(R.string.app_name);
	}

	/**
	 * 取屏幕参数
	 */
	private void initDisplayMetrics(){
		DisplayMetrics dm = new DisplayMetrics(); 
		dm = this.getApplicationContext().getResources().getDisplayMetrics(); 
		//屏幕分辨率
		nDisplayWidth = dm.widthPixels;	
		nDisplayHeight = dm.heightPixels;
		//逻辑密度
		nDensity = dm.densityDpi;
	}
	
	/**
	 * 获取当前系统的android版本号
	 * @return
	 */
	public static String getOSVersion(){
//		return String.valueOf(android.os.Build.VERSION.SDK_INT);
		return String.valueOf(android.os.Build.VERSION.RELEASE);
	}
	
	/**
	 * 手机的型号
	 * @return
	 */
	public static String getMoelno(){
		return android.os.Build.MODEL;
	}

	/**
	 * 获取当前应用的版本号
	 * @return
	 * @throws Exception
	 */
	private String getAppVersion() {
		String version = "";
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return version; 
	}

	static public final String getResourceDirectory() {
		String toursWholePath = GlobalVariables.gAppContext.getDir("Resource", Context.MODE_PRIVATE).getPath();
		toursWholePath += "/" + GlobalVariables.gAppResource.getString(R.string.ResourceDirectory);
		return toursWholePath;
	}

	static public final String getResourceZipDirectory() {
		String toursWholePath = GlobalVariables.gAppContext.getDir("Resource", Context.MODE_PRIVATE).getPath();
		toursWholePath += "/" + GlobalVariables.gAppResource.getString(R.string.ResourceZipDirectory);
		return toursWholePath;
	}

	static public final String getResourceTempDirectory() {
		String toursWholePath = GlobalVariables.gAppContext.getDir("Resource", Context.MODE_PRIVATE).getPath();
		toursWholePath += "/" + GlobalVariables.gAppResource.getString(R.string.ResourceTempDirectory);
		return toursWholePath;
	}

	static public int getHeightbyFileName(String fileName) {
		int nHeight = 0;
		String mResourceDirectory = GlobalVariables.getResourceDirectory();
		if ("" != mResourceDirectory) {
			String strFileName = mResourceDirectory + fileName;
			File file = new File(strFileName);
			try {
				FileInputStream is;
				is = new FileInputStream(file);
				Options options = new Options();
				// options.inDensity = DisplayMetrics.DENSITY_LOW;
				options.inDensity = DisplayMetrics.DENSITY_HIGH;
				Bitmap image = BitmapFactory.decodeFile(strFileName, options);
				// Bitmap image = BitmapFactory.decodeStream(is);
				nHeight = image.getHeight();
				image.recycle();
				is.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return nHeight;
	}

	// 计算CRC
	static public String calcCrc(byte[] data) {
		if(data != null){
			CRC32 crc = new CRC32();
			crc.update(data);
			data = null;
			return Long.toHexString(crc.getValue());
		}
		return "";
	}

	// static public boolean parseSettingFile()
	// {
	// GlobalVariables.gMapTranslate = ParseXmlFile.parseTranslateFile();
	// SettingItem settingItem = ParseXmlFile.parsesettingfile();
	// if(null == settingItem)
	// {
	// return false;
	// }
	// else
	// {
	// GlobalVariables.gModuleList = settingItem.m_ListModuleInfo;
	// GlobalVariables.gListNewsCategory = settingItem.m_ListNewsCategory;
	// GlobalVariables.gListEventsCategory = settingItem.m_ListEventsCategories;
	// GlobalVariables.gCenterDomain = settingItem.strCenterAddress;
	// //GlobalVariables.gSchoolKey = settingItem.strSchoolKey;
	// GlobalVariables.bShowAboutInSettingModule =
	// settingItem.bShowAboutInSettingModule;
	// GlobalVariables.gResourceVersion = settingItem.strResourceVersion;
	// return true;
	// }
	// }

	// 获取机器唯一标识号
	static public String getDeviceId() {
//		String deviceid = "";
//		TelephonyManager telephonyManager = (TelephonyManager) gAppContext.getSystemService(Context.TELEPHONY_SERVICE);
//		deviceid = telephonyManager.getDeviceId();
//		if (deviceid == null) {
//			WifiManager wifi = (WifiManager) gAppContext.getSystemService(Context.WIFI_SERVICE);
//			if (wifi != null) {
//				WifiInfo info = wifi.getConnectionInfo();
//				if (info != null) {
//					deviceid = info.getMacAddress();
//				}
//			}
//		}
//		return deviceid;
//		PushManager.
		DrServiceJni dsj = new DrServiceJni();
		return dsj.GetDeviceId();//PushManager.getDeviceMac();
	}

	public static void toastShow(String arg) {
		if (toast == null) {
			toast = Toast.makeText(gAppContext, arg, Toast.LENGTH_SHORT);
		} else {
			if (getSDKVersionNumber() <= 14) {
				toast.cancel();
			}
			toast.setText(arg);
		}
		toast.show();
	}

	public static int getSDKVersionNumber() {

		int sdkVersion;

		try {

			sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);

		} catch (NumberFormatException e) {

			sdkVersion = 0;

		}

		return sdkVersion;

	}

	/**
	 * 被踢下线提示框(系统)
	 * 
	 * @param pMsg
	 */
	public static void showInvalidSessionKeyMessage(final Context context) {
		Activity activity = ((Activity) context);
		while (activity.getParent() != null) {
			activity = activity.getParent();
		}

		if(activity != null){
			AlertDialog.Builder builder = new Builder(activity);
			builder.setMessage(context.getResources().getString(R.string.invalidSessionKey));
			builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					LoginManager.getInstance(context).mOnlineStatus = OnlineStatus.OFFLINE;
					
					//发送下线广播
					Intent intentoffline = new Intent(ActivityActionDefine.LOGIN_STATUS_CHANGED);
					context.sendBroadcast(intentoffline);
					
					//发送弹出登录界面广播
					Intent intentologin = new Intent(ActivityActionDefine.SHOWLOGIN_ACTION);
					context.sendBroadcast(intentologin);

					//
//					Intent intent = new Intent(context, LoginActivity.class);
//					context.startActivity(intent);
					// 关闭当前activity，防止和IOS等刷新异常问题出现
					((Activity) context).finish();
					dialog.dismiss();
				}
			});

			if(builder !=null)
				builder.create().show();
		}
		
	}

//	private static ProgressDialog progressDlg;

//	/**
//	 * 显示全屏的LOADING框
//	 * 
//	 * @param context
//	 */
//	public static void showLoading(Context context) {
//		if (progressDlg == null) {
//			progressDlg = new ProgressDialog(context);
//		}
//
//		progressDlg.setMessage("loading...");
//		progressDlg.setCancelable(false);
//		progressDlg.show();
//	}
//
//	public static void hideLoading() {
//		if (progressDlg != null) {
//			progressDlg.dismiss();
//		}
//	}

	/**
	 * 保存新建通告所添加的附件 (有些机器的Intent不能保存太大的数据,保存到全局变量中作传递. 使用后要.clear())
	 */
	public static ArrayList<Attachment> mListAttachmentData = new ArrayList<EventDraftItem.Attachment>();
}
