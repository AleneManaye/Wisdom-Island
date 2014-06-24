package com.drcom.drpalm.Activitys.events.review;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.events.NewEventActivity;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.events.review.ReviewActivityManagement;
import com.drcom.drpalm4tianzhujiao.R;

/**
 * 回评
 * @author zhaojunjie
 *
 */
public class ReviewActivity  extends ModuleActivity{
	public static String REPLY_EVENT_ID = "ReplyEventId";
	
	//控件
	private LinearLayout mLayout;
	private Button buttonSave;
	
	//变量
	private int eventid = 0;
	private ReviewActivityManagement mReviewActivityManagement;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.event_review_view, mLayout_body);
		
		mLayout = (LinearLayout)findViewById(R.id.LinearLayoutMain);
		
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(REPLY_EVENT_ID))
		{
			eventid = extras.getInt(REPLY_EVENT_ID, 0);
		}
		
		mReviewActivityManagement = new ReviewActivityManagement(ReviewActivity.this,eventid);
		mReviewActivityManagement.InitUI(mLayout);
		
		initTitlebar();
		hideToolbar();
	}
	
	/**
	 * Titlebar
	 */
	private void initTitlebar() {

		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
		p.setMargins(0, 0, 5, 0);
		
		// 发送
		buttonSave = new Button(this);
		buttonSave.setBackgroundResource(R.drawable.btn_title_blue_selector);
		buttonSave.setText(getString(R.string.consultation_commit));
		buttonSave.setTextAppearance(ReviewActivity.this, R.style.TitleBtnText);
		buttonSave.setLayoutParams(p);
		buttonSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mReviewActivityManagement.Summit(mHandlerSummit);
			}
		});

		setTitleRightButton(buttonSave);
		
		setTitleText(getString(R.string.eventsreview));
	}
	
	private Handler mHandlerSummit = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == ReviewActivityManagement.HANDLER_SUMMIT_SUCCEED){
				Toast.makeText(ReviewActivity.this, getResources().getString(R.string.summitsucced), Toast.LENGTH_SHORT).show();
				finishDraw();
			}else if(msg.arg1 == ReviewActivityManagement.HANDLER_SUMMIT_FAIL){
				Toast.makeText(ReviewActivity.this, getResources().getString(R.string.summitfail), Toast.LENGTH_SHORT).show();
			}
		}
	};
	

}
