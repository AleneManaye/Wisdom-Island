package com.drcom.drpalm.View.mOrganization;

import java.util.ArrayList;
import java.util.List;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Activitys.mOrganization.StateActivity;
import com.drcom.drpalm.DB.OrganizationDB;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.OrganizationItem;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

public class StateActivityManagement {
	
	private Context mContext;
//	private static StateActivityManagement mStateActivityManagement;
	private OrganizationDB mOrganizationDB;
	private List<OrganizationItem> result;
	private boolean isAddMyself2Result = false;	//是否要将自己加入返回的数据中(当自己是管理员时用到)
	private int mSelfId;	//我的ID
	private OrganizationItem myselfitem;
//	
//	public static StateActivityManagement getInstance(Context context){
//		if(mStateActivityManagement == null){
//			mStateActivityManagement = new StateActivityManagement(context);
//		}
//		return mStateActivityManagement;
//	}
	
	public StateActivityManagement(Context context){
		this.mContext = context;
		mOrganizationDB = OrganizationDB.getInstance(mContext, GlobalVariables.gSchoolKey);
		result = new ArrayList<OrganizationItem>();
	}
	
	/**
	 * 获取顶层节点列表
	 * @return
	 */
	public List<OrganizationItem> getTopPoint(){
		String mUsername = "";
		SettingManager setInstance = SettingManager.getSettingManager(mContext);
		mUsername = setInstance.getCurrentUserInfo().strUsrName;
		return mOrganizationDB.getTopPoint(mUsername);
	}
	
	/**
	 * 获取子节点列表
	 * @param parent_id 父节点
	 * @return
	 */
	public List<OrganizationItem> getChildPoint(int parent_id){
		return mOrganizationDB.getChildPoint(parent_id);
	}
	
	/**
	 * 获取指定父ID的子列表
	 * @param kind  父ID
	 * @return
	 */
	public List<OrganizationItem> getData(int kind) {
		List<OrganizationItem> idata = new ArrayList<OrganizationItem>();
		for (int i = 0; i < StateActivity.all.size(); i++) {
			if (kind == StateActivity.all.get(i).orgPid) {
				idata.add(StateActivity.all.get(i));
			}
		}
		return idata;
	}
	
	/**
	 * 全选数据处理
	 * @param idata 当前列表数据
	 * @param isChecked
	 */
	public void selectAllResponse(List<OrganizationItem> idata, boolean isChecked){
		if (isChecked) {
			for (int i = 0; i < idata.size(); i++) {
				for (int j = 0; j < StateActivity.all.size(); j++) {
					if (idata.get(i).orgID == StateActivity.all.get(j).orgID)
						saveData(StateActivity.all.get(j), 1);
				}
			}
		} else {
			for (int i = 0; i < idata.size(); i++) {
				for (int j = 0; j < StateActivity.all.size(); j++) {
					if (idata.get(i).orgID == StateActivity.all.get(j).orgID)
						saveData(StateActivity.all.get(j), 0);
				}
			}
		}
	}
	
	/**
	 * 选择自己数据处理
	 * @param selfId 选择自己数据处理
	 * @param isChecked
	 */
	public void selectSelfResponse(int selfId, boolean isChecked){
		if (isChecked) {
			for (int i = 0; i < StateActivity.all.size(); i++) {
				if (StateActivity.all.get(i).orgID == selfId){
					saveData(StateActivity.all.get(i), 1);
				}
			}
			
			mSelfId = selfId;
			isAddMyself2Result = true;
			myselfitem = mOrganizationDB.getORGItem(mSelfId);
		} else {
			for (int i = 0; i < StateActivity.all.size(); i++) {
				if (StateActivity.all.get(i).orgID == selfId){
					saveData(StateActivity.all.get(i), 0);
				}
			}
			
			mSelfId = selfId;
			isAddMyself2Result = false;
		}
	}
	
	public List<OrganizationItem> getSelectedBackData(int parent_id){
		if(result != null){
			result.clear();
		}else{
			result = new ArrayList<OrganizationItem>();
		}
		getSelectedData(parent_id);
		return result;
	}
	
	
	/**
	 * 保存，获取返回数据列表
	 * @param parentId 默认给0即可
	 * @return
	 */
	public void getSelectedData(int parentId) {
		if(isAddMyself2Result){
			//列表中找不到自己(如果自己是管理员,是找不到的)
			if(!result.contains(myselfitem)){
				if(myselfitem != null)
					result.add(myselfitem);
			}
		}else{
			result.remove(myselfitem);
		}
		
		for (int i = 0; i < StateActivity.all.size(); i++) {
			if (StateActivity.all.get(i).orgPid == parentId && StateActivity.all.get(i).state == 1) {
				int j = 0;
				for (j = 0; j < result.size(); j++) {// 如果结果列表中有了该项，则不添加
					if (isContainInResult(StateActivity.all.get(i), result.get(j)))
						break;
				}
				if (j == result.size())// 结果列表中没有添加，则添加
					result.add(StateActivity.all.get(i));
			} else if (StateActivity.all.get(i).orgPid == parentId && StateActivity.all.get(i).state == 2) {
				getSelectedData(StateActivity.all.get(i).orgID);
			}
		}
	}
	
