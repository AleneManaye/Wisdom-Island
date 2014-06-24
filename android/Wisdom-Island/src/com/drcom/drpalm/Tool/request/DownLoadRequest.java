package com.drcom.drpalm.Tool.request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.request.DownLoadCallback.DownloadingStruct;

public class DownLoadRequest {	
	public boolean mCanContinue = true;
	public DownLoadRequest(){};
	public void stopDownLoad(){
		mCanContinue = false;
	}
	public boolean DownLoadTours(final String urlString, final DownLoadCallback callback){
		final int CONNECTION_ERROR = 0;
		final int CONNECTION_RESPONSE = 1;
		final int CONNECTION_ACCEPTING = 2;	
		final int CANCEL_DOWNLOAD = 3;	
		
		ConnectivityManager connectivityManager = (ConnectivityManager) GlobalVariables.gAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo == null || !networkInfo.isAvailable()) {
			callback.onError("手机连上网络了吗?");
			return false;
		}
		
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.arg1 == CONNECTION_ERROR) {
					callback.onError((String)msg.obj);
				} 
				else if(msg.arg1 == CONNECTION_ACCEPTING){					
					callback.onDownloading((DownloadingStruct)msg.obj);
				}
				else if(msg.arg1 == CONNECTION_RESPONSE) {					
					callback.onFinished((File)msg.obj);
				}
				else if(msg.arg1 == CANCEL_DOWNLOAD){
					
				}
			}
		};		
		Thread thread = new Thread() {
			@Override
			public void run() {
				Message message = Message.obtain();
				FileOutputStream out = null;
				File file = null;
				boolean finished = false;
				try {					
					URL url = new URL(urlString);				
					HttpURLConnection conn=(HttpURLConnection)url.openConnection();
					InputStream input= conn.getInputStream(); 
					
					String urlPath = url.getPath();
					String fileName = urlPath.substring(urlPath.lastIndexOf('/')+1);
					
					File dir = new File(GlobalVariables.getResourceTempDirectory());
					if(!dir.exists()){
						dir.mkdir();
					}
					file = File.createTempFile("tours_", fileName, dir);
					out = new FileOutputStream(file);

					DownloadingStruct downloadingObj = new DownloadingStruct();	
					downloadingObj.totalByte = conn.getContentLength();					
					
					byte[] buffer = new byte[4*1024];
					int byteRead = 0;
                    while((byteRead=input.read(buffer))!=-1 && mCanContinue){  
                    	out.write(buffer,0,byteRead);  
                    	downloadingObj.readByte += byteRead;
                    	Message msg = Message.obtain();
                    	msg.arg1 = CONNECTION_ACCEPTING;
                    	msg.obj = downloadingObj;
                    	handler.sendMessage(msg);
                    } 
					if(mCanContinue){						
						message.arg1 = CONNECTION_RESPONSE;
						message.obj = (File)file;
						finished = true;
					}
					else{
						message.arg1 = CANCEL_DOWNLOAD;						
						finished = false;
					}					
				} catch (MalformedURLException e) {						
					e.printStackTrace();					
					finished = false;
				} catch (IOException e) {
					e.printStackTrace();					
					finished = false;
				}	
				finally{
					if(null != out){
						try {
							out.flush();
							out.close();							
						} catch (IOException e1) {			
						}						
					}					
					if(!finished){
						message.arg1 = CONNECTION_ERROR;
						message.obj = "手机连上网络了吗?";
						if(null != file){
							if(file.exists())
								file.delete();
						}
					}
				}
				handler.sendMessage(message);
			}				
		};
		thread.start();
		return true;
		
	}
}
