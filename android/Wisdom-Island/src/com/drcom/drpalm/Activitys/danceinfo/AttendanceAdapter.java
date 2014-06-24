package com.drcom.drpalm.Activitys.danceinfo;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.objs.AttendanceinfoItem;
import com.drcom.drpalm4tianzhujiao.R;

public class AttendanceAdapter extends BaseAdapter {
	private List<AttendanceinfoItem> itemList ;
	private LayoutInflater listContainer;  
	private AttendanceinfoItem mItem;
	private Context mContext;
//	private List<AttendanceinfoItem.Records> infoItem = new ArrayList<AttendanceinfoItem.Records>();//考勤信息
	
	// 这个是互教通的adapter
	public AttendanceAdapter(Context context, List<AttendanceinfoItem> itemList) {
		this.listContainer = LayoutInflater.from(context); 
		this.itemList = itemList;
		mContext=context;
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public AttendanceinfoItem getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder = null;
		if (convertView == null) {
			convertView =  listContainer.inflate(R.layout.attendance_item, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
			
			holder.date = (TextView)convertView.findViewById(R.id.date);
			holder.festival = (TextView)convertView.findViewById(R.id.festival);
			holder.info = (LinearLayout)convertView.findViewById(R.id.info);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
    	mItem = this.getItem(position);
//    	infoItem.clear();
//    	infoItem = mItem.mRecordslist;
//		Time t = new Time("GMT+8");
//		t.set(Long.valueOf(mItem.attendancetime+"000"));
//		StringBuffer sb = new StringBuffer();
//		sb.append((t.month + 1));
//		sb.append(mContext.getString(R.string.month));
//		sb.append(t.monthDay);
//		sb.append(mContext.getString(R.string.day));
//		sb.append("　");
//		sb.append(DateFormatter.getWeekStr(t.weekDay));
//		
//		holder.date.setText(sb.toString());
//		SetInfoItemView(holder.info, mItem.mRecordslist);//初始化考勤信息
//		
//		t = null;
//		sb = null;
		
    	SetInfoDate(holder.date,mItem.attendancetime);		//考勤日期
		SetInfoItemView(holder.info, mItem.mRecordslist);	//初始化考勤信息
		
		return convertView;
   
	}

    class ViewHolder{
    	LinearLayout info;
    	TextView date;
    	TextView festival;
    }
    
    private void SetInfoDate(TextView txtview, String attendancetime){
    	Date d = new Date(Long.valueOf(attendancetime+"000"));
    	txtview.setText(DateFormatter.getStringYYYYMMDD(d));
    	
    	//?月?日
//    	Time t = new Time();	//不需要参数("GMT+08:00")); 会少一天
//		t.set(Long.valueOf(attendancetime)*1000);
//		StringBuffer sb = new StringBuffer();
//		sb.append((t.month + 1));
//		sb.append(mContext.getString(R.string.month));
//		sb.append(t.monthDay);
//		sb.append(mContext.getString(R.string.day));
//		sb.append("　");
//		sb.append(DateFormatter.getWeekStr(t.weekDay));
//    	
//    	txtview.setText(sb.toString());
//    	
//    	t = null;
//		sb = null;
    	
    	//
//    	StringBuffer sb = new StringBuffer();
//    	
//    	Date d = new Date(Long.valueOf(attendancetime+"000"));
//    	Time t = new Time("GMT+8");
//    	t.set(Long.valueOf(attendancetime+"000"));
//    	
//    	if(LanguageManagement.getSysLanguage(mContext) == CurrentLan.ENGLISH){
//    		sb.append(DateFormatter.getStringMMDD(d));
//    	}else{
//    		sb.append(DateFormatter.getStringMMDDChina(d));
//    	}
//    	sb.append("　");
//		sb.append(DateFormatter.getWeekStr(t.weekDay));
//		
//    	txtview.setText(sb.toString());
//    	
//    	t = null;
//		sb = null;
    }
    
    private void SetInfoItemView(LinearLayout infoView,List<AttendanceinfoItem.Records> infoItem){
    	infoView.removeAllViews();
    	
    	String time_in_str = "--:--";
    	String des_in_str = "";
    	String time_out_str = "--:--";
    	String des_out_str = "";
    	
    	//入园时间
    	View child_in = LayoutInflater.from(mContext).inflate(R.layout.attendance_info_item, null);
		TextView title_in = (TextView)child_in.findViewById(R.id.title);
		TextView time_in = (TextView)child_in.findViewById(R.id.time);
		TextView detail_in = (TextView)child_in.findViewById(R.id.detail);
		
		infoView.addView(child_in);
		
		//异常时间
		int sum_unusual = 0;
    	for(int i=0;i<infoItem.size();i++){
    		if(infoItem.get(i).status.equals(AttendanceinfoItem.KEY_IN)){
    			Date d = new Date(Long.valueOf(infoItem.get(i).time+"000"));
    			time_in_str = DateFormatter.getStringHHmm(d);
    			des_in_str = infoItem.get(i).des;
    		}else if(infoItem.get(i).status.equals(AttendanceinfoItem.KEY_UNUSUAL)){
	    		View child = LayoutInflater.from(mContext).inflate(R.layout.attendance_info_item, null);
	    		TextView title = (TextView)child.findViewById(R.id.title);
	    		TextView time = (TextView)child.findViewById(R.id.time);
	    		TextView detail = (TextView)child.findViewById(R.id.detail);
	    		
	    		if(sum_unusual==0)
	    			title.setText(mContext.getString(R.string.attendance_unusual));
	    		else
	    			title.setText("");
	    		Date d = new Date(Long.valueOf(infoItem.get(i).time+"000"));
	    		time.setText(DateFormatter.getStringHHmm(d));
	    		detail.setText(infoItem.get(i).des);
	    		infoView.addView(child);
	    		
	    		sum_unusual++;
    		}else if(infoItem.get(i).status.equals(AttendanceinfoItem.KEY_OUT)){
    			Date d = new Date(Long.valueOf(infoItem.get(i).time+"000"));
    			time_out_str = DateFormatter.getStringHHmm(d);
    			des_out_str = infoItem.get(i).des;
    		}
    	}
    	
    	//离园时间
    	View child_out = LayoutInflater.from(mContext).inflate(R.layout.attendance_info_item, null);
		TextView title_out = (TextView)child_out.findViewById(R.id.title);
		TextView time_out = (TextView)child_out.findViewById(R.id.time);
		TextView detail_out = (TextView)child_out.findViewById(R.id.detail);
		
		infoView.addView(child_out);
    	
    	//填入时间
		title_in.setText(mContext.getString(R.string.attendance_admission));
		time_in.setText(time_in_str);
		detail_in.setText(des_in_str);
		
		title_out.setText(mContext.getString(R.string.attendance_leave));
		time_out.setText(time_out_str);
		detail_out.setText(des_out_str);
    }

 }