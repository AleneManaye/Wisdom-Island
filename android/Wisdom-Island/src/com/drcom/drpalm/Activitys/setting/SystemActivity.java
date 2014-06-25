package com.drcom.drpalm.Activitys.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TableLayout;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.events.NewEventActivity;
import com.drcom.drpalm.DB.AttendanceinfoDB;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.DB.LatestNewsDB;
import com.drcom.drpalm.DB.NewsDB;
import com.drcom.drpalm.DB.SystemInfoDB;
import com.drcom.drpalm.Tool.drHttpClient.HttpStatus;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.View.controls.MyHourPicker;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.View.login.LoginManager;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.View.setting.SettingManager;
import com.wisdom.island.R;
import com.drcom.ui.View.controls.SlipBtn.SlipButton;
import com.drcom.ui.View.controls.SlipBtn.SlipButton.OnChangedListener;

public class SystemActivity extends ModuleActivity {
	private static final int PUSHSET_SUCCESS = 1;
	private static final int PUSHSET_FAILED = 2;
	private TextView setting_pushtime;
	private RelativeLayout settingTime, settingShacke, settingSound;
//	private CheckBox push;
//	private CheckBox sound;
//	private CheckBox shake;
//	private CheckBox m_cbOnlyWifi = null;
	private SlipButton mSlipButtonPush,mSlipButtonSound,mSlipButtonShake,mSlipButtonOnlyWifi;
	private Button commit, clearNewsButton, clearInfomation;
	private RelativeLayout clearnews, clearcnews, setting_main;// 推送模块
	private LinearLayout line;
	private TableLayout push_block;
	private SlidingDrawer moduleDrawer;

	private SettingManager setInstance = null;
	private LoginManager instance = LoginManager.getInstance(GlobalVariables.gAppContext);
	private EventsDB mEventsDB;
	private NewsDB mNewsDB;
	private AttendanceinfoDB mAttendanceinfoDB;
	private LatestNewsDB mLatestNewsDB;
	private SystemInfoDB mSystemInfoDB;

