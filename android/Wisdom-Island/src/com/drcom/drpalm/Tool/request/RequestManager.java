package com.drcom.drpalm.Tool.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.drcom.drpalm.objs.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Parser;

import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.drcom.drpalm.GlobalVariables;
import com.drcom.drpalm.Tool.XmlParse.UserInfo;
import com.drcom.drpalm.Tool.jsonparser.IParser;
import com.drcom.drpalm.Tool.service.DrServiceJni;
import com.drcom.drpalm.Tool.service.DrServiceJniCallback;
import com.drcom.drpalm.Tool.service.RequestParse;
import com.drcom.drpalm4tianzhujiao.R;

import javax.xml.transform.Result;

/**
 * 新请求接口类
 *
 * 旧的请求接口方法要在IDrPalmRequest.aidl 定义方法,然后通过DrPalmService中处理逻辑,
 * 再在RequestOperation.java 调用DrPalmService中对应函数,请求接口,较为烦琐。
 *
 * 此类直接调用 .SO 的接口，简化接口定义，详细请参考Logout()函数。
 *
 * (在DrPalmService中GetNetworkGate取网关成功后初始化)
 *
 * @author zhaojunjie
 *
 */
public class RequestManager extends Request {
	private static DrServiceJni mDrServiceJni;
	private static String mSchoolId = "";
	private static String mSessionKey = "";
	private static String mDomain = "";
	private static RequestOperation mRequestOperation = RequestOperation.getInstance();	//调用服务

//	private RequestManager requestManagerInstance = null;
//	public static RequestManager getInstance(Context context, String key) {
//		if(requestManagerInstance == null) {
//			requestManagerInstance = new requestManagerInstance(context,key);
//		} 
//		return requestManagerInstance;
//	}

	public RequestManager(String domain, String schoolID){
		super();
		setDomain(domain);
		setSchoolId(schoolID);
		mDomain = domain;
		mSchoolId = schoolID;
		mDrServiceJni = new DrServiceJni();
		mDrServiceJni.NativeInit();
	}

	/**
	 * 初始化参数
	 * @param domain
	 * @param schoolID
	 */
	public void init(String domain, String schoolID){
		mDomain = domain;
		mSchoolId = schoolID;
	}
	
	/**
	 * 
	 * @return
	 */
	private static DrServiceJni getDrServiceJniInstance(){
		if(mDrServiceJni == null){
			mDrServiceJni = new DrServiceJni();
			mDrServiceJni.NativeInit();
		}
		return mDrServiceJni;
	}

	private static boolean mChecklock = false;
	private static boolean result = false;
	/**
	 * 程序初始化没网络的状态下,会造成请求网关失败,请求前要先判断一下，
	 * 若没对应IP，要先请求网关
	 */
	private static boolean CheckGateway(){
		//没校园ip/校园ID
		if(mDomain.equals("") || mSchoolId.equals("")){
			mChecklock = true;
			RequestOperation mRequestOperation = RequestOperation.getInstance();
			mRequestOperation.GetNetworkGate(new RequestOperationCallback() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					mChecklock = false;
					result = true;
				}

				@Override
				public void onError(String err) {
					// TODO Auto-generated method stub
					mChecklock = false;
					result = false;
				}
			});

