package com.drcom.drpalm.View.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.NewEventActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.jsonparser.ClassTitleParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.EventsTypeLanguage;
import com.drcom.drpalm.objs.EventsTypeLanguageResultItem;
import com.drcom.drpalm.objs.EventDraftItem.Attachment;

public class NewEventActivityManagement {
	private int SEND_SUCCEED = 1;
	private int SEND_FAILD = 0;

	private Context mContext;

	private EventsDB mEventsDB;
	private SettingManager setInstance;
	private String mUsername = "";
	private boolean isRequestRelogin = true; // 登录SECCION超时要重登录?

	public NewEventActivityManagement(Context c) {
		mContext = c;
		setInstance = SettingManager.getSettingManager(c);
		mEventsDB = EventsDB.getInstance(c, GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
	}

	/**
	 * 初始化编辑通告
	 * 
	 * @param detailitem
	 * @return
	 */
	public EventDraftItem initEditDraf(EventDetailsItem detailitem) {
		EventDraftItem item = new EventDraftItem();
		item.old_eventid = detailitem.eventid;
		item.owner = detailitem.owner;
		item.ownerid = detailitem.ownerid;

		for (int i = 0; i < detailitem.imgs.size(); i++) {
			Attachment a = new Attachment();
			a.data = detailitem.imgs.get(i).imgData;
			a.description = detailitem.imgs.get(i).imgDescription;
			a.fileId = detailitem.imgs.get(i).fileId;
			a.fileType = detailitem.imgs.get(i).fileType;
			a.item = String.valueOf(detailitem.imgs.get(i).attid);
			a.type = "id";

			GlobalVariables.mListAttachmentData.add(a);
		}

		return item;
	}

	/**
	 * 保存草稿
	 * 
	 * @param id
	 * @param title
	 * @param shortloc
	 * @param body
	 * @param type
	 * @param oristatus
	 * @param old_eventid
	 * @param bifeshow
	 * @param start
	 * @param end
	 * @param owner
	 * @param ownerid
	 */
	public void SaveDraf(Integer id, String title, String shortloc,
			String body, int type, String oristatus, Integer old_eventid,
			boolean bifeshow, Date start, Date end, String owner, String ownerid ,String type_titleId) {
		EventDraftItem ditem = new EventDraftItem();
		ditem.pk_id = id;
		ditem.title = title;
		ditem.shortloc = shortloc;
		ditem.body = body;
		ditem.type = type;
		ditem.oristatus = oristatus;
		ditem.eventDraftAttachment = GlobalVariables.mListAttachmentData;
		ditem.type_titleId = type_titleId;

		if (old_eventid != null) { // 如果是转发的通告保存为附件，则把在服务器的附件ID保存,下次从草稿中发送时同时发送
			ditem.old_eventid = old_eventid;

			if (GlobalVariables.mListAttachmentData.size() > 0) {
				ditem.isAttc = true;

				// 附件是已发通告的话,要将内容赋为空
				for (int i = 0; i < ditem.eventDraftAttachment.size(); i++) {
					if (ditem.eventDraftAttachment.get(i).type.equals("id")) {
						// 如果附件是在服务器端，就不保存到草稿附件库
						ditem.eventDraftAttachment.get(i).data = null;
						GlobalVariables.mListAttachmentData.get(i).data = null;
					}
				}
			}
		} else {
			if (GlobalVariables.mListAttachmentData.size() > 0) {
				ditem.isAttc = false;

				// 附件是已发过的话,要将内容赋为空
				for (int i = 0; i < ditem.eventDraftAttachment.size(); i++) {
					if (ditem.eventDraftAttachment.get(i).type.equals("id")) {
						// 如果附件是在服务器端，就不保存到草稿附件库
						ditem.eventDraftAttachment.get(i).data = null;
						GlobalVariables.mListAttachmentData.get(i).data = null;
					} else {
						// 否则，保存到草稿附件库
						ditem.isAttc = true;
					}
				}
			}
		}

		ditem.bifeshow = bifeshow;
		ditem.user = mUsername;
		ditem.save = DateFormatter.getDateFromMilliSeconds(System
				.currentTimeMillis());
		ditem.start = start;
		ditem.end = end;
		ditem.owner = owner;
		ditem.ownerid = ownerid;

		mEventsDB.saveEventsDraftItem(ditem);
	}

	/**
	 * 
	 * @param h
	 * @param item
	 */
	public void SendEventRequest(final Handler h, final EventDraftItem item) {
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback() {
			@Override
			public void onSuccess() { // 请求数据成功
				Message message = Message.obtain();
				message.arg1 = SEND_SUCCEED;
				h.sendMessage(message);
				Log.i("zjj", "发送通告成功");
			}

			@Override
			public void onCallbackError(String str) {
				Message message = Message.obtain();
				message.arg1 = SEND_FAILD;
				message.obj = str;
				h.sendMessage(message);
				Log.i("zjj", "发送通告失败" + str);
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				Log.i("zjj", "发送通告:自动重登录失败");
			}

			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				Log.i("zjj", "发送通告:自动重登录成功");
				if (isRequestRelogin) {
					SendEventRequest(h, item); // 自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};

		System.gc();
		mRequestOperation.sendGetNeededInfo("SubmitEvent", new Object[] { item,
				callback }, callback.getClass().getName());
	}

	/**
	 * 从服务器获取通告列表总数据
	 * @param handler
	 */
	public void getEventTypeLanguage(final Handler handler ,final int title) {

		RequestOperationReloginCallback requestOperationReloginCallback = new RequestOperationReloginCallback() {
			@Override
			public void onSuccess(Object object) { // 请求数据成功
				if (object != null) {
					EventsTypeLanguageResultItem resultItem = (EventsTypeLanguageResultItem) object;
					Log.i("selectReceiver", "获取失败-------------------------");
					if (!resultItem.isResult()) {
						Toast.makeText(mContext, "获取失败", Toast.LENGTH_LONG)
								.show();
						Log.i("EventTypeLanguage", "获取失败----------->Management");
					} else {
						// 清除表数据
						mEventsDB.deleteEventsTypeLanguageTable();
						
						EventsTypeLanguage type  = new EventsTypeLanguage();
						for(int i = 0;i<resultItem.mEventsTypeLanguage.size();i++){
							type.type_index = resultItem.mEventsTypeLanguage.get(i).type_index;
							type.type_event = resultItem.mEventsTypeLanguage.get(i).type_event;
							type.type_chinese = resultItem.mEventsTypeLanguage.get(i).type_chinese;
							type.type_chinese_character =  resultItem.mEventsTypeLanguage.get(i).type_chinese_character;
							type.type_english =  resultItem.mEventsTypeLanguage.get(i).type_english;
							mEventsDB.saveEventsTypeLanguage(type);
							Log.i("EventsTypeLanguage", "入库Manage这里被运行了-------------------");
						}
						
						Message message = new Message();
						message.arg1 = NewEventActivity.SUCCESS;
						if(title == NewEventActivity.DRAFT_TITLE)
						{
							message.arg2 = NewEventActivity.DRAFT_TITLE;
						}else {
							message.arg2 = NewEventActivity.NORMAL_TITLE;
						}
						
						handler.sendMessage(message);
						Log.i("leaveApplicationManager", "获取成功");
					}
				}
			}

			@Override
			public void onCallbackError(String str) {
				Message message = Message.obtain();
				message.arg1 = SEND_FAILD;
				message.obj = str;
				handler.sendMessage(message);
				Log.i("hzy", "获取通告类型数据失败" + str);
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				Log.i("hzy", "获取通告类型:自动重登录失败");
			}

			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				Log.i("hzy", "获取通告类型:自动重登录成功");
				if (isRequestRelogin) {
					getEventTypeLanguage(handler,title); // 自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}

		};
		System.gc();
		RequestManager.getEventsTypeList(new ClassTitleParser(),
				requestOperationReloginCallback);
	}

	/**
	 * 
	 * 从数据库获取通告列表类型数据
	 * @param l
	 * @param CategoryID
	 * @return
	 */
	public List<EventsTypeLanguage> getTypeData(CurrentLan l, int CategoryID) {
		
		List<EventsTypeLanguage> data = mEventsDB.findEventsTypeLanguage(l, CategoryID);

		return data;
	}
	
}
