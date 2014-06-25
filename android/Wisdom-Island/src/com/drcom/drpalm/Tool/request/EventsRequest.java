package com.drcom.drpalm.Tool.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;

import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.CommonTranslate.StringTranslate;
import com.drcom.drpalm.Tool.service.DrServiceJni;
import com.drcom.drpalm.Tool.service.DrServiceJniCallback;
import com.drcom.drpalm.Tool.service.RequestParse;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.wisdom.island.R;

public class EventsRequest extends Request {
	private DrServiceJni mDrServiceJni;
	protected String mSchoolId = "";
	protected String mSessionKey = "";
	protected String mLastReadTime = "";
	public EventsRequest(String domain, String schoolID,String sessionKey){
		super();
		setDomain(domain);
		setSchoolId(schoolID);
		setSessionKey(sessionKey);
		mDomain = domain;
		mSchoolId = schoolID;
		mSessionKey = sessionKey;
		mDrServiceJni = new DrServiceJni();
		mDrServiceJni.NativeInit();
	}

	public boolean GetEventsList(final Integer category_id, String lastupdate, final String lastreadtime, final ViewRequestCallback callback){
		DrServiceJniCallback jniHttpCallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				
				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try {

					map = parse.getHashMap();
					EventsParse eventParse = new EventsParse(map);
					if(eventParse.parseOperate()){
						//补取
						if(eventParse.parseRetainCount()>0){
							ArrayList<EventDetailsItem> list = eventParse.parseEvents();
							Date date = list.get(list.size() - 1).lastupdate;
//							String test_lastupdate = String.valueOf(date.getTime());
							for(EventDetailsItem item : list){
								if((item.lastreadtime.getTime()/1000)>Long.valueOf(mLastReadTime)){
									mLastReadTime = String.valueOf((item.lastreadtime.getTime()/1000));
								}
							}
//							String test_time = mLastReadTime;
							GetEventsList(category_id, String.valueOf(date.getTime()/1000), mLastReadTime, callback);
						}
						callback.onSuccess(eventParse);  
					}
					else{
						String err = eventParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		String category = Integer.toString(category_id);
		mLastReadTime = lastreadtime;
		return mDrServiceJni.GetEventList(mDomain, mSchoolId, mSessionKey, category, lastupdate, lastreadtime, jniHttpCallback);
	}
	
	public  boolean GetEventDetail(String eventid, int allfield, final ViewRequestCallback callback){
		DrServiceJniCallback jniHttpCallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				
				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse newsParse = new EventsParse(map);
					if(newsParse.parseOperate()){
						callback.onSuccess(newsParse);
					}
					else{
						String err = newsParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		String field = String.valueOf(allfield);
		return mDrServiceJni.GetEventDetail(mDomain, mSchoolId, mSessionKey, eventid, field, jniHttpCallback);
	}
	
	public boolean GetPublishEventList(final Integer category_id, String lastupdate, final ViewRequestCallback callback){
		DrServiceJniCallback jniHttpCallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				String test = String.valueOf(value);
				Log.i("zjj", test);
				
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				
				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try {

					map = parse.getHashMap();
					EventsParse eventParse = new EventsParse(map);
					if(eventParse.parseOperate()){
						callback.onSuccess(eventParse);  
					}
					else{
						String err = eventParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		String category = Integer.toString(category_id);
		return mDrServiceJni.GetPublishEventList(mDomain, mSchoolId, mSessionKey, category, lastupdate, jniHttpCallback);
	}
	
	public  boolean GetPublishEventDetail(String eventid, final ViewRequestCallback callback){
		DrServiceJniCallback jniHttpCallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				
				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse newsParse = new EventsParse(map);
					if(newsParse.parseOperate()){
						callback.onSuccess(newsParse);
					}
					else{
						String err = newsParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return mDrServiceJni.GetPublishEventDetail(mDomain, mSchoolId, mSessionKey, eventid, jniHttpCallback);
	}
	
	public boolean GetReplyInfo(String eventid, String awspubid, String lastawstime, final ViewRequestCallback callback){
		DrServiceJniCallback jniHttpCallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				
				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse newsParse = new EventsParse(map);
					if(newsParse.parseOperate()){
						callback.onSuccess(newsParse);
					}
					else{
						String err = newsParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return mDrServiceJni.GetReplyInfo(mDomain, mSchoolId, mSessionKey, eventid, awspubid, lastawstime, jniHttpCallback);
	}
	
	public boolean AutoAnswer(String eventid, final ViewRequestCallback callback){
		DrServiceJniCallback jniHttpCallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				
				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse newsParse = new EventsParse(map);
					if(newsParse.parseOperate()){
						callback.onSuccess(newsParse);
					}
					else{
						String err = newsParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return mDrServiceJni.GetPublishEventDetail(mDomain, mSchoolId, mSessionKey, eventid, jniHttpCallback);
	}
	
	public boolean GetNewEvents(Date startTime, Date endTime, Date startPost, final ViewRequestCallback callback){
		setPost(false);
		String strStart = String.valueOf(startTime.getTime());
		String strEnd =  String.valueOf(endTime.getTime());
		String strPost =  String.valueOf(startPost.getTime());

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put(EventsDefine.EVENTS_START, strStart);
		parameters.put(EventsDefine.EVENTS_END, strEnd);
		parameters.put(EventsDefine.EVENTS_START_POST, strPost);
		return requestRaw(EventsDefine.EVENTS_PATH, parameters, new RequestCallback(){
			@Override
			public void onError() {
				if(null != callback)
					callback.onError("手机连上网络了吗?");
			}
			@Override
			public void onResponse(String string) {
				if(null == callback)
					return;
				try{
					RequestParse parse = new RequestParse(string);
					HashMap<String, Object> map = parse.getHashMap();
					EventsParse eventParse = new EventsParse(map);
					if(eventParse.parseOperate()){
						callback.onSuccess(eventParse);
					}
					else{
						String err = eventParse.parseErrorCode();
						callback.onError(err);
					}
				}catch(JSONException e){
					callback.onError("JSONException");
				}
			}
		});
	}
	public boolean GetObjectType(String languageType, final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
			@Override
			public void onSuccess(byte[] data) {
				RequestParse parse = new RequestParse(new String(data));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse eventParse = new EventsParse(map);
					if(eventParse.parseOperate()){
						callback.onSuccess(eventParse);
					}
					else{
						String err = eventParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}
			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return true;
//		return mDrServiceJni.GetObjectType(mDomain, mSchoolId, mSessionKey, languageType, jnicallback);
	}


	public boolean SubmitEvent(EventDraftItem item, final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
			@Override
			public void onSuccess(byte[] data) {
				RequestParse parse = new RequestParse(new String(data));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse eventParse = new EventsParse(map);
					if(eventParse.parseOperate()){
						callback.onSuccess(eventParse);
					}
					else{
						String err = eventParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}
			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		String id = "";
		String type = "";
		String objtype = "";
		String oristatus = "";
		String ownerid = "";
		String owner = "";
		String start = "";
		String end = "";
		String title = "";
		String shortloc = "_";	//地点不需要
		String body = "";
		String locUrl = "";
		String thumbname = "";
		String bifeshow = "0";
		//String comment = "";
		if(null != item.old_eventid){
			id = Integer.toString(item.old_eventid);
		}
		if(null != item.type){
			type = Integer.toString(item.type);
		}
//		if(null != item.objtype){
//			objtype = Integer.toString(item.objtype);
//		}
		if(null != item.oristatus){
			oristatus = item.oristatus;
		}
		if(null != item.ownerid){
			ownerid = item.ownerid;
		}
		if(null != item.ownerid){
			owner = item.owner;
		}
		if(null != item.start){
			Long time = item.start.getTime() / 1000;
			start = Long.toString(time);

		}
		if(null != item.end){
			Long time = item.end.getTime() / 1000;
			end = Long.toString(time);
		}
//		if(null != item.shortloc){
//			shortloc = item.shortloc;
//		}
		if(null != item.title){
			title = item.title;
		}
		if(null != item.body){
			body = StringTranslate.HTMLChange(item.body);
		}
		bifeshow = (item.bifeshow == true)?"1":"0";
		if(null != item.locUrl){
			locUrl = item.locUrl;
		}
		if(null != item.thumbname){
			thumbname = item.thumbname;
		}

//		if(null != item.comment)
//		{
//			comment = item.comment;
//		}
//		ArrayList<Attachment> fileList = new ArrayList<Attachment>();
//		for(Entry<String, Attachment> entry:item.mAttachmentMap.entrySet()){
//			fileList.add(entry.getValue());
//		}

		return mDrServiceJni.SubmitEvent4Kids(mDomain, mSchoolId, mSessionKey, id, type, objtype, oristatus, ownerid, owner,
				start, end, shortloc, title, body, bifeshow, locUrl, thumbname, item.eventDraftAttachment, jnicallback);
	}

	public boolean AutoAnwserEvent(Integer eventID, final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
			@Override
			public void onSuccess(byte[] data) {
				RequestParse parse = new RequestParse(new String(data));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse eventParse = new EventsParse(map);
					if(eventParse.parseOperate()){
						callback.onSuccess(eventParse);
					}
					else{
						String err = eventParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}
			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		String strEventId = String.valueOf(eventID);
		return true;
//		return mDrServiceJni.AutoAnwserEvent(mDomain, mSchoolId, mSessionKey, strEventId, jnicallback);
	}

	public boolean ReplyPost(int eventid, String aswpubid,
			String replyContent,final ViewRequestCallback callback)
	{
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				RequestParse parse = new RequestParse(new String(data));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse eventParse = new EventsParse(map);
					if(eventParse.parseOperate()){
						callback.onSuccess(eventParse);
					}
					else{
						String err = eventParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		String strEventId = String.valueOf(eventid);
		return mDrServiceJni.ReplyPost(mDomain, mSchoolId, mSessionKey, strEventId, aswpubid, replyContent, jnicallback);
	}

	public boolean GetReplyInfo(int eventid, String aswpubid, long begintime, final ViewRequestCallback callback)
	{
		DrServiceJniCallback jnicallback = new DrServiceJniCallback() {

			@Override
			public void onSuccess(byte[] value) {
				// TODO Auto-generated method stub
				RequestParse parse = new RequestParse(new String(value));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse eventParse = new EventsParse(map);
					if(eventParse.parseOperate()){
						callback.onSuccess(eventParse);
					}
					else{
						String err = eventParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub
				RequestParse parse = new RequestParse(new String(value));
			}

			@Override
			public void onError(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		String strEventId = String.valueOf(eventid);
		String strBegingtime = Long.toString(begintime) ;
		//String stroriawsid  = String.valueOf(oriawsid);
		String strTemplatetype = "0";
		String strTitle = "0";

		return mDrServiceJni.GetReplyInfo(mDomain, mSchoolId, mSessionKey, strEventId, aswpubid, strBegingtime, jnicallback);
	}

	//public boolean AutoAnwserEventList(List<Integer> listId, final ViewRequestCallback callback){
	public boolean AutoAnwserEventList(int[] listId, final ViewRequestCallback callback){
		String value = "";
		if(null == listId)
			return false;
		for(int i = 0; i< listId.length; i++){
			value += listId[i];
			if(i != listId.length - 1){
				value += ",";
			}
		}
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}
			@Override
			public void onSuccess(byte[] data) {
				RequestParse parse = new RequestParse(new String(data));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse eventParse = new EventsParse(map);
					if(eventParse.parseOperate()){
						callback.onSuccess(eventParse);
					}
					else{
						String err = eventParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}
			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return true;
//		return mDrServiceJni.AutoAnwserEventList(mDomain, mSchoolId, mSessionKey, value, jnicallback);
	}
	public boolean GetPublishEvents(Date curpost, final ViewRequestCallback callback){
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				
				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse eventParse = new EventsParse(map);
					if(eventParse.parseOperate()){
						if(eventParse.parseRetainCount()>0){
							ArrayList<EventDetailsItem> list = eventParse.parseEvents();
							Date date = list.get(list.size() - 1).post;
							GetPublishEvents(date, callback);
						}
						callback.onSuccess(eventParse);
					}
					else{
						String err = eventParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		String strPost = "0";
		if(null != curpost)
			strPost =  String.valueOf(curpost.getTime() / 1000);
		return true;
//		return mDrServiceJni.GetPublishEvents(mDomain, mSchoolId, mSessionKey, strPost, jnicallback);
	}

	public boolean GetNewPublishEvents(Date startTime, Date endTime, Date startPost, final ViewRequestCallback callback){
		String strStart = String.valueOf(startTime.getTime());
		String strEnd =  String.valueOf(endTime.getTime());
		String strPost =  String.valueOf(startPost.getTime());
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				RequestParse parse = new RequestParse(new String(data));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse eventParse = new EventsParse(map);
					if(eventParse.parseOperate()){
						if(eventParse.parseRetainCount()>0){
							ArrayList<EventDetailsItem> list = eventParse.parseEvents();
							Date date = list.get(list.size() - 1).post;
							GetPublishEvents(date, callback);
						}
						callback.onSuccess(eventParse);
					}
					else{
						String err = eventParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return true;
//		return mDrServiceJni.GetNewPublishEvents(mDomain, mSchoolId, mSessionKey, strStart, strEnd, strPost, jnicallback);
	}

	public boolean GetEventReadStatus(Integer eventID, final ViewRequestCallback callback){
		setPost(false);
		HashMap<String, String> parameters = new HashMap<String, String>();
		String value =  String.valueOf(eventID);
		parameters.put(EventsDefine.EVENTS_VENTID, value);
		return requestRaw(EventsDefine.EVENTRET, parameters, new RequestCallback(){
			@Override
			public void onError() {
				if(null != callback)
					callback.onError("手机连上网络了吗?");
			}
			@Override
			public void onResponse(String string) {
				if(null == callback)
					return;
				try{
					RequestParse parse = new RequestParse(string);
					HashMap<String, Object> map = parse.getHashMap();
					EventsParse eventParse = new EventsParse(map);
					if(eventParse.parseOperate()){
						callback.onSuccess(eventParse);
					}
					else{
						String err = eventParse.parseErrorCode();
						callback.onError(err);
					}
				}catch(JSONException e){
					callback.onError("JSONException");
				}

			}

		});
	}
	
	/**
	 * 获取读取人名列表信息
	 * @param eventid
	 * @param callback
	 * @return
	 */
	public  boolean GetEventReadInfo(String eventid, final ViewRequestCallback callback){
		DrServiceJniCallback jniHttpCallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}
				
				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try {
					map = parse.getHashMap();
					EventsParse newsParse = new EventsParse(map);
					if(newsParse.parseOperate()){
						callback.onSuccess(newsParse);
					}
					else{
						String err = newsParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return mDrServiceJni.GetEventReadInfo(mDomain, mSchoolId, mSessionKey, eventid, jniHttpCallback);
	}
};

