package com.drcom.drpalm.Tool.nettool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;

public class NetStatusManager {
	private Context mContext;
	
	static private NetStatusManager mStatusManager = null;
	public static NetStatusManager getSettingManager(Context context)
	{
		if(null == mStatusManager){
			mStatusManager = new NetStatusManager(context);
		}
    	return mStatusManager;
	}
	
	public NetStatusManager(Context context){
		mContext = context;
	}
	

	/**
	 * 网络类型
	 * @author zhaojunjie
	 *
	 */
	public enum NetType{
		WIFI,
		GPS,
		NOTCONNECT
	}
	
	/**
     * 网络是否连接
     * @param context
     * @return
     */
    public NetType GetNetType()
    {
//    	boolean success = false;
		//获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState(); // 获取网络连接状态
		if (State.CONNECTED == state) { // 判断是否正在使用WIFI网络
			Log.d("DrPalmPushService:IsNetUsed", "正在使用WIFI网络");
//			writeLog("正在使用WIFI网络");
//			success = true;
			return NetType.WIFI;
		}

		try{
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState(); // 获取网络连接状态
			if (State.CONNECTED == state) { // 判断是否正在使用GPRS网络
				Log.d("DrPalmPushService:IsNetUsed", "正在使用GPRS网络");
//				writeLog("正在使用GPRS网络");
//				success = true;
				return NetType.GPS;
			}
		}catch (Exception e) {
			//没SIM卡的机可能会报空指针错误
			return NetType.NOTCONNECT;
		}

//		if(success == false){
			Log.d("DrPalmPushService:IsNetUsed", "没有网络");
//			writeLog("没有网络");
			return NetType.NOTCONNECT;
//		}

//		return success;
    }
}