	private String mUsername = "";
	private int start;
	private int end;
	private int mHourPickerStart;// 记录时间表上设置的时间
	private int mHourPickerEnd;
	private boolean isLogin = false;
	private boolean isSaved = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.setting_system, mLayout_body);

		initData();
		hideToolbar();
	}

	/**
	 * 初始化数据 if (LoginManager.OnlineStatus.OFFLINE ==
	 * instance.getOnlineStatus()) {
	 */
	private void initData() {
//		m_cbOnlyWifi = (CheckBox) findViewById(R.id.onlywifi);
		push_block = (TableLayout) findViewById(R.id.system_pushblock);
		setting_pushtime = (TextView) findViewById(R.id.setting_pushtime);
		settingTime = (RelativeLayout) findViewById(R.id.settingTime);
		settingShacke = (RelativeLayout) findViewById(R.id.settingShacke);
		settingSound = (RelativeLayout) findViewById(R.id.settingSound);
		setting_main = (RelativeLayout) findViewById(R.id.setting_main);
//		push = (CheckBox) findViewById(R.id.checkbox_system_push);
//		sound = (CheckBox) findViewById(R.id.checkbox_system_sound);
//		shake = (CheckBox) findViewById(R.id.checkbox_system_shake);
		mSlipButtonPush = (SlipButton)findViewById(R.id.slipbutton_system_push);
		mSlipButtonPush.SetResid (R.drawable.switch_on,R.drawable.switch_off, R.drawable.switch_slider);
		mSlipButtonSound = (SlipButton)findViewById(R.id.slipbutton_system_sound);
		mSlipButtonSound.SetResid (R.drawable.switch_on,R.drawable.switch_off, R.drawable.switch_slider);
		mSlipButtonShake = (SlipButton)findViewById(R.id.slipbutton_system_shake);
		mSlipButtonShake.SetResid (R.drawable.switch_on,R.drawable.switch_off, R.drawable.switch_slider);
		mSlipButtonOnlyWifi = (SlipButton)findViewById(R.id.slipbutton_onlywifi);
		mSlipButtonOnlyWifi.SetResid (R.drawable.switch_on,R.drawable.switch_off, R.drawable.switch_slider);
		clearnews = (RelativeLayout) findViewById(R.id.clearnews);// 清除离线消息
		clearcnews = (RelativeLayout) findViewById(R.id.clearcnews);// 清除离线通告
		line = (LinearLayout) findViewById(R.id.system_clear_line);// 间隔线
		moduleDrawer = (SlidingDrawer) findViewById(R.id.module_drawer);// 抽屉组件
		clearNewsButton = (Button) findViewById(R.id.clearNewsDrawerButton);
		clearInfomation = (Button) findViewById(R.id.clearInfomation);

		setInstance = SettingManager.getSettingManager(this);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		mEventsDB = EventsDB.getInstance(this, GlobalVariables.gSchoolKey);
		mNewsDB = NewsDB.getInstance(this, GlobalVariables.gSchoolKey);
		mLatestNewsDB = LatestNewsDB.getInstance(this, GlobalVariables.gSchoolKey);
		mAttendanceinfoDB = AttendanceinfoDB.getInstance(this, GlobalVariables.gSchoolKey);
		mSystemInfoDB = SystemInfoDB.getInstance(this, GlobalVariables.gSchoolKey);

		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
		commit = new Button(this);
		commit.setLayoutParams(p);
		commit.setBackgroundResource(R.drawable.btn_title_blue_selector);
		commit.setText(getString(R.string.saveimagefile));
		commit.setTextAppearance(SystemActivity.this, R.style.TitleBtnText);
		setTitleRightButton(commit);
		commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (HttpStatus.IsNetUsed(GlobalVariables.gAppContext) == HttpStatus.STATUS_NOCONNECT) { // 没网络
					if (!isLogin) {
						setInstance.bOnlyWiFi = mSlipButtonOnlyWifi.isChecked();// 是否只使用wifi
						setInstance.saveSetting();// 保存设置
						setTimeWrong(getResources().getString(R.string.saveimagefilesuccess));
						isSaved = true;
					} else {
						setTimeWrong(getResources().getString(R.string.system_savewrong));
						isSaved = false;
					}
					return;
				} else {// 有网络
					if (!isLogin) {
						setInstance.bOnlyWiFi = mSlipButtonOnlyWifi.isChecked();// 是否只使用wifi
						setInstance.saveSetting();// 保存设置
						setTimeWrong(getResources().getString(R.string.saveimagefilesuccess));
						isSaved = true;
					} else {
						//setPushInfotoServer();
						String pushtime = "[{\"start\":\"" + start + ":00\",\"end\":\"" + end + ":00\"}]";
						setInstance.setPushInfo2Server(mSlipButtonPush.isChecked(), mSlipButtonSound.isChecked(), mSlipButtonShake.isChecked(), pushtime	, mChooseTimeHandler);
					}
				}
			}
		});

//		m_cbOnlyWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				setInstance.bOnlyWiFi = isChecked;
//			}
//		});
		mSlipButtonOnlyWifi.SetOnChangedListener(new OnChangedListener() {
			public void OnChanged(boolean CheckState) {
				setInstance.bOnlyWiFi = CheckState;
			}
		});

		settingTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDateMessage();
			}
		});
		
		if (LoginManager.OnlineStatus.OFFLINE == instance.getOnlineStatus()) {// 没登陆时
			setInfomation();
			push_block.setVisibility(View.GONE);
			clearcnews.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
			isLogin = false;
		} else {// 登陆时
			// 从本地读取数据，设置控件状态，全局变量start ,end
			setInfomation();
			isLogin = true;
			push_block.setVisibility(View.VISIBLE);
			clearcnews.setVisibility(View.VISIBLE);
			line.setVisibility(View.VISIBLE);
		}

		if (mSlipButtonPush.isChecked()) {
			settingTime.setClickable(true);
			mSlipButtonSound.setClickable(true);
			mSlipButtonShake.setClickable(true);
			settingTime.setVisibility(View.VISIBLE);
			settingShacke.setVisibility(View.VISIBLE);
			settingSound.setVisibility(View.VISIBLE);
			// setTextColor(getResources().getColor(R.color.black));
		} else {
			settingTime.setClickable(false);
			mSlipButtonSound.setClickable(false);
			mSlipButtonShake.setClickable(false);
			settingTime.setVisibility(View.GONE);
			settingShacke.setVisibility(View.GONE);
			settingSound.setVisibility(View.GONE);
			// setTextColor(getResources().getColor(R.color.light_gray));
		}
		
