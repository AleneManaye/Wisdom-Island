package com.drcom.drpalm.Activitys.consultation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.drcom.drpalm.View.consultation.ConsultationActivityManagement;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.objs.ConsultDraftItem;
import com.drcom.drpalm4tianzhujiao.R;

/**
 * 园长信箱
 * @author zhaojunjie
 *
 */
public class ConsultationActivity extends ModuleActivity {
	private static final int SUCCESS = 1;
	private static final int ERROR = 0;

	private EditText name;
	private EditText email;
	private EditText phone;
	private EditText content;
	private EditText title;
	private Button commit;
	private TextView name_check;
	private TextView email_check;
	private TextView phone_check;
	private TextView content_check;
	// private TextView title_check;

	private String sname;
	private String semail;
	private String sphone;
	private String scontent;
	private String stitle;
	
	private ConsultationActivityManagement mConsultationActivityManagement;

//	private SharedPreferences mSharedPreferences;
//	private Editor mEditor;
	private ConsultDraftItem mConsultDraftItem;
	private boolean isSpellRight = false;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				name.setText("");
				email.setText("");
				phone.setText("");
				content.setText("");
				title.setText("");
				savEdit();

				name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				email.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				phone.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				content.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				commit.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_title_green_selector));
				commit.setText(getString(R.string.send));
				ToastInfo(getResources().getString(R.string.consultation_send_success));
				break;
			case ERROR:
				String strError = (msg.obj != null) ? (String) msg.obj : getString(R.string.album_downfail);
				new ErrorNotificatin(ConsultationActivity.this).showErrorNotification(strError);
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
		inflater.inflate(R.layout.consultation_main, mLayout_body);
		mConsultationActivityManagement = new ConsultationActivityManagement(ConsultationActivity.this);
		hideProgressBar();
		initComponent();
		commitContent();
	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {
		hideToolbar();
		name = (EditText) findViewById(R.id.consultation_name_editor);
		email = (EditText) findViewById(R.id.consultation_email_Editor);
		phone = (EditText) findViewById(R.id.consultation_phone_editor);
		content = (EditText) findViewById(R.id.consultation_content_editor);
		title = (EditText) findViewById(R.id.consultation_title_Editor);

		name_check = (TextView) findViewById(R.id.consultation_name_check);
		email_check = (TextView) findViewById(R.id.consultation_email_check);
		phone_check = (TextView) findViewById(R.id.consultation_phone_check);
		// title_check = (TextView) findViewById(R.id.consultation_title_check);
		content_check = (TextView) findViewById(R.id.consultation_content_check);

		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
		commit = new Button(this);
		commit.setLayoutParams(p);
		commit.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_title_green_selector));
		commit.setText(getString(R.string.send));
		commit.setTextAppearance(ConsultationActivity.this, R.style.TitleBtnText);
		setTitleRightButton(commit);
		
		mConsultDraftItem = mConsultationActivityManagement.getConsultDraftDetail();
		name.setText(mConsultDraftItem.username);
		phone.setText(mConsultDraftItem.phone);
		email.setText(mConsultDraftItem.email);
		content.setText(mConsultDraftItem.content);
		title.setText(mConsultDraftItem.title);
		
		setTitleText(getString(R.string.schoolmail));
	}

	/**
	 * 检查拼写
	 * 
	 */
	private boolean checkComponent() {
		sname = name.getText().toString().trim();
		semail = email.getText().toString().trim();
		sphone = phone.getText().toString().trim();
		scontent = content.getText().toString().trim();
		stitle = title.getText().toString().trim();

		setTitlebarBgColor(getResources().getColor(R.color.bgblue));

		isSpellRight = true;
		if ("".equals(sname)) {
			name_check.setText(getResources().getString(R.string.consultation_name_empty));
			name.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.consultation_wrong), null);
			isSpellRight = false;
		} else {
			name_check.setText("");
			name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}
		if ("".equals(scontent)) {
			content_check.setText(getResources().getString(R.string.consultation_content_empty));
			content.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.consultation_wrong), null);
			isSpellRight = false;
		} else {
			content_check.setText("");
			content.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}

		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(semail);
		if ("".equals(semail)) {
			email_check.setText("");
			email.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		} else if (!matcher.matches()) {
			email_check.setText(getResources().getString(R.string.consultation_email_wrong));
			email.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.consultation_wrong), null);
			isSpellRight = false;
		} else {
			email_check.setText("");
			email.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}

		if ("".equals(sphone)) {
			phone_check.setText(getResources().getString(R.string.consultation_tel_empty));
			phone.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.consultation_wrong), null);
			isSpellRight = false;
		} else if (sphone.length() < 7 || sphone.length() > 15) {
			phone_check.setText(getResources().getString(R.string.consultation_tel_wrong));
			phone.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.consultation_wrong), null);
			isSpellRight = false;
		} else {
			phone_check.setText("");
			phone.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
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
					mConsultDraftItem.username = sname;
					mConsultDraftItem.content = scontent;
					mConsultDraftItem.phone = sphone;
					mConsultDraftItem.email = semail;
					mConsultDraftItem.title = stitle;
					mConsultDraftItem.type = "consult";// 此字段用于区分意见反馈 和入托咨询
					
					mConsultationActivityManagement.Commit(mConsultDraftItem, mHandler);
					
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
//		mEditor.putString("name", name.getText().toString());
//		mEditor.putString("phone", phone.getText().toString());
//		mEditor.putString("email", email.getText().toString());
//		mEditor.putString("content", content.getText().toString());
//		mEditor.putString("title", title.getText().toString());
//		mEditor.commit();
		
		ConsultDraftItem consultDraftItem = new ConsultDraftItem();
		consultDraftItem.username = name.getText().toString();
		consultDraftItem.phone = phone.getText().toString();
		consultDraftItem.email = email.getText().toString();
		consultDraftItem.content = content.getText().toString();
		consultDraftItem.title = title.getText().toString();
		
		mConsultationActivityManagement.SaveConsultDraftDetail(mConsultDraftItem);
	}

	/**
	 * 显示提示信息
	 * 
	 * @param str
	 *            提示内容
	 */
	private void ToastInfo(String str) {
		// AlertDialog.Builder ab = new
		// AlertDialog.Builder(ConsultationActivity.this);
		// ab.setTitle(str);
		// ab.setPositiveButton(getString(R.string.ok), new
		// DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// }
		// });
		// ab.show();

		final Dialog lDialog = new Dialog(ConsultationActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
		lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		lDialog.setContentView(R.layout.r_normaldialogview);
		((TextView) lDialog.findViewById(R.id.dialog_title)).setVisibility(View.GONE);
		((TextView) lDialog.findViewById(R.id.dialog_message)).setText(str);
		Button btn_ok = (Button) lDialog.findViewById(R.id.ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				lDialog.dismiss();
				finishDraw();
			}
		});
		Button btn_cancel = (Button) lDialog.findViewById(R.id.cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lDialog.dismiss();

			}
		});

		// ((Button)
		// lDialog.findViewById(R.id.cancel)).setVisibility(View.GONE);
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
