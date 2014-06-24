package com.drcom.drpalm.Tool.request;

import java.io.InputStream;
import java.util.HashMap;

import com.drcom.drpalm.Tool.service.ItemDataTranslate;
import com.drcom.drpalm.objs.PushMessageItem;

public class PushParse {
	protected InputStream mStream = null;
	protected HashMap<String, Object> mHashMap = null;
	public PushParse(HashMap<String, Object> map){
		mHashMap = map;
	}

	public PushMessageItem parseMessageBody() {
		PushMessageItem item = new PushMessageItem();
		if(mHashMap.containsKey(PushDefine.PUSH_BODY)) {
			try{
				HashMap<String,Object> bodyMap = ((HashMap<String,Object>)mHashMap.get(PushDefine.PUSH_BODY));
				if(bodyMap.containsKey(PushDefine.PUSH_GET_APS)){
					HashMap<String,Object> apsMap = ((HashMap<String,Object>)bodyMap.get(PushDefine.PUSH_GET_APS));
					if(apsMap.containsKey(PushDefine.PUSH_GET_ALERT)){
						item.alert = (String)apsMap.get(PushDefine.PUSH_GET_ALERT);
					}
					if(apsMap.containsKey(PushDefine.PUSH_GET_BADGE)){
						 String value = (String)apsMap.get(PushDefine.PUSH_GET_BADGE);
						 item.badge = ItemDataTranslate.String2Intger(value);
					}
					if(apsMap.containsKey(PushDefine.PUSH_GET_SOUND)){
						item.sound = (String)apsMap.get(PushDefine.PUSH_GET_SOUND);
					}
				}
				if(bodyMap.containsKey(PushDefine.PUSH_GET_ETYPE)){
					String value = (String)bodyMap.get(PushDefine.PUSH_GET_ETYPE);
					item.etype = ItemDataTranslate.String2Intger(value);
				}
			}catch(Exception e){

			}

		}
		return item;
	}
	public String parseTokenId() {
		if(mHashMap.containsKey(PushDefine.PUSH_TOKENID)) {
			return ((String)mHashMap.get(PushDefine.PUSH_TOKENID)).trim();
		}
		return "";
	}
	public String parseChallenge() {
		if(mHashMap.containsKey(PushDefine.PUSH_CHANLLENGE)) {
			return ((String)mHashMap.get(PushDefine.PUSH_CHANLLENGE)).trim();
		}
		return "";
	}
	public String parseErrorCode(){
		String errCode = null;
		String errString = "";
		if(mHashMap.containsKey(PushDefine.PUSH_RET)) {
			try{
				errCode = ((String)mHashMap.get(PushDefine.PUSH_RET)).trim();
				if(errCode.equals(PushDefine.Succeed)){
					errString = "Succeed";
				}else if(errCode.equals(PushDefine.ServerRejectRequest)){
					errString = "ServerRejectRequest";
				}
				else if(errCode.equals(PushDefine.DataFormatIncorrect)){
					errString = "DataFormatIncorrect";
				}
				else if(errCode.equals(PushDefine.CheckError)){
					errString = "CheckError";
				}
				else if(errCode.equals(PushDefine.DataOverflow)){
					errString = "DataOverflow";
				}
			}catch (Exception e){

			}
		}
		return errString;
	}
	public boolean parseOperate(){
		boolean bFlag = false;
		if(mHashMap.containsKey(PushDefine.PUSH_RET)) {
			try{
				String retString = ((String)mHashMap.get(PushDefine.PUSH_RET)).trim();
				if(retString.equals(PushDefine.Succeed)){
					bFlag = true;
				}
				else{
					bFlag = false;
				}
			}catch (Exception e){
				bFlag = false;
			}
		}
		return bFlag;
	}
	public String parseUpgradeVer(){
		String value = "";
		if(mHashMap.containsKey(PushDefine.PUSH_BODY)) {
			try{
				Object obj = mHashMap.get(PushDefine.PUSH_BODY);
				if(null != obj){
					// must sure the type of the object, else throw a exception
					String strClass = obj.getClass().getName();
					if(!String.class.getName().equals(strClass)){
						HashMap<String,Object> upgradeMap = (HashMap<String,Object>)obj;
						if(upgradeMap.containsKey(PushDefine.PUSH_UPGRADE_VER)) {
							 value = ((String)upgradeMap.get(PushDefine.PUSH_UPGRADE_VER)).trim();
						}
					}
				}
			}catch(Exception e) {

			}
		}
		return value;
	}
	public String parseUpgradeUrl(){
		String value = "";
		if(mHashMap.containsKey(PushDefine.PUSH_BODY)) {
			try{
				Object obj = mHashMap.get(PushDefine.PUSH_BODY);
				if(null != obj){
					// must sure the type of the object, else throw a exception
					String strClass = obj.getClass().getName();
					if(!String.class.getName().equals(strClass)){
						HashMap<String,Object> upgradeMap = (HashMap<String,Object>)obj;
						if(upgradeMap.containsKey(PushDefine.PUSH_UPGRADE_URL)) {
							 value = ((String)upgradeMap.get(PushDefine.PUSH_UPGRADE_URL)).trim();
						}
					}
				}
			}catch(Exception e) {

			}
		}
		return value;
	}
	public String parseUpgradeDes(){
		String value = "";
		if(mHashMap.containsKey(PushDefine.PUSH_BODY)) {
			try{
				Object obj = mHashMap.get(PushDefine.PUSH_BODY);
				if(null != obj){
					// must sure the type of the object, else throw a exception
					String strClass = obj.getClass().getName();
					if(!String.class.getName().equals(strClass)){
						HashMap<String,Object> upgradeMap = (HashMap<String,Object>)obj;
						if(upgradeMap.containsKey(PushDefine.PUSH_UPGRADE_DES)) {
							 value = ((String)upgradeMap.get(PushDefine.PUSH_UPGRADE_DES)).trim();
						}
					}
				}
			}catch(Exception e) {

			}
		}
		return value;
	}
}
