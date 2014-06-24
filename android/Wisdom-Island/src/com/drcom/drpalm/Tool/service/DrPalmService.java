package com.drcom.drpalm.Tool.service;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.DB.CommunicationDB;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.DB.FavDB;
import com.drcom.drpalm.DB.LatestNewsDB;
import com.drcom.drpalm.DB.LoginDB;
import com.drcom.drpalm.DB.MyPhotoDB;
import com.drcom.drpalm.DB.NavigationDB;
import com.drcom.drpalm.DB.NewsDB;
import com.drcom.drpalm.DB.OrganizationDB;
import com.drcom.drpalm.DB.ResourceMsgDB;
import com.drcom.drpalm.DB.SystemInfoDB;
import com.drcom.drpalm.DB.UpdateTimeDB;
import com.drcom.drpalm.Tool.ResourceManagement;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.request.BaseParse;
import com.drcom.drpalm.Tool.request.CommonRequest;
import com.drcom.drpalm.Tool.request.CommunicateParse;
import com.drcom.drpalm.Tool.request.CommunicateRequest;
import com.drcom.drpalm.Tool.request.EventUpdateTimeRequest;
import com.drcom.drpalm.Tool.request.EventsParse;
import com.drcom.drpalm.Tool.request.EventsRequest;
import com.drcom.drpalm.Tool.request.GatewayParse;
import com.drcom.drpalm.Tool.request.GatewayRequest;
import com.drcom.drpalm.Tool.request.LatestEventsRequest;
import com.drcom.drpalm.Tool.request.LatestNewsParse;
import com.drcom.drpalm.Tool.request.LatestNewsRequest;
import com.drcom.drpalm.Tool.request.NavigationParse;
import com.drcom.drpalm.Tool.request.NavigationRequest;
import com.drcom.drpalm.Tool.request.NewsParse;
import com.drcom.drpalm.Tool.request.NewsRequest;
import com.drcom.drpalm.Tool.request.NewsUpdateTimeParse;
import com.drcom.drpalm.Tool.request.NewsUpdateTimeRequest;
import com.drcom.drpalm.Tool.request.NurseryRequest;
import com.drcom.drpalm.Tool.request.OrganizationParse;
import com.drcom.drpalm.Tool.request.OrganizationRequest;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.SetMailRequest;
import com.drcom.drpalm.Tool.request.SystemInfoParse;
import com.drcom.drpalm.Tool.request.SystemInfoRequest;
import com.drcom.drpalm.Tool.request.ViewRequestCallback;
import com.drcom.drpalm.objs.AwsContentItem;
import com.drcom.drpalm.objs.CommunicateItem;
import com.drcom.drpalm.objs.ConsultDraftItem;
import com.drcom.drpalm.objs.ContactItem;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.NavigationItem;
import com.drcom.drpalm.objs.NewsItem;
import com.drcom.drpalm.objs.NoticeTypeItem;
import com.drcom.drpalm.objs.OrgLimitItem;
import com.drcom.drpalm.objs.OrganizationBackItem;
import com.drcom.drpalm.objs.OrganizationItem;
import com.drcom.drpalm.objs.SystemInfoItem;
import com.drcom.drpalm.objs.ToursItem;
import com.drcom.drpalm.objs.UpdateTimeItem;

public class DrPalmService extends Service{

