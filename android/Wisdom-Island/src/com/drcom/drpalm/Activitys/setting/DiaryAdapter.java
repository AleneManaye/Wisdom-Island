package com.drcom.drpalm.Activitys.setting;

import java.util.ArrayList;
import java.util.List;

import com.drcom.drpalm.Activitys.events.draft.EventsDraftListCursorAdapter;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.View.setting.DiaryManager;
import com.drcom.drpalm.objs.DiaryItem;
import com.drcom.drpalm.objs.GrowdiaryItem;
import com.drcom.drpalm4tianzhujiao.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DiaryAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<DiaryItem> mData;
	private LayoutInflater mInflater;
	private OnDeleteBtnClickListener mOnDeleteBtnClickListener;
	private DiaryManager mDiaryManager;
	private Handler mHandler;
	
	public DiaryAdapter(Context context, List<DiaryItem> list, Handler handler){
		this.mContext = context;
		this.mData = list;
		this.mHandler = handler;
		mDiaryManager = new DiaryManager(mContext);
		this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = new ViewHolder();
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.diary_item, null);
			holder.mTitle = (TextView)convertView.findViewById(R.id.diary_title);
			holder.mSummary = (TextView)convertView.findViewById(R.id.diary_sum);
			holder.mLastupdate = (TextView)convertView.findViewById(R.id.diary_lastupdate);
			holder.mDeleteBtn = (ImageView)convertView.findViewById(R.id.delete_btn);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.mTitle.setText(mData.get(position).diaryTitle);
		holder.mSummary.setText(mData.get(position).diarySum);
		holder.mLastupdate.setText(DateFormatter.getStringYYYYMMDD(mData.get(position).lastUpdate));
		final GrowdiaryItem cur_item = new GrowdiaryItem();
		cur_item.diaryid = mData.get(position).diaryId + "";
		cur_item.title = mData.get(position).diaryTitle;
		cur_item.contect = mData.get(position).diaryContent;
		cur_item.status = "C";
		holder.mDeleteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
				ab.setTitle(mContext.getResources().getString(R.string.suretodelete));
				ab.setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<GrowdiaryItem> filelist = new ArrayList<GrowdiaryItem>();
						filelist.add(cur_item);
						mDiaryManager.SubmitGrowdiary(filelist, mHandler);
					}
				});
				ab.setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				ab.show();
			}
//				GrowdiaryItem item = new GrowdiaryItem();
//				item.diaryid = mData.get(position).diaryId + "";
//				item.title = mData.get(position).diaryTitle;
//				item.contect = mData.get(position).diaryContent;
//				item.status = "C";
//				ArrayList<GrowdiaryItem> filelist = new ArrayList<GrowdiaryItem>();
//				filelist.add(item);
//				mDiaryManager.SubmitGrowdiary(filelist, mHandler);
//			}
		});
		return convertView;
	}
	
	public void setOnDeleteBtnClickListener(OnDeleteBtnClickListener listener){
		this.mOnDeleteBtnClickListener = listener;
	}
	
	public interface OnDeleteBtnClickListener{
		public void onClick();
	}
	
	private class ViewHolder{
		TextView mTitle;
		TextView mSummary;
		TextView mLastupdate;
		ImageView mDeleteBtn;
	}
}
