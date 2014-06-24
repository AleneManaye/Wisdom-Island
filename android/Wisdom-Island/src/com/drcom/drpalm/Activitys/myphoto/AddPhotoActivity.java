package com.drcom.drpalm.Activitys.myphoto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.events.NewEventActivity;
import com.drcom.drpalm.Tool.FileManagement;
import com.drcom.drpalm.Tool.jsonparser.SubmitResultParser;
import com.drcom.drpalm.Tool.request.RequestManager;
import com.drcom.drpalm.Tool.request.RequestOperationReloginCallback;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.objs.EventDraftItem.Attachment;
import com.drcom.drpalm.objs.MyPhoto;
import com.drcom.drpalm4tianzhujiao.R;

public class AddPhotoActivity extends ModuleActivity{
	public static String KEY_ISEDIT = "KEY_ISEDIT";
	public static String KEY_PHOTO = "KEY_PHOTO";
	
	public static final int RESULT_CAPTURE_IMAGE = 1;
	public static final int RESULT_CHOOSE_IMAGE = 2;
	private static final String IMGDIR = "/DrpalmPic/";
	
	//
	private ImageView mImageViewPhoto;
	private EditText mEditTextDes;
	private Button buttonSend;
	
	//
	private boolean isEdit = false;
	private String[] mChoose;
	private String strImgPath = "";
	private Uri mCapturedImageUri;
	private MyPhoto mMyPhoto = new MyPhoto();
	private ImageLoader mImageLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.addphoto, mLayout_body);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			if (extras.containsKey(KEY_ISEDIT)) {
				isEdit = extras.getBoolean(KEY_ISEDIT);
			}
			if (extras.containsKey(KEY_PHOTO)) {
				mMyPhoto = extras.getParcelable(KEY_PHOTO);
			}
		}
		
		mChoose = getResources().getStringArray(R.array.choosephoto);
		mImageLoader = getmSchoolImageLoader();
		
		mImageViewPhoto = (ImageView)findViewById(R.id.imgview_photo);
		mEditTextDes = (EditText)findViewById(R.id.editText_des);
		
		initTitlebar();
		hideToolbar();
		
		initUI();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMyPhoto = null;
		super.onDestroy();
	}
	
	private void initTitlebar(){
		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
		
		// 发送
		buttonSend = new Button(this);
		buttonSend.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_title_green_selector));
		buttonSend.setText(getString(R.string.send));
		buttonSend.setTextAppearance(AddPhotoActivity.this, R.style.TitleBtnText);
		buttonSend.setLayoutParams(p);
		buttonSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isEdit){
					mMyPhoto.des = mEditTextDes.getText().toString();
					mMyPhoto.data = null;
					mMyPhoto.status = MyPhoto.EDIT;
				}else{
					mMyPhoto.des = mEditTextDes.getText().toString();
					mMyPhoto.status = MyPhoto.NEW;
				}
				
				sendPic();
			}
		});
		setTitleRightButton(buttonSend);
		setTitleText(getString(R.string.selfalbum));
	}
	
	private void initUI(){
		
		if(isEdit){
//			if(mMyPhoto.data != null){
			Log.i("zjj", "编辑器相册  url" + mMyPhoto.url);
				mImageViewPhoto.setImageBitmap(mImageLoader.getBitmapFromCache(mMyPhoto.url));
//			}
			mEditTextDes.setText(mMyPhoto.des);
			buttonSend.setBackgroundResource(R.drawable.btn_title_blue_selector);
			buttonSend.setText(getString(R.string.saveimagefile));
		}else{
			mImageViewPhoto.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					showListDialog(); // 选择相片对话框
				}
			}, 500);
		}
	}
	
	//------------------选择图片--------------------------
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
		try {
			Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			strImgPath = Environment.getExternalStorageDirectory().toString() + IMGDIR;// 存放照片的文件夹
			String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";// 照片命名
			File out = new File(strImgPath);
			if (!out.exists()) {
				out.mkdirs();
			}
			out = new File(strImgPath, fileName);
			strImgPath = strImgPath + fileName;// 该照片的绝对路径
			GlobalVariables.gPicPath = strImgPath;
			mCapturedImageUri = Uri.fromFile(out);
			imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageUri);
			startActivityForResult(imageCaptureIntent, RESULT_CAPTURE_IMAGE);
		} catch (Exception e) {
			System.out.println("AttachmentActivity.takePhoto()");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean bCamera = false;

		// System.out.println("requestCode:" + requestCode + ",resultCode:" +
		// resultCode);

		// 不能多过 SUM(15) 张图片
//		if (GlobalVariables.mListAttachmentData.size() >= SUM) {
//			return;
//		}

		if (resultCode == RESULT_OK) {
			if (requestCode == RESULT_CHOOSE_IMAGE) { // 选图
				Uri uri = data.getData();
				Log.e("uri", uri.toString());
				
				if(uri.toString().indexOf("file:///")>-1){
					strImgPath = uri.toString().replace("file:///", "");
				}else{
					String[] proj = { MediaStore.Images.Media.DATA };
					Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
					if(actualimagecursor!=null){
						int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						actualimagecursor.moveToFirst();
						strImgPath = actualimagecursor.getString(actual_image_column_index);
						
						try
			            {
							//4.0以上的版本会自动关闭 (3.0--11; 4.0--14;; 4.0.3--15)
			                if(Integer.parseInt(Build.VERSION.SDK) < 11)
			                {
			                	actualimagecursor.close();
			                }
			            }catch(Exception e){}
					}
				}
			} else if (resultCode == RESULT_OK && requestCode == RESULT_CAPTURE_IMAGE) // 拍照
			{
				bCamera = true;
			}

			// 后缀名
			mMyPhoto.fileType = FileManagement.getExtName(strImgPath);
			if (strImgPath.length() <= 0)
				strImgPath = GlobalVariables.gPicPath;

			// 压缩数据
			byte[] filedata = MyMothod.compressFile(strImgPath);
			if (filedata != null) {
				// attachmentdata.mstrFildData = filedata.toString();
				mMyPhoto.data = filedata;
			}
//			// Calc CRC
//			File file = new File(strImgPath);
//			String strCrc = "";
//			if (file.exists()) {
//				try {
//
//					FileInputStream in = new FileInputStream(file);// 指定目标文件
//					// int length = (int)file.length();
//					byte[] bytes = new byte[(int) file.length()];
//					// Read file contents to a byte array.
//					// long begin = System.currentTimeMillis();
//					in.read(bytes);
//					strCrc = GlobalVariables.calcCrc(bytes);
//					
//					in.close();
//					in = null;
//					bytes = null;
//				} catch (Exception e) {
//					System.out.println("Calc CRC Exception" + e.toString());
//				}
//			}
			String strCrc = "";
			strCrc = GlobalVariables.calcCrc(mMyPhoto.data);
			
			Log.i("zjj", "拍照成功:" + mMyPhoto.data.length);

			mMyPhoto.fileId = strCrc;
			mMyPhoto.filename = strCrc + "." + mMyPhoto.fileType;
//			mMyPhoto.type = "file";
			if (bCamera) {
				File file = new File(strImgPath);
				file.delete();
			}
				
			mImageViewPhoto.setImageBitmap(MyMothod.Byte2Bitmap(mMyPhoto.data,false));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 提交图片
	 * @param pic
	 */
	private void sendPic(){
		if(!isEdit && mMyPhoto.data == null){	//没相片时
			Toast.makeText(AddPhotoActivity.this, getString(R.string.pic_cant_be_null), Toast.LENGTH_LONG).show();
		}else{
			ShowLoadingDialog();
			ArrayList<MyPhoto> myphotolist = new ArrayList<MyPhoto>();
			myphotolist.add(mMyPhoto);
			
			RequestManager.SubmitUseralbum(myphotolist, new SubmitResultParser(), callback);
		}
	}
	
	private RequestOperationReloginCallback callback = new RequestOperationReloginCallback(){
		
		@Override
		public void onError(String str) {
			Log.i("zjj", "新增个人相册 返回失败");
			HideLoadingDialog();
		}
		@Override
		public void onSuccess() {		
			Log.i("zjj", "新增个人相册  返回成功");
			HideLoadingDialog();
			finishDraw();
		}								
	};
}
