package com.drcom.drpalm.Activitys.danceinfo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
import com.drcom.drpalm.DB.AttendanceinfoDB;
import com.drcom.drpalm.View.controls.FootView;
import com.drcom.drpalm.View.controls.RefreshListView;
import com.drcom.drpalm.View.controls.RefreshListView.OnRefreshListener;
import com.drcom.drpalm.View.danceinfo.AttendanceinfoManager;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.AttendanceinfoItem;
import com.wisdom.island.R;

/**
 * 考勤
 * ("更多"分页逻辑完善)
 * @author zhaojunjie
 *
 */
public class AttendanceActivity extends ModuleActivity {
	public static final int LOADING = -2;		//刷新中
	public static final int UPDATEFINISH = 1;	//刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2;	//更多请求返回成功
	private int PAGE_SIZE = 10;	//分页显示条数
	
	//控件
	private RefreshListView mListview;
	private FootView mFooterView = null;
	
	//变量
	private Handler uiHandler = null;
	private AttendanceinfoManager mAttendanceinfoManager;
	private AttendanceAdapter mAdapter;
	private List<AttendanceinfoItem> mData;
	private AttendanceinfoDB mDiaryDB;
	private String mUsername;
	private int mCurNewCount = 0;	//当前纪录数
	private int mPageNum = 1;		//当前页数
	private int mNextPage = 2;		//下一页
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.diary_main, mLayout_body);
		hideToolbar();
		initHandler();
		mAttendanceinfoManager = new AttendanceinfoManager(this);
		SettingManager setInstance = SettingManager.getSettingManager(this);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		mDiaryDB = AttendanceinfoDB.getInstance(this, GlobalVariables.gSchoolKey);
		mListview = (RefreshListView)findViewById(R.id.diary_list);
		mListview.setonRefreshListener(new OnRefreshListener() {// 点击文字 刷新
			@Override
			public void onRefresh() {
//				String lastupdate = String.valueOf(0);
//				if(mData.size()>0){
//					lastupdate = String.valueOf(mData.get(0).attendancetime);
//				}
//				GetGrowdiary(lastupdate);
        		ReflashData();
			}
		});
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
//				Intent intent = new Intent(AttendanceActivity.this,DiarySendActivity.class);
//				intent.putExtra(DiarySendActivity.DIARY_ID, mData.get(arg2-1).diaryId);
//				startActivity(intent);
				if(view == mFooterView) {
//					Cursor newsCursor = (Cursor) mListview.getItemAtPosition(mListview.getCount()-2);
//					EventDetailsItem newsItem = mEventsDB.retrievePublishEventDetailItem(newsCursor);
//					sendGetEventsRequest((newsItem.lastupdate.getTime()/1000) + "");
//					initalizeCursorAdapter();
					if(mData != null && mData.size()>0){
						mNextPage = mPageNum + 1;
						AttendanceinfoItem item = mData.get(mData.size() - 1);
						GetData(item.attendancetime);
					}
					
					return;
				}
			}
		});
		
		initializeAdapter();	
		
		if(mAttendanceinfoManager.isTimeToReflash()){
			mListview.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ReflashData();
				}
			}, 300);
		}
		
		if(getIntent().getExtras().containsKey(SchoolView.TITLE)){
			setTitleText(getIntent().getExtras().getString(SchoolView.TITLE));
		}else{
		setTitleText(getString(R.string.attendance_title));
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		uiHandler = null;
		mData = null;
		mAdapter = null;
		mDiaryDB = null;
		mAttendanceinfoManager.destory();
		mAttendanceinfoManager = null;
		
		super.onDestroy();
	}
	
	private void initializeAdapter(){
		if(mData == null){
			mData = new ArrayList<AttendanceinfoItem>();
		}
		mData.clear();
		try{
			List<AttendanceinfoItem> list = mDiaryDB.getAttendanceinfos(mUsername,(mPageNum * PAGE_SIZE )+"");
			mCurNewCount = list.size();
			
			if(list.size()>0){
				mData.addAll(list);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if(mAdapter == null){
			mAdapter = new AttendanceAdapter(this, mData);
			mListview.setAdapter(mAdapter);
		}else{
			mAdapter.notifyDataSetChanged();
		}
		
		//处理头的显示问题
		if(mData.size()>0){
			mListview.hideHeadView();
		}else{
			mListview.setHeadViewVisible();
		}
		
		//是否还有"更多"
		initalizeFootView();
		if(mCurNewCount<(mPageNum * PAGE_SIZE)){
			mListview.removeFooterView(mFooterView);
		}
	}
	
	/**
	 * 初始化列表FootView更多
	 */
	private void initalizeFootView(){
		if(null == mFooterView){
			Context context = AttendanceActivity.this;
			mFooterView = new FootView(context);

		}
		mFooterView.setTitle(GlobalVariables.gAppResource.getString(R.string.loadmorenews));
		if(0 == mListview.getFooterViewsCount()){
			if(mListview.getCount()>1){
				mListview.addFooterView(mFooterView);
			}
		}
	}
	
	/**
	 * 刷新数据
	 */
	private void ReflashData(){
		mCurNewCount = 0;
		mPageNum = 1;
		mNextPage = mPageNum + 1;
		GetData("0");
	}
	
	/**
	 * 取数据
	 * @param lastupdate
	 */
	private void GetData(String lastupdate){
		mAttendanceinfoManager.GetAttendanceinfo(lastupdate, uiHandler);
	}
	
	private void initHandler(){
		 uiHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.arg1 == LOADING)
				{
					showProgressBar();
				}
				else if(msg.arg1 == UPDATEFINISH || msg.arg1 == MOREFINISH)
				{
					//更多后,页数+1
					if(msg.arg1 == MOREFINISH)
						mPageNum = mNextPage;
					
					initializeAdapter();
					mListview.onRefreshComplete();
					
					
					hideProgressBar();
				}
				else if(msg.arg1 == UPDATEFAILED)
				{
					hideProgressBar();	
					mListview.onRefreshComplete();
					String strError = (msg.obj != null)?(String)msg.obj:"";
					new ErrorNotificatin(AttendanceActivity.this).showErrorNotification(strError);
				}
//				else if(msg.arg1 == DiarySendActivity.SEND_DIARY_LIST_SUCCESSFUL){
//					String lastupdate = String.valueOf(0);
//					if(mData.size()>0){
//						lastupdate = String.valueOf(mData.get(0).attendancetime);
//					}
//					GetGrowdiary(lastupdate);
//				}else if(msg.arg1 == DiarySendActivity.SEND_DIARY_LIST_FAILED){
//					String strError = (msg.obj != null)?(String)msg.obj:"";
//					new ErrorNotificatin(AttendanceActivity.this).showErrorNotification(strError);
//				}
			}
				
		};
	}
}
