package com.drcom.drpalm.Tool.drHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.drcom.drpalm.View.controls.myinterface.UICallBack;


/**
 * 请求数据接口
 * @author zsy@cndatacom.com
 */
public class DrRequestImgsTask extends AsyncTask {
	
	private String tag = "RequestDataTask-->";
	private Context context;
	private String requestURL;
	private boolean isok=false;
	private boolean iSTips = true; //false,不提示;true提示
	private ProgressDialog progressDlg; 
	private UICallBack uicallback;
	private byte[] attachmentBytes;
	
	public DrRequestImgsTask(Context context,String url,boolean iSTips,UICallBack uicallback){
		this.context = context;
		this.uicallback = uicallback;
		this.iSTips = iSTips;
		this.requestURL =url;
	}

	public byte[] getData() {
		if (requestURL == null || "".equals(requestURL)) {
			System.out.println("RequestTask error:requestJson == null || requestURL == null");
			return null;
		}
		System.out.println("==url==" + this.requestURL);
		int connCount = 0;
		while (!isok && connCount < 5) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(this.requestURL); 
				HttpResponse response;
				try {
					response = httpclient.execute(httpget);
					

					
					HttpEntity entity = response.getEntity();
					System.out.println("Content-Type>>>>" + entity.getContentType() ) ;
					if (entity != null && response.getStatusLine().getStatusCode() == 200) {
						try{
							attachmentBytes = EntityUtils.toByteArray(entity);
						}
						catch (OutOfMemoryError e) {
							// TODO: handle exception
						}
					}
				}
				catch (Exception e) {
				}
				isok = true;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("RequestTask Exception:" + e.toString());
			} 
			connCount++;
		}
		
		
		if (!isok || "".equals(attachmentBytes) || attachmentBytes == null) {
			return null;
		}
		return attachmentBytes;// MyMethods.decodeBASE64(result.toString());
	}

	@Override
	protected Object doInBackground(Object... params) {
	   	return getData();
	}
 
	

   protected void onPostExecute(Object result) { 
	   	if(iSTips){
	   		progressDlg.dismiss();
	   	}
	   	uicallback.callBack((Object)result);
   } 


	protected void onPreExecute() { 
	   super.onPreExecute();
	   if(iSTips){
		  progressDlg = new ProgressDialog(context); 
		  progressDlg.setMessage("请稍等..."); 
		  progressDlg.setCancelable(false); 
		  progressDlg.show(); 
	   }
	}


}
