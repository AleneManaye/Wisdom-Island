package com.drcom.drpalm.View.myphoto;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.myphoto.MyPhotoDetailActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.DB.FavDB;
import com.drcom.drpalm.DB.MyPhotoDB;
import com.drcom.drpalm.Tool.jsonparser.MyphotoListParser;
import com.drcom.drpalm.Tool.jsonparser.SubmitResultParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.FavItem;
import com.drcom.drpalm.objs.MyPhoto;
import com.drcom.drpalm.objs.MyphotolistResultItem;

public class MyPhotoDetailActivityManager {
	
	private static final int DOWN = 0;// 下载成功的标签
	private static final int NOT_DOWN = 1;// 下载失败的标签
	private static final int DEL = 6;// 删除成功的标签
	private static final int NOT_DEL = 7;// 删除失败的标签
	
	private SettingManager setInstance ;
	private String mUsername = "";	
	private MyPhotoDB mMyPhotoDB;
	private Handler mUpdateHandler;	//更新结果句柄
	private boolean isRequestRelogin = true; // 登录SECCION超时要重登录?
	
	public MyPhotoDetailActivityManager(Context c){
		setInstance = SettingManager.getSettingManager(c);	
		mMyPhotoDB = MyPhotoDB.getInstance(c, GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
	}
	
	/**
	 * 从库中读取
	 * @return
	 */
	public ArrayList<MyPhoto> getMyPhotosFormDB (){
		ArrayList<MyPhoto> photos = new ArrayList<MyPhoto>();
		
		Cursor cursor = mMyPhotoDB.getMyPhotoListCursor(mUsername);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				MyPhoto item = mMyPhotoDB.retrieveMyPhotoItem(cursor);
				photos.add(item);
				cursor.moveToNext();
			}
		}
		cursor.close();
		
		return photos;
	}
	
	
	private String mLastupdatetime;
	/**
	 * 向服务器请求个人相册数据
	 * @param lastupdatetime
	 * @param h
	 */
	public void getMyphotoDetail(String lastupdatetime , final Handler h)
	{
		mUpdateHandler = h;
		mLastupdatetime = lastupdatetime;
		MyphotoListParser mplp = new MyphotoListParser();
		mplp.SetUsername(mUsername);
		RequestManager.GetMyphoto(mLastupdatetime, mplp, getmyphotocallback);
	}

	/**
	 * 取个人相册请求结果
	 */
	private RequestOperationReloginCallback getmyphotocallback = new RequestOperationReloginCallback(){
		@Override
		public void onError(String str) {
			Log.i("zjj", "取个人相册请求结果  返回失败");
			if(mUpdateHandler != null){
				Message msg = new Message();
				msg.what = NOT_DOWN;
				msg.obj = str;
				mUpdateHandler.sendMessage(msg);
			}
		}
		@Override
		public void onSuccess(Object obj) {		
			Log.i("zjj", "取个人相册请求结果   返回成功");
			MyphotolistResultItem result = (MyphotolistResultItem)obj;
			
			for(int i = 0 ; i<result.mMyPhotolist.size(); i++){
				mMyPhotoDB.saveMyPhotoItem(result.mMyPhotolist.get(i));
			}
			
			if(mUpdateHandler != null){
				Message msg = new Message();
				msg.what = DOWN;
				mUpdateHandler.sendMessage(msg);
			}
		}			
		@Override
		public void onReloginError() {
			// TODO Auto-generated method stub
			Log.i("zjj", "通告列表:自动重登录失败");
		}

		@Override
		public void onReloginSuccess() {
			// TODO Auto-generated method stub
			Log.i("zjj", "通告列表:自动重登录成功");
			if (isRequestRelogin) {
				getMyphotoDetail(mLastupdatetime,mUpdateHandler); // 自动登录成功后，再次请求数据
				isRequestRelogin = false;
			}
		}
	};
	
	/**
	 * 提交图片
	 * @param pic
	 */
	public void delPic(MyPhoto myPhoto, final Handler h){
		mUpdateHandler = h;
		
		imgid = myPhoto.imgid;
		
		ArrayList<MyPhoto> myphotolist = new ArrayList<MyPhoto>();
		myphotolist.add(myPhoto);
		
		RequestManager.SubmitUseralbum(myphotolist, new SubmitResultParser(), callback);
	}
	private String imgid = "";
	
	private RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
		
		@Override
		public void onError(String str) {
			Log.i("zjj", "删除个人相册 返回失败");
			if(mUpdateHandler != null){
				Message msg = new Message();
				msg.what = NOT_DEL;
				msg.obj = str;
				mUpdateHandler.sendMessage(msg);
			}
		}
		@Override
		public void onSuccess() {		
			Log.i("zjj", "删除个人相册  返回成功");
			mMyPhotoDB.deleteMyPhotoItem(imgid, mUsername);
			if(mUpdateHandler != null){
				Message msg = new Message();
				msg.what = DEL;
				mUpdateHandler.sendMessage(msg);
			}
		}								
	};
}
