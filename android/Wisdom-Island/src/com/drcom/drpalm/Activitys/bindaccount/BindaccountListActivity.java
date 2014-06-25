package com.drcom.drpalm.Activitys.bindaccount;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SlidingDrawer;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.DB.NavigationDB;
import com.drcom.drpalm.Tool.DownloadProgressUtils;
import com.drcom.drpalm.View.bindaccount.BindaccountListManagement;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.controls.cache.ImageLoader;
import com.drcom.drpalm.objs.BindaccountItem;
import com.drcom.drpalm.objs.BindaccountResultItem;
import com.wisdom.island.R;

public class BindaccountListActivity extends ModuleActivity implements OnClickListener{
	private SlidingDrawer moduleDrawer;
	private Button mButtonAdd, mButtonDel, mButtonCancel;
	private ListView mListView;
	private NavigationDB mNavigationDb;
	private BindaccountAdapter mBindaccountAdapter;
	private List<BindaccountItem> mList, mDelList,mICPInfoList,mInfoList;
	private Button mbtnAdd;// 标题栏的编辑按钮
	private Handler mHandler;

	public final static int FAIL = 0;// 获取失败
	public final static int SUCCESS = 1;// 获取账号关联列表返回1
	public final static int ADDSUCCESS = 2;// 添加账号关联2
	public final static int DElSUCCESS = 3;// 删除账号关联3
	public final static int ADAPTER_ARG1 = 4;// adapter传数据（删除位置）过来的flag
	public final static int SUCCESSINFO = 5;//请求账号详情
	private static final int REQUESTCODE = 10;
	
	private int index = 0;//获取账号详情的item标识
	private String tokenid = GlobalVariables.Devicdid;
	private String appid = String.valueOf(GlobalVariables.getAgentID());
	
