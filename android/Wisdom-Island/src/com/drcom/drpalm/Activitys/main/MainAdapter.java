package com.drcom.drpalm.Activitys.main;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drcom.drpalm.objs.MainMenuItem;
import com.wisdom.island.R;

public class MainAdapter extends ArrayAdapter<MainMenuItem> {
	private List<MainMenuItem> itemList ;
	private LayoutInflater listContainer;  
//	private BuyerMainActivity context;
	private MainMenuItem info;
	
	public MainAdapter(Context context, List<MainMenuItem> itemList) {
		super(context, 0, itemList);
//		this.context = (BuyerMainActivity) context;
		this.listContainer = LayoutInflater.from(context); 
		this.itemList = itemList;
		
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public MainMenuItem getItem(int position) {
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
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.img.setBackgroundResource(info.getPicResId());
		holder.txt.setText(info.getName());
		if(!info.getCount().equals("0")){
			holder.numTxt.setText(info.getCount());
			holder.numTxt.setVisibility(View.VISIBLE);
		}else{
			holder.numTxt.setVisibility(View.GONE);
		}
		
		if(info.CanDel()){
			holder.imgdel.setVisibility(View.VISIBLE);
			if(info.getonClickDelListener() != null){
				holder.imgdel.setOnClickListener(info.getonClickDelListener());
			}
		}else{
			holder.imgdel.setVisibility(View.GONE);
		}
		
		if(info.getmBitmap() != null){
			holder.img.setImageBitmap(info.getmBitmap());
		}else{
			holder.img.setImageBitmap(null);
		}
		
		
		return convertView;
    }
    
    class ViewHolder{
    	ImageView img;
    	ImageView imgdel;
    	TextView txt;
    	TextView numTxt;
    }

}
