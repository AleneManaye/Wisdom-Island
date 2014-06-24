package com.drcom.drpalm.Activitys.LeaveApplication;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.DB.EventsDB;
import com.drcom.drpalm.DB.LeaveApplicationDB;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.CommonTranslate.DateFormatter;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.EventDetailsItem;
import com.drcom.drpalm.objs.EventDraftItem;
import com.drcom.drpalm.objs.LeaveApplicationMainItem;
import com.drcom.drpalm4tianzhujiao.R;

public class LeaveApplicationMainAdapter extends CursorAdapter {
	
	//控件
	private LinearLayout mLinearLayoutIV;
	private ImageView thumb_imgview;	//缩略图
	private ImageView event_rowbookmark ;
	private ImageView event_rowattachment ;
	private ImageView event_isPrize ;
	private ImageView event_isfeedback;
	private ImageView event_needreview;
	private TextView evnetsTitleTV = null;
	private TextView evnetsSummaryTV = null;
	private TextView evnetsDateTV = null;
	private ImageView mImgview_unreadmark;
	//变量
	private Context mContext ;
	private LeaveApplicationDB mLeaveDB ;
	//下载句柄
	private ImageLoader mImageLoader;
	private TextPaint tp;
	private Date today = new Date();

	public LeaveApplicationMainAdapter(Context context,Cursor c,ImageLoader imageLoader) {
		super(context, c);
		// TODO Auto-generated constructor stub
		this.mContext = context ;
		this.mImageLoader = imageLoader;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view =  vi.inflate(R.layout.events_listitem_view, null);
		bindView(view, context, cursor);
		return view ;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		LeaveApplicationMainItem item = mLeaveDB.getInstance(context,GlobalVariables.gSchoolKey).retrieveLeaveApplicationMainItem(cursor);

		//图标 LAYOUT
		mLinearLayoutIV = (LinearLayout)view.findViewById(R.id.IV_layout);
		
		//收藏
		event_rowbookmark = (ImageView) view.findViewById(R.id.event_rowbookmark);
		event_rowbookmark.setVisibility(View.GONE);

		//附件
		event_rowattachment = (ImageView) view.findViewById(R.id.event_rowattachment);
		if (item.hasatt == 1) {
			event_rowattachment.setVisibility(View.VISIBLE);
		} else{
			event_rowattachment.setVisibility(View.GONE);
		}
		
		//加急
		event_isPrize = (ImageView) view.findViewById(R.id.event_isPrize);
		event_isPrize.setVisibility(View.GONE);
		
		//回评
		event_needreview = (ImageView) view.findViewById(R.id.event_needreview);
		event_needreview.setVisibility(View.GONE);
		
		//回复气泡
		event_isfeedback = (ImageView) view.findViewById(R.id.event_feedback);
		event_isfeedback.setVisibility(View.GONE);
			
		
		
		//是否有中划线,设置标题
		evnetsTitleTV = (TextView) view.findViewById(R.id.eventsTitle_Txtview);
//		if (item.title.equals("sick")) {
//			evnetsTitleTV.setText(mContext.getResources().getString(
//					R.string.leave_type_sick));
//		} else {
//			evnetsTitleTV.setText(mContext.getResources().getString(
//					R.string.leave_type_casual));
//		}
		evnetsTitleTV.setText(item.title);
		
		tp = evnetsTitleTV.getPaint();
		tp.setStrikeThruText(false);
		
		//描述(发布人？)
		evnetsSummaryTV = (TextView) view.findViewById(R.id.eventsSummary_Txtview);
		evnetsSummaryTV.setText(item.pubname);
		
		//发布时间&回复时间
		evnetsDateTV = (TextView) view.findViewById(R.id.eventsDate_Txtview);
		String time = "";
		time = time + mContext.getString(R.string.post) + (DateFormatter.getStringYYYYMMDD(item.post).equals(DateFormatter.getStringYYYYMMDD(today))?DateFormatter.getStringHHmm(item.post):DateFormatter.getStringYYYYMMDD(item.post));
		evnetsDateTV.setText(time);
		
		//未读标识
		mImgview_unreadmark = (ImageView)view.findViewById(R.id.Imgview_unreadmark);
		//已读,0是未读，1是已读
		//判断是是学生登陆，如果是，则不显示未读标识
		if (!isTeacherLogin()) {
				item.isread = 0;
			}
		if(item.isread == 1){
			evnetsTitleTV.setTextColor(mContext.getResources().getColor(R.color.light_gray));
			evnetsSummaryTV.setTextColor(mContext.getResources().getColor(R.color.light_gray));
			evnetsDateTV.setTextColor(mContext.getResources().getColor(R.color.light_gray));
			mImgview_unreadmark.setVisibility(View.GONE);
		}else{
			evnetsTitleTV.setTextColor(mContext.getResources().getColor(R.color.listtitletxt));
			evnetsSummaryTV.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
			evnetsDateTV.setTextColor(mContext.getResources().getColor(R.color.orange));
			mImgview_unreadmark.setVisibility(View.VISIBLE);
			if (!isTeacherLogin()) {
				mImgview_unreadmark.setVisibility(View.GONE);
			}
		}
		thumb_imgview = (ImageView) view.findViewById(R.id.newsRowIV);
			thumb_imgview.setVisibility(View.GONE);
			mLinearLayoutIV.setVisibility(View.GONE);
	}
	
	/**
	 * 设置列表ICON是否可视
	 */
	private boolean isListiconVisiable(int type){
		switch (type) {
		case RequestCategoryID.EVENTS_NEWS_ID:
		case RequestCategoryID.EVENTS_COMMENT_ID:
		case RequestCategoryID.EVENTS_COURSEWARE_ID:
			return false;

		default:
			return true;
		}
	}
	
	private boolean isTeacherLogin(){
		SettingManager settingInstance = SettingManager
		.getSettingManager(mContext);
if (settingInstance.getCurrentUserInfo() != null) {
	if (!settingInstance.getCurrentUserInfo().strUsrType
			.equals(UserInfo.USERTYPE_TEACHER)) {
		return false;
	}else{
		return true;
	}
}return false;
	}

}
