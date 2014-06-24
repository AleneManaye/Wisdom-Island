package com.drcom.drpalm.Tool.jsonparser;
import org.json.JSONObject;

import android.util.Log;

import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.RequestDefine;

/**
 * 基本资料Json
 * @author zhr
 */
/**
 * {"opret":{"opflag":"获取结果标志(0：失败，1：成功) ","code":""},"username":"用户名称","level":
 * 用户等级,"levelupscore":升下一级所需的积分,"curscore":用户当前积分,"serviceenddate":使用期限,"headurl"
 * :"用户头像","headlastupdate":头像最后更新时间,"lastupdate":最后一次更新时间}
 **/
public class UserInfoParser implements IParser {
	private String mUsername;

	public void SetUsername(String usermane) {
		mUsername = usermane;
	}

	@Override
	public Object parser(JSONObject json) {
		if (json != null) {
			UserInfo resultModule = new UserInfo();
			try {
				JSONObject opret = json.getJSONObject("opret");
				int opflag = opret.getInt("opflag");
				if (opflag == 1) {
					resultModule.result = true;
					resultModule.strUserNickName = json.getString("username");
					resultModule.level = String.valueOf((json.getInt("level")));
					resultModule.levelupscore = String.valueOf((json.getInt("levelupscore")));
					resultModule.curscore = String.valueOf((json.getInt("curscore")));
					resultModule.serviceenddate = String.valueOf((json.getInt("curscore")));
					resultModule.headurl = json.getString("headurl");
					resultModule.headlastupdate = String.valueOf((json.getInt("headlastupdate")));
					resultModule.lastupdate = String.valueOf((json.getInt("lastupdate")));
					
				} else {
					resultModule.result = false;
					resultModule.errorcode = opret.getString("code");
					resultModule.errordes = BaseParse
							.getErrorString(resultModule.errorcode);
				}
				// 解析json
				return resultModule;
			} catch (Exception e) {
				Log.i("zhr", "ResultParser Exception:" + e.toString());

				resultModule.result = false;
				resultModule.errorcode = RequestDefine.JsonParserError;
				resultModule.errordes = BaseParse
						.getErrorString(RequestDefine.JsonParserError);
				return resultModule;
			}
		}
		return null;
	}
}
