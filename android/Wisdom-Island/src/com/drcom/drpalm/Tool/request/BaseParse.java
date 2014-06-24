package com.drcom.drpalm.Tool.request;

import java.util.HashMap;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm4tianzhujiao.R;

public class BaseParse {
	// Protocol
	public static final String OPRET = "opret";
	public static final String OPFLAG = "opflag";
	public static final String CODE = "code";
	
	protected HashMap<String, Object> mHashMap = null;		
	
	public BaseParse(HashMap<String, Object> map){
		mHashMap = map;
	}
	public boolean parseOperate(){
		boolean bFlag = false;
		if(mHashMap.containsKey(OPRET)) {
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get(OPRET);
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey(OPFLAG)) {
						String str = (String)opMap.get(OPFLAG);
						if(str.contentEquals("1")){
							bFlag = true;
						}
						else
							bFlag = false;
					}
				}	
			}catch (Exception e){
				
			}					
		}
		return bFlag;
	}
	public String parseErrorCode(){
		String errCode = null;
		String errString = GlobalVariables.gAppContext.getString(R.string.ElseError);	//.jni_error);// 
		if(mHashMap.containsKey(OPRET)) {
			try{
				Object obj = (HashMap<String, Object>)mHashMap.get(OPRET);
				if(null != obj){
					HashMap<String, Object> opMap = (HashMap<String, Object>)obj;
					if(opMap.containsKey(CODE)) {		
						errCode = ((String)opMap.get(CODE)).trim();
						errString = getErrorString(errCode);
					}
				}	
			}catch (Exception e){
				
			}					
		}
		return errString;
	}
	
	/**
	 * 错误代码转文字描述
	 * @param errCode
	 * @return
	 */
	public static String getErrorString(String errCode){
		String errString = "";
		if(errCode.equals(RequestDefine.JsonParserError)){
			errString = GlobalVariables.gAppContext.getString(R.string.JsonParserError);
		}
		else if(errCode.equals(RequestDefine.InvalidSchoolKey)){
			errString = GlobalVariables.gAppContext.getString(R.string.InvalidSchoolKey);
		}
		else if(errCode.equals(RequestDefine.UnopenSchoolGatway)){
			errString = GlobalVariables.gAppContext.getString(R.string.UnopenSchoolGatway);
		}
		else if(errCode.equals(RequestDefine.AccountNotExist)){
			errString = GlobalVariables.gAppContext.getString(R.string.AccountNotExist);
		}
		else if(errCode.equals(RequestDefine.LoginInfoNotMatch)){
			errString = GlobalVariables.gAppContext.getString(R.string.LoginInfoNotMatch);
		}
		else if(errCode.equals(RequestDefine.LoginInfoNoMoney)){
			errString = GlobalVariables.gAppContext.getString(R.string.LoginInfoNoMoney);
		}
		else if(errCode.equals(RequestDefine.LoginParamEmpty)){
			errString = GlobalVariables.gAppContext.getString(R.string.LoginParamEmpty);
		}
		else if(errCode.equals(RequestDefine.InvalidSessionKey)){
			errString = GlobalVariables.gAppContext.getString(R.string.InvalidSessionKey);
		}
		else if(errCode.equals(RequestDefine.OvertimeSessionKey)){
			errString = GlobalVariables.gAppContext.getString(R.string.OvertimeSessionKey);
		}
		else if(errCode.equals(RequestDefine.InvalidSchoolId)){
			errString = GlobalVariables.gAppContext.getString(R.string.InvalidSchoolId);
		}
		else if(errCode.equals(RequestDefine.SchoolIdisEmpty)){
			errString = GlobalVariables.gAppContext.getString(R.string.SchoolIdisEmpty);
		}
		else if(errCode.equals(RequestDefine.NotTeacher)){
			errString = GlobalVariables.gAppContext.getString(R.string.NotTeacher);
		}
		else if(errCode.equals(RequestDefine.OrgNotExist)){
			errString = GlobalVariables.gAppContext.getString(R.string.OrgNotExist);
		}
		else if(errCode.equals(RequestDefine.EventStartDateEmpty)){
			errString = GlobalVariables.gAppContext.getString(R.string.EventStartDateEmpty);
		}
		else if(errCode.equals(RequestDefine.ReceiverIDEmpty)){
			errString = GlobalVariables.gAppContext.getString(R.string.ReceiverIDEmpty);
		}
		else if(errCode.equals(RequestDefine.ReceiverNameEmpty)){
			errString = GlobalVariables.gAppContext.getString(R.string.ReceiverNameEmpty);
		}
		else if(errCode.equals(RequestDefine.EventLocationEmpty)){
			errString = GlobalVariables.gAppContext.getString(R.string.EventLocationEmpty);
		}
		else if(errCode.equals(RequestDefine.EventTypeEmpty)){
			errString = GlobalVariables.gAppContext.getString(R.string.EventTypeEmpty);
		}
		else if(errCode.equals(RequestDefine.EventTitleEmpty)){
			errString = GlobalVariables.gAppContext.getString(R.string.EventTitleEmpty);
		}
		else if(errCode.equals(RequestDefine.EventContentEmpty)){
			errString = GlobalVariables.gAppContext.getString(R.string.EventContentEmpty);
		}
		else if(errCode.equals(RequestDefine.EventUnknowError)){
			errString = GlobalVariables.gAppContext.getString(R.string.EventUnknowError);
		}
		else if(errCode.equals(RequestDefine.EventInvalidEventID)){
			errString = GlobalVariables.gAppContext.getString(R.string.EventInvalidEventID);
		}	
		else if(errCode.equals(RequestDefine.ReplyTitleEmpty)){
			errString = GlobalVariables.gAppContext.getString(R.string.ReplyTitleEmpty);
		}
		else if(errCode.equals(RequestDefine.ReplyContentEmpty)){
			errString = GlobalVariables.gAppContext.getString(R.string.ReplyContentEmpty);
		}	
		else if(errCode.equals(RequestDefine.MailFormatError)){
			errString = GlobalVariables.gAppContext.getString(R.string.MailFormatError);
		}	
		else if(errCode.equals(RequestDefine.OrgIdNotNull)){
			errString = GlobalVariables.gAppContext.getString(R.string.OrgIdNotNull);
		}
		else if(errCode.equals(RequestDefine.OrgResourceNotNew)){
			errString = GlobalVariables.gAppContext.getString(R.string.OrgResourceNotNew);
		}
		else if(errCode.equals(RequestDefine.CategoryIdNotNull)){
			errString = GlobalVariables.gAppContext.getString(R.string.CategoryIdNotNull);
		}
		else if(errCode.equals(RequestDefine.ConsultTelOrMailNotNull)){
			errString = GlobalVariables.gAppContext.getString(R.string.ConsultTelOrMailNotNull);
		}
		else if(errCode.equals(RequestDefine.ConsultContentNotNull)){
			errString = GlobalVariables.gAppContext.getString(R.string.ConsultContentNotNull);
		}
		else if(errCode.equals(RequestDefine.ConsultNameNotNull)){
			errString = GlobalVariables.gAppContext.getString(R.string.ConsultNameNotNull);
		}
		else if(errCode.equals(RequestDefine.AttendanceErrorParam)){
			errString = GlobalVariables.gAppContext.getString(R.string.AttendanceErrorParam);
		}
		else if(errCode.equals(RequestDefine.AttendanceOvertime)){
			errString = GlobalVariables.gAppContext.getString(R.string.AttendanceOvertime);
		}
		else if(errCode.equals(RequestDefine.AttendanceErrorCheck)){
			errString = GlobalVariables.gAppContext.getString(R.string.AttendanceErrorCheck);
		}else if(errCode.equals(RequestDefine.AccountIsRepeatBinding)){
			errString = GlobalVariables.gAppContext.getString(R.string.AccountIsRepeatBinding);
		}
		else{
			if(errCode.length()>0){
				errString = GlobalVariables.gAppContext.getString(R.string.ElseError) + ":" + errCode;
			}
		}
		return errString;
	}
}
