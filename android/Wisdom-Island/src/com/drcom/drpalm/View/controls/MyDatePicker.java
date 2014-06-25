package com.drcom.drpalm.View.controls;

import java.util.Calendar;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.drcom.drpalm.View.controls.wheel.NumericWheelAdapter;
import com.drcom.drpalm.View.controls.wheel.OnWheelChangedListener;
import com.drcom.drpalm.View.controls.wheel.OnWheelScrollListener;
import com.drcom.drpalm.View.controls.wheel.WheelView;
import com.wisdom.island.R;

/**
 * 日期控件
 * 字体大小可在WheelView.class中设置,TEXT_SIZE_DP
 * @author zhaojunjie
 *
 */
public class MyDatePicker extends LinearLayout{
	private WheelView year;
	private WheelView month;
	private WheelView day;
	private WheelView hours;
	private WheelView mins;
	
	private NumericWheelAdapter m31daysAdapter;
	private NumericWheelAdapter m30daysAdapter;
	private NumericWheelAdapter m29daysAdapter;
	private NumericWheelAdapter m28daysAdapter;
	
	private LinearLayout mLinearlayout_time;
	private Time mTime = new Time();
	
	public MyDatePicker(Context context) {
		super(context);
		inintView(context);
	}
	public MyDatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		inintView(context);
	}

	private void inintView(Context context){
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        inflater.inflate(R.layout.mydatepicker, this); 
        
		//声明
        year = (WheelView) findViewById(R.id.year);
        month = (WheelView) findViewById(R.id.month);
        day = (WheelView) findViewById(R.id.day);
        hours = (WheelView) findViewById(R.id.hours);
        mins = (WheelView) findViewById(R.id.mins);
        mLinearlayout_time = (LinearLayout)findViewById(R.id.time_layout);
        
        // 取当前时间
		Calendar c = Calendar.getInstance();
		int curYear = c.get(Calendar.YEAR);
		int curMonth = c.get(Calendar.MONTH);	//得到的是月分的Index,现在是8月, 返回的是7
		int curDay = c.get(Calendar.DATE);// .DAY_OF_MONTH
		int curHours = c.get(Calendar.HOUR_OF_DAY);
		int curMinutes = c.get(Calendar.MINUTE);
		
		//适配器
		m31daysAdapter = new NumericWheelAdapter(1, 31);
		m30daysAdapter = new NumericWheelAdapter(1, 30);
		m29daysAdapter = new NumericWheelAdapter(1, 29);
		m28daysAdapter = new NumericWheelAdapter(1, 28);
        
		//设置范围
		year.setAdapter(new NumericWheelAdapter(curYear - 4, curYear + 4));
//		year.setLabel("年");
		
		month.setAdapter(new NumericWheelAdapter(1, 12));
//		month.setLabel("月");
		month.setCyclic(true);
		
		day.setAdapter(new NumericWheelAdapter(1, 31));
//		day.setLabel("日");
		day.setCyclic(true);
		
        hours.setAdapter(new NumericWheelAdapter(0, 23));
//		hours.setLabel("时");
        
		mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
//		mins.setLabel("分");
		mins.setCyclic(true);
		
		//设置当前时间
		System.out.println("curDaycurDaycurDaycurDaycurDaycurDaycurDaycurDay:" + curMonth+ "-" + curDay);
		year.setCurrentItem(4);
		month.setCurrentItem(curMonth);
		day.setCurrentItem(curDay - 1);
		hours.setCurrentItem(curHours);
		mins.setCurrentItem(curMinutes);
		
		//设置事件
		month.addScrollingListener(scrollListener_Month);
		year.addScrollingListener(scrollListener_Month);
		
		year.addChangingListener(mChangedListener);
		month.addChangingListener(mChangedListener);
		day.addChangingListener(mChangedListener);
		hours.addChangingListener(mChangedListener);
		mins.addChangingListener(mChangedListener);
	}
	
	/**
	 * 时间部分是否可视
	 * @param b
	 */
	public void setTimeVisibility(int visibility){
		mLinearlayout_time.setVisibility(visibility);
	}
	
	//事件监听听
	public void addYearChangeListener(OnWheelChangedListener l){
		year.addChangingListener(l);
	}
	
	public void addMonthChangeListener(OnWheelChangedListener l){
		month.addChangingListener(l);
	}
	
	public void addDayChangeListener(OnWheelChangedListener l){
		day.addChangingListener(l);
	}
	
	public void addHourChangeListener(OnWheelChangedListener l){
		hours.addChangingListener(l);
	}
	
	public void addMinsChangeListener(OnWheelChangedListener l){
		mins.addChangingListener(l);
	}
	
	public void setChangeListener(OnWheelChangedListener l){
		addYearChangeListener(l);
		addMonthChangeListener(l);
		addDayChangeListener(l);
		addHourChangeListener(l);
		addMinsChangeListener(l);
	}
	
	//取值
	public String getYear(){
		return year.getTextItem(year.getCurrentItem());
	}
	public String getMonth(){
		return month.getTextItem(month.getCurrentItem());
	}
	public String getDay(){
		return day.getTextItem(day.getCurrentItem());
	}
	public String getHours(){
		return hours.getTextItem(hours.getCurrentItem());
	}
	public String getMins(){
		return mins.getTextItem(mins.getCurrentItem());
	}
	public Time getTime(){
		mTime.year = Integer.valueOf(getYear());
		mTime.month = month.getCurrentItem();
		mTime.monthDay = Integer.valueOf(getDay());
		mTime.hour = hours.getCurrentItem();
		mTime.minute = mins.getCurrentItem();
		return mTime;
	}
	
	//转到某项
	public void setYearTo(String str){
		year.Scroll2TheSameItem(str);
	}
	public void setMonthTo(String str){
		month.Scroll2TheSameItem(str);
	}
	public void setDayTo(String str){
		day.Scroll2TheSameItem(str);
	}
	public void setHoursTo(String str){
		hours.Scroll2TheSameItem(str);
	}
	public void setMinsTo(String str){
		mins.Scroll2TheSameItem(str);
	}
	public void setDate(Time time){
		setYearTo(String.valueOf(time.year));
		month.setCurrentItem(time.month);
		day.setCurrentItem(time.monthDay - 1);
		hours.setCurrentItem(time.hour);
		mins.setCurrentItem(time.minute);
	}
	
	/**
	 * 更新时间
	 */
	OnWheelChangedListener mChangedListener = new OnWheelChangedListener() {
		
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			mTime.year = Integer.valueOf(getYear());
			mTime.month = month.getCurrentItem();
			mTime.monthDay = Integer.valueOf(getDay());
			mTime.hour = hours.getCurrentItem();
			mTime.minute = mins.getCurrentItem();
		}
	};
	
	/**
	 * 计算每月要几天
	 */
	OnWheelScrollListener scrollListener_Month = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
			
		}
		public void onScrollingFinished(WheelView wheel) {
			switch (month.getCurrentItem()) {
			case 0:
			case 2:
			case 4:
			case 6:
			case 7:
			case 9:
			case 11:
			case 13:
				day.setAdapter(m31daysAdapter);
				break;
			case 3:
			case 5:
			case 8:
			case 10:
			case 12:
				day.setAdapter(m30daysAdapter);
				break;
			case 1:	//润年：年（四位数）能被四整除就是，但能被100整除的又不是，而能被400整除的又是
				
				System.out.println("year.getTextItem:" + year.getTextItem(year.getCurrentItem()));
				
				int yearnum = Integer.valueOf(year.getTextItem(year.getCurrentItem()));
				
				if(yearnum % 4 == 0){
					if(yearnum % 100 == 0){
						day.setAdapter(m28daysAdapter);
					}else{
						day.setAdapter(m29daysAdapter);
					}
				}else{
					if(yearnum % 400 == 0){
						day.setAdapter(m29daysAdapter);
					}
					day.setAdapter(m28daysAdapter);
				}
				break;
			default:
				break;
			}
		}
	};
}

