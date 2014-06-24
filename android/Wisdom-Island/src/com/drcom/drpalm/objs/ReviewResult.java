package com.drcom.drpalm.objs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 2.3.3.1.11 提交班级回评
 * @author zhaojunjie
 *
 */
public class ReviewResult implements Parcelable {
	public String id = "";
	public String type = "count";
	public String value = "1";
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(id);		
		dest.writeString(type);	
		dest.writeString(value);	
	}
	
	
}
