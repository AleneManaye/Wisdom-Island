package com.drcom.drpalm.objs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 个人相册
 * @author Administrator
 *
 */
public class MyPhoto implements Parcelable {
	public static String NEW = "N";
	public static String DEL = "C";
	public static String EDIT = "M";
	
	public String username = "";
	public String imgid = "";
	public String filename = "";
	public String des="";
	public String status = "";	
	public String url = "";
	public String lastupdatetime = "";
	public String fileId = "";		//提交图片封装数据时要
	public String fileType = "";	//提交图片封装数据时要
	public byte[] data = null;	
	
	public MyPhoto(){}
	
	
	public MyPhoto(byte[] data, String imgid ,String filename,String des,String status, String fileId ,String fileType, String url){
		this.data = data.clone();
		this.imgid = imgid;
		this.filename = filename;
		this.des = des;
		this.status = status;
		this.fileId = fileId;
		this.fileType = fileType;
		this.url = url;
	}
	
	 public static Bitmap Bytes2Bimap(byte[] b){  
	 if(b == null)
		 return null;
        if(b.length!=0){  
        	try
        	{
        		return BitmapFactory.decodeByteArray(b, 0, b.length);   
        	}
        	catch(OutOfMemoryError e)
        	{
        		e.printStackTrace();
        	}
        }   
        return null;   
  }  
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(imgid);		
		dest.writeString(filename);	
		dest.writeString(des);	
		dest.writeString(status);
		dest.writeString(fileId);		
		dest.writeString(fileType);	
		dest.writeString(url);	
		dest.writeByteArray(data);
	}
	
	
	public static final Parcelable.Creator<MyPhoto> CREATOR  
	= new Parcelable.Creator<MyPhoto>(){
		@Override
		public MyPhoto createFromParcel(Parcel source){
			MyPhoto data = new MyPhoto(); 
			data.imgid = source.readString();
			data.filename = source.readString();
			data.des = source.readString();
			data.status = source.readString();
			data.fileId = source.readString();
			data.fileType = source.readString();
			data.url = source.readString();
			data.data = source.createByteArray();
			return data;
		}			
		public MyPhoto[] newArray(int size){  
	        return new MyPhoto[size];  
	    } 
	}; 
	
	public String getDes() {
		return des;
	}
	public void setDes(String description) {
		this.des = description;
	}
}
