package com.drcom.drpalm.Activitys.bindaccount;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.Navigation.SchoolNavigation;
import com.drcom.drpalm.Activitys.bindaccount.InstitutionList.SchoolkeyReceiver;
import com.drcom.drpalm.Tool.Encryption;
import com.drcom.drpalm.View.bindaccount.BindaccountListManagement;
import com.drcom.drpalm4tianzhujiao.R;

public class NewbindaccountActivity extends ModuleActivity implements OnClickListener{
	
	private LinearLayout mSearchSchool;
	private EditText mUserName;
	private EditText mUserPwd;
	private Button mBtn_Relation;
	
	private Handler mHandler;
	
	private SchoolkeyReceiver mReceiver;
	private Button mBtnSchoolName;
	private String mSchoolName="";
	private String mSchoolKey="";
	
	private BindaccountListManagement mbindaccountManager;
	public static final int RESULTCODE = 20;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.newbindaccount_view, mLayout_body);
		
		mbindaccountManager = new BindaccountListManagement(this);
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.arg1) {
				case BindaccountListActivity.ADDSUCCESS:
					setResult(RESULTCODE);
					NewbindaccountActivity.this.finish();
					break;
				case BindaccountListActivity.FAIL:
					ClientValidation(msg.obj.toString());
					break;
				default:
					break;
				}
				super.handleMessage(msg);

			}
		};
		
		initView();
		initReceiver();
		hideToolbar();
	}
	/**
	 * 初始化界面
	 */
	private void initView(){
		mSearchSchool = (LinearLayout) findViewById(R.id.bindaccount_chooseschool_layout);
		mSearchSchool.setOnClickListener(this);
		
		mBtnSchoolName = (Button) findViewById(R.id.bindaccount_chooseschool);
		
		mUserName = (EditText) findViewById(R.id.bindaccount_username);
		mUserPwd = (EditText) findViewById(R.id.bindaccount_userpwd);
		
		mBtn_Relation = (Button) findViewById(R.id.bindaccount_btn);
		mBtn_Relation.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.bindaccount_chooseschool_layout:
			intent.setClass(NewbindaccountActivity.this,InstitutionList.class);
			startActivity(intent);
			break;
		case R.id.bindaccount_btn:
			String pwd = Encryption.toMd5(mUserPwd.getText().toString());
			mbindaccountManager.addAccountRelation(mUserName.getText().toString(), pwd, mSchoolKey,GlobalVariables.Devicdid, mHandler);
			break;

		default:
			break;
		}
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
					Bundle bundle = intent.getExtras(); 
					mSchoolName = bundle.getString(SchoolNavigation.SCHOOLNAME);
					mSchoolKey= bundle.getString(SchoolNavigation.SCHOOLKEY);
					mBtnSchoolName.setText(mSchoolName);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	/**
	 * 客户端验证（实际上也是发到服务返回结果才验证的）
	 */
	private void ClientValidation(String obj){
		String ID_and_pwd_nonull = this.getResources().getString(R.string.ID_and_pwd_nonull);
		String chooseSchool = this.getResources().getString(R.string.Please_ChooseSchool);
		if(mBtnSchoolName.getText().equals("")){
			GlobalVariables.toastShow(chooseSchool);
			return;
		}
		if(mUserName.getText().toString().equals("")){
			GlobalVariables.toastShow(ID_and_pwd_nonull);
			return;
		}
		if(mUserPwd.getText().toString().equals("")){
			GlobalVariables.toastShow(ID_and_pwd_nonull);
			return;
		}
		else{
			GlobalVariables.toastShow(obj);
			return;
		}
	}
	
	/**
	 * initialize receiver
	 */
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
