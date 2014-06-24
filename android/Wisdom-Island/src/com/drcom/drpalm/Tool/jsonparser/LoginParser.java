package com.drcom.drpalm.Tool.jsonparser;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.RequestDefine;
import com.drcom.drpalm.objs.PushSettingInfo;
import com.drcom.drpalm.objs.PushSettingInfo.PushTime;


/**
 * 登录结果
 * @author lenovo123
 *
 */
public class LoginParser implements IParser {

	@Override
	public Object parser(JSONObject json) {
		if (json != null) {
			UserInfo resultModule = new UserInfo();
			try {
				JSONObject opret = json.getJSONObject("opret");
				int opflag = opret.getInt("opflag");
				if (opflag == 1) {
					resultModule.result = true;
					
					resultModule.strUsrType = json.getString("acctype");
					resultModule.sessionKey = json.getString("sessionkey");
					
					if(json.has("sendpermis")){
						if(GlobalVariables.UserSendpermisList != null)
							GlobalVariables.UserSendpermisList.clear();
						
						JSONArray gwArray = json.getJSONArray("sendpermis");
						for (int i = 0; i < gwArray.length(); i++) {
							GlobalVariables.UserSendpermisList.add( gwArray.get(i).toString());
						}
					}
					
					JSONObject opretaccountinfo = json.getJSONObject("accountinfo");
					resultModule.level = opretaccountinfo.getString("level");
					resultModule.levelupscore = opretaccountinfo.getString("levelupscore");
					resultModule.curscore = opretaccountinfo.getString("curscore");
					resultModule.headurl = opretaccountinfo.getString("headurl");
					resultModule.headlastupdate = opretaccountinfo.getString("headlastupdate");
					resultModule.lastupdate = opretaccountinfo.getString("lastupdate");
					resultModule.strUserNickName = json.getString("username");
					
					JSONObject pushopret = json.getJSONObject("pushinfo");
					resultModule.pushSetting.ifpush = pushopret.getInt("ifpush")==1?true:false;
					resultModule.pushSetting.ifsound = pushopret.getInt("ifsound")==1?true:false;
					resultModule.pushSetting.ifshake = pushopret.getInt("ifshake")==1?true:false;
					JSONArray gwArray = pushopret.getJSONArray("pushtime");
					for (int i = 0; i < gwArray.length(); i++) {
						JSONObject pushtimeJsonObject = (JSONObject) gwArray.get(i);
						PushSettingInfo.PushTime pt = new PushTime();
						pt.start = pushtimeJsonObject.getString("start");
						pt.end = pushtimeJsonObject.getString("end");
						
						resultModule.pushSetting.pushTime.add(pt);
					}
				}else{
					resultModule.result = false;
					resultModule.errorcode = opret.getString("code");
					resultModule.errordes = BaseParse.getErrorString(resultModule.errorcode);
				}
				//解析json
				return resultModule;
			} catch (Exception e) {
				Log.i("zjj", "ResultParser Exception:" + e.toString());
				
				resultModule.result = false;
				resultModule.errorcode = RequestDefine.JsonParserError;
				resultModule.errordes = BaseParse.getErrorString(RequestDefine.JsonParserError);
				return resultModule;
			}
		}
		return null;
	}
}
