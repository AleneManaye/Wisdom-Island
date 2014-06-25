
package com.drcom.drpalm.Activitys.events.reply;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.request.EventsRequest;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.events.reply.EventsReplyActivityManagement;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.AwsContentItem;
import com.wisdom.island.R;

/**      
 * Date         : 2012-8-6
 * Author       : zjj
 * Copyright    : City Hotspot Co., Ltd.
 * 
 * 通告,聊天内容界面
 */
public class EventsReplyActivity extends ModuleActivity{
	private  int SENDREPLY_BEGIN   = 0;
	private  int SENDREPLY_SUCCESS = 1;
	private  int SENDREPLY_FAILED  = 2;
	private  int GET_REPLY_START   = 3;
	private  int GET_REPLY_SUCCESS = 4;
	private  int GET_REPLY_FAILED  = 5;
	
	private static long TIMEINTERVAL = 1*60*1000;  //时间间隔(单位：毫秒) 分钟*秒*毫秒
	
	private ReplyMessageAdapter replymessageAdapter;
	private List<AwsContentItem> evetnsReplyList = new ArrayList<AwsContentItem>();	//显示在界面的对话数据(分左右)
	private List<AwsContentItem>  listquerydata = new ArrayList<AwsContentItem>();	//数据源

//	private LinearLayout layoutreply = null;
	private ListView chatHistoryLv;
	private Button sendBtn;
	private EditText textEditor;
	private TextView textviewHead;
	private ProgressDialog m_pSendDialog;
	private ProgressDialog m_pInitDialog;
	
	public static String REPLY_EVENT_ID = "ReplyEventId";
	public static String REPLY_ASWPUBID = "AswPubId";   //讨论组ID
	public static String REPLY_HEADSHOW = "HeadShow";   //标题显示(对方ID+名称)
	public static String REPLY_ABLE = "REPLY_ABLE";   	//能否发送回复/聊天
	private int eventid = 0;
	private String aswpubid = "";	//讨论组ID
	private Handler uiHandler = null;
	private UserInfo mCurrentUserInfo = null;
	private SettingManager setInstance = null;
	
	
//	private EventsDB mEventsDB = null;
	
	private String mUsername = "";
	
	private EventsReplyActivityManagement mEventsReplyActivityManagement;
	
	//回复内容比较类(根据发布时间比较)
//	private class ComporetorReplyItem implements Comparator{
//
//		@Override
//		public int compare(Object lhs, Object rhs) {
//			// TODO Auto-generated method stub
//			AwsContentItem item1 = (AwsContentItem)lhs;
//			AwsContentItem item2 = (AwsContentItem)rhs;
//			long time1 = Long.parseLong(item1.replyTime);
//			long time2 = Long.parseLong(item2.replyTime);
//			return (int)(time1 - time2);
//		}
//		
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.events_reply2, mLayout_body);
		
		SettingManager setInstance = SettingManager.getSettingManager(EventsReplyActivity.this);
		mUsername = setInstance.mCurrentUserInfo.strUsrName;
//		mEventsDB = EventsDB.getInstance(EventsReplyActivity.this, GlobalVariables.gSchoolKey);
		mEventsReplyActivityManagement = new EventsReplyActivityManagement(EventsReplyActivity.this,mUsername);
		
		setInstance = SettingManager.getSettingManager(EventsReplyActivity.this);
		mCurrentUserInfo = setInstance.getCurrentUserInfo();
		
		//头
