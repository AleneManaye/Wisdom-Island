package com.drcom.drpalm.Tool.request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.PushManager;
import com.drcom.drpalm.Tool.nettool.NetStatusManager;
import com.drcom.drpalm.Tool.nettool.NetStatusManager.NetType;
import com.drcom.drpalm.Tool.service.ConnectPushCallback;
import com.drcom.drpalm.Tool.service.DrPalmGuideService;
import com.drcom.drpalm.Tool.service.DrPalmPushService;
import com.drcom.drpalm.Tool.service.DrPalmService;
import com.drcom.drpalm.Tool.service.DrServiceLog;
import com.drcom.drpalm.Tool.service.IDrPalmPush;
import com.drcom.drpalm.Tool.service.IDrPalmRequest;
import com.drcom.drpalm.Tool.service.IDrPalmRequestCallback;
import com.drcom.drpalm.Tool.service.IDrPalmRequestGetEventListCallback;
import com.drcom.drpalm.Tool.service.IDrPalmRequestGetStatusCallback;
import com.drcom.drpalm.Tool.service.IDrPalmRequestOrganizationCallback;
import com.drcom.drpalm.Tool.service.IDrPalmRequestResourceCallback;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.ConsultDraftItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.OrganizationBackItem;
import com.drcom.drpalm.objs.OrganizationItem;
import com.drcom.drpalm.objs.PushRegItem;
import com.drcom.drpalm.objs.ToursItem;
import com.drcom.drpalm4tianzhujiao.R;

public class RequestOperation {
	// DB
//	private EventsDB mEventsDB;

	private IDrPalmRequest mDrPalmService = null;
	private ServiceConnection mConnection = null;
	private RequestOperationCallback mRequestServiceCallback = null;

	private IDrPalmPush mDrPalmPushService = null;
	private ServiceConnection mPushConnection = null;
	private RequestOperationCallback mPushServiceCallback = null;

	// Request
	private EventsRequest mEventsRequest;
	// member variable
	private String mUsername = "";
	
	private static Context mContext;

