package com.drcom.drpalm.Tool.CommonTranslate;

import android.content.res.Resources;
import android.util.TypedValue;

import com.drcom.drpalm.GlobalVariables;

public class TypeValueTranslate {
	/**
	 * 获取当前分辨率下指定单位对应的像素大小（根据设备信息）
	 * px,dip,sp -> px
	 * 
	 * Paint.setTextSize()单位为px
	 * 
	 * 代码摘自：TextView.setTextSize()
	 * 
	 * @param unit  TypedValue.COMPLEX_UNIT_*
	 * @param size
	 * @return
	 */
	public static float getRawSize(int unit, float size) {	       
		Resources r = GlobalVariables.gAppResource; 
		return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
	}
}
