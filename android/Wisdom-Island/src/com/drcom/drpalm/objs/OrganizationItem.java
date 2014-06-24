package com.drcom.drpalm.objs;

import android.os.Parcel;
import android.os.Parcelable;

public class OrganizationItem implements Parcelable {
	public int orgID = 0;//自身结点ID
	public String orgName = "";
	public String orgType = "";
	public String orgStatus = "";
	public String orgPath = "";
	public int orgPid = -1;//父结点的ID
	public int orgCount = -1;
	public int state = 0;//0为不选，1为选中，2为半选状态

	public OrganizationItem() {

	}

	public OrganizationItem(Integer orgID, String orgName, String orgType) {
		this.orgID = orgID;
		this.orgName = orgName;
		this.orgType = orgType;

	}

	private void readFromParcel(Parcel in) {
		orgID = in.readInt();
		orgName = in.readString();
		orgType = in.readString();
		orgStatus = in.readString();
		state = in.readInt();
		orgPid = in.readInt();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(orgID);
		dest.writeString(orgName);
		dest.writeString(orgType);
		dest.writeString(orgStatus);
		dest.writeInt(state);
		dest.writeInt(orgPid);

	}

	public static final Parcelable.Creator<OrganizationItem> CREATOR = new Parcelable.Creator<OrganizationItem>() {
		@Override
		public OrganizationItem createFromParcel(Parcel source) {
			OrganizationItem item = new OrganizationItem();
			item.readFromParcel(source);
			return item;
		}

		public OrganizationItem[] newArray(int size) {
			return new OrganizationItem[size];
		}
	};
}