//		push.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				// setPushInfotoServer();// 设置服务器
//				if (isChecked) {
//					settingTime.setClickable(true);
//					sound.setClickable(true);
//					shake.setClickable(true);
//					settingTime.setVisibility(View.VISIBLE);
//					settingShacke.setVisibility(View.VISIBLE);
//					settingSound.setVisibility(View.VISIBLE);
//				} else {// 不推送则设置其它控件不可点击
//					settingTime.setVisibility(View.GONE);
//					settingShacke.setVisibility(View.GONE);
//					settingSound.setVisibility(View.GONE);
//				}
//
//			}
//		});
		mSlipButtonPush.SetOnChangedListener(new OnChangedListener() {
			public void OnChanged(boolean CheckState) {
				if (CheckState) {
					settingTime.setClickable(true);
					mSlipButtonSound.setClickable(true);
					mSlipButtonShake.setClickable(true);
					settingTime.setVisibility(View.VISIBLE);
					settingShacke.setVisibility(View.VISIBLE);
					settingSound.setVisibility(View.VISIBLE);
				} else {// 不推送则设置其它控件不可点击
					settingTime.setVisibility(View.GONE);
					settingShacke.setVisibility(View.GONE);
					settingSound.setVisibility(View.GONE);
				}
			}
		});
		
//		sound.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				// setPushInfotoServer();// 设置服务器
//			}
//		});
//		shake.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				// setPushInfotoServer();// 设置服务器
//
//			}
//		});
		// 取消按钮
		clearInfomation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeDrawer();
			}
		});

		// 清除校园新闻
		clearnews.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!moduleDrawer.isOpened()) {
					clearNewsButton.setText(getResources().getString(R.string.system_clearnews));
					openDrawer();
					clearNewsButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 清除操作,消息
							mLatestNewsDB.clearNews();
							mNewsDB.clearAllStories();
							ImageLoader mImageLoader = getmSchoolImageLoader();
							mImageLoader.clearCache();
							closeDrawer();
						}
					});
				}

			}
		});
		// 清除班级通告
		clearcnews.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!moduleDrawer.isOpened()) {
					clearNewsButton.setText(getResources().getString(R.string.system_clearcnews));
					openDrawer();
					clearNewsButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 清除操作,通告
							mLatestNewsDB.clearTable(mUsername);
							mEventsDB.clearAllStories(mUsername);
							mAttendanceinfoDB.cleanAttendanceinfo(mUsername);
							mSystemInfoDB.clean(mUsername);
							ImageLoader mImageLoader = getmClassImageLoader();
							mImageLoader.clearCache();
							closeDrawer();
						}
					});
				}

			}
		});
		setTitleText(getString(R.string.systemsetting));
	}

	// 保存到网络
	private Handler mChooseTimeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case PUSHSET_SUCCESS:
				String time = start + ":00 - " + end + ":00";
				setting_pushtime.setText(time);// 推送时间
				saveInfomation();// 设置成功则保存到本地
				setTimeWrong(getResources().getString(R.string.saveimagefilesuccess));
				isSaved = true;
				break;
			case PUSHSET_FAILED:
				setTimeWrong(getResources().getString(R.string.system_savewrong));
				String strError = (msg.obj != null) ? (String) msg.obj : getString(R.string.album_downfail);
				new ErrorNotificatin(SystemActivity.this).showErrorNotification(strError);
				isSaved = false;
				break;
			default:
				break;
			}
		}
	};

