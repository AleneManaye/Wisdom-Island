package com.drcom.drpalm.Activitys.LeaveApplication;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.events.EventsListActivity;
import com.drcom.drpalm.Activitys.events.EventsListCursorAdapter;
import com.drcom.drpalm.Activitys.events.sent.EventsSentListActivity;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.Activitys.main.SchoolView;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.DB.LeaveApplicationDB;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.View.LeaveApplication.LeaveApplicationMainManager;
import com.drcom.drpalm.View.controls.FootView;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.RefreshListView;
import com.drcom.drpalm.View.controls.RefreshListView.OnRefreshListener;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.LeaveApplicationMainItem;
import com.drcom.drpalm.objs.MessageObject;
import com.wisdom.island.R;

/**
 * @author zhr
 * **/
public class LeaveApplicationMainActivity extends ModuleActivity {
	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2; // 更多请求返回成功
	private int PAGE_SIZE = 10;	//分页显示条数
	private FootView mFooterView = null;
	private int mPageNum = 1;	
	
	private Button btnAdd;// 新增按钮
	private Context mContext;
	private RefreshListView listView;
	private LeaveApplicationMainAdapter adapter;
	private String mUsername = "";
	private SettingManager setInstance;
	private LeaveApplicationMainManager leaveMainManager;
	private String mLastUpdatetime = "0"; // 最顶一条消息的最后更新时间
	private LeaveApplicationDB mLeaveDB;
	private Cursor mLeaveCursor = null;
	private int mCurNewCount = 0; // 当前纪录数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.leave_application_main, mLayout_body);
		mContext = LeaveApplicationMainActivity.this;
		initUI();
	}

	private void initUI() {
		listView = (RefreshListView) findViewById(R.id.leaveApplicationMainListview);
		hideToolbar();
		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(mContext,
				GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(mContext,
				GlobalVariables.btnHeight_Titlebar));
		btnAdd = new Button(mContext);
		btnAdd.setLayoutParams(p);
		btnAdd.setBackgroundResource(R.drawable.btn_title_blue_selector);
		btnAdd.setText(getString(R.string.add));
		btnAdd.setTextAppearance(mContext, R.style.TitleBtnText);

		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, AddApplicationActivity.class);
				startActivity(intent);

			}
		});
		setTitleRightButton(btnAdd);
		BlockAdd();
		leaveMainManager = new LeaveApplicationMainManager(mContext);
		mLeaveDB = LeaveApplicationDB.getInstance(this, GlobalVariables.gSchoolKey);
		setInstance = SettingManager.getSettingManager(this);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		listView.setCacheColorHint(Color.WHITE);
		listView.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (listView.getCount() > 1) {
//					Cursor newsCursor = (Cursor) listView.getItemAtPosition(1);
//					mLastUpdatetime = leaveMainManager
//							.GetTheFristLeaveLastupdateTime(newsCursor);
					
//					Log.i("XXXX", " mLastUpdatetime:" + mLastUpdatetime);
					
					GetLeaveList(mLastUpdatetime);
				} else {
					GetLeaveList(mLastUpdatetime);
				}
//				listView.postDelayed(new Runnable() {
//					
//					@Override
//					public void run() {
//						addTestData(listView.getCount()+1);
//						initalizeCursorAdapter();
//						listView.hideHeadView();
//					}
//				}, 1000);
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(view == mFooterView) {
					mPageNum++;
					initalizeCursorAdapter();
					return;
				}
				Cursor newsCursor = (Cursor) listView
						.getItemAtPosition(position);
				leaveMainManager.onListviewItemClick(newsCursor);
			}
		});
		if(getIntent().getExtras().containsKey(SchoolView.TITLE)){
			setTitleText(getIntent().getExtras().getString(SchoolView.TITLE));
		}else{
		setTitleText(getResources().getString(R.string.ask4leave));
		}