	//private IDrPalmRequest.Stub mBinder;
	// member variable
	private String mUsername = "";
	private int mLastUpdatePublishedEventCount = 0;
	// Request
	private GatewayRequest mGatewayRequest = new GatewayRequest();
	private NavigationRequest mNavigationRequest = new NavigationRequest();
	private EventsRequest mEventsRequest;
	private CommonRequest mCommonRequest;
	private RequestManager mRequestManager;	//新的请求处理类
	private OrganizationRequest mOrganizationRequest;
	private NewsRequest mNewsRequest;
	private NurseryRequest mNurseryRequest;
	private CommunicateRequest mCommunicateRequest;
	private SystemInfoRequest mSystemInfoRequest;
	private SetMailRequest mSetMailRequest;
	private LatestNewsRequest mLatestNewsRequest;
	private LatestEventsRequest mLatestEventsRequest;
	private NewsUpdateTimeRequest mNewsUpdateTimeRequest;
	private EventUpdateTimeRequest mEventUpdateTimeRequest;
	// DB
	private NewsDB mNewsDB;
	private EventsDB mEventsDB;
	private OrganizationDB mOrgDB;
	private CommunicationDB mCommunicationDB;
	private SystemInfoDB mSystemInfoDB;
	private LatestNewsDB mLatestNewsDB;
	private UpdateTimeDB mUpdateTimeDB;
	private NavigationDB mNavigationDB;
	private ResourceMsgDB mResourceMsgDB;
	private LoginDB mLoginDB;
	private FavDB mFavDB;
	private MyPhotoDB mMyPhotoDB;
	// Management
	private PreferenceManagement mPreferenceManagement;	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("DrPalmService","DrPalmService:onBind");  
		return mBinder;
	}
	@Override
	public void onCreate(){
		Log.d("DrPalmService","DrPalmService:onCreate");
		mNewsDB = NewsDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
		mEventsDB = EventsDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);		
		mOrgDB = OrganizationDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
		mCommunicationDB = CommunicationDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
		mSystemInfoDB = SystemInfoDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
		mLatestNewsDB = LatestNewsDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
		mUpdateTimeDB = UpdateTimeDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
		mNavigationDB = NavigationDB.getInstance(GlobalVariables.gAppContext);
		mResourceMsgDB = ResourceMsgDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
		mLoginDB = LoginDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
		mFavDB = FavDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
		mMyPhotoDB = MyPhotoDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
		mPreferenceManagement = PreferenceManagement.getInstance(GlobalVariables.gAppContext);
	}
	@Override
	public void onStart(Intent intent, int startId){
		Log.d("DrPalmService:onStart : id = ", String.valueOf(startId));
	}
	private DrPalmRequest mBinder = new DrPalmRequest();
	public class DrPalmRequest extends IDrPalmRequest.Stub{
		/**
		 * 重新初始化DB接口，防止学校切换导致写入同一个库的问题（由于service绑定Application启动，导致先于获得schoolkey）
		 */
		public boolean initDB()throws RemoteException{
			Log.d("DrPalmService","DrPalmService:onCreate");
			mNewsDB = NewsDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
			mEventsDB = EventsDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);		
			mOrgDB = OrganizationDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
			mCommunicationDB = CommunicationDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
			mSystemInfoDB = SystemInfoDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
			mLatestNewsDB = LatestNewsDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
			mUpdateTimeDB = UpdateTimeDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
			mResourceMsgDB = ResourceMsgDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
			mLoginDB = LoginDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
			mFavDB = FavDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
			mMyPhotoDB = MyPhotoDB.getInstance(GlobalVariables.gAppContext,GlobalVariables.gSchoolKey);
			return true;
		}
		@Override
		public boolean GetNetworkGate(String domain, String schoolKey,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mGatewayRequest)
				return false;
			return mGatewayRequest.GetNetworkGate(
					domain, 
					schoolKey, 
					new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(final BaseParse parse) {
					GatewayParse gateParse = (GatewayParse)parse;
					ArrayList<HashMap<String,String>> objectList = gateParse.parseGateway();
					if(objectList == null || objectList.size() == 0){
						return ;
					}
					String strAddr = objectList.get(0).get(GatewayParse.GATEWAYADDR);
					String strPort = objectList.get(0).get(GatewayParse.GATEWAYPORT);
					String strAccUrl = objectList.get(0).get(GatewayParse.GATEWAYURL);
					String strSchoolId = gateParse.parseSchoolID();
					String strDomain = strAddr + ":" + strPort;						
					GlobalVariables.gGateawayDomain = strDomain;
//					GlobalVariables.gGateawayDomain = "192.168.24.211:8001";
					GlobalVariables.gSchoolId = strSchoolId;
//					GlobalVariables.gAccUrl = URLDecoder.decode(strAccUrl);
					GlobalVariables.gAccUrl = strAccUrl;	//2014.2.18 zjj
					System.out.println("GetNetwork Successful@@@@@@@@GatewayDomain = " +GlobalVariables.gGateawayDomain + "SchoolId = " +GlobalVariables.gSchoolId );
					mNewsRequest = new NewsRequest(strDomain, strSchoolId);	
					mNurseryRequest = new NurseryRequest(strDomain, strSchoolId);
					mNewsUpdateTimeRequest = new NewsUpdateTimeRequest(strDomain, strSchoolId);
					mLatestNewsRequest = new LatestNewsRequest(strDomain, strSchoolId);
					//新的请求接口
					if(mRequestManager == null){
						mRequestManager = new RequestManager(GlobalVariables.gGateawayDomain, 
								GlobalVariables.gSchoolId);
					}else{
						mRequestManager.init(GlobalVariables.gGateawayDomain, 
								GlobalVariables.gSchoolId);
					}
					
					try {
						listener.onSuccess();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
			});
		}
		
		public boolean GetSchoolList(String domain, final String parentid,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mNavigationRequest)
				return false;
			return mNavigationRequest.GetSchoolList(
					domain, 
					parentid, 
					new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(final BaseParse parse) {					
					new Thread() {					
						@Override
						public void run() {					
							synchronized(mNavigationDB) {
								NavigationParse navigationParse = (NavigationParse)parse;
								List<NavigationItem> navigationItems = navigationParse.parseNavigation();				
								if(navigationItems == null) {
									onError("");
									return;
								}	
								mNavigationDB.startTransaction();
								mNavigationDB.clearChildID(parentid);
								for(NavigationItem item : navigationItems) {							
									mNavigationDB.saveNavigationItem(item);
								}							
								mNavigationDB.endTransaction();				
							}
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
				}			
			});
		}
		
		public boolean SearchSchool(String domain, final String appid, final String searchkey,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mNavigationRequest)
				return false;
			return mNavigationRequest.SearchSchool(
					domain, 
					appid,
					searchkey, 
					new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(final BaseParse parse) {					
					new Thread() {					
						@Override
						public void run() {					
							synchronized(mNavigationDB) {
								NavigationParse navigationParse = (NavigationParse)parse;
								List<NavigationItem> navigationItems = navigationParse.parseNavigation();				
								if(navigationItems == null) {
									onError("");
									return;
								}	
								mNavigationDB.startTransaction();
								mNavigationDB.clearChildBySearchKey(searchkey);
								for(NavigationItem item : navigationItems) {							
									mNavigationDB.saveNavigationItem(item);
								}							
								mNavigationDB.endTransaction();				
							}
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
				}			
			});
		}

		
		
		@Override
		public boolean LoginGateway(final String userId, String pass,String identfy,String packagename,
				final IDrPalmRequestLoginCallback listener) throws RemoteException {
				return mGatewayRequest.LoginGateway(GlobalVariables.gGateawayDomain,
					GlobalVariables.gSchoolId
					, userId, pass,identfy,packagename,
					new ViewRequestCallback(){
				@Override
				public void onError(String err) {							
					try {
						listener.onError(err);
					} catch (RemoteException e) {						
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(BaseParse parse) {
//					GatewayParse loginParse = (GatewayParse)parse;	
//					LoginSucItem item = new LoginSucItem(loginParse.paresUsername(),
//					loginParse.paresAccessType(), 
//					loginParse.paresSessionKey());
//					GlobalVariables.gPushSettingInfo = loginParse.parsePushSetting();
//					mUsername = userId; 
//					mEventsRequest = new EventsRequest(GlobalVariables.gGateawayDomain,
//							GlobalVariables.gSchoolId,
//							item.sessionKey);
//					mCommonRequest = new CommonRequest(GlobalVariables.gGateawayDomain,
//							GlobalVariables.gSchoolId,
//							item.sessionKey);
//					mOrganizationRequest = new OrganizationRequest(GlobalVariables.gGateawayDomain,
//							GlobalVariables.gSchoolId,
//							item.sessionKey);
//					mCommunicateRequest = new CommunicateRequest(GlobalVariables.gGateawayDomain, 
//							GlobalVariables.gSchoolId, 
//							item.sessionKey);
//					mSystemInfoRequest = new SystemInfoRequest(GlobalVariables.gGateawayDomain, 
//							GlobalVariables.gSchoolId, 
//							item.sessionKey);
//					mSetMailRequest = new SetMailRequest(GlobalVariables.gGateawayDomain, 
//							GlobalVariables.gSchoolId, 
//							item.sessionKey);	
//					mLatestEventsRequest = new LatestEventsRequest(GlobalVariables.gGateawayDomain, 
//							GlobalVariables.gSchoolId, 
//							item.sessionKey);
//					mEventUpdateTimeRequest = new EventUpdateTimeRequest(GlobalVariables.gGateawayDomain, 
//							GlobalVariables.gSchoolId, 
//							item.sessionKey);
//					
//					try {
//						listener.onSuccess(item);
//					} catch (RemoteException e) {						
//						e.printStackTrace();
//					}	
				}						
			});
		}

		@Override
		public boolean Logout(final IDrPalmRequestCallback listener)
				throws RemoteException {
			if(null == mCommonRequest)
				return false;
			return mCommonRequest.Logout(new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
				@Override
				public void onSuccess(BaseParse parse) {
					try {
						listener.onSuccess();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
			});
		}

		@Override
		public boolean PushInfo(boolean bPush, boolean bSound, boolean bShake,
				String time, final IDrPalmRequestCallback listener)
				throws RemoteException {
			if(null == mCommonRequest)
				return false;
			return mCommonRequest.PushInfo(bPush, bSound, bShake, time, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
				@Override
				public void onSuccess(BaseParse parse) {
					try {
						listener.onSuccess();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
			});
		}
		
		

		/**
		 * 提交错误log和bug接口
		 */
		public boolean SubmitProblem(final String problem,final String suggestion,
				final IDrPalmRequestCallback listener)
				throws RemoteException {
			// TODO Auto-generated method stub
			if(mCommonRequest == null)
				return false;
			return mCommonRequest.SubmitProblem(problem, suggestion, new ViewRequestCallback() {
				
				@Override
				public void onSuccess(BaseParse parse) {
					// TODO Auto-generated method stub
					try{
					listener.onSuccess();
					}
					catch (Exception e) {
						// TODO: handle exception
					}
				}
				
				@Override
				public void onError(String err) {
					// TODO Auto-generated method stub
					try{
						listener.onError(err);
						}
						catch (Exception e) {
							// TODO: handle exception
						}
				}
			});
		}
		
		/**
		 * 设置邮箱接口
		 */
		public boolean SetUserEmail(String email,
				final IDrPalmRequestCallback listener)
				throws RemoteException {
			// TODO Auto-generated method stub
			if(mSetMailRequest == null)
				return false;
			return mSetMailRequest.SetUserEmail(email, new ViewRequestCallback() {
				
				@Override
				public void onSuccess(BaseParse parse) {
					// TODO Auto-generated method stub
					try{
					listener.onSuccess();
					}
					catch (Exception e) {
						// TODO: handle exception
					}
				}
				
				@Override
				public void onError(String err) {
					// TODO Auto-generated method stub
					try{
						listener.onError(err);
						}
						catch (Exception e) {
							// TODO: handle exception
						}
				}
			});
		}
		
		/***
		 * 获取最新新闻接口，不需要sessionKey
		 */
		public boolean GetNewsInfoList(final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mLatestNewsRequest)
				return false;
			return mLatestNewsRequest.GetNewsInfoList(new ViewRequestCallback(){
				@Override
				public void onError(String err) {				
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
				@Override
				public void onSuccess(final BaseParse parse) {
					new Thread() {					
						@Override
						public void run() {					
							synchronized(mUpdateTimeDB) {
								LatestNewsParse latestNewsParse = (LatestNewsParse)parse;
								List<UpdateTimeItem> latestNewsItems = latestNewsParse.parseLatestNewsModuleItems();					
								if(latestNewsItems == null) {
									onError("");
									return;
								}	
								mUpdateTimeDB.startTransaction();
								for(UpdateTimeItem item : latestNewsItems) {							
									item.user = mUsername;
									mUpdateTimeDB.saveUpdateTimeItem(item);
								}
								mUpdateTimeDB.endTransaction();	
							}
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}			
			});
		}
		
		/**
		 * 获取最新通告接口
		 */
		public boolean GetEventsInfoList(final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mLatestEventsRequest)
				return false;
			return mLatestEventsRequest.GetEventsInfoList(new ViewRequestCallback(){
				@Override
				public void onError(String err) {				
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
				@Override
				public void onSuccess(final BaseParse parse) {
					new Thread() {					
						@Override
						public void run() {					
							synchronized(mUpdateTimeDB) {
								LatestNewsParse latestNewsParse = (LatestNewsParse)parse;
								List<UpdateTimeItem> latestNewsItems = latestNewsParse.parseLatestEventsModuleItems();				
								if(latestNewsItems == null) {
									onError("");
									return;
								}	
								mUpdateTimeDB.startTransaction();
								for(UpdateTimeItem item : latestNewsItems) {							
									item.user = mUsername;
									mUpdateTimeDB.saveUpdateTimeItem(item);
								}							
								mUpdateTimeDB.endTransaction();				
							}
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}			
			});
		}
		
		/**
		 * 获取新闻模块最后更新时间
		 * @param listener
		 * @return
		 * @throws RemoteException
		 */
		public boolean GetNewsLastUpdate(final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mNewsUpdateTimeRequest)
				return false;
			return mNewsUpdateTimeRequest.GetNewsLastUpdate(new ViewRequestCallback(){
				@Override
				public void onError(String err) {				
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
				@Override
				public void onSuccess(final BaseParse parse) {
					new Thread() {					
						@Override
						public void run() {					
							synchronized(mUpdateTimeDB) {
								NewsUpdateTimeParse updateTimeParse = (NewsUpdateTimeParse)parse;
								List<UpdateTimeItem> updateTimeItems = updateTimeParse.parseNewsUpdateItems();				
								if(updateTimeItems == null) {
									onError("");
									return;
								}	
								mUpdateTimeDB.startTransaction();
								for(UpdateTimeItem item : updateTimeItems) {							
									item.user = mUsername;
									mUpdateTimeDB.saveUpdateTimeItem(item);
								}
								mUpdateTimeDB.endTransaction();	
							}
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}			
			});
		}
		
		/**
		 * 获取通告模块最后更新时间
		 * @param listener
		 * @return
		 * @throws RemoteException
		 */
		public boolean GetEventsLastUpdate(final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mEventUpdateTimeRequest)
				return false;
			return mEventUpdateTimeRequest.GetEventsLastUpdate(new ViewRequestCallback(){
				@Override
				public void onError(String err) {				
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
				@Override
				public void onSuccess(final BaseParse parse) {
					new Thread() {					
						@Override
						public void run() {					
							synchronized(mUpdateTimeDB) {
								NewsUpdateTimeParse updateTimeParse = (NewsUpdateTimeParse)parse;
								List<UpdateTimeItem> updateTimeItems = updateTimeParse.parseNewsUpdateItems();				
								if(updateTimeItems == null) {
									onError("");
									return;
								}	
								mUpdateTimeDB.startTransaction();
								for(UpdateTimeItem item : updateTimeItems) {							
									item.user = mUsername;
									mUpdateTimeDB.saveUpdateTimeItem(item);
								}							
								mUpdateTimeDB.endTransaction();				
							}
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}			
			});
		}

		@Override
		public boolean KeepAlive(final IDrPalmRequestCallback listener)
				throws RemoteException {
			if(null == mCommonRequest)
				return false;
			return mCommonRequest.Logout(new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
				@Override
				public void onSuccess(BaseParse parse) {
					try {
						listener.onSuccess();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
			});
		}

		public boolean GetEventsList(int category_id, String lastupdate, String lastreadtime,final IDrPalmRequestGetEventListCallback listener)
				throws RemoteException {
			if(null == mEventsRequest)
				return false;
			return mEventsRequest.GetEventsList(category_id, lastupdate, lastreadtime, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
				@Override
				public void onSuccess(BaseParse parse) {
					final EventsParse eventsParse = (EventsParse)parse;
					final List<EventDetailsItem> eventItems = eventsParse.parseEvents();
					new Thread() {					
						@Override
						public void run() {
							int itemCount = 0;						
							synchronized(mEventsDB) {
								mEventsDB.startTransaction();
								for(EventDetailsItem item : eventItems) {								
									item.user = mUsername;
									if(mEventsDB.saveEventsItem(item)){
										itemCount++;
									}
								}
								mEventsDB.endTransaction();
							}						
							mPreferenceManagement.markEventsCategoryAsFresh();
							mPreferenceManagement.markEventsCountOfFresh(itemCount);
							if(eventsParse.parseRetainCount() == 0){
								try {
									listener.onSuccess();
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else{
								try {
									listener.onLoading();
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}.start();
					
				}
			});
		}
		
		public  boolean GetEventDetail(String eventid, final int allfield, final IDrPalmRequestCallback listener)throws RemoteException {
			if(null == mEventsRequest)
				return false;
			return mEventsRequest.GetEventDetail(eventid, allfield, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
				@Override
				public void onSuccess(BaseParse parse) {
					EventsParse eventsParse = (EventsParse)parse;
					final EventDetailsItem eventItems = eventsParse.parseEventsDetail();
					new Thread() {					
						@Override
						public void run() {						
							synchronized(mEventsDB) {
								mEventsDB.startTransaction();							
								eventItems.user = mUsername;
								mEventsDB.saveEventsDetail(eventItems,allfield);
								mEventsDB.endTransaction();
							}						
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}
			});
		}
		
		/**
		 * 获取已读明细信息
		 */
		public  boolean GetEventReadInfo(final String eventid, final IDrPalmRequestCallback listener)throws RemoteException {
			if(null == mEventsRequest)
				return false;
			return mEventsRequest.GetEventReadInfo(eventid, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
				@Override
				public void onSuccess(BaseParse parse) {
					EventsParse eventsParse = (EventsParse)parse;
					final EventDetailsItem eventItems = eventsParse.parseEventsReadInfo();
					new Thread() {					
						@Override
						public void run() {						
							synchronized(mEventsDB) {
								mEventsDB.startTransaction();							
								eventItems.user = mUsername;
								eventItems.eventid = Integer.valueOf(eventid);
								mEventsDB.saveEventReadInfo(eventItems);
								mEventsDB.endTransaction();
							}						
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}
			});
		}
		
		public boolean GetPublishEventList(int category_id, String lastupdate, final IDrPalmRequestCallback listener)
			throws RemoteException {
			if(null == mEventsRequest)
				return false;
			return mEventsRequest.GetPublishEventList(category_id, lastupdate, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
				listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
				@Override
				public void onSuccess(BaseParse parse) {
					final EventsParse eventsParse = (EventsParse)parse;
					final List<EventDetailsItem> eventItems = eventsParse.parseEvents();
					new Thread() {					
						@Override
						public void run() {
							int itemCount = 0;						
							synchronized(mEventsDB) {
								mEventsDB.startTransaction();
								for(EventDetailsItem item : eventItems) {								
									item.user = mUsername;
									if(mEventsDB.savePublishEventsItem(item)){
										itemCount++;
									}	
								}
								mEventsDB.endTransaction();
							}						
							mPreferenceManagement.markEventsCategoryAsFresh();
							mPreferenceManagement.markEventsCountOfFresh(itemCount);
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
			
				}
			});
		}
		
		public  boolean GetPublishEventDetail(String eventid, final IDrPalmRequestCallback listener)throws RemoteException {
			if(null == mEventsRequest)
				return false;
			return mEventsRequest.GetPublishEventDetail(eventid, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
				@Override
				public void onSuccess(BaseParse parse) {
					EventsParse eventsParse = (EventsParse)parse;
					final EventDetailsItem eventItems = eventsParse.parseEventsDetail();
					new Thread() {					
						@Override
						public void run() {						
							synchronized(mEventsDB) {
								mEventsDB.startTransaction();							
								eventItems.user = mUsername;
								mEventsDB.savePublishEventsDetail(eventItems);
								mEventsDB.endTransaction();
							}						
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}
			});
		}
		
		public  boolean GetReplyInfo(final String eventid, final String awspubid, String lastawstime, final IDrPalmRequestCallback listener)throws RemoteException {
			if(null == mEventsRequest)
				return false;
			return mEventsRequest.GetReplyInfo(eventid, awspubid, lastawstime, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
				@Override
				public void onSuccess(BaseParse parse) {
					EventsParse eventsParse = (EventsParse)parse;
					final List<AwsContentItem> awsContentItems = eventsParse.parseEventsReply();
					new Thread() {					
						@Override
						public void run() {						
							synchronized(mEventsDB) {
								mEventsDB.startTransaction();
								for(AwsContentItem item : awsContentItems){
									item.user = mUsername;
									item.eventid = Integer.parseInt(eventid);
									item.aws_group_id = awspubid;
									mEventsDB.saveAwsContent(item);
								}
								mEventsDB.endTransaction();
							}						
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}
			});
		}
		
		/**
		 * 获取交流联系人列表
		 * @param listener
		 * @return
		 * @throws RemoteException
		 */
		public boolean GetContactList(final IDrPalmRequestCallback listener)throws RemoteException {
//			if(null == mCommunicateRequest)
				return false;
//			return mCommunicateRequest.GetContactList(new ViewRequestCallback(){
//				@Override
//				public void onError(String err) {
//					try {
//						listener.onError(err);
//					} catch (RemoteException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}		
//				}
//				@Override
//				public void onSuccess(BaseParse parse) {
//					CommunicateParse communicateParse = (CommunicateParse)parse;
//					final List<ContactItem> contactItems = communicateParse.parseContacts();
//					new Thread() {					
//						@Override
//						public void run() {						
//							synchronized(mCommunicationDB) {
//								mCommunicationDB.startTransaction();
//								for(ContactItem item : contactItems){
//									item.user = mUsername;
//									mCommunicationDB.saveContacts(item);
//								}
//								mCommunicationDB.endTransaction();
//							}						
//							try {
//								listener.onSuccess();
//							} catch (RemoteException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					}.start();
//					
//				}
//			});
		}
		
		/**
		 * 获取交流回复列表
		 * @param contact_id
		 * @param lastupdate
		 * @param listener
		 * @return
		 * @throws RemoteException
		 */
		public boolean GetContactMsgs(final String contact_id, String lastupdate, final IDrPalmRequestCallback listener)throws RemoteException {
			if(null == mCommunicateRequest)
				return false;
			return mCommunicateRequest.GetContactMsgs(contact_id, lastupdate, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
				@Override
				public void onSuccess(BaseParse parse) {
					CommunicateParse communicateParse = (CommunicateParse)parse;
					final List<CommunicateItem> contentItems = communicateParse.parseCommunicateContents();
					new Thread() {					
						@Override
						public void run() {						
							synchronized(mCommunicationDB) {
								mCommunicationDB.startTransaction();
								for(CommunicateItem item : contentItems){
									item.user = mUsername;
									item.topic_id = contact_id;
									mCommunicationDB.saveCommunicateContent(item);
								}
								mCommunicationDB.endTransaction();
							}						
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}
			});
		}
		
		/**
		 * 交流发送回复接口
		 * @param contactid
		 * @param body
		 * @param listener
		 * @return
		 * @throws RemoteException
		 */
		public boolean SendContactMsg(String contactid, String body,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mCommunicateRequest)
				return false;
			return mCommunicateRequest.SendContactMsg(contactid, body, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(BaseParse parse) {				
					try {
						listener.onSuccess();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
			});
		}
		
		/**
		 * 获取系统信息列表接口
		 * @param lastupdate
		 * @param listener
		 * @return
		 * @throws RemoteException
		 */
		public boolean GetSysMsgs(String lastid, final IDrPalmRequestGetEventListCallback listener)throws RemoteException {
			if(null == mSystemInfoRequest)
				return false;
			return mSystemInfoRequest.GetSysMsgs(lastid, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
				@Override
				public void onSuccess(BaseParse parse) {
					final SystemInfoParse systemInfoParse = (SystemInfoParse)parse;
					final List<SystemInfoItem> systemInfoItems = systemInfoParse.parseSystemInfos();
					new Thread() {					
						@Override
						public void run() {						
							synchronized(mSystemInfoDB) {
								mSystemInfoDB.startTransaction();
								for(SystemInfoItem item : systemInfoItems){
									item.msg_user = mUsername;
									mSystemInfoDB.saveSystemInfoItem(item);
								}
								mSystemInfoDB.endTransaction();
							}						
							if(systemInfoParse.parseRetainCount() == 0){
								try {
									listener.onSuccess();
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else{
								try {
									listener.onLoading();
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}.start();
					
				}
			});
		}
		
		/**
		 * 获取系统信息详细接口
		 * @param sysmsgid
		 * @param listener
		 * @return
		 * @throws RemoteException
		 */
		public boolean GetSysMsgContent(String sysmsgid, final IDrPalmRequestCallback listener)throws RemoteException {
			if(null == mSystemInfoRequest)
				return false;
			return mSystemInfoRequest.GetSysMsgContent(sysmsgid, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
				@Override
				public void onSuccess(BaseParse parse) {
					SystemInfoParse systemInfoParse = (SystemInfoParse)parse;
					final SystemInfoItem item = systemInfoParse.parseSystemInfoDetail();
					new Thread() {					
						@Override
						public void run() {						
							synchronized(mSystemInfoDB) {
								mSystemInfoDB.startTransaction();
								item.msg_user = mUsername;
								mSystemInfoDB.saveSystemInfoDetail(item);
								mSystemInfoDB.endTransaction();
							}						
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}
			});
		}
		

		@Override
		public boolean GetObjectType(final IDrPalmRequestCallback listener)
				throws RemoteException {
			if(null == mEventsRequest)
				return false;
			String loc = Locale.getDefault().getLanguage();	
			String country = Locale.getDefault().getCountry();
			String languageType = null ;
			if(loc.equals("zh")){
				if(country.contains("CN")) //简体
					languageType = "cn" ;
				else //繁体
					languageType = "big" ;
			}
			else{
				languageType = "en" ;
			}
			return mEventsRequest.GetObjectType(languageType, new ViewRequestCallback(){
				@Override
				public void onError(String err) {				
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(BaseParse parse) {
					EventsParse eventsParse =(EventsParse)parse;
					final List<NoticeTypeItem>noticeTypeItems = eventsParse.parseNoticeType();
					new Thread(){
						@Override
						public void run() {
							synchronized(mEventsDB) {
								mEventsDB.startTransaction();
								if (!noticeTypeItems.isEmpty()){
									if (mEventsDB.eventTypeExists(mUsername)){
										mEventsDB.deleteNoticeType(mUsername);
									}
								}
								mEventsDB.endTransaction();
								mEventsDB.startTransaction();
								for(NoticeTypeItem item : noticeTypeItems) {
									item.user = mUsername;
									mEventsDB.saveNoticeType(item) ;
								}
								mEventsDB.endTransaction() ;
							}
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}						
					}.start();					
				}	
			});	
		}

		@Override
		public boolean GetPublishEvents(long curpost,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mEventsRequest)
				return false;				
			Date date = new Date(curpost);
			return mEventsRequest.GetPublishEvents(date, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(BaseParse parse) {
					final EventsParse eventsParse = (EventsParse)parse;
					final List<EventDetailsItem> eventItems = eventsParse.parseEvents();
					new Thread(){
						 @Override
						 public void run() {
							 int eventsItemCount = 0;
							 synchronized(mEventsDB){
								 mEventsDB.startTransaction() ;
								 for(EventDetailsItem item : eventItems){
									 item.user = mUsername;
//									 if(mEventsDB.saveEventsSendItem(item)){
//										 eventsItemCount++;
//									 }
								 }	
								 mEventsDB.endTransaction() ;							 
							 }
							 mLastUpdatePublishedEventCount += eventsItemCount;
							 if(0 == eventsParse.parseRetainCount()){
								 mPreferenceManagement.markEventsCategoryAsFresh();
								 mPreferenceManagement.markEventsCountOfFresh(mLastUpdatePublishedEventCount);					
								 mLastUpdatePublishedEventCount = 0;
								 try {
									listener.onSuccess();
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							 }	
						 }
					 }.start();
					 
				}
			});
			
		}

		@Override
		public boolean GetNewPublishEvents(long startTime, long endTime,
				long startPost, final IDrPalmRequestCallback listener)
				throws RemoteException {
			if(null == mEventsRequest)
				return false;	
			Date startDate = new Date(startTime);
			Date endDate = new Date(endTime);
			Date postDate = new Date(startPost);
			return mEventsRequest.GetNewPublishEvents(startDate, endDate, postDate, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(BaseParse parse) {
					EventsParse eventsParse = (EventsParse)parse;
					final List<EventDetailsItem> eventItems = eventsParse.parseEvents();
					new Thread(){
						 @Override
						 public void run() {
							 int itemCount = 0;
							 synchronized(mEventsDB){
								 mEventsDB.startTransaction() ;
								 for(EventDetailsItem item : eventItems){
									 item.user = mUsername;
//									 if(mEventsDB.saveEventsSendItem(item)){
//										 itemCount++;
//									 }
								 }	
								 mEventsDB.endTransaction();							 
							 }
							 mPreferenceManagement.markEventsCategoryAsFresh();
							 mPreferenceManagement.markEventsCountOfFresh(itemCount);
						 }					 
					 }.start(); 
					 try {
						listener.onSuccess();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
			});
		}

		@Override
		public boolean GetOrganization(final IDrPalmRequestOrganizationCallback listener) throws RemoteException {
			if(null == mOrganizationRequest)
				return false;
			final String preference_filename = GlobalVariables.gSchoolKey + "_organization_lastupdate" ;
			SharedPreferences preferences = getSharedPreferences(preference_filename, Context.MODE_PRIVATE);
			String lastupdate = String.valueOf(preferences.getLong("lastupdate", 0));
//			lastupdate = "0";
			return mOrganizationRequest.GetOrganization(lastupdate, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(final BaseParse parse) {				
						new Thread() {							
						@Override
						public void run() {	
							Looper.prepare();
							final OrganizationParse orgParse = (OrganizationParse)parse;
							OrganizationBackItem item = new OrganizationBackItem();
							//存储权限设置里面的可访问顶点节点
							List<OrgLimitItem> orgLimitList = new ArrayList<OrgLimitItem>();
							orgLimitList = orgParse.parseOrgLimitList();
							synchronized(mOrgDB) {
								mOrgDB.startTransaction();
								mOrgDB.clearAllLimit(mUsername);
								for(OrgLimitItem orgLimitItem : orgLimitList) {		
									//add sub item and parent item								
									// save to database
									// save item
									orgLimitItem.user = mUsername;
									mOrgDB.saveOrganizationLimit(orgLimitItem);
									// save parent item								
//									mOrgDB.saveOrgParItem(recvItem.orgID, item.orgID, mUsername);								
								}
								mOrgDB.endTransaction();	
							}
							
							GlobalVariables.gSelfOrgID = orgParse.parseSelfOrgid();
							
							//组织架构有最新更新
							if(orgParse.parseOrgLastupdate() >= 0){
								//保存最后更新时间
//								SharedPreferences preferences = getSharedPreferences(preference_filename, Context.MODE_PRIVATE);
//								Editor editor = preferences.edit();
//								editor.putLong("lastupdate",orgParse.parseOrgLastupdate());
//								editor.commit();
								item.lastupdate = orgParse.parseOrgLastupdate();
								item.url = orgParse.parseOrgUrl();
								item.checksum = orgParse.parseOrgChecksum();
							}
							
							
							try {
								listener.onSuccess(item);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}												
						}
					}.start();
				}			
			});
		}

		@Override
		public boolean GetParentOrganization(final OrganizationItem item,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mOrganizationRequest)
				return false;
			return mOrganizationRequest.GetParentOrganization(item.orgID, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(final BaseParse parse) {
					new Thread() {							
						@Override
						public void run() {
							OrganizationParse orgParse = (OrganizationParse)parse;
							ArrayList<OrganizationItem> recvOrgList = null;
							recvOrgList = orgParse.parseParentOrganization();
							synchronized(mOrgDB) {		
								// if is the root 
								if(0 == recvOrgList.size()){
//									mOrgDB.saveOrgParItem(item.orgID, 0, mUsername);
								}	
//								mOrgDB.startTransaction();
//								for(OrganizationItem itemRecv : recvOrgList) {
//									itemRecv.user = mUsername;													
////									mOrgDB.saveOrgParItem(item.orgID, itemRecv.orgID, mUsername);
//									
//								}
//								// add self item into database 							
//								item.user = mUsername;
//								mOrgDB.saveOrgItem(item);
//								mOrgDB.endTransaction();
								
								for(OrganizationItem itemRecv : recvOrgList) {
//									if(!mOrgDB.isOrgExist(itemRecv.orgID,mUsername)){									
//										try {
//											GetParentOrganization(itemRecv, listener);
//										} catch (RemoteException e) {
//											// TODO Auto-generated catch block
//											e.printStackTrace();
//										}									
//									}
//									try {
////										GetOrganization(itemRecv, listener);
//									} catch (RemoteException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
								}								
							}	
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								try {
									listener.onError(e.toString());
								} catch (RemoteException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					}.start();
				}			
			});
		}
		@Override
		public boolean GetEventReadStatus(final int eventID,
				final IDrPalmRequestGetStatusCallback listener) throws RemoteException {
			if(null == mEventsRequest)
				return false;
			return mEventsRequest.GetEventReadStatus(eventID,  new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					// TODO Auto-generated method stub
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(BaseParse parse) {
					EventsParse eventsParse = (EventsParse)parse;
					int readCount = eventsParse.parseFBCount();
					try {
//						mEventsDB.updateEventSendReadStatus(eventID, readCount,mUsername);
						listener.onSuccess(readCount);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						try {
							listener.onError(e.toString());
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}				
			});
		}
		@Override
		public boolean AutoAnwserEvent(int eventID,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mEventsRequest)
				return false;
			return mEventsRequest.AutoAnwserEvent(eventID, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(BaseParse parse) {
					try {
						listener.onSuccess();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
			});
			
		}

		@Override
		public boolean AutoAnwserEventList(int[] listId,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mEventsRequest)
				return false;
			return mEventsRequest.AutoAnwserEventList(listId, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(BaseParse parse) {
					try {
						listener.onSuccess();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
			});
		}
		
		public boolean SearchNews(String start, String searchkey,
				final IDrPalmRequestCallback listener)
				throws RemoteException {
			// TODO Auto-generated method stub
			return mNewsRequest.SearchNews(start, searchkey, new ViewRequestCallback() {
				
				@Override
				public void onSuccess(final BaseParse parse) {
					// TODO Auto-generated method stub
					new Thread() {					
						@Override
						public void run() {					
							synchronized(mNewsDB) {
								NewsParse newsParse = (NewsParse)parse;
								List<NewsItem> newsItems = newsParse.parseNewsItems();				
								if(newsItems == null) {
									onError("");
									return;
								}	
								mNewsDB.startTransaction();
								int itemCount = 0;
								for(NewsItem item : newsItems) {							
									if(mNewsDB.saveNewsItem(item, true)){
										itemCount++;
									}
								}							
								mNewsDB.endTransaction();			
							}
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}
				
				@Override
				public void onError(String err) {
					// TODO Auto-generated method stub
					try{
						listener.onError(err);
					}
					catch (Exception e) {
						// TODO: handle exception
					}
				}
			});
			//return false;
		}

		@Override
		public boolean SubmitEvent(EventDraftItem item,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mEventsRequest)
				return false;
			return mEventsRequest.SubmitEvent(item, new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(BaseParse parse) {				
					try {
						listener.onSuccess();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
			});
		}

		public boolean GetNews(final int category, String lastupdate,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mNewsRequest)
				return false;
			return mNewsRequest.GetNews(category, lastupdate, new ViewRequestCallback(){
				@Override
				public void onError(String err) {				
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
				@Override
				public void onSuccess(final BaseParse parse) {
					new Thread() {					
						@Override
						public void run() {					
							synchronized(mNewsDB) {
								NewsParse newsParse = (NewsParse)parse;
								List<NewsItem> newsItems = newsParse.parseNewsItems();				
								if(newsItems == null) {
									onError("");
									return;
								}	
								mNewsDB.startTransaction();
								int itemCount = 0;
								for(NewsItem item : newsItems) {							
									if(mNewsDB.saveNewsItem(item, true)){
										itemCount++;
									}
								}							
								mNewsDB.endTransaction();
								mPreferenceManagement.markNewsCategoryAsFresh();
								mPreferenceManagement.markNewsCountOfFresh(itemCount);					
							}
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}			
			});
		}
		
		
		public boolean GetNewsDetail(int story_id, final int allfield,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mNewsRequest)
				return false;
			return mNewsRequest.GetNewsDetail(story_id, allfield, new ViewRequestCallback(){
				@Override
				public void onError(String err) {				
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
				@Override
				public void onSuccess(final BaseParse parse) {
					new Thread() {					
						@Override
						public void run() {					
							synchronized(mNewsDB) {
								NewsParse newsParse = (NewsParse)parse;
								NewsItem item = new NewsItem();
								item = newsParse.parseNewsItem();				
								if(item == null) {
									onError("");
									return;
								}	
								mNewsDB.startTransaction();
								mNewsDB.saveNewsDetailsItem(item,allfield, false);						
								mNewsDB.endTransaction();					
							}
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}			
			});
		}
		
		@Override
		public boolean GetTours(final IDrPalmRequestResourceCallback listener, final IDrPalmRequestCallback pswListener)
				throws RemoteException {
			if(null == mGatewayRequest)
				return false;
//			Date date = mPreferenceManagement.getLastUpdate(GlobalVariables.gSchoolKey);
//			if(GlobalVariables.gSeqid.length()>0) //定制版
//				date = mPreferenceManagement.getCustomizeLastUpdate(GlobalVariables.gSeqid);
			ResourceManagement r = new ResourceManagement();//.getResourceManagement();
			ToursItem titem = r.getResourceMsgItem();
			Date date = DateFormatter.getDateFromMilliSecondsString(titem.lastmdate + "000");	//服务器给的时间是秒
			GlobalVariables.gAccUrl = titem.url;
			
			Log.i("zjj", "请求前,取得资源包的Lastmdate:" + date.toGMTString());
			
			String model = android.os.Build.MODEL;
			String system = "Android " + android.os.Build.VERSION.RELEASE;
			System.out.println("@@@@GatewayDomain = " +GlobalVariables.gGateawayDomain + "SchoolId = " +GlobalVariables.gSchoolId );
			return mGatewayRequest.GetTours(GlobalVariables.gGateawayDomain,
					GlobalVariables.gSchoolId, 				
					date,
					GlobalVariables.nDisplayWidth,
					GlobalVariables.nDisplayHeight,
					GlobalVariables.nDensity,
					system,
					model, 
					new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(BaseParse parse) {
					GatewayParse toursParse = (GatewayParse)parse;
					String lastUpdateDate = toursParse.parseLastUpdateDate();						
					ArrayList<ToursItem> list = toursParse.parseToursItems();	
//					for(ToursItem item:list){
//						//item.date = lastUpdateDate;
//						
//					}
					
//					if(toursParse.parsePswUrlOperate()){
//						GlobalVariables.gAccUrl = toursParse.parsePswUrl();
//						try {
//							pswListener.onSuccess();//lastUpdateDate.getTime());
//						} catch (RemoteException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}	
//					}else{
//						String errorCode = toursParse.parsePswUrlError();
//						try {
//							pswListener.onError(errorCode);
//						} catch (RemoteException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
					
					if(list.size()>0){
//						ToursItem item = list.get(0);
						ToursItem item = new ToursItem();
						item.name = list.get(0).name;
						item.size = list.get(0).size;
						item.verifycode = list.get(0).verifycode;
						item.schoolid = GlobalVariables.gSchoolId;
						item.lastmdate = lastUpdateDate;
						item.url = GlobalVariables.gAccUrl;
						mResourceMsgDB.startTransaction();
						mResourceMsgDB.saveToursItem(item);		
						mResourceMsgDB.endTransaction();
					}
					try {
						listener.onSuccess(list,0);//lastUpdateDate.getTime());
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//保存考勤地址
					mResourceMsgDB.startTransaction();
					mResourceMsgDB.SaveAttendanceUrl(GlobalVariables.gSchoolId, GlobalVariables.gAttendanceUrl);
					mResourceMsgDB.endTransaction();
				}			
			});
		}

		@Override
		public boolean GetSchoolKey(String domain, String seqid,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mGatewayRequest)
				return false;
			return mGatewayRequest.GetSchoolKey(domain,
					seqid,
					new ViewRequestCallback(){
				@Override
				public void onError(String err) {
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				@Override
				public void onSuccess(BaseParse parse) {
					GatewayParse seqidParse = (GatewayParse)parse;	
					GlobalVariables.gSchoolKey = seqidParse.parseSchoolKey();
					mPreferenceManagement.markCustomizeSchoolKey(GlobalVariables.gSchoolKey);
					try {
						listener.onSuccess();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
			});
		}

		public boolean ReplyPost(int eventid, String aswpubid,
				String replyContent, final IDrPalmRequestCallback listener)
				throws RemoteException {
			// TODO Auto-generated method stub
			if(null == mEventsRequest)
				return false;
			return mEventsRequest.ReplyPost(eventid, aswpubid, replyContent, new ViewRequestCallback() {
				
				@Override
				public void onSuccess(BaseParse parse) {
					// TODO Auto-generated method stub
					try
					{
						listener.onSuccess();
					}
					catch (RemoteException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				
				@Override
				public void onError(String err) {
					// TODO Auto-generated method stub
					try
					{
						listener.onError(err);
					}
					catch (RemoteException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
				}
			});
		}
		/**
		 * 提交入托
		 */
		public boolean PutConSult(ConsultDraftItem item,
				final IDrPalmRequestCallback listener) throws RemoteException {
			if(null == mNurseryRequest)
				return false;
			return mNurseryRequest.PutConSult(item, new ViewRequestCallback(){
				@Override
				public void onError(String err) {				
					try {
						listener.onError(err);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
				@Override
				public void onSuccess(final BaseParse parse) {
					new Thread() {					
						@Override
						public void run() {					
							try {
								listener.onSuccess();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					
				}			
			});
		}
		
		@Override
		/**
		 * 初始化其它接口类
		 * @param item
		 */
		public void initRequest(String userid,String sessionKey){
			
			mUsername = userid; 
			mEventsRequest = new EventsRequest(GlobalVariables.gGateawayDomain,
					GlobalVariables.gSchoolId,
					sessionKey);
			mCommonRequest = new CommonRequest(GlobalVariables.gGateawayDomain,
					GlobalVariables.gSchoolId,
					sessionKey);
			mOrganizationRequest = new OrganizationRequest(GlobalVariables.gGateawayDomain,
					GlobalVariables.gSchoolId,
					sessionKey);
			mCommunicateRequest = new CommunicateRequest(GlobalVariables.gGateawayDomain, 
					GlobalVariables.gSchoolId, 
					sessionKey);
			mSystemInfoRequest = new SystemInfoRequest(GlobalVariables.gGateawayDomain, 
					GlobalVariables.gSchoolId, 
					sessionKey);
			mSetMailRequest = new SetMailRequest(GlobalVariables.gGateawayDomain, 
					GlobalVariables.gSchoolId, 
					sessionKey);	
			mLatestEventsRequest = new LatestEventsRequest(GlobalVariables.gGateawayDomain, 
					GlobalVariables.gSchoolId, 
					sessionKey);
			mEventUpdateTimeRequest = new EventUpdateTimeRequest(GlobalVariables.gGateawayDomain, 
					GlobalVariables.gSchoolId, 
					sessionKey);
		}
	}	
}
