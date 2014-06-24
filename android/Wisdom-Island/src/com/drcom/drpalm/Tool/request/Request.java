package com.drcom.drpalm.Tool.request;
import java.io.InputStream;
import java.util.Map;

import android.util.Log;

import com.drcom.drpalm.Tool.MobileWebApi;
public class Request {

	protected MobileWebApi mWebApi = null;
	protected ViewRequestCallback mViewCallback;
	protected String mDomain = "";
	protected String mBasePath = "";
	public Request(){
		mWebApi = new MobileWebApi();
	}
	public boolean requestRaw(String path, Map<String, String> parameters,final RequestCallback callback) {
		boolean isStarted = mWebApi.requestRaw(path, parameters, new MobileWebApi.RawResponseListener(null, null) {
			@Override
			public void onError() {
				callback.onError();
			}
			@Override
			public void onResponse(InputStream stream) {
				String string = RequestDefine.convertStreamToString(stream);
				Log.d("Request", "onResponse: " + string);
				callback.onResponse(string);
			}
		});
		return isStarted;
	}

	public void setBasePath(String strPath){
		mWebApi.setBasePath(strPath);
	}
	public void setSchoolId(String id){
		mWebApi.setSchoolId(id);
	}
	public void setDomain(String domain){
		mWebApi.setDomain(domain);
	}
	public void setSessionKey(String sessionKey){
		mWebApi.setSessionKey(sessionKey);
	}
	public void setPost(boolean bPost){
		mWebApi.setPost(bPost);
	}
	public void setViewCallback(final ViewRequestCallback viewCallback){
		if(null != viewCallback)
			mViewCallback = viewCallback;
	}



}
