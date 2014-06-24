package com.drcom.drpalm.Tool.request;

import java.util.HashMap;

import org.json.JSONException;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.service.DrServiceJni;
import com.drcom.drpalm.Tool.service.DrServiceJniCallback;
import com.drcom.drpalm.Tool.service.RequestParse;
import com.drcom.drpalm.objs.ConsultDraftItem;
import com.drcom.drpalm4tianzhujiao.R;

public class NurseryRequest extends Request{
	protected String mSchoolId = "";
	protected String mSessionKey = "";
	private DrServiceJni mDrServiceJni;
	public NurseryRequest(String domain, String schoolID){
		super();
		setDomain(domain);
		setSchoolId(schoolID);
		mDomain = domain;
		mSchoolId = schoolID;
		mDrServiceJni = new DrServiceJni();
		mDrServiceJni.NativeInit();
	}
	public boolean PutConSult(ConsultDraftItem item, final ViewRequestCallback callback){
		DrServiceJniCallback jniHttpCallback = new DrServiceJniCallback(){
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
					NewsParse newsParse = new NewsParse(map);
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
		String username = "";
		String email = "";
		String phone = "";
		String title = "";
		String content = "";
		String type = "";
		if(null != item.username){
			username = item.username;
		}
		if(null != item.email){
			email = item.email;
		}
		if(null != item.phone){
			phone = item.phone;
		}
		if(null != item.title){
			title = item.title;
		}
		if(null != item.content){
			content = item.content;
		}
		if(null != item.type){
			type = item.type;
		}
		return mDrServiceJni.PutConSult(mDomain, mSchoolId, username, email, phone,title, content, type, jniHttpCallback);
	}
}
