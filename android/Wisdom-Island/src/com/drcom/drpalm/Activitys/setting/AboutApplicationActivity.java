package com.drcom.drpalm.Activitys.setting;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm4tianzhujiao.R;

/*      
 * Date         : 2012-6-26
 * Author       : zeng han hua
 * Copyright    : City Hotspot Co., Ltd.
 */
public class AboutApplicationActivity extends ModuleActivity {

	private TextView versionTV;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.about_application, mLayout_body);
		versionTV = (TextView) super.findViewById(R.id.version_no);
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if (GlobalVariables.gAppContext.getString(R.string.IsTestVersion).contentEquals("true"))
//			versionTV.setText(packInfo.versionName + "(测试)");
//		else
			versionTV.setText(packInfo.versionName);

		hideToolbar();
		setTitleText(getString(R.string.about));
	}
}
