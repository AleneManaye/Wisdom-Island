package com.drcom.drpalm.Activitys.events.review;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm4tianzhujiao.R;

public class ReviewTestActivity extends ModuleActivity{
	private Spinner mSpinner1,mSpinner2;
	
	private ArrayAdapter<String> arrayAdapter1 = null;  
	private ArrayAdapter<String> arrayAdapter2 = null;  
	
	private String[] mStr1 = new String[0];
	private String[] mStr2 = new String[0];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.reviewtest_view, mLayout_body);
		
		mSpinner1 = (Spinner)findViewById(R.id.sp1);
		mSpinner2 = (Spinner)findViewById(R.id.sp2);
		
		mStr1 = getResources().getStringArray(R.array.yesno);
		mStr2 = getResources().getStringArray(R.array.numberofpeople);
		
		arrayAdapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mStr1);
		arrayAdapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mStr2);
		
		arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		mSpinner1.setAdapter(arrayAdapter1);
		mSpinner2.setAdapter(arrayAdapter2);
		
		hideToolbar();
	}
}
