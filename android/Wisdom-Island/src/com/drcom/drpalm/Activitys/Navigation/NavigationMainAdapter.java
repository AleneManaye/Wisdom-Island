package com.drcom.drpalm.Activitys.Navigation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.Tool.DownloadProgressUtils;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.objs.NavigationListItem;
import com.wisdom.island.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NavigationMainAdapter extends BaseAdapter {
	private List<NavigationListItem> myData;
	private LayoutInflater inflater;
	private Context mContext;
	private ImageLoader mImageLoader;

	public NavigationMainAdapter(Context context, List<NavigationListItem> data) {
		myData = data;
		mContext = context;
		this.inflater = LayoutInflater.from(context);
		mImageLoader = DownloadProgressUtils.getmClassImageLoader();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCount() {
		return myData.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (view == null) {
			view = inflater.inflate(R.layout.item, null);
			holder.mImageView = (ImageView) view.findViewById(R.id.image);
			holder.mBgLayout = (RelativeLayout) view.findViewById(R.id.layout);
			holder.mName = (TextView) view.findViewById(R.id.name);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
			
		if (myData.get(position).colorFlag) {
			// holder.mBgLayout.setBackgroundColor(Color.BLUE);
			holder.mBgLayout.setBackgroundColor(mContext.getResources()
					.getColor(R.color.nav_blue));
		} else {
			holder.mBgLayout.setBackgroundColor(Color.WHITE);
		}
		/**
		 * if(myData.get(position).type.equals("kinder")){
		 * if(LanguageManagement.getSysLanguage(mContext) ==
		 * CurrentLan.COMPLES_CHINESE){
		 * holder.mImageView.setBackgroundResource(R.
		 * drawable.navigation_kinder_hk); }else
		 * if(LanguageManagement.getSysLanguage(mContext) ==
		 * CurrentLan.ENGLISH){
		 * holder.mImageView.setBackgroundResource(R.drawable
		 * .navigation_kinder_en); }else{
		 * holder.mImageView.setBackgroundResource
		 * (R.drawable.navigation_kinder); } }else
		 * if(myData.get(position).type.equals("institution")){
		 * if(LanguageManagement.getSysLanguage(mContext) ==
		 * CurrentLan.COMPLES_CHINESE){
		 * holder.mImageView.setBackgroundResource(R
		 * .drawable.navigation_institution_hk); }else
		 * if(LanguageManagement.getSysLanguage(mContext) ==
		 * CurrentLan.ENGLISH){
		 * holder.mImageView.setBackgroundResource(R.drawable
		 * .navigation_institution_en); }else{
		 * holder.mImageView.setBackgroundResource
		 * (R.drawable.navigation_institution); } }else
		 * if(myData.get(position).type.equals("hotline")){
		 * if(LanguageManagement.getSysLanguage(mContext) ==
		 * CurrentLan.COMPLES_CHINESE){
		 * holder.mImageView.setBackgroundResource(R
		 * .drawable.navigation_hotline_hk); }else
		 * if(LanguageManagement.getSysLanguage(mContext) ==
		 * CurrentLan.ENGLISH){
		 * holder.mImageView.setBackgroundResource(R.drawable
		 * .navigation_hotline_en); }else{
		 * holder.mImageView.setBackgroundResource
		 * (R.drawable.navigation_hotline); } }else{
		 * if(LanguageManagement.getSysLanguage(mContext) ==
		 * CurrentLan.COMPLES_CHINESE){
		 * holder.mImageView.setBackgroundResource(R
		 * .drawable.navigation_ebaby_channel_hk); }else
		 * if(LanguageManagement.getSysLanguage(mContext) ==
		 * CurrentLan.ENGLISH){
		 * holder.mImageView.setBackgroundResource(R.drawable
		 * .navigation_ebaby_channel_en); }else{
		 * holder.mImageView.setBackgroundResource
		 * (R.drawable.navigation_ebaby_channel); } }
		 **/
		if (LanguageManagement.getSysLanguage(mContext) == CurrentLan.COMPLES_CHINESE) {

				myData.get(position).name_used = myData.get(position).name_zht;

		} else if (LanguageManagement.getSysLanguage(mContext) == CurrentLan.ENGLISH) {

				myData.get(position).name_used = myData.get(position).name_en;

		} else {
				myData.get(position).name_used = myData.get(position).name_zhs;

		}
		
		if(myData.get(position).type.equals("hotline")){
			holder.mName.setText(mContext.getResources().getString(R.string.customer_service));
			holder.mImageView.setImageResource(R.drawable.navigation_hotline);
		}else if(myData.get(position).type.equals("ebabychannel")){
			holder.mName.setText(mContext.getResources().getString(R.string.WisdomIsland_channel));
			holder.mImageView.setImageResource(R.drawable.navigation_ebaby_channel);
		}else{
			holder.mName.setText(myData.get(position).name_used);
		}
		
		mImageLoader.DisplayImage(myData.get(position).imageurl, holder.mImageView, false);
		
		return view;
	}

	private class ViewHolder {
		public ImageView mImageView;
		public RelativeLayout mBgLayout;
		public TextView mName;
	}

}
