package com.drcom.drpalm.Tool.jsonparser;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.drcom.drpalm.Tool.DrCOMWS.DrAES128;
import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.RequestDefine;
import com.drcom.drpalm.Tool.service.DrServiceJni;
import com.drcom.drpalm.objs.BindaccountItem;
import com.drcom.drpalm.objs.BindaccountResultItem;
/**
 * 
 * @author hzy
 *{“opret”:{“opflag”:”1”,”code”:””},
 *“accounts”:[{”account”:”帐号名”,“name”: “用户名称”,“password”: “密码”, 
 *“schoolkey”:”学校标识key” ,“schoolname”:”学校名称”,“badge”:”未读消息数”}],
 *“challange”:“密码挑战值”}
 */

public class BindaccountListParser implements IParser {

	@Override
	public Object parser(JSONObject json) {
		// TODO Auto-generated method stub
		Log.i("hzy", "json="+json);
		if (json != null) {
			BindaccountResultItem  resultItem = new BindaccountResultItem();
			
			try {
				JSONObject opret = json.getJSONObject("opret");
				int opflag = opret.getInt("opflag");
				if (opflag == 1) {

					JSONArray accounts = json.getJSONArray("accounts");
					resultItem.result = true;
					for (int i = 0; i < accounts.length(); i++) {
						
						JSONObject account = accounts.getJSONObject(i);
						BindaccountItem bindaccount = new BindaccountItem();
						if(account.has("account")){
							bindaccount.pub_account = account.getString("account");	
						}
						if(account.has("name")){
							bindaccount.pub_name = account.getString("name");
						}
						if(account.has("password")){
							bindaccount.pub_password = account.getString("password");
							
							if(json.has("challange")){
								DrServiceJni jni  = new DrServiceJni();
								String challange = json.getString("challange");
								String str = jni.AesDecrypt(bindaccount.pub_password, challange+"@DrCOM");
								bindaccount.pub_password = str;
								Log.i("hzy", "解密为="+str);
							}
						}
						if(account.has("schoolkey")){
							bindaccount.schoolkey = account.getString("schoolkey");
						}
						if(account.has("schoolid")){
							bindaccount.pub_schoolId = account.getString("schoolid");
						}
						//获取学校名多语言
						if(account.has("schoolname")){
							JSONObject languagelist = account.getJSONObject("schoolname");	
							if(languagelist.has("zh-hans")){
								bindaccount.pub_schoolName_hans = languagelist.getString("zh-hans");
							}
							if(languagelist.has("zh-hant")){
								bindaccount.pub_schoolName_hant = languagelist.getString("zh-hant");
							}
							if(languagelist.has("en")){
								bindaccount.pub_schoolName_en = languagelist.getString("en");
							}
						}
						
						if(account.has("gwip")){
							bindaccount.gwip = account.getString("gwip");
						}
						if(account.has("gwport")){
							bindaccount.gwport = account.getString("gwport");
						}
						//2.3.1.12 获取帐号信息接口的三个与列表不同字段
						if(account.has("badge")){
							bindaccount.pub_image_number = account.getString("badge");
						}
						if(account.has("headimgurl")){
							bindaccount.pub_imgaeurl = account.getString("headimgurl");
						}
						if(account.has("headimglastupdate")){
							bindaccount.headimglastupdate = account.getString("headimglastupdate");
						}
						resultItem.mBindaccountItem.add(bindaccount);
					}
					if(json.has("challange")){
						//这里要做pwd解密等问题。（未做）
						String challange = json.getString("challange");
					}

				}else {
					resultItem.result = false;
					resultItem.errorcode = opret.getString("code");
					resultItem.errordes =  BaseParse
								.getErrorString(resultItem.errorcode);
					Log.i("hzy", "errorcode="+resultItem.errorcode);
				}
				return resultItem;
			} catch (Exception e) {

				Log.i("hzy", "ResultParser Exception:" + e.toString());
				resultItem.result = false;
				resultItem.errorcode = RequestDefine.JsonParserError;
				resultItem.errordes =  BaseParse
						.getErrorString(RequestDefine.JsonParserError);
				Log.i("hzy", "errorcode="+resultItem.errorcode);
				return resultItem; 
			}
		}
		return null;
	}

}