	/**
	 * 
	 * 判断 item 选项是否包含在结点 resultItem中
	 * 
	 * @param item
	 * @param resultItem
	 * @return
	 */
	private boolean isContainInResult(OrganizationItem item, OrganizationItem resultItem) {
		int i = 0;
		if (item.orgID == resultItem.orgID)
			return true;
		else {
			List<OrganizationItem> idata = getData(resultItem.orgID);
			for (i = 0; i < idata.size(); i++) {
				if (isContainInResult(item, idata.get(i)))
					return true;
			}
			if (i >= idata.size())
				return false;
			else
				return true;
		}
	}
	
	/**
	 * 获取返回数据列表
	 * @return
	 */
	public List<OrganizationItem> getBackResponseList(List<OrganizationItem> data){
		int parentId = 0;
		int id = 0;
		if (data.size() > 0) {
			parentId = data.get(0).orgPid;
		}
		List<OrganizationItem> idata = new ArrayList<OrganizationItem>();
		for (int i = 0; i < StateActivity.all.size(); i++) {
			if (parentId == StateActivity.all.get(i).orgID)
				id = StateActivity.all.get(i).orgPid;
		}
		idata = getData(id);
		return idata;
	}

//	public List<OrganizationItem> getCurentData() {
//		return data;
//	}

	// 保存状态
	public void saveData(OrganizationItem item, int state) {
		synchronized (this) {
			Log.i("xpf", "线程" + Thread.currentThread().getName()+" :线程数= "+Thread.activeCount());
			item.state = state;// 保存当前节点
			List<OrganizationItem> idata = getData(item.orgPid);// 兄弟节点
			setState(item, state);// 保存子节点
			int number = idata.size();
			int selectorNumber = 0;
			int halfSelectorNumber = 0;
			for (int i = 0; i < idata.size(); i++) {// 保存父亲节点
				if (idata.get(i).state == 0) {

				} else if (idata.get(i).state == 1) {
					selectorNumber++;
				} else if (idata.get(i).state == 2) {
					halfSelectorNumber++;
				}
			}
			if (selectorNumber == 0 && halfSelectorNumber == 0) {// 全不选 ,父节点
				setParentState(idata, 0);
			} else if (selectorNumber == 0 && halfSelectorNumber > 0 || selectorNumber < number) {// 半选
				setParentState(idata, 2);
			} else if (selectorNumber == number) {// 全选
				setParentState(idata, 1);
			}
		}
	}

	// // 设置选项及子选项的状态
	public void setState(OrganizationItem item, final int state) {
		if (state == 0 || state == 1) {// 只保存01选项，及子选项
			List<OrganizationItem> idata = getData(item.orgID);// item的所有子节点列表
			for (int i = 0; i < idata.size(); i++) {
				for (int j = 0; j < StateActivity.all.size(); j++) {
					if (StateActivity.all.get(j).orgID == idata.get(i).orgID && StateActivity.all.get(j).orgPid == idata.get(i).orgPid)
						StateActivity.all.get(j).state = state;// 保存当前子节点状态
					else if (StateActivity.all.get(j).orgID == idata.get(i).orgID && StateActivity.all.get(j).orgPid != idata.get(i).orgPid) {
						final int k = j;
						new Thread() {
							public void run() {
								saveData(StateActivity.all.get(k), state);// 保存其它分组子结点状态
							};
						}.start();
					}
				}
			}

			if (idata.size() > 0) {
				for (int i = 0; i < idata.size(); i++) {
					for (int j = 0; j < StateActivity.all.size(); j++) {
						if (idata.get(i).orgID == StateActivity.all.get(j).orgID && idata.get(i).orgPid == StateActivity.all.get(j).orgPid) {
							setState(StateActivity.all.get(j), state);// 递归保存子节点的子节点。。
						}
					}
				}
			}
		}
	}

	// 设置父选项的状态
	public void setParentState(List<OrganizationItem> idata, int state) {
		if (idata.size() > 0) {
			if (idata.get(0).orgPid != 0) {
				for (int i = 0; i < StateActivity.all.size(); i++) {
					if (idata.get(0).orgPid == StateActivity.all.get(i).orgID) {
						StateActivity.all.get(i).state = state;// 设置父结点
						
//						//通知Adapter更新UI
//						Message msg = new Message();
//						msg.what = REFRESH;
//						mHandler.sendMessage(msg);
						
						if (StateActivity.all.get(i).orgPid != 0) {// 迭代
							ArrayList<OrganizationItem> jdata = new ArrayList<OrganizationItem>();
							for (int j = 0; j < StateActivity.all.size(); j++) {
								if (StateActivity.all.get(j).orgPid == StateActivity.all.get(i).orgPid) {
									jdata.add(StateActivity.all.get(j));

								}
							}

							int jnumber = jdata.size();
							int jselectorNumber = 0;
							int notallNumber = 0;
							for (int j = 0; j < jdata.size(); j++) {
								if (jdata.get(j).state == 1)
									jselectorNumber++;
								else if (jdata.get(j).state == 2)
									notallNumber++;
							}
							if (jselectorNumber == 0 && notallNumber == 0)
								setParentState(jdata, 0);
							else if (jselectorNumber == 0 && notallNumber > 0 || jselectorNumber < jnumber)
								setParentState(jdata, 2);
							else if (jselectorNumber == jnumber)
								setParentState(jdata, 1);
						}

					}
				}
			}
		}
	}
}
