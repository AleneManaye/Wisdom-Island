package com.drcom.drpalm.Activitys.main;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.LeaveApplication.LeaveApplicationMainActivity;
import com.drcom.drpalm.Activitys.danceinfo.AttendanceActivity;
import com.drcom.drpalm.Activitys.events.EventsListActivity;
import com.drcom.drpalm.Activitys.events.NewEventActivity;
import com.drcom.drpalm.Activitys.events.album.ClassAlbumActivity;
import com.drcom.drpalm.Activitys.events.bookmark.EventsBookmarkListActivity;
import com.drcom.drpalm.Activitys.events.draft.EventsDraftListActivity;
import com.drcom.drpalm.Activitys.events.face2face.MemberListActivity;
import com.drcom.drpalm.Activitys.events.search.EventsSearchListActivity;
import com.drcom.drpalm.Activitys.events.sent.EventsSentListActivity;
import com.drcom.drpalm.Activitys.events.video.ClassVideolistActivity;
import com.drcom.drpalm.Activitys.sysinfo.SysinfoListActivity;
import com.drcom.drpalm.Definition.ModuleNameDefine;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.Tool.ResourceManagement;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.View.controls.MyGridView;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.Block;
import com.drcom.drpalm.objs.MainMenuItem;
import com.drcom.drpalm.objs.UpdateTimeItem;
import com.drcom.drpalm4tianzhujiao.R;

public class ClassView extends LinearLayout {
	public static int CHANGE_SUM = 0;

	private MainAdapter mMainAdapter;
	private MainAdapter mMainAdapter2;

	private Context mContext;
	private List<MainMenuItem> mMyGVList1 = new ArrayList<MainMenuItem>();
	private List<MainMenuItem> mMyGVList2 = new ArrayList<MainMenuItem>();
	private int mSelectGVIndex = 1;		//选中哪个表格
	private int mSelectGVItemIndex = 0;		//选中表格中哪个位置的按钮
	private int language=0;//0=简体，1=繁体，2=英文
	public static String TITLE = "title";
//	private Handler mHandler;

	private MyGridView myGVgridview1;
	private MyGridView myGVgridview2;
	private RelativeLayout mRelativeLayoutMsg;
	private LinearLayout mLinearLayoutGird2;
	private RelativeLayout mLayout_top;
	private RelativeLayout mLayout_middle;
	
	private Animation animation;
	

	public ClassView(final Context context) {
		super(context);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.main_class_view, this);

		mRelativeLayoutMsg = (RelativeLayout) findViewById(R.id.Layout_middle);
		mLinearLayoutGird2 = (LinearLayout)findViewById(R.id.mainclass_Layout_gridview2);

		mMainAdapter = new MainAdapter(context, mMyGVList1);
		mMainAdapter2 = new MainAdapter(context, mMyGVList2);
		
		mLayout_top = (RelativeLayout)findViewById(R.id.Layout_top);
		if(LanguageManagement.getSysLanguage(context) == CurrentLan.COMPLES_CHINESE){
			mLayout_top.setBackgroundResource(R.drawable.class_msgtitle_1_hk);
		}else if(LanguageManagement.getSysLanguage(context) == CurrentLan.ENGLISH){
			mLayout_top.setBackgroundResource(R.drawable.class_msgtitle_1_en);
		}
		mLayout_middle = (RelativeLayout)findViewById(R.id.Layout_middle);
		if(LanguageManagement.getSysLanguage(context) == CurrentLan.COMPLES_CHINESE){
			mLayout_middle.setBackgroundResource(R.drawable.class_msgtitle_2_hk);
		}else if(LanguageManagement.getSysLanguage(context) == CurrentLan.ENGLISH){
			mLayout_middle.setBackgroundResource(R.drawable.class_msgtitle_2_en);
		}

		animation = AnimationUtils.loadAnimation(context, R.anim.btn_scale);
		animation.setAnimationListener(new mAnimationListenerGL());
		
		myGVgridview1 = (MyGridView) findViewById(R.id.mainclass_gridview1);
		// 添加元素给gridview
		myGVgridview1.setAdapter(mMainAdapter);
		myGVgridview1.setSelector(new ColorDrawable(Color.TRANSPARENT));
		myGVgridview1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
				mSelectGVIndex = 1;
				mSelectGVItemIndex = arg2;
				
