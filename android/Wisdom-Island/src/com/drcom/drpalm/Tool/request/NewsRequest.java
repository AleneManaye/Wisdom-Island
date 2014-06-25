package com.drcom.drpalm.Tool.request;

import java.util.HashMap;

import org.json.JSONException;

import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.service.DrServiceJni;
import com.drcom.drpalm.Tool.service.DrServiceJniCallback;
import com.drcom.drpalm.Tool.service.RequestParse;
import com.wisdom.island.R;

public class NewsRequest extends Request {
	public static enum NewsRequestType {
		SEARCH
	};

	public static final int FETCH_SUCCESSFUL = 1;
	public static final int FETCH_FAILED = 2;
	protected String mSchoolId = "";
	protected String mSessionKey = "";
	private DrServiceJni mDrServiceJni;

	public NewsRequest(String domain, String schoolID) {
		super();
		setDomain(domain);
		setSchoolId(schoolID);
		mDomain = domain;
		mSchoolId = schoolID;
		mDrServiceJni = new DrServiceJni();
		mDrServiceJni.NativeInit();
	}

	public void setSessionKey(String sessionKey) {
		mSessionKey = sessionKey;
	}

	public String getSessionKey() {
		return mSessionKey;
	}

	public boolean SearchNews(String start, final String searchkey, final ViewRequestCallback callback) {
		DrServiceJniCallback jnicallback = new DrServiceJniCallback() {

			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {// -17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				if (iEnd <= iBegin) {
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
					if (newsParse.parseOperate()) {
						callback.onSuccess(newsParse);
					} else {
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
		return mDrServiceJni.SearchNews(mDomain, mSchoolId, searchkey, start, jnicallback);
	}

	public boolean GetNews(final Integer category, String lastupdate, final ViewRequestCallback callback) {
		DrServiceJniCallback jnicallback = new DrServiceJniCallback() {

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
				// callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {// -17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				
				if (iEnd <= iBegin) {
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
					if (newsParse.parseOperate()) {
						callback.onSuccess(newsParse);
					} else {
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
		String strCategory = Integer.toString(category);
		return mDrServiceJni.GetNews(mDomain, mSchoolId, strCategory, lastupdate, jnicallback);
	}

	public boolean GetNewsDetail(final Integer story_id, int allfield, final ViewRequestCallback callback) {
		DrServiceJniCallback jnicallback = new DrServiceJniCallback() {
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
				// callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length, start = 0, end = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {// -17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				Log.i("xpf", "size=" + value.length);
				Log.i("xpf", "start" + iBegin);
				Log.i("xpf", "end=" + iEnd);
				byte[] valueN = new byte[end - start + 1];
				System.arraycopy(value, start, valueN, 0, end - start);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try {

					map = parse.getHashMap();
					NewsParse newsParse = new NewsParse(map);
					if (newsParse.parseOperate()) {
						callback.onSuccess(newsParse);
					} else {
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
		String strStoryId = Integer.toString(story_id);
		String field = String.valueOf(allfield);
		return mDrServiceJni.GetNewsDetail(mDomain, mSchoolId, strStoryId, field, jnicallback);
	}

};
