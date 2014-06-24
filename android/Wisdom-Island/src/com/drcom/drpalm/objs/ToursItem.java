package com.drcom.drpalm.objs;

import android.os.Parcel;
import android.os.Parcelable;

public class ToursItem implements Parcelable{
	public String schoolid = "0";
	public String name = "none";	//资源包文件夹名字(注：资源包更新后，名字都不相同)
	public String url;
	public String verifycode;
	public String size;
	public String lastmdate = "0";
	
	public ToursItem(){

	}
	public ToursItem(String schoolid ,String name, String url, String verifycode,String size, String lastmdate){
		this.schoolid = schoolid;
		this.name = name;
		this.url = url;
		this.verifycode = verifycode;
		this.size = size;
		this.lastmdate = lastmdate;
		//this.date = date;
	}
	private void readFromParcel(Parcel in) {
		schoolid = in.readString();
		name = in.readString();
		url = in.readString();
		verifycode = in.readString();
		size = in.readString();
		lastmdate = in.readString();
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(schoolid);	
		dest.writeString(name);	
		dest.writeString(url);		
		dest.writeString(verifycode);
		dest.writeString(size);
		dest.writeString(lastmdate);
	}
	
	public static final Parcelable.Creator<ToursItem> CREATOR  
	= new Parcelable.Creator<ToursItem>(){
		@Override
		public ToursItem createFromParcel(Parcel source){
			ToursItem item = new ToursItem(); 
			item.readFromParcel(source);
			return item;
		}			
		public ToursItem[] newArray(int size){  
	        return new ToursItem[size];  
	    } 
	};

}

