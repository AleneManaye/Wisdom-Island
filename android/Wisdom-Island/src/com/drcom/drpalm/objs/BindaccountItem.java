package com.drcom.drpalm.objs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 账号关联
 * @author hzy
 *
 */
public class BindaccountItem implements Parcelable {
	
	public String pub_account = ""; 		//账号名
	public String pub_name = "";			//账号人名字
	public String pub_schoolName = "";		//账号人学校
	public String pub_schoolName_hans="";	//账号人学校简体
	public String pub_schoolName_hant=""; 	//账号人学校繁体
	public String pub_schoolName_en="";		//账号人学校英文
	public String pub_schoolId = "";		//账号人学校id
	public String pub_imgaeurl = "";		//账号头像url add by hzy 14-05-07
	public String pub_image_number = "";	//账号头像数字     add by hzy 14-05-07
	public String schoolkey = "";
	public String tokenid = "";				//设备id
	public String pub_password = "";	 	//密码
	public String gwip ="";					//网关IP
	public String gwport ="";				//网关端口
	public String headimglastupdate =""; 	//头像最后更新时间


	public BindaccountItem() {
		super();
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	//update 04/6/9
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(pub_account);		
		dest.writeString(pub_name);	
		dest.writeString(pub_schoolName);
		dest.writeString(pub_schoolId);
		dest.writeString(pub_imgaeurl);
		dest.writeString(pub_image_number);
		dest.writeString(schoolkey);
		dest.writeString(tokenid);
		dest.writeString(gwip);
		dest.writeString(gwport);
		dest.writeString(headimglastupdate);
	}

}
