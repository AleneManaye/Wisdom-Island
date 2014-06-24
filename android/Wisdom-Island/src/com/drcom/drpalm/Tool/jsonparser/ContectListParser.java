package com.drcom.drpalm.Tool.jsonparser;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.RequestDefine;
import com.drcom.drpalm.objs.ContactItem;
import com.drcom.drpalm.objs.ContectlistResultItem;
import com.drcom.drpalm.objs.FavItem;
import com.drcom.drpalm.objs.FavlistResultItem;


/**
 * 家园桥联系人列表
 * @author lenovo123
 *
 */
public class ContectListParser implements IParser {
	private String mUsername;
	public void SetUsername(String usermane){
		mUsername = usermane;
	}

	@Override
	public Object parser(JSONObject json) {
		if (json != null) {
			ContectlistResultItem resultModule = new ContectlistResultItem();
			try {
				JSONObject opret = json.getJSONObject("opret");
				int opflag = opret.getInt("opflag");
				if (opflag == 1) {
					resultModule.result = true;
					
					JSONArray flArray = json.getJSONArray("contacts");
					for (int i = 0; i < flArray.length(); i++) {
						JSONObject itemJsonObj = (JSONObject) flArray.get(i);
						ContactItem fi = new ContactItem();
						fi.cnid = itemJsonObj.getString("cid");
						fi.cnname = itemJsonObj.getString("cname");
						fi.unread = itemJsonObj.getString("unread");
						fi.lastupdate = DateFormatter.getDateFromSecondsString(itemJsonObj.getString("lastupdate"));
						fi.user = mUsername;
						if(itemJsonObj.has("headimgurl"))
							fi.headimgurl = itemJsonObj.getString("headimgurl");
						
						if(itemJsonObj.has("headimglastupdate"))
							fi.headimglastupdate = DateFormatter.getDateFromSecondsString(itemJsonObj.getString("headimglastupdate"));
						
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
