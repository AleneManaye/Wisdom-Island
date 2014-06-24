package com.drcom.drpalm.Tool.request;

import java.util.HashMap;

import org.json.JSONException;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.service.DrServiceJni;
import com.drcom.drpalm.Tool.service.DrServiceJniCallback;
import com.drcom.drpalm.Tool.service.RequestParse;
import com.drcom.drpalm4tianzhujiao.R;

public class LatestEventsRequest extends Request{
	
	private DrServiceJni mDrServiceJni;
	protected String mSchoolId = "";
	protected String mSessionKey = "";
	public LatestEventsRequest(String domain, String schoolID,String sessionKey){
		super();
		setDomain(domain);
		setSchoolId(schoolID);
		setSessionKey(sessionKey);
		mDomain = domain;
		mSchoolId = schoolID;
		mSessionKey = sessionKey;
		mDrServiceJni = new DrServiceJni();
		mDrServiceJni.NativeInit();
	}
	
	public  boolean GetEventsInfoList(final ViewRequestCallback callback){
		DrServiceJniCallback jniHttpCallback = new DrServiceJniCallback(){
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
				try {
					map = parse.getHashMap();
					LatestNewsParse newsParse = new LatestNewsParse(map);
					if(newsParse.parseOperate()){
						callback.onSuccess(newsParse);
					}
					else{
						String err = newsParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return mDrServiceJni.GetEventsModuleInfoList(mDomain, mSchoolId, mSessionKey, jniHttpCallback);
	}
}
