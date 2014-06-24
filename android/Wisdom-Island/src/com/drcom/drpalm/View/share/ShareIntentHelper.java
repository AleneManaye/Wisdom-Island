package com.drcom.drpalm.View.share;

//import com.tencent.mm.sdk.openapi.IWXAPI;
//import com.tencent.mm.sdk.openapi.SendMessageToWX;
//import com.tencent.mm.sdk.openapi.WXAPIFactory;
//
//import com.tencent.mm.sdk.openapi.WXMediaMessage;
//import com.tencent.mm.sdk.openapi.WXTextObject;

import android.R.bool;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Region;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

//分享按钮intent帮助类
public class ShareIntentHelper {
	private Intent mIntent;
	private Context mContext;
	private String subject, url;
	private String errorMessage;
	private static final String WECHAT = "wechat";
	private static final String SINAWEIBO = "sina";
	private static final String EMAIL = "email";
	private static final String SMS = "sms";
	private boolean errorFlag = false;
	public static final String WX_APP_ID = "wxa6c059cf37bba989";
//	private IWXAPI api;

	public ShareIntentHelper(Context context, String subject, String url,
			String errorMessage) {
		mContext = context;
		this.subject = subject;
		this.url = url;
		this.errorMessage = errorMessage;
//		api = WXAPIFactory.createWXAPI(mContext, WX_APP_ID,true);
//		api.registerApp(WX_APP_ID);

	}

	// 这个方法用于判断传进来的type需要调用的是哪个intent
	public void startIntent(String type) {
		// 在这里进行Intent的判断
		Intent intent = new Intent(Intent.ACTION_SEND);

		// 微信
		/**if (type.equals(WECHAT)) {
			if (api.openWXApp()) {
				// 初始化一个WXTextObject对象
				WXTextObject textObj = new WXTextObject();
				textObj.text = url;

				// 用WXTextObject对象初始化一个WXMediaMessage对象
				WXMediaMessage msg = new WXMediaMessage();
				msg.mediaObject = textObj;
				// 发送文本类型的消息时，title字段不起作用
				msg.title = "EBaby";
				msg.description = "EBaby";

				// 构造一个Req
				SendMessageToWX.Req req = new SendMessageToWX.Req();
//				req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
				req.transaction = String.valueOf(System.currentTimeMillis());
				req.scene = SendMessageToWX.Req.WXSceneSession;
				req.message = msg;
				// 调用api接口发送数据到微信
				boolean apiFlag = api.sendReq(req);
				Log.i("zhr", apiFlag+"");
				} else {
				errorFlag = true;
			}

		}**/

		// 微博
		if (type.equals(SINAWEIBO)) {
			intent.putExtra(Intent.EXTRA_TEXT, url);
			// 发送带图片微博
			// intent.setType("image/*");
			// intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new
			// File("/sdcard/aaa.jpg")));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			Context context = null;

			try {
				context = mContext.createPackageContext("com.sina.weibo",
						Context.CONTEXT_IGNORE_SECURITY);
				intent.setClassName(context, "com.sina.weibo.EditActivity");

				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				errorFlag = true;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				errorFlag = true;
			}
		}

		// E-Mail
		if (type.equals(EMAIL)) {
			// intent.setType("message/rfc882");
			// //设置类型
			//
			// //调用系统发送邮件
			// intent.putExtra(Intent.EXTRA_EMAIL,"");
			// intent.putExtra(Intent.EXTRA_SUBJECT,subject);
			// intent.putExtra(Intent.EXTRA_TEXT, url);
			// Intent.createChooser(intent, "Choose Email Client");

		}

		// 短信
		if (type.equals(SMS)) {
			try {

				Uri smsToUri = Uri.parse("smsto:");
				intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
				// intent.setType("text/plain");
				// intent.putExtra(Intent.EXTRA_SUBJECT, subject);
				intent.putExtra("sms_body", url);
				mContext.startActivity(intent);
			} catch (Exception e) {
				errorFlag = true;
			}
		}

		if (isWorkable(type) && !errorFlag) {
			mIntent = intent;
		} else {
			errorFlag = false;
			Toast.makeText(mContext, errorMessage, 100).show();
		}
	}

	// 判断在帮助类中是否有这个tpye的方法
	public boolean isWorkable(String type) {
		if (type.equals(SINAWEIBO)) {
			return true;
		}
		if (type.equals(WECHAT)) {
			return true;//微信入口已注释
		}
		if (type.equals(EMAIL)) {
			return true;
		}
		if (type.equals(SMS)) {
			return true;
		}
		return false;
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

}
