package com.drcom.drpalm.Activitys.login;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.wisdom.island.R;

public class LoginAdapter extends ArrayAdapter<UserInfo> {
	private Context mContext;
	private List<UserInfo> mData;
	private Handler mHandlerDel;
	private ImageLoader mImageLoader;

	public LoginAdapter(Context context, List<UserInfo> data) {
		super(context, 0, data);
		mContext = context;
		mData = data;
		mImageLoader = ((LoginActivity)mContext).getmClassImageLoader();
	}

	@Override
	public int getCount() {
		if(mData != null){
			return mData.size();
		}
		return 0;
	}

	@Override
	public UserInfo getItem(int position) {
		return mData.get(position);
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		view = LayoutInflater.from(mContext).inflate(R.layout.main_item, null);
		
		
		TextView textView = (TextView) view.findViewById(R.id.main_item_textView);
		textView.setText(mData.get(position).strUsrName);
		
//		imageView.setImageBitmap(mData.get(position).image);
//		if(mData.get(position).pic != null){
//			imageView.setImageBitmap(mData.get(position).pic);
//		}else{
//			imageView.setBackgroundResource(R.drawable.login_default_user);
//		}
		
		ImageView imageView = (ImageView) view.findViewById(R.id.main_item_imageView);
		imageView.setBackgroundResource(R.drawable.white_kk);

		if(mData.get(position).headurl.equals("")){
			imageView.setImageResource(R.drawable.login_default_user);
		}else{
			mImageLoader.DisplayImage(mData.get(position).headurl, imageView, false);
		}
		
		TextView textViewNum = (TextView) view.findViewById(R.id.main_item_number_txtv);
		textViewNum.setVisibility(View.GONE);
		
		ImageView imageViewDel = (ImageView) view.findViewById(R.id.main_item_del_imageView);
		imageViewDel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mHandlerDel != null){
					Message msg = new Message();
					msg.arg1 = position;
					mHandlerDel.sendMessage(msg);
				}
			}
		});
		
		return view;
	}
	
	public void setDelHandler(Handler h){
		mHandlerDel = h;
	}
}
