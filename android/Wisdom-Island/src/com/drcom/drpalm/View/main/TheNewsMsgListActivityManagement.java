package com.drcom.drpalm.View.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.MessageObject;
import com.drcom.drpalm.objs.UpdateTimeItem;

public class TheNewsMsgListActivityManagement {
	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	
	private Context mContext;
	private boolean isRequestRelogin = true; // 登录SECCION超时要重登录?
	
	public TheNewsMsgListActivityManagement(Context c){
		mContext = c;
	}
	
	/**
	 * 拿最新5条 通告
	 * @param h
	 */
	public void GetData(final Handler h){
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback() {
			@Override
			public void onSuccess() { // 请求数据成功
				Message message = Message.obtain();
				message.arg1 = UPDATEFINISH; // 刷新
				message.obj = new MessageObject(true, false);
				h.sendMessage(message);

				Log.i("zjj", "5条新列表:刷新成功");
			}

			@Override
			public void onCallbackError(String str) {
				Message message = Message.obtain();
				message.arg1 = UPDATEFAILED;
				message.obj = str;
				h.sendMessage(message);

				Log.i("zjj", "5条新列表:刷新失败" + str);
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				Log.i("zjj", "5条新列表:自动重登录失败");
			}

			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				Log.i("zjj", "5条新列表:自动重登录成功");
				if (isRequestRelogin) {
					GetData(h); // 自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		mRequestOperation.sendGetNeededInfo("GetEventsInfoList", new Object[] { callback }, callback.getClass().getName());
	}
	
	/**
	 * 拿最新5条新闻和通告
	 * @param h
	 */
	public void GetNewsData(final Handler h){
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback() {
			@Override
			public void onSuccess() { // 请求数据成功
				Message message = Message.obtain();
				message.arg1 = UPDATEFINISH; // 刷新
				message.obj = new MessageObject(true, false);
				h.sendMessage(message);

				Log.i("zjj", "5条新列表:刷新成功");
			}

			@Override
			public void onCallbackError(String str) {
				Message message = Message.obtain();
				message.arg1 = UPDATEFAILED;
				message.obj = str;
				h.sendMessage(message);

				Log.i("zjj", "5条新列表:刷新失败" + str);
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				Log.i("zjj", "5条新列表:自动重登录失败");
			}

			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				Log.i("zjj", "5条新列表:自动重登录成功");
				if (isRequestRelogin) {
					GetData(h); // 自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		mRequestOperation.sendGetNeededInfo("GetNewsInfoList", new Object[] { callback }, callback.getClass().getName());
	}
	
	/**
	 * 班级新闻过滤
	 * 学生登录，排除 [已发]
	 * 和 配置文件中不显示的内容
	 * @param list
	 * @return
	 */
	public List<UpdateTimeItem> Filter( List<UpdateTimeItem> list){
		SettingManager settingInstance = SettingManager.getSettingManager(mContext);
		//身份
		if (!settingInstance.getCurrentUserInfo().strUsrType.equals(UserInfo.USERTYPE_TEACHER)) {

			List<UpdateTimeItem> templist = new ArrayList<UpdateTimeItem>();

			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).update_time_channel == RequestCategoryID.EVENTS_SEND_ID) {
					templist.add(list.get(i));
				} 
//				else if (list.get(i).update_time_channel == RequestCategoryID.SYSINFO_ID) {
//					templist.add(list.get(i));
//				}
			}

			list.removeAll(templist);
			templist.clear();
			templist = null;
		}

		//配置文件
		for (int j = 0; j < list.size(); j++) {
			int id = list.get(j).update_time_channel.intValue();
			boolean needremove = true;
			
			a:
			for (int j2 = 0; j2 < GlobalVariables.blocks.size(); j2++) {
				if("class".equals(GlobalVariables.blocks.get(j2).getType())){
					if (id == GlobalVariables.blocks.get(j2).getId() ){
						needremove = false;
						
						if (!GlobalVariables.blocks.get(j2).isVisible()) {
							needremove = true;
						}
						
						break a;
					}
				}
			}
			
			if(needremove){
				list.remove(j);
				j = -1;
			}
				
		}
		
		return list;
	}
	
	/**
	 * 校园新闻过滤
	 * 排除 配置文件中不显示的内容
	 * @param list
	 * @return
	 */
	public List<UpdateTimeItem> FilterNews( List<UpdateTimeItem> list){
		for (int j = 0; j < list.size(); j++) {
			int id = list.get(j).update_time_channel.intValue();
			boolean needremove = true;
			
			a:
			for (int j2 = 0; j2 < GlobalVariables.blocks.size(); j2++) {
				if("school".equals(GlobalVariables.blocks.get(j2).getType())){
					if (id == GlobalVariables.blocks.get(j2).getId() ){
						needremove = false;
						
						if (!GlobalVariables.blocks.get(j2).isVisible()) {
							needremove = true;
						}
						
						break a;
					}
				}
				
//				if (id == GlobalVariables.blocks.get(j2).getId() && "school".equals(GlobalVariables.blocks.get(j2).getType())) {
//					if (!GlobalVariables.blocks.get(j2).isVisible()) {
//						list.remove(j);
//						j = -1;
//					}
//				}
			}
			
			if(needremove){
				list.remove(j);
				j = -1;
			}
		}
		return list;
	}
}
