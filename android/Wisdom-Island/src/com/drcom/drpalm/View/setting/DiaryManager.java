package com.drcom.drpalm.View.setting;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.Navigation.NavigationMainActivity;
import com.drcom.drpalm.Activitys.setting.DiaryActivity;
import com.drcom.drpalm.Activitys.setting.DiarySendActivity;
import com.drcom.drpalm.DB.DiaryDB;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.DB.NavigationDB;
import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.DiaryParse;
import com.drcom.drpalm.Tool.request.NavigationParse;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.ViewRequestCallback;
import com.drcom.drpalm.objs.DiaryItem;
import com.drcom.drpalm.objs.GrowdiaryItem;
import com.drcom.drpalm.objs.NavigationListItem;

public class DiaryManager {
	
	private DiaryDB mDiaryDB;
	private Context mContext;
	private String mUsername;
	public DiaryManager(Context context){
		this.mContext = context;
		mDiaryDB = DiaryDB.getInstance(mContext,GlobalVariables.gSchoolKey);
		SettingManager setInstance = SettingManager.getSettingManager(context);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
	}
	
	//获取成长点滴列表
	public void GetGrowdiary(String lastupdate, final Handler handler){
		Message msg = Message.obtain();
		msg.arg1 = DiaryActivity.GET_DIARY_LIST_START;
		handler.sendMessage(msg);
		RequestManager.GetGrowdiary(lastupdate, new ViewRequestCallback() {
			
			@Override
			public void onSuccess(final BaseParse parse) {
				// TODO Auto-generated method stub
				new Thread() {					
					@Override
					public void run() {					
						synchronized(mDiaryDB) {
							DiaryParse diaryParse = (DiaryParse)parse;
							List<DiaryItem> diaryItems = diaryParse.parseDiaryList();				
							if(diaryItems == null) {
								onError("");
								return;
							}	
							mDiaryDB.startTransaction();
							for(DiaryItem item : diaryItems) {
								item.user = mUsername;
								mDiaryDB.saveDiaryItem(item);
							}							
							mDiaryDB.endTransaction();
							Message msg = Message.obtain();
							msg.arg1 = DiaryActivity.GET_DIARY_LIST_SUCCESSFUL;
							handler.sendMessage(msg);
						}
					}
				}.start();
			}
			
			@Override
			public void onError(String err) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = DiaryActivity.GET_DIARY_LIST_FAILED;
				msg.obj = err;
				handler.sendMessage(msg);
			}
		});
	}
	
	//提交成长点滴
	public void SubmitGrowdiary(ArrayList<GrowdiaryItem> filelist, final Handler handler){
		Message msg = Message.obtain();
		msg.arg1 = DiarySendActivity.SEND_DIARY_LIST_START;
		handler.sendMessage(msg);
		RequestManager.SubmitGrowdiary(filelist, new ViewRequestCallback() {
			
			@Override
			public void onSuccess(final BaseParse parse) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = DiarySendActivity.SEND_DIARY_LIST_SUCCESSFUL;
				handler.sendMessage(msg);
			}
			
			@Override
			public void onError(String err) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = DiarySendActivity.SEND_DIARY_LIST_FAILED;
				msg.obj = err;
				handler.sendMessage(msg);
			}
		});
	}
}
