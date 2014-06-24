package com.drcom.drpalm.View.mOrganization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.drcom.drpalm.CommonFileManagent;
import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.DB.OrganizationDB;
import com.drcom.drpalm.Tool.Encryption;
import com.drcom.drpalm.Tool.ResourceManagement;
import com.drcom.drpalm.Tool.request.DownLoadCallback;
import com.drcom.drpalm.Tool.request.DownLoadRequest;
import com.drcom.drpalm.Tool.request.OrganizationFileParse;
import com.drcom.drpalm.Tool.request.RequestOperation;
import com.drcom.drpalm.Tool.request.RequestOrganizationCallback;
import com.drcom.drpalm.Tool.service.RequestParse;
import com.drcom.drpalm.View.setting.SettingManager;
import com.drcom.drpalm.objs.OrgTraversalItem;
import com.drcom.drpalm.objs.OrganizationBackItem;
import com.drcom.drpalm.objs.OrganizationItem;
/**
 * 
 * @author MCX
 *
 */
public class FetchSchoolOrganization {

	private static final int FETCH_SUCCESSFUL = 1;
	private static final int FETCH_FAILED = 2;
	public static final int FETCH_ORGANIZATION_SUCCESSFUL = 3;
	public static final int FETCH_ORGANIZATION_FAILED = 4;
	private ResourceManagement mResourceManagement;
	private Context mContext;

	public FetchSchoolOrganization(Context context) {
		this.mContext = context;
	}

