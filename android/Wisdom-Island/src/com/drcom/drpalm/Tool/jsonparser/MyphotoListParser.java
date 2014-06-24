package com.drcom.drpalm.Tool.jsonparser;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.RequestDefine;
import com.drcom.drpalm.objs.MyPhoto;
import com.drcom.drpalm.objs.MyphotolistResultItem;


/**
 * 收藏列表
 * @author lenovo123
 *
 */
public class MyphotoListParser implements IParser {
	private String mUsername;
	public void SetUsername(String usermane){
		mUsername = usermane;
	}

	@Override
	public Object parser(JSONObject json) {
		if (json != null) {
			MyphotolistResultItem resultModule = new MyphotolistResultItem();
			try {
				JSONObject opret = json.getJSONObject("opret");
				int opflag = opret.getInt("opflag");
				if (opflag == 1) {
					resultModule.result = true;
					
					try{
						JSONArray flArray = json.getJSONArray("albumlist");
						for (int i = 0; i < flArray.length(); i++) {
							JSONObject myphotoJsonObj = (JSONObject) flArray.get(i);
							MyPhoto mp = new MyPhoto();
							mp.imgid = myphotoJsonObj.getString("imgid");
							mp.url = myphotoJsonObj.getString("imgurl");
							mp.des = myphotoJsonObj.getString("des");
							mp.status = myphotoJsonObj.getString("status");
							
							mp.lastupdatetime = json.getString("lastupdate");
							mp.username = mUsername;
							
							resultModule.mMyPhotolist.add(mp);
						}
					}catch (Exception e) {
						// TODO: handle exception
						//没图片
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
