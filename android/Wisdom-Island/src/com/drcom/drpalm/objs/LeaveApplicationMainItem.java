package com.drcom.drpalm.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LeaveApplicationMainItem implements Parcelable {

    //告假ID
    public int leaveid;
    //告假类型
    public String type;
    //发布人ID
    public String pubid;
    //发布人名称
    public String pubname;
    //接收人ID
    public String ownerid;
    //接收人名称
    public String owner;
    //发布时间
    public Date post = new Date();
    //告假起始时间
    public Date start = new Date();
    //告假截止时间
    public Date end = new Date();
    //告假标题
    public String title;
    //告假内容
    public String content = "";
    //最后更新时间
    public Date lastupdate = new Date();
    //是否已读
    public int isread;
    //是否有附件
    public int hasatt;
    //用户名称
    public String user;

    //详细附件集合
    public List<EventDetailsItem.Imags> attr;

    //上传附件集合
    public List<EventDraftItem.Attachment> attachments;

    public LeaveApplicationMainItem(){
        attr = new ArrayList<EventDetailsItem.Imags>();
        attachments = new ArrayList<EventDraftItem.Attachment>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.getLeaveid());
        dest.writeString(this.getType());
        dest.writeString(this.getPubid());
        dest.writeString(this.getPubname());
        dest.writeString(this.getOwnerid());
        dest.writeString(this.getOwner());
        dest.writeSerializable(this.getPost());
        dest.writeSerializable(this.getStart());
        dest.writeSerializable(this.getEnd());
        dest.writeString(this.getTitle());
        dest.writeString(this.getContent());
        dest.writeSerializable(this.getLastupdate());
        dest.writeInt(this.getIsread());
        dest.writeInt(this.getHasatt());
        dest.writeString(this.getUser());
        dest.writeList(this.getAttr());

    }

    private static final Parcelable.Creator<LeaveApplicationMainItem> CREATOR
            = new Parcelable.Creator<LeaveApplicationMainItem>() {
        @Override
        public LeaveApplicationMainItem createFromParcel(Parcel source) {
            LeaveApplicationMainItem leaveApplicationMainItem = new LeaveApplicationMainItem();

            leaveApplicationMainItem.setLeaveid(source.readInt());
            leaveApplicationMainItem.setType(source.readString());
            leaveApplicationMainItem.setPubid(source.readString());
            leaveApplicationMainItem.setPubname(source.readString());
            leaveApplicationMainItem.setOwnerid(source.readString());
            leaveApplicationMainItem.setOwner(source.readString());
            leaveApplicationMainItem.setPost((Date) source.readSerializable());
            leaveApplicationMainItem.setStart((Date) source.readSerializable());
            leaveApplicationMainItem.setEnd((Date) source.readSerializable());
            leaveApplicationMainItem.setTitle(source.readString());
            leaveApplicationMainItem.setContent(source.readString());
            leaveApplicationMainItem.setIsread(source.readInt());
            leaveApplicationMainItem.setHasatt(source.readInt());
            leaveApplicationMainItem.setUser(source.readString());

            List<EventDetailsItem.Imags> attrList = new ArrayList<EventDetailsItem.Imags>();
            source.readList(attrList,List.class.getClassLoader());
            leaveApplicationMainItem.setAttr(attrList);

            return leaveApplicationMainItem;
        }

        @Override
        public LeaveApplicationMainItem[] newArray(int size) {
            return new LeaveApplicationMainItem[0];
        }
    };

    public int getLeaveid() {
        return leaveid;
    }

    public void setLeaveid(int leaveid) {
        this.leaveid = leaveid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPubid() {
        return pubid;
    }

    public void setPubid(String pubid) {
        this.pubid = pubid;
    }

    public String getPubname() {
        return pubname;
    }

    public void setPubname(String pubname) {
        this.pubname = pubname;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getPost() {
        return post;
    }

    public void setPost(Date post) {
        this.post = post;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(Date lastupdate) {
        this.lastupdate = lastupdate;
    }

    public int getIsread() {
        return isread;
    }

    public void setIsread(int isread) {
        this.isread = isread;
    }

    public int getHasatt() {
        return hasatt;
    }

    public void setHasatt(int hasatt) {
        this.hasatt = hasatt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setAttr(List<EventDetailsItem.Imags> attr) {
        this.attr = attr;

    }

    public List<EventDetailsItem.Imags> getAttr() {
        return attr;
    }

    public List<EventDraftItem.Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<EventDraftItem.Attachment> attachments) {
        this.attachments = attachments;
    }
}
