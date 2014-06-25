package com.drcom.drpalm.Activitys.Navigation;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.View.Navigation.searchSchoolActivityManagement;
import com.drcom.drpalm.View.controls.NewSearchBar.OnEnterKeyClickListener;
import com.drcom.drpalm.View.controls.NewSearchBar.OnSearchButtonClickListener;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.objs.NavigationItem;
import com.wisdom.island.R;

public class searchSchoolActivity extends SubActivity{
	
	public static final String NAVIGATION_SEARCH_KEY = "search_key";
	public static final int SEARCH_NAVIGATION_SUCCESSFUL = 1;
	public static final int SEARCH_NAVIGATION_FAILED = 2;
	public static final int SEARCH_NAVIGATION_START = 0;
	private static final String FLAG = "flag";

	private ListView mListView;
	private String mSearchKey;
	private ProgressDialog m_pDialog;
	private NavigationListAdapter mNavigationAdapter;
	private List<NavigationItem> mNavigationList = new ArrayList<NavigationItem>();
	private Handler uiHandler = null;
	
	private searchSchoolActivityManagement mSearchSchoolActivityManagement;
	
	private GroupReceiver mGroupReceiver;	// Receiver
	
	private String mFlag="";
	private CurrentLan mCurrentLan;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContentLayout.setBackgroundResource(R.drawable.bg_main);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.navigation_search, mContentLayout);
		
		mSearchSchoolActivityManagement = new searchSchoolActivityManagement(this);
		initHandler();
		
		mSearchbar.setOnSearchButtonClickListener(new OnSearchButtonClickListener() {
			
			@Override
			public void onClick(View v, String searchKey) {
				// TODO Auto-generated method stub
				mSearchSchoolActivityManagement.searchNavigation(String.valueOf(GlobalVariables.getAgentID()),searchKey, uiHandler);
			}
		});
		
		mSearchbar.setOnEnterKeyClickListener(new OnEnterKeyClickListener() {
			
			@Override
			public void onClick(String searchKey) {
				// TODO Auto-generated method stub
				mSearchSchoolActivityManagement.searchNavigation(String.valueOf(GlobalVariables.getAgentID()),searchKey, uiHandler);
			}
		});
		
		Intent intent = getIntent();
		if(null != intent){
			Bundle bundle = intent.getExtras();
			if(bundle != null){
				if(bundle.containsKey(NAVIGATION_SEARCH_KEY)){
					mSearchKey = bundle.getString(NAVIGATION_SEARCH_KEY);
				}
				if(bundle.containsKey(FLAG)){
					mFlag = bundle.getString(FLAG);
				}
			}
		}
		if(mSearchKey != null){
			mSearchbar.setSearchText(mSearchKey);
		}
		
		//初始化dialog
		initDialog();
		
		mListView = (ListView)findViewById(R.id.school_list);
		mListView.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
						/*打开指定学校的APP 应用*/
//						Cursor schoolCursor = (Cursor) listView.getItemAtPosition(position);
//						NavigationItem schoolItem = mNavigationDB.retrieveNavigationItem(schoolCursor);
						NavigationItem schoolItem = mNavigationList.get(position);
						if(schoolItem != null){
							//判断是从哪里进来的（true为账号关联）
							if(mFlag.equals(FLAG)){
								String schoolName = "";
								mCurrentLan = LanguageManagement.getSysLanguage(searchSchoolActivity.this);
								if (mCurrentLan == CurrentLan.SIMPLIFIED_CHINESE) {
									schoolName = schoolItem.text_s;
								} else if (mCurrentLan == CurrentLan.COMPLES_CHINESE) {
									schoolName = schoolItem.text_t;
								} else if (mCurrentLan == CurrentLan.ENGLISH) {
									schoolName = schoolItem.text_en;
								}

								Intent intent = new Intent(SchoolNavigation.ACTION);
								intent.putExtra(SchoolNavigation.SCHOOLKEY, schoolItem.key);
								intent.putExtra(SchoolNavigation.SCHOOLNAME, schoolName);
								sendBroadcast(intent);
								searchSchoolActivity.this.finish();
							}else {
								mSearchSchoolActivityManagement.openAppBySchoolkey(schoolItem);
							}
							
						}
					}
				}
		);
		if(mSearchKey != null){
			initialCursorAdapter(mSearchKey);
			mSearchSchoolActivityManagement.searchNavigation(String.valueOf(GlobalVariables.getAgentID()), mSearchKey, uiHandler);
		}
		initReceiver();
	}
	
	private void initialCursorAdapter(String searchKey){
		
		mNavigationList = mSearchSchoolActivityManagement.getSearchResultList(searchKey);
		if(mNavigationList.size()>0){
			//如果是正式版本,排除带(DR_COM)字样的学校
			if(!GlobalVariables.getAppIsTestversion()){
				List<NavigationItem> tempschoollist = new ArrayList<NavigationItem>();
				for(int i = 0;i <  mNavigationList.size();i++){
					if(mNavigationList.get(i).name.indexOf("DR_COM")>-1){
						tempschoollist.add(mNavigationList.get(i));
					}
				}
				mNavigationList.removeAll(tempschoollist);
			}
			
			mListView.setVisibility(View.VISIBLE);
			if(mNavigationAdapter == null){
				mNavigationAdapter = new NavigationListAdapter(this, mNavigationList);
				mListView.setAdapter(mNavigationAdapter);
			}else{
				mNavigationAdapter.changeSourceData(mNavigationList);
				mNavigationAdapter.notifyDataSetChanged();
			}
		}else{
			mListView.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mGroupReceiver);
		if(mNavigationList != null){
			mNavigationList.clear();
			mNavigationList = null;
		}
		super.onDestroy();
	}
	
	
	private void initDialog(){
		if(m_pDialog == null){
			m_pDialog = new ProgressDialog(this);
		}
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		m_pDialog.setMessage(getResources().getString(R.string.loading));
		m_pDialog.setIndeterminate(false);
		m_pDialog.setCancelable(true);
	}
	
	private void initHandler()
	{
		uiHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.arg1 == SEARCH_NAVIGATION_START)
				{
					m_pDialog.show();
				}
				else if(msg.arg1 == SEARCH_NAVIGATION_SUCCESSFUL)
				{
					m_pDialog.cancel();
					String searchkey = (msg.obj != null)?(String)msg.obj:"";
					initialCursorAdapter(searchkey);
				}
				else if(msg.arg1 == SEARCH_NAVIGATION_FAILED)
				{
					m_pDialog.cancel();	
					String strError = (msg.obj != null)?(String)msg.obj:"";
					new ErrorNotificatin(searchSchoolActivity.this).showErrorNotification(strError);
				}
			}
			
		};
	}
	
	/**
     * initialize receiver
     */
    private void initReceiver(){
    	mGroupReceiver = new GroupReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActivityActionDefine.EXITAPP_ACTION);
        registerReceiver(mGroupReceiver,filter);
    }
	
	 /**
     * ***************
     * 广播接收
     * ***************
     */
    public class GroupReceiver extends BroadcastReceiver{
    	@Override
    	public void onReceive(Context context, Intent intent){
    		try{
    			String stringAction = intent.getAction();
    			Log.i("zjj", "stringAction:" + stringAction);
    			
    			if(stringAction.equals(ActivityActionDefine.EXITAPP_ACTION)){
    				finish();
    			}
    		}
    		catch (Exception e) {
				// TODO: handle exception
    				e.printStackTrace();
			}
        }
	}
}
