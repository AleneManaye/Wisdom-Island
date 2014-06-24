package com.drcom.drpalm.Tool.drHttpClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class DrHttpClient {
	static final String MULTIPART_FORM_DATA = "multipart/form-data";
	static final String TEXTHTML_FORM_DATA = "text/html";
	static final String BOUNDARY = "---------7d4a6d158c9"; 
	byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();
	private HttpURLConnection mConn = null;
	//private InputStream mInput = null;
	//private OutputStream mOutput = null;
	private URL mUrl = null;
	private boolean mIsKeepAlive = false;
	public void setUrl(String url){		
		try {
			mUrl = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			mUrl = null;
			e.printStackTrace();
		}
	} 
	private void setKeepAlive(boolean isKeepAlive){
		mIsKeepAlive = isKeepAlive;
	}
	public void closeConnection(){
		if(null != mConn)
			mConn.disconnect();
	}
	public boolean normalPost(DrHttpBody body,final DrHttpClientCallback callback){
		if(null == mUrl)
			return false;		
		if(!initHttpHeader(false))
			return false;
		//mIsKeepAlive = false;	
		post(body,false,callback);
		return true;
	}
	public boolean keepAlivePost(DrHttpBody body,final DrHttpClientKeepAliveCallback callback){
		if(null == mUrl)
			return false;		
		if(!initHttpHeader(true))
			return false;		
		//mIsKeepAlive = true;		
		post(body,true,callback);
		return true;
	}
	private void post(final DrHttpBody body,final boolean isKeepAlive,final DrHttpClientCallback callback){
		
			Thread thread = new Thread(){
				@Override
				public void run(){
					try {						
						OutputStream output = mConn.getOutputStream();
						DataOutputStream outStream = new DataOutputStream(output);
						mConn.connect();
						outStream.write(body.getData());				
						//outStream.write(end_data);
						outStream.flush();
						int ret = mConn.getResponseCode();
						if(ret == HttpStatus.SC_OK){
							InputStream mInput = mConn.getInputStream();
							//if(isKeepAlive){
							String retValue = convertStreamToString(mInput,isKeepAlive, callback);
							Log.d("DrHttpClient Post:", retValue);
							callback.onFinished(retValue);							
							//}
						}
						else{
							callback.onError("DrHttpClient Connection Error:" + ret);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();	
						callback.onError(e.toString());
					}
				}
			};
			thread.start();
	}
	private boolean initHttpHeader(boolean isKeepAlive){
		try {
			mConn = (HttpURLConnection)mUrl.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		mConn.setConnectTimeout(10* 1000);
		mConn.setDoInput(true);//允许输入
		mConn.setDoOutput(true);//允许输出
		mConn.setUseCaches(false);//不使用Cache
		mConn.setRequestProperty("Charset", HTTP.UTF_8);
		//if(isKeepAlive)
		mConn.setRequestProperty(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
		//else
			//mConn.setRequestProperty(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
				
		try {
			mConn.setRequestMethod("POST");
			//mConn.setRequestProperty(HTTP.CONTENT_TYPE, MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
			mConn.setRequestProperty(HTTP.CONTENT_TYPE, TEXTHTML_FORM_DATA);
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
		return true;
		
	}
	private static void memset(char[] buf, int value, int size){
		int capacity = buf.length;
		for(int i = 0; i< size; i++){
			buf[i] = (char)value;
		}
	}
	private static String convertStreamToString(InputStream stream, boolean isKeepAlive, final DrHttpClientCallback callback) {  
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder stringBuilder = new StringBuilder(); 
        String line = null;
        int readCount = 0;
        char[] buffer = new  char[1024];       
        try {       
        	memset(buffer, '\0', 1024);
        	while( -1 != (readCount = reader.read(buffer, 0, 1024)) ){
        		if(isKeepAlive){
        			DrHttpClientKeepAliveCallback keepAlive = (DrHttpClientKeepAliveCallback)callback;
        			keepAlive.onRecvString(new String(buffer,0,readCount));
        		}
        		else{
        			stringBuilder.append(new String(buffer, 0, readCount) + "\n");
        		}        		
        	}
        
//        	while ((line = reader.readLine()) != null) {
//        		if(isKeepAlive){
//        			DrHttpClientKeepAliveCallback keepAlive = (DrHttpClientKeepAliveCallback)callback;
//        			String ret = line + "\r\n";
//        			keepAlive.onRecvString(ret);
//        		}
//        		else{
//        			stringBuilder.append(line + "\r\n");
//        		}
//        	}                    	
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }        
        return stringBuilder.toString();
    }
}
