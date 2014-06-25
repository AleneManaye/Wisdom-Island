package com.drcom.drpalm.Activitys.setting;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.DB.DiaryDB;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.View.setting.DiaryManager;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.DiaryItem;
import com.drcom.drpalm.objs.GrowdiaryItem;
import com.wisdom.island.R;

public class DiarySendActivity extends ModuleActivity{
	
	public static final String DIARY_ID = "diary_id";
	public static final int SEND_DIARY_LIST_START = 3;
	public static final int SEND_DIARY_LIST_SUCCESSFUL = 4;
	public static final int SEND_DIARY_LIST_FAILED = 5;
	
	private EditText mTitleEdit;
	private EditText mContentEdit;
	private TextView mTitleError;
	private TextView mContentError;
	private TextView mTitleDes;
	private TextView mContentDes;
	private int diary_id = -1;
	private GrowdiaryItem mGrowdiaryItem = new GrowdiaryItem();
	private Handler uiHandler = null;
	private DiaryDB mDiaryDB;
	private String mUsername;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.consultation_feedback, mLayout_body);
		hideToolbar();
		Intent intent = getIntent();
		if(intent != null){
			Bundle bundle = intent.getExtras();
			if(bundle != null){
				if(bundle.containsKey(DIARY_ID)){
					diary_id = bundle.getInt(DIARY_ID);
				}
			}
		}
		mDiaryDB = DiaryDB.getInstance(this, GlobalVariables.gSchoolKey);
		SettingManager setInstance = SettingManager.getSettingManager(this);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		initHandler();
		initViews();
		initSendBtn();
	}
	
	private void initViews(){
		mTitleEdit = (EditText)findViewById(R.id.feedback_email_editor);
		mTitleEdit.setHint(R.string.title_cant_be_null);
		mContentEdit = (EditText)findViewById(R.id.feedback_content_editor);
		mContentEdit.setHint(R.string.consultation_contenthint);
		mTitleError = (TextView)findViewById(R.id.consultation_femail_check);
		mContentError = (TextView)findViewById(R.id.consultation_fcontent_check);
		mTitleDes = (TextView)findViewById(R.id.consultation_femail_des);
		mTitleDes.setText(R.string.diary_title);
		mContentDes = (TextView)findViewById(R.id.consultation_content_text);
		mContentDes.setText(R.string.diary_content);
		if(diary_id >= 0){
			DiaryItem item = mDiaryDB.getDiaryItemByDiaryID(diary_id,mUsername);
			mTitleEdit.setText(item.diaryTitle);
			mContentEdit.setText(item.diaryContent);
		}
		setTitleText(getString(R.string.diary));
	}
	
	private void initSendBtn(){
		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
		Button commit = new Button(this);
		commit.setLayoutParams(p);
		commit.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_title_blue_selector));
		commit.setTextAppearance(DiarySendActivity.this, R.style.TitleBtnText);
		if(diary_id >= 0){
			commit.setText(getString(R.string.saveimagefile));
		}else{
			commit.setText(getString(R.string.send));
		}
		setTitleRightButton(commit);
		commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkComponent()){
					ArrayList<GrowdiaryItem> filelist = new ArrayList<GrowdiaryItem>();
					if(mGrowdiaryItem != null){
						filelist.add(mGrowdiaryItem);
					}
					SubmitGrowdiary(filelist);
				}
			}
		});
	}
	
	private void SubmitGrowdiary(ArrayList<GrowdiaryItem> filelist){
		DiaryManager diaryManager = new DiaryManager(this);
		diaryManager.SubmitGrowdiary(filelist, uiHandler);
	}
	
	private boolean checkComponent(){
		boolean isCheckRight = true;
		String title = mTitleEdit.getText().toString().trim();
		String content = mContentEdit.getText().toString().trim();
		if(title.equals("")){
			mTitleError.setVisibility(View.VISIBLE);
			mTitleError.setText(getResources().getString(R.string.title_cant_be_null));
			mTitleEdit.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.consultation_wrong), null);
			isCheckRight =  false;
		}else{
			mTitleError.setVisibility(View.GONE);
			mTitleError.setText("");
			mTitleEdit.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}
		if(content.equals("")){
			mContentError.setText(getResources().getString(R.string.consultation_contenthint));
			mContentEdit.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.consultation_wrong), null);
			isCheckRight =  false;
		}else{
			mContentError.setText("");
			mContentEdit.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}
		if(isCheckRight){
			mGrowdiaryItem.title = title;
			mGrowdiaryItem.contect = content;
			if(diary_id >= 0){
				mGrowdiaryItem.diaryid = String.valueOf(diary_id);
				mGrowdiaryItem.status = "M";
			}else{
				mGrowdiaryItem.diaryid = "";
				mGrowdiaryItem.status = "N";
			}
		}
		return isCheckRight;
	}
	
	private void initHandler(){
		 uiHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.arg1 == SEND_DIARY_LIST_START)
				{
					showProgressBar();
				}
				else if(msg.arg1 == SEND_DIARY_LIST_SUCCESSFUL)
				{
					hideProgressBar();
					finish();
				}
				else if(msg.arg1 == SEND_DIARY_LIST_FAILED)
				{
					hideProgressBar();	
					String strError = (msg.obj != null)?(String)msg.obj:"";
					new ErrorNotificatin(DiarySendActivity.this).showErrorNotification(strError);
				}
			}
				
		};
	}
}