//	private void setPushInfotoServer() {
//		String pushtime = "[{\"start\":\"" + start + ":00\",\"end\":\"" + end + ":00\"}]";
//		// 保存设置到服务端
//		RequestOperation mRequestOperation = RequestOperation.getInstance();
//		RequestOperationCallback callback = new RequestOperationCallback() {
//			@Override
//			public void onError(String str) {
//				Message msg = new Message();
//				msg.arg1 = PUSHSET_FAILED;
//				msg.obj = str;
//				mChooseTimeHandler.sendMessage(msg);
//			}
//
//			@Override
//			public void onSuccess() {
//				Message msg = new Message();
//				msg.arg1 = PUSHSET_SUCCESS;
//				mChooseTimeHandler.sendMessage(msg);
//			}
//		};
//		mRequestOperation.sendGetNeededInfo("PushInfo", new Object[] { push.isChecked(), sound.isChecked(), shake.isChecked(), pushtime, callback }, callback.getClass().getName());
//
//	}

	/**
	 * 从本地读取数据，设置控件状态，全局变量start ,end
	 * 
	 */
	private void setInfomation() {
		// 从本地读取用户设置，并保存
		setInstance = SettingManager.getSettingManager(this);
		mSlipButtonSound.setCheck(GlobalVariables.gPushSettingInfo.ifsound);// 声音
		mSlipButtonShake.setCheck(GlobalVariables.gPushSettingInfo.ifshake);// 振动
		mSlipButtonPush.setCheck(GlobalVariables.gPushSettingInfo.ifpush);// 推送
		mSlipButtonOnlyWifi.setCheck(setInstance.bOnlyWiFi);//Wifi
		
		if (GlobalVariables.gPushSettingInfo.pushTime.size() > 0) {
			String time = GlobalVariables.gPushSettingInfo.pushTime.get(GlobalVariables.gPushSettingInfo.pushTime.size() - 1).start + " - "
					+ GlobalVariables.gPushSettingInfo.pushTime.get(GlobalVariables.gPushSettingInfo.pushTime.size() - 1).end;
			setting_pushtime.setText(time);// 推送时间

			String pstart = GlobalVariables.gPushSettingInfo.pushTime.get(GlobalVariables.gPushSettingInfo.pushTime.size() - 1).start;
			String pend = GlobalVariables.gPushSettingInfo.pushTime.get(GlobalVariables.gPushSettingInfo.pushTime.size() - 1).end;

			mHourPickerStart = Integer.parseInt(pstart.substring(0, pstart.indexOf(":")));
			mHourPickerEnd = Integer.parseInt(pend.substring(0, pend.indexOf(":")));
			start = mHourPickerStart;
			end = mHourPickerEnd;
		} else {
			start = setInstance.nPushStartTime;
			end = setInstance.nPushEndTime;
			String time = String.valueOf(start) + ":00-" + String.valueOf(end) + ":00";
			setting_pushtime.setText(time);// 推送时间
		}

	}

	/**
	 * 从控件读取数据，并保存到本地
	 * 
	 */
	private void saveInfomation() {
		// 保存设置到本地
		setInstance.bVoicePrompt = mSlipButtonSound.isChecked();// 声音
		setInstance.bVibrationPrompt = mSlipButtonShake.isChecked();// 振动
		setInstance.bPush = mSlipButtonPush.isChecked();// 推送
		setInstance.nPushStartTime = start;// 开始时间
		setInstance.nPushEndTime = end;// 结束时间
		setInstance.bOnlyWiFi = mSlipButtonOnlyWifi.isChecked();// 是否只使用wifi
		setInstance.saveSetting();// 保存设置

		GlobalVariables.gPushSettingInfo.ifsound = mSlipButtonSound.isChecked();// 声音
		GlobalVariables.gPushSettingInfo.ifshake = mSlipButtonShake.isChecked();// 振动
		GlobalVariables.gPushSettingInfo.ifpush = mSlipButtonPush.isChecked();// 推送
		GlobalVariables.gPushSettingInfo.pushTime.get(GlobalVariables.gPushSettingInfo.pushTime.size() - 1).start = String.valueOf(start) + ":00";
		GlobalVariables.gPushSettingInfo.pushTime.get(GlobalVariables.gPushSettingInfo.pushTime.size() - 1).end = String.valueOf(end) + ":00";
	}

	/**
	 * 选择时间对话框
	 * 
	 * @param pTitle
	 */
	private void showDateMessage() {
		final AlertDialog dlg = new AlertDialog.Builder(SystemActivity.this).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.setting_setpushtime);
		final MyHourPicker mHourPicker = (MyHourPicker) window.findViewById(R.id.hourtime_picker);
		mHourPicker.setTimeVisibility(View.VISIBLE);
		// 设置控件初始时间为本地时间
		mHourPicker.setStartHours(mHourPickerStart);
		mHourPicker.setEndHours(mHourPickerEnd);
		Button ok = (Button) window.findViewById(R.id.datetime_ok);
		ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String starttime = mHourPicker.getStartTime();
				String endttime = mHourPicker.getEndTime();
				String time = mHourPicker.getTime();
				// 记录时间表上设置的时间
				SystemActivity.this.mHourPickerStart = Integer.parseInt(time.substring(0, time.indexOf(":")).trim());
				SystemActivity.this.mHourPickerEnd = Integer.parseInt(time.substring(time.indexOf("-") + 1, time.lastIndexOf(":")).trim());
				if (Integer.parseInt(starttime) < Integer.parseInt(endttime)) {
					SystemActivity.this.start = Integer.parseInt(time.substring(0, time.indexOf(":")).trim());
					SystemActivity.this.end = Integer.parseInt(time.substring(time.indexOf("-") + 1, time.lastIndexOf(":")).trim());
					// setPushInfotoServer();// 设置服务器
					String times = start + ":00 - " + end + ":00";
					setting_pushtime.setText(times);// 推送时间
				} else {
					setTimeWrong(getString(R.string.pushtimewrong));
				}
				dlg.cancel();
			}
		});

		// 关闭alert对话框
		Button cancel = (Button) window.findViewById(R.id.datetime_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dlg.cancel();
			}
		});
	}

	/**
	 * 时间设置失败提示
	 */
	private void setTimeWrong(String info) {
		Activity activity = this;
		while (this.getParent() != null) {
			activity = this.getParent();
		}
		// AlertDialog.Builder ab = new AlertDialog.Builder(activity);
		// ab.setTitle(info);
		// ab.setPositiveButton(getString(R.string.ok), new
		// DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// }
		// });

		final Dialog lDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
		lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		lDialog.setContentView(R.layout.r_normaldialogview);
		((TextView) lDialog.findViewById(R.id.dialog_title)).setVisibility(View.GONE);
		((TextView) lDialog.findViewById(R.id.dialog_message)).setText(info);
		Button btn_ok = (Button) lDialog.findViewById(R.id.ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// write your code to do things after users clicks OK
				lDialog.dismiss();
				if (isSaved) {
					finishDraw();
				}
			}
		});

		((Button) lDialog.findViewById(R.id.cancel)).setVisibility(View.GONE);

		if (!activity.isFinishing()) {
			lDialog.show();
		}

	}

	/**
	 * 打开抽屉
	 */
	public void openDrawer() {
		moduleDrawer.animateOpen();
	}

	/**
	 * 关闭底部抽屉
	 * 
	 */
	public void closeDrawer() {
		moduleDrawer.animateClose();
	}
}