	public void getOrganization(final Handler handler) {
		HandlerThread handlerThread = new HandlerThread("organization_deal");
		handlerThread.start();
		final Handler uiHandler = new Handler(handlerThread.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.arg1 == FETCH_SUCCESSFUL) {
					final OrganizationBackItem item = (OrganizationBackItem) msg.obj;
					final String checksum = item.checksum;
					if ((item.url != null) && (item.url.lastIndexOf('/')!=-1)&&(item.url.lastIndexOf('/')!=item.url.length()-1)) {
						// 保存文件名字
						String fileName = item.url.substring(item.url.lastIndexOf('/') + 1);
						String test = fileName.substring(0, fileName.lastIndexOf(".")) + ".txt";
						GlobalVariables.gOrgName = fileName.substring(0, fileName.lastIndexOf(".")) + ".txt";
						// 下载指定URL的ZIP文件
						DownLoadRequest mDownLoadRequest = new DownLoadRequest();
						mDownLoadRequest.DownLoadTours(item.url, new DownLoadCallback() {
							@Override
							public void onError(String err) {
								sendErrorInfo(handler);
							}

							@Override
							public void onDownloading(DownloadingStruct obj) {
								// show sth in view
							}

							@Override
							public void onFinished(File file) {
								// 校验文件是否完整
								if (confirmFileCheckSum(file, checksum)) {
									// unzip and record the last update
									String filename = file.getName();
									Log.i("xpf", "xd" + file.getAbsolutePath());
									String newFileName = GlobalVariables.gSchoolId + "_" + filename;
									File newFile = copyFileToResourceDirectory(file, newFileName);
									if (null != newFile) {
										if (newFile.exists()) {
											file.delete();
										}
									}
									// 解压ZIP文件到指定文件目录
									mResourceManagement = new ResourceManagement();
									mResourceManagement.initResourceWithZipForOrganization(newFile.getName(), true);

									new Thread() {
										@Override
										public void run() {
											// 获取指定文件的二进制流
											byte[] value = mResourceManagement.getFileContent(GlobalVariables.gOrgName);
											String test = new String(value);
											// 取出头部和尾部多余的东西，纯化jason数据
											int iBegin = 0, iEnd = value.length;
											for (iBegin = 0; iBegin < value.length; iBegin++) {
												if (value[iBegin] == -17 || value[iBegin] == '{') {// -17(0xEF)
													break;
												}
											}
											for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
												if (value[iEnd] == '}') {
													break;
												}
											}

											if (iEnd <= iBegin) {
												return;
											}

											byte[] valueN = new byte[iEnd - iBegin + 1];
											System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
											String test2 = new String(valueN);
											RequestParse parse = new RequestParse(new String(valueN));
											HashMap<String, Object> map;
											ArrayList<OrganizationItem> recvOrgList = new ArrayList<OrganizationItem>();
											try {
												map = parse.getHashMap();
												OrganizationFileParse orgFileParse = new OrganizationFileParse(map);
												recvOrgList = orgFileParse.parseOrganization();
											} catch (Exception e) {
												// TODO: handle exception
											}
											recvOrgList = (ArrayList<OrganizationItem>) createPointPath(recvOrgList);
											recvOrgList = (ArrayList<OrganizationItem>) createChildrenCount(recvOrgList);
											OrganizationDB mOrgDB = OrganizationDB.getInstance(GlobalVariables.gAppContext, GlobalVariables.gSchoolKey);
											SettingManager setInstance = SettingManager.getSettingManager(mContext);
											String mUsername = setInstance.getCurrentUserInfo().strUsrName;
											synchronized (mOrgDB) {
												mOrgDB.startTransaction();
												//清除表
												mOrgDB.clearOrgItem();
												
												for (OrganizationItem recvItem : recvOrgList) {
													// add sub item and parent
													// item
													// save to database
													// save item
													mOrgDB.saveOrgItem(recvItem);
													// save parent item
													// mOrgDB.saveOrgParItem(recvItem.orgID,
													// item.orgID, mUsername);
												}
												mOrgDB.endTransaction();
											}

											String preference_filename = GlobalVariables.gSchoolKey + "_organization_lastupdate";
											SharedPreferences preferences = mContext.getSharedPreferences(preference_filename, Context.MODE_PRIVATE);
											Editor editor = preferences.edit();
											editor.putLong("lastupdate", item.lastupdate);
											editor.commit();

											sendSuccessInfo(handler);
										}
									}.start();
								} else {
									sendErrorInfo(handler);
								}
							}
						});
					} else {
						sendSuccessInfo(handler);
					}
				} else if (msg.arg1 == FETCH_FAILED) {
					sendErrorInfo(handler);
				}
			}
		};
		getNativeOrganization(uiHandler);
	}

	/**
	 * 返回错误信息给主线程
	 * 
	 * @param handler
	 */
	private void sendErrorInfo(Handler handler) {
		Message msg = Message.obtain();
		msg.arg1 = FETCH_ORGANIZATION_FAILED;
		handler.sendMessage(msg);
	}

	/**
	 * 返回错误信息给主线程
	 * 
	 * @param handler
	 */
	private void sendSuccessInfo(Handler handler) {
		Message msg = Message.obtain();
		msg.arg1 = FETCH_ORGANIZATION_SUCCESSFUL;
		handler.sendMessage(msg);
	}

	/**
	 * 底层掉接口从服务器获取组织架构信息（URL）
	 * 
	 * @param uiHandler
	 */
	private void getNativeOrganization(final Handler uiHandler) {
		RequestOperation mRequestOperation = RequestOperation.getInstance();
		RequestOrganizationCallback callback = new RequestOrganizationCallback() {

			@Override
			public void onSuccess(final OrganizationBackItem item) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = FETCH_SUCCESSFUL;
				msg.obj = item;
				uiHandler.sendMessage(msg);
			}

			@Override
			public void onError(String err) {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				msg.arg1 = FETCH_FAILED;
				msg.obj = err;
				uiHandler.sendMessage(msg);
			}
		};
		mRequestOperation.sendGetNeededInfo("GetOrganization", new Object[] { callback }, callback.getClass().getName());
	}

	// MD5文件完整性验证
	public boolean confirmFileCheckSum(File file, String checkSum) {
		// String filePath = GlobalVariables.getResourceZipDirectory() + "/" +
		// fileName;
		String fileSum = Encryption.toMd5(file);
		return (fileSum.equals(checkSum)) ? true : false;
	}

	/**
	 * 拷贝文件到指定目录
	 * 
	 * @param file
	 * @param newFileName
	 * @return
	 */
	private File copyFileToResourceDirectory(File file, String newFileName) {
		// GlobalVariables.gAppResource.getString(R.string.ResourceUpdateZip);
		File newFile = null;
		String newFileWholePath = GlobalVariables.getResourceZipDirectory() + '/' + newFileName;
		if (CommonFileManagent.copyFile(file.getAbsolutePath(), newFileWholePath, true)) {
			newFile = new File(newFileWholePath);
		}
		return newFile;
	}

	/**
	 * 构建节点路径
	 * 
	 * @param list
	 * @return
	 */
	private List<OrganizationItem> createPointPath(List<OrganizationItem> list) {
		List<OrgTraversalItem> casheList = new ArrayList<OrgTraversalItem>(); // 存放层遍历所得节点的临时list
		// 初始化遍历开始根节点
		OrgTraversalItem item = new OrgTraversalItem();
		item.orgId = 0;
		casheList.add(item);
		// 层遍历获取所有节点列表
		for (int i = 0; i < casheList.size(); i++) {
			List<OrgTraversalItem> subList = getChildrenList(casheList.get(i), list);
			for (int j = 0; j < subList.size(); j++) {
				casheList.add(subList.get(j));
			}
		}
		// 遍历节点，添加路径项
		for (int j = 0; j < list.size(); j++) {
			list.get(j).orgPath = getPointPath(list.get(j), casheList);
		}

		return list;
	}

	/**
	 * 获取父节点的多有子节点list，以便顺序插入队列实现层遍历
	 * 
	 * @param item
	 *            父节点信息
	 * @param list
	 *            服务器返回，文件读取的节点关系list
	 * @return
	 */
	private List<OrgTraversalItem> getChildrenList(OrgTraversalItem item, List<OrganizationItem> list) {
		List<OrgTraversalItem> sublist = new ArrayList<OrgTraversalItem>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).orgPid == item.orgId) {
				OrgTraversalItem traversalItem = new OrgTraversalItem();
				traversalItem.orgId = list.get(i).orgID;
				traversalItem.path = item.path + "/" + item.orgId;
				sublist.add(traversalItem);
			}
		}
		return sublist;
	}

	/**
	 * 构建节点path信息
	 * 
	 * @param item
	 *            需构建的节点信息
	 * @param list
	 *            层遍历构建的完整节点list
	 * @return
	 */
	private String getPointPath(OrganizationItem item, List<OrgTraversalItem> list) {
		List<OrgTraversalItem> casheList = new ArrayList<OrgTraversalItem>();
		String path = "";
		// 处理一个及节点多个父亲，多路径问题
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).orgId == item.orgID) {
				casheList.add(list.get(i));
			}
		}
		if (casheList.size() > 0)
			path += casheList.get(0).path;
		for (int j = 1; j < casheList.size(); j++) {
			path += "," + casheList.get(j).path;
		}
		return path;
	}

	/**
	 * 获取每个节点的子节点数目
	 * 
	 * @param list
	 *            所有节点的list列表
	 * @return
	 */
	private List<OrganizationItem> createChildrenCount(List<OrganizationItem> list) {
		List<OrganizationItem> cashelist = new ArrayList<OrganizationItem>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).orgType.equals("group")) {
				// 排除相同的节点
				cashelist.addAll(list);
				cashelist = (ArrayList<OrganizationItem>) purge(cashelist);
				int count = 0;
				for (int j = 0; j < cashelist.size(); j++) {
					// 路径中包含orgid 且 为叶节点，则判断为当前组织的子叶节点
					if (cashelist.get(j).orgPath.contains(String.valueOf(list.get(i).orgID)) && !cashelist.get(j).orgType.equals("group")) {
						count++;
					}
				}
				list.get(i).orgCount = count;
			} else {
				list.get(i).orgCount = 0;
			}
		}
		return list;
	}

	/**
	 * 排重算法，排除list中相同节点
	 * 
	 * @param list
	 * @return
	 */
	private List<OrganizationItem> purge(List<OrganizationItem> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			int orgId = list.get(i).orgID;
			for (int j = i + 1; j < list.size(); j++) {
				if (list.get(j).orgID == orgId) {
					list.remove(j);
				}
			}
		}
		return list;
	}
}
