package com.drcom.drpalm.Activitys.sysinfo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.main.SchoolView;
import com.drcom.drpalm.DB.SystemInfoDB;
import com.drcom.drpalm.View.controls.FootView;
import com.drcom.drpalm.View.controls.RefreshListView;
import com.drcom.drpalm.View.controls.RefreshListView.OnRefreshListener;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.View.sysinfo.SysinfoListActivityManagement;
import com.drcom.drpalm.objs.MessageObject;
import com.drcom.drpalm.objs.SystemInfoItem;
import com.wisdom.island.R;

public class SysinfoListActivity extends ModuleActivity {
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2;	//更多请求返回成功
	public static final String CATEGORYID_KEY = "categoryidkey";
	private int PAGE_SIZE = 60;	//最多显示条数
//	private String KEY_REFLASHTIME = "sysinfoflashtime";
	
	//变量
//	private int mCategoryid = 0;	//分类ID
	private SystemInfoDB mSystemInfoDB;
	private Cursor mEventCursor = null;
	private SysinfoListCursorAdapter mAdapter;
//	private int mCurNewCount = 0;	//当前纪录数
//	private int mLastActivityId = 0;	//最后一个活动的ID
	private String mUsername = "";	
	private SettingManager setInstance ;	
//	private LoginManager mLogininstance = LoginManager.getInstance(this);
//	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
//	private int mBeforemoresum = 0;	//记录点"更新"之前的总数
//	private SharedPreferences sp;	//15分钟间隔自动刷新
//	private Editor editor;
//	private Date lastrefreshTime;
	private SysinfoListActivityManagement mSysinfoListActivityManagement;
	
	//控件
	private RefreshListView mEventsListView;
	private FootView mFooterView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.sysinfo_list_view, mLayout_body);
		
		//
		setInstance = SettingManager.getSettingManager(this);	
		mSystemInfoDB = SystemInfoDB.getInstance(this,GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
//		sp = this.getSharedPreferences(KEY_REFLASHTIME, MODE_WORLD_READABLE);
//		editor = sp.edit();
//		lastrefreshTime = new Date(sp.getLong(KEY_REFLASHTIME, 0));
		
		mSysinfoListActivityManagement = new SysinfoListActivityManagement(SysinfoListActivity.this);
		
		//
		hideToolbar();
		mEventsListView = (RefreshListView)findViewById(R.id.sysinfoListview);
		mEventsListView.setCacheColorHint(Color.WHITE) ;
		mEventsListView.setonRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	if(mEventCursor.getCount() == 0){
//            		mCurNewCount = 0;
//                	mBeforemoresum = 0;
                	sendGetEventsRequest("0");
            	}else{
            		Cursor newsCursor = (Cursor) mEventsListView.getItemAtPosition(1);
					SystemInfoItem sysinfoItem = mSystemInfoDB.retrieveSystemInfoItem(newsCursor);
					sendGetEventsRequest(sysinfoItem.msg_id + "");
            	}
            	
//            	initalizeFootView();	//添加"更多"
            }
        });
		mEventsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(view == mFooterView) {
//					mBeforemoresum = mEventCursor.getCount();
					
					Cursor newsCursor = (Cursor) mEventsListView.getItemAtPosition(mEventsListView.getCount()-2);
					mSysinfoListActivityManagement.getMore(newsCursor,mHandler);
//					initalizeCursorAdapter();
					return;
				}
				
				Cursor newsCursor = (Cursor) mEventsListView.getItemAtPosition(position);
				mSysinfoListActivityManagement.onListviewItemClick(newsCursor);
			}
		});
//		mEventsListView.setOnScrollListener(new OnScrollListener() {
//		
//		@Override
//		public void onScrollStateChanged(AbsListView view, int scrollState) {
//			// TODO Auto-generated method stub
//			if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
//		       //判断是否滚动到底部
//				if (view.getCount()-1 == PAGE_SIZE && view.getLastVisiblePosition() == view.getCount() - 1) {
//					String msg1 = getString(R.string.sysinfomaxmsg);
//					msg1 = String.format(msg1, PAGE_SIZE + "");
//					Toast.makeText(SysinfoListActivity.this,msg1,Toast.LENGTH_LONG).show();
//				}
//		     }
//		}
//		
//		@Override
//		public void onScroll(AbsListView view, int firstVisibleItem,
//				int visibleItemCount, int totalItemCount) {
//			// TODO Auto-generated method stub
//			
//		}
//	});
		
		mEventsListView.postDelayed(new Runnable() {
			
			@Override
			public void run() {
//				initalizeFootView();
				initalizeCursorAdapter();
				
				// 大于15分钟自动刷新
				if (mSysinfoListActivityManagement.isOver15mins()) {
					mEventsListView.hideHeadView();
					
//					mCurNewCount = 0;
//		        	mBeforemoresum = 0;
		        	sendGetEventsRequest("0");
//		        	initalizeFootView();	//添加"更多"

				}
			}
		},300);
		if(getIntent().getExtras().containsKey(SchoolView.TITLE)){
			setTitleText(getIntent().getExtras().getString(SchoolView.TITLE));
		}else{
		setTitleText(getString(R.string.sysinfo));
		}
	}
	
	private void initalizeCursorAdapter(){
		mEventCursor = mSystemInfoDB.getSystemInfoListCursor(mUsername,
				System.currentTimeMillis(),
				PAGE_SIZE + "");
				//(mCurNewCount + PAGE_SIZE )+"");// .getEventCursor(mCategoryid,0,0,mUsername,0);
		mEventCursor.requery();
		mEventCursor.moveToFirst();
		
		//显示到列表中
		if(mAdapter == null){
			mAdapter = new SysinfoListCursorAdapter(SysinfoListActivity.this, mEventCursor);
			mEventsListView.setAdapter(mAdapter);
		}else{
			mAdapter.changeCursor(mEventCursor);
		}

		
		//列表头
		if (mEventCursor == null) {
			mEventsListView.hideHeadView();
		} else {
			if (mEventCursor.getCount() > 0) {
				mEventsListView.hideHeadView();
//				mCurNewCount = mEventCursor.getCount();
			} else {
				mEventsListView.setHeadViewVisible();
			}
		}
		
//		//添加"更多"
//		initalizeFootView();
//		//是否还有"更多"
//		if(mBeforemoresum == mCurNewCount){
//			mEventsListView.removeFooterView(mFooterView);
//		}
	}
	
	/*
	 * 初始化列表FootView更多
	 */