			while(mChecklock){
				//卡住
//				Log.i("zjj", "正在取网关数据...");
			}

		}else{
			result = true;
		}

		return result;
	}

	/**
	 * 注销
	 * @param iparser	JSON解释类
	 * @param callback	界面回调处理
	 * @return
	 */
	public static void Logout(final IParser iparser,final RequestOperationCallback callback){

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				LogoutItem logoutresult = new LogoutItem();
				try {
					JSONObject jb = new JSONObject(new String(data));
					logoutresult = (LogoutItem)iparser.parser(jb);	//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(logoutresult.result)
						callback.onSuccess();
					else
						callback.onError(logoutresult.errordes);
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		if(mDrServiceJni != null)
			mDrServiceJni.Logout(mDomain, mSchoolId, mSessionKey, jnicallback);
	}

	/**
	 * 登录接口
	 * @param iparser
	 * @param userid
	 * @param pass
	 * @param identify		//设备ID		(identify与packagename,会生成Tokenid 决定Push到哪个应用上)
	 * @param packagename	//应用包名	(identify与packagename,会生成Tokenid 决定Push到哪个应用上)
	 * @param callback
	 * @return
	 */
	public static boolean Login(final IParser iparser,final String userid, String pass,String identify,String packagename, final RequestLoginCallback callback){
		if(!CheckGateway()){
			return false;
		}

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				UserInfo loginresult = new UserInfo();
				try {
					JSONObject jb = new JSONObject(new String(data));
					loginresult = (UserInfo)iparser.parser(jb);	//JSON解释结果
					if(loginresult != null){
						//要把登录帐号赋值
						loginresult.strUsrName = userid;

						//这个类中的其它接口要用到
						mSessionKey = loginresult.sessionKey;
						//初始化旧接口
						mRequestOperation.initRequestInterface(userid, loginresult.sessionKey);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(loginresult.result)
						callback.onSuccess(loginresult);
					else
						callback.onError(loginresult.errordes);
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		if(mDrServiceJni != null)
			return mDrServiceJni.LoginGateway(mDomain, mSchoolId, userid, pass, identify, packagename, jnicallback);
		else
			return false;
	}

	/**
	 * 提交用户资料
	 * @param iparser	JSON解释类
	 * @param callback	界面回调处理
	 * @return
	 */
	public static void Submitaccountinfo(byte[] picbyte, final IParser iparser,final RequestOperationReloginCallback callback){
		if(!CheckGateway()){
			return ;
		}

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				SubmitResultItem result = new SubmitResultItem();
				try {
					JSONObject jb = new JSONObject(new String(data));
					result = (SubmitResultItem)iparser.parser(jb);	//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(result.result)
						callback.onSuccess();
					else
						callback.onError(result.errordes);
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		if(mDrServiceJni != null)
			mDrServiceJni.SubmitAccountinfo(mDomain, mSchoolId, mSessionKey,picbyte, jnicallback);
	}


	//获取成长点滴列表
	public static boolean GetGrowdiary(String lastupdate, final ViewRequestCallback callback){
		if(!CheckGateway()){
			return false;
		}

		DrServiceJniCallback jniHttpCallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}

				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try {

					map = parse.getHashMap();
					DiaryParse diaryParse = new DiaryParse(map);
					if(diaryParse.parseOperate()){
						callback.onSuccess(diaryParse);
					}
					else{
						String err = diaryParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return mDrServiceJni.GetGrowdiary(mDomain, mSchoolId, mSessionKey, lastupdate, jniHttpCallback);
	}

	//提交成长点滴列表
	public static boolean SubmitGrowdiary(ArrayList<GrowdiaryItem> filelist, final ViewRequestCallback callback){
		if(!CheckGateway()){
			return false;
		}

		DrServiceJniCallback jniHttpCallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				int iBegin = 0, iEnd = value.length;
				for (iBegin = 0; iBegin < value.length; iBegin++) {
					if (value[iBegin] == -17 || value[iBegin] == '{') {//-17(0xEF)
						break;
					}
				}
				for (iEnd = value.length - 1; iEnd >= 0; iEnd--) {
					if (value[iEnd] == '}') {
						break;
					}
				}

				if(iEnd <= iBegin){
//					callback.onError("JSONException");
					return;
				}
				byte[] valueN = new byte[iEnd - iBegin + 1];
				System.arraycopy(value, iBegin, valueN, 0, iEnd - iBegin + 1);
				RequestParse parse = new RequestParse(new String(valueN));
				HashMap<String, Object> map;
				try {

					map = parse.getHashMap();
					DiaryParse diaryParse = new DiaryParse(map);
					if(diaryParse.parseOperate()){
						callback.onSuccess(diaryParse);
					}
					else{
						String err = diaryParse.parseErrorCode();
						callback.onError(err);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					callback.onError("JSONException");
				}
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return mDrServiceJni.SubmitGrowdiary(mDomain, mSchoolId, mSessionKey, filelist, jniHttpCallback);
	}

	/**
	 * 提交班级收藏
	 * @param iparser	JSON解释类
	 * @param callback	界面回调处理
	 * @return
	 */
	public static void SubmitClassfav(ArrayList<FavItem> eventsList, final IParser iparser,final RequestOperationReloginCallback callback){
		if(!CheckGateway()){
			return ;
		}

		for(int i = 0 ; i < eventsList.size();i++){
			Log.i("zjj", "提交通告收藏 id:" + eventsList.get(i).mEventid + "状态:" + eventsList.get(i).mStatus);
		}

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				SubmitResultItem result = new SubmitResultItem();
				try {
					JSONObject jb = new JSONObject(new String(data));
					result = (SubmitResultItem)iparser.parser(jb);	//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(result.result)
						callback.onSuccess();
					else
						callback.onError(result.errordes);
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		if(mDrServiceJni != null)
			mDrServiceJni.SubmitClassfav(mDomain, mSchoolId, mSessionKey,eventsList, jnicallback);
	}

	/**
	 * 获取班级收藏夹接口
	 * @param lastupdate
	 * @param iparser
	 * @param callback
	 */
	public static void GetClassFavorite(String lastupdate, final IParser iparser,final RequestOperationReloginCallback callback){
		if(!CheckGateway()){
			return ;
		}

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				FavlistResultItem favresult = new FavlistResultItem();
				try {
					JSONObject jb = new JSONObject(new String(data));
					favresult = (FavlistResultItem)iparser.parser(jb);	//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(favresult.result)
						callback.onSuccess(favresult);
					else
						callback.onError(favresult.errordes);
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		if(mDrServiceJni != null)
			mDrServiceJni.GetClassFavorite(mDomain, mSchoolId, mSessionKey,lastupdate, jnicallback);
	}

	/**
	 * 获取个人相册接口
	 * @param lastupdate
	 * @param iparser
	 * @param callback
	 */
	public static void GetMyphoto(String lastupdate, final IParser iparser,final RequestOperationReloginCallback callback){
		if(!CheckGateway()){
			return ;
		}

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				MyphotolistResultItem result = new MyphotolistResultItem();
				try {
					JSONObject jb = new JSONObject(new String(data));
					result = (MyphotolistResultItem)iparser.parser(jb);	//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(result.result)
						callback.onSuccess(result);
					else
						callback.onError(result.errordes);
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		if(mDrServiceJni != null)
			mDrServiceJni.GetUseralbum(mDomain, mSchoolId, mSessionKey,lastupdate, jnicallback);
	}

	/**
	 * 提交个人相册
	 * @param iparser	JSON解释类
	 * @param callback	界面回调处理
	 * @return
	 */
	public static void SubmitUseralbum(ArrayList<MyPhoto> myphotolist, final IParser iparser,final RequestOperationReloginCallback callback){
		if(!CheckGateway()){
			return ;
		}

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				SubmitResultItem result = new SubmitResultItem();
				try {
					JSONObject jb = new JSONObject(new String(data));
					result = (SubmitResultItem)iparser.parser(jb);	//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(result.result)
						callback.onSuccess();
					else
						callback.onError(result.errordes);
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		if(mDrServiceJni != null)
			mDrServiceJni.SubmitUseralbum(mDomain, mSchoolId, mSessionKey,myphotolist, jnicallback);
	}

	/**
	 * 删除已发的通告
	 * @param iparser	JSON解释类
	 * @param callback	界面回调处理
	 * @return
	 */
	public static void SubmitDelEvent(String eventid, final IParser iparser,final RequestOperationReloginCallback callback){
		if(!CheckGateway()){
			return ;
		}

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				SubmitResultItem result = new SubmitResultItem();
				try {
					JSONObject jb = new JSONObject(new String(data));
					result = (SubmitResultItem)iparser.parser(jb);	//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(result.result)
						callback.onSuccess();
					else
						callback.onError(result.errordes);
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		if(mDrServiceJni != null)
			mDrServiceJni.SubmitDelEvent(mDomain, mSchoolId, mSessionKey,eventid, jnicallback);
	}

	/**
	 * 获取联系人列表接口
	 * @param iparser	JSON解释类
	 * @param callback	界面回调处理
	 * @return
	 */
	public static void GetContactList(ArrayList<ContactItem> contactItemList, final IParser iparser,final RequestOperationReloginCallback callback){
		if(!CheckGateway()){
			return ;
		}

		for(int i = 0 ; i < contactItemList.size();i++){
			Log.i("zjj", "提交联系人 id:" + contactItemList.get(i).cnid + "时间:" + contactItemList.get(i).lastawstimeseconds);
		}

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				ContectlistResultItem result = new ContectlistResultItem();
				try {
					JSONObject jb = new JSONObject(new String(data));
					result = (ContectlistResultItem)iparser.parser(jb);	//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(result.result)
						callback.onSuccess(result);
					else
						callback.onError(result.errordes);
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		if(mDrServiceJni != null)
			mDrServiceJni.GetContactList(mDomain, mSchoolId, mSessionKey,contactItemList, jnicallback);
	}

	/**
	 * 2.3.3.1.11 提交班级回评接口
	 * @param filelist
	 * @param callback
	 * @return
	 */
	public static boolean SubmitClassreview(String eventid,ArrayList<ReviewResult> filelist, final IParser iparser, final RequestOperationReloginCallback callback){
		if(!CheckGateway()){
			return false;
		}

		DrServiceJniCallback jniHttpCallback = new DrServiceJniCallback(){
			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				SubmitResultItem result = new SubmitResultItem();
				try {
					JSONObject jb = new JSONObject(new String(data));
					result = (SubmitResultItem)iparser.parser(jb);	//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(result.result)
						callback.onSuccess();
					else
						callback.onError(result.errordes);
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};
		return mDrServiceJni.SubmitClassreview(mDomain, mSchoolId, mSessionKey,eventid, filelist, jniHttpCallback);
	}

	/**
	 * 2.3.5.1 获取考勤信息接口
	 * @param lastupdate
	 * @param iparser
	 * @param callback
	 */
	public static void GetAttendanceinfo(String userid, String lastupdate, String verificationkey,String challenge,final IParser iparser,final RequestOperationReloginCallback callback){
		if(!CheckGateway()){
			return ;
		}

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				AttendanceinfoListResultItem result = new AttendanceinfoListResultItem();
				try {
					JSONObject jb = new JSONObject(new String(data));
					result = (AttendanceinfoListResultItem)iparser.parser(jb);	//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(result.result)
						callback.onSuccess(result);
					else
						callback.onError(result.errordes);
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		if(mDrServiceJni != null)
			mDrServiceJni.GetAttendanceinfo(GlobalVariables.gAttendanceUrl, mSchoolId, userid,lastupdate,verificationkey, challenge,jnicallback);
	}

	/**
	 * 2.3.5.1 获取个人信息接口
	 * @param lastupdate
	 * @param callback
	 */
	public static void GetUserInfo(String lastupdate, final IParser iparser,final RequestOperationReloginCallback callback){
		if(!CheckGateway()){
			return ;
		}

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				UserInfo result = new UserInfo();
				try {
					JSONObject jb = new JSONObject(new String(data));
					result = (UserInfo)iparser.parser(jb);	//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(result.result)
						callback.onSuccess(result);
					else
						callback.onError(result.errordes);
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		if(mDrServiceJni != null)
			mDrServiceJni.GetAccountinfo(mDomain, mSchoolId, mSessionKey, lastupdate, jnicallback);
	}

    public static void getReceiverList(final IParser iParser,final RequestOperationReloginCallback requestOperationReloginCallback){

        DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

            @Override
            public void onError(byte[] value) {
                requestOperationReloginCallback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
            }

            @Override
            public void onSuccess(byte[] value) {
            	ReceiverResultItem resultItem = new ReceiverResultItem();
                try {
                    JSONObject jb = new JSONObject(new String(value));
                    resultItem = (ReceiverResultItem)iParser.parser(jb);
                    
                }catch (Exception e){

                }
                //回调到界面
                if(requestOperationReloginCallback != null){
                    if(resultItem.isResult())
                        requestOperationReloginCallback.onSuccess(resultItem);
                    else
                        requestOperationReloginCallback.onError(resultItem.getErrordes());
                }
            }

            @Override
            public void onReceiveData(byte[] value) {

            }
        };

        if(mDrServiceJni != null){
            mDrServiceJni.GetLeaveownerlist(mDomain,mSchoolId,mSessionKey,jnicallback);
        }
    }
    
    //4期
    
    /**
     * 获取告假列表接口
     * 
     * **/
    public static void getLeaveApplicationList(String lastupdate, final IParser iparser,final RequestOperationReloginCallback callback){
    	if(!CheckGateway()){
			return ;
		}

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				ResultItem resultItem = new ResultItem();
				ArrayList<LeaveApplicationMainItem> result = new ArrayList<LeaveApplicationMainItem>();
				try {
					JSONObject jb = new JSONObject(new String(data));
					resultItem = (ResultItem)iparser.parser(jb);
					result = (ArrayList<LeaveApplicationMainItem>)resultItem.getData();//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(resultItem.isResult())
						callback.onSuccess(result);
					else
						callback.onError(resultItem.getErrordes());
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		if(mDrServiceJni != null)
			mDrServiceJni.GetLeavelist(mDomain, mSchoolId, mSessionKey, lastupdate, jnicallback);
    }
    
    /**
     * 获取告假详细接口
     * 
     * **/
    public static void getApplicationInfo(String leaveid, String allfield, final IParser iparser,final RequestOperationReloginCallback callback){
    	if(!CheckGateway()){
			return ;
		}

		//接口回调处理
		DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] data) {
				// TODO Auto-generated method stub
				callback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] data) {
				ResultItem resultItem = new ResultItem();
				LeaveApplicationMainItem result = new LeaveApplicationMainItem();
				try {
					JSONObject jb = new JSONObject(new String(data));
					resultItem = (ResultItem)iparser.parser(jb);
					result = (LeaveApplicationMainItem)resultItem.getData();//JSON解释结果

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//回调到界面
				if(callback != null){
					if(resultItem.isResult())
						callback.onSuccess(result);
					else
						callback.onError(resultItem.getErrordes());
				}

			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub

			}
		};

		if(mDrServiceJni != null)
			mDrServiceJni.GetLeaveinfo(mDomain, mSchoolId, mSessionKey, leaveid, allfield, jnicallback);
    }
    
    /**
     * 告假发送接口
     * @param type
     * @param ownerid
     * @param owner
     * @param start
     * @param end
     * @param title
     * @param content
     * @param fileList
     */
    public static void submitLeaveInfo(String type, String ownerid, String owner,
                                       String start, String end, String title, String content, final IParser iParser,
                                       ArrayList<EventDraftItem.Attachment> fileList, final RequestOperationReloginCallback requestOperationReloginCallback){

        //接口回调处理
        DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

            @Override
            public void onError(byte[] value) {
                requestOperationReloginCallback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
            }

            @Override
            public void onSuccess(byte[] value) {
                ResultItem resultItem = new ResultItem();
                try {
                    JSONObject jb = new JSONObject(new String(value));
                    resultItem = (ResultItem)iParser.parser(jb);
                }catch (Exception e){

                }
                //回调到界面
                if(requestOperationReloginCallback != null){
                    if(resultItem.isResult())
                        requestOperationReloginCallback.onSuccess(resultItem);
                    else
                        requestOperationReloginCallback.onError(resultItem.getErrordes());
                }
            }

            @Override
            public void onReceiveData(byte[] value) {

            }
        };
        if(mDrServiceJni != null)
            mDrServiceJni.SubmitLeave(mDomain,
                    mSchoolId,
                    mSessionKey,type,ownerid,owner,start,end,title,content,fileList,jnicallback);
    }
    
    //获取通告标题列表数据
    public static void getEventsTypeList(final IParser iParser,final RequestOperationReloginCallback requestOperationReloginCallback){
    	
        DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

            @Override
            public void onError(byte[] value) {
                requestOperationReloginCallback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
            }

            @Override
            public void onSuccess(byte[] value) {
            	EventsTypeLanguageResultItem resultItem = new EventsTypeLanguageResultItem();
                try {
                    JSONObject jb = new JSONObject(new String(value));
                    resultItem = (EventsTypeLanguageResultItem)iParser.parser(jb);
                    
                }catch (Exception e){

                }
                //回调到界面
                if(requestOperationReloginCallback != null){
                    if(resultItem.isResult())
                        requestOperationReloginCallback.onSuccess(resultItem);
                    else
                        requestOperationReloginCallback.onError(resultItem.getErrordes());
                }
            }

            @Override
            public void onReceiveData(byte[] value) {

            }
        };

        if(mDrServiceJni != null){
            mDrServiceJni.GetEventtitlelist(mDomain,mSchoolId,mSessionKey,jnicallback);
        }
    }
    
    
    /**
     * 获取账号关联列表
     * @param iParser
     * @param requestOperationReloginCallback
     */
    public static void getBindaccountList(String appid,String tokenid,final IParser iParser,final RequestOperationReloginCallback requestOperationReloginCallback){
    	
        DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

            @Override
            public void onError(byte[] value) {
                requestOperationReloginCallback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
            }

            @Override
            public void onSuccess(byte[] value) {
            	BindaccountResultItem resultItem = new BindaccountResultItem();
                try {
                    JSONObject jb = new JSONObject(new String(value));
                    resultItem = (BindaccountResultItem)iParser.parser(jb);
                    
                }catch (Exception e){

                }
                //回调到界面
                if(requestOperationReloginCallback != null){
                    if(resultItem.isResult())
                        requestOperationReloginCallback.onSuccess(resultItem);
                    else
                        requestOperationReloginCallback.onError(resultItem.getErrordes());
                }
            }

            @Override
            public void onReceiveData(byte[] value) {

            }
        };
        	getDrServiceJniInstance().GetRelationaccount(GlobalVariables.gCenterDomain,appid,tokenid,jnicallback);
    }
    
    /**
     * 获取账号信息（通过网关地址头像url和未读数）
     * @param iParser
     * @param requestOperationReloginCallback
     */
    public static void getBindaccount(String domain, String tokenid, List<BindaccountItem> accounts, final IParser iParser,final RequestOperationReloginCallback requestOperationReloginCallback){
    	
        DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

            @Override
            public void onError(byte[] value) {
                requestOperationReloginCallback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
            }

            @Override
            public void onSuccess(byte[] value) {
            	BindaccountResultItem resultItem = new BindaccountResultItem();
                try {
                    JSONObject jb = new JSONObject(new String(value));
                    resultItem = (BindaccountResultItem)iParser.parser(jb);
                    
                }catch (Exception e){

                }
                //回调到界面
                if(requestOperationReloginCallback != null){
                    if(resultItem.isResult())
                        requestOperationReloginCallback.onSuccess(resultItem);
                    else
                        requestOperationReloginCallback.onError(resultItem.getErrordes());
                }
            }

            @Override
            public void onReceiveData(byte[] value) {

            }
        };
        	getDrServiceJniInstance().Getaccountsinfo(domain,tokenid,accounts,jnicallback);
    }
    
    
    /**
     * 添加账号关联
     * @param account
     * @param passwordMd5
     * @param schoolkey
     * @param tokenid
     * @param iParser
     * @param requestOperationReloginCallback
     */
    public static void addAccountRelation(String account, String passwordMd5, String schoolkey, String tokenid,final IParser iParser,final RequestOperationReloginCallback requestOperationReloginCallback){
    	 DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] value) {
				// TODO Auto-generated method stub
				 requestOperationReloginCallback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				// TODO Auto-generated method stub
				SubmitResultItem resultItem = new SubmitResultItem();
                try {
                    JSONObject jb = new JSONObject(new String(value));
                    resultItem = (SubmitResultItem)iParser.parser(jb);
                    
                }catch (Exception e){

                }
                //回调到界面
                if(requestOperationReloginCallback != null){
                    if(resultItem.result)
                        requestOperationReloginCallback.onSuccess(resultItem);
                    else
                        requestOperationReloginCallback.onError(resultItem.errordes);
                }
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub
				
			}
    		 
    	 };
        	 getDrServiceJniInstance().Addrelationaccount(GlobalVariables.gCenterDomain,account,passwordMd5,schoolkey,tokenid,jnicallback);
    }

    /**
     * 删除账号关联
     * @param accountlist
     * @param iParser
     * @param requestOperationReloginCallback
     */
    public static void deleteAccountRelation(List<BindaccountItem> accountlist,final IParser iParser,final RequestOperationReloginCallback requestOperationReloginCallback){
    	
   	 DrServiceJniCallback jnicallback = new DrServiceJniCallback(){

			@Override
			public void onError(byte[] value) {
				// TODO Auto-generated method stub
				 requestOperationReloginCallback.onError(GlobalVariables.gAppContext.getString(R.string.jni_error));
			}

			@Override
			public void onSuccess(byte[] value) {
				// TODO Auto-generated method stub
				SubmitResultItem resultItem = new SubmitResultItem();
               try {
                   JSONObject jb = new JSONObject(new String(value));
                   resultItem = (SubmitResultItem)iParser.parser(jb);
                   
               }catch (Exception e){

               }
               //回调到界面
               if(requestOperationReloginCallback != null){
                   if(resultItem.result)
                       requestOperationReloginCallback.onSuccess(resultItem);
                   else
                       requestOperationReloginCallback.onError(resultItem.errordes);
               }
			}

			@Override
			public void onReceiveData(byte[] value) {
				// TODO Auto-generated method stub
				
			}
   	 };
        	getDrServiceJniInstance().Delrelationaccount(GlobalVariables.gCenterDomain,accountlist,jnicallback);
    	
    }
}
