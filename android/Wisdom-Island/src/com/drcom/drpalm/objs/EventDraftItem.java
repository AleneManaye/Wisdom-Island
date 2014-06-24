package com.drcom.drpalm.objs;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;

public class EventDraftItem implements Parcelable {	
	public static String ORISTATUS_TYPE_N =  "N";	//Normal
	public static String ORISTATUS_TYPE_C =  "C";	//Cancel
	public static String ORISTATUS_TYPE_D =  "D";	//Del
	
	public Integer pk_id ;//作为一个唯一标识符号从数据库拿到数据，与发送到服务器无关
	public Integer old_eventid = -1;//更新通告时使用（指定更改的通告）
	public String oristatus = ORISTATUS_TYPE_N;//对更改前原事件状态改变值 保持有效为N，取消则为C
	public Integer type = 0;//事件类型
	public String ownerid = "";//受众对象ID
	public String owner = "";//受众对象名称
	public boolean bifeshow = false;   //是否加急
	public String shortloc = "_";//事件发生简址
	public String locUrl = "";//对用地址的地图（预留）
	public Date start;//事件起始时间
	public Date end;//事件截止时间
	public Date save;//保存时间
	public String title = "";//事件标题
	public String body = "";//事件内容
	public String user = "";//用户id
	public String comment = "";  //评语
	public String thumbname = "";//指定主图名称
	public boolean isAttc = false;	//是否有附件
	public String type_titleId ="";//通告标题index
//	public Integer objtype = null;//目标范围类型
	
	// Attachment
	public static class Attachment implements Parcelable {
		public byte[] data = null;
        public String fileId = "";
        public String fileType = "";
        public String item = "";	//附件ID(后台生成,转发通告时带上,则可转发附件)
        public String type = "";//file 为普通发送 Id为转发时通告id
        public Attachment(){}
		public String description="";
		public Attachment(byte[] data, String fileId ,String fileType,String description,String item, String type){
			this.data = data.clone();
			this.fileId = fileId;
			this.fileType = fileType;
			this.description = description;
			this.item = item;
			this.type = type;
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
			dest.writeString(fileId);		
			dest.writeString(fileType);	
			dest.writeString(description);	
			dest.writeString(item);	
			dest.writeString(type);	
			//dest.writeString(mstrFildData);
			dest.writeByteArray(data);
		}
		
		
		public static final Parcelable.Creator<Attachment> CREATOR  
		= new Parcelable.Creator<Attachment>(){
			@Override
			public Attachment createFromParcel(Parcel source){
				Attachment data = new Attachment(); 
				data.fileId = source.readString();
				data.fileType = source.readString();
				data.description = source.readString();
				data.item = source.readString();
				data.type = source.readString();
				//data.mstrFildData = source.readString();
				data.data = source.createByteArray();
				//source.readByteArray(data.filedata);
				return data;
			}			
			public Attachment[] newArray(int size){  
		        return new Attachment[size];  
		    } 
		}; 
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}
	
	
	static public byte[] getFile2Data(String filePath){
		try{
			if(null != filePath){	
				FileInputStream in = new FileInputStream(filePath); 
				ByteArrayOutputStream out = new ByteArrayOutputStream(); 
				byte buffer[] = new byte[1024];
				if (in != null){
					int length = 0;
					while ((length = in.read(buffer)) != -1){
						out.write(buffer, 0, length);
					}
				}
				out.close();
				in.close();
				return out.toByteArray();  
			}
		}catch(Exception e){
			
		}	
		return null;
	}
	
	public ArrayList<Attachment> eventDraftAttachment;
	//public List<OrganizationItem> receiverList = null;	
//	public Map<String,Attachment> mAttachmentMap = null;