	private BindaccountListManagement mbindaccountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.bindaccountlist_view, mLayout_body);
		
		mNavigationDb = NavigationDB.getInstance(this);
		mbindaccountManager = new BindaccountListManagement(this);
		mList = new ArrayList<BindaccountItem>();
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				switch (msg.arg1) {
				case SUCCESS:
					mICPInfoList = ((BindaccountResultItem)msg.obj).mBindaccountItem;
					mInfoList = new ArrayList<BindaccountItem>();
					//获取详情
					getaccountInfo();
					//⑤读库
					mList.clear();
					mList.addAll(mNavigationDb.getAccountItem());
					mBindaccountAdapter.notifyDataSetChanged();
					break;
				case SUCCESSINFO:
					//获取详情
					getaccountInfo();
					mList.clear();
					mList.addAll(mNavigationDb.getAccountItem());
					mBindaccountAdapter.notifyDataSetChanged();
					break;
				case DElSUCCESS://请求删除账号关联成功后清除表数据
					for(int i =0;i<mDelList.size();i++){
						mNavigationDb.deleteBindaccount(mDelList.get(i).pub_account);
					}
					mbtnAdd.setText(R.string.edit);
					mBindaccountAdapter.mBtn_flag = false;
					mBindaccountAdapter.notifyDataSetChanged();
					HideLoadingDialog();
					break;
				case ADAPTER_ARG1://移除adapter的item，并未删除库数据
					int position = msg.what;
					mDelList.add(mList.get(position));
					mList.remove(position);
					mBindaccountAdapter.notifyDataSetChanged();
					break;
				case FAIL:
					Log.i("hzy", "请求失败");
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};

		//①读库
		mList.clear();
		mList.addAll(mNavigationDb.getAccountItem());
		initView();
		//②从服务器获取账号关联列表
		mbindaccountManager.getaccountsList(appid,tokenid,mHandler);
		initTitlebar();
		hideToolbar();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		HideLoadingDialog();
		
		super.onStop();
	}
	
	private void initView(){
		moduleDrawer = (SlidingDrawer) findViewById(R.id.module_drawer);// 抽屉组件
		
		mButtonAdd = (Button) findViewById(R.id.addDrawerButton);
		mButtonAdd.setOnClickListener(this);
		
		mButtonDel = (Button) findViewById(R.id.delDrawerButton);
		mButtonDel.setOnClickListener(this);
		
		mButtonCancel = (Button) findViewById(R.id.cancelDrawerButton);
		mButtonCancel.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.bindaccount_listview);
		mBindaccountAdapter = new BindaccountAdapter(this, mList,
				R.layout.bindaccount_item, getmClassImageLoader(), mHandler); 
		mListView.setAdapter(mBindaccountAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				ShowLoadingDialog();
				
				BindaccountItem item = mList.get(position);
				mbindaccountManager.onListviewItemClick(item);
			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.addDrawerButton:
			// 增加帐号关联界面
			Intent i = new Intent(BindaccountListActivity.this,
					NewbindaccountActivity.class);
			startActivityForResult(i, REQUESTCODE);
			closeDrawer();
			break;
		case R.id.delDrawerButton:
			mBindaccountAdapter.mBtn_flag = true;
			mBindaccountAdapter.notifyDataSetChanged();
			
			closeDrawer();
			mbtnAdd.setText(R.string.saveimagefile);
			break;
		case R.id.cancelDrawerButton:
			closeDrawer();
			break;

		default:
			break;
		}
	}
	// add by hzy 获取图片缓存，改变保存目录为class
	public ImageLoader getmClassImageLoader() {
		Log.i("xpf", "xpf getClaass");
		return DownloadProgressUtils.getmClassImageLoader();
	}

	// 初始化按钮 添加了一个导航编辑按钮
	private void initTitlebar() {
		setTitleLogo(BitmapFactory.decodeResource(getResources(),
				R.drawable.defaulttitlelogo));

		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this,
				GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this,
				GlobalVariables.btnHeight_Titlebar));

		mDelList = new ArrayList<BindaccountItem>();
		// 添加一个编辑按钮
		mbtnAdd = new Button(this);
		mbtnAdd.setLayoutParams(p);
		mbtnAdd.setBackgroundResource(R.drawable.btn_title_blue_selector);
		mbtnAdd.setText(getString(R.string.edit));
		mbtnAdd.setTextAppearance(BindaccountListActivity.this,
				R.style.TitleBtnText);
		mbtnAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String btn_text = mbtnAdd.getText().toString();
				String edit_Text = BindaccountListActivity.this
						.getResources().getString(R.string.edit);//"编辑"
				
				if (btn_text.equals(edit_Text)) {
					openDrawer();
				} else {
					//保存--->删除账号关联请求
					mbindaccountManager.deleteAccountRelation(mDelList, mHandler);
					ShowLoadingDialog();
				}
			}
		});
		setTitleRightButton(mbtnAdd);
	}
	//获取账号详情
	private void getaccountInfo(){
		if(index < mICPInfoList.size()){
			mInfoList.clear();
			mInfoList.add(mICPInfoList.get(index));
			String domain = "http://" + mICPInfoList.get(index).gwip + ":" + mICPInfoList.get(index).gwport;
			mbindaccountManager.getaccountInfo(domain,GlobalVariables.Devicdid, mInfoList, mHandler);
			index++;
		}
		
	}
	//添加关联后返回列表界面重新加载一次数据
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case NewbindaccountActivity.RESULTCODE:
			//②从服务器获取账号关联列表
			mbindaccountManager.getaccountsList(appid,tokenid,mHandler);
			index = 0;
			Log.i("hzy", "resultCode---");
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 打开抽屉
	 */
	public void openDrawer() {
		if (!moduleDrawer.isOpened()) {
			moduleDrawer.animateOpen();
		}
	}

	/**
	 * 关闭底部抽屉
	 * 
	 */
	public void closeDrawer() {
		if (moduleDrawer.isOpened()) {
			moduleDrawer.animateClose();
		}
	}

}
