package com.drcom.drpalm.Activitys.shares;

import java.util.List;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.drcom.drpalm.View.controls.MyGridView;
import com.drcom.drpalm.objs.ShareToFriendItem;
import com.drcom.drpalm4tianzhujiao.R;

public class ShareChildView extends LinearLayout{
	 private MyGridView myGridView;
	 private List<ShareToFriendItem> myList;
	 private ShareToFriendAdapter adapter;
	 
	public ShareChildView(Context context,List<ShareToFriendItem> list,String subject,String title) {
		super(context);
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
       inflater.inflate(R.layout.main_more_tell_friend, this);
		myList=list;
		myGridView=(MyGridView)findViewById(R.id.main_more_gridview);
		for(int i=0;i<myList.size();i++){
			if(!myList.get(i).visible){
				myList.remove(i);
			}
		}
		adapter = new ShareToFriendAdapter(context, myList,subject, title);
		myGridView.setAdapter(adapter);
	}
	
}
