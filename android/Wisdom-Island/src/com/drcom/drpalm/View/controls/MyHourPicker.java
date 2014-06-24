package com.drcom.drpalm.View.controls;

import java.util.Calendar;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.drcom.drpalm.View.controls.wheel.NumericWheelAdapter;
import com.drcom.drpalm.View.controls.wheel.OnWheelChangedListener;
import com.drcom.drpalm.View.controls.wheel.WheelView;
import com.drcom.drpalm4tianzhujiao.R;

public class MyHourPicker extends LinearLayout {
	private WheelView hours;
	private WheelView mins;

	private LinearLayout mLinearlayout_time;
	private Time mTime = new Time();

	public MyHourPicker(Context context) {
		super(context);
		inintView(context);
	}

	public MyHourPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		inintView(context);
	}

	private void inintView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.setting_myhourpicker, this);

		// 声明
		hours = (WheelView) findViewById(R.id.hours);
		mins = (WheelView) findViewById(R.id.mins);
		mLinearlayout_time = (LinearLayout) findViewById(R.id.time_layout);

		// 取当前时间
		Calendar c = Calendar.getInstance();
		int curHours = c.get(Calendar.HOUR_OF_DAY);
		int curMinutes = c.get(Calendar.MINUTE);

		// 设置范围

		hours.setAdapter(new NumericWheelAdapter(0, 24));
		hours.setLabel("点开始");
		hours.setCyclic(true);

		mins.setAdapter(new NumericWheelAdapter(0, 24));
		mins.setLabel("点结束");
		mins.setCyclic(true);

		// 设置当前时间
		hours.setCurrentItem(curHours);
		mins.setCurrentItem(curMinutes);

		// 设置事件
		hours.addChangingListener(mChangedListener);
		mins.addChangingListener(mChangedListener);
	}

	/**
	 * 时间部分是否可视
	 * 
	 * @param b
	 */
	public void setTimeVisibility(int visibility) {
		mLinearlayout_time.setVisibility(visibility);
	}

	// 事件监听听

	public void addHourChangeListener(OnWheelChangedListener l) {
		hours.addChangingListener(l);
	}

	public void addMinsChangeListener(OnWheelChangedListener l) {
		mins.addChangingListener(l);
	}

	public void setChangeListener(OnWheelChangedListener l) {
		addHourChangeListener(l);
		addMinsChangeListener(l);
	}

	// 取值
	public String getStartTime() {
		return hours.getTextItem(hours.getCurrentItem());
	}

	public String getEndTime() {
		return mins.getTextItem(mins.getCurrentItem());
	}

	public String getTime() {
		String time = getStartTime() + ":00 - " + getEndTime() + ":00";
		return time;
	}

	public void setStartHours(int start) {
		hours.setCurrentItem(start);
	}

	public void setEndHours(int end) {
		mins.setCurrentItem(end);
	}

	// 转到某项
	public void setHoursTo(String str) {
		hours.Scroll2TheSameItem(str);
	}

	public void setMinsTo(String str) {
		mins.Scroll2TheSameItem(str);
	}

	public void setDate(Time time) {
		hours.setCurrentItem(time.hour);
		mins.setCurrentItem(time.minute);
	}

	/**
	 * 更新时间
	 */
	OnWheelChangedListener mChangedListener = new OnWheelChangedListener() {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			mTime.hour = hours.getCurrentItem();
			mTime.minute = mins.getCurrentItem();
		}
	};

}
