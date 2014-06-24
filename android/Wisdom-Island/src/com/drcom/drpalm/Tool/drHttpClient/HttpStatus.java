package com.drcom.drpalm.Tool.drHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class HttpStatus {
	public static int STATUS_NOCONNECT = -1;	//未连接
	public static int STATUS_WIFI = 1;
	public static int STATUS_GPRS = 2;
	
    /**
     * 网络是否连接
     * @param context
     * @return
     */
    public static int IsNetUsed(Context context)
    {
    	int status = STATUS_NOCONNECT; 
		//获得网络连接服务 
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE); 
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState(); // 获取网络连接状态 
		if (State.CONNECTED == state) { // 判断是否正在使用WIFI网络 
			status = STATUS_WIFI; 
		} 
		
		//没SIM卡的机可能会报空指针错误
		try{
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState(); // 获取网络连接状态 
			if (State.CONNECTED == state) { // 判断是否正在使用GPRS网络 
				status = STATUS_GPRS; 
			} 
		}catch(Exception e){
			
		}
		
		
		return status;
    }
}