//		GetLeaveList(mLastUpdatetime);
//		initalizeCursorAdapter();
	}
	
	private void initalizeCursorAdapter() {
		mLeaveCursor = mLeaveDB.getLeaveCursor(mUsername,(mPageNum * PAGE_SIZE )+"");
		mLeaveCursor.requery();
		mLeaveCursor.moveToFirst();

		mCurNewCount = mLeaveCursor.getCount();
		// 显示到列表中
		if (adapter == null) {
			adapter = new LeaveApplicationMainAdapter(LeaveApplicationMainActivity.this, mLeaveCursor, getmClassImageLoader());
			listView.setAdapter(adapter);
		} else {
			adapter.changeCursor(mLeaveCursor);
		}

		// 列表头
		if (mLeaveCursor == null) {
			listView.hideHeadView();
		} else {
			if (mLeaveCursor.getCount() > 0) {
				listView.hideHeadView();
			} else {
				listView.setHeadViewVisible();
			}

		}
		//是否还有"更多"
		initalizeFootView();
		if(mCurNewCount<(mPageNum * PAGE_SIZE)){
			listView.removeFooterView(mFooterView);
		}
		hideProgressBar();
	}
	
//	public void addTestData(int leaveId){
//		LeaveApplicationMainItem leaveItem = new LeaveApplicationMainItem();
//		leaveItem.content="test";
//		leaveItem.hasatt=1;
//		leaveItem.isread=0;
//		leaveItem.owner="test";
//		leaveItem.ownerid="123";
//		leaveItem.lastupdate=new Date(0);
//		leaveItem.post=new Date();
//		leaveItem.pubid="123";
//		leaveItem.pubname=leaveId+"";
//		leaveItem.start=new Date(0);
//		leaveItem.title="123";
//		leaveItem.type="sick";
//		leaveItem.user = mUsername;
//		leaveItem.leaveid = leaveId;
//		mLeaveDB.saveleavesItem(leaveItem);
//	}
	
	private void GetLeaveList(final String lastupdate) {
		showProgressBar();
		listView.setOnloadingRefreshVisible();
		leaveMainManager.GetListData(lastupdate,mHandler);
	}
	
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == UPDATEFINISH) {
				MessageObject obj = (MessageObject) msg.obj;
				if (obj.isSuccess) {
					initalizeCursorAdapter();
					leaveMainManager.SaveReflashtime();
				}
			} else if (msg.arg1 == MOREFINISH) {
				initalizeCursorAdapter();
				leaveMainManager.SaveReflashtime();
			} else if (msg.arg1 == UPDATEFAILED) {
				String err = (String) msg.obj;
				if (err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))) {
					GlobalVariables.showInvalidSessionKeyMessage(LeaveApplicationMainActivity.this);
				}else{
					GlobalVariables.toastShow(err);
				}
				listView.hideHeadView();
				hideProgressBar();
			}

		}
	};

	/**
	 * 判断是否是老师，是则不显示添加按钮，否则显示添加按钮
	 * **/
	public void BlockAdd() {
		SettingManager settingInstance = SettingManager
				.getSettingManager(mContext);
		if (settingInstance.getCurrentUserInfo() != null) {
			if (!settingInstance.getCurrentUserInfo().strUsrType
					.equals(UserInfo.USERTYPE_TEACHER)) {
				btnAdd.setVisibility(Button.VISIBLE);
			} else {
				btnAdd.setVisibility(Button.GONE);
			}
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(adapter == null){
			// 显示正在加载的title，CursorAdapter Cursor 可以为空
			adapter = new LeaveApplicationMainAdapter(LeaveApplicationMainActivity.this, mLeaveCursor, getmClassImageLoader());
			listView.setAdapter(adapter);
			listView.setOnloadingRefreshVisible();
			// 异步加载数据,以免在初始化界面时加载,导致打开窗体时卡界面
			listView.postDelayed(new Runnable() {

				@Override
				public void run() {
					initalizeCursorAdapter();
					// 大于15分钟自动刷新
					if (leaveMainManager.isTimeToReflash()) {
						GetLeaveList(mLastUpdatetime);
					}
				}
			}, 300);
		}else{
			initalizeCursorAdapter();
		}
	}
	
	@Override
	protected void onDestroy() {
		if (mLeaveCursor != null)
			mLeaveCursor.close();
		mLeaveCursor = null;
		super.onDestroy();
	}
	
	/*
	 * 初始化列表FootView更多
	 */
	private void initalizeFootView(){
		if(null == mFooterView){
			Context context = LeaveApplicationMainActivity.this;
			mFooterView = new FootView(context);

		}
		mFooterView.setTitle(GlobalVariables.gAppResource.getString(R.string.loadmorenews));
		if(0 == listView.getFooterViewsCount()){
			if(listView.getCount()>1){
				listView.addFooterView(mFooterView);
			}
		}
	}

}
