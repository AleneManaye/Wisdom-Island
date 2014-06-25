package com.drcom.drpalm.Activitys.events.sent;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.objs.EventDetailsItem.Replyer;
import com.wisdom.island.R;

public class ReplyersAdapter extends ArrayAdapter<Replyer>{
	private List<Replyer> mList;
	private LayoutInflater listContainer;
	private Replyer info;

	public ReplyersAdapter(Context context,List<Replyer> list) {
		super(context,0,list);
		this.listContainer = LayoutInflater.from(context); 
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}
	@Override
	public Replyer getItem(int position) {
		return mList.get(position);
	}
	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		info = this.getItem(position);
		if (convertView == null) {
			convertView =  listContainer.inflate(R.layout.replyerlist_item_view, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
			
			holder.name = (TextView) convertView.findViewById(R.id.replyerlist_item_name_txtview);
			holder.date = (TextView) convertView.findViewById(R.id.replyerlist_item_date_txtview);
			holder.pic = (ImageView) convertView.findViewById(R.id.replyerlist_item_imgview);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(info.ReplyerName);
		holder.date.setText(DateFormatter.getStringYYYYMMDD(DateFormatter.getDateFromSecondsString(info.ReplyLastTime)));
		if(DateFormatter.getDateFromSecondsString(info.lastawstimeread ).getTime() < DateFormatter.getDateFromSecondsString(info.ReplyLastTime).getTime()){
			holder.pic.setVisibility(View.VISIBLE);
		}else{
			holder.pic.setVisibility(View.GONE);
		}

		return convertView;
	}
	
	class ViewHolder{
		TextView name;
		TextView date;
		ImageView pic;
	}
}
