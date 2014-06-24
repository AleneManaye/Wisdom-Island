package com.drcom.drpalm.Activitys.main;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.consultation.ConsultationActivity;
import com.drcom.drpalm.Activitys.news.NewsActivity;
import com.drcom.drpalm.Activitys.news.album.AlbumActivity;
import com.drcom.drpalm.Activitys.news.bookmark.NewsBookmarkListActivity;
import com.drcom.drpalm.Activitys.news.search.NewsSearchListActivity;
import com.drcom.drpalm.Activitys.tours.ToursAcitivity;
import com.drcom.drpalm.Definition.RequestCategoryID;
import com.drcom.drpalm.Tool.LanguageManagement;
import com.drcom.drpalm.Tool.LanguageManagement.CurrentLan;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.objs.Block;
import com.drcom.drpalm.objs.MainMenuItem;
import com.drcom.drpalm4tianzhujiao.R;

public class SchoolView extends LinearLayout {
	private MainAdapter mMainAdapter;

	private TextView masterEmail;
	private RelativeLayout mLayout_top;
	private Animation animation;

//	private boolean showMasterEmail = false;
	private Button consultation;
	private List<MainMenuItem> mMyGVList = new ArrayList<MainMenuItem>();
	private Context mContext;
	private int mSelectGVItemIndex = 0;		//选中表格中哪个位置的按钮
	public static String TITLE = "title";
	public GridView myGVgridview;

	public SchoolView(final Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.main_school_view, this);

		mContext = context;
		masterEmail = (TextView) findViewById(R.id.masterEmail);
		mLayout_top = (RelativeLayout) findViewById(R.id.Layout_top);
		if (LanguageManagement.getSysLanguage(context) == CurrentLan.COMPLES_CHINESE) {
			mLayout_top.setBackgroundResource(R.drawable.school_msgtitle_1_hk);
		} else if (LanguageManagement.getSysLanguage(context) == CurrentLan.ENGLISH) {
			mLayout_top.setBackgroundResource(R.drawable.school_msgtitle_1_en);
		}

