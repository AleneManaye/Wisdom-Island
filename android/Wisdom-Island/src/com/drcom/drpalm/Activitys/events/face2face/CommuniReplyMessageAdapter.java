package com.drcom.drpalm.Activitys.events.face2face;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.AwsContentItem;
import com.drcom.drpalm.objs.CommunicateItem;
import com.wisdom.island.R;

public class CommuniReplyMessageAdapter extends BaseAdapter{
	protected static final String TAG = "ChattingAdapter";
	private Context context;
	private SettingManager setInstance;
	private ImageLoader mImageLoader;

	//private List<ReplyMessage> replyMessages;
	private List<CommunicateItem>  replyItemlist ;

	public CommuniReplyMessageAdapter(Context context, List<CommunicateItem> messages,ImageLoader imageLoader) {
		super();
		this.context = context;
		this.replyItemlist = messages;
		setInstance = SettingManager.getSettingManager(context);
		this.mImageLoader = imageLoader;
	}

	@Override
	public int getCount() {
		return replyItemlist.size();
	}

	@Override
	public Object getItem(int position) {
		return replyItemlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		CommunicateItem item = replyItemlist.get(position);
		if (convertView == null || (holder = (ViewHolder) convertView.getTag()).flag != item.direction) {	//

			holder = new ViewHolder();
			if (item.direction == AwsContentItem.MESSAGE_FROM) {
//			if(item.rec_id == setInstance.mCurrentUserInfo.strUsrName){
				holder.flag = AwsContentItem.MESSAGE_FROM;

				convertView = LayoutInflater.from(context).inflate(R.layout.events_reply_item_from, null);
			} else {
				holder.flag = AwsContentItem.MESSAGE_TO;
				convertView = LayoutInflater.from(context).inflate(R.layout.events_reply_item_to, null);
			}

			holder.mImg = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.text = (TextView) convertView.findViewById(R.id.chatting_content_itv);
			holder.text.setSingleLine(false);
			
			holder.textTime = (TextView) convertView.findViewById(R.id.chatting_content_time);
			convertView.setTag(holder);
		}else{
			holder =(ViewHolder) convertView.getTag();
		}
		
		mImageLoader.DisplayImage(item.headimgurl, holder.mImg, false);
		holder.text.setText(item.body);
		holder.textTime.setTextColor(Color.GRAY);
		
		if(item.lastupdate == null)
			holder.textTime.setVisibility(View.GONE);
		else 
		{
//			long time = Long.parseLong(item.replyTime);
//			time *= 1000;
//			Date date = new Date(time);
			String strTime = DateFormatter.getStringYYYYMMDDHHmm(item.lastupdate);
			holder.textTime.setText(strTime);
			holder.textTime.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
	
	//优化listview的Adapter
	static class ViewHolder {
		ImageView mImg;
		TextView text;
		TextView textTime;
		int flag;
	}

}
