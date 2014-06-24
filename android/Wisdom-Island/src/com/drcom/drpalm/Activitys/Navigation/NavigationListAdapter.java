package com.drcom.drpalm.Activitys.Navigation;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.DB.NavigationDB;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.objs.NavigationItem;
import com.drcom.drpalm4tianzhujiao.R;

public class NavigationListAdapter extends BaseAdapter{
	
	private List<NavigationItem> mNavigationList = new ArrayList<NavigationItem>();
	private Context mContext;
	private LayoutInflater inflater;
	private NavigationDB mNavigationDB;
	private OnBookmarkBtnClickListener mOnBookmarkBtnClickListener;
	
	public NavigationListAdapter(Context context,List<NavigationItem> list){
		this.mContext = context;
		this.mNavigationList = list;
		mNavigationDB = NavigationDB.getInstance(context);
		inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void changeSourceData(List<NavigationItem> list){
		this.mNavigationList = list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNavigationList.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();
		final NavigationItem item = mNavigationList.get(position);
		if(convertView == null){
			convertView = inflater.inflate(R.layout.navigation_list_item, null);
			viewHolder.bookmark = (ImageView)convertView.findViewById(R.id.list_item_bookmark);
			viewHolder.nameText = (TextView)convertView.findViewById(R.id.list_item_tx);
			viewHolder.bookmark_bg = (LinearLayout)convertView.findViewById(R.id.list_item_bookmark_bg);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		if(LanguageManagement.getSysLanguage(mContext) == CurrentLan.COMPLES_CHINESE){
			viewHolder.nameText.setText(mNavigationList.get(position).text_t);
		}else if(LanguageManagement.getSysLanguage(mContext) == CurrentLan.SIMPLIFIED_CHINESE){
			viewHolder.nameText.setText(mNavigationList.get(position).text_s);
		}else{
			viewHolder.nameText.setText(mNavigationList.get(position).text_en);
		}
		
		if(mNavigationList.get(position).type.equals("school")){
			viewHolder.bookmark_bg.setVisibility(View.VISIBLE);
			
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewHolder.nameText.getLayoutParams();
//			layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.list_item_bookmark_bg);
//			layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.list_item_next);
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			layoutParams.setMargins(MyMothod.Dp2Px(mContext, 5), 0, 0, 0);
			viewHolder.nameText.setLayoutParams(layoutParams);
			
//			if(item.bookmark){
//				viewHolder.bookmark.setBackgroundResource(R.drawable.bookmark_on);
//			}else{
//				viewHolder.bookmark.setBackgroundResource(R.drawable.bookmark_off);
//			}
			if(mNavigationDB.getBookmarkByID(item.point_id)){
				viewHolder.bookmark.setBackgroundResource(R.drawable.bookmark_small);
			}else{
				viewHolder.bookmark.setBackgroundResource(R.drawable.bookmark_off);
			}
			final ImageView bookmarkButton = viewHolder.bookmark;
			viewHolder.bookmark_bg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					Log.i("test_onclick","onclick enter");
					if(mNavigationDB.getBookmarkByID(item.point_id)){
						int i = item.point_id;
						bookmarkButton.setBackgroundResource(R.drawable.bookmark_off);
//						mNavigationDB.markAsBookmark(item.point_id,false);
						mNavigationDB.deleteBookmarkFlag(item.point_id);
					}else{
						int i = item.point_id;
						bookmarkButton.setBackgroundResource(R.drawable.bookmark_small);
//						mNavigationDB.markAsBookmark(item.point_id,true);
						mNavigationDB.markAsBookmark(item.point_id);
					}
					if(mOnBookmarkBtnClickListener != null){
						mOnBookmarkBtnClickListener.onClick();
					}
				}
			});
		}else if(item.type.equals("local")){
			viewHolder.bookmark_bg.setVisibility(View.GONE);
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewHolder.nameText.getLayoutParams();
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
			layoutParams.setMargins(MyMothod.Dp2Px(mContext, 10), 0, 0, 0);
			viewHolder.nameText.setLayoutParams(layoutParams);
		}
		return convertView;
	}
	
	public void setOnBookmarkBtnClickListener(OnBookmarkBtnClickListener listrner){
		mOnBookmarkBtnClickListener = listrner;
	}
	
	public class ViewHolder{
		ImageView bookmark;
		TextView nameText;
		LinearLayout bookmark_bg;
	}
	
	public interface OnBookmarkBtnClickListener{
		public void onClick();
	}
}
