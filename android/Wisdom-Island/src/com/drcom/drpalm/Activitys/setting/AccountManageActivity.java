package com.drcom.drpalm.Activitys.setting;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.danceinfo.AttendanceActivity;
import com.drcom.drpalm.Activitys.events.NewEventActivity;
import com.drcom.drpalm.Activitys.events.NewEventAttc2Activity;
import com.drcom.drpalm.Activitys.myphoto.MyPhotoDetailActivity;
import com.drcom.drpalm.Definition.ActivityActionDefine;
import com.drcom.drpalm.Tool.BitmapUtil;
import com.drcom.drpalm.Tool.ImageUntil;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.jsonparser.SubmitResultParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperationCallback;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDraftItem.Attachment;
import com.wisdom.island.R;
import com.drcom.ui.View.controls.SlipBtn.SlipButton;
import com.drcom.ui.View.controls.SlipBtn.SlipButton.OnChangedListener;

/**
 * 帐号管理(个人信息)
 * 
 * @author zhaojunjie
 * 
 */
public class AccountManageActivity extends ModuleActivity {
	public static final int RESULT_CAPTURE_IMAGE = 1;
	public static final int RESULT_CHOOSE_IMAGE = 2;
	private static final String IMGDIR = "/DrpalmPic/";
	// private int SUM = 15;
	private static final int CompressQuality = 20; // 相片压缩质量 (0-100)100为质量最高

	//
	private TextView mtvAccountName = null; // 登陆后返回的用户称呼
	private TextView mtvUserName = null; // 登陆的帐号
	private TextView mtvLevel;
	private TextView mtvScore;
	private TextView mtvLevelupScore;
	private ImageView mImgViewUserpic;
	private SlipButton mSlipButtonRememberPwd, mSlipButtonAutologin;
	private Button commit;

	private RelativeLayout mRlDiary; // 成长点滴
	private RelativeLayout mRlSelfAlbum; // 个人相册
	private RelativeLayout mRlUserpic; // 头像

	//
	private UserInfo mCurrentUserInfo = null;
	private SettingManager setInstance = null;
	private String[] mChoose;
	// private String strImgPath = "";
	// private Uri mCapturedImageUri;
	private Attachment attachmentdata;
	private ImageLoader mImageLoader;

