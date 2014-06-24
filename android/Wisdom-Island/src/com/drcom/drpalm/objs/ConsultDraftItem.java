package com.drcom.drpalm.objs;

import android.os.Parcel;
import android.os.Parcelable;


public class ConsultDraftItem implements Parcelable {
	
	public String username;		    //用户名
	public String email;			//邮件
	public String phone;            //电话号码
	public String title;            //标题
	public String content;          //内容
	public String type;             //类型 意见反馈：feedback，入托咨询：consult


	public ConsultDraftItem(){
		
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(username);
		dest.writeString(email);
		dest.writeString(phone);
		dest.writeString(title);
		dest.writeString(content);
		dest.writeString(type);
	}
	private void readFromParcel(Parcel in) {
		username = in.readString();
		email =in.readString();
		phone = in.readString();
		title = in.readString();
		content = in.readString();
		type = in.readString();
	}
	public static final Parcelable.Creator<ConsultDraftItem> CREATOR
	= new Parcelable.Creator<ConsultDraftItem>(){
		@Override
		public ConsultDraftItem createFromParcel(Parcel source){
			ConsultDraftItem item = new ConsultDraftItem();
			item.readFromParcel(source);
			return item;
		}
		public ConsultDraftItem[] newArray(int size){
	        return new ConsultDraftItem[size];
	    }
	};
}

