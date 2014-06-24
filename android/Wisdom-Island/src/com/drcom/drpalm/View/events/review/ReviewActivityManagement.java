package com.drcom.drpalm.View.events.review;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.events.review.ReviewActivity;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.Tool.jsonparser.SubmitResultParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.ReviewResult;
import com.drcom.drpalm.objs.EventDetailsItem.ReviewTemp;
import com.drcom.drpalm4tianzhujiao.R;

public class ReviewActivityManagement {
	public static int HANDLER_SUMMIT_SUCCEED = 1;
	public static int HANDLER_SUMMIT_FAIL = 0;
	
	private Context mContext;
	//变量
	private int mEvent_id = -1;
	private String mUsername = "";	
	private SettingManager setInstance ;	
	private EventsDB mEventsDB;
	private Cursor mEventCursor = null;
	private LoginManager instance ;
	private EventDetailsItem mEventdetailItem;
	private ArrayList<ReviewResult> mReviewResultList = new ArrayList<ReviewResult >(); 
	private LinearLayout mLayout;
	
	public ReviewActivityManagement(Context c,int id){
		mContext = c;
		mEvent_id = id;
		setInstance = SettingManager.getSettingManager(c);	
		mEventsDB = EventsDB.getInstance(c,GlobalVariables.gSchoolKey);
		mUsername = setInstance.getCurrentUserInfo().strUsrName; 
		instance = LoginManager.getInstance(GlobalVariables.gAppContext);
		
		//测试数据
//		EventDetailsItem.ReviewTemp rt1 = new ReviewTemp();
//		rt1.id = "1";
//		rt1.title = "吃饭指数";
//		rt1.type =  ReviewTemp.TYPE_COUNT;
//		rt1.max = 5;
//		rt1.defaultsum = 2;
//		rt1.required = true;
//		
//		EventDetailsItem.ReviewTemp rt2 = new ReviewTemp();
//		rt2.id = "2";
//		rt2.title = "其它评论";
//		rt2.type =  ReviewTemp.TYPE_TEXT;
//		rt2.required = false;
//		
//		mEventdetailItem = new EventDetailsItem();
//		mEventdetailItem.mReviewTempList.add(rt1);
//		mEventdetailItem.mReviewTempList.add(rt2);
		
		//从库中读取
		GetDataInDB(id);
	}
	
	/**
	 * 从库读取
	 */
	public EventDetailsItem GetDataInDB(int id){
		mEventCursor = mEventsDB.getOneEventCursor(id,mUsername);
		mEventCursor.requery();
		mEventCursor.moveToFirst();
		mEventdetailItem = mEventsDB.retrieveEventDetailItem(mEventCursor);
		mEventCursor.close();
		mEventCursor = null;
		
		return mEventdetailItem;
	}
	
	/**
	 * 生成界面
	 * @param layout
	 */
	public void InitUI(LinearLayout layout){
		mLayout = layout;
		
		for(int i=0;i<mEventdetailItem.mReviewTempList.size();i++){
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			LinearLayout newLayout=new LinearLayout(mContext);
			newLayout.setLayoutParams(params);
			newLayout.setOrientation(LinearLayout.VERTICAL);
			
			ViewGroup.LayoutParams textParams=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
			
			//
			TextView title=new TextView(mContext);
			title.setTextAppearance(mContext, R.style.ListItemTitle);
			title.setLayoutParams(textParams);
			if(mEventdetailItem.mReviewTempList.get(i).required){
				title.setText(mEventdetailItem.mReviewTempList.get(i).title+"(*):");
			}else{
				title.setText(mEventdetailItem.mReviewTempList.get(i).title+":");
			}
			newLayout.addView(title);
			
			//
			if(mEventdetailItem.mReviewTempList.get(i).type.equals(ReviewTemp.TYPE_COUNT) ){
				LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				RatingBar bar=(RatingBar)inflater.inflate(R.layout.myratingbar, null);
//				bar.setId(Integer.parseInt(mEventdetailItem.mReviewTempList.get(i).id));
				bar.setId(i);
				bar.setTag(ReviewTemp.TYPE_COUNT);
				bar.setLayoutParams(textParams);
				bar.setMax(mEventdetailItem.mReviewTempList.get(i).max);
				bar.setStepSize(1);
		        bar.setNumStars (mEventdetailItem.mReviewTempList.get(i).max);
		        bar.setRating(mEventdetailItem.mReviewTempList.get(i).defaultsum);
				newLayout.addView(bar);
			}else if(mEventdetailItem.mReviewTempList.get(i).type.equals(ReviewTemp.TYPE_TEXT)){
				EditText reviewText=new EditText(mContext);
//				reviewText.setId(Integer.parseInt(mEventdetailItem.mReviewTempList.get(i).id));
				reviewText.setId(i);
				reviewText.setTag(ReviewTemp.TYPE_TEXT);
				textParams.width=LayoutParams.MATCH_PARENT;
				reviewText.setLayoutParams(textParams);
				newLayout.addView(reviewText);
			}
			
			
			layout.addView(newLayout);
		}
	}
	
	/**
	 * 提交结果句柄
	 */
	private Handler mHandlerSummitResult;
	/**
	 * 提交结果
	 */
	public void Summit(Handler h){
		mHandlerSummitResult = h;
		
		for(int i=0;i<mEventdetailItem.mReviewTempList.size();i++){
			if(mEventdetailItem.mReviewTempList.get(i).type.equals(ReviewTemp.TYPE_COUNT)){
//				RatingBar bar = (RatingBar)mLayout.findViewById(Integer.parseInt(mEventdetailItem.mReviewTempList.get(i).id));
				RatingBar bar = (RatingBar)mLayout.findViewById(i);
				
				ReviewResult rr = new ReviewResult();
				rr.id = mEventdetailItem.mReviewTempList.get(i).id;
				rr.type = ReviewTemp.TYPE_COUNT;
				rr.value = String.valueOf(bar.getRating());
				
				mReviewResultList.add(rr);
			}else if(mEventdetailItem.mReviewTempList.get(i).type.equals(ReviewTemp.TYPE_TEXT)){
//				EditText edit = (EditText)mLayout.findViewById(Integer.parseInt(mEventdetailItem.mReviewTempList.get(i).id));
				EditText edit = (EditText)mLayout.findViewById(i);
				
				ReviewResult rr = new ReviewResult();
				rr.id = mEventdetailItem.mReviewTempList.get(i).id;
				rr.type = ReviewTemp.TYPE_TEXT;
				rr.value = edit.getText().toString();
				
				mReviewResultList.add(rr);
			}
		}
		
		RequestManager.SubmitClassreview(String.valueOf(mEventdetailItem.eventid) , mReviewResultList, new SubmitResultParser(), callback);
		
	}
	
	/**
	 * 提交结果
	 */
	private RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
		
		@Override
		public void onError(String str) {
			Log.i("zjj", "提交回评 返回失败");
			
			if(mHandlerSummitResult != null){
				Message msg = new Message();
				msg.arg1 = HANDLER_SUMMIT_FAIL;
				mHandlerSummitResult.sendMessage(msg);
			}
			
		}
		@Override
		public void onSuccess() {		
			Log.i("zjj", "提交回评  返回成功");
			
			if(mHandlerSummitResult != null){
				Message msg = new Message();
				msg.arg1 = HANDLER_SUMMIT_SUCCEED;
				mHandlerSummitResult.sendMessage(msg);
			}
//			HideLoadingDialog();
//			Toast.makeText(GlobalVariables.gAppContext, R.string.summitsucced, Toast.LENGTH_SHORT).show();
			
		}								
	};
	
}
