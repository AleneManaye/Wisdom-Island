package com.drcom.drpalm.View.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.drcom.drpalm.GlobalVariables;
import com.wisdom.island.R;

public class ErrorNotificatin {
	public static final int NOTIFICATION_ID = 123456;
	private Context mContext = null;
	private NotificationManager mNotification;
	public ErrorNotificatin(Context context){
		this.mContext = context;
		mNotification = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
	}
	private void showNotification(int icon,String tickertext,String title,String content,
    		boolean isVibrate, boolean isSound, boolean isAutoCancel, int notificationId){
    	Notification notification= new Notification(icon,tickertext,System.currentTimeMillis());
    	//notification.defaults = Notification.DEFAULT_ALL;
    	notification.defaults |= Notification.DEFAULT_LIGHTS;
    	if(isVibrate){
    		notification.defaults |= Notification.DEFAULT_VIBRATE;
    		long[] vibrate = {0,100,200,300};
    		notification.vibrate = vibrate;
    	}
    	if(isSound){
    		notification.defaults |= Notification.DEFAULT_SOUND;
    	}

    	Intent intent = new Intent(mContext, mContext.getClass());
    	PendingIntent pt = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

    	notification.setLatestEventInfo(mContext,title,content,pt);

    	mNotification.notify(notificationId,notification);
    	mNotification.cancel(notificationId);
    }
	
	public void showErrorNotification(String error){
		showNotification(R.drawable.app_icon, error, GlobalVariables.gAppResource.getString(R.string.last_update),
				error, true, true, true, NOTIFICATION_ID);
	}
}
