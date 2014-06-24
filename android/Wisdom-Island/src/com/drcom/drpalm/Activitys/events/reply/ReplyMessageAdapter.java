package com.drcom.drpalm.Activitys.events.reply;

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
import com.drcom.drpalm4tianzhujiao.R;

public class ReplyMessageAdapter extends BaseAdapter{
	protected static final String TAG = "ChattingAdapter";
	private Context context;
	private SettingManager setInstance;
	private ImageLoader mImageLoader;

	//private List<ReplyMessage> replyMessages;
	private List<AwsContentItem>  replyItemlist ;

	public ReplyMessageAdapter(Context context, List<AwsContentItem> messages,ImageLoader imageLoader) {
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
		AwsContentItem item = replyItemlist.get(position);
		if (convertView == null || (holder = (ViewHolder) convertView.getTag()).flag != item.direction) {	//

			holder = new ViewHolder();
			if (item.direction == AwsContentItem.MESSAGE_FROM) {
//			if(item.rec_id == setInstance.mCurrentUserInfo.strUsrName){
				holder.flag = AwsContentItem.MESSAGE_FROM;

				convertView = LayoutInflater.from(context).inflate(R.layout.events_reply_item_from, null);
				holder.pubName = (TextView)convertView.findViewById(R.id.reply_pub_name);
			} else {
				holder.flag = AwsContentItem.MESSAGE_TO;
				convertView = LayoutInflater.from(context).inflate(R.layout.events_reply_item_to, null);
			}

			holder.mImg = (ImageView)convertView.findViewById(R.id.imageView1);
			
			holder.text = (TextView) convertView.findViewById(R.id.chatting_content_itv);
			holder.text.setSingleLine(false);
			
			holder.textTime = (TextView) convertView.findViewById(R.id.chatting_content_time);
			convertView.setTag(holder);
		}else{
			holder =(ViewHolder) convertView.getTag();
		}
		
		
		if(holder.flag == AwsContentItem.MESSAGE_FROM){
			holder.pubName.setText(item.pub_name);
		}
		holder.text.setText(item.aws_body);
		holder.textTime.setTextColor(Color.GRAY);
		mImageLoader.DisplayImage(item.headimgurl, holder.mImg, false);
		
		if(item.aws_time == null)
			holder.textTime.setVisibility(View.GONE);
		else 
		{
//			long time = Long.parseLong(item.replyTime);
//			time *= 1000;
//			Date date = new Date(time);
			String strTime = DateFormatter.getStringYYYYMMDDHHmm(item.aws_time);
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
		TextView pubName;
		int flag;
	}

}
