package com.drcom.drpalm.View.LeaveApplication;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.drcom.drpalm.Activitys.LeaveApplication.AddApplicationActivity;
import com.drcom.drpalm.Activitys.mOrganization.StateActivity;
import com.drcom.drpalm.Tool.jsonparser.SubmitLeaveParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.LeaveApplicationMainItem;
import com.drcom.drpalm.objs.ResultItem;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2014/5/6.
 */
public class AddLeaveApplicationManager {

    private Context context;

    private Handler handler;

    public String TAG = "addLeaveApplicationManager-->";

    public AddLeaveApplicationManager(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    /**
     * 判断开始日期与结束日期的关系
     *
     * @param type
     * @param selectCalendar
     * @param addApplicationActivity
     * @param handler
     * @param strMsg
     */
    public void selectDate(int type, Calendar selectCalendar, AddApplicationActivity addApplicationActivity, Handler handler, String strMsg) {
        //假如设置的是结束日期,则检查是否比开始日期要早,假如是比开始日期要早,则弹出提示.
        if (type == AddApplicationActivity.END_TIME) {
            if (addApplicationActivity.getStartCalendar().getTimeInMillis() >= selectCalendar.getTimeInMillis()) {
                Toast.makeText(addApplicationActivity, strMsg, Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            addApplicationActivity.setStartCalendar(selectCalendar);
        }

        Message msg = new Message();
        msg.arg1 = type;
        msg.obj = selectCalendar;
        handler.sendMessage(msg);
    }

    /**
     * 根据calendar获取对应字符串
     *
     * @param calendar
     * @return
     */
    public String getTimeStrByCalendar(Calendar calendar) {

        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append(calendar.get(Calendar.YEAR));
        stringBuffer.append("-");
        if (calendar.get(Calendar.MONTH) < 10) {
            stringBuffer.append("0");
        }
        stringBuffer.append(calendar.get(Calendar.MONTH) + 1);
        stringBuffer.append("-");
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            stringBuffer.append("0");
        }
        stringBuffer.append(calendar.get(Calendar.DAY_OF_MONTH));
        stringBuffer.append(" ");

        switch (calendar.get(Calendar.AM_PM))
        {
            case Calendar.AM:

                if(calendar.get(Calendar.HOUR) < 10){
                    stringBuffer.append("0");
                }
                stringBuffer.append(calendar.get(Calendar.HOUR));

                break;
            case Calendar.PM:
                stringBuffer.append(calendar.get(Calendar.HOUR) + 12);

                break;
        }

        stringBuffer.append(":");
        if (calendar.get(Calendar.MINUTE) < 10) {
            stringBuffer.append("0");
        }
        stringBuffer.append(calendar.get(Calendar.MINUTE));

        return stringBuffer.toString();
    }

    /**
     * 根据年,月,日,小时,分钟获取Calendar对象
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param min
     * @return
     */
    public Calendar createCalendar(int year, int month, int day, int hour, int min) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(year,month,day,hour,min);

        return calendar;
    }

    /**
     * 提交告假
     *
     * @param leaveApplicationMainItem
     */
    public void submitLeave(LeaveApplicationMainItem leaveApplicationMainItem) {

        Calendar calendar = Calendar.getInstance();

        String type = leaveApplicationMainItem.getType();
        String ownerId = leaveApplicationMainItem.getOwnerid();
        String owner = leaveApplicationMainItem.getOwner();
        String title = leaveApplicationMainItem.getTitle();
        String content = leaveApplicationMainItem.getContent();
        List<EventDraftItem.Attachment> attachments = leaveApplicationMainItem.getAttachments();
        Date startDate = leaveApplicationMainItem.getStart();
        calendar.setTime(startDate);
        String startMill = String.valueOf(calendar.getTimeInMillis() / 1000);
        Date endDate = leaveApplicationMainItem.getEnd();
        calendar.setTime(endDate);
        String endMill = String.valueOf(calendar.getTimeInMillis() / 1000);

        RequestManager.submitLeaveInfo(type,
                ownerId, owner, startMill, endMill,
                title, content, new SubmitLeaveParser(),
                (java.util.ArrayList<EventDraftItem.Attachment>) attachments,
                requestOperationReloginCallback);
    }

    /**
     * 回调接口:
     * 处理回调成功或失败的情况
     */
    RequestOperationReloginCallback requestOperationReloginCallback = new RequestOperationReloginCallback() {

        @Override
        public void onCallbackError(String err) {
            super.onCallbackError(err);
        }

        @Override
        public void onSuccess(Object object) {
            ResultItem resultItem = (ResultItem) object;
            if (!resultItem.isResult()) {
                handler.sendEmptyMessage(0);
            } else {
                handler.sendEmptyMessage(1);
            }
        }
    };

}
