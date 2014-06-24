package com.drcom.drpalm.Activitys;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;

import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm4tianzhujiao.R;

/**
 * 底部界面
 * 
 * @author zhaojunjie
 * 
 */
public class UndergroundActivity extends Activity {
	private GroupReceiverExit mGroupReceiverExit;	// Receiver

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//一定要放在第一行
		super.onCreate(savedInstanceState);
		setContentView(R.layout.underground_view);
		
		initReceiver();
		
		openDefault();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mGroupReceiverExit);
		
		super.onDestroy();
	}

	/**
	 * 打开Loading窗体
	 */
	private void openDefault(){
		Intent sendIntent = new Intent();
		sendIntent.setClass(UndergroundActivity.this,DefaultActivity.class);
		startActivity(sendIntent);
	}
	
	/**
     * 收退出程序广播
     * initialize receiver
     */
    private void initReceiver(){
    	mGroupReceiverExit = new GroupReceiverExit();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActivityActionDefine.EXITAPP_ACTION);
        filter.addAction(ActivityActionDefine.CLOSE_UNDERGROUND_ACTIVITY);
        registerReceiver(mGroupReceiverExit,filter);
    }
	
	/**
     * ***************
     * 广播接收
     * ***************
     */
    private class GroupReceiverExit extends BroadcastReceiver{
    	@Override
    	public void onReceive(Context context, Intent intent){
    		try{
    			String stringAction = intent.getAction();
    			if(stringAction.equals(ActivityActionDefine.EXITAPP_ACTION)
    					|| stringAction.equals(ActivityActionDefine.CLOSE_UNDERGROUND_ACTIVITY)){
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
