package com.drcom.drpalm.Activitys.mOrganization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.ModuleActivity;
import com.drcom.drpalm.Activitys.mOrganization.StateAdapter.OnOrganizationCheckedListener;
import com.drcom.drpalm.Activitys.main.MainActivity;
import com.drcom.drpalm.DB.OrganizationDB;
import com.drcom.drpalm.Tool.Encryption;
import com.drcom.drpalm.View.controls.MyMothod;
import com.drcom.drpalm.View.mOrganization.FetchSchoolOrganization;
import com.drcom.drpalm.View.mOrganization.StateActivityManagement;
import com.drcom.drpalm.View.notification.ErrorNotificatin;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.OrganizationItem;
import com.drcom.drpalm4tianzhujiao.R;

public class StateActivity extends ModuleActivity {
	public static int RESULT_CODE = 2103;
	public static String RESULT_CODE_KEY = "RESULT_CODE_KEY";
	public static String ORG_LIST_KEY = "ORG_LIST_KEY";

	private List<OrganizationItem> data, getResult;
	public static List<OrganizationItem> all;
	private ListView listView;
	private CheckBox selectall;
	private CheckBox selectself;
	private StateAdapter mAdapter;
	private ProgressBar state_progressBar;
	private int selfId = -1;

	public static final int FETCH_SUCCESSFUL = 1;
	public static final int FETCH_FAILED = 2;
	
	private StateActivityManagement mStateActivityManagement;

	// private ResourceManagement mResourceManagement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.school_state, mLayout_body);
		selectall = (CheckBox) findViewById(R.id.org_selectall);
		selectself = (CheckBox) findViewById(R.id.org_selectself);
		listView = (ListView) findViewById(R.id.stateListView);
		state_progressBar = (ProgressBar) findViewById(R.id.state_progressBar);
		
		mStateActivityManagement = new StateActivityManagement(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey(ORG_LIST_KEY)) {
				getResult = extras.getParcelableArrayList(ORG_LIST_KEY);
			}
		}

		all = new ArrayList<OrganizationItem>();
		data = new ArrayList<OrganizationItem>();
		mAdapter = new StateAdapter(this, data);
		mAdapter.setOnOrganizationCheckedListener(new OnOrganizationCheckedListener() {
			
			@Override
			public void onClick(int orgID, int state) {
				// TODO Auto-generated method stub
				for (int i = 0; i < StateActivity.all.size(); i++) {
					if (orgID == all.get(i).orgID) {
						mStateActivityManagement.saveData(all.get(i), state);
					}
				}
			}
		});
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (mStateActivityManagement.getData(data.get(position).orgID).size() > 0) {
					List<OrganizationItem> idata = mStateActivityManagement.getData(data.get(position).orgID);
					//重新初始化数据，否则无法刷新界面
					if(idata.size()>0){
						data.clear();
						for(int i=0;i<idata.size();i++){
							data.add(idata.get(i));
						}
					}
					mAdapter.notifyDataSetChanged();
				}
			}
		});

		initTitlebar();
		hideToolbar();
		showProgress();
		downLoadData();

		// 全选
		selectall.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mStateActivityManagement.selectAllResponse(data, isChecked);
				mAdapter.notifyDataSetChanged();
				
				selectself.setChecked(isChecked);
			}
		});
		// 自己
		selectself.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mStateActivityManagement.selectSelfResponse(selfId, isChecked);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	private void initTitlebar() {
		setTitlebarBgColor(getResources().getColor(R.color.bgblue));

		LayoutParams p = new LayoutParams(MyMothod.Dp2Px(this, GlobalVariables.btnWidth_Titlebar), MyMothod.Dp2Px(this, GlobalVariables.btnHeight_Titlebar));
		p.setMargins(MyMothod.Dp2Px(this, 3), 0, MyMothod.Dp2Px(this, 3), 0);

		// 取消
		Button btnCancel = new Button(this);
		btnCancel.setLayoutParams(p);
		btnCancel.setBackgroundResource(R.drawable.btn_title_blue_selector);
		btnCancel.setText(getString(R.string.cancel));
		btnCancel.setTextAppearance(StateActivity.this, R.style.TitleBtnText);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finishDraw();
			}
		});

		setTitleRightButton(btnCancel);

		// 保存
		Button btnSave = new Button(this);
		btnSave.setLayoutParams(p);
		btnSave.setBackgroundResource(R.drawable.btn_title_blue_selector);
		btnSave.setText(getString(R.string.saveimagefile));
		btnSave.setTextAppearance(StateActivity.this, R.style.TitleBtnText);
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				List<OrganizationItem> result = new ArrayList<OrganizationItem>();
				result = mStateActivityManagement.getSelectedBackData(0);
				Intent intent = new Intent();
				intent.putParcelableArrayListExtra(RESULT_CODE_KEY, (ArrayList<? extends Parcelable>) result);
				setResult(RESULT_CODE, intent);
				finishDraw();
			}
		});

		setTitleRightButton(btnSave);

		// 返回按钮
		SetBackBtnOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (data.size()>0 && data.get(0).orgPid == 0)
					finishDraw();
				else
					Back();
			}
		});
		
		setTitleText(getString(R.string.choosecontacts));
	}

	// MD5文件完整性验证
