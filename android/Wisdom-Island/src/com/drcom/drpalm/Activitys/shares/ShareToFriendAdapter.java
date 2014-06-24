package com.drcom.drpalm.Activitys.shares;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drcom.drpalm.View.share.ShareIntentHelper;
import com.drcom.drpalm.objs.ShareToFriendItem;
import com.drcom.drpalm4tianzhujiao.R;

public class ShareToFriendAdapter extends ArrayAdapter<ShareToFriendItem> {
	private List<ShareToFriendItem> itemList ;
	private LayoutInflater listContainer;  
	private ShareToFriendItem info;
	private Context mContext;
	private ShareIntentHelper helper;
	private String subject,title;
	
	// 这个是分享中生成分享按钮的GridView的adapter
	public ShareToFriendAdapter(Context context, List<ShareToFriendItem> itemList,String subject,String title) {
		super(context, 0, itemList);
		this.listContainer = LayoutInflater.from(context); 
		this.itemList = itemList;
		mContext=context;
		this.subject = subject;
		this.title = title;
		String errorMessage = mContext.getResources().getString(R.string.shareerrormessage);
		helper = new ShareIntentHelper(mContext,subject,title,errorMessage);
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public ShareToFriendItem getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder = null;
		info = this.getItem(position);
		if (convertView == null) {
			convertView =  listContainer.inflate(R.layout.main_item, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
			
			holder.img = (ImageView)convertView.findViewById(R.id.main_item_imageView);
			holder.imgdel = (ImageView)convertView.findViewById(R.id.main_item_del_imageView);
			holder.txt = (TextView)convertView.findViewById(R.id.main_item_textView);
			holder.numTxt = (TextView)convertView.findViewById(R.id.main_item_number_txtv);
			holder.type=info.type;
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.img.setBackgroundResource(info.btnIcon);
		holder.txt.setText(info.btnName);
		holder.txt.setTextColor(Color.WHITE);
		holder.numTxt.setVisibility(View.GONE);
		holder.imgdel.setVisibility(View.GONE);
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//设置跳转
				ViewHolder holder=(ViewHolder)v.getTag();
				helper.startIntent(holder.type);
			}
		});
		if(!info.visible){
			convertView.setVisibility(View.GONE);
		}
		return convertView;
    }
    
    class ViewHolder{
    	ImageView img;
    	ImageView imgdel;
    	TextView txt;
    	TextView numTxt;
    	String type;
    }

}
