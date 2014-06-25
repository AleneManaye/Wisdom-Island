package com.drcom.drpalm.Activitys.consultation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.setting.DiarySendActivity;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.View.consultation.FeedBackActivityManagement;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.objs.ConsultDraftItem;
import com.wisdom.island.R;

/**
 * 意见反馈
 * @author zhaojunjie
 *
 */
public class FeedBackActivity extends ModuleActivity {
	private static final int SUCCESS = 1;
	private static final int ERROR = 0;

	private EditText email;
	private EditText content;
	private Button commit;
	private TextView email_check;
	private TextView content_check;
	private TextView email_des;
	private TextView content_des;

	private String semail;
	private String scontent;

//	private SharedPreferences mSharedPreferences;
//	private Editor mEditor;
	private FeedBackActivityManagement mFeedBackActivityManagement;
	private ConsultDraftItem mConsultDraftItem;
	private boolean isSpellRight = false;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				email.setText("");
				content.setText("");
				savEdit();

				ToastInfo(getResources().getString(R.string.consultation_send_success));
				break;
			case ERROR:
				String strError = (msg.obj != null) ? (String) msg.obj : getString(R.string.album_downfail);
				new ErrorNotificatin(FeedBackActivity.this).showErrorNotification(strError);
				break;
			default:
				break;
			}
			hideProgressBar();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.consultation_feedback, mLayout_body);
		hideProgressBar();
		initComponent();
		commitContent();
	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {
		hideToolbar();
		email = (EditText) findViewById(R.id.feedback_email_editor);
		email.setHint(getResources().getString(R.string.feedback_email));
		content = (EditText) findViewById(R.id.feedback_content_editor);
		content.setHint(getResources().getString(R.string.consultatio_content_input));
		email_check=(TextView) findViewById(R.id.consultation_femail_check);
		content_check=(TextView) findViewById(R.id.consultation_fcontent_check);
		email_des = (TextView)findViewById(R.id.consultation_femail_des);
		email_des.setText(R.string.calling);
		content_des = (TextView)findViewById(R.id.consultation_content_text);
		content_des.setText(R.string.feeding_suggest);
		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
		commit = new Button(this);
		commit.setLayoutParams(p);
		commit.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_title_green_selector));
		commit.setText(getString(R.string.send));
		commit.setTextAppearance(FeedBackActivity.this, R.style.TitleBtnText);
		setTitleRightButton(commit);

//		mSharedPreferences = FeedBackActivity.this.getSharedPreferences("consultation", MODE_PRIVATE);
//		mEditor = mSharedPreferences.edit();
		
		mFeedBackActivityManagement = new FeedBackActivityManagement(FeedBackActivity.this);
		
		mConsultDraftItem = mFeedBackActivityManagement.getConsultDraftDetail();
		email.setText(mConsultDraftItem.email);
		content.setText(mConsultDraftItem.content);

		setTitleText(getString(R.string.advice_feedback));
	}

	/**
	 * 检查拼写
	 * 
	 */
	private boolean checkComponent() {
		semail = email.getText().toString().trim();
		scontent = content.getText().toString().trim();

		isSpellRight = true;
		if ("".equals(semail)) {
			email_check.setVisibility(View.VISIBLE);
			email_check.setText(getResources().getString(R.string.consultation_input_contact_infomation));
			email.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.consultation_wrong), null);
			isSpellRight = false;
		} else{
			email_check.setText("");
			email_check.setVisibility(View.GONE);
			email.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			
		}
		if("".equals(scontent)){
			content_check.setText(getResources().getString(R.string.consultation_content_empty));
			content.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.consultation_wrong), null);
			isSpellRight = false;
		}else{
			content_check.setText("");
			content.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}
		return isSpellRight;
	}

	/**
	 * 
	 * 提交内容
	 * 
	 */
	private void commitContent() {

		commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkComponent()) {
					showProgressBar();
					mConsultDraftItem = new ConsultDraftItem();
					mConsultDraftItem.content = scontent;
					mConsultDraftItem.email = semail;
					mConsultDraftItem.type = "feedback";// 此字段用于区分意见反馈 和入托咨询
					
					mFeedBackActivityManagement.Commit(mConsultDraftItem, mHandler);
					
//					RequestOperation mRequestOperation = RequestOperation.getInstance();
//					RequestOperationCallback callback = new RequestOperationCallback() {
//						@Override
//						public void onSuccess() {
//							Message msg = new Message();
//							msg.what = SUCCESS;
//							mHandler.sendMessage(msg);
//						}
//
//						@Override
//						public void onError(String err) {
//							Message msg = new Message();
//							msg.what = ERROR;
//							msg.obj = err;
//							mHandler.sendMessage(msg);
//						}
//					};
//					mRequestOperation.sendGetNeededInfo("PutConSult", new Object[] { mConsultDraftItem, callback }, callback.getClass().getName());
				}
			}
		});
	}

	/**
	 * 保存编辑框中的内容到本地，方便下次读取
	 */
	private void savEdit() {
		
		ConsultDraftItem consultDraftItem = new ConsultDraftItem();
		consultDraftItem.email = email.getText().toString();
		consultDraftItem.content = content.getText().toString();
		mFeedBackActivityManagement.SaveConsultDraftDetail(consultDraftItem);
		
//		mConsultationActivityManagement.SaveConsultDraftDetail(mConsultDraftItem);
//		mEditor.putString("feedemail", email.getText().toString());
//		mEditor.putString("feedcontent", content.getText().toString());
//		mEditor.commit();
	}

	/**
	 * 显示提示信息
	 * 
	 * @param str
	 *            提示内容
	 */
	private void ToastInfo(String str) {
		AlertDialog.Builder ab = new AlertDialog.Builder(FeedBackActivity.this);
//		ab.setTitle(str);
//		ab.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
//		ab.show();
		
		final Dialog lDialog = new Dialog(FeedBackActivity.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		lDialog.setContentView(R.layout.r_normaldialogview);
		((TextView) lDialog.findViewById(R.id.dialog_title)).setVisibility(View.GONE);
		((TextView) lDialog.findViewById(R.id.dialog_message)).setText(str);
		Button btn_ok = (Button)lDialog.findViewById(R.id.ok);
		btn_ok.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						lDialog.dismiss();
						finishDraw();
					}
				});

		Button btn_cancel = (Button)lDialog.findViewById(R.id.cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						lDialog.dismiss();
					}
				});
 		lDialog.show();
	}

	/**
	 * 退出时保存
	 */
	@Override
	protected void onDestroy() {
		savEdit();
		super.onDestroy();
	}
}
