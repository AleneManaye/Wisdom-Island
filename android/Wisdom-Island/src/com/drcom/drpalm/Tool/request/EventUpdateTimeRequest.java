package com.drcom.drpalm.Tool.request;

import java.util.HashMap;

import org.json.JSONException;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.service.DrServiceJni;
import com.drcom.drpalm.Tool.service.DrServiceJniCallback;
import com.drcom.drpalm.Tool.service.RequestParse;
import com.wisdom.island.R;

public class EventUpdateTimeRequest extends Request{
	
	private DrServiceJni mDrServiceJni;
	protected String mSchoolId = "";
	protected String mSessionKey = "";
	public EventUpdateTimeRequest(String domain, String schoolID,String sessionKey){
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
	
	public boolean GetEventsLastUpdate(final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback() {

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
					NewsUpdateTimeParse newsParse = new NewsUpdateTimeParse(map);
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
				System.err.println();
			}

			@Override
			public void onError(byte[] value) {
				// TODO Auto-generated method stub
//				callback.onError(new String(value));
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
		};
		return mDrServiceJni.GetLastUpdate(mDomain, mSchoolId, mSessionKey, GlobalVariables.Devicdid,true, jnicallback);
	}
}
