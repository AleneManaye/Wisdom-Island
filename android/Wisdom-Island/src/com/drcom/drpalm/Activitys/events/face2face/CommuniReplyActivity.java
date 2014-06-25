
package com.drcom.drpalm.Activitys.events.face2face;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.events.face2face.CommuniReplyActivityManagement;
import com.drcom.drpalm.objs.CommunicateItem;
import com.wisdom.island.R;

/**     
 * Date         : 2012-8-6
 * Author       : JiangBo
 * Copyright    : City Hotspot Co., Ltd.
 * 
 * 家园桥聊天内容界面
 */
public class CommuniReplyActivity extends ModuleActivity{
	public static final int SENDREPLY_BEGIN   = 0;
	public static final int SENDREPLY_SUCCESS = 1;
	public static final int SENDREPLY_FAILED  = 2;
	public static final int SENDREPLY_RELOGIN  = 3;
	public static final int GET_REPLY_START   = 4;
	public static final int GET_REPLY_SUCCESS = 5;
	public static final int GET_REPLY_FAILED  = 6;
	public static final int GET_REPLY_RELOGIN = 7;
	
	private static long TIMEINTERVAL = 300*1000;  //时间间隔(单位：毫秒)
	private CommuniReplyMessageAdapter replymessageAdapter;
	private List<CommunicateItem> evetnsReplyList = new ArrayList<CommunicateItem>();
	
	private CommuniReplyActivityManagement mCommuniReplyActivityManagement;


//	private LinearLayout layoutreply = null;
	private ListView chatHistoryLv;
	private Button sendBtn;
	private EditText textEditor;
	private TextView textviewHead;
	
	public static String REPLY_EVENT_ID = "ReplyEventId";
	public static String REPLY_ASWPUBID = "AswPubId";   //对方ID
	public static String REPLY_HEADSHOW = "HeadShow";   //标题显示(对方ID+名称)
	private int eventid = 0;
	private String aswpubid = "";	//对方ID 讨论组ID
	private Handler uiHandler = null;
	private ProgressDialog m_pSendDialog;
	private ProgressDialog m_pInitDialog;
	
	
	private boolean isRequestRelogin = true;	//登录SECCION超时要重登录?
	
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
		
		mCommuniReplyActivityManagement = new CommuniReplyActivityManagement(this);
		//头
//		layouthead = (LinearLayout) findViewById(R.id.reply_head_layout);
//		layouthead.setBackgroundDrawable(GlobalVariables.gResourceManagement.getDrawableByFileName(getString(R.string.listview_header)));
		
//		textviewHead = (TextView)findViewById(R.id.reply_head_textview);
//		textviewHead.setText("Xxx");
		
//		layoutreply = (LinearLayout) findViewById(R.id.chat_root);
//		mListResFileName.add(GlobalVariables.gAppContext.getString(R.string.reply_bg));
//		layoutreply.setBackgroundDrawable(GlobalVariables.gResourceManagement.getDrawableByFileName(getString(R.string.reply_bg)));
		
		
		
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(CommuniReplyActivity.REPLY_EVENT_ID))
		{
			eventid = extras.getInt(CommuniReplyActivity.REPLY_EVENT_ID, 0);
		}
		if(extras.containsKey(CommuniReplyActivity.REPLY_ASWPUBID))
		{
			aswpubid = extras.getString(CommuniReplyActivity.REPLY_ASWPUBID);
		}
		if(extras.containsKey(CommuniReplyActivity.REPLY_HEADSHOW))
		{
			textviewHead = (TextView)findViewById(R.id.reply_head_textview);
			String strShow = extras.getString(CommuniReplyActivity.REPLY_HEADSHOW);
			textviewHead.setText(strShow);
		}
		
		Log.i("zjj", "查看回复内容:" + eventid + "," + aswpubid);
		
		initProgressDialog();
		initHandler();
		hideToolbar();
		
		chatHistoryLv = (ListView) findViewById(R.id.reply_history_lv);
		getDataInDB();
		replymessageAdapter = new CommuniReplyMessageAdapter(this,evetnsReplyList,getmClassImageLoader());
		chatHistoryLv.setAdapter(replymessageAdapter);
		mCommuniReplyActivityManagement.GetContactMsgs(aswpubid, uiHandler);
		
		sendBtn = (Button) findViewById(R.id.send_button);
		textEditor = (EditText) findViewById(R.id.text_editor);
		sendBtn.setOnClickListener(l);
	}

	/**
	 * 加载本地数据
	 */
	private void getDataInDB(){
		List<CommunicateItem> list = new ArrayList<CommunicateItem>();
		list = mCommuniReplyActivityManagement.getCommunicateList(aswpubid);
		if(list.size()>0){
			evetnsReplyList.clear();
			for(CommunicateItem item : list){
				evetnsReplyList.add(item);
//				evetnsReplyList = mCommuniReplyActivityManagement.getCommunicateList(aswpubid);
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
					mCommuniReplyActivityManagement.sendContactMsg(aswpubid, sendStr, uiHandler);

				}
				textEditor.setText("");

			} 
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
					String content = (String)msg.obj;
					evetnsReplyList.add(mCommuniReplyActivityManagement.GetMyNewReplyItem(content));
					//evetnsReplyList.add(new EventsReplyItem(EventsReplyItem.MESSAGE_TO, content));
					replymessageAdapter.notifyDataSetChanged();
					//Toast.makeText(PushManageActivity.this, getString(R.string.pushsetsuccess), Toast.LENGTH_SHORT).show();
//					Toast.makeText(CommuniReplyActivity.this, getResources().getString(R.string.replysucceed), Toast.LENGTH_SHORT).show();
					return ;
				}
				else if(msg.arg1 == SENDREPLY_FAILED)
				{
					m_pSendDialog.cancel();
					
					String err = (String)msg.obj;
					if(err.equals(GlobalVariables.gAppContext.getResources().getString(R.string.InvalidSessionKey))){
						GlobalVariables.showInvalidSessionKeyMessage(CommuniReplyActivity.this);
					}else{
						Toast.makeText(CommuniReplyActivity.this, getResources().getString(R.string.replyfaild), Toast.LENGTH_SHORT).show();
					}
					
					return;
				}
				else if (msg.arg1 == SENDREPLY_RELOGIN){
					if(isRequestRelogin){
						mCommuniReplyActivityManagement.GetContactMsgs(aswpubid, uiHandler);	//自动登录成功后，再次请求数据
						isRequestRelogin = false;
					}
				}
				else if(msg.arg1 == GET_REPLY_START)
				{
					m_pInitDialog.show();
				}
				else if(msg.arg1 == GET_REPLY_SUCCESS)
				{
					getDataInDB();
					replymessageAdapter.notifyDataSetChanged();
					
					//把回复时间写入本地,和下次刷新列表所得的lastupdatatime对比,是否有新回复
					if(evetnsReplyList.size() > 0){
						mCommuniReplyActivityManagement.updataLastAwstime(aswpubid);
					}
					
					
					m_pInitDialog.cancel();
				}
				else if(msg.arg1 == GET_REPLY_FAILED)
				{
					m_pInitDialog.cancel();
				}
				else if(msg.arg1 == GET_REPLY_RELOGIN){
					if(isRequestRelogin){
						mCommuniReplyActivityManagement.GetContactMsgs(aswpubid, uiHandler);	//自动登录成功后，再次请求数据
						isRequestRelogin = false;
					}
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
