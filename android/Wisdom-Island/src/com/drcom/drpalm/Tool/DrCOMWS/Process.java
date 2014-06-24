/*
 * File         : Process.java
 * Date         : 2011-06-17
 * Author       : Keqin Su
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : Android ProcessBar
 */

package com.drcom.drpalm.Tool.DrCOMWS;


import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.drcom.drpalm4tianzhujiao.R;

public class Process extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.drcomws_process);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
