package com.drcom.drpalm.Activitys.bindaccount;

import java.util.ArrayList;
import java.util.List;

import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.Navigation.NavigationMainActivity;
import com.drcom.drpalm.Activitys.Navigation.SchoolNavigation;
import com.drcom.drpalm.DB.NavigationDB;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.objs.NavigationListItem;
import com.wisdom.island.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class InstitutionList extends ModuleActivity implements OnItemClickListener{

	public static String NAVIGATION_INSTITUTION = "institution";// 机构
	public static final String NAVIGATION_PARENT_ID = "parent_id";
	public static final String NAVIGATION_PARENT_TYPE = "parent_type";
	private static final String SEARCHSCHOOL = "flag";//标志是当前Activity发送过去的
	
	private InstitutionListAdapter mAdapter;
	private List<NavigationListItem> mList,mTempList;
	private NavigationDB mNavigationDB;
	private ListView mListview;
	private SchoolkeyReceiver mReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.search_school_list, mLayout_body);
		
		initData();
		
		mAdapter = new InstitutionListAdapter(InstitutionList.this,mTempList,R.layout.search_school_item);
		mListview = (ListView) findViewById(R.id.Lv_search_school);
		mListview.setAdapter(mAdapter);
		
		mListview.setOnItemClickListener(this);
		
		initReceiver();
		hideToolbar();
	}
	private void initData(){
		
		mNavigationDB = NavigationDB.getInstance(this);
		
		mList = new ArrayList<NavigationListItem>();
		mList = mNavigationDB.getNavigationLists();
		
		mTempList = new ArrayList<NavigationListItem>();
		
		for(int i = 0;i<mList.size();i++){
			if(mList.get(i).type.equals(NAVIGATION_INSTITUTION)){
				mTempList.add(mList.get(i)); 
			}
		}	
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent sendIntent = new Intent();
		sendIntent.setClass(InstitutionList.this,SchoolNavigation.class);
		sendIntent.putExtra(NAVIGATION_PARENT_ID,mTempList.get(position).content);	//根目录ID(代理商ID)
		sendIntent.putExtra(NAVIGATION_PARENT_TYPE,mTempList.get(position).type);   //传入类型用于区分是从外部进入
		sendIntent.putExtra(SEARCHSCHOOL,SEARCHSCHOOL);
		startActivity(sendIntent);
	}
	/**
	 * *************** 广播接收 ***************
	 */
	public class SchoolkeyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String stringAction = intent.getAction();
				if (stringAction.equals(SchoolNavigation.ACTION)) {
					InstitutionList.this.finish();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	private void initReceiver(){
		mReceiver = new SchoolkeyReceiver();
		 IntentFilter myIntentFilter = new IntentFilter();  
	     myIntentFilter.addAction(SchoolNavigation.ACTION);  
	        //注册广播        
	        registerReceiver(mReceiver, myIntentFilter); 
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
}
