package com.drcom.drpalm.Activitys.bindaccount;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.drcom.drpalm.DB.NavigationDB;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.objs.BindaccountItem;
import com.drcom.drpalm4tianzhujiao.R;

public class BindaccountAdapter extends BaseAdapter implements OnClickListener {
	private List<BindaccountItem> mData;
	private Context mContext;
	private int mItem;
	private LayoutInflater mLayoutInflater;
	public Boolean mBtn_flag = false;
	private NavigationDB mNavigationDb;
	private ImageLoader mImageLoader;
	private Handler mHandler;
	private CurrentLan mCurrentLan;
	
	public BindaccountAdapter(Context context, List<BindaccountItem> data,
			int bindaccountItem, ImageLoader mImageLoader,Handler handle) {
		this.mData = data;
		this.mContext = context;
		mItem = bindaccountItem;
		this.mImageLoader = mImageLoader;
		this.mHandler = handle;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mNavigationDb = NavigationDB.getInstance(context);
		mCurrentLan = LanguageManagement.getSysLanguage(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DataWrapper datawrapper = null;
		if (converView == null) {
			datawrapper = new DataWrapper();
			converView = mLayoutInflater.inflate(mItem, null);
			datawrapper.bindaccount_name = (TextView) converView
					.findViewById(R.id.tv_bindaccount_name);
			datawrapper.bindaccout_id = (TextView) converView
					.findViewById(R.id.tv_bindaccount_id);
			datawrapper.bindaccout_schoolname = (TextView) converView
					.findViewById(R.id.tv_bindaccount_schoolName);
			datawrapper.btn_accountDelete = (Button) converView
					.findViewById(R.id.btn_bindaccountDelete);
			datawrapper.btn_accountDelete.setOnClickListener(this);
			datawrapper.bindaccount_imageview = (ImageView) converView
					.findViewById(R.id.bindaccount_IV);
			datawrapper.bindaccount_IVnumber = (TextView) converView
					.findViewById(R.id.bindaccount_number_txtv);
			converView.setTag(datawrapper);
		} else {
			datawrapper = (DataWrapper) converView.getTag();
		}

		datawrapper.btn_accountDelete.setTag(position);

		datawrapper.bindaccount_name.setText(mData.get(position).pub_name);
		datawrapper.bindaccout_id.setText("(" + mData.get(position).pub_account
				+ ")");
		
		if (mCurrentLan == CurrentLan.SIMPLIFIED_CHINESE) {
			datawrapper.bindaccout_schoolname
			.setText(mData.get(position).pub_schoolName_hans);
		} else if (mCurrentLan == CurrentLan.COMPLES_CHINESE) {
			datawrapper.bindaccout_schoolname
			.setText(mData.get(position).pub_schoolName_hant);
		} else if (mCurrentLan == CurrentLan.ENGLISH) {
			datawrapper.bindaccout_schoolname
			.setText(mData.get(position).pub_schoolName_en);
		}

		if (mBtn_flag) {
			datawrapper.btn_accountDelete.setVisibility(View.VISIBLE);
		} else {
			datawrapper.btn_accountDelete.setVisibility(View.GONE);
		}
		// add by hzy 
		if (mData.get(position).pub_imgaeurl != null && !mData.get(position).pub_imgaeurl.equals("") ) {
			mImageLoader.DisplayImage(mData.get(position).pub_imgaeurl, datawrapper.bindaccount_imageview, false);
		} else {
			datawrapper.bindaccount_imageview
					.setImageResource(R.drawable.login_default_user);
		}
		String number_new = mData.get(position).pub_image_number;
		if (!number_new.equals("")&& !number_new.equals("0")) {
			datawrapper.bindaccount_IVnumber.setVisibility(View.VISIBLE);
			datawrapper.bindaccount_IVnumber.setText(mData.get(position).pub_image_number);
		}else{
			datawrapper.bindaccount_IVnumber.setVisibility(View.GONE);
		}
		
		return converView;
	}

	public final class DataWrapper {

		public TextView bindaccount_name;
		public TextView bindaccout_id;
		public TextView bindaccout_schoolname;
		public Button btn_accountDelete;
		public ImageView bindaccount_imageview;
		public TextView bindaccount_IVnumber;

		public DataWrapper() {
			super();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		int i = (Integer) v.getTag();
		String userId = mData.get(i).pub_account;
		Log.i("test1", "userId=" + userId);
		
		Message msg = mHandler.obtainMessage();
		msg.arg1 = BindaccountListActivity.ADAPTER_ARG1;
		msg.what = i;
		msg.obj = userId;
		mHandler.sendMessage(msg);
	}

}
