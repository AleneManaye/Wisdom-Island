package com.drcom.drpalm.objs;

import java.util.Date;

public class DiaryItem {
	public int diaryId = -1;
	public String diaryTitle = "";
	public String diarySum = "";
	public String diaryContent = "";
	public String diaryStatus = "";
	public Date lastUpdate = null;
	public String user = "";
	
	public DiaryItem(){
		lastUpdate = new Date();
	}
}