	private Handler uiHandler;
	private UserInfo mUpdateUserInfo = null;
	public static final int LOADING = -2; // 刷新中
	public static final int UPDATEFINISH = 1; // 刷新请求返回成功
	public static final int UPDATEFAILED = 0;
	private boolean upDateFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.setting_accountmanage, mLayout_body);

		mChoose = getResources().getStringArray(R.array.choosephoto);
		mImageLoader = getmClassImageLoader();

		// @+id/textviewaccountname
		mtvAccountName = (TextView) findViewById(R.id.textviewaccountname);

		mtvUserName = (TextView) findViewById(R.id.loginusername);
		mtvLevel = (TextView) findViewById(R.id.textview_usrlevel);
		mtvScore = (TextView) findViewById(R.id.textview_usercurscore);
		mtvLevelupScore = (TextView) findViewById(R.id.textview_userlevelupscore);
		mImgViewUserpic = (ImageView) findViewById(R.id.imgviewaccountpic);
		// mImgViewUserpic.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// showListDialog(); // 选择相片对话框
		// }
		// });
		mSlipButtonRememberPwd = (SlipButton) findViewById(R.id.slipbutton_rememberpwd);
		mSlipButtonRememberPwd.SetResid(R.drawable.switch_on,
				R.drawable.switch_off, R.drawable.switch_slider);
		mSlipButtonAutologin = (SlipButton) findViewById(R.id.slipbutton_autologin);
		mSlipButtonAutologin.SetResid(R.drawable.switch_on,
				R.drawable.switch_off, R.drawable.switch_slider);

		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this,
				GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this,
				GlobalVariables.btnHeight_Titlebar));
		commit = new Button(this);
		commit.setLayoutParams(p);
		commit.setBackgroundResource(R.drawable.btn_title_blue_selector);
		commit.setText(getString(R.string.saveimagefile));
		commit.setTextAppearance(AccountManageActivity.this,
				R.style.TitleBtnText);
		setTitleRightButton(commit);
		commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setInstance.setUserInfo(mCurrentUserInfo);
				setInstance.saveSetting();
			}
		});
		commit.setVisibility(View.GONE);

		mSlipButtonRememberPwd.SetOnChangedListener(new OnChangedListener() {
			public void OnChanged(boolean CheckState) {
				if (!CheckState) {
					mSlipButtonAutologin.setCheck(false);
				}
				mCurrentUserInfo.bRememberPwd = CheckState;
				setInstance.setUserInfo(mCurrentUserInfo);
				setInstance.saveSetting();
			}
		});
		mSlipButtonAutologin.SetOnChangedListener(new OnChangedListener() {
			public void OnChanged(boolean CheckState) {
				if (CheckState) {
					mSlipButtonRememberPwd.setCheck(true);
				}
				mCurrentUserInfo.bAutoLogin = CheckState;
				setInstance.setUserInfo(mCurrentUserInfo);
				setInstance.saveSetting();
			}
		});

		// 头像
		mRlUserpic = (RelativeLayout) findViewById(R.id.RLayout_accountpic);
		mRlUserpic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showListDialog(); // 选择相片对话框
			}
		});

		// 成长点滴
		mRlDiary = (RelativeLayout) findViewById(R.id.diary);
		mRlDiary.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AccountManageActivity.this,
						DiaryActivity.class);
				startActivity(intent);
			}
		});

		// 个人相册
		mRlSelfAlbum = (RelativeLayout) findViewById(R.id.self_album);
		mRlSelfAlbum.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AccountManageActivity.this,
						MyPhotoDetailActivity.class);
				startActivity(intent);
			}
		});

		hideToolbar();
		initHandler();
		setTitleText(getString(R.string.accountmanage));
		ReflashUI();
		setInstance.GetUpdateUserInfo(
				mCurrentUserInfo.lastupdate, uiHandler,mCurrentUserInfo);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * 刷新界面
	 */
	private void ReflashUI() {
		setInstance = SettingManager
				.getSettingManager(AccountManageActivity.this);
		if (setInstance != null) {
			mCurrentUserInfo = setInstance.getCurrentUserInfo();
			if (mCurrentUserInfo != null) {
				mtvAccountName.setText(mCurrentUserInfo.strUserNickName);
				mtvUserName.setText(mCurrentUserInfo.strUsrName);
				mtvLevel.setText(mCurrentUserInfo.level);
				mtvScore.setText(mCurrentUserInfo.curscore);
				mtvLevelupScore.setText(mCurrentUserInfo.levelupscore);
				mSlipButtonRememberPwd.setCheck(mCurrentUserInfo.bRememberPwd);
				mSlipButtonAutologin.setCheck(mCurrentUserInfo.bAutoLogin);
				// if(mCurrentUserInfo.pic != null)
				// mImgViewUserpic.setImageBitmap(mCurrentUserInfo.pic);
				Log.i("zjj", "个人信息界面 头像URL: " + mCurrentUserInfo.headurl);
				
				if(mCurrentUserInfo.headurl.equals("")){
					mImgViewUserpic.setImageResource(R.drawable.login_default_user);
				}else{
					mImageLoader.DisplayImage(mCurrentUserInfo.headurl,
							mImgViewUserpic, false);
				}
			}
		}

		if (mSlipButtonRememberPwd.isChecked()) {
			mSlipButtonAutologin.setClickable(true);
		} else {
			mSlipButtonAutologin.setClickable(false);
			mSlipButtonAutologin.setCheck(false);
		}
	}

	private void initHandler() {
		uiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.arg1 == LOADING) {
					Log.i("zhr", "账号信息读取中...");
					showProgressBar();
				} else if (msg.arg1 == UPDATEFINISH) {
					hideProgressBar();
					ReflashUI();
					Log.i("zhr", "账号信息读取成功!");

				} else if (msg.arg1 == UPDATEFAILED) {
					Log.i("zhr", "账号信息读取失败!");
					hideProgressBar();
					String strError = (msg.obj != null) ? (String) msg.obj : "";
					new ErrorNotificatin(AccountManageActivity.this)
							.showErrorNotification(strError);

				}
			}
		};
	}

	// ------------------头像--------------------------
	/**
	 * 弹出对话框
	 */
	private void showListDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle("请选择省份");
		builder.setItems(mChoose, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0: // 拍照
					takephoto();
					break;
				case 1: // 选本地图片
					Intent intent = new Intent();
					/* 开启Pictures画面Type设定为image */
					intent.setType("image/*");
					/* 使用Intent.ACTION_GET_CONTENT这个Action */
					intent.setAction(Intent.ACTION_GET_CONTENT);
					/* 取得相片后返回本画面 */
					startActivityForResult(intent, RESULT_CHOOSE_IMAGE);
					break;
				default:
					break;
				}
			}
		});
		builder.show();
	}

	/**
	 * 拍照
	 */
	private void takephoto() {
		// try {
		// Intent imageCaptureIntent = new
		// Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// strImgPath = Environment.getExternalStorageDirectory().toString() +
		// IMGDIR;// 存放照片的文件夹
		// // strImgPath = "/DCIM" + IMGDIR;// 存放照片的文件夹
		// String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new
		// Date()) + ".jpg";// 照片命名
		// File out = new File(strImgPath);
		// if (!out.exists()) {
		// out.mkdirs();
		// }
		// out = new File(strImgPath, fileName);
		// strImgPath = strImgPath + fileName;// 该照片的绝对路径
		// Log.i("zjj", "点击拍照 strImgPath:" + strImgPath);
		// GlobalVariables.gPicPath = strImgPath;
		// mCapturedImageUri = Uri.fromFile(out);
		// imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
		// mCapturedImageUri);
		// startActivityForResult(imageCaptureIntent, RESULT_CAPTURE_IMAGE);
		// } catch (Exception e) {
		// System.out.println("AttachmentActivity.takePhoto()");
		// }

		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// if (Machine.isSDCardExist()) {
			File file = new File(ImageUntil.IMAGE_CACHE_PATH);
			if (!file.exists()) {
				file.mkdirs();
			}
			File picFile = new File(ImageUntil.CAMERA_SAVE_USER_PATH);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picFile));
			// }
			startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// boolean bCamera = false;

		Log.i("zjj", "requestCode:" + requestCode + ",resultCode:" + resultCode);

		if (resultCode == RESULT_OK) {
			// if (requestCode == RESULT_CHOOSE_IMAGE) { // 选图
			// Uri uri = data.getData();
			// Log.i("zjj","选择头像本地URI:" + uri.toString());
			//
			// if(uri.toString().indexOf("file:///")>-1){
			// strImgPath = uri.toString().replace("file:///", "");
			// }else{
			// String[] proj = { MediaStore.Images.Media.DATA };
			// Cursor actualimagecursor = managedQuery(uri, proj, null, null,
			// null);
			// if(actualimagecursor!=null){
			// int actual_image_column_index =
			// actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			// actualimagecursor.moveToFirst();
			// strImgPath =
			// actualimagecursor.getString(actual_image_column_index);
			//
			// try
			// {
			// // 4.0以上的版本会自动关闭 (3.0.1--11; 4.0--14; 4.0.3--15)
			// if (Integer.parseInt(Build.VERSION.SDK) < 11) {
			// actualimagecursor.close();
			// }
			// }catch(Exception e){}
			// }
			// }
			// } else if (requestCode == RESULT_CAPTURE_IMAGE) // 拍照
			// {
			// bCamera = true;
			// Log.i("zjj","选择头像 拍照完成 uri1:" + strImgPath);
			// }

			// 拍照
			if (requestCode == RESULT_CAPTURE_IMAGE) {
				ImageUntil.compressImage(ImageUntil.CAMERA_SAVE_USER_PATH,
						ImageUntil.COMPRESS_SAVE_USER_PATH);
			}
			// 相冊
			else if (requestCode == RESULT_CHOOSE_IMAGE) {
				String datastr = data.getData().toString();
				if (datastr.startsWith("content:")) {
					try {
						Uri originalUri = data.getData();// 得到图片的URI
						String[] imgs = { MediaStore.Images.Media.DATA };// 将图片URI转换成存储路径
						Cursor cursor = this.managedQuery(originalUri, imgs,
								null, null, null);
						int index = cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						String filePath = cursor.getString(index);

						try {
							// 4.0以上的版本会自动关闭 (3.0.1--11; 4.0--14; 4.0.3--15)
							if (Integer.parseInt(Build.VERSION.SDK) < 11) {
								cursor.close();
							}
						} catch (Exception e) {
						}
						//
						ImageUntil.compressImage(filePath,
								ImageUntil.COMPRESS_SAVE_USER_PATH);
					} catch (Exception e) {
					}
				} else if (datastr.startsWith("file:")) {
					String filePath = datastr.substring(datastr
							.indexOf("file://") + 7);
					ImageUntil.compressImage(filePath,
							ImageUntil.COMPRESS_SAVE_USER_PATH);
				}
			}

			attachmentdata = new Attachment();
			// 后缀名
			attachmentdata.fileType = getExtName(ImageUntil.COMPRESS_SAVE_USER_PATH);
			// if (strImgPath.length() <= 0)
			// strImgPath = GlobalVariables.gPicPath;

			// Log.i("zjj","选择头像 拍照完成 uri2:" + strImgPath);

			// 压缩数据
			// byte[] filedata = compressFile(strImgPath);
//			byte[] filedata = BitmapUtil
//					.imageToByteArray(ImageUntil.COMPRESS_SAVE_USER_PATH);
			byte[] filedata = MyMothod.compressFile(ImageUntil.COMPRESS_SAVE_USER_PATH);
			if (filedata != null) {
				// attachmentdata.mstrFildData = filedata.toString();
				attachmentdata.data = filedata;
			}
			filedata = null;
			Log.i("zjj", "取头像成功:" + attachmentdata.data.length);

			// Calc CRC 2
			String strCrc = "";
			strCrc = GlobalVariables.calcCrc(attachmentdata.data);
			Log.i("zjj", "取头像成功 strCrc:" + strCrc);

			attachmentdata.fileId = strCrc;
			attachmentdata.item = strCrc;
			attachmentdata.type = "file";
			mImgViewUserpic.setImageBitmap(MyMothod
					.Byte2Bitmap(attachmentdata.data,false));
			Log.i("zjj", "uploadPic");

			uploadPic(attachmentdata.data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 获取后缀名
	private String getExtName(String strPath) {
		int i = strPath.lastIndexOf(".");
		if (i <= 0)
			return "";
		else
			return strPath.substring(i + 1, strPath.length());
	}

	/**
	 * 提交头像
	 * 
	 * @param pic
	 */
	private void uploadPic(byte[] pic) {
		RequestManager.Submitaccountinfo(pic, new SubmitResultParser(),
				callback);
	}

	private RequestOperationReloginCallback callback = new RequestOperationReloginCallback() {
		@Override
		public void onError(String str) {
			Log.i("zjj", "提交头像 返回失败");
		}

		@Override
		public void onSuccess() {
//			// 发出广播,重新登录系统下载头像
//			Intent intent = new Intent(
//					ActivityActionDefine.CHANGEUSERPIC_ACTION);
//			sendBroadcast(intent);
//			Log.i("zjj", "提交头像 返回成功");
			setInstance.GetUpdateUserInfo(
					mCurrentUserInfo.lastupdate, uiHandler,mCurrentUserInfo);
		}
	};

}
