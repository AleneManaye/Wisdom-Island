package com.drcom.drpalm.Tool;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;

public class ConnectionWrapper {
	public static enum ErrorType {
		Network,
		Server,
		Timeout
	};
	
	private static final String HTTP_USER_AGENT = 
		"DrPalm " + GlobalVariables.getResVersion() + " for Android";
	
	private static final int CONNECTION_ERROR = 0;
	private static final int CONNECTION_RESPONSE = 1;
	
	static final String TAG = "ConnectionWrapper";
	
	public static interface ConnectionInterface {
		void onResponse(InputStream stream);
		
		void onError(ErrorType error);
	}
	
	private ConnectivityManager mConnectivityManager;
	
	public ConnectionWrapper(Context context) {
		mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	/*
	 * This will create a connection wrapper that does not attempt to check
	 * if it has a network conenction before making the network request
	 */
	public ConnectionWrapper() {
		mConnectivityManager = null;
	}
	
	//是否Wifi
	public boolean isWifiOnline()
	{
		if(mConnectivityManager != null) {
			NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
			if(networkInfo != null)
			{
				if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
					return true;
			}
		}
		return false;
	}
	
	/*
	 * @return if attempted to begin network connection
	 */
	//public boolean postUrl(final String url, final List<BasicNameValuePair> params, final ConnectionInterface callback){
	public boolean postUrl(final String url,final MultipartEntity mutiEntity, final ConnectionInterface callback){
		if(mConnectivityManager != null) {
			NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
			if(networkInfo == null || !networkInfo.isAvailable()) {
				// network not available call error handler
				callback.onError(ErrorType.Network);
				return false;
			}
		}
		if(Looper.myLooper() != null) {
			post(url, mutiEntity, new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if(msg.arg1 == CONNECTION_ERROR) {
						callback.onError((ErrorType) msg.obj);
					} else if(msg.arg1 == CONNECTION_RESPONSE) {
						callback.onResponse((InputStream) msg.obj);
					}
				}
			});
		} else {
			post(url, mutiEntity, callback);
		}
		return true;
	}
	public boolean openURL(final String url, final ConnectionInterface callback) {
		Log.d(TAG, "starting request: " + url);
		
		if(mConnectivityManager != null) {
			NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
			if(networkInfo == null || !networkInfo.isAvailable()) {
				// network not available call error handler
				callback.onError(ErrorType.Network);
				return false;
			}
		}
		
		// we want to insure any response/error is posted back
		// on the same thread as openURL was initiated
		
		// if there is no Looper, no way to do the connection asynchronously
		// also, probably no need to do asynchronous connection 
		// (since the thread probably does not block anything else)
		if(Looper.myLooper() != null) {
			final HandlerThread threadHandler = new HandlerThread("MessageThread");
			if(!threadHandler.isAlive()){
				threadHandler.start();
			}
			asynchronous(url, new Handler(threadHandler.getLooper()) {
				@Override
				public void handleMessage(Message msg) {
					if(msg.arg1 == CONNECTION_ERROR) {
						callback.onError((ErrorType) msg.obj);
					} else if(msg.arg1 == CONNECTION_RESPONSE) {
						callback.onResponse((InputStream) msg.obj);
					}
				}
			});
		} else {
			synchronous(url, callback);
		}
		
		return true;
	}
	
	private static void asynchronous(final String url, final Handler threadHandler) {
		new Thread() {
			@Override
			public void run() {
				Message message = Message.obtain();
				
				// HttpParams httpParams = new BasicHttpParams();
				// HttpConnectionParams.setConnectionTimeout(httpParams, 500); 
				// HttpConnectionParams.setSoTimeout(httpParams, 60 * 1000); 
				HttpClient httpClient = new DefaultHttpClient();
				httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				//httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpGet httpGet = new HttpGet(url);
				httpGet.setHeader("User-Agent", HTTP_USER_AGENT);
				httpGet.setHeader("Accept", "text/html,application/xml,application/xhtml");
				httpGet.setHeader("Accept-Encoding", "gzip");				
				httpGet.setHeader("Accept-Language", "zh-CN");
				//httpGet.setHeader("Accept-Language", "en-US");
				httpGet.setHeader(HTTP.CHARSET_PARAM ,HTTP.UTF_8);
				try {
					HttpResponse response = httpClient.execute(httpGet);
					if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						InputStream stream = response.getEntity().getContent();	
						Log.d("asynchronous", "getContentLength:" +response.getEntity().getContentLength());
						Log.d("asynchronous", "getContentEncoding:" +response.getEntity().getContentEncoding());
						Log.d("asynchronous", "getContentType:" +response.getEntity().getContentType());
						message.arg1 = CONNECTION_RESPONSE;
						message.obj = stream;						
					} else {
						message.arg1 = CONNECTION_ERROR;
						message.obj = ErrorType.Server;
					}
				} catch (IOException e) {
					message.arg1 = CONNECTION_ERROR;
					message.obj = ErrorType.Network;
				} catch (IllegalArgumentException e){
					message.arg1 = CONNECTION_ERROR;
					message.obj = ErrorType.Network;
				}
				
				threadHandler.sendMessage(message);
			}
		}.start();
	}
	
	private static void synchronous(final String url, final ConnectionInterface callback) {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		//httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("User-Agent", HTTP_USER_AGENT);
		httpGet.setHeader("Accept-Charset",HTTP.UTF_8);
		httpGet.setHeader("Accept", "text/html,application/xml,application/xhtml");
		httpGet.setHeader("Accept-Encoding", "gzip");				
		httpGet.setHeader("Accept-Language", "zh-CN");
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream stream = response.getEntity().getContent();
				callback.onResponse(stream);						
			} else {
				callback.onError(ErrorType.Server);
			}
		} catch (IOException e) {
			callback.onError(ErrorType.Network);
		}
	}
	
	private static void post(final String url, final MultipartEntity mutiEntity, final Handler threadHandler) {
		new Thread() {
			@Override
			public void run() {
				Message message = Message.obtain();
				
				// HttpParams httpParams = new BasicHttpParams();
				// HttpConnectionParams.setConnectionTimeout(httpParams, 500); 
				// HttpConnectionParams.setSoTimeout(httpParams, 60 * 1000); 
				HttpClient httpClient = new DefaultHttpClient();
				httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
				//httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
				HttpPost httpPost = new HttpPost(url);
				httpPost.setHeader("User-Agent", HTTP_USER_AGENT);	
				httpPost.setHeader("Accept-Charset",HTTP.UTF_8);
				httpPost.setHeader("Accept", "text/html,application/xml,application/xhtml");
				httpPost.setHeader("Accept-Encoding", "gzip");				
				httpPost.setHeader("Accept-Language", "zh-CN");
				try {					
					//httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));	
					
					httpPost.setEntity(mutiEntity);
					HttpResponse response = httpClient.execute(httpPost);
					if(response.getStatusLine().getStatusCode() == 200) {
						InputStream stream = response.getEntity().getContent();
						message.arg1 = CONNECTION_RESPONSE;
						message.obj = stream;						
					} else {
						message.arg1 = CONNECTION_ERROR;
						message.obj = ErrorType.Server;
					}
				} catch (IOException e) {
					message.arg1 = CONNECTION_ERROR;
					message.obj = ErrorType.Network;
				} catch (IllegalArgumentException e){
					message.arg1 = CONNECTION_ERROR;
					message.obj = ErrorType.Network;
				} 				
				threadHandler.sendMessage(message);
			}
		}.start();
	}
	
	//private static void post(final String url, final List<BasicNameValuePair> params, final ConnectionInterface callback) {
	private static void post(final String url, final MultipartEntity mutiEntity, final ConnectionInterface callback) {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		//httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("User-Agent", HTTP_USER_AGENT);
		httpPost.setHeader("Accept-Charset",HTTP.UTF_8);
		httpPost.setHeader("Accept", "text/html,application/xml,application/xhtml");
		httpPost.setHeader("Accept-Encoding", "gzip");				
		httpPost.setHeader("Accept-Language", "zh-CN");
		try {			
			httpPost.setEntity(mutiEntity);
			HttpResponse response = httpClient.execute(httpPost);
			if(response.getStatusLine().getStatusCode() == 200) {
				InputStream stream = response.getEntity().getContent();
				callback.onResponse(stream);						
			} else {
				callback.onError(ErrorType.Server);
			}
		} catch (IOException e) {
			callback.onError(ErrorType.Network);
		}
	}
}
