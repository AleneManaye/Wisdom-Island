package com.drcom.drpalm.View.bindaccount;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.bindaccount.BindaccountListActivity;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.DB.DatabaseHelper;
import com.drcom.drpalm.DB.NavigationDB;
import com.drcom.drpalm.Tool.AppPassDataHelper;
import com.drcom.drpalm.Tool.jsonparser.BindaccountListParser;
import com.drcom.drpalm.Tool.jsonparser.SubmitResultParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.objs.BindaccountItem;
import com.drcom.drpalm.objs.BindaccountResultItem;
import com.drcom.drpalm.objs.SubmitResultItem;

public class BindaccountListManagement {
	
	private Context mContext ;
	private NavigationDB mNavigationDb;
	private boolean isRequestRelogin = true; // 登录SECCION超时要重登录?
	public BindaccountListManagement(Context context) {
		super();
		this.mContext = context;
		mNavigationDb = NavigationDB.getInstance(context);
	}
	
	/**
	 * 从服务器获取账号信息（头像url和未读数）
	 * @param handler
	 */
	public void getaccountInfo(final String domain ,final String tokenid,final List<BindaccountItem> accounts,final Handler handler) {
		
		RequestOperationReloginCallback requestOperationReloginCallback = new RequestOperationReloginCallback() {
			@Override
			public void onSuccess(Object object) { // 请求数据成功
				if (object != null) {
					BindaccountResultItem resultItem = (BindaccountResultItem) object;
					if (resultItem.isResult()) {
						//把获取到的信息更新数据库
						for(int i = 0;i<resultItem.mBindaccountItem.size();i++){
							BindaccountItem account  = new BindaccountItem();
							account.pub_account = resultItem.mBindaccountItem.get(i).pub_account;
							account.pub_schoolId = resultItem.mBindaccountItem.get(i).pub_schoolId;
							account.pub_image_number = resultItem.mBindaccountItem.get(i).pub_image_number;
							account.pub_imgaeurl = resultItem.mBindaccountItem.get(i).pub_imgaeurl;
							account.headimglastupdate = resultItem.mBindaccountItem.get(i).headimglastupdate;
							
							mNavigationDb.updateAccountDetail(account);
						}
						Message message = handler.obtainMessage();
						message.arg1 = BindaccountListActivity.SUCCESSINFO;//获取列表成5
						handler.sendMessage(message);
						Log.i("hzy", "获取成功");
					}
				}
			}

			@Override
			public void onCallbackError(String str) {
				Message message = handler.obtainMessage();
				message.arg1 = BindaccountListActivity.FAIL;
				message.obj = str;
				handler.sendMessage(message);
				Log.i("hzy", "获取账号关联列表数据失败" + str);
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				Log.i("hzy", "获取账号关联列表自动重登录失败");
			}

			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				Log.i("hzy", "获取账号关联列表:自动重登录成功");
				if (isRequestRelogin) {
					getaccountInfo(domain,tokenid,accounts,handler); // 自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}
		};
		System.gc();
		RequestManager.getBindaccount(domain,tokenid, accounts, new BindaccountListParser(),
				requestOperationReloginCallback);
	}
	
	
	/**
	 * 从服务器获取账号关联列表
	 * @param handler
	 */
	public void getaccountsList(final String appid,final String tokenid,final Handler handler) {
		
		RequestOperationReloginCallback requestOperationReloginCallback = new RequestOperationReloginCallback() {
			@Override
			public void onSuccess(Object object) { // 请求数据成功
				if (object != null) {
					BindaccountResultItem resultItem = (BindaccountResultItem) object;
					if (resultItem.isResult())
						//③清除表数据
						mNavigationDb.deleteBindaccountTable();
						
						//④获取到的数据保存到数据库中
						for(int i = 0;i<resultItem.mBindaccountItem.size();i++){
							BindaccountItem account  = new BindaccountItem();
							account.pub_account = resultItem.mBindaccountItem.get(i).pub_account;
							account.pub_name = resultItem.mBindaccountItem.get(i).pub_name;
							account.pub_password = resultItem.mBindaccountItem.get(i).pub_password;
							account.pub_schoolId = resultItem.mBindaccountItem.get(i).pub_schoolId;
							account.pub_schoolName_hans = resultItem.mBindaccountItem.get(i).pub_schoolName_hans;
							account.pub_schoolName_hant = resultItem.mBindaccountItem.get(i).pub_schoolName_hant;
							account.pub_schoolName_en = resultItem.mBindaccountItem.get(i).pub_schoolName_en;
							account.schoolkey = resultItem.mBindaccountItem.get(i).schoolkey;
							account.tokenid = GlobalVariables.Devicdid;
							mNavigationDb.addAccount(account);//先别入库(未做任何处理)
						
						Message message = handler.obtainMessage();
						message.arg1 = BindaccountListActivity.SUCCESS;//获取列表成1
						message.obj = resultItem;
						handler.sendMessage(message);
						Log.i("hzy", "获取成功");
					}
				}
			}

			@Override
			public void onCallbackError(String str) {
				Message message = handler.obtainMessage();
				message.arg1 = BindaccountListActivity.FAIL;
				message.obj = str;
				handler.sendMessage(message);
				Log.i("hzy", "获取账号关联列表数据失败" + str);
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				Log.i("hzy", "获取账号关联列表自动重登录失败");
			}

			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				Log.i("hzy", "获取账号关联列表:自动重登录成功");
				if (isRequestRelogin) {
					getaccountsList(appid,tokenid,handler); // 自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}

		};
		System.gc();
		RequestManager.getBindaccountList(appid,tokenid,new BindaccountListParser(),
				requestOperationReloginCallback);
	}
	
	

	/**
	 * 添加账号关联
	 */
	public void addAccountRelation(final String account,final String passwordMd5,final String schoolkey,final String tokenid,final  Handler handler){
		
		RequestOperationReloginCallback requestOperationReloginCallback = new RequestOperationReloginCallback() {
			@Override
			public void onSuccess(Object object) { // 请求数据成功
				if (object != null) {
					SubmitResultItem resultItem = (SubmitResultItem) object;
					if (resultItem.result) {
						Message message = handler.obtainMessage();
						message.arg1 = BindaccountListActivity.ADDSUCCESS;//arg1==2 关联成功。
						handler.sendMessage(message);
						Log.i("hzy", "获取成功");
					}
				}
			}

			@Override
			public void onCallbackError(String str) {
				Message message = handler.obtainMessage();
				message.arg1 = BindaccountListActivity.FAIL;
				message.obj = str;
				handler.sendMessage(message);
				Log.i("hzy", "账号关联失败" + str);
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				Log.i("hzy", "账号关联:自动重关联失败");
			}

			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				Log.i("hzy", "账号关联:自动重关联成功");
				if (isRequestRelogin) {
					addAccountRelation(account, passwordMd5, schoolkey, tokenid, handler); // 自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}

		};
		System.gc();
		RequestManager.addAccountRelation(account, passwordMd5, schoolkey, tokenid, new SubmitResultParser(), requestOperationReloginCallback);
	}
	/**
	 * 删除账号关联
	 */
	public void deleteAccountRelation(final List<BindaccountItem> accountlist,final  Handler handler){
		
		RequestOperationReloginCallback requestOperationReloginCallback = new RequestOperationReloginCallback() {
			@Override
			public void onSuccess(Object object) { // 请求数据成功
				if (object != null) {
					SubmitResultItem resultItem = (SubmitResultItem) object;
					if (resultItem.result) {
						Message message = handler.obtainMessage();
						message.arg1 = BindaccountListActivity.DElSUCCESS;//arg1==3 删除关联成功。
						handler.sendMessage(message);
						Log.i("hzy", "删除成功");
					} 
				}
			}

			@Override
			public void onCallbackError(String str) {
				Message message = handler.obtainMessage();
				message.arg1 = BindaccountListActivity.FAIL;
				message.obj = str;
				handler.sendMessage(message);
				Log.i("hzy", "删除账号关联失败" + str);
			}

			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				Log.i("hzy", "删除账号关联:自动重删除关联失败");
			}

			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				Log.i("hzy", "删除账号关联:自动重删除关联成功");
				if (isRequestRelogin) {
					deleteAccountRelation(accountlist, handler); // 自动登录成功后，再次请求数据
					isRequestRelogin = false;
				}
			}

		};
		System.gc();
		RequestManager.deleteAccountRelation(accountlist, new SubmitResultParser(), requestOperationReloginCallback);
	}
	
	/**
	 * 列表项点击事件
	 * @param item
	 */
	public void onListviewItemClick(BindaccountItem item){
		//保存为默认的SCHOOL
//		SharedPreferences  preferences = mContext.getSharedPreferences("default_school", Context.MODE_PRIVATE);
//		SharedPreferences.Editor editor = preferences.edit();
//		editor.putString("school_key", item.schoolkey);
//		editor.commit();
		SharedPreferences  preferences = mContext.getSharedPreferences("default_school", Context.MODE_PRIVATE);
		if(preferences.contains("school_key")){
			if(!preferences.getString("school_key", "").equals(item.schoolkey)){
				DatabaseHelper.setChanged(true);
			}
		}
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("school_key", item.schoolkey);
		editor.commit();
		
		//保存帐号到缓存中
		GlobalVariables.gTempUsermane = item.pub_account;
		GlobalVariables.gTempPW = item.pub_password;
		
		//进入主程序
		Intent sendIntent = new Intent();
		sendIntent.setClass(mContext,MainActivity.class);
		sendIntent.putExtra(AppPassDataHelper.KEY_START_SCHOOLKEY,item.schoolkey);
		sendIntent.putExtra(MainActivity.KEY_BINDCOUNTSTART, true);
		mContext.startActivity(sendIntent);
	}
	
}
