package com.drcom.drpalm.View.events.face2face;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.face2face.CommuniReplyActivity;
import com.drcom.drpalm.Activitys.setting.AccountManageActivity;
import com.drcom.drpalm.DB.CommunicationDB;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.events.EventBaseInfoTools;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.CommunicateItem;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CommuniReplyActivityManagement {
	
	private Context mContext;
	private String mCurrentUserName;
	private CommunicationDB mCommunicationDB = null;
	private static long TIMEINTERVAL = 300*1000;  //时间间隔(单位：毫秒)
	private List<CommunicateItem>  listquerydata = new ArrayList<CommunicateItem>();	//聊天内容数据
	private UserInfo mCurrentUserInfo = null;
	private SettingManager setInstance = null;
	
	public CommuniReplyActivityManagement(Context context){
		this.mContext = context;
		EventBaseInfoTools baseInfo = new EventBaseInfoTools();
		mCurrentUserName = baseInfo.getCurrentUserName(mContext);
		
		mCommunicationDB = CommunicationDB.getInstance(mContext,GlobalVariables.gSchoolKey);
		
		setInstance = SettingManager.getSettingManager(context);
		mCurrentUserInfo = setInstance.getCurrentUserInfo();
	}
	
	/**
	 * 发送消息
	 * @param aswpubid 联系人ID
	 * @param sendStr  消息内容
	 * @param handler  消息分发handler
	 */
	public void sendContactMsg(String aswpubid, final String sendStr, final Handler handler){
		sendHandlerMsg(CommuniReplyActivity.SENDREPLY_BEGIN,"",handler);
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
			@Override
			public void onSuccess() {
//				getDataInDB();
				sendHandlerMsg(CommuniReplyActivity.SENDREPLY_SUCCESS, sendStr,handler);
			}
			
			@Override
			public void onCallbackError(String err) {
				// TODO Auto-generated method stub
				sendHandlerMsg(CommuniReplyActivity.SENDREPLY_FAILED,err,handler);
			}
			
			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				super.onReloginError();
				Log.i("zjj", "反馈回复:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				Log.i("zjj", "反馈回复:自动重登录成功");
				sendHandlerMsg(CommuniReplyActivity.SENDREPLY_RELOGIN,"",handler);
			}
		};
		mRequestOperation.sendGetNeededInfo("SendContactMsg", new Object[]{aswpubid, sendStr, callback},callback.getClass().getName());
	}
	
	/**
	 * 获取消息列表
	 * @param aswpubid 联系人ID
	 * @param handler
	 */
	public void GetContactMsgs(String aswpubid, final Handler handler){

		sendHandlerMsg(CommuniReplyActivity.GET_REPLY_START, "",handler);
		
		//请求
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOperationReloginCallback callback = new RequestOperationReloginCallback() {
			
			@Override
			public void onSuccess() {
				sendHandlerMsg(CommuniReplyActivity.GET_REPLY_SUCCESS, "",handler);
			}
			
			@Override
			public void onCallbackError(String err) {
				// TODO Auto-generated method stub
				sendHandlerMsg(CommuniReplyActivity.GET_REPLY_FAILED, err,handler);
			}
			
			@Override
			public void onReloginError() {
				// TODO Auto-generated method stub
				super.onReloginError();
				Log.i("zjj", "回复内容列表:自动重登录失败");
			}
			
			@Override
			public void onReloginSuccess() {
				// TODO Auto-generated method stub
				super.onReloginSuccess();
				Log.i("zjj", "回复内容列表:自动重登录成功");
				sendHandlerMsg(CommuniReplyActivity.GET_REPLY_RELOGIN, "",handler);
			}
		};
		mRequestOperation.sendGetNeededInfo("GetContactMsgs", new Object[]{aswpubid, "0",callback},callback.getClass().getName());
	}
	
	/**
	 * 获取消息队列
	 * @param aswpubid 联系人ID
	 * @return
	 */
	public List<CommunicateItem> getCommunicateList(String aswpubid){
		long lastReplyTime = 0;   //前一条回复时间
		List<CommunicateItem> evetnsReplyList = new ArrayList<CommunicateItem>();
		
		listquerydata = getAwsContentList(aswpubid);
		if(listquerydata.size()>0){
			for(CommunicateItem item : listquerydata){		
				//比较时间间隔，以确定是否需要显示时间
				boolean bShowReplyTime = false;
				long replytime = item.lastupdate.getTime();
				
				if(replytime - lastReplyTime > TIMEINTERVAL)
				{
					bShowReplyTime = true;
					lastReplyTime = replytime;
				}
				
				if(item.sendid.contentEquals(aswpubid))	//如果是对方说的话
				{
					CommunicateItem citem = new CommunicateItem(CommunicateItem.MESSAGE_FROM, item.body,bShowReplyTime?item.lastupdate:null);
					citem.headimgurl = item.headimgurl;
					evetnsReplyList.add(citem);
				}
				else
				{
					CommunicateItem citem = new CommunicateItem(CommunicateItem.MESSAGE_TO, item.body,bShowReplyTime?item.lastupdate:null);
					citem.headimgurl = mCurrentUserInfo.headurl;
					evetnsReplyList.add(citem);
				}
				
			}
		}
		return evetnsReplyList;
	} 
	
	/**
	 * 生成我回复成功的内容
	 * @param content
	 * @return
	 */
	public CommunicateItem GetMyNewReplyItem(String content){
		CommunicateItem citem = new CommunicateItem(CommunicateItem.MESSAGE_TO, content,null);
		citem.lastupdate = new Date(System.currentTimeMillis());
		citem.headimgurl = mCurrentUserInfo.headurl;
		return citem;
	}
	
	private void sendHandlerMsg(int nType,String strContent, Handler handler)
	{
		Message msg = Message.obtain();
		msg.arg1 = nType;
		msg.obj  = strContent;
		if(handler!=null)
			handler.sendMessage(msg);
	}
	
	/**
	 * 获取消息列表
	 * @param aswpubid 联系人ID
	 * @return
	 */
	public List<CommunicateItem> getAwsContentList(String aswpubid){
		 return mCommunicationDB.getAwsContentList(mCurrentUserName,aswpubid);
	}
	
	/**
	 * 
	 * @param lastupdate   	最后的对话内容时间(毫秒)
	 * @param replyerid		回复人ID
	 */
	public void updataLastAwstime(String replyerid){
		mCommunicationDB.updataAsworgLastawstimeread(listquerydata.get(listquerydata.size() - 1).lastupdate.getTime() + "",
				mCurrentUserName,
				replyerid);
	}
}
