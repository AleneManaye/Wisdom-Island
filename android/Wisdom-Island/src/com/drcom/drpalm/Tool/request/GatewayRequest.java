package com.drcom.drpalm.Tool.request;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;

import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.service.DrServiceJni;
import com.drcom.drpalm.Tool.service.DrServiceJniCallback;
import com.drcom.drpalm.Tool.service.RequestParse;
import com.wisdom.island.R;


public class GatewayRequest extends Request {

	static final String LASTUPDATE_RESOURCE_PREFERENCES_FILE = "LastupdateResourcePreferencesFfile";
	static final String LASTUPDATE_RESOURCE_DATE = "LastupdateResourceDate";

	private DrServiceJni mDrServiceJni;
	public GatewayRequest(){
		super();
		mDrServiceJni = new DrServiceJni();
		mDrServiceJni.NativeInit();
	}
	// Get Gateway IP , port and schoolid
	public boolean isConnected(){
		if("" != GlobalVariables.gGateawayDomain && "" != GlobalVariables.gSchoolId)
			return true;
		else
			return false;
	}
	public boolean GetSchoolKey(String domain, String seqid, final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
			@Override
			public void onSuccess(byte[] data) {
				try{
					RequestParse parse = new RequestParse(new String(data));
					HashMap<String, Object> map = parse.getHashMap();
					GatewayParse commonParse = new GatewayParse(map);
					if(commonParse.parseOperate()){
						callback.onSuccess(commonParse);
					}
					else{
						String err = commonParse.parseErrorCode();
						callback.onError(err);
					}
				}catch(JSONException e){
					callback.onError("JSONException");
				}
			}
			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return true;
//		return mDrServiceJni.GetSchoolKey(domain, seqid, jnicallback);

	}
	public boolean GetNetworkGate(String domain, String schoolKey, final ViewRequestCallback callback){
		setDomain(domain);
		setSchoolId("");
		setBasePath(RequestDefine.NETAPP_PATH);
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
			@Override
			public void onSuccess(byte[] data) {
				try{
					String str = new String(data);
					RequestParse parse = new RequestParse(new String(data));
					HashMap<String, Object> map = parse.getHashMap();
					GatewayParse commonParse = new GatewayParse(map);
					if(commonParse.parseOperate()){
						callback.onSuccess(commonParse);

					}
					else{
						String err = commonParse.parseErrorCode();
						callback.onError(err);
					}
				}catch(JSONException e){
					callback.onError("JSONException");
				}
			}
			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		
		String numno =  "1";	//客户端种类ID（Android：1，iPhone：10；iPad：20，Windows：40）

		return mDrServiceJni.GetNetworkGate(domain, schoolKey,
				numno,
				String.valueOf(GlobalVariables.nDensity),
				String.valueOf(GlobalVariables.nDisplayWidth),
				String.valueOf(GlobalVariables.nDisplayHeight),
				GlobalVariables.getOSVersion(),
				GlobalVariables.getMoelno(),
				GlobalVariables.nAppVersion,
				GlobalVariables.Devicdid,
				GlobalVariables.nPkgname,
				jnicallback);
	}
	public boolean LoginGateway(String domain, String schoolID, String userid, String pass,String identify,String packagename, final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
			@Override
			public void onSuccess(byte[] data) {
				try{
					String str = new String(data);
					RequestParse parse = new RequestParse(new String(data));
					HashMap<String, Object> map = parse.getHashMap();
					GatewayParse loginParse = new  GatewayParse(map);
					if(loginParse.parseOperate()){
						callback.onSuccess(loginParse);
					}
					else{
						String err = loginParse.parseErrorCode();
						callback.onError(err);
					}
				}catch(JSONException e){
					callback.onError("JSONException");
				}
			}
			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}

		};
		return mDrServiceJni.LoginGateway(domain, schoolID, userid, pass, identify, packagename, jnicallback);
	}
	public boolean GetTours(String domain, String schoolID, Date lastUpdateDate, Integer dspwidth,
			Integer dspheight, Integer dpi, String os, String modelno, final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
			@Override
			public void onSuccess(byte[] data) {
				try{
					RequestParse parse = new RequestParse(new String(data));
					HashMap<String, Object> map = parse.getHashMap();
					GatewayParse loginParse = new  GatewayParse(map);
					
					//解释找回密码
					if (loginParse.parsePswUrlOperate()) {
						GlobalVariables.gAccUrl = loginParse.parsePswUrl();
						Log.i("zjj", "找回密码URL:" + GlobalVariables.gAccUrl);
					} else {
						String errorCode = loginParse.parsePswUrlError();
						Log.i("zjj", "找回密码URL错误CODE:" + errorCode);
					}
					
					//解释考勤信息接口地址
					if(loginParse.parseAppAttendanceOperate()){
						GlobalVariables.gAttendanceUrl = loginParse.parseAttendanceUrl();
					}else{
						GlobalVariables.gAttendanceUrl = "";
					}
					Log.i("zjj", "考勤信息URL:" + GlobalVariables.gAttendanceUrl);
					
					//解释资源版本
					if(loginParse.parseAppResourceOperate()){
						callback.onSuccess(loginParse);
					}
					else{
						String err = loginParse.parseToursError();
						callback.onError(err);
					}
					
					
				}catch(JSONException e){
					callback.onError("JSONException");
				}
			}
			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}

		};
		String strLastUpdateDate ="";
		String strNum = "1";
		String strModelno = "";
		String strDspwidth = "";
		String strDspheight = "";
		String strDpi = "";
		String strOS = "";
		if(null != lastUpdateDate){
			Long time = lastUpdateDate.getTime() / 1000;
			strLastUpdateDate = Long.toString(time);
			
			Log.i("zjj", "取更新包 lastUpdateDate:" + strLastUpdateDate);
		}
		if(null != dspwidth){
			strDspwidth =  String.valueOf(dspwidth);
		}
		if(null != dspheight){
			strDspheight =  String.valueOf(dspheight);
		}
		if(null != dpi){
			strDpi = String.valueOf(dpi);
		}
		if(null != os){
			strOS = os;
		}
		if(null != modelno){
			strModelno = modelno;
		}
		return mDrServiceJni.GetTours(domain, schoolID, strLastUpdateDate, strDspwidth, strDspheight, strDpi, strNum, strOS, strModelno, jnicallback);
	}


}