				v.startAnimation(animation);
			}
		});

		myGVgridview2 = (MyGridView) findViewById(R.id.mainclass_gridview2);
		// 添加元素给gridview
		myGVgridview2.setAdapter(mMainAdapter2);
		myGVgridview2.setSelector(new ColorDrawable(Color.TRANSPARENT));
		myGVgridview2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
				mSelectGVIndex = 2;
				mSelectGVItemIndex = arg2;
				
				v.startAnimation(animation);
			}
		});

		//新通告
		Button button_newevent = (Button) findViewById(R.id.button_newevent);
		button_newevent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, NewEventActivity.class);
				context.startActivity(i);
			}
		});

		//查看收藏
		Button button_bookmark = (Button) findViewById(R.id.buttonBookmark);
		button_bookmark.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, EventsBookmarkListActivity.class);
				context.startActivity(i);
			}
		});

		//搜索通告
		Button button_search = (Button) findViewById(R.id.buttonSearch);
		button_search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, EventsSearchListActivity.class);
				context.startActivity(i);
			}
		});

		initData();

//		ReflashUI();
		mRelativeLayoutMsg.setVisibility(View.GONE);
		mLinearLayoutGird2.setVisibility(View.GONE);
	}

	/**
	 * 初始化 按钮数据
	 */
	public void initData() {
		mMyGVList1.clear();
		mMyGVList2.clear();
		if (LanguageManagement.getSysLanguage(mContext) == CurrentLan.COMPLES_CHINESE) {

			language = 1;

		} else if (LanguageManagement.getSysLanguage(mContext) == CurrentLan.ENGLISH) {

			language = 2;

		} else {
			language = 0;

		}
		
		// 按钮顺序
		for (Block iterable_element : GlobalVariables.blocks) {
			//上面的图标
			if ("class".equals(iterable_element.getType())) {
				MainMenuItem item = new MainMenuItem();
				item.setId(iterable_element.getId());
				item.setCount("0");

				String name;
				if(language==0){
					name = iterable_element.getNameChsString();
				}else if(language == 1){
					name = iterable_element.getNameChtString();
				}else{
					name = iterable_element.getNameEnString();
				}
				if (name.equals("")) {
					// 如果取不到
					item.setName(name);
				} else {
					item.setName(name);
				}

				item.setmBitmap(MyMothod.getIcon(mContext, iterable_element.getIconString()));
				item.setShow(iterable_element.isVisible());
				Log.i("zhr", item.isShow() + " type:"+iterable_element.getType());
				if (item.isShow()) {
					mMyGVList1.add(item);
				}
				continue;
			}
			//下面的图标
			if("classt".equals(iterable_element.getType())){
				MainMenuItem item = new MainMenuItem();
				item.setId(iterable_element.getId());
				item.setCount("0");

				String name;
				if(language==0){
					name = iterable_element.getNameChsString();
				}else if(language == 1){
					name = iterable_element.getNameChtString();
				}else{
					name = iterable_element.getNameEnString();
				}
				if (name.equals("")) {
					// 如果取不到
					item.setName(name);
				} else {
					item.setName(name);
				}

				item.setmBitmap(MyMothod.getIcon(mContext, iterable_element.getIconString()));
				item.setShow(iterable_element.isVisible());
				Log.i("zhr", item.isShow() + " type:"+iterable_element.getType());
				if (item.isShow()) {
					mMyGVList2.add(item);
				}
				continue;
			}
		}
		
	}

	/**
	 * 由权限判断界面显示内容
	 */
	public void ReflashUI() {
		SettingManager settingInstance = SettingManager.getSettingManager(mContext);
		if(settingInstance.getCurrentUserInfo()!=null){
			if (!settingInstance.getCurrentUserInfo().strUsrType.equals(UserInfo.USERTYPE_TEACHER)) {
				mRelativeLayoutMsg.setVisibility(View.GONE);
				mLinearLayoutGird2.setVisibility(View.GONE);
			} else {
				mRelativeLayoutMsg.setVisibility(View.VISIBLE);
				mLinearLayoutGird2.setVisibility(View.VISIBLE);
			}
		}
	}

	
	
	/**
	 * 刷新各按钮的数字状态
	 *
	 * @param ids
	 */
	public void ReflashItemstatus(List<UpdateTimeItem> ids) {
		for (int i = 0; i < mMyGVList1.size(); i++) {
			for (int j = 0; j < ids.size(); j++) {
				if (mMyGVList1.get(i).getId() == ids.get(j).update_time_channel) {
					mMyGVList1.get(i).setCount(ids.get(j).update_unreadcount + "");
				}
			}
		}

		for (int i = 0; i < mMyGVList2.size(); i++) {
			for (int j = 0; j < ids.size(); j++) {
				if (mMyGVList2.get(i).getId() == ids.get(j).update_time_channel) {
					mMyGVList2.get(i).setCount(ids.get(j).update_unreadcount + "");
				}
			}
		}

		mMainAdapter.notifyDataSetChanged();
		mMainAdapter2.notifyDataSetChanged();
	}
	
	/**
	 * 9宫格动画响应
	 * @author zhaojunjie
	 *
	 */
	private class mAnimationListenerGL implements AnimationListener {
		
		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			if (mSelectGVIndex == 1) {
				Intent intent = new Intent();
				;
				// MainMenuItem item = (MainMenuItem)
				// arg0.getItemAtPosition(arg2);
				MainMenuItem item = mMyGVList1.get(mSelectGVItemIndex);

				intent.putExtra(TITLE, item.getName());
				if (item.getId() == RequestCategoryID.EVENTS_NEWS_ID) {

					intent.setClass(mContext, EventsListActivity.class);
					intent.putExtra(EventsListActivity.KEY_CATEGORYID,
							RequestCategoryID.EVENTS_NEWS_ID);
					mContext.startActivity(intent);
				} else if (item.getId() == RequestCategoryID.EVENTS_ACTIVITY_ID) {

					intent.setClass(mContext, EventsListActivity.class);
					intent.putExtra(EventsListActivity.KEY_CATEGORYID,
							RequestCategoryID.EVENTS_ACTIVITY_ID);
					mContext.startActivity(intent);
				} else if (item.getId() == RequestCategoryID.EVENTS_COMMENT_ID) {

					intent.setClass(mContext, EventsListActivity.class);
					intent.putExtra(EventsListActivity.KEY_CATEGORYID,
							RequestCategoryID.EVENTS_COMMENT_ID);
					mContext.startActivity(intent);
				} else if (item.getId() == RequestCategoryID.EVENTS_ALBUM_ID) {

					intent.setClass(mContext, ClassAlbumActivity.class);
					intent.putExtra(ClassAlbumActivity.CATEGORYID_KEY,
							RequestCategoryID.EVENTS_ALBUM_ID);
					mContext.startActivity(intent);
				} else if (item.getId() == RequestCategoryID.EVENTS_COURSEWARE_ID) {

					intent.setClass(mContext, EventsListActivity.class);
					intent.putExtra(EventsListActivity.KEY_CATEGORYID,
							RequestCategoryID.EVENTS_COURSEWARE_ID);
					mContext.startActivity(intent);
				} else if (item.getId() == RequestCategoryID.EVENTS_COMMUNION_ID) {

					intent.setClass(mContext, MemberListActivity.class);
					mContext.startActivity(intent);
				} else if (item.getId() == RequestCategoryID.EVENTS_VIDEO_ID) {

					intent.setClass(mContext, ClassVideolistActivity.class);
					intent.putExtra(ClassVideolistActivity.CATEGORYID_KEY,
							RequestCategoryID.EVENTS_VIDEO_ID);
					mContext.startActivity(intent);
				} else if (item.getId() == RequestCategoryID.EVENTS_INOUT_ID) {
					// showExitMessage(mContext.getResources().getString(R.string.nofunction));
					if (GlobalVariables.gAttendanceUrl.equals("")) {
						Toast.makeText(
								mContext,
								mContext.getResources().getString(
										R.string.nofunction), Toast.LENGTH_LONG)
								.show();
					} else {

						intent.setClass(mContext, AttendanceActivity.class);
						mContext.startActivity(intent);
					}
				} else if (item.getId() == RequestCategoryID.SYSINFO_ID) {

					intent.setClass(mContext, SysinfoListActivity.class);
					mContext.startActivity(intent);
				} else if (item.getId() == RequestCategoryID.EVENTS_ASK4LEAVE_ID) {

					intent.setClass(mContext,
							LeaveApplicationMainActivity.class);
					mContext.startActivity(intent);
				}
			} else if (mSelectGVIndex == 2) {
				// MainMenuItem item = (MainMenuItem)
				// arg0.getItemAtPosition(arg2);
				MainMenuItem item = mMyGVList2.get(mSelectGVItemIndex);
				Intent intent = new Intent();
				intent.putExtra(TITLE, item.getName());
				if (item.getId() == RequestCategoryID.EVENTS_SEND_ID) {

					intent.setClass(mContext, EventsSentListActivity.class);
					mContext.startActivity(intent);
				} else if (item.getId() == RequestCategoryID.EVENTS_DRAFT_ID) {

					intent.setClass(mContext, EventsDraftListActivity.class);
					mContext.startActivity(intent);
				}
			}
		}
	};
}
