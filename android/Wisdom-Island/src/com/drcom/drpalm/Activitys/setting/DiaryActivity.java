package com.drcom.drpalm.Activitys.setting;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.DB.DiaryDB;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.RefreshListView;
import com.drcom.drpalm.View.controls.RefreshListView.OnRefreshListener;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.View.setting.DiaryManager;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.DiaryItem;
import com.wisdom.island.R;

/**
 * 成长点滴
 * @author MCX
 *
 */
public class DiaryActivity extends ModuleActivity{
	
	public static final int GET_DIARY_LIST_START = 0;
	public static final int GET_DIARY_LIST_SUCCESSFUL = 1;
	public static final int GET_DIARY_LIST_FAILED = 2;
	
	private RefreshListView mListview;
	private Handler uiHandler = null;
	private DiaryManager mDiaryManager;
	private DiaryAdapter mAdapter;
	private List<DiaryItem> mData;
	private DiaryDB mDiaryDB;
	private String mUsername;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.diary_main, mLayout_body);
		hideToolbar();
		initHandler();
		initNewBtn();
		mDiaryManager = new DiaryManager(this);
		SettingManager setInstance = SettingManager.getSettingManager(this);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		mDiaryDB = DiaryDB.getInstance(this, GlobalVariables.gSchoolKey);
		mListview = (RefreshListView)findViewById(R.id.diary_list);
		mListview.setonRefreshListener(new OnRefreshListener() {// 点击文字 刷新
			@Override
			public void onRefresh() {
//				String lastupdate = String.valueOf(0);
//				if(mData.size()>0){
//					lastupdate = String.valueOf(mData.get(0).lastUpdate.getTime()/1000);
//				}
//				GetGrowdiary(lastupdate);
				Refesh();
			}
		});
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(DiaryActivity.this,DiarySendActivity.class);
				intent.putExtra(DiarySendActivity.DIARY_ID, mData.get(arg2-1).diaryId);
				startActivity(intent);
			}
		});
		
		initializeAdapter();	
		setTitleText(getString(R.string.diary));
		
		mListview.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Refesh();
			}
		}, 300);
	}
	
	private void initializeAdapter(){
		if(mData == null){
			mData = new ArrayList<DiaryItem>();
		}
		mData.clear();
		try{
			List<DiaryItem> list = mDiaryDB.getDiaryList(mUsername);
			if(list.size()>0){
				mData.addAll(list);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if(mAdapter == null){
			mAdapter = new DiaryAdapter(this, mData, uiHandler);
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
	}
	
	/**
	 * 刷新
	 */
	private void Refesh(){
		String lastupdate = String.valueOf(0);
		if(mData.size()>0){
			lastupdate = String.valueOf(mData.get(0).lastUpdate.getTime()/1000);
		}
		GetGrowdiary(lastupdate);
	}
	
	/**
	 * 请求接口
	 * @param lastupdate
	 */
	private void GetGrowdiary(String lastupdate){
		mDiaryManager.GetGrowdiary(lastupdate, uiHandler);
	}
	
	private void initNewBtn(){
		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
		Button newBtn = new Button(this);
		newBtn.setLayoutParams(p);
		newBtn.setBackgroundResource(R.drawable.btn_title_blue_selector);
		newBtn.setText(getString(R.string.btn_add));
		newBtn.setTextAppearance(DiaryActivity.this, R.style.TitleBtnText);
		setTitleRightButton(newBtn);
		newBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DiaryActivity.this,DiarySendActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void initHandler(){
		 uiHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.arg1 == GET_DIARY_LIST_START)
				{
					showProgressBar();
				}
				else if(msg.arg1 == GET_DIARY_LIST_SUCCESSFUL)
				{
					hideProgressBar();
					initializeAdapter();
					mListview.onRefreshComplete();
				}
				else if(msg.arg1 == GET_DIARY_LIST_FAILED)
				{
					hideProgressBar();	
					mListview.onRefreshComplete();
					String strError = (msg.obj != null)?(String)msg.obj:"";
					new ErrorNotificatin(DiaryActivity.this).showErrorNotification(strError);
				}else if(msg.arg1 == DiarySendActivity.SEND_DIARY_LIST_SUCCESSFUL){
					String lastupdate = String.valueOf(0);
					if(mData.size()>0){
						lastupdate = String.valueOf(mData.get(0).lastUpdate.getTime()/1000);
					}
					GetGrowdiary(lastupdate);
				}else if(msg.arg1 == DiarySendActivity.SEND_DIARY_LIST_FAILED){
					String strError = (msg.obj != null)?(String)msg.obj:"";
					new ErrorNotificatin(DiaryActivity.this).showErrorNotification(strError);
				}
			}
				
		};
	}
}
