package com.drcom.drpalm.Tool.request;

import java.util.HashMap;

import org.json.JSONException;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.service.DrServiceJni;
import com.drcom.drpalm.Tool.service.DrServiceJniCallback;
import com.drcom.drpalm.Tool.service.RequestParse;
import com.drcom.drpalm4tianzhujiao.R;

public class NavigationRequest extends Request{
	
	private DrServiceJni mDrServiceJni;
	public NavigationRequest(){
		super();
		mDrServiceJni = new DrServiceJni();
		mDrServiceJni.NativeInit();
	}
	
	public boolean GetSchoolList(String domain, String parentid, final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
//				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				
				
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try{
					map = parse.getHashMap();
					NavigationParse commonParse = new NavigationParse(map);
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
		
		String numno = "1";	//客户端种类ID（Android：1，iPhone：10；iPad：20，Web：30，PC：40）（可无）
		
		return mDrServiceJni.GetSchoolList(domain, parentid,numno,GlobalVariables.nAppVersion, jnicallback);
	}
	
	public boolean SearchSchool(String domain,String appid, String searchkey, final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try{
					map = parse.getHashMap();
					NavigationParse commonParse = new NavigationParse(map);
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
		
		
		return mDrServiceJni.SearchSchool(domain,appid, searchkey,LanguageManagement.getSysLanguageUrlStr(GlobalVariables.gAppContext), jnicallback);
	}
	
	public boolean GetNavigationList(String appid, final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
//				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				
				
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try{
					map = parse.getHashMap();
					NavigationParse commonParse = new NavigationParse(map);
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
		return mDrServiceJni.GetNavigationList(GlobalVariables.gCenterDomain, appid, jnicallback);
	}
}
