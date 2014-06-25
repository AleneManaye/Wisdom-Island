package com.drcom.drpalm.Activitys.LeaveApplication;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.ImgsAttc.ImageAttcGalleryActivity;
import com.drcom.drpalm.Activitys.events.EventsDetailActivity;
import com.drcom.drpalm.DB.LeaveApplicationDB;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.View.LeaveApplication.LeaveApplicationMainManager;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.LeaveApplicationMainItem;
import com.drcom.drpalm.objs.MessageObject;
import com.wisdom.island.R;

public class ApplicationInfoActivity extends ModuleActivity {
	private TextView type;// 请假类型
	private TextView applicant;// 申请人
	private TextView applicationTime;// 发布时间
	private TextView applicationStart;//告假开始时间
	private TextView applicationEnd;//告假结束时间
	private TextView reason;// 请假理由
	private TextView receiver;// 接收人
	private TextView attachmentNumber;// 附件数量
	private LinearLayout attachment;// 附件item
	public static String KEY_LEAVE_ID = "KEY_LEAVE_ID";
	public static String KEY_IMGSURL = "KEY_IMGSURL";
	private int leaveId;
	private LeaveApplicationMainManager leaveManager;
	private LeaveApplicationDB leaveDB;
	private Cursor leaveCursor;
	private String mUsername = "";
	private SettingManager setInstance;
	private LeaveApplicationMainItem item;

	private String allfield = "1";// 获取全部数据
	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	public static final int MOREFINISH = 2; // 更多请求返回成功
	String[] urls;
	String[] names;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.leave_application_info, mLayout_body);
		if (getIntent().getExtras().containsKey(KEY_LEAVE_ID)) {
			leaveId = getIntent().getExtras().getInt(KEY_LEAVE_ID);
		}
		initUI();
		leaveManager = new LeaveApplicationMainManager(this);
		refleshUI();
		if (item.content == null||item.content.equals("")||item.isread==1) {
			GetLeaveDetail(leaveId + "", allfield);
			refleshUI();
		}
	}

	private void GetLeaveDetail(String leaveid, String allfield) {
		showProgressBar();
		leaveManager.GetDetailData(leaveid, allfield, mHandler);

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == UPDATEFINISH) {
				MessageObject obj = (MessageObject) msg.obj;
				if (obj.isSuccess) {
					refleshUI();
					hideProgressBar();
				}
			} else if (msg.arg1 == MOREFINISH) {
				refleshUI();
				hideProgressBar();
			} else if (msg.arg1 == UPDATEFAILED) {
				String err = (String) msg.obj;
				if (err.equals(GlobalVariables.gAppContext.getResources()
						.getString(R.string.InvalidSessionKey))) {
					GlobalVariables
							.showInvalidSessionKeyMessage(ApplicationInfoActivity.this);
					hideProgressBar();
				} else {
					GlobalVariables.toastShow(err);
					hideProgressBar();
				}
			}

		}
	};

	private void initUI() {
		hideToolbar();
		type = (TextView) findViewById(R.id.type);
		applicant = (TextView) findViewById(R.id.applicant);
		applicationTime = (TextView) findViewById(R.id.application_time);
		reason = (TextView) findViewById(R.id.application_reason);
		receiver = (TextView) findViewById(R.id.receiver);
		attachmentNumber = (TextView) findViewById(R.id.attachmentNumber);
		attachment = (LinearLayout) findViewById(R.id.attachment);
		applicationStart = (TextView)findViewById(R.id.application_start);
		applicationEnd = (TextView)findViewById(R.id.application_end);
	}

	private void refleshUI() {
		leaveDB = LeaveApplicationDB.getInstance(this,
				GlobalVariables.gSchoolKey);
		setInstance = SettingManager.getSettingManager(this);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		leaveCursor = leaveDB.getLeaveIdCursor(leaveId + "");
		leaveCursor.moveToFirst();
		item = leaveDB.retrieveLeaveApplicationMainItem(leaveCursor);
		if(item.hasatt==1){
			initAttr();
		}

		// 告假类型
		if (item.type.equals("sick")) {
			type.setText(getResources().getString(R.string.leave_type_sick));
		} else {
			type.setText(getResources().getString(R.string.leave_type_casual));
		}
		// 申请人
		applicant.setText(item.pubname);
		// 发布时间
		String time = "";
		time = time
				+ (DateFormatter.getStringYYYYMMDD(item.post)+" "+DateFormatter.getStringHHmm(item.post));
		applicationTime.setText(time);
		//告假开始时间
		String start = "";
		start = start
				+ ( DateFormatter.getStringYYYYMMDD(item.start)+" "+DateFormatter.getStringHHmm(item.start));
		applicationStart.setText(start);
		
		//告假结束时间
		String end = "";
		end = end
				+ ( DateFormatter.getStringYYYYMMDD(item.end)+" "+DateFormatter.getStringHHmm(item.end));
		applicationEnd.setText(end);
		
		// 请假理由
		reason.setText(item.content);
		// 接收人
		receiver.setText(item.owner);
		// 附件数量
		attachmentNumber.setText(item.attr.size() + "");
		setTitleText(item.title);

	}

	private void initAttr() {
		urls = new String[item.attr.size()];
		names = new String[item.attr.size()];
		for (int i = 0; i < urls.length; i++) {
			urls[i] = item.attr.get(i).URL;
			names[i] = item.attr.get(i).attname;
		}
		attachment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 打开附件页面
				Intent i = new Intent(ApplicationInfoActivity.this,
						ImageAttcGalleryActivity.class);
				i.putExtra(ImageAttcGalleryActivity.KEY_IMGSURL, urls);
				i.putExtra(ImageAttcGalleryActivity.KEY_IMGSNAME, names);
				ApplicationInfoActivity.this.startActivity(i);
			}
		});

	}

}
