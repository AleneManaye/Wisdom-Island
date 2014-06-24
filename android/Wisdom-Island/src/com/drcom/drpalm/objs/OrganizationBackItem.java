package com.drcom.drpalm.objs;

import android.os.Parcel;
import android.os.Parcelable;

public class OrganizationBackItem implements Parcelable {
	public String url; 
	public String checksum;	
	public long lastupdate;
	
	public OrganizationBackItem(){}
	
	public OrganizationBackItem(String url, String checksum, long lastuodate){
		this.url = url;
		this.checksum = checksum;
		this.lastupdate = lastuodate;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);	
		dest.writeString(checksum);	
		dest.writeLong(lastupdate);
	}
	public void readFromParcel(Parcel in) {
		url = in.readString();
		checksum = in.readString();	
		lastupdate = in.readLong();
	}
	
//	public static final Parcelable.Creator<LoginSucItem> CREATOR  
//	= new Parcelable.Creator<LoginSucItem>(){
//		@Override
//		public LoginSucItem createFromParcel(Parcel source){
//			LoginSucItem item = new LoginSucItem(); 
//			item.readFromParcel(source);
//			return item;
//		}			
//		public LoginSucItem[] newArray(int size){  
//	        return new LoginSucItem[size];  
//	    } 
//	}; 
}
