package com.drcom.drpalm.Tool.jsonparser;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.RequestDefine;
import com.drcom.drpalm.objs.FavItem;
import com.drcom.drpalm.objs.FavlistResultItem;


/**
 * 收藏列表
 * @author lenovo123
 *
 */
public class FavListParser implements IParser {
	private String mUsername;
	public void SetUsername(String usermane){
		mUsername = usermane;
	}

	@Override
	public Object parser(JSONObject json) {
		if (json != null) {
			FavlistResultItem resultModule = new FavlistResultItem();
			try {
				JSONObject opret = json.getJSONObject("opret");
				int opflag = opret.getInt("opflag");
				if (opflag == 1) {
					resultModule.result = true;
					
					JSONArray flArray = json.getJSONArray("favoritelist");
					for (int i = 0; i < flArray.length(); i++) {
						JSONObject favItemJsonObj = (JSONObject) flArray.get(i);
						FavItem fi = new FavItem();
						fi.mEventid = favItemJsonObj.getString("eventid");
						fi.mCategroyid = favItemJsonObj.getString("type");
						fi.mStatus = favItemJsonObj.getString("favoritestatus");
						fi.mLastupdatetime = favItemJsonObj.getString("lastupdate");
						fi.mUsername = mUsername;
						
						resultModule.mFavlist.add(fi);
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
