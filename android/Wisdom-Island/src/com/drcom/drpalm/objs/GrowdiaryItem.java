package com.drcom.drpalm.objs;

import java.io.Serializable;

public class GrowdiaryItem implements Serializable{
		public String diaryid = "";
		public String title = "";
		public String contect = "";
		public String status = "";
		
		public GrowdiaryItem(String diaryid,String title,String contect,String status){
			this.diaryid = diaryid ;	
			this.title = title;
			this.contect = contect;
			this.status = status;
		}
		public GrowdiaryItem(){};
}