//	public boolean confirmFileCheckSum(File file, String checkSum) {
//		// String filePath = GlobalVariables.getResourceZipDirectory() + "/" +
//		// fileName;
//		String fileSum = Encryption.toMd5(file);
//		return (fileSum.equals(checkSum)) ? true : false;
//	}

	// /**
	// * 拷贝文件到指定目录
	// *
	// * @param file
	// * @param newFileName
	// * @return
	// */
	// private File copyFileToResourceDirectory(File file, String newFileName) {
	// // GlobalVariables.gAppResource.getString(R.string.ResourceUpdateZip);
	// File newFile = null;
	// String newFileWholePath = GlobalVariables.getResourceZipDirectory() + '/'
	// + newFileName;
	// if (CommonFileManagent.copyFile(file.getAbsolutePath(), newFileWholePath,
	// true)) {
	// newFile = new File(newFileWholePath);
	// }
	// return newFile;
	// }
	//
	// /**
	// * 构建节点路径
	// *
	// * @param list
	// * @return
	// */
	// private List<OrganizationItem> createPointPath(List<OrganizationItem>
	// list) {
	// List<OrgTraversalItem> casheList = new ArrayList<OrgTraversalItem>(); //
	// 存放层遍历所得节点的临时list
	// // 初始化遍历开始根节点
	// OrgTraversalItem item = new OrgTraversalItem();
	// item.orgId = 0;
	// casheList.add(item);
	// // 层遍历获取所有节点列表
	// for (int i = 0; i < casheList.size(); i++) {
	// List<OrgTraversalItem> subList = getChildrenList(casheList.get(i), list);
	// for (int j = 0; j < subList.size(); j++) {
	// casheList.add(subList.get(j));
	// }
	// }
	// // 遍历节点，添加路径项
	// for (int j = 0; j < list.size(); j++) {
	// list.get(j).orgPath = getPointPath(list.get(j), casheList);
	// }
	//
	// return list;
	// }
	//
	// /**
	// * 获取父节点的多有子节点list，以便顺序插入队列实现层遍历
	// *
	// * @param item
	// * 父节点信息
	// * @param list
	// * 服务器返回，文件读取的节点关系list
	// * @return
	// */
	// private List<OrgTraversalItem> getChildrenList(OrgTraversalItem item,
	// List<OrganizationItem> list) {
	// List<OrgTraversalItem> sublist = new ArrayList<OrgTraversalItem>();
	// for (int i = 0; i < list.size(); i++) {
	// if (list.get(i).orgPid == item.orgId) {
	// OrgTraversalItem traversalItem = new OrgTraversalItem();
	// traversalItem.orgId = list.get(i).orgID;
	// traversalItem.path = item.path + "/" + item.orgId;
	// sublist.add(traversalItem);
	// }
	// }
	// return sublist;
	// }
	//
	// /**
	// * 构建节点path信息
	// *
	// * @param item
	// * 需构建的节点信息
	// * @param list
	// * 层遍历构建的完整节点list
	// * @return
	// */
	// private String getPointPath(OrganizationItem item, List<OrgTraversalItem>
	// list) {
	// List<OrgTraversalItem> casheList = new ArrayList<OrgTraversalItem>();
	// String path = "";
	// // 处理一个及节点多个父亲，多路径问题
	// for (int i = 0; i < list.size(); i++) {
	// if (list.get(i).orgId == item.orgID) {
	// casheList.add(list.get(i));
	// }
	// }
	// path += casheList.get(0).path;
	// for (int j = 1; j < casheList.size(); j++) {
	// path += "," + casheList.get(j).path;
	// }
	// return path;
	// }
	//
	// /**
	// * 获取每个节点的子节点数目
	// *
	// * @param list
	// * 所有节点的list列表
	// * @return
	// */
	// private List<OrganizationItem> createChildrenCount(List<OrganizationItem>
	// list) {
	// List<OrganizationItem> cashelist = new ArrayList<OrganizationItem>();
	// for (int i = 0; i < list.size(); i++) {
	// if (list.get(i).orgType.equals("group")) {
	// int count = 0;
	// for (int j = 0; j < list.size(); j++) {
	// // 路径中包含orgid 且 为叶节点，则判断为当前组织的子叶节点
	// if (list.get(j).orgPath.contains(String.valueOf(list.get(i).orgID)) &&
	// !list.get(j).orgType.equals("group")) {
	// count++;
	// }
	// }
	// list.get(i).orgCount = count;
	// } else {
	// list.get(i).orgCount = 0;
	// }
	// }
	// return list;
	// }

	private void refreshUI() {
		all = mStateActivityManagement.getTopPoint();
		for (int i = 0; i < all.size(); i++) {// 初始化所有根结点的PID=0
			all.get(i).orgPid = 0;
		}
		for (int i = 0; i < all.size(); i++) {// 取根结点下的所有子结点，并添加到all中
			List<OrganizationItem> idata = new ArrayList<OrganizationItem>();
			idata = mStateActivityManagement.getChildPoint(all.get(i).orgID);
			for (int j = 0; j < idata.size(); j++) {
				all.add(idata.get(j));
			}
		}
		Log.i("xpf", "selfID" + GlobalVariables.gSelfOrgID);
		if (null != GlobalVariables.gSelfOrgID) {
			selfId = Integer.parseInt(GlobalVariables.gSelfOrgID);
		}
		if (getResult.size() > 0) {// 如果返回列表不为空，则设置当前状态
			for (int i = 0; i < getResult.size(); i++) {
				for (int j = 0; j < all.size(); j++) {// 设置选项及子选项状态
					if (getResult.get(i).orgID == all.get(j).orgID) {
						for (int k = 0; k < all.size(); k++) {
							if (all.get(j).orgID == all.get(k).orgID)
								mStateActivityManagement.saveData(all.get(j), 1);
						}
					}
				
					//有自己,选上"发送给自己"
					if(getResult.get(i).orgID == selfId) {
						selectself.setChecked(true);
					}
				}
			}
		}

		selectall.setClickable(true);
		selectself.setClickable(true);
		
		hideProgress();
		List<OrganizationItem> idata = mStateActivityManagement.getData(0);
		//重新初始化数据，否则无法刷新界面
		if(idata.size()>0){
			data.clear();
			for(int i=0;i<idata.size();i++){
				data.add(idata.get(i));
			}
		}
		mAdapter.notifyDataSetChanged();
//		data = mStateActivityManagement.getData(0);
//		mAdapter = new StateAdapter(StateActivity.this, data);
//		listView.setAdapter(mAdapter);
	}

	/**
	 * 返回上级菜单
	 */
	private void Back() {
		List<OrganizationItem> idata = mStateActivityManagement.getBackResponseList(data);
		//重新初始化数据，否则无法刷新界面
		if(idata.size()>0){
			data.clear();
			for(int i=0;i<idata.size();i++){
				data.add(idata.get(i));
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 取组织架构数据
	 */
	private void downLoadData() {
		FetchSchoolOrganization org = new FetchSchoolOrganization(StateActivity.this);
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.arg1 == FetchSchoolOrganization.FETCH_ORGANIZATION_SUCCESSFUL) {
					refreshUI();
				} else if (msg.arg1 == FetchSchoolOrganization.FETCH_ORGANIZATION_FAILED) {
					hideProgress();
					String strError = (msg.obj != null) ? (String) msg.obj : getString(R.string.album_downfail);
					new ErrorNotificatin(StateActivity.this).showErrorNotification(strError);
				}
			}
		};
		org.getOrganization(handler);

	}

	private void hideProgress() {
		state_progressBar.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
	}

	private void showProgress() {
		state_progressBar.setVisibility(View.VISIBLE);
		listView.setVisibility(View.GONE);
	}
}
