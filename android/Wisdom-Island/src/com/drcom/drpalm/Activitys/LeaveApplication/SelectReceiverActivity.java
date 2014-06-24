package com.drcom.drpalm.Activitys.LeaveApplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.mOrganization.StateActivity;
import com.drcom.drpalm.View.LeaveApplication.SelectReceiverManager;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.objs.ReceiverItem;
import com.drcom.drpalm.objs.ReceiverResultItem;
import com.drcom.drpalm4tianzhujiao.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/5/9.
 */
public class SelectReceiverActivity extends ModuleActivity implements
		View.OnClickListener {
	private Context mContext;
	private CheckBox cbSelectAll;
	private boolean selectAll;
	private ListView listView;
	private BaseAdapter listViewAdapter;

	public final static int SUCCESS = 0x1;

	public final static int FAIL = 0x2;

	public boolean firstEnter = true;

	private SelectReceiverManager selectReceiverManager;

	private ArrayList<ReceiverItem> mReceivers = new ArrayList<ReceiverItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.select_receiver_main, mLayout_body);
		
		addbtnOk();
		hideToolbar();
        setTitleText(getString(R.string.choosecontacts));

		mContext = this;
		selectReceiverManager = new SelectReceiverManager(mContext);
		findByIds();
		initViews();

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("result", "result---------------");
		if (firstEnter) {
			showProgressBar();
			selectReceiverManager.getReceiverList(handler);
		}		
	}
	/**
	 * 初始化控件
	 */
	public void findByIds() {
		cbSelectAll = (CheckBox) findViewById(R.id.cb_select_all);
		listView = (ListView) findViewById(R.id.receiver_list);
	}
	
	/**
	 * 重写键盘监听
	 */
	@Override
	public boolean onKeyDown(int keyCoder, KeyEvent event) {

		switch (keyCoder) {
		case KeyEvent.KEYCODE_BACK:
			selectReceiverManager.selected(mReceivers);
			finishDraw();
			break;
		}

		return super.onKeyDown(keyCoder, event);
	}


	/**
	 * 创建一个导航Ok按钮
	 */
	public void addbtnOk() {
		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this,
				GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this,
				GlobalVariables.btnHeight_Titlebar));
		// 发送
		Button btnOK;
		btnOK = new Button(this);
		btnOK.setBackgroundResource(R.drawable.btn_title_blue_selector);
		btnOK.setText(getString(R.string.OK));
		btnOK.setTextAppearance(this, R.style.TitleBtnText);
		btnOK.setLayoutParams(p);

		/**
		 * 全选checkbox的监听事件
		 */
		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectReceiverManager.selected(mReceivers);
				finishDraw();
			}
		});
		setTitleRightButton(btnOK);
	}

	/**
	 * 设置控件监听事件
	 */
	public void initViews() {
		cbSelectAll.setOnClickListener(this);
		cbSelectAll.setEnabled(false);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			hideProgressBar();
			switch (msg.arg1) {
			case SUCCESS:
				mReceivers = ((ReceiverResultItem) msg.obj).mReceiver;

				listViewAdapter = new ReceiverListAdapter(mReceivers,
						SelectReceiverActivity.this);
				listView.setAdapter(listViewAdapter);
//				listViewAdapter.notifyDataSetChanged();
				
				cbSelectAll.setEnabled(true);
				
				boolean b = selectReceiverManager.lastTimeIschecked(mReceivers);
				cbSelectAll.setChecked(b);
				listViewAdapter.notifyDataSetChanged();
				
				break;
			case FAIL:
				Toast.makeText(SelectReceiverActivity.this,
						R.string.download_faile, Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	/**
	 * 全选checkbox的监听事件
	 */
	@Override
	public void onClick(View v) {

		for (int i = 0; i < listView.getCount(); i++) {
			View view = listView.getChildAt(i);
			if (view != null) {
				CheckBox checkBox = (CheckBox) view
						.findViewById(R.id.receiver_info);
				checkBox.setChecked(cbSelectAll.isChecked());
				if (cbSelectAll.isChecked()) {
					mReceivers.get(i).ischecked = true;
				}else {
					mReceivers.get(i).ischecked =false;
				}
			}
		}
	}

}
