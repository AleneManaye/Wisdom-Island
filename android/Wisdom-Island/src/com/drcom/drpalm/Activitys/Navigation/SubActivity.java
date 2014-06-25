package com.drcom.drpalm.Activitys.Navigation;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

import com.drcom.drpalm.View.controls.NewSearchBar;
import com.wisdom.island.R;

public class SubActivity extends Activity{
	public LinearLayout mContentLayout;
	public NewSearchBar mSearchbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);	//一定要放在第一行
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_activity);
		mContentLayout = (LinearLayout)findViewById(R.id.content_layout);
		mSearchbar = (NewSearchBar)findViewById(R.id.search_bar);
	}
}
