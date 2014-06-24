package com.drcom.drpalm.Tool.request;


import java.util.HashMap;

import org.json.JSONException;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.service.DrServiceJni;
import com.drcom.drpalm.Tool.service.DrServiceJniCallback;
import com.drcom.drpalm.Tool.service.RequestParse;
import com.drcom.drpalm4tianzhujiao.R;

public class OrganizationRequest extends Request {
	private DrServiceJni mDrServiceJni;
	protected String mSchoolId = "";
	protected String mSessionKey = "";
	public OrganizationRequest(String domain, String schoolID, String sessionKey){
		super();
		mDomain = domain;
		mSchoolId = schoolID;
		mSessionKey = sessionKey;
		mDrServiceJni = new DrServiceJni();
		mDrServiceJni.NativeInit();
	}
	
	
	public boolean GetOrganization(String lastupdate, final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				RequestParse parse = new RequestParse(new String(data));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					OrganizationParse orgParse = new OrganizationParse(map);
					if(orgParse.parseOperate()){
						callback.onSuccess(orgParse);
					}
					else{
						String err = orgParse.parseErrorCode();
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
		return mDrServiceJni.GetOrganization(mDomain, mSchoolId, mSessionKey, lastupdate, jnicallback);

	}
	public boolean SearchStudent(Integer limit, String name, String start, String ordering, final RequestCallback callback){
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(OrganizationDefine.STUDENT_OPTION, OrganizationDefine.STUDENT_OPTION_VALUE);
		parameters.put(OrganizationDefine.STUDENT_VIEW, OrganizationDefine.STUDENT_VIEW_VALUE);
		parameters.put(OrganizationDefine.STUDENT_LIMIT, String.valueOf(limit));
		parameters.put(OrganizationDefine.STUDENT_SEARCHNAME, name);
		parameters.put(OrganizationDefine.STUDENT_START, String.valueOf(start));
		parameters.put(OrganizationDefine.STUDENT_ORDERING, ordering);
		return requestRaw(OrganizationDefine.STUDENT_PATH, parameters, callback);
	}

	public boolean GetParentOrganization(Integer id, final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				RequestParse parse = new RequestParse(new String(data));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					OrganizationParse orgParse = new OrganizationParse(map);
					if(orgParse.parseOperate()){
						callback.onSuccess(orgParse);
					}
					else{
						String err = orgParse.parseErrorCode();
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
		String strOrgid = String.valueOf(id);
		return true;
//		return mDrServiceJni.GetParentOrganization(mDomain, mSchoolId, mSessionKey, strOrgid, jnicallback);
	}
};

