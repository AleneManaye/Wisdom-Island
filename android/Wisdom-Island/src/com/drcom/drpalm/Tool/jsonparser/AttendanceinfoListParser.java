package com.drcom.drpalm.Tool.jsonparser;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.RequestDefine;
import com.drcom.drpalm.objs.AttendanceinfoItem;
import com.drcom.drpalm.objs.AttendanceinfoListResultItem;


/**
 * 考勤信息列表
 * @author zjj
 *
 */
public class AttendanceinfoListParser implements IParser {
	private String mUsername;
	public void SetUsername(String usermane){
		mUsername = usermane;
	}

	@Override
	public Object parser(JSONObject json) {
		if (json != null) {
			AttendanceinfoListResultItem resultModule = new AttendanceinfoListResultItem();
			try {
				JSONObject opret = json.getJSONObject("opret");
				int opflag = opret.getInt("opflag");
				if (opflag == 1) {
					resultModule.result = true;
					
					JSONArray flArray = json.getJSONArray("attendances");
					for (int i = 0; i < flArray.length(); i++) {
						JSONObject attendanceItemJsonObj = (JSONObject) flArray.get(i);
						AttendanceinfoItem fi = new AttendanceinfoItem();
						fi.id = attendanceItemJsonObj.getString("id");
						fi.attendancetime = attendanceItemJsonObj.getString("attendancetime");
						
						if(attendanceItemJsonObj.has("status"))
							fi.status = attendanceItemJsonObj.getString("status");
						fi.mUsername = mUsername;
						
						JSONArray recordArray = attendanceItemJsonObj.getJSONArray("records");
						for (int j = 0; j < recordArray.length(); j++) {
							JSONObject recordJsonObj = (JSONObject) recordArray.get(j);
							AttendanceinfoItem.Records record = new AttendanceinfoItem.Records();
							record.id = recordJsonObj.getString("recid");
							record.time = recordJsonObj.getString("time");
							record.status = recordJsonObj.getString("status");
							record.des = recordJsonObj.getString("des");
							
							record.mUsername = mUsername;
							fi.mRecordslist.add(record);
						}
						
						resultModule.mAttendancelist.add(fi);
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
