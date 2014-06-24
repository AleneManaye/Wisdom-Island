package com.drcom.drpalm.Activitys.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.objs.Block;
import com.drcom.drpalm.objs.UpdateTimeItem;
import com.drcom.drpalm4tianzhujiao.R;

public class TheNewsAdapter extends BaseAdapter {
	public static int TYPE_SCHOOL = 0;
	public static int TYPE_CLASS = 1;
	private int language=0;//0=简体，1=繁体，2=英文
	
	public int mType = TYPE_SCHOOL;
	
	private Context context;
	private LayoutInflater mLayoutInflater;
	private List<UpdateTimeItem> data;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
	private Date mDateToday = new Date();
	
	public TheNewsAdapter(Context context, List<UpdateTimeItem> data) {
		super();
		this.context = context;
		this.mLayoutInflater = LayoutInflater.from(context);
		this.data = data;
		if (LanguageManagement.getSysLanguage(context) == CurrentLan.COMPLES_CHINESE) {

			language = 1;

		} else if (LanguageManagement.getSysLanguage(context) == CurrentLan.ENGLISH) {

			language = 2;

		} else {
			language = 0;

		}
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	class ViewHolder {
		ImageView icon;
		TextView sum;
		TextView moduleName;
		TextView title;
		TextView time;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		UpdateTimeItem item = data.get(position);
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.thenews_item, null);
			convertView.setTag(vh);
			
			vh.icon = (ImageView)convertView.findViewById(R.id.thenewsRowIV);
			vh.sum = (TextView)convertView.findViewById(R.id.thenews_number_txtv);
			vh.moduleName = (TextView)convertView.findViewById(R.id.thenewsModuleName_Txtview);
			vh.title = (TextView)convertView.findViewById(R.id.thenewsTitle_Txtview);
			vh.time = (TextView)convertView.findViewById(R.id.thenewsDate_Txtview);
		}else {
			vh = (ViewHolder) convertView.getTag();
		}
		
		vh.title.setText(item.update_title);
		
		if(item.update_time_last.getTime() == 0){
			vh.time.setText("");
		}else{
			if(item.update_time_last.getYear() == mDateToday.getYear() &&
					item.update_time_last.getMonth() == mDateToday.getMonth() &&
					item.update_time_last.getDay() == mDateToday.getDay())
			{
				vh.time.setText(sdftime.format(item.update_time_last));
			}else{
				vh.time.setText(sdf.format(item.update_time_last));
			}
		}
		
		
		if(item.update_unreadcount.equals("") || item.update_unreadcount.equals("0")){
			vh.sum.setVisibility(View.GONE);
		}else{
			vh.sum.setText(item.update_unreadcount);
			vh.sum.setVisibility(View.VISIBLE);
		}
		
		if(mType == TYPE_SCHOOL){
			vh.icon.setImageBitmap(MyMothod.getIcon(this.context, getSchoolIconName(item.update_time_channel)));
			vh.moduleName.setText(getSchoolModuleName(item.update_time_channel));
		}else{
			vh.icon.setImageBitmap(MyMothod.getIcon(this.context, getClassIconName(item.update_time_channel)));
			vh.moduleName.setText(getClassModuleName(item.update_time_channel));
		}
		
		return convertView;
	}
	
	/**
	 * 取资源图片名(校园)
	 * @param channel
	 * @return
	 */
	private String getSchoolIconName(int channel){
		String iconname = "";
		for (Block iterable_element : GlobalVariables.blocks) {
			if ("school".equals(iterable_element.getType()) && iterable_element.getId() == channel){
				iconname = iterable_element.getIconString();
				break;
			}
		}
		return iconname;
	}
	
	/**
	 * 取资源图片名(班级)
	 * @param channel
	 * @return
	 */
	private String getClassIconName(int channel){
		String iconname = "";
		for (Block iterable_element : GlobalVariables.blocks) {
			if ("class".equals(iterable_element.getType())&& iterable_element.getId() == channel){
				iconname = iterable_element.getIconString();
				break;
			}
		}
		return iconname;
	}
	
	/**
	 * 取模块名称(校园)
	 * @param channel
	 * @return
	 */
	private String getSchoolModuleName(int channel){
		String modulename = "";
		for (Block iterable_element : GlobalVariables.blocks) {
			if ("school".equals(iterable_element.getType())&& iterable_element.getId() == channel){
				if(language==0){
					modulename  = iterable_element.getNameChsString();
				}else if(language == 1){
					modulename  = iterable_element.getNameChtString();
				}else{
					modulename  = iterable_element.getNameEnString();
				}
			}
		}
		return modulename;
	}
	
	/**
	 * 取模块名称(班级)
	 * @param channel
	 * @return
	 */
	private String getClassModuleName(int channel){
		String modulename = "";
		for (Block iterable_element : GlobalVariables.blocks) {
			if ("class".equals(iterable_element.getType())&& iterable_element.getId() == channel){
				if(language==0){
					modulename  = iterable_element.getNameChsString();
				}else if(language == 1){
					modulename  = iterable_element.getNameChtString();
				}else{
					modulename  = iterable_element.getNameEnString();
				}
				break;
			}
		}
		return modulename;
	}
}
