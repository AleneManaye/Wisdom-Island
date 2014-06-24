package com.drcom.drpalm.Activitys.shares;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import com.drcom.drpalm.View.controls.PageControlView;
import com.drcom.drpalm.View.share.SelectionPageAdapter;
import com.drcom.drpalm.objs.ShareToFriendItem;
import com.drcom.drpalm4tianzhujiao.R;

public class ShareToFriendViewHelper {
	private Button cancel;
	private SlidingDrawer tellFriendDrawaer;// 抽屉
	private List<ShareToFriendItem> moreList;// 数据
	private PageControlView myPageControl;// pageView分页控件
	private FrameLayout viewPageContainer;
	private SelectionPageAdapter mSelectionPageAdapter;
	private ViewPager myViewPager;
	private List<View> mPageViews = new ArrayList<View>();
	private Context mContext;
	private View mLayoutSharemanage;
	private String subject, title;// 分享的主题和内容
	private LinearLayout toolbar;

	// 这个类是用于生成抽屉中带有各种分享（微博，微信等）的界面的工具类，zhr
	public ShareToFriendViewHelper(View mLayoutSharemanage,
			SlidingDrawer tellFriendDrawaer, List<ShareToFriendItem> moreList,
			Context mcContext, String subject, String title,LinearLayout toolbar) {

		this.tellFriendDrawaer = tellFriendDrawaer;
		this.moreList = moreList;
		this.mContext = mcContext;
		this.mLayoutSharemanage = mLayoutSharemanage;
		this.subject = subject;
		this.title = title;
		this.cancel = (Button) this.tellFriendDrawaer.findViewById(R.id.cancel);
		this.viewPageContainer = (FrameLayout) this.tellFriendDrawaer
				.findViewById(R.id.viewpager);
		this.myPageControl = (PageControlView) this.tellFriendDrawaer
				.findViewById(R.id.more_pageControl);
		this.toolbar = toolbar;
		init();
	}

	private void init() {
		tellFriendDrawaer.findViewById(R.id.module_content).setOnTouchListener(
				new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						return true;
					}
				});
		int groupNum = 0, moreListNum = moreList.size();
		while (moreListNum >= 0) {
			moreListNum = moreListNum - 6;
			groupNum++;
		}
		myViewPager = new ViewPager(mContext);
		int k = 0;
		int max = 0;
		for (int i = 0; i < groupNum; i++) {
			List<ShareToFriendItem> items = new ArrayList<ShareToFriendItem>();
			if (i < groupNum - 1) {
				max = k + 6;
			} else if (i == groupNum - 1) {
				max = moreList.size();
			}
			while (k < max) {
				items.add(moreList.get(k));
				k++;
			}
			ShareChildView viewChild = new ShareChildView(mContext, items,
					subject, title);
			mPageViews.add(viewChild);
		}

		viewPageContainer.addView(myViewPager);
		mSelectionPageAdapter = new SelectionPageAdapter(mPageViews);
		myViewPager.setAdapter(mSelectionPageAdapter);
		myPageControl.setCount(mPageViews.size());
		myPageControl.generatePageControl(0);
		myViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				myPageControl.generatePageControl(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		mSelectionPageAdapter.notifyDataSetChanged();
		mLayoutSharemanage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!tellFriendDrawaer.isOpened()) {
					tellFriendDrawaer.animateOpen();
					if(toolbar!=null){
						toolbar.setVisibility(LinearLayout.GONE);
					}
				}
			}
		});
		// "告诉朋友"中的取消按钮
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tellFriendDrawaer.animateClose();
				if(toolbar!=null){
					toolbar.setVisibility(LinearLayout.VISIBLE);
				}
			}
		});

	}

}