	public EventDraftItem(){
		eventDraftAttachment = new ArrayList<Attachment>() ;
//		mAttachmentMap = new HashMap<String,Attachment>();
		//receiverList = new ArrayList<OrganizationItem>();
		
		Calendar cal= Calendar.getInstance();
		save = cal.getTime();
		start = cal.getTime();
		end = cal.getTime();
		end.setHours(start.getHours() + 1);
	}
	
		
//	public void clearFileList(){
//		if(null != mAttachmentMap){		
//			mAttachmentMap.clear();
//		}
//	}	
	
//	public boolean addAttachment(byte[] data, String fileId ,String fileType,String description){					
//		Attachment att = new Attachment(data,fileId,fileType,description);
//		if(mAttachmentMap.containsValue(fileId))
//			return false;
//		else{
//			//eventDraftAttachment.add(att) ;
//			mAttachmentMap.put(fileId, att);	
//			return true;
//		}		
//	}	
//	public boolean addAttachment(String filePath, String fileId,String description){
//		boolean bFlag = false;
//		File file = new File(filePath);
//		String fileType = file.getName();
//		fileType = fileType.substring(fileType.lastIndexOf('.') + 1);
//		byte[] buffer = getFile2Data(filePath);
//		//String str = null;
//		try{
//			if(null != mAttachmentMap){
//				if(mAttachmentMap.containsValue(filePath))
//					return false;
//				//str = new String(Base64.encode(buffer),"utf-8");
//				//str = new String(buffer,"utf-8");				
//				Attachment att = new Attachment(buffer, fileId, fileType,description);
//				mAttachmentMap.put(fileId, att);
//				bFlag = true;
//			}			
//		}catch(Exception e){
//			
//		}		
//		return bFlag;
//	}	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
//		CopyAttclist2Map();
		
		dest.writeInt(pk_id);
		dest.writeString(ownerid);
		dest.writeString(owner);
		dest.writeInt(old_eventid);
		dest.writeInt(type);
//		dest.writeInt(objtype);
		dest.writeString(oristatus);
		dest.writeValue(save.getTime());
		dest.writeValue(start.getTime());
		dest.writeValue(end.getTime());		
		dest.writeString(shortloc);
		dest.writeString(title);
		dest.writeString(body);
		dest.writeString(user);
		dest.writeString(locUrl);
		dest.writeString(thumbname);
		writeBool(dest,bifeshow);
//		dest.writeMap(mAttachmentMap);
//		dest.writeList(eventDraftAttachment);	//附件太长,不能在这处理
	}	
	
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		pk_id = in.readInt();
		ownerid = in.readString();
		owner = in.readString();
		old_eventid = in.readInt();
		type = in.readInt();
//		objtype = in.readInt();
		oristatus = in.readString();
		save = DateFormatter.getDateFromMilliSeconds(in.readLong());// (Date)in.readSerializable();
		start = DateFormatter.getDateFromMilliSeconds(in.readLong()); //(Date)in.readSerializable();
		end =  DateFormatter.getDateFromMilliSeconds(in.readLong()); //(Date)in.readSerializable();
		shortloc = in.readString();
		title = in.readString();
		body = in.readString();
		user = in.readString();
		locUrl = in.readString();
		thumbname = in.readString();
		bifeshow = readBool(in);
//		mAttachmentMap = in.readHashMap(getClass().getClassLoader());
//		eventDraftAttachment = in.readArrayList(getClass().getClassLoader());
//		Map2CopyAttclist();
	}
	
	public static final Parcelable.Creator<EventDraftItem> CREATOR  
	= new Parcelable.Creator<EventDraftItem>(){
		@Override
		public EventDraftItem createFromParcel(Parcel source){
			EventDraftItem item = new EventDraftItem(); 
			item.readFromParcel(source);
			return item;
		}			
		public EventDraftItem[] newArray(int size){  
	        return new EventDraftItem[size];  
	    } 
	};
	
	private static void writeBool(Parcel dest, boolean bool) {
		dest.writeInt(bool ? 1 : 0);
	}
	
	private static boolean readBool(Parcel in) {
		return (in.readInt() == 1);
	}
	
//	/**
//	 * 将附件列表转成MAP
//	 */
//	private void CopyAttclist2Map(){
//		mAttachmentMap.clear();
//		
//		for(int i = 0;i<eventDraftAttachment.size();i++){
//			mAttachmentMap.put(eventDraftAttachment.get(i).fileId, eventDraftAttachment.get(i));
//		}
//		
//	}
//	
//	private void Map2CopyAttclist(){
//		eventDraftAttachment = (ArrayList<Attachment>) mAttachmentMap.values();
//		int aaa= 0;
//	}
}
