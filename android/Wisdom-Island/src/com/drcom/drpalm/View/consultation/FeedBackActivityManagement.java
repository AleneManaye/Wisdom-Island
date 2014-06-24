package com.drcom.drpalm.View.consultation;

import com.drcom.drpalm.Activitys.consultation.FeedBackActivity;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.objs.ConsultDraftItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;

public class FeedBackActivityManagement {
	private static final int SUCCESS = 1;
	private static final int ERROR = 0;
	
	private SharedPreferences mSharedPreferences;
	private Editor mEditor;
	
	public FeedBackActivityManagement(Context c){
		mSharedPreferences = c.getSharedPreferences("feedback", c.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
		
	}
	
	/**
	 * 
	 * @return
	 */
	public ConsultDraftItem getConsultDraftDetail(){
		ConsultDraftItem consultDraftItem = new ConsultDraftItem();
		consultDraftItem.username = mSharedPreferences.getString("name", "");
		consultDraftItem.phone = mSharedPreferences.getString("phone", "");
		consultDraftItem.email = mSharedPreferences.getString("feedemail", "");
		consultDraftItem.content = mSharedPreferences.getString("feedcontent", "");
		consultDraftItem.title = mSharedPreferences.getString("title", "");
		return consultDraftItem;
	}
	
	
	/**
	 * 
	 */
	public void SaveConsultDraftDetail(ConsultDraftItem consultDraftItem){
		mEditor.putString("name", consultDraftItem.username);
		mEditor.putString("phone", consultDraftItem.phone);
		mEditor.putString("email", consultDraftItem.email);
		mEditor.putString("content", consultDraftItem.content);
		mEditor.putString("title", consultDraftItem.title);
		mEditor.commit();
	}
	
	
	public void Commit(ConsultDraftItem cdf,final Handler h){
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationCallback callback = new RequestOperationCallback() {
			@Override
			public void onSuccess() {
				Message msg = new Message();
				msg.what = SUCCESS;
				h.sendMessage(msg);
			}

			@Override
			public void onError(String err) {
				Message msg = new Message();
				msg.what = ERROR;
				msg.obj = err;
				h.sendMessage(msg);
			}
		};
		mRequestOperation.sendGetNeededInfo("PutConSult", new Object[] { cdf, callback }, callback.getClass().getName());
	}
}
