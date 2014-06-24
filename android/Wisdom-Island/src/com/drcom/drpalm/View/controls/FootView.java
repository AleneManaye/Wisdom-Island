package com.drcom.drpalm.View.controls;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drcom.drpalm4tianzhujiao.R;

public class FootView extends LinearLayout{

	private TextView title_txtview;

	public FootView(Context context) {
		super(context);
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        inflater.inflate(R.layout.foot_view, this); 
        
        title_txtview = (TextView)findViewById(R.id.foot_textView);
	}
	
	public void setTitle(CharSequence title) {
		title_txtview.setText(title);
	}
	
	@Override
	public void setVisibility(int visibility) {
		// TODO Auto-generated method stub
		title_txtview.setVisibility(visibility);
		super.setVisibility(visibility);
	}

}
