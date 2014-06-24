package com.drcom.drpalm.Tool.jsonparser;

import org.json.JSONObject;

import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.RequestDefine;
import com.drcom.drpalm.objs.LogoutItem;
import android.util.Log;


/**
 * 只返回成功/失败结果解析类
 * @author lenovo123
 *
 */
public class LogoutParser implements IParser {

	@Override
	public Object parser(JSONObject json) {
		if (json != null) {
			LogoutItem resultModule = new LogoutItem();
			try {
				JSONObject opret = json.getJSONObject("opret");
				int opflag = opret.getInt("opflag");
				if (opflag == 1) {
					resultModule.result = true;
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