	private int LOGIN_GATEWAY = 1;
	private int GETTOURS = 3;
	private int GETNEWSDETAIL = 4;

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.arg1 == LOGIN_GATEWAY) {
			}
			else if (msg.arg1 == GETTOURS) {
				//RequestOperation.getInstance().GetTours();
			}
			else if (msg.arg1 == GETNEWSDETAIL) {
			}

		}
	};
	
	private static SettingManager setInstance ;

	static private RequestOperation mRequestOperation = null;
	static public RequestOperation getInstance(){
		if(null == mRequestOperation){
			mRequestOperation = new RequestOperation();
			mContext = GlobalVariables.gAppContext;
		}
		return mRequestOperation;
	}
	public RequestOperation(){
		//防止数据库错乱
//		mEventsDB = EventsDB.getInstance(GlobalVariables.gAppContext);

		mConnection = new ServiceConnection() {
	        public void onServiceDisconnected(ComponentName name) {
	        	mDrPalmService = null;
	        }
	        public void onServiceConnected(ComponentName name, IBinder service) {
	        	mDrPalmService = IDrPalmRequest.Stub.asInterface(service);
	        	if(null != mRequestServiceCallback)
	        		mRequestServiceCallback.onSuccess();
	        	Log.d("RequestOperation:onServiceConnected","bind DrPalmService Success");
	        }
		};

		mPushConnection = new ServiceConnection(){
			public void onServiceDisconnected(ComponentName name) {
				mDrPalmPushService = null;
	        }
	        public void onServiceConnected(ComponentName name, IBinder service) {
	        	mDrPalmPushService = IDrPalmPush.Stub.asInterface(service);
	        	if(null != mPushServiceCallback)
	        		mPushServiceCallback.onSuccess();
	        	Log.d("RequestOperation:onServiceConnected","bind DrPalmPushService Success");
	        }
		};
		//Intent intentService = new Intent(GlobalVariables.gAppContext,DrPalmService.class);

	}
	
	/**
	 * 获取绑定的service句柄 add by menchx
	 * @return
	 */
	public IDrPalmRequest getIDrPalmRequest(){
		return mDrPalmService;
	}
	
	/**
	 * 获取绑定的pushservice句柄 add by menchx
	 * @return
	 */
	public IDrPalmPush getIDrPalmPush(){
		return mDrPalmPushService;
	}
	
	public void unbindService(){
		if(null != mConnection){
			GlobalVariables.gAppContext.unbindService(mConnection);
			GlobalVariables.gAppContext.unbindService(mPushConnection);
			Log.d("RequestOperation:unbindService:","");
		}
	}
	public void bindService(final RequestOperationCallback requestServiceCallback,final RequestOperationCallback pushServiceCallback){
		mRequestServiceCallback = requestServiceCallback;
		mPushServiceCallback = pushServiceCallback;
		//Intent intentService = new Intent(context,DrPalmService.class);
		//intentService.setAction("com.drcom.drpalm.service.IDrPalmService");
		Intent intentService = new Intent(GlobalVariables.gAppContext,DrPalmService.class);
		GlobalVariables.gAppContext.startService(intentService);
		boolean ret = GlobalVariables.gAppContext.bindService(intentService, mConnection, Context.BIND_AUTO_CREATE);
		Log.d("RequestOperation:bindService:", String.valueOf(ret));

//		Intent intentPushService = new Intent(GlobalVariables.gAppContext,DrPalmPushService.class);
//		GlobalVariables.gAppContext.startService(intentPushService);
//		ret = GlobalVariables.gAppContext.bindService(intentPushService, mPushConnection, Context.BIND_AUTO_CREATE);
//		Log.d("RequestOperation:bindPushService:", String.valueOf(ret));
//
//		Intent intentGuideService = new Intent(GlobalVariables.gAppContext,DrPalmGuideService.class);
//		GlobalVariables.gAppContext.startService(intentGuideService);
//		Log.d("RequestOperation:startGuideService:", "");

	}

	/**
	 * 网关 和学校是否已选择
	 * GatewayRequest
	 */
	public boolean isConnected(){
		boolean connect = false;
		
		if("" != GlobalVariables.gGateawayDomain && "" != GlobalVariables.gSchoolId)
			connect = true;
		else
			connect = false;
		

		
		return connect;
	}
	
	/**
	 * 网络是否可用
	 * @return
	 */
	private boolean isNetUse(){
		boolean connect = true;
		setInstance = SettingManager.getSettingManager(GlobalVariables.gAppContext);	
		//是否仅WIFI使用
		if(setInstance.bOnlyWiFi){
			if( NetStatusManager.getSettingManager(GlobalVariables.gAppContext).GetNetType() == NetType.WIFI){
				connect = true;
			}else{
				connect = false;
			}
		}else{
			if( NetStatusManager.getSettingManager(GlobalVariables.gAppContext).GetNetType() == NetType.NOTCONNECT){
				connect = false;
			}
		}
		
		return connect;
	}
	
	public boolean GetSchoolKey(final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetSchoolKey(GlobalVariables.gCenterDomain,
					GlobalVariables.gSeqid,
					new IDrPalmRequestCallback.Stub(){
						@Override
						public void onError(String err) throws RemoteException {
							// TODO Auto-generated method stub
							callback.onError(err);
						}
						@Override
						public void onSuccess() throws RemoteException {
							// TODO Auto-generated method stub
							callback.onSuccess();
						}

			});
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 重新初始化DB接口，防止学校切换导致写入同一个库的问题（由于service绑定Application启动，导致先于获得schoolkey）
	 */
	public boolean initDB(){
		if(null == mDrPalmService)
		{
			System.out.println("GetNetworkGate @@@@mDrPalmService == null");
			return false;
		}
		try {
			return mDrPalmService.initDB();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean GetNetworkGate(final RequestOperationCallback callback){
		if(null == mDrPalmService)
		{
			System.out.println("GetNetworkGate @@@@mDrPalmService == null");
			return false;
		}
		try {
			//如果BUG掉,GlobalVariables内的值会被清掉, 需要重新赋值
			//以免出现"无效SchoolKey"的问题
			if(GlobalVariables.gCenterDomain.equals("") || GlobalVariables.gSchoolKey.equals("")){
				GlobalVariables.gAppContext = mContext;
				GlobalVariables.get();
//				mDrPalmService.initDB();
			}
			
			return mDrPalmService.GetNetworkGate(GlobalVariables.gCenterDomain,
					GlobalVariables.gSchoolKey,
					new IDrPalmRequestCallback.Stub(){
						@Override
						public void onError(String err) throws RemoteException {
							// TODO Auto-generated method stub
							callback.onError(err);
						}
						@Override
						public void onSuccess() throws RemoteException {
							// TODO Auto-generated method stub
							callback.onSuccess();

							Message message = Message.obtain();
							message.arg1 = GETTOURS;
							mHandler.sendMessage(message);

							String packageName = GlobalVariables.gAppContext.getPackageName();
							PushRegItem regItem = new PushRegItem();
							regItem.packageName = packageName;
							regItem.SchoolID = GlobalVariables.gSchoolId;
							regItem.SchoolKey = GlobalVariables.gSchoolKey;
							regItem.appver = "";
							PackageManager packageManager = GlobalVariables.gAppContext.getPackageManager();
							try {
								PackageInfo packInfo = packageManager.getPackageInfo(GlobalVariables.gAppContext.getPackageName(),0);
								regItem.appver = packInfo.versionName;
							} catch (NameNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							DrServiceLog.getInstance(GlobalVariables.gAppContext);
							
							PushManager.init(GlobalVariables.gAppContext,new ConnectPushCallback() {

								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									try {
										PushManager.Register(GlobalVariables.gSchoolId, GlobalVariables.gSchoolKey);
									} catch (RemoteException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								
								@Override
								public void onError(String err) {
									// TODO Auto-generated method stub
									
								}
							});
							
//							RegAppPush(regItem, new RequestOperationCallback(){
//								@Override
//								public void onError(String err) {
//								}
//								@Override
//								public void onSuccess() {
//								}
//							});
							
							//把用到的全局保存到文件中,以免BUG掉后会没值
							GlobalVariables.save();
						}

			});
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	};
	
	public boolean GetSchoolList(String parent_id, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetSchoolList(GlobalVariables.gCenterDomain,
					parent_id, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean SearchSchool(String appid, String searchKey, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.SearchSchool(GlobalVariables.gCenterDomain,
					appid,searchKey, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean GetParentOrganization(final OrganizationItem item, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetParentOrganization(item, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	
	
	/*
	 * EventsRequest
	 */
//	public boolean GetEventsList(int category_id, String lastupdate, final RequestOperationReloginCallback callback){
//		if(null == mDrPalmService)
//			return false;
//		try {
//			return mDrPalmService.GetEventsList(category_id, lastupdate, new IDrPalmRequestCallback.Stub(){
//				@Override
//				public void onError(String err) {
//					callback.onError(err);
//				}
//				@Override
//				public void onSuccess() {
//					callback.onSuccess();
//				}
//
//			});
//		}catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return false;
//	}
	
	public boolean GetObjectType(final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetObjectType(new IDrPalmRequestCallback.Stub(){

				@Override
				public void onError(String err) throws RemoteException {
					callback.onError(err);
				}
				@Override
				public void onSuccess() throws RemoteException {
					callback.onSuccess();
				}

			});
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean AutoAnwserEvent(Integer eventID, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.AutoAnwserEvent(eventID, new IDrPalmRequestCallback.Stub(){

				@Override
				public void onError(String err) throws RemoteException {
					callback.onError(err);
				}
				@Override
				public void onSuccess() throws RemoteException {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public boolean AutoAnwserEventList(List<Integer> listId, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		int[] arrayId = new int[listId.size()];
		for(int i = 0;i < listId.size();i++){
			arrayId[i] = listId.get(i);
		}
		try {
			return mDrPalmService.AutoAnwserEventList(arrayId, new IDrPalmRequestCallback.Stub(){

				@Override
				public void onError(String err) throws RemoteException {
					callback.onError(err);
				}
				@Override
				public void onSuccess() throws RemoteException {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public boolean GetPublishEvents(Date curpost, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		long date = 0;
		if(null != curpost)
			date = curpost.getTime();
		try {
			return mDrPalmService.GetPublishEvents(date, new IDrPalmRequestCallback.Stub(){

				@Override
				public void onError(String err) throws RemoteException {
					callback.onError(err);
				}
				@Override
				public void onSuccess() throws RemoteException {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}
	public boolean GetNewPublishEvents(Date startTime, Date endTime, Date startPost, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		long startDate = 0;
		long endDate = 0;
		long postDate = 0;
		if(null != startTime)
			startDate = startTime.getTime();
		if(null != endTime)
			endDate = startTime.getTime();
		if(null != startPost)
			postDate = startTime.getTime();
		try {
			return mDrPalmService.GetNewPublishEvents(startDate, endDate, postDate, new IDrPalmRequestCallback.Stub(){

				@Override
				public void onError(String err) throws RemoteException {
					callback.onError(err);
				}
				@Override
				public void onSuccess() throws RemoteException {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public boolean GetEventReadStatus(final Integer eventID, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetEventReadStatus(eventID, new IDrPalmRequestGetStatusCallback.Stub() {				@Override
				public void onSuccess(int count) throws RemoteException {

//					mEventsDB.updateEventSendReadStatus(eventID, count, mUsername);
				}

				@Override
				public void onError(String err) throws RemoteException {
					callback.onError(err);

				}
			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

//	public boolean RegAppPush(final PushRegItem regItem, final RequestOperationCallback callback){
//		if(null == mDrPalmPushService)
//			return false;
//		try {
//			return mDrPalmPushService.RegAppPush(regItem, new IDrPalmPushCallback.Stub(){
//				@Override
//				public void onError(String err) throws RemoteException {
//					callback.onError(err);
//				}
//				@Override
//				public void onSuccess() throws RemoteException {
//					callback.onSuccess();
//				}
//			});
//		}catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return false;
//	}
	
	/**
	 * 统一对外接口，用于处理需要网关的接口
	 * @param methodname
	 * @param args
	 */
	public boolean sendGetNeededInfo(final String methodname, final Object[] args, final String callbackClassname){
		if(isNetUse()){
			if(!isConnected()){
				GetNetworkGate(new RequestOperationCallback() {
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						invokeMethodByName(getClass().getName(),methodname,args);
					}
					
					@Override
					public void onError(String err) {
						// TODO Auto-generated method stub
						invokeErrorCallback(args[args.length-1], "onError", new Object[] {err}, callbackClassname);
//						invokeMethodByName(args[args.length-1].getClass().getName(), "onError", new Object[] {err});
					}
				});
			}else{
				invokeMethodByName(getClass().getName(),methodname,args);
			}
		}else{
			invokeErrorCallback(args[args.length-1], "onError", new Object[] {GlobalVariables.gAppContext.getString(R.string.NotWiFi)}, callbackClassname);
		}
		return false;
	}
	
	private void invokeErrorCallback(Object obj, String methodname, Object[] args, String classname){
		try {
//			Object instance = Class.forName(classname).newInstance();
//			if(instance.equals(null))
//				return ;
			Method[] methods = Class.forName(classname).getMethods();
			if(methods != null){
				int i;
				for(i = 0; i<methods.length; i++){
					if(methods[i].getName().equals(methodname)){
						break;
					}
				}
				methods[i].invoke(obj, args);
			}
		} catch (IllegalAccessException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		} catch (IllegalArgumentException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		} catch (InvocationTargetException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		}catch (ClassNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	private void invokeMethodByName(String classname, String methodname, Object[] args){
		try {
			Object obj = RequestOperation.getInstance();
			if(obj.equals(null))
				return ;
			Method[] methods = RequestOperation.getInstance().getClass().getMethods();
			if(methods != null){
				int i;
				for(i = 0; i<methods.length; i++){
					if(methods[i].getName().equals(methodname)){
						break;
					}
				}
				methods[i].invoke(obj, args);
			}
		} catch (IllegalAccessException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		} catch (IllegalArgumentException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		} catch (InvocationTargetException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取资源包
	 * @param callback
	 * @return
	 */
	public boolean GetTours(final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetTours(new IDrPalmRequestResourceCallback.Stub(){
				@Override
				public void onError(String err) throws RemoteException {
					// TODO Auto-generated method stub
					Intent downIntent = new Intent(ActivityActionDefine.NO_DOWNLOADTOURS_ACTION);
					GlobalVariables.gAppContext.sendBroadcast(downIntent);
				}
				@Override
				public void onSuccess(List<ToursItem> list, long date)
						throws RemoteException {
					Intent downIntent = new Intent(ActivityActionDefine.DOWNLOADTOURS_ACTION);
					downIntent.putParcelableArrayListExtra(ActivityActionDefine.TOURS_ITEM_LIST, (ArrayList<? extends Parcelable>) list);
					Date lastUpdateDate = new Date(date);
					downIntent.putExtra(ActivityActionDefine.TOURS_LASTUPDATE_DATE, lastUpdateDate);
					GlobalVariables.gAppContext.sendBroadcast(downIntent);
				}
			},new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) throws RemoteException {
					callback.onError(err);
				}
				@Override
				public void onSuccess() throws RemoteException {
					callback.onSuccess();
				}
			});
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return false;
	}
	
	
	/*
	 * 获取新闻列表
	 */
	public boolean GetNews(final Integer category, final String lastupdate, final RequestOperationCallback callback){
		if(null == mDrPalmService)
		{
			System.out.println("GetNews @@@@mDrPalmService == null");
			return false;
		}
		try {
			return mDrPalmService.GetNews(category, lastupdate, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) throws RemoteException {
					callback.onError(err);
				}
				@Override
				public void onSuccess() throws RemoteException {
					callback.onSuccess();
				}
			});
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
//	public boolean LoginGateway(final String userId, final String pass,final String identfy,final String packagename, final RequestLoginCallback callback){
//		if(null == mDrPalmService)
//			return false;
//		try {
//			return mDrPalmService.LoginGateway(userId,
//					pass, identfy,packagename,
//					new IDrPalmRequestLoginCallback.Stub(){
//						@Override
//						public void onError(String err) throws RemoteException {
//							callback.onError(err);
//						}
////						@Override
////						public void onSuccess(LoginSucItem item)
////								throws RemoteException {
////							callback.onSuccess(item);
////						}
//			});
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return false;
//	}
	
	/**
	 * 获取新闻模块最新更新时间(未登录)
	 * @param callback
	 * @return
	 */
	public boolean GetNewsLastUpdate(final RequestOperationCallback callback)
	{
		if(null == mDrPalmService)
			return false;
		try{
			return mDrPalmService.GetNewsLastUpdate(new IDrPalmRequestCallback.Stub() {

				@Override
				public void onSuccess() throws RemoteException {
					// TODO Auto-generated method stub
					callback.onSuccess();
				}

				@Override
				public void onError(String err) throws RemoteException {
					// TODO Auto-generated method stub
					callback.onError(err);
				}
			});
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	
	/**
	 * 获取最新新闻接口
	 * @param callback
	 * @return
	 */
	public boolean GetNewsInfoList(final RequestOperationCallback callback)
	{
		if(null == mDrPalmService)
			return false;
		try{
			return mDrPalmService.GetNewsInfoList(new IDrPalmRequestCallback.Stub() {

				@Override
				public void onSuccess() throws RemoteException {
					// TODO Auto-generated method stub
					callback.onSuccess();
				}

				@Override
				public void onError(String err) throws RemoteException {
					// TODO Auto-generated method stub
					callback.onError(err);
				}
			});
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	/*
	 * 获取新闻详细接口
	 */
	public boolean GetNewsDetail(final Integer story_id, final int allfield, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			int newid = 0;
			if(null != story_id){
				newid = story_id;
			}
			return mDrPalmService.GetNewsDetail(story_id, allfield, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) throws RemoteException {
					callback.onError(err);
				}
				@Override
				public void onSuccess() throws RemoteException {
					callback.onSuccess();
				}
			});
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * 新闻搜索接口
	 */
	public boolean SearchNews(String start, final String searchkey,final RequestOperationCallback callback)
	{
		if(null == mDrPalmService)
			return false;
		try{
			return mDrPalmService.SearchNews(start, searchkey, new IDrPalmRequestCallback.Stub() {

				@Override
				public void onSuccess() throws RemoteException {
					// TODO Auto-generated method stub
					callback.onSuccess();
				}

				@Override
				public void onError(String err) throws RemoteException {
					// TODO Auto-generated method stub
					callback.onError(err);
				}
			});
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	/**
	 * 提交入托申请
	 */
	public boolean PutConSult(ConsultDraftItem item, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.PutConSult(item, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) throws RemoteException {
					callback.onError(err);
				}
				@Override
				public void onSuccess() throws RemoteException {
					callback.onSuccess();
				}
			});
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	} 
	
	/*
	 * 登录注销接口
	 */
	public boolean Logout(final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.Logout(new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * push 注册接口
	 * @param bPush 是否开push
	 * @param bSound 是否声音提示
	 * @param bshake 是否震动
	 * @param time
	 * @param callback
	 * @return
	 */
	public boolean PushInfo(boolean bPush, boolean bSound, boolean bshake, String time, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.PushInfo(bPush, bSound, bshake, time, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 保持心跳，暂时不用
	 * @param callback
	 * @return
	 */
	public boolean KeepAlive(final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.KeepAlive(new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 终端BUG上传
	 * @param problem
	 * @param suggestion
	 * @param callback
	 * @return
	 */
	public boolean SubmitProblem(String problem, String suggestion, final RequestOperationCallback callback)
	{
		if(null == mDrPalmService)
			return false;
		try{
			return mDrPalmService.SubmitProblem(problem, suggestion, new IDrPalmRequestCallback.Stub() {

				@Override
				public void onSuccess() throws RemoteException {
					// TODO Auto-generated method stub
					callback.onSuccess();
				}

				@Override
				public void onError(String err) throws RemoteException {
					// TODO Auto-generated method stub
					callback.onError(err);
				}
			});
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	/**
	 * 设置邮箱接口
	 * @param email
	 * @param callback
	 * @return
	 */
	public boolean SetUserEmail(String email, final RequestOperationCallback callback)
	{
		if(null == mDrPalmService)
			return false;
		try{
			return mDrPalmService.SetUserEmail(email, new IDrPalmRequestCallback.Stub() {

				@Override
				public void onSuccess() throws RemoteException {
					// TODO Auto-generated method stub
					callback.onSuccess();
				}

				@Override
				public void onError(String err) throws RemoteException {
					// TODO Auto-generated method stub
					callback.onError(err);
				}
			});
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	/**
	 * 获取最新通告接口
	 * @param callback
	 * @return
	 */
	public boolean GetEventsInfoList(final RequestOperationCallback callback)
	{
		if(null == mDrPalmService)
			return false;
		try{
			return mDrPalmService.GetEventsInfoList(new IDrPalmRequestCallback.Stub() {

				@Override
				public void onSuccess() throws RemoteException {
					// TODO Auto-generated method stub
					callback.onSuccess();
				}

				@Override
				public void onError(String err) throws RemoteException {
					// TODO Auto-generated method stub
					callback.onError(err);
				}
			});
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	/**
	 * 获取活动和新闻模块最新更新时间(已登录)
	 * @param callback
	 * @return
	 */
	public boolean GetEventsLastUpdate(final RequestOperationCallback callback)
	{
		if(null == mDrPalmService)
			return false;
		try{
			return mDrPalmService.GetEventsLastUpdate(new IDrPalmRequestCallback.Stub() {

				@Override
				public void onSuccess() throws RemoteException {
					// TODO Auto-generated method stub
					callback.onSuccess();
				}

				@Override
				public void onError(String err) throws RemoteException {
					// TODO Auto-generated method stub
					callback.onError(err);
				}
			});
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	/*
	 * 获取活动列表
	 */
	public boolean GetEventsList(int category_id, String lastupdate, String lastreadtime, final RequestGetEventListCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetEventsList(category_id, lastupdate, lastreadtime,new IDrPalmRequestGetEventListCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}
				@Override
				public void onLoading() throws RemoteException {
					// TODO Auto-generated method stub
					callback.onLoading();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * 拿活动详细接口
	 */
	public boolean GetEventDetail(String eventid, int allfield, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetEventDetail(eventid, allfield, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * 获取已发明细接口
	 */
	public boolean GetEventReadInfo(String eventid, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetEventReadInfo(eventid, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	/*
	 * 拿已发活动列表
	 */
	public boolean GetPublishEventList(int category_id, String lastupdate, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetPublishEventList(category_id, lastupdate, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * 拿已发活动详细接口
	 */
	public boolean GetPublishEventDetail(String eventid, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetPublishEventDetail(eventid, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 发送通告
	 * @param item
	 * @param callback
	 * @return
	 */
	public boolean SubmitEvent(EventDraftItem item, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.SubmitEvent(item, new IDrPalmRequestCallback.Stub(){

				@Override
				public void onError(String err) throws RemoteException {
					callback.onError(err);
				}

				@Override
				public void onSuccess() throws RemoteException {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * 拿反馈内容列表接口
	 */
	public boolean GetReplyInfo(String eventid, String awspubid, String lastawstime, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetReplyInfo(eventid, awspubid, lastawstime, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 活动反馈发送接口
	 * @param eventId
	 * @param replyContent
	 * @param callback
	 * @return
	 */
	public boolean ReplyPost(final Integer eventId, String aswpubid, final String replyContent, final RequestOperationCallback callback)
	{
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.ReplyPost(eventId, aswpubid, replyContent, new IDrPalmRequestCallback.Stub() {

				@Override
				public void onSuccess() throws RemoteException {
					// TODO Auto-generated method stub
					callback.onSuccess();

				}

				@Override
				public void onError(String err) throws RemoteException {
					// TODO Auto-generated method stub
					callback.onError(err);
				}
			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * 拿组织架构
	 */
	public boolean GetOrganization(final RequestOrganizationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetOrganization(new IDrPalmRequestOrganizationCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess(OrganizationBackItem item) {
					callback.onSuccess(item);
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * 获取交流联系人列表
	 */
//	public boolean GetContactList(final RequestOperationCallback callback){
//		if(null == mDrPalmService)
//			return false;
//		try {
//			return mDrPalmService.GetContactList(new IDrPalmRequestCallback.Stub(){
//				@Override
//				public void onError(String err) {
//					callback.onError(err);
//				}
//				@Override
//				public void onSuccess() {
//					callback.onSuccess();
//				}
//
//			});
//		}catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return false;
//	}
	
	/*
	 * 拿交流内容列表
	 */
	public boolean GetContactMsgs(String contact_id, String lastupdate, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetContactMsgs(contact_id, lastupdate, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * 交流回复发送接口
	 */
	public boolean SendContactMsg(String contactid, String body, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.SendContactMsg(contactid, body, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * 拿系统信息列表接口
	 */
	public boolean GetSysMsgs(String lastid, final RequestGetEventListCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetSysMsgs(lastid, new IDrPalmRequestGetEventListCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}
				@Override
				public void onLoading() throws RemoteException {
					// TODO Auto-generated method stub
					callback.onLoading();
				}
			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * 拿系统信息详细接口
	 */
	public boolean GetSysMsgContent(String sysmsgid, final RequestOperationCallback callback){
		if(null == mDrPalmService)
			return false;
		try {
			return mDrPalmService.GetSysMsgContent(sysmsgid, new IDrPalmRequestCallback.Stub(){
				@Override
				public void onError(String err) {
					callback.onError(err);
				}
				@Override
				public void onSuccess() {
					callback.onSuccess();
				}

			});
		}catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 初始化服务中的其它接口类
	 * @param username
	 * @param sessionKey
	 */
	public void initRequestInterface(String userid,String sessionKey){
		if(null == mDrPalmService)
			return ;
		try {
			mDrPalmService.initRequest(userid, sessionKey);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
