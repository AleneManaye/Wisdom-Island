package com.drcom.drpalm.Activitys.LeaveApplication;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.drcom.drpalm.objs.ReceiverItem;
import com.drcom.drpalm4tianzhujiao.R;

class ReceiverListAdapter extends BaseAdapter{

	private LayoutInflater layoutInflater;
    private List<ReceiverItem> mreceiver_list;

    public ReceiverListAdapter(List<ReceiverItem> receiver_list, Context context){
        this.mreceiver_list = receiver_list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.mreceiver_list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mreceiver_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        final int index = position;
        
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.receiver_item,null);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.receiver_info);

            viewHolder.checkBox.setTextColor(Color.BLACK);
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {                	
                	mreceiver_list.get(index).ischecked = ((CheckBox)v).isChecked();
                	Log.i("debug_checkBox",mreceiver_list.get(index).receiver_name);
                }
            });

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.checkBox.setText(mreceiver_list.get(index).receiver_name);
        viewHolder.checkBox.setChecked(mreceiver_list.get(index).ischecked);
        return convertView;
    }

    class ViewHolder{
        public CheckBox checkBox;
    }

}
