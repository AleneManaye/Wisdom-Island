package com.drcom.drpalm.Activitys.bindaccount;

import java.util.List;

import com.drcom.drpalm.Activitys.bindaccount.BindaccountAdapter.DataWrapper;
import com.drcom.drpalm.Activitys.events.NewEventActivity;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.objs.NavigationListItem;
import com.drcom.drpalm4tianzhujiao.R;

import android.R.bool;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InstitutionListAdapter extends BaseAdapter {

	private List<NavigationListItem> mList;
	private Context mContext;
	private int mItem;
	private LayoutInflater mLayoutInflater;
	private CurrentLan mCurrentLan;

	public InstitutionListAdapter(Context context,
			List<NavigationListItem> list, int searchSchoolItem) {
		// TODO Auto-generated constructor stub
		mList = list;
		mContext = context;
		mItem = searchSchoolItem;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCurrentLan = LanguageManagement.getSysLanguage(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DataWrapper datawrapper = null;
		if (convertView == null) {
			datawrapper = new DataWrapper();
			convertView = mLayoutInflater.inflate(mItem, null);
			datawrapper.schoolname = (TextView) convertView
					.findViewById(R.id.tv_school_name);
			datawrapper.imagenext = (ImageView) convertView
					.findViewById(R.id.iv_school_next);
			convertView.setTag(datawrapper);
		} else {
			datawrapper = (DataWrapper) convertView.getTag();
		}
		if (mCurrentLan == CurrentLan.SIMPLIFIED_CHINESE) {
			datawrapper.schoolname.setText(mList.get(position).name_zhs);
		} else if (mCurrentLan == CurrentLan.COMPLES_CHINESE) {
			datawrapper.schoolname.setText(mList.get(position).name_zht);
		} else if (mCurrentLan == CurrentLan.ENGLISH) {
			datawrapper.schoolname.setText(mList.get(position).name_en);
		}
		return convertView;
	}
	public final class DataWrapper {

		public TextView schoolname;
		public ImageView imagenext;

		public DataWrapper() {
			super();
		}
	}

}