//		layoutreply = (LinearLayout) findViewById(R.id.chat_root);
		sendBtn = (Button) findViewById(R.id.send_button);
		textEditor = (EditText) findViewById(R.id.text_editor);
		
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(EventsReplyActivity.REPLY_EVENT_ID))
		{
			eventid = extras.getInt(EventsReplyActivity.REPLY_EVENT_ID, 0);
		}
		if(extras.containsKey(EventsReplyActivity.REPLY_ASWPUBID))
		{
			aswpubid = extras.getString(EventsReplyActivity.REPLY_ASWPUBID);
		}
		if(extras.containsKey(EventsReplyActivity.REPLY_HEADSHOW))
		{
			textviewHead = (TextView)findViewById(R.id.reply_head_textview);
			String strShow = extras.getString(EventsReplyActivity.REPLY_HEADSHOW);
			textviewHead.setText(strShow);
		}
		if(extras.containsKey(EventsReplyActivity.REPLY_ABLE))
		{
			if(!extras.getBoolean(REPLY_ABLE)){
				textEditor.setHint(R.string.cantreply);
				textEditor.setHintTextColor(getResources().getColor(R.color.dark_gray));
				textEditor.setFocusable(false);
				sendBtn.setClickable(false);
				sendBtn.setBackgroundResource(R.drawable.reply_cantsend_btn);
			}
		}
		
		Log.i("zjj", "查看回复内容:" + eventid + "," + aswpubid);
		
		initProgressDialog();
		initHandler();
		hideToolbar();
		
		chatHistoryLv = (ListView) findViewById(R.id.reply_history_lv);
		replymessageAdapter = new ReplyMessageAdapter(this,evetnsReplyList,getmClassImageLoader());
		chatHistoryLv.setAdapter(replymessageAdapter);
		initReplyInfo();
		
		sendBtn.setOnClickListener(l);

	}

	/**
	 * 加载本地数据
	 */
	private void getDataInDB(){
		
		listquerydata.clear();
		listquerydata.addAll(mEventsReplyActivityManagement.getAwsContentList(eventid,aswpubid));
		if(listquerydata.size()>0)
		{
			showMessage(listquerydata);
		}
	}
	
	// 为listView添加数据
	private void initReplyInfo() {
		sendHandlerMsg(GET_REPLY_START, "");
		
		getDataInDB();
		
		//收取聊天内容请求
		mEventsReplyActivityManagement.getData(uiHandler, String.valueOf(eventid), aswpubid);
	}
	
	//显示回复内容
	private void showMessage(List<AwsContentItem> list)
	{
		long lastReplyTime = 0;   //前一条回复时间
		evetnsReplyList.clear();
		for(AwsContentItem item : list)
		{
//			String strContent = "[";
//			try{
//				long time = Long.parseLong(item.replyTime);
//				time *= 1000;
//				Date date = new Date(time);
//				String strTime = DateFormatter.getStringYYYYMMDDHHmm(date);
//				strContent += strTime +"]"; 
//			}
//			catch (Exception e) {
//				// TODO: handle exception
//			}
//			strContent += "\r\n";
//			strContent += item.replybody;
			
			//比较时间间隔，以确定是否需要显示时间
			boolean bShowReplyTime = false;
//			long replytime = Long.parseLong(item.replyTime);
			long replytime = item.aws_time.getTime();
			
			long interval = replytime - lastReplyTime ;
			
			Log.i("zjj","lastReplyTime:" + lastReplyTime + ",replytime:" + replytime + ",interval:" + interval + ",item.pub_id:" + item.pub_id);
			
			if(replytime - lastReplyTime > TIMEINTERVAL)
			{
				bShowReplyTime = true;
				lastReplyTime = replytime;
			}
			
//			if(item.pub_id.contentEquals(aswpubid))	//如果是对方说的话
//			{
//				evetnsReplyList.add(new AwsContentItem(AwsContentItem.MESSAGE_FROM, item.aws_body,bShowReplyTime?item.aws_time:null));
//			}
//			else
//			{
//				evetnsReplyList.add(new AwsContentItem(AwsContentItem.MESSAGE_TO, item.aws_body,bShowReplyTime?item.aws_time:null));
//			}
			
			if(item.pub_id.contentEquals(mUsername))	//如果是自己说的话
			{
				AwsContentItem awsItem = new AwsContentItem(AwsContentItem.MESSAGE_TO, item.aws_body, item.pub_name, bShowReplyTime?item.aws_time:null);
				awsItem.headimgurl = mCurrentUserInfo.headurl;
				evetnsReplyList.add(awsItem);
			}
			else
			{
				AwsContentItem awsItem = new AwsContentItem(AwsContentItem.MESSAGE_FROM, item.aws_body, item.pub_name, bShowReplyTime?item.aws_time:null);
				awsItem.headimgurl = item.headimgurl;
				evetnsReplyList.add(awsItem);
			}
		}
	}

	/**
	 * 按键时间监听
	 */
	private View.OnClickListener l = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v.getId() == sendBtn.getId()) {
				String str = textEditor.getText().toString();
				String sendStr;
				if (str != null
						&& (sendStr = str.trim().replaceAll("\r", "").replaceAll("\t", "").replaceAll("\n", "")
								.replaceAll("\f", "")) != "") {
					sendMessage(sendStr);

				}
				textEditor.setText("");
				// 关闭软键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(sendBtn.getWindowToken(), 0);
			} 
		}
		
		

		/**
		 * 发送消息
		 * @param sendStr
		 */
		private void sendMessage(final String sendStr) {
			sendHandlerMsg(SENDREPLY_BEGIN,"");
			Log.i("zjj", "回复消息:eventid:" + eventid + ",aswpubid:" + aswpubid );
			mEventsReplyActivityManagement.SendData(uiHandler,eventid, aswpubid, sendStr);
		}

	};
	
	private void initProgressDialog(){
		if(null == m_pSendDialog)
		{
			m_pSendDialog = new ProgressDialog(this);
		}
		m_pSendDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		m_pSendDialog.setMessage(GlobalVariables.gAppResource.getString(R.string.Sending));
		m_pSendDialog.setIndeterminate(false);
		m_pSendDialog.setCancelable(true);
		
		if(null == m_pInitDialog )
			m_pInitDialog = new ProgressDialog(this);
		m_pInitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		m_pInitDialog.setMessage(GlobalVariables.gAppResource.getString(R.string.getreplyinfoing));
		m_pInitDialog.setIndeterminate(false);
		m_pInitDialog.setCancelable(true);
	}
	
	private void sendHandlerMsg(int nType,String strContent)
	{
		Message msg = Message.obtain();
		msg.arg1 = nType;
		msg.obj  = strContent;
		if(uiHandler!=null)
			uiHandler.sendMessage(msg);
	}
	
	private void initHandler()
	{
		uiHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.arg1 == SENDREPLY_BEGIN)
				{
					m_pSendDialog.show();
				}
				else if(msg.arg1 == SENDREPLY_SUCCESS)
				{
					
					m_pSendDialog.cancel();
//					String content = (String)msg.obj;
//					evetnsReplyList.add(new AwsContentItem(AwsContentItem.MESSAGE_TO, content,null));
//					//evetnsReplyList.add(new EventsReplyItem(EventsReplyItem.MESSAGE_TO, content));
//					replymessageAdapter.notifyDataSetChanged();
//					//Toast.makeText(PushManageActivity.this, getString(R.string.pushsetsuccess), Toast.LENGTH_SHORT).show();
//					Toast.makeText(EventsReplyActivity.this, getResources().getString(R.string.replysucceed), Toast.LENGTH_SHORT).show();
					
					//收取聊天内容请求
					mEventsReplyActivityManagement.getData(uiHandler, String.valueOf(eventid), aswpubid);
					return ;
				}
				else if(msg.arg1 == SENDREPLY_FAILED)
				{
					m_pSendDialog.cancel();
					String err = (String)msg.obj;
					if(err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))){
						GlobalVariables.showInvalidSessionKeyMessage(EventsReplyActivity.this);
					}else{
						Toast.makeText(EventsReplyActivity.this, getResources().getString(R.string.replyfaild), Toast.LENGTH_SHORT).show();
					}
					return;
				}
				else if(msg.arg1 == GET_REPLY_START)
				{
					m_pInitDialog.show();
				}
				else if(msg.arg1 == GET_REPLY_SUCCESS)
				{
					getDataInDB();
					replymessageAdapter.notifyDataSetChanged();
					//
					if(listquerydata.size()>0){
						mEventsReplyActivityManagement.UpdateReadtime(listquerydata.get(listquerydata.size()-1).aws_time.getTime()/1000 + "", 
								eventid, mUsername, aswpubid);//listquerydata.get(listquerydata.size()-1).pub_id);
					}
					m_pInitDialog.cancel();
				}
				else if(msg.arg1 == GET_REPLY_FAILED)
				{
					m_pInitDialog.cancel();
				}
			}
			
		};
	}

//	@Override
//	public boolean onKeyDownMethod() {
//		// TODO Auto-generated method stub
//		Intent intent = new Intent(ActivityActionDefine.POPACTIVITY_ACTION);
//		//intent.putParcelableArrayListExtra(EventsSendActivity.ATTACHMENTDIRLIST, (ArrayList<? extends Parcelable>)mListAttachment);
//		sendBroadcast(intent);
//		return true;
//	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		layoutreply.setBackgroundDrawable(null);
		sendBtn.setBackgroundDrawable(null);
		super.onDestroy();
	}
	
}