//	private void initalizeFootView(){
//		if(null == mFooterView){
//			Context context = SysinfoListActivity.this;
//			mFooterView = new FootView(context);
//
//		}
//		mFooterView.setTitle(GlobalVariables.gAppResource.getString(R.string.loadmorenews));
//		if(0 == mEventsListView.getFooterViewsCount()){
//			if(mEventsListView.getCount()>1){
//				mEventsListView.addFooterView(mFooterView);
//			}
//		}
//		
//	}
	
	/**
	 * 请求网络(取列表)
	 * @param lastActivityId   0:刷新         非0：更多
	 * @param uiHandler
	 */
	private void sendGetEventsRequest(final String lastupdate){
		if(mSysinfoListActivityManagement.GetData(lastupdate, mHandler)){
			showProgressBar();
		}
		
//		//非在线登录/网络不通时,返回
//		LoginManager instance = LoginManager.getInstance(GlobalVariables.gAppContext);
//		if(HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT){	//LoginManager.OnlineStatus.ONLINE_LOGINED != instance.getOnlineStatus() ||
//			return;
//		}
//		
//		showProgressBar();
//		
//		/*
//		 * 使用RequestOperationReloginCallback类回调，当SESSIONKEY失效时，会自动登录，再请求数据接口
//		 * 注意：代码要使用private boolean isRequestRelogin = true;	登录SECCION超时重登录标志记录，以免不断重登造成死循环
//		 */
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
//		RequestGetEventListReloginCallback callback = new RequestGetEventListReloginCallback(){
//			@Override
//			public void onSuccess() {	//请求数据成功
////				super.onSuccess();
//				Message message = Message.obtain();
//				if(lastupdate.equals("0")){
//					message.arg1 = UPDATEFINISH;	//刷新
//				}else{
//					message.arg1 = MOREFINISH;		//更多
//				}
//				message.obj = new MessageObject(true,false);
//				mHandler.sendMessage(message) ;
//				
//				Log.i("zjj", "通告列表:刷新成功");
//			}
//			
//			@Override
//			public void onCallbackError(String str) {
////				super.onError(str);
//				Message message = Message.obtain();
//				message.arg1 = UPDATEFAILED;
//				message.obj = str;
//				mHandler.sendMessage(message);
//				
//				Log.i("zjj", "通告列表:刷新失败" + str);
//			}
//			
//			@Override
//			public void onLoading() {
//				// TODO Auto-generated method stub
//				Message message = Message.obtain();
//				message.arg1 = MOREFINISH;		//更多
//				message.obj = new MessageObject(true,false);
//				mHandler.sendMessage(message) ;
//			}
//			
//			@Override
//			public void onReloginError() {
//				// TODO Auto-generated method stub
////				super.onReloginError();
//				Log.i("zjj", "通告列表:自动重登录失败");
//			}
//			
//			@Override
//			public void onReloginSuccess() {
//				// TODO Auto-generated method stub
////				super.onReloginSuccess();
//				Log.i("zjj", "通告列表:自动重登录成功");
//				if(isRequestRelogin){
//					sendGetEventsRequest(lastupdate);	//自动登录成功后，再次请求数据
//					isRequestRelogin = false;
//				}
//			}
//		};
//		mRequestOperation.sendGetNeededInfo("GetSysMsgs", new Object[]{lastupdate,callback},callback.getClass().getName());
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == UPDATEFINISH){
				MessageObject obj = (MessageObject)msg.obj;
				if(obj.isSuccess){
					initalizeCursorAdapter();
					mSysinfoListActivityManagement.SaveReflashtime();
				}
			}else if(msg.arg1 == MOREFINISH){
				initalizeCursorAdapter();
				mSysinfoListActivityManagement.SaveReflashtime();
			}else if(msg.arg1 == UPDATEFAILED){
				String err = (String)msg.obj;
				if(err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))){
					GlobalVariables.showInvalidSessionKeyMessage(SysinfoListActivity.this);
				}else{
					GlobalVariables.toastShow(err);
				}
				mEventsListView.clearPullToRefresh(true);
			}
			hideProgressBar();
		}
	};
	
	@Override
	protected void onRestart() {
		super.onRestart();
		initalizeCursorAdapter();
	}
	
	@Override
	protected void onDestroy() {
		mEventCursor.close();
		mEventCursor = null;
		super.onDestroy();
	}
	
	
	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		mEventsListView.onTouchEvent(ev);
////		mGestureDetector.onTouchEvent(ev);
//		return super.dispatchTouchEvent(ev);
//	}
}
