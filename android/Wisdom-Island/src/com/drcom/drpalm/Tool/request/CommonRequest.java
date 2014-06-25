package com.drcom.drpalm.Tool.request;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.service.DrServiceJni;
import com.drcom.drpalm.Tool.service.DrServiceJniCallback;
import com.drcom.drpalm.Tool.service.RequestParse;
import com.wisdom.island.R;
import org.json.JSONException;

import java.util.HashMap;

public class CommonRequest extends Request {

	private DrServiceJni mDrServiceJni;
	protected String mSchoolId = "";
	protected String mSessionKey = "";
	public CommonRequest(String domain, String schoolID, String sessionKey){
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
	public boolean KeepAlive(final ViewRequestCallback callback){
		setPost(false);
		return requestRaw(RequestDefine.KEEP_PATH, null, new RequestCallback(){

			@Override
			public void onError() {
				// TODO Auto-generated method stub
				callback.onError("手机连上网络了吗?");
			}

			@Override
			public void onResponse(String string) {
				// TODO Auto-generated method stub
				try{
					RequestParse parse = new RequestParse(string);
					HashMap<String, Object> map = parse.getHashMap();
					CommonParse commonParse = new  CommonParse(map);
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

		});
	}
	public boolean Logout(final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				try
				{
					RequestParse parse = new RequestParse(new String(data));
					HashMap<String, Object> map = parse.getHashMap();
					CommonParse pushInfoParse = new CommonParse(map);
					if(pushInfoParse.parseOperate()){
						callback.onSuccess(pushInfoParse);
					}
					else
					{
						String err = pushInfoParse.parseErrorCode();
						callback.onError(err);
					}
				}
				catch(JSONException e){
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return mDrServiceJni.Logout(mDomain, mSchoolId, mSessionKey, jnicallback);
	}
	public boolean PushInfo(boolean bPush, boolean bSound, boolean bShake, String time, final ViewRequestCallback callback){

		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				try
				{
					RequestParse parse = new RequestParse(new String(data));
					HashMap<String, Object> map = parse.getHashMap();
					CommonParse pushInfoParse = new CommonParse(map);
					if(pushInfoParse.parseOperate()){
						callback.onSuccess(pushInfoParse);
					}
					else
					{
						String err = pushInfoParse.parseErrorCode();
						callback.onError(err);
					}
				}
				catch(JSONException e){
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return mDrServiceJni.PushInfo(mDomain, mSchoolId, mSessionKey, bPush, bSound, bShake, time, jnicallback);
	}
	
	public boolean SubmitProblem(String problem,String suggestion,final ViewRequestCallback callback)
	{
		DrServiceJniCallback jnicallback = new DrServiceJniCallback() {
			
			@Override
			public void onSuccess(byte[] value) {
				// TODO Auto-generated method stub
				try
				{
					RequestParse parse = new RequestParse(new String(value));
					HashMap<String, Object> map = parse.getHashMap();
					CommonParse pushInfoParse = new CommonParse(map);
					if(pushInfoParse.parseOperate()){
						callback.onSuccess(pushInfoParse);
					}
					else
					{
						String err = pushInfoParse.parseErrorCode();
						callback.onError(err);
					}
				}
				catch(Exception e){
					callback.onError("");
				}
			}
			
			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(byte[] value) {
				// TODO Auto-generated method stub
//				callback.onError(new String(value));
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
		};
		return mDrServiceJni.SubmitProblem(mDomain, mSchoolId, mSessionKey, problem, suggestion, jnicallback);
	}
}