		consultation = (Button) findViewById(R.id.button3);
		consultation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ConsultationActivity.class);
				context.startActivity(intent);
			}
		});

		mMainAdapter = new MainAdapter(context, mMyGVList);
		
		animation = AnimationUtils.loadAnimation(context, R.anim.btn_scale);
		animation.setAnimationListener(new mAnimationListenerGL());

		myGVgridview = (GridView) findViewById(R.id.mainschool_gridview);
		// 添加元素给gridview
		myGVgridview.setAdapter(mMainAdapter);
		myGVgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		myGVgridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				
				mSelectGVItemIndex = arg2;
				v.startAnimation(animation);
				// item.setCount("0");
				// mMainAdapter.notifyDataSetChanged();
			}
		});

		Button button_bookmark = (Button) findViewById(R.id.buttonBookmark);
		button_bookmark.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, NewsBookmarkListActivity.class);
				context.startActivity(i);
			}
		});

		Button button_search = (Button) findViewById(R.id.buttonSearch);
		button_search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, NewsSearchListActivity.class);
				context.startActivity(i);
			}
		});

		initData();
	}

	public void RefreshUI(){
		initData();
		mMainAdapter.notifyDataSetChanged();
//		myGVgridview.setAdapter(mMainAdapter);
	}
	
	/**
	 * 初始化 按钮数据
	 */
	public void initData() {
		mMyGVList.clear();
		
		MainMenuItem mi1 = new MainMenuItem();
		mi1.setId(0);
		mi1.setCount("0");
		mi1.setName(getResources().getString(R.string.tours));
		mi1.setPicResId(R.drawable.icon_school_tours);
		mMyGVList.add(mi1);
		
		for (Block iterable_element : GlobalVariables.blocks) {

			if ("school".equals(iterable_element.getType()) && (RequestCategoryID.MASTEREMAIL_ID != iterable_element.getId())) {
				MainMenuItem item = new MainMenuItem();
				item.setId(iterable_element.getId());
				item.setCount("0");

				String name = "";
				
				if (LanguageManagement.getSysLanguage(mContext) == CurrentLan.SIMPLIFIED_CHINESE) {
					name = iterable_element.getNameChsString();
				}else if (LanguageManagement.getSysLanguage(mContext) == CurrentLan.COMPLES_CHINESE) {
					name = iterable_element.getNameChtString();
				}else {
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
				Log.i("zhr", item.isShow() + "");
				if (item.isShow()) {
					mMyGVList.add(item);
				}
				continue;

			} else if (RequestCategoryID.MASTEREMAIL_ID == iterable_element.getId()) {
				if (iterable_element.isVisible()) {
//					showMasterEmail = true;
					
					if (LanguageManagement.getSysLanguage(mContext) == CurrentLan.SIMPLIFIED_CHINESE) {
						masterEmail.setText(iterable_element.getNameChsString());
					}else if (LanguageManagement.getSysLanguage(mContext) == CurrentLan.COMPLES_CHINESE) {
						masterEmail.setText(iterable_element.getNameChtString());
					}else {
						masterEmail.setText(iterable_element.getNameEnString());
					}
					
					continue;
				}else{
					consultation.setVisibility(View.GONE);
					masterEmail.setVisibility(View.GONE);
				}
			}
		}

	}

	/**
	 * 刷新各按钮是否有NEW的状态
	 * 
	 * @param ids
	 */
	public void ReflashItemstatus(List<Integer> ids) {
		for (int i = 0; i < mMyGVList.size(); i++) {
			mMyGVList.get(i).setCount("0");
			
			for (int j = 0; j < ids.size(); j++) {
				if (mMyGVList.get(i).getId() == (int) ids.get(j)) {
					mMyGVList.get(i).setCount("New");
				}
			}
		}

		mMainAdapter.notifyDataSetChanged();
	}

	/**
	 * 刷新一个按钮是否有NEW的状态
	 * 
	 * @param ids
	 */
	public void ReflashItemstatus(int channleid, boolean isnew) {
		for (int i = 0; i < mMyGVList.size(); i++) {
			if (mMyGVList.get(i).getId() == channleid) {
				if (isnew)
					mMyGVList.get(i).setCount("New");
				else
					mMyGVList.get(i).setCount("0");
			}
		}

		mMainAdapter.notifyDataSetChanged();
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
			Intent intent = new Intent();
//			MainMenuItem item = (MainMenuItem) arg0.getItemAtPosition(arg2);
			MainMenuItem item = mMyGVList.get(mSelectGVItemIndex);
			intent.putExtra(TITLE, item.getName());
			if (item.getId() == 0) {
				// Tours
				intent.setClass(mContext, ToursAcitivity.class);
				mContext.startActivity(intent);
			} else if (item.getId() == RequestCategoryID.NEWS_NEWS_ID) {
				// 新闻列表3001
				intent.putExtra(NewsActivity.KEY_CATEGORY,
						RequestCategoryID.NEWS_NEWS_ID);
				intent.setClass(mContext, NewsActivity.class);
				mContext.startActivity(intent);
			} else if (item.getId() == RequestCategoryID.NEWS_ACTIVITY_ID) {
				// 通告列表1001
				intent.putExtra(NewsActivity.KEY_CATEGORY,
						RequestCategoryID.NEWS_ACTIVITY_ID);
				intent.setClass(mContext, NewsActivity.class);
				mContext.startActivity(intent);
			} else if (item.getId() == RequestCategoryID.NEWS_ALBUM_ID) {
				// 相册列表
				intent.setClass(mContext, AlbumActivity.class);
				mContext.startActivity(intent);
			} else if (item.getId() == RequestCategoryID.NEWS_INFANTDIET_ID) {
				// 食谱列表2001
				intent.putExtra(NewsActivity.KEY_CATEGORY,
						RequestCategoryID.NEWS_INFANTDIET_ID);
				intent.setClass(mContext, NewsActivity.class);
				mContext.startActivity(intent);
			} else if (item.getId() == RequestCategoryID.NEWS_PARENTING_ID) {
				// 育儿列表4001
				intent.putExtra(NewsActivity.KEY_CATEGORY,
						RequestCategoryID.NEWS_PARENTING_ID);
				intent.setClass(mContext, NewsActivity.class);
				mContext.startActivity(intent);
			}
		}
	};	
}
