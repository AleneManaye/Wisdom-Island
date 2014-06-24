package com.drcom.drpalm.View.LeaveApplication;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.drcom.drpalm.Activitys.LeaveApplication.AddApplicationActivity;
import com.drcom.drpalm.Activitys.LeaveApplication.SelectReceiverActivity;
import com.drcom.drpalm.Activitys.mOrganization.StateActivity;
import com.drcom.drpalm.Tool.jsonparser.ReceiverListParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.objs.ReceiverItem;
import com.drcom.drpalm.objs.ReceiverResultItem;

/**
 * Created by Administrator on 2014/5/9.
 */
public class SelectReceiverManager {

    private Context context;

    public SelectReceiverManager(Context context) {
        this.context = context;
    }
	/**
	 * 获取checkbox的名字和id
	 * @param handler
	 */
    public void getReceiverList(final Handler handler) {

        RequestOperationReloginCallback requestOperationReloginCallback = new RequestOperationReloginCallback() {
            @Override
            public void onSuccess(Object object) {
            	if(object!=null){
					ReceiverResultItem resultItem = (ReceiverResultItem) object;
	                Log.i("selectReceiver", "获取失败-------------------------");
	                if (!resultItem.isResult()) {
	                    Toast.makeText(context, "获取失败", Toast.LENGTH_LONG).show();
	                    Log.i("selectReceiver", "获取失败");
	                } else {
	                    Message message = new Message();
	                    message.obj = resultItem;
	                    message.arg1 = SelectReceiverActivity.SUCCESS;
	                    handler.sendMessage(message);
	                    Log.i("leaveApplicationManager", "获取成功");
	                }
            	}
            }
        };

        RequestManager.getReceiverList(new ReceiverListParser(), requestOperationReloginCallback);
    }
    
	/**
     * 保存checkbox被选中的信息
	 * @param mReceivers 
     */
	public void selected(ArrayList<ReceiverItem> mReceivers) {
		Intent receiverIntent = new Intent();

		StringBuffer ownername = new StringBuffer();
		StringBuffer ownerid = new StringBuffer();

		if (mReceivers.size() > 0) {
			for (int i = 0; i < mReceivers.size(); i++) {
				if (mReceivers.get(i).ischecked) {
					ownername.append(mReceivers.get(i).receiver_name);
					ownername.append(",");

					ownerid.append(mReceivers.get(i).receiver_id);
					ownerid.append(",");
				}

			}

		}

		String ownernames = ownername.toString();
		String ownerids = ownerid.toString();
		if (ownernames.lastIndexOf(",") > 0) {
			ownernames = ownernames.substring(0, ownernames.lastIndexOf(","));
			ownerids = ownerids.substring(0, ownerids.lastIndexOf(","));
		}

		Bundle bundle = new Bundle();
		bundle.putString(AddApplicationActivity.KEY_RECEIVER_LIST, ownernames);
		bundle.putString(AddApplicationActivity.KEY_RECEIVER_ID, ownerids);
		
		receiverIntent.putExtra(AddApplicationActivity.KEY_RECEIVER_BUNDLE, bundle);

		((Activity)context).setResult(StateActivity.RESULT_CODE, receiverIntent);
	}

	/**
	 * 记住上次checkbox被选中的状态
	 * @param mReceivers
	 * @return
	 */
	public boolean lastTimeIschecked(ArrayList<ReceiverItem> mReceivers){
		
		Intent intent = ((Activity)context).getIntent();
		Bundle bundle = intent.getExtras();
		String str = bundle.getString(AddApplicationActivity.KEY_RECEIVER_ID);
		
		if(str != ""){
			String[] strs = str.split(",");
			
			for(int j =0,i=0;j<mReceivers.size();j++){
				if(i<strs.length){
					if(mReceivers.get(j).receiver_id.equals(strs[i])){
						mReceivers.get(j).ischecked = true;
						i++;
					}
				}
			}
			if(strs.length==mReceivers.size()){
				return true;
			}
		}
		
		return false;
	}
	


}
