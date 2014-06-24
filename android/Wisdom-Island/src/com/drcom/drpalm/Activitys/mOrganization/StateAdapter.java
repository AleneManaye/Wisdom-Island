package com.drcom.drpalm.Activitys.mOrganization;

import java.util.ArrayList;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.drcom.drpalm.View.mOrganization.StateActivityManagement;
import com.drcom.drpalm.objs.OrganizationItem;
import com.drcom.drpalm4tianzhujiao.R;

public class StateAdapter extends BaseAdapter {
//	private static final int REFRESH = 1;
//	private Context context;
	private List<OrganizationItem> data;
	private LayoutInflater mLayoutInflater;
//	private int kind;;
	private TextView tv;
	private OnOrganizationCheckedListener mOnOrganizationCheckedListener;
//	private Handler mHandler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			if (REFRESH == msg.what)
//				StateAdapter.this.notifyDataSetChanged();
//		};
//	};
	private StateActivityManagement mStateActivityManagement;

//	private int number;
//	private int selectorNumber;
//	private int halfSelectorNumber;

	public StateAdapter(Context context, List<OrganizationItem> data) {
		super();
//		this.context = context;
//		this.kind = kind;
		mStateActivityManagement = new StateActivityManagement(context);
		this.data = data;
		this.mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public ImageView img;
		private TextView tv;
		private ImageView next;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.school_state_item, null);
			vh.img = (ImageView) convertView.findViewById(R.id.school_checkbox);
			vh.tv = (TextView) convertView.findViewById(R.id.school_tv);
			vh.next = (ImageView) convertView.findViewById(R.id.school_nextpage);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		OrganizationItem item = data.get(position);
		final OrganizationItem item1 = item;
//		convertView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (mStateActivityManagement.getData(item1.orgID).size() > 0) {
//					data = mStateActivityManagement.getData(item1.orgID);
//					StateAdapter.this.notifyDataSetChanged();
//				}
//			}
//		});
		vh.tv.setText(item.orgName);
		if (mStateActivityManagement.getData(item.orgID).size() > 0)
			vh.next.setVisibility(View.VISIBLE);
		else
			vh.next.setVisibility(View.INVISIBLE);

		if (item.state == 0)// 0为不选
			vh.img.setBackgroundResource(R.drawable.checkboxnone);
		else if (item.state == 1)// 1为选中
			vh.img.setBackgroundResource(R.drawable.checkboxall);
		else if (item.state == 2)// 2为半选
			vh.img.setBackgroundResource(R.drawable.checkboxnotall);
		final ImageView imageViewTemp = vh.img;
		imageViewTemp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (item1.state == 0) {
					imageViewTemp.setBackgroundResource(R.drawable.checkboxall);
					
//					for (int i = 0; i < StateActivity.all.size(); i++) {
//						if (item1.orgID == StateActivity.all.get(i).orgID) {
//							mStateActivityManagement.saveData(StateActivity.all.get(i), 1);
//						}
//					}
					if(mOnOrganizationCheckedListener != null){
						mOnOrganizationCheckedListener.onClick(item1.orgID, 1);
					}
				} else if (item1.state == 1) {
					imageViewTemp.setBackgroundResource(R.drawable.checkboxnone);
//					for (int i = 0; i < StateActivity.all.size(); i++) {
//						if (item1.orgID == StateActivity.all.get(i).orgID) {
//							mStateActivityManagement.saveData(StateActivity.all.get(i), 0);
//						}
//					}
					if(mOnOrganizationCheckedListener != null){
						mOnOrganizationCheckedListener.onClick(item1.orgID, 0);
					}
				} else if (item1.state == 2) {
					imageViewTemp.setBackgroundResource(R.drawable.checkboxall);
//					for (int i = 0; i < StateActivity.all.size(); i++) {
//						if (item1.orgID == StateActivity.all.get(i).orgID) {
//							mStateActivityManagement.saveData(StateActivity.all.get(i), 1);
//						}
//					}
					if(mOnOrganizationCheckedListener != null){
						mOnOrganizationCheckedListener.onClick(item1.orgID, 1);
					}
				}
			}
		});

		return convertView;
	}
	
	public void setKind(int kind) {
		int id = 0;
		List<OrganizationItem> idata = new ArrayList<OrganizationItem>();
		for (int i = 0; i < StateActivity.all.size(); i++) {
			if (kind == StateActivity.all.get(i).orgID)
				id = StateActivity.all.get(i).orgPid;
		}
		data = mStateActivityManagement.getData(id);
		StateAdapter.this.notifyDataSetChanged();
	}
	
	public int getParentKind() {
		int parentId = 0;
		if (data.size() > 0) {
			parentId = data.get(0).orgPid;
		}
		return parentId;
	}

	public List<OrganizationItem> getCurentData() {
		return data;
	}
	
	public interface OnOrganizationCheckedListener{
		public void onClick(int orgID, int state);
	}
	
	/*
	 * checkBox 点击功能响应接口
	 */
	public void setOnOrganizationCheckedListener(OnOrganizationCheckedListener onOrganizationCheckedListener){
		this.mOnOrganizationCheckedListener = onOrganizationCheckedListener;
	}
}