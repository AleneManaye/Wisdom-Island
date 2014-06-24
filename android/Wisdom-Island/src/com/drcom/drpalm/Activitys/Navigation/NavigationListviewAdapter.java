package com.drcom.drpalm.Activitys.Navigation;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drcom.drpalm.DB.NavigationDB;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.objs.NavigationItem;
import com.drcom.drpalm4tianzhujiao.R;

public class NavigationListviewAdapter extends CursorAdapter{
	private Context mContext;
	private NavigationDB mNavigationDB;
//	private Button bookmark;
	public NavigationListviewAdapter(Context context, Cursor cursor){
		super(context,cursor);
		mContext = context;
		mNavigationDB = NavigationDB.getInstance(context);
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.navigation_list_item, null);
		return view;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		final NavigationItem item = mNavigationDB.retrieveNavigationItem(cursor);
		if(item != null){
			RelativeLayout rl = (RelativeLayout)view.findViewById(R.id.list_item_bg) ;
//			DrawableSelector selector = new DrawableSelector(mContext);
//
//			Drawable listnormal = mContext.getResources().getDrawable(R.drawable.list_pattern);
//			Drawable listpressed =  mContext.getResources().getDrawable(R.drawable.list_pattern_pressed);
//			StateListDrawable listdrawable = selector.setBackgroundDrawable(listnormal,listpressed ,listnormal,listnormal);
//	    	rl.setBackgroundDrawable(listdrawable);
	    	
	    	final ImageView bookmark = (ImageView)view.findViewById(R.id.list_item_bookmark);
	    	TextView nameText = (TextView)view.findViewById(R.id.list_item_tx);
	    	nameText.setText(item.name);
	    	final LinearLayout bookmark_bg = (LinearLayout)view.findViewById(R.id.list_item_bookmark_bg);
	    	
			if(item.type.equals("school")){
				bookmark_bg.setVisibility(View.VISIBLE);
				
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)nameText.getLayoutParams();
				layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.list_item_bookmark_bg);
				layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.list_item_next);
				layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
				layoutParams.setMargins(MyMothod.Dp2Px(mContext, 5), 0, 0, 0);
				nameText.setLayoutParams(layoutParams);
				
//				if(item.bookmark){
//					bookmark.setBackgroundResource(R.drawable.bookmark_on);
//				}else{
//					bookmark.setBackgroundResource(R.drawable.bookmark_off);
//				}
				if(mNavigationDB.getBookmarkByID(item.point_id)){
					bookmark.setBackgroundResource(R.drawable.bookmark_small);
				}else{
					bookmark.setBackgroundResource(R.drawable.bookmark_off);
				}
				bookmark_bg.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						Log.i("test_onclick","onclick enter");
						if(mNavigationDB.getBookmarkByID(item.point_id)){
							int i = item.point_id;
							bookmark.setBackgroundResource(R.drawable.bookmark_off);
//							mNavigationDB.markAsBookmark(item.point_id,false);
							mNavigationDB.deleteBookmarkFlag(item.point_id);
						}else{
							int i = item.point_id;
							bookmark.setBackgroundResource(R.drawable.bookmark_small);
//							mNavigationDB.markAsBookmark(item.point_id,true);
							mNavigationDB.markAsBookmark(item.point_id);
						}
					}
				});		
			}else if(item.type.equals("local")){
				bookmark_bg.setVisibility(View.GONE);
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)nameText.getLayoutParams();
				layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
				layoutParams.setMargins(MyMothod.Dp2Px(mContext, 10), 0, 0, 0);
				nameText.setLayoutParams(layoutParams);
			}
		}
	}
}
