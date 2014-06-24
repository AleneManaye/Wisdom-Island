/*
 * File         : com_drcom_drpalm_Tool_service_DrServiceJni.cpp
 * Date         : 2012-07-04
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : com_drcom_drpalm_Tool_service_DrServiceJni source
 */


#include "com_drcom_drpalm_Tool_service_DrServiceJni.h"
#include "DrUrlConnection.h"
#include "DrRequestDefine.h"
#include "StringHandle.h"
#include "DrLog.h"
#include <map>
#include <list>
#include "json/json/json.h"

typedef map<int, jobject> JNICALLBACKOBJECTMAP;
JNICALLBACKOBJECTMAP g_ObjectMap;

DrMutex gMutex;
jobject g_JniCallbackObject = NULL;
JavaVM *g_jvm = NULL;

static void checkAndClearExceptionFromCallback(JNIEnv* env, const char* methodName, bool isThrow);
static void writeLogWithJava(JNIEnv *env, unsigned char* buf, long len);

static void insertCallbackObject(JNIEnv *env, long id, jobject callback, string methodname = "") {
	jobject obj = env->NewGlobalRef(callback);
	gMutex.lock(methodname.c_str());
	JNICALLBACKOBJECTMAP::iterator it = g_ObjectMap.find(id);
	if(it != g_ObjectMap.end()){
		jobject oldObj = it->second;
		if(NULL != oldObj){
			showLog("Jni.DrService", "modify old %s callback object iThreadId:%ld", methodname.c_str(), id);
			env->DeleteGlobalRef(oldObj);
			oldObj = NULL;
		}
		it->second = obj;
	}
	else {
		showLog("Jni.DrService", "insert %s callback object iThreadId:%ld", methodname.c_str(), id);
		g_ObjectMap.insert(JNICALLBACKOBJECTMAP::value_type(id, obj));
	}
	gMutex.unlock(methodname.c_str());
}
static void earseCallbackObject(JNIEnv *env, long id, string methodname = "") {
	gMutex.lock(methodname.c_str());
	JNICALLBACKOBJECTMAP::iterator it = g_ObjectMap.find(id);
	if(it != g_ObjectMap.end()){
		jobject obj = it->second;
		if(NULL != obj){
			env->DeleteGlobalRef(obj);
			obj = NULL;
		}
		showLog("Jni.DrService", "earse old %s callback object iThreadId:%ld", methodname.c_str(), id);
		g_ObjectMap.erase(it);
	}
	gMutex.unlock(methodname.c_str());
}

DrUrlConnection gDrUrlConnection;
static void checkAndClearExceptionFromCallback(JNIEnv* env, const char* methodName, bool isThrow){
	if(env->ExceptionCheck()){
		showLog("Jni.DrService.checkAndClearExceptionFromCallback", "An exception was thrown by callback:'%s'", methodName);
		env->ExceptionClear();
	}
}
static void writeLogWithJava(JNIEnv *env, unsigned char* buf, long len) {
	showLog("Jni.DrService.writeLogWithJava", "buf(%ld):%s", len, buf);
	jclass cls_DrServiceJniCallback = env->GetObjectClass(g_JniCallbackObject);
	jmethodID mth_WriteJniLog = env->GetMethodID(cls_DrServiceJniCallback, "WriteJniLog","([B)V");
	checkAndClearExceptionFromCallback(env, "Jni.DrService.WriteJniLog", false);
	if(0 != mth_WriteJniLog){
		jbyteArray data = env->NewByteArray(len);
		env->SetByteArrayRegion(data, 0, len, (jbyte*)buf);
		env->CallVoidMethod(g_JniCallbackObject, mth_WriteJniLog, data);
	}
}
static void onWriteLog(unsigned char* buf, long len, long iThreadId) {
	JNIEnv *env;
	if(g_jvm){
		jlong iRet;
		iRet = g_jvm->AttachCurrentThread((JNIEnv **)&env, NULL);
		writeLogWithJava(env, buf, len);
		g_jvm->DetachCurrentThread();
	}
}

static void onSuccess(unsigned char* buf, long len, long iThreadId, bool bBackToView){
	showLog("Jni.DrService.onSuccess", "buf:%s len:%ld, iThreadId:%ld", buf, len, iThreadId);
	JNIEnv *env;//AndroidRuntime::getJNIEnv();
	if(g_jvm){
		jlong iRet;
		iRet = g_jvm->AttachCurrentThread((JNIEnv **)&env, NULL);
		showLog("Jni.DrService.onSuccess", "AttachCurrentThread iRet:%ld", iRet);

		jbyteArray data = env->NewByteArray(len);
		env->SetByteArrayRegion(data, 0, len, (jbyte*)buf);
		showLog("Jni.DrService.onSuccess", "len:%ld", len);

		jobject obj = NULL;
		gMutex.lock("onSuccess");
		JNICALLBACKOBJECTMAP::iterator it = g_ObjectMap.find(iThreadId);
		if(it != g_ObjectMap.end()){
			obj = it->second;
		}
		gMutex.unlock("onSuccess");

		if(NULL != obj && bBackToView) {
			showLog("Jni.DrService.onSuccess", "find callback object iThreadId:%ld", iThreadId);
			jclass cls_DrServiceJniCallback = env->GetObjectClass(obj);
			jmethodID mth_onSuccess = env->GetMethodID(cls_DrServiceJniCallback,"onSuccess","([B)V");
			checkAndClearExceptionFromCallback(env, "Jni.DrService.onSuccess", false);
			if(0 != mth_onSuccess){
				env->CallVoidMethod(obj, mth_onSuccess, data);
			}
			showLog("Jni.DrService.onSuccess", "finished");
		}

		earseCallbackObject(env, iThreadId, "onSuccess");

		g_jvm->DetachCurrentThread();
	}

}
static void onError(unsigned char* buf, long len, long iThreadId, bool bBackToView){
	showLog("Jni.DrService.onError", "buf:%s len:%ld, iThreadId:%ld", buf, len, iThreadId);
	JNIEnv *env;//AndroidRuntime::getJNIEnv();
	if(g_jvm){

		g_jvm->AttachCurrentThread((JNIEnv **)&env, NULL);
		jbyteArray data = env->NewByteArray(len);
		env->SetByteArrayRegion(data, 0, len, (jbyte*)buf);

		jobject obj = NULL;
		gMutex.lock("onError");
		JNICALLBACKOBJECTMAP::iterator it = g_ObjectMap.find(iThreadId);
		if(it != g_ObjectMap.end()){
			obj = it->second;
		}
		gMutex.unlock("onError");

		if(NULL != obj && bBackToView) {
			jclass cls_DrServiceJniCallback = env->GetObjectClass(obj);
			jmethodID mth_onError = env->GetMethodID(cls_DrServiceJniCallback,"onError","([B)V");
			checkAndClearExceptionFromCallback(env, "Jni.DrService.onError", false);
			if(0 != mth_onError){
				env->CallVoidMethod(obj, mth_onError, data);
			}
			showLog("Jni.DrService.onError", "finished");
		}
		earseCallbackObject(env, iThreadId, "onError");
		g_jvm->DetachCurrentThread();
	}
}
static void onReceiveData(unsigned char* buf, long len, long iThreadId, bool bBackToView){
	showLog("Jni.DrService.onReceiveData", "buf:%s len:%ld, iThreadId:%ld", buf, len, iThreadId);

//	JNIEnv *env;
//	bool isOnReceivePushData = false;
//	if(g_jvm){
//		g_jvm->AttachCurrentThread((JNIEnv **)&env, NULL);
//
//		jobject obj = NULL;
//		gMutex.lock("onReceiveData");
//		JNICALLBACKOBJECTMAP::iterator it = g_ObjectMap.find(iThreadId);
//		if(it != g_ObjectMap.end()){
//			obj = it->second;
//		}
//		gMutex.unlock("onReceiveData");
//		if(NULL != obj){
//
//			jclass cls_DrServiceJniCallback = env->GetObjectClass(obj);
//			if(cls_DrServiceJniCallback){
//				jmethodID mth_onReceiveData = env->GetMethodID(cls_DrServiceJniCallback,"onReceiveData","([B)V");
//				checkAndClearExceptionFromCallback(env, "onReceiveData", false);
//				if(0 != mth_onReceiveData){
//					showLog("Jni.DrService.onReceiveData", "onReceiveData");
//					jbyteArray data = env->NewByteArray(len);
//					env->SetByteArrayRegion(data, 0, len, (jbyte*)buf);
//					env->CallVoidMethod(obj, mth_onReceiveData, data);
//				}
//			}
//			showLog("Jni.DrPushService.onReceiveData", "finished");
//		}
//		g_jvm->DetachCurrentThread();
//	}
}

static JNICALLBACK m_JniCallback = {
		onSuccess,
		onError,
		onReceiveData,
		onWriteLog
};

inline string grantMD5(string strData) {
	unsigned char md[DrCOM_BUFFER_16B] = {'\0'};
	char cTmp[3]={'\0'};
	string strRet = "";

	MD5((unsigned char*)strData.c_str(), strData.length(), md);
	for (long i = 0; i < 16; i++){
		sprintf(cTmp,"%02x", md[i]);
		strRet += cTmp;
	}
	return strRet;
}
inline string getTokenid(string indetify, string packageName){
	string strIndetify = indetify + packageName;
	showLog("Jni.DrService.getCheckCode", "before md5 tokenid:%s", strIndetify.c_str());
	string tokenid = "";
	if("" != strIndetify)
		tokenid = grantMD5(strIndetify);
	return tokenid;
}
inline string getAppid(string schoolid){
	return grantMD5(schoolid);
}
inline string getCheckCode(string tokenid, string challenge, string schoolKey){
	string md5SchoolKey = grantMD5(schoolKey);
	string value = tokenid + challenge + md5SchoolKey;
	showLog("Jni.DrService.getCheckCode", "before md5 tokenid:%s", tokenid.c_str());
	showLog("Jni.DrService.getCheckCode", "before md5 challenge:%s", challenge.c_str());
	showLog("Jni.DrService.getCheckCode", "before md5 grantMD5(schoolKey):%s", md5SchoolKey.c_str());
	showLog("Jni.DrService.getCheckCode", "before md5 value:%s", value.c_str());
	return grantMD5(value);
}
/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    NativeInit
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_NativeInit
  (JNIEnv *env, jobject obj){
	bool bFlag = false;
	if(env){
		env->GetJavaVM(&g_jvm);
		if(obj){
			g_JniCallbackObject = env->NewGlobalRef(obj);
			gDrUrlConnection.setCallback(m_JniCallback);
			showLog("Jni.DrService.Native_Init", "succeed");
			return bFlag;
		}
	}
	return bFlag;

}
/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetFirstMacAddress
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetFirstMacAddress
  (JNIEnv *env, jobject obj) {
	string macAddress = tcpSocket::GetFirstMacAddress();
	return env->NewStringUTF(macAddress.c_str());
}
/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetDeviceId
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetDeviceId
  (JNIEnv *env, jobject obj) {
	string macAddress = tcpSocket::GetFirstMacAddress();
	string deviceId = grantMD5(macAddress);
	return env->NewStringUTF(deviceId.c_str());
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetNetworkGate
 * Signature: (Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetNetworkGate
  (JNIEnv *env, jobject, jstring domain, jstring schoolkey, 
   jstring numno, jstring dpi, jstring dspwidth, jstring dspheight,
   jstring os, jstring modelno, jstring appver, jstring tokenid, jstring appname,
   jobject callback){
	// new buffer
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolKey = env->GetStringUTFChars(schoolkey, 0);
	const char *pNumno = env->GetStringUTFChars(numno, 0);
	const char *pDpi = env->GetStringUTFChars(dpi, 0);
	const char *pDspwidth = env->GetStringUTFChars(dspwidth, 0);
	const char *pDspheight = env->GetStringUTFChars(dspheight, 0);
	const char *pOs = env->GetStringUTFChars(os, 0);
	const char *pModelno = env->GetStringUTFChars(modelno, 0);
	const char *pAppver = env->GetStringUTFChars(appver, 0);
	const char *pTokenid = env->GetStringUTFChars(tokenid, 0);
	const char *pAppname = env->GetStringUTFChars(appname, 0);

	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(NETAPP_PATH);
	gDrUrlConnection.setPath(GET_GATE_PATH);
	gDrUrlConnection.addParam(SCHOOLKEY, pSchoolKey);
	gDrUrlConnection.addParam(GW_NUMNO,pNumno);
	gDrUrlConnection.addParam(GW_DPI,pDpi);
	gDrUrlConnection.addParam(GW_DSPWIDTH,pDspwidth);
	gDrUrlConnection.addParam(GW_DSPHEIGHT,pDspheight);
	gDrUrlConnection.addParam(GW_OS,pOs);
	gDrUrlConnection.addParam(GW_MODELNO,pModelno);
	gDrUrlConnection.addParam(GW_APPPVER,pAppver);
	gDrUrlConnection.addParam(GW_TOKENID,pTokenid);
	gDrUrlConnection.addParam(GW_APPNAME,pAppname);
	
	long id = gDrUrlConnection.startRequest();

	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetNetworkGate");
		bFlag = true;
	}

	// release buffer
	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolkey, pSchoolKey);
	env->ReleaseStringUTFChars(numno, pNumno);
	env->ReleaseStringUTFChars(dpi, pDpi);
	env->ReleaseStringUTFChars(dspwidth, pDspwidth);
	env->ReleaseStringUTFChars(dspheight, pDspheight);
	env->ReleaseStringUTFChars(os, pOs);
	env->ReleaseStringUTFChars(modelno, pModelno);
	env->ReleaseStringUTFChars(appver, pAppver);
	env->ReleaseStringUTFChars(tokenid, pTokenid);
	env->ReleaseStringUTFChars(appname, pAppname);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetTours
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetTours
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring lastupdate,
		  jstring dspwidth,  jstring dspheight, jstring dpi, jstring numno, jstring os,
		  jstring modelno, jobject callback){
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pLastupdate = env->GetStringUTFChars(lastupdate, 0);
	const char *pDspwidth= env->GetStringUTFChars(dspwidth, 0);
	const char *pDspheight = env->GetStringUTFChars(dspheight, 0);
	const char *pDpi = env->GetStringUTFChars(dpi, 0);
	const char *pNumno = env->GetStringUTFChars(numno, 0);
	const char *pOs= env->GetStringUTFChars(os, 0);
	const char *pModelno = env->GetStringUTFChars(modelno, 0);

	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(TOURS_GETCLIPKG);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(TOURS_LASTMDATE, pLastupdate);
	gDrUrlConnection.addParam(TOURS_DSPWIDTH, pDspwidth);
	gDrUrlConnection.addParam(TOURS_DSPHEIGHT, pDspheight);
	gDrUrlConnection.addParam(TOURS_DPI, pDpi);
	gDrUrlConnection.addParam(TOURS_OS, pOs);
	gDrUrlConnection.addParam(TOURS_MODELNO, pModelno);
	gDrUrlConnection.addParam(TOURS_NUMNO, pNumno);
	long id = gDrUrlConnection.startRequest();

	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetTours");
		bFlag = true;
	}

	jobject obj = env->NewGlobalRef(callback);
	g_ObjectMap.insert(JNICALLBACKOBJECTMAP::value_type(id, obj));
	// release buffer
	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(lastupdate, pLastupdate);
	env->ReleaseStringUTFChars(dspheight, pDspheight);
	env->ReleaseStringUTFChars(dspwidth, pDspwidth);
	env->ReleaseStringUTFChars(dpi, pDpi);
	env->ReleaseStringUTFChars(os, pOs);
	env->ReleaseStringUTFChars(modelno, pModelno);
	env->ReleaseStringUTFChars(numno, pNumno);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    LoginGateway
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_LoginGateway
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring userid, jstring pwd, jstring indetify, jstring packageName, jobject callback){
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pUserId = env->GetStringUTFChars(userid, 0);
	const char *pPwd = env->GetStringUTFChars(pwd, 0);
	const char *pIdentify = env->GetStringUTFChars(indetify, 0);
	const char *pPackageName = env->GetStringUTFChars(packageName, 0);

	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(LOGIN_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(LOGIN_USERID, pUserId);
	gDrUrlConnection.addParam(LOGIN_PWD, pPwd);
	string tokenid = getTokenid(pIdentify, pPackageName);
	gDrUrlConnection.addParam(LOGIN_TOKEN, tokenid);
	gDrUrlConnection.addParam(LOGIN_DEVICETYPE,"1");  //Android�豸����Ϊ1

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "LoginGateway");
		bFlag = true;
	}

	jobject obj = env->NewGlobalRef(callback);
	g_ObjectMap.insert(JNICALLBACKOBJECTMAP::value_type(id, obj));
	// release buffer
	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(userid, pUserId);
	env->ReleaseStringUTFChars(pwd, pPwd);
	env->ReleaseStringUTFChars(indetify, pIdentify);
	env->ReleaseStringUTFChars(packageName, pPackageName);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    Logout
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_Logout
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jobject callback){
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *psessionkey = env->GetStringUTFChars(sessionkey, 0);

	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(LOGOUT_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, psessionkey);
	long id = gDrUrlConnection.startRequest();

	if(-1 != id){
		insertCallbackObject(env, id, callback, "Logout");
		bFlag = true;
	}
	// release buffer
	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, psessionkey);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    PushInfo
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZZLjava/lang/String;Lcom/drcom/drpalm/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_PushInfo
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jboolean ispush, \
		  jboolean issound, jboolean isshake, jstring time, jobject callback){
	showLog("Jni.DrService.PushInfo", "PushInfo");
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pTime = env->GetStringUTFChars(time, 0);

	string strPush = ispush == true?"1":"0";
	string strSound = issound == true?"1":"0";
	string strShake = isshake == true?"1":"0";

	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(PUSH_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	gDrUrlConnection.addParam(PUSH_SWITCH, strPush);
	gDrUrlConnection.addParam(PUSH_SOUND, strSound);
	gDrUrlConnection.addParam(PUSH_SHAKE, strShake);
	gDrUrlConnection.addParam(PUSH_TIME, pTime);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "PushInfo");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(time, pTime);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    KeepAlive
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_KeepAlive
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jobject callback){
	// new buffer
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(KEEPALIVE_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "KeepAlive");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    SubmitProblem
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SubmitProblem
  (JNIEnv * env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring problem,jstring suggestion, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	const char *pProblem = env->GetStringUTFChars(problem, 0);
	const char *pSuggestion = env->GetStringUTFChars(suggestion,0);

	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(SUBMITPROBLEM_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(SUBMITPROBLEM_PROBLEM, pProblem);
	gDrUrlConnection.addParam(SUBMITPROBLEM_SUGGESTION, pSuggestion);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "SubmitProblem");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(problem, pProblem);
	env->ReleaseStringUTFChars(suggestion, pSuggestion);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    SetUserEmail
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SetUserEmail
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring email, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	const char *pEmail = env->GetStringUTFChars(email, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(SETUSEREMAIL_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(EMAIL, pEmail);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "SetUserEmail");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(email, pEmail);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetNewsModuleInfoList
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetNewsModuleInfoList
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETNEWSMODULEINFOLIST_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetNewsInfoList");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetEventsModuleInfoList
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetEventsModuleInfoList
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETEVENTSMODULEINFOLIST_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetNewsInfoList");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetLastUpdate
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetLastUpdate
(JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey,jstring deviceid,jboolean hassessionkey,jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	const char *pDeviceid = env->GetStringUTFChars(deviceid, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETLASTUPDATE_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	if(hassessionkey)
	{
		gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	}
	gDrUrlConnection.addParam(REQUEST_TOKENID, pDeviceid);
	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetLastUpdate");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(deviceid, pDeviceid);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetSchoolList
 * Signature: (Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetSchoolList
  (JNIEnv *env, jobject, jstring domain, jstring parentid, jstring numno , jstring appver, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pParentId = env->GetStringUTFChars(parentid, 0);
	const char *pNumno = env->GetStringUTFChars(numno, 0);
	const char *pAppver = env->GetStringUTFChars(appver, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(NETAPP_PATH);
	gDrUrlConnection.setPath(GETSCHOOLLIST_PATH);
	gDrUrlConnection.addParam(GETSCHOOLLIST_PARENTID, pParentId);
	gDrUrlConnection.addParam(GW_NUMNO,pNumno);
	gDrUrlConnection.addParam(GW_APPPVER,pAppver);

	showLog("Jni.DrService.GetSchoolList", "ParentId:%s", pParentId);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetSchoolList");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(parentid, pParentId);
	env->ReleaseStringUTFChars(numno, pNumno);
	env->ReleaseStringUTFChars(appver, pAppver);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    SearchSchool
 * Signature: (Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SearchSchool
  (JNIEnv *env, jobject, jstring domain, jstring appid , jstring searchkey, jstring lang, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSearchKey = env->GetStringUTFChars(searchkey, 0);
	const char *pappid = env->GetStringUTFChars(appid, 0);
	const char *pLang = env->GetStringUTFChars(lang, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(NETAPP_PATH);
	gDrUrlConnection.setPath(SEARCHSCHOOL_PATH);
	gDrUrlConnection.addParam(SEARCHSCHOOL_KEYWORD, pSearchKey);
	gDrUrlConnection.addParam(SEARCHSCHOOL_APPID, pappid);
	gDrUrlConnection.addParam(SEARCHSCHOOL_LANG, pLang);
	
	showLog("Jni.DrService.SearchSchool", "SearchKey:%s", pSearchKey);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "SearchSchool");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(searchkey, pSearchKey);
	env->ReleaseStringUTFChars(appid, pappid);
	env->ReleaseStringUTFChars(lang, pLang);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetNews
 * Signature: (Ljava/lang/String;Ljava/lang/String;IILcom/drcom/drpalm/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetNews
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring category, jstring lastupdate, jobject callback){
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pCategory = env->GetStringUTFChars(category, 0);
	const char *pLastUpdate = env->GetStringUTFChars(lastupdate, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETNEWSLIST_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(NEWS_CHANNEL, pCategory);
	gDrUrlConnection.addParam(NEWS_LASTUPDATE, pLastUpdate);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetNews");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(category, pCategory);
	env->ReleaseStringUTFChars(lastupdate, pLastUpdate);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetNewsDetail
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetNewsDetail
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring newsid, jstring allfield, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolid = env->GetStringUTFChars(schoolid, 0);
	const char *pNewsId  = env->GetStringUTFChars(newsid, 0);
	const char *pAllField = env->GetStringUTFChars(allfield, 0);


	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETNEWSDETAIL_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolid);
	gDrUrlConnection.addParam(GETNEWSDETAIL_NEWSID, pNewsId);
	gDrUrlConnection.addParam(GETNEWSDETAIL_ALLFIELD,pAllField);

	showLog("Jni.DrService.GetNewsDetail", "NewsId:%s", pNewsId);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetNewsDetail");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolid);
	env->ReleaseStringUTFChars(newsid, pNewsId);
	env->ReleaseStringUTFChars(allfield, pAllField);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    SearchNews
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SearchNews
  (JNIEnv *env, jobject, jstring domain,jstring schoolid,jstring searchkey,jstring start, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId  = env->GetStringUTFChars(schoolid, 0);
	const char *pSearchKey = env->GetStringUTFChars(searchkey, 0);
	const char *pStart    = env->GetStringUTFChars(start, 0);
	//const char *pLimit    = env->GetStringUTFChars(limit, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(SEARCHNEWS_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SEARCHNEWS_SEARCHWORD, pSearchKey);
	gDrUrlConnection.addParam(SEARCHNEWS_START, pStart);
	//gDrUrlConnection.addParam(SEARCHNEWS_LIMIT, pLimit);

	showLog("Jni.DrService.SearchNews", "SearchKey:%s", pSearchKey);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "SearchNews");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(searchkey, pSearchKey);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(start, pStart);
	//env->ReleaseStringUTFChars(limit, pLimit);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    PutConSult
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_PutConSult
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring username, jstring email, jstring phone,jstring title, jstring content, jstring type, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolid = env->GetStringUTFChars(schoolid, 0);
	const char *pUsername  = env->GetStringUTFChars(username, 0);
	const char *pEmail = env->GetStringUTFChars(email, 0);
	const char *pPhone  = env->GetStringUTFChars(phone, 0);
	const char *pTitle  = env->GetStringUTFChars(title, 0);
	const char *pContent = env->GetStringUTFChars(content, 0);
	showLog("Jni.DrService.PutConSult", "Content:%s", pContent);
	const char *pType = env->GetStringUTFChars(type, 0);
	showLog("Jni.DrService.PutConSult", "Type:%s", pType);

	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(PUTCONSULT_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolid);
	gDrUrlConnection.addParam(PUTCONSULT_USERNAME, pUsername);
	gDrUrlConnection.addParam(PUTCONSULT_EMAIL, pEmail);
	gDrUrlConnection.addParam(PUTCONSULT_PHONE, pPhone);
	gDrUrlConnection.addParam(PUTCONSULT_TITLE, pTitle);
	gDrUrlConnection.addParam(PUTCONSULT_CONTENT, pContent);
	gDrUrlConnection.addParam(PUTCONSULT_TYPE, pType);

	showLog("Jni.DrService.PutConSult", "gDrUrlConnection:%s", pType);
	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "PutConSult");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolid);
	env->ReleaseStringUTFChars(username, pUsername);
	env->ReleaseStringUTFChars(email, pEmail);
	env->ReleaseStringUTFChars(phone, pPhone);
	env->ReleaseStringUTFChars(title, pTitle);
	env->ReleaseStringUTFChars(content, pContent);
	env->ReleaseStringUTFChars(type, pType);
	return bFlag;
}

/*
 * 2.3.3.1.1 通告获取列表接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetEventList
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetEventList
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring category, jstring lastupdate, jstring lastreadtime,jobject callback){
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pCategory = env->GetStringUTFChars(category, 0);
	const char *pLastUpdate = env->GetStringUTFChars(lastupdate, 0);
	const char *pLastReadTime = env->GetStringUTFChars(lastreadtime, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETEVENTSLIST_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	gDrUrlConnection.addParam(GETEVENTSLIST_CATEGORY, pCategory);
	gDrUrlConnection.addParam(GETEVENTSLIST_LASTUPDATE, pLastUpdate);
	gDrUrlConnection.addParam(GETEVENTSLIST_LASTREADTIME, pLastReadTime);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetEventList");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(category, pCategory);
	env->ReleaseStringUTFChars(lastupdate, pLastUpdate);
	env->ReleaseStringUTFChars(lastreadtime, pLastReadTime);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetEventDetail
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetEventDetail
  (JNIEnv *env, jobject, jstring domain , jstring schoolid, jstring sessionkey, jstring eventid, jstring allfield, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pEventId = env->GetStringUTFChars(eventid, 0);
	const char *pAllField = env->GetStringUTFChars(allfield, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETEVENTDETAIL_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	gDrUrlConnection.addParam(EVENT_ID, pEventId);
	gDrUrlConnection.addParam(GETEVENTDETAIL_ALLFIELD, pAllField);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetEventDetail");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(eventid, pEventId);
	env->ReleaseStringUTFChars(allfield, pAllField);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetEventReadInfo
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetEventReadInfo
  (JNIEnv *env, jobject, jstring domain , jstring schoolid, jstring sessionkey, jstring eventid, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pEventId = env->GetStringUTFChars(eventid, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETEVENTREADINFO_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	gDrUrlConnection.addParam(EVENT_ID, pEventId);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetEventDetail");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(eventid, pEventId);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetPublishEventList
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetPublishEventList
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring category, jstring lastupdate, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pCategory = env->GetStringUTFChars(category, 0);
	const char *pLastupdate = env->GetStringUTFChars(lastupdate, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETPUBLISHEVENTSLIST_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	gDrUrlConnection.addParam(GETEVENTSLIST_CATEGORY, pCategory);
	gDrUrlConnection.addParam(GETEVENTSLIST_LASTUPDATE, pLastupdate);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetPublishEventList");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(category, pCategory);
	env->ReleaseStringUTFChars(lastupdate, pLastupdate);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetPublishEventDetail
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetPublishEventDetail
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring eventid, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pEventid = env->GetStringUTFChars(eventid, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETPUBLISSHEVENTDETAIL_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	gDrUrlConnection.addParam(EVENT_ID, pEventid);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetPublishEventDetail");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(eventid, pEventid);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    AutoAnswer
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_AutoAnswer
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring eventid, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pEventid = env->GetStringUTFChars(eventid, 0);
	//const char *pAlbumid = env->GetStringUTFChars(albumid, 0);


	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(AUTOAWSEVENT);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	//if( 0 != strcmp(pEventid, "") )
	gDrUrlConnection.addParam(EVENT_ID, pEventid);
	//gDrUrlConnection.addParam(ALBUM_ID, pAlbumid);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "AutoAnswer");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(eventid, pEventid);
	//env->ReleaseStringUTFChars(albumid, pAlbumid);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    AutoAnswerList
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_AutoAnswerList
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring eventids, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pEventids = env->GetStringUTFChars(eventids, 0);
	//const char *pAlbumids = env->GetStringUTFChars(albumids, 0);

	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(BATAWSEVENT);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	//if(0 != strcmp(pEventids, ""))
	gDrUrlConnection.addParam(EVENT_IDS, pEventids);
	//gDrUrlConnection.addParam(ALBUM_IDS, pAlbumids);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "AutoAnswerList");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(eventids, pEventids);
	//env->ReleaseStringUTFChars(albumids, pAlbumids);
	return bFlag;
}
/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetReplyInfo
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetReplyInfo
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring eventid, jstring aswpubid, jstring lastawstime, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey,0);
	const char *pEventid = env->GetStringUTFChars(eventid, 0);
	const char *pAswpubid = env->GetStringUTFChars(aswpubid, 0);
	const char *pLastAwstime = env->GetStringUTFChars(lastawstime,0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(EVENTS_GETREPLYINFO_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(EVENT_ID, pEventid);
	gDrUrlConnection.addParam(EVENTS_GETREPLYINFO_ASWPUBID, pAswpubid);
	gDrUrlConnection.addParam(EVENTS_GETREPLYINFO_LASTAWSTIME, pLastAwstime);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetReplyInfo");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(eventid, pEventid);
	env->ReleaseStringUTFChars(aswpubid, pAswpubid);
	env->ReleaseStringUTFChars(lastawstime, pLastAwstime);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    ReplyPost
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_ReplyPost
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring eventid, jstring aswpubid, jstring body, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey,0);
	const char *pEventid = env->GetStringUTFChars(eventid, 0);
	const char *pAwsPubId = env->GetStringUTFChars(aswpubid, 0);
	const char *pBody = env->GetStringUTFChars(body, 0);

	showLog("Jni.DrService.ReplyEvents", "pDomain:%s", pDomain);
	showLog("Jni.DrService.ReplyEvents", "pSchoolId:%s", pSchoolId);
	showLog("Jni.DrService.ReplyEvents", "pSessionkey:%s", pSessionkey);
	showLog("Jni.DrService.ReplyEvents", "pEventid:%s", pEventid);
	//showLog("Jni.DrService.ReplyEvents", "pAlbumid:%s", pAlbumid);
	//showLog("Jni.DrService.ReplyEvents", "pOriawsid:%s", pOriawsid);
	//showLog("Jni.DrService.ReplyEvents", "pTitle:%s", pTitle);
	showLog("Jni.DrService.ReplyEvents", "pBody:%s", pBody);

	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(REPLYPOST_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(EVENT_ID,pEventid);
	//gDrUrlConnection.addParam(ALBUM_ID,pAlbumid);
	gDrUrlConnection.addParam(REPLYPOST_AWSPUBID, pAwsPubId);
	gDrUrlConnection.addParam(REPLYPOST_BODY,pBody);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "ReplyPost");
		bFlag = true;
	}


	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(eventid, pEventid);
	//env->ReleaseStringUTFChars(albumid, pAlbumid);
	env->ReleaseStringUTFChars(aswpubid, pAwsPubId);
	//env->ReleaseStringUTFChars(title, pTitle);
	env->ReleaseStringUTFChars(body, pBody);

	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetOrganization
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetOrganization
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring lastupdate, jobject callback){
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pLastupdate = env->GetStringUTFChars(lastupdate, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(ORGINFO_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	gDrUrlConnection.addParam(ORGINFO_LASTUPDATE, pLastupdate);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetOrganization");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(lastupdate, pLastupdate);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetContactMsgs
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetContactMsgs
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring contactid, jstring lastupdate, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pContactId = env->GetStringUTFChars(contactid, 0);
	const char *pLastupdate = env->GetStringUTFChars(lastupdate, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETCONTACTMSGS_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	gDrUrlConnection.addParam(GETCONTACTMSGS_CONTACTID, pContactId);
	gDrUrlConnection.addParam(GETCONTACTMSGS_LASTUPDATE, pLastupdate);


	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetContactMsgs");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(contactid, pContactId);
	env->ReleaseStringUTFChars(lastupdate, pLastupdate);

	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    SendContactMsg
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SendContactMsg
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring contactid, jstring body, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pContactId = env->GetStringUTFChars(contactid, 0);
	const char *pBody = env->GetStringUTFChars(body, 0);

	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(SENDCONTACTMSGS_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	gDrUrlConnection.addParam(GETCONTACTMSGS_CONTACTID, pContactId);
	gDrUrlConnection.addParam(SENDCONTACTMSGS_BODY, pBody);


	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "SendContactMsg");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(contactid, pContactId);
	env->ReleaseStringUTFChars(body, pBody);

	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetSysMsgs
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetSysMsgs
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring lastupdate, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pLastupdate = env->GetStringUTFChars(lastupdate, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETSYSMSGS_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	gDrUrlConnection.addParam(GETCONTACTMSGS_LASTUPDATE, pLastupdate);


	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetSysMsgs");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(lastupdate, pLastupdate);

	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetSysMsgContent
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetSysMsgContent
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, jstring sysmsgid, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pSysMsgid = env->GetStringUTFChars(sysmsgid, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETSYSMSGSCONTENT_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	gDrUrlConnection.addParam(GETSYSMSGSCONTENT_SYSMSGID, pSysMsgid);


	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetSysMsgContent");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(sysmsgid, pSysMsgid);

	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    SubmitEvent4Kids
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/ArrayList;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SubmitEvent4Kids
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, \
		  jstring eventid, jstring type, jstring objecttype, jstring oristatus, \
		  jstring ownerid, jstring owner, jstring start, jstring end, jstring shortloc,\
		  jstring title, jstring body, jstring emergent, jstring locurl, jstring thumbname, jobject attarray, jobject callback){
	showLog("Jni.DrService.SubmitEvent4Kids", "SubmitEvent4Kids");
	// get string
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pEventId = env->GetStringUTFChars(eventid, 0);
	const char *pType = env->GetStringUTFChars(type, 0);
	const char *pObjecttype = env->GetStringUTFChars(objecttype, 0);
	const char *pOristatus = env->GetStringUTFChars(oristatus, 0);
	const char *pOwnerid = env->GetStringUTFChars(ownerid, 0);
	const char *pOwner = env->GetStringUTFChars(owner, 0);
	const char *pStart = env->GetStringUTFChars(start, 0);
	const char *pEnd = env->GetStringUTFChars(end, 0);
	const char *pShortloc = env->GetStringUTFChars(shortloc, 0);
	const char *pTitle = env->GetStringUTFChars(title, 0);
	const char *pBody = env->GetStringUTFChars(body, 0);
	const char *pEmergent = env->GetStringUTFChars(emergent, 0);
	const char *pLocurl = env->GetStringUTFChars(locurl, 0);
	const char *pThumbname = env->GetStringUTFChars(thumbname, 0);


	string strDescription = "description";
	string strJsonFiledes = "";
	//jstring FileTitle = "";
	//jstring Filefrom = "";
	//const char *pComment = env->GetStringUTFChars(comment, 0);
	showLog("Jni.DrService.SubmitEvent4Kids", "get jstring ok");


	// get array_list
	bool bHasAtt =  false;
	jclass cls_arraylist = env->GetObjectClass(attarray);
	jmethodID mth_arraylist_get  = env->GetMethodID(cls_arraylist, "get", "(I)Ljava/lang/Object;");
	jmethodID mth_arraylist_size = env-> GetMethodID(cls_arraylist, "size", "()I");
	jlong arraylen = env->CallIntMethod(attarray, mth_arraylist_size);
	Json::Value root;
	root = Json::arrayValue;
	for(long i=0; i< arraylen; i++){
		bHasAtt = true;
		showLog("Jni.DrService.SubmitEvent4Kids", "arraylen:%ld", arraylen);
		jobject obj_attitem = env->CallObjectMethod(attarray, mth_arraylist_get, i);
		jclass cls_attitem = env->GetObjectClass(obj_attitem);
		showLog("Jni.DrService.SubmitEvent4Kids", "get obj_attitem success");

		//pic description
		jfieldID fid_attitem_description = env->GetFieldID(cls_attitem, "description", "Ljava/lang/String;");
		showLog("Jni.DrService.SubmitEvent4Kids", "get fid_attitem_description success");
		jstring attitem_description = (jstring)env->GetObjectField(obj_attitem, fid_attitem_description);
		showLog("Jni.DrService.SubmitEvent4Kids", "get fid_attitem_description success");
		const char *pAttItemDesc = env->GetStringUTFChars(attitem_description, 0);
		showLog("Jni.DrService.SubmitEvent4Kids", "%s", pAttItemDesc);
		if(pAttItemDesc == NULL)
		{
			strDescription = "";
		}
		else
		{
			strDescription = pAttItemDesc;
		}
		showLog("Jni.DrService.SubmitEvent", "strFileDescription:%s", strDescription.c_str());

		//type
		jfieldID fid_attitem_type = env->GetFieldID(cls_attitem, "type", "Ljava/lang/String;");
		jstring attitem_type = (jstring)env->GetObjectField(obj_attitem, fid_attitem_type);
		const char *pAttItemtype = env->GetStringUTFChars(attitem_type, 0);
		string strtype = "";
		if(pAttItemtype!=NULL)
			strtype = pAttItemtype;
		showLog("Jni.DrService.SubmitEvent", "strType:%s", strtype.c_str());
		//item
		jfieldID fid_attitem_item = env->GetFieldID(cls_attitem, "item", "Ljava/lang/String;");
		jstring attitem_item = (jstring)env->GetObjectField(obj_attitem, fid_attitem_item);
		const char *pAttItem_Item = env->GetStringUTFChars(attitem_item, 0);
		string strItem = "";
		if(pAttItem_Item!=NULL)
			strItem = pAttItem_Item;
		showLog("Jni.DrService.SubmitEvent", "strItem:%s", strItem.c_str());

		jfieldID fid_attitem_id = env->GetFieldID(cls_attitem, "fileId", "Ljava/lang/String;");
		jstring attitem_id = (jstring)env->GetObjectField(obj_attitem, fid_attitem_id);
		const char *pAttItemId = env->GetStringUTFChars(attitem_id, 0);
		string strId = pAttItemId;
		showLog("Jni.DrService.SubmitEvent", "strId:%s", strId.c_str());

		jfieldID fid_attitem_ext = env->GetFieldID(cls_attitem, "fileType", "Ljava/lang/String;");
		jstring attitem_ext = (jstring)env->GetObjectField(obj_attitem, fid_attitem_ext);
		const char *pAttItemext = env->GetStringUTFChars(attitem_ext, 0);
		string strExt = pAttItemext;
		showLog("Jni.DrService.SubmitEvent", "strExt:%s", strExt.c_str());

		string strfile = pAttItemId;
		string strfileext = pAttItemext;
		//strfile += ".";
		//strfile += pAttItemType;
		showLog("Jni.DrService.SubmitEvent", "strfile:%s", strfile.c_str());

		jfieldID fid_attitem_data = env->GetFieldID(cls_attitem, "data", "[B");
		jbyteArray dataarray = (jbyteArray)env->GetObjectField(obj_attitem, fid_attitem_data);

		if(0 != dataarray) {
			showLog("Jni.DrService.SubmitEvent4Kids", "dataarray:%ld", dataarray);
			jbyte *data = env->GetByteArrayElements(dataarray, 0);
			showLog("Jni.DrService.SubmitEvent4Kids", "data:%ld", data);
			jsize datasize = env->GetArrayLength(dataarray);
			showLog("Jni.DrService.SubmitEvent4Kids", "datasize:%ld", datasize);
			gDrUrlConnection.addFile(strfile, strfileext,(char*)data, datasize);
			env->ReleaseByteArrayElements(dataarray, data, JNI_COMMIT);
		}



			Json::Value body;
			body[EVENTS_FILETITLE] = strDescription;
			body[EVENTS_FILEFROM] = "";
			body[EVENTS_TYPE] = strtype;
			body[EVENTS_ITEM] = strItem;

//			Json::FastWriter writer;
//			string filebody = writer.write(body);
//			showLog("Jni.DrService.SubmitEvent4Kids", "filebody:%s", filebody.c_str());
//			string fileKey = DrHttpClient_POST_FILE_PRE;
//			char cFileCount[10];
//			sprintf(cFileCount, "%ld", i);
//			fileKey += cFileCount;
			root.append(body);
//			root[strfile] = body;


		env->ReleaseStringUTFChars(attitem_id, pAttItemId);
		env->ReleaseStringUTFChars(attitem_ext, pAttItemext);
		env->ReleaseStringUTFChars(attitem_type, pAttItemtype);
		env->ReleaseStringUTFChars(attitem_item, pAttItem_Item);
	}
	string fileDesc = "";
	if(bHasAtt) {
		Json::FastWriter writer;
		fileDesc = writer.write(root);
	}
	showLog("Jni.DrService.SubmitEvent4Kids", "fileDesc:%s", fileDesc.c_str());

	gDrUrlConnection.setPost(true);
	//gDrUrlConnection.setKeepAlive(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(EVENTS_SUBMIT_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	if(0 != strcmp(pEventId,""))
		gDrUrlConnection.addParam(OLD_EVENTS_ID, pEventId);
	if(0 != strcmp(pType,""))
		gDrUrlConnection.addParam(EVENTS_TYPE, pType);
	if(0 != strcmp(pObjecttype,""))
		gDrUrlConnection.addParam(EVENTS_OBJTYPE, pObjecttype);
	if(0 != strcmp(pOristatus,""))
		gDrUrlConnection.addParam(EVENTS_ORISTATUS, pOristatus);
	if(0 != strcmp(pOwnerid,""))
		gDrUrlConnection.addParam(EVENTS_OWNERID, pOwnerid);
	if(0 != strcmp(pOwner,""))
		gDrUrlConnection.addParam(EVENTS_OWNER, pOwner);
	if(0 != strcmp(pStart,""))
		gDrUrlConnection.addParam(EVENTS_START, pStart);
	if(0 != strcmp(pEnd,""))
		gDrUrlConnection.addParam(EVENTS_END, pEnd);
	if(0 != strcmp(pShortloc,""))
		gDrUrlConnection.addParam(EVENTS_SHORTLOC, pShortloc);
	if(0 != strcmp(pTitle,""))
		gDrUrlConnection.addParam(EVENTS_TITLE, pTitle);
	if(0 != strcmp(pBody,""))
		gDrUrlConnection.addParam(EVENTS_BODY, pBody);
	if(0 != strcmp(pEmergent, "")){
		gDrUrlConnection.addParam(EVENTS_EMERGENT, pEmergent);
	}
	else {
		gDrUrlConnection.addParam(EVENTS_EMERGENT, "0");
	}
	gDrUrlConnection.addParam(EVENTS_LOCURL, pLocurl);
	gDrUrlConnection.addParam(EVENTS_THUMBNAME, pThumbname);
	//if(0 != strcmp(,"")
	gDrUrlConnection.addParam(EVENTS_FILEDES,fileDesc);



//	if(0 != strcmp(pComment,""))
//	{
//		gDrUrlConnection.addParam(EVENTS_COMMENTS,pComment);
//	}

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "SubmitEvent4Kids");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(eventid, pEventId);
	env->ReleaseStringUTFChars(type, pType);
	env->ReleaseStringUTFChars(objecttype, pObjecttype);
	env->ReleaseStringUTFChars(oristatus, pOristatus);
	env->ReleaseStringUTFChars(ownerid, pOwnerid);
	env->ReleaseStringUTFChars(owner, pOwner);
	env->ReleaseStringUTFChars(start, pStart);
	env->ReleaseStringUTFChars(end, pEnd);
	env->ReleaseStringUTFChars(shortloc, pShortloc);
	env->ReleaseStringUTFChars(title, pTitle);
	env->ReleaseStringUTFChars(body, pBody);
	env->ReleaseStringUTFChars(emergent, pEmergent);
	env->ReleaseStringUTFChars(locurl, pLocurl);
	env->ReleaseStringUTFChars(thumbname, pThumbname);
	return bFlag;
}

//2013-8-28
// 3期新增接口

/*
 * 2.3.3.1.7 获取班级收藏夹接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetClassFavorite 
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetClassFavorite
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jstring lastupdate, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	const char *pLastUpdate = env->GetStringUTFChars(lastupdate, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETCLASSFAVORITE_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(LASTUPDATE, pLastUpdate);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetClassFavorite");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(lastupdate, pLastUpdate);
	return bFlag;
}

/*
 * 2.3.3.1.8 提交班级收藏接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    SubmitClassfav
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SubmitClassfav
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jobject faveventarray, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);

	string eventid = "";
	string status = "N";

	// get event_list
	bool bHasEvents =  false;
	jclass cls_arraylist = env->GetObjectClass(faveventarray);
	jmethodID mth_arraylist_get  = env->GetMethodID(cls_arraylist, "get", "(I)Ljava/lang/Object;");
	jmethodID mth_arraylist_size = env-> GetMethodID(cls_arraylist, "size", "()I");
	jlong arraylen = env->CallIntMethod(faveventarray, mth_arraylist_size);
	Json::Value root;
	root = Json::arrayValue;
	for(long i=0; i< arraylen; i++){
		bHasEvents = true;
		//item
		showLog("Jni.DrService.SubmitClassfav", "arraylen:%ld", arraylen);
		jobject obj_item = env->CallObjectMethod(faveventarray, mth_arraylist_get, i);
		jclass cls_item = env->GetObjectClass(obj_item);
		showLog("Jni.DrService.SubmitClassfav", "get obj_attitem success");
		
		//eventid
		//jfieldID fid_item_eventid = env->GetFieldID(cls_item, "eventid", "Ljava/lang/Integer;");
		//showLog("Jni.DrService.SubmitClassfav", "get fid_item_eventid1 success fid_item_eventid:%ld", fid_item_eventid);
		
		//jobject obj_eventid = env->GetObjectField(obj_item, fid_item_eventid);
		//showLog("Jni.DrService.SubmitClassfav", "get obj_eventid success %ld", obj_eventid);
		//jclass cls_eventid = env->GetObjectClass(obj_eventid);
		//showLog("Jni.DrService.SubmitClassfav", "get cls_eventid success %ld", cls_eventid);
		//取整型
		//jfieldID fid_eventid = env->GetFieldID(cls_eventid, "value", "I");
		//showLog("Jni.DrService.SubmitClassfav", "get fid_eventid success %ld", fid_eventid);
		//eventid = (jint)env->GetObjectField(obj_eventid, fid_eventid);
		//showLog("Jni.DrService.SubmitClassfav", "get eventid success %ld", eventid);
		
		//eventid
		jfieldID fid_item_eventid = env->GetFieldID(cls_item, "mEventid", "Ljava/lang/String;");
		showLog("Jni.DrService.SubmitClassfav", "get fid_item_eventid1 success:%ld",fid_item_eventid);
		jstring item_eventid = (jstring)env->GetObjectField(obj_item, fid_item_eventid);
		showLog("Jni.DrService.SubmitClassfav", "get fid_item_eventid1 success");
		const char *pAttItemEventid = env->GetStringUTFChars(item_eventid, 0);
		if(pAttItemEventid == NULL)
		{
			eventid = "";
		}
		else
		{
			eventid = pAttItemEventid;
		}
		showLog("Jni.DrService.SubmitClassfav", "eventid:%s", eventid.c_str());
		
		
		//status
		jfieldID fid_item_status = env->GetFieldID(cls_item, "mStatus", "Ljava/lang/String;");
		showLog("Jni.DrService.SubmitClassfav", "get fid_item_status1 success:%ld",fid_item_status);
		jstring item_status = (jstring)env->GetObjectField(obj_item, fid_item_status);
		showLog("Jni.DrService.SubmitClassfav", "get fid_item_status2 success");
		const char *pAttItemStatus = env->GetStringUTFChars(item_status, 0);
		
		//showLog("Jni.DrService.SubmitClassfav", "%s", pAttItemStatus);
		if(pAttItemStatus == NULL)
		{
			status = "";
		}
		else
		{
			status = pAttItemStatus;
		}
		showLog("Jni.DrService.SubmitClassfav", "status:%s", status.c_str());
		
		Json::Value body;
		body[FAV_EVENT_ID] = eventid;
		body[FAV_EVENT_STATUS] = status;
		
		root.append(body);
		
		env->ReleaseStringUTFChars(item_eventid, pAttItemEventid);
		env->ReleaseStringUTFChars(item_status, pAttItemStatus);
	}

	string param = "";
	if(bHasEvents) {
		Json::FastWriter writer;
		param = writer.write(root);
	}
	showLog("Jni.DrService.SubmitClassfav", "param:%s", param.c_str());


	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(SUBMITCLASSFAV_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(FAV_EVENT_PARAM,param);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "SubmitClassfav");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	return bFlag;
}


/*
 * 2.3.4.1.1 获取基本资料接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetAccountinfo
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetAccountinfo
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jstring lastupdate, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	const char *pLastUpdate = env->GetStringUTFChars(lastupdate, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETACCOUNTINFO_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(LASTUPDATE, pLastUpdate);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetAccountinfo");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(lastupdate, pLastUpdate);
	return bFlag;
}

/*
 * 2.3.4.1.2 提交基本资料接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    SubmitAccountinfo
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SubmitAccountinfo
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jbyteArray dataarray, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	
	string strfile = "userpic";
	string strfileext = "userpic";
	
	if(0 != dataarray) {
		showLog("Jni.DrService.SubmitAccountinfo", "dataarray:%ld", dataarray);
		jbyte *data = env->GetByteArrayElements(dataarray, 0);
		showLog("Jni.DrService.SubmitAccountinfo", "data:%ld", data);
		jsize datasize = env->GetArrayLength(dataarray);
		showLog("Jni.DrService.SubmitAccountinfo", "datasize:%ld", datasize);
		gDrUrlConnection.addFile(strfile, strfileext,(char*)data, datasize);
		env->ReleaseByteArrayElements(dataarray, data, JNI_COMMIT);
	}
	
	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(SUBMITACCOUNTINFO_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	
	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "SubmitAccountinfo");
		bFlag = true;
	}
	
	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	
	return bFlag;
}

/*
 * 2.3.1.1 获取导航页显示列表
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetNavigation
 * Signature: (Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetNavigation
  (JNIEnv *env, jobject, jstring domain, jstring appid, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pAppId = env->GetStringUTFChars(appid, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(NETAPP_PATH);
	gDrUrlConnection.setPath(GETNAVIGATION_PATH);
	gDrUrlConnection.addParam(APPID, pAppId);

	showLog("Jni.DrService.GetNavigation", "AppId:%s", pAppId);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetNavigation");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(appid, pAppId);
	return bFlag;
}

/*
 * 2.3.4.2.1 获取个人相册接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetUseralbum
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetUseralbum
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jstring lastupdate, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	const char *pLastUpdate = env->GetStringUTFChars(lastupdate, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETUSERALBUM_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(LASTUPDATE, pLastUpdate);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetUseralbum");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(lastupdate, pLastUpdate);
	return bFlag;
}

/*
 * 2.3.4.2.2 提交个人相册接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    SubmitUseralbum
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SubmitUseralbum
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jobject attarray, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	
	string strDescription = "description";
	string strJsonFiledes = "";
	showLog("Jni.DrService.SubmitSeralbum", "get jstring ok");
	
	//封装JSON
	bool bHasAtt =  false;
	jclass cls_arraylist = env->GetObjectClass(attarray);
	jmethodID mth_arraylist_get  = env->GetMethodID(cls_arraylist, "get", "(I)Ljava/lang/Object;");
	jmethodID mth_arraylist_size = env-> GetMethodID(cls_arraylist, "size", "()I");
	jlong arraylen = env->CallIntMethod(attarray, mth_arraylist_size);
	Json::Value root;
	root = Json::arrayValue;
	for(long i=0; i< arraylen; i++){
		bHasAtt = true;
		showLog("Jni.DrService.SubmitSeralbum", "arraylen:%ld", arraylen);
		jobject obj_attitem = env->CallObjectMethod(attarray, mth_arraylist_get, i);
		jclass cls_attitem = env->GetObjectClass(obj_attitem);
		showLog("Jni.DrService.SubmitSeralbum", "get obj_attitem success");
	
		//pic description
		jfieldID fid_attitem_description = env->GetFieldID(cls_attitem, "des", "Ljava/lang/String;");
		showLog("Jni.DrService.SubmitSeralbum", "get fid_attitem_description1 success");
		jstring attitem_description = (jstring)env->GetObjectField(obj_attitem, fid_attitem_description);
		showLog("Jni.DrService.SubmitSeralbum", "get fid_attitem_description2 success");
		const char *pAttItemDesc = env->GetStringUTFChars(attitem_description, 0);
		showLog("Jni.DrService.SubmitSeralbum", "%s", pAttItemDesc);
		if(pAttItemDesc == NULL)
		{
			strDescription = "";
		}
		else
		{
			strDescription = pAttItemDesc;
		}
		showLog("Jni.DrService.SubmitSeralbum", "strFileDescription:%s", strDescription.c_str());
		
		//status
		jfieldID fid_attitem_status = env->GetFieldID(cls_attitem, "status", "Ljava/lang/String;");
		jstring attitem_status = (jstring)env->GetObjectField(obj_attitem, fid_attitem_status);
		const char *pAttItemstatus = env->GetStringUTFChars(attitem_status, 0);
		string strstatus = "";
		if(pAttItemstatus!=NULL)
			strstatus = pAttItemstatus;
		showLog("Jni.DrService.SubmitSeralbum", "strStatus:%s", strstatus.c_str());
		
		//id
		jfieldID fid_attitem_id = env->GetFieldID(cls_attitem, "imgid", "Ljava/lang/String;");
		jstring attitem_id = (jstring)env->GetObjectField(obj_attitem, fid_attitem_id);
		const char *pAttItem_Id = env->GetStringUTFChars(attitem_id, 0);
		string strId = "";
		if(pAttItem_Id!=NULL)
			strId = pAttItem_Id;
		showLog("Jni.DrService.SubmitSeralbum", "strId:%s", strId.c_str());
		
		//filename
		jfieldID fid_attitem_filename = env->GetFieldID(cls_attitem, "filename", "Ljava/lang/String;");
		jstring attitem_filename = (jstring)env->GetObjectField(obj_attitem, fid_attitem_filename);
		const char *pAttItem_Filename = env->GetStringUTFChars(attitem_filename, 0);
		string strFilename = "";
		if(pAttItem_Filename!=NULL)
			strFilename = pAttItem_Filename;
		showLog("Jni.DrService.SubmitSeralbum", "strFilename:%s", strFilename.c_str());
		
		//fileid
		jfieldID fid_attitem_fileid = env->GetFieldID(cls_attitem, "fileId", "Ljava/lang/String;");
		jstring attitem_fileid = (jstring)env->GetObjectField(obj_attitem, fid_attitem_fileid);
		const char *pAttItemfileId = env->GetStringUTFChars(attitem_fileid, 0);
		string strfileId = pAttItemfileId;
		showLog("Jni.DrService.SubmitSeralbum", "strfileId:%s", strfileId.c_str());

		//fileType
		jfieldID fid_attitem_ext = env->GetFieldID(cls_attitem, "fileType", "Ljava/lang/String;");
		jstring attitem_ext = (jstring)env->GetObjectField(obj_attitem, fid_attitem_ext);
		const char *pAttItemext = env->GetStringUTFChars(attitem_ext, 0);
		string strExt = pAttItemext;
		showLog("Jni.DrService.SubmitSeralbum", "strExt:%s", strExt.c_str());

		string strfile = pAttItemfileId;	//文件名
		string strfileext = pAttItemext;	//后缀名
		
		//图片
		jfieldID fid_attitem_data = env->GetFieldID(cls_attitem, "data", "[B");
		jbyteArray dataarray = (jbyteArray)env->GetObjectField(obj_attitem, fid_attitem_data);
		if(0 != dataarray) {
			showLog("Jni.DrService.SubmitSeralbum", "dataarray:%ld", dataarray);
			jbyte *data = env->GetByteArrayElements(dataarray, 0);
			showLog("Jni.DrService.SubmitSeralbum", "data:%ld", data);
			jsize datasize = env->GetArrayLength(dataarray);
			showLog("Jni.DrService.SubmitSeralbum", "datasize:%ld", datasize);
			gDrUrlConnection.addFile(strfile, strfileext,(char*)data, datasize);
			env->ReleaseByteArrayElements(dataarray, data, JNI_COMMIT);
		}
		
		Json::Value body;
		if(strId.compare("") != 0){
			body[USERALBUM_IMGID] = strId;
		}
		if(strFilename.compare("") != 0){
			body[USERALBUM_FILENAME] = strFilename;
		}
		body[USERALBUM_DES] = strDescription;
		body[USERALBUM_STATUS] = strstatus;
		
		root.append(body);
		
		env->ReleaseStringUTFChars(attitem_id, pAttItem_Id);
		env->ReleaseStringUTFChars(attitem_filename, pAttItem_Filename);
		env->ReleaseStringUTFChars(attitem_fileid, pAttItemfileId);
		env->ReleaseStringUTFChars(attitem_ext, pAttItemext);
		env->ReleaseStringUTFChars(attitem_status, pAttItemstatus);
		env->ReleaseStringUTFChars(attitem_description, pAttItemDesc);
	}
	
	string fileDesc = "";
	if(bHasAtt) {
		Json::FastWriter writer;
		fileDesc = writer.write(root);
	}
	showLog("Jni.DrService.SubmitSeralbum", "fileDesc:%s", fileDesc.c_str());
	
	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(SUBIMT_USERALBUM_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(USERALBUM_PARAM,fileDesc);
	
	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "SubmitUseralbum");
		bFlag = true;
	}
	
	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	return bFlag;
}

/*
 * 2.3.4.3.1 获取成长点滴接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetGrowdiary
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetGrowdiary
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jstring lastupdate, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	const char *pLastUpdate = env->GetStringUTFChars(lastupdate, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETGROWDIARY_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(LASTUPDATE, pLastUpdate);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetGrowdiary");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(lastupdate, pLastUpdate);
	return bFlag;
}

/*
 * 2.3.4.3.2 提交成长点滴接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    SubmitGrowdiary
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SubmitGrowdiary
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jobject objarray, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);

	string diaryid = "";
	string title = "";
	string contect = "";
	string status = "";

	// get obj_list
	bool bHasObjs =  false;
	jclass cls_arraylist = env->GetObjectClass(objarray);
	jmethodID mth_arraylist_get  = env->GetMethodID(cls_arraylist, "get", "(I)Ljava/lang/Object;");
	jmethodID mth_arraylist_size = env-> GetMethodID(cls_arraylist, "size", "()I");
	jlong arraylen = env->CallIntMethod(objarray, mth_arraylist_size);
	Json::Value root;
	root = Json::arrayValue;
	for(long i=0; i< arraylen; i++){
		bHasObjs = true;
		//item
		showLog("Jni.DrService.SubmitGrowdiary", "arraylen:%ld", arraylen);
		jobject obj_item = env->CallObjectMethod(objarray, mth_arraylist_get, i);
		jclass cls_item = env->GetObjectClass(obj_item);
		showLog("Jni.DrService.SubmitGrowdiary", "get obj_attitem success");
		
		//diaryid
		jfieldID fid_item_diaryid = env->GetFieldID(cls_item, "diaryid", "Ljava/lang/String;");
		showLog("Jni.DrService.SubmitGrowdiary", "get fid_item_diaryid success");
		jstring item_diaryid = (jstring)env->GetObjectField(obj_item, fid_item_diaryid);
		showLog("Jni.DrService.SubmitGrowdiary", "get item_diaryid success");
		const char *pAttItemDiaryid = env->GetStringUTFChars(item_diaryid, 0);
		showLog("Jni.DrService.SubmitGrowdiary", "%s", pAttItemDiaryid);
		if(pAttItemDiaryid == NULL)
		{
			diaryid = "";
		}
		else
		{
			diaryid = pAttItemDiaryid;
		}
		
		//title
		jfieldID fid_item_title = env->GetFieldID(cls_item, "title", "Ljava/lang/String;");
		showLog("Jni.DrService.SubmitGrowdiary", "get fid_item_title success");
		jstring item_title = (jstring)env->GetObjectField(obj_item, fid_item_title);
		showLog("Jni.DrService.SubmitGrowdiary", "get item_title success");
		const char *pAttItemTitle = env->GetStringUTFChars(item_title, 0);
		showLog("Jni.DrService.SubmitGrowdiary", "%s", pAttItemTitle);
		if(pAttItemDiaryid == NULL)
		{
			title = "";
		}
		else
		{
			title = pAttItemTitle;
		}
		
		//contect
		jfieldID fid_item_contect = env->GetFieldID(cls_item, "contect", "Ljava/lang/String;");
		showLog("Jni.DrService.SubmitGrowdiary", "get fid_item_contect success");
		jstring item_contect = (jstring)env->GetObjectField(obj_item, fid_item_contect);
		showLog("Jni.DrService.SubmitGrowdiary", "get item_contect success");
		const char *pAttItemContect = env->GetStringUTFChars(item_contect, 0);
		showLog("Jni.DrService.SubmitGrowdiary", "%s", pAttItemContect);
		if(pAttItemContect == NULL)
		{
			contect = "";
		}
		else
		{
			contect = pAttItemContect;
		}
		
		//status
		jfieldID fid_item_status = env->GetFieldID(cls_item, "status", "Ljava/lang/String;");
		showLog("Jni.DrService.SubmitGrowdiary", "get fid_item_status success");
		jstring item_status = (jstring)env->GetObjectField(obj_item, fid_item_status);
		showLog("Jni.DrService.SubmitGrowdiary", "get fid_item_status success");
		const char *pAttItemStatus = env->GetStringUTFChars(item_status, 0);
		showLog("Jni.DrService.SubmitGrowdiary", "%s", pAttItemStatus);
		if(pAttItemStatus == NULL)
		{
			status = "";
		}
		else
		{
			status = pAttItemStatus;
		}
		showLog("Jni.DrService.SubmitGrowdiary", "status:%s", status.c_str());
		
		Json::Value body;
		if(status.compare("N") != 0){
			showLog("Jni.DrService.SubmitGrowdiary", "status no diaryid");
			body[GROWDIARY_ID] = diaryid;
		}
		body[GROWDIARY_TITLE] = title;
		body[GROWDIARY_CONTECT] = contect;
		body[GROWDIARY_STATUS] = status;
		
		root.append(body);
		
		env->ReleaseStringUTFChars(item_diaryid, pAttItemDiaryid);
		env->ReleaseStringUTFChars(item_title, pAttItemTitle);
		env->ReleaseStringUTFChars(item_contect, pAttItemContect);
		env->ReleaseStringUTFChars(item_status, pAttItemStatus);
	}

	string param = "";
	
	if(bHasObjs) {
		Json::FastWriter writer;
		param = writer.write(root);
	}
	showLog("Jni.DrService.SubmitGrowdiary", "param:%s", param.c_str());


	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(SUBMITGROWDIARY_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(FAV_EVENT_PARAM,param);
	
	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "SubmitGrowdiary");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetNavigationList
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetNavigationList
  (JNIEnv *env, jobject, jstring domain, jstring appid, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pAppId = env->GetStringUTFChars(appid, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(NETAPP_PATH);
	gDrUrlConnection.setPath(GETNAVIGATIONLIST_PATH);
	gDrUrlConnection.addParam(GETNAVIGATIONLIST_APPID, pAppId);


	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetSysMsgContent");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(appid, pAppId);

	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni_SubmitDelEvent
 * Method:    SubmitDelEvent
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SubmitDelEvent
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jstring eventid, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	const char *pEventid = env->GetStringUTFChars(eventid, 0);

	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(SUBMITDELEVENT_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(EVENT_ID, pEventid);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "SubmitDelEvent");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(eventid, pEventid);

	return bFlag;
}

/*
 * 2.3.3.4.1 获取联系人列表接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetContactList
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetContactList
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jobject contactItemarray, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);

	string cid = "";
	string lastupdate = "0";

	// get contactItem
	bool bHasEvents =  false;
	jclass cls_arraylist = env->GetObjectClass(contactItemarray);
	jmethodID mth_arraylist_get  = env->GetMethodID(cls_arraylist, "get", "(I)Ljava/lang/Object;");
	jmethodID mth_arraylist_size = env-> GetMethodID(cls_arraylist, "size", "()I");
	jlong arraylen = env->CallIntMethod(contactItemarray, mth_arraylist_size);
	Json::Value root;
	root = Json::arrayValue;
	for(long i=0; i< arraylen; i++){
		bHasEvents = true;
		//item
		showLog("Jni.DrService.GetContactList", "arraylen:%ld", arraylen);
		jobject obj_item = env->CallObjectMethod(contactItemarray, mth_arraylist_get, i);
		jclass cls_item = env->GetObjectClass(obj_item);
		showLog("Jni.DrService.GetContactList", "get obj_attitem success");
		
		//cid
		jfieldID fid_item_cid = env->GetFieldID(cls_item, "cnid", "Ljava/lang/String;");
		showLog("Jni.DrService.GetContactList", "get fid_item_cid1 success:%ld",fid_item_cid);
		jstring item_cid = (jstring)env->GetObjectField(obj_item, fid_item_cid);
		showLog("Jni.DrService.GetContactList", "get fid_item_cid1 success");
		const char *pAttItemCid = env->GetStringUTFChars(item_cid, 0);
		if(pAttItemCid == NULL)
		{
			cid = "";
		}
		else
		{
			cid = pAttItemCid;
		}
		showLog("Jni.DrService.GetContactList", "cid:%s", cid.c_str());
		
		
		//lastupdate
		jfieldID fid_item_lastupdate = env->GetFieldID(cls_item, "lastawstimeseconds", "Ljava/lang/String;");
		showLog("Jni.DrService.GetContactList", "get fid_item_lastupdate success:%ld",fid_item_lastupdate);
		jstring item_lastupdate = (jstring)env->GetObjectField(obj_item, fid_item_lastupdate);
		showLog("Jni.DrService.GetContactList", "get item_lastupdate1 success");
		const char *pAttItemLastupdate = env->GetStringUTFChars(item_lastupdate, 0);
		
		//showLog("Jni.DrService.GetContactList", "%s", pAttItemLastupdate);
		if(pAttItemLastupdate == NULL)
		{
			lastupdate = "";
		}
		else
		{
			lastupdate = pAttItemLastupdate;
		}
		showLog("Jni.DrService.GetContactList", "lastupdate:%s", lastupdate.c_str());
		
		Json::Value body;
		body[CN_ID] = cid;
		body[LASTUPDATE] = lastupdate;
		
		root.append(body);
		
		env->ReleaseStringUTFChars(item_cid, pAttItemCid);
		env->ReleaseStringUTFChars(item_lastupdate, pAttItemLastupdate);
	}

	string param = "";
	if(bHasEvents) {
		Json::FastWriter writer;
		param = writer.write(root);
	}
	showLog("Jni.DrService.GetContactList", "param:%s", param.c_str());


	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETCONTACTLIST_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(GETCONTACTLIST_PARAM,param);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetContactList");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	return bFlag;
}

/*
 * 2.3.3.1.11 提交班级回评接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    SubmitClassreview
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SubmitClassreview
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey,jstring eventid, jobject objarray, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	const char *pEventid = env->GetStringUTFChars(eventid, 0);

	string id = "";
	string type = "";
	string value = "";

	// get obj_list
	bool bHasObjs =  false;
	jclass cls_arraylist = env->GetObjectClass(objarray);
	jmethodID mth_arraylist_get  = env->GetMethodID(cls_arraylist, "get", "(I)Ljava/lang/Object;");
	jmethodID mth_arraylist_size = env-> GetMethodID(cls_arraylist, "size", "()I");
	jlong arraylen = env->CallIntMethod(objarray, mth_arraylist_size);
	Json::Value root;
	root = Json::arrayValue;
	for(long i=0; i< arraylen; i++){
		bHasObjs = true;
		//item
		showLog("Jni.DrService.SubmitClassreview", "arraylen:%ld", arraylen);
		jobject obj_item = env->CallObjectMethod(objarray, mth_arraylist_get, i);
		jclass cls_item = env->GetObjectClass(obj_item);
		showLog("Jni.DrService.SubmitClassreview", "get obj_attitem success");
		
		//id
		jfieldID fid_item_id = env->GetFieldID(cls_item, "id", "Ljava/lang/String;");
		showLog("Jni.DrService.SubmitClassreview", "get fid_item_id success");
		jstring item_id = (jstring)env->GetObjectField(obj_item, fid_item_id);
		showLog("Jni.DrService.SubmitClassreview", "get item_id success");
		const char *pAttItemId = env->GetStringUTFChars(item_id, 0);
		showLog("Jni.DrService.SubmitClassreview", "%s", pAttItemId);
		if(pAttItemId == NULL)
		{
			id = "";
		}
		else
		{
			id = pAttItemId;
		}
		
		//type
		jfieldID fid_item_type = env->GetFieldID(cls_item, "type", "Ljava/lang/String;");
		showLog("Jni.DrService.SubmitClassreview", "get fid_item_type success");
		jstring item_type = (jstring)env->GetObjectField(obj_item, fid_item_type);
		showLog("Jni.DrService.SubmitClassreview", "get item_type success");
		const char *pAttItemType = env->GetStringUTFChars(item_type, 0);
		showLog("Jni.DrService.SubmitClassreview", "%s", pAttItemType);
		if(pAttItemType == NULL)
		{
			type = "";
		}
		else
		{
			type = pAttItemType;
		}
		
		//value
		jfieldID fid_item_value = env->GetFieldID(cls_item, "value", "Ljava/lang/String;");
		showLog("Jni.DrService.SubmitClassreview", "get fid_item_value success");
		jstring item_value = (jstring)env->GetObjectField(obj_item, fid_item_value);
		showLog("Jni.DrService.SubmitClassreview", "get item_contect success");
		const char *pAttItemValue = env->GetStringUTFChars(item_value, 0);
		showLog("Jni.DrService.SubmitClassreview", "%s", pAttItemValue);
		if(pAttItemValue == NULL)
		{
			value = "";
		}
		else
		{
			value = pAttItemValue;
		}
		
		Json::Value body;
		body[REVIEW_ID] = id;
		body[REVIEW_TYPE] = type;
		body[REVIEW_VALUE] = value;
		
		root.append(body);
		
		env->ReleaseStringUTFChars(item_id, pAttItemId);
		env->ReleaseStringUTFChars(item_type, pAttItemType);
		env->ReleaseStringUTFChars(item_value, pAttItemValue);
	}

	string param = "";
	
	if(bHasObjs) {
		Json::FastWriter writer;
		param = writer.write(root);
	}
	showLog("Jni.DrService.SubmitClassreview", "param:%s", param.c_str());


	gDrUrlConnection.setPost(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(SUBMITCLASSREVIEW_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(EVENT_ID, pEventid);
	gDrUrlConnection.addParam(FAV_EVENT_PARAM,param);
	
	long idrequest = gDrUrlConnection.startRequest();
	if(-1 != idrequest){
		insertCallbackObject(env, idrequest, callback, "SubmitClassreview");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(eventid, pEventid);
	return bFlag;
}

/*
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetAttendanceinfo
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetAttendanceinfo
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring userid, jstring lastupdate, jstring verificationkey,jstring challenge, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pUserid = env->GetStringUTFChars(userid, 0);
	const char *pLastUpdate = env->GetStringUTFChars(lastupdate, 0);
	const char *pVerificationkey = env->GetStringUTFChars(verificationkey, 0);
	const char *pChallenge = env->GetStringUTFChars(challenge, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI4ATTENDANCE_PATH);
	gDrUrlConnection.setPath(GETATTENDANCEINFO_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(USERID, pUserid);
	gDrUrlConnection.addParam(ATTENDANCETIME, pLastUpdate);
	gDrUrlConnection.addParam(VERIFICATIONKEY, pVerificationkey);
	gDrUrlConnection.addParam(CHALLENGE, pChallenge);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetAttendanceinfo");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(userid, pUserid);
	env->ReleaseStringUTFChars(lastupdate, pLastUpdate);
	env->ReleaseStringUTFChars(verificationkey, pVerificationkey);
	env->ReleaseStringUTFChars(challenge, pChallenge);
	return bFlag;
}

/*
 * 2.3.3.6.1 告假获取列表接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetLeavelist
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetLeavelist
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jstring lastupdate, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	const char *pLastUpdate = env->GetStringUTFChars(lastupdate, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETLEAVELIST_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(LASTUPDATE, pLastUpdate);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetLeavelist");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(lastupdate, pLastUpdate);
	return bFlag;
}

/*
 * 2.3.3.6.2 告假获取详细接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetLeaveinfo
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetLeaveinfo
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jstring leaveid,jstring allfield, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);
	const char *pLeaveid = env->GetStringUTFChars(leaveid, 0);
	const char *pAllfield = env->GetStringUTFChars(allfield, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETLEAVEINFO_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);
	gDrUrlConnection.addParam(GETLEAVE_LEAVEID, pLeaveid);
	gDrUrlConnection.addParam(GETLEAVE_ALLFIELD, pAllfield);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetLeaveinfo");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	env->ReleaseStringUTFChars(leaveid, pLeaveid);
	env->ReleaseStringUTFChars(allfield, pAllfield);
	return bFlag;
}

/*
 * 2.3.3.6.3 告假发送接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    SubmitLeave
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/ArrayList;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_SubmitLeave
  (JNIEnv *env, jobject, jstring domain, jstring schoolid, jstring sessionkey, \
		  jstring type, jstring ownerid, jstring owner, \
		  jstring start, jstring end, jstring title, jstring body, jobject attarray, jobject callback){
	showLog("Jni.DrService.SubmitEvent4Kids", "SubmitLeave");
	// get string
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionKey = env->GetStringUTFChars(sessionkey, 0);
	const char *pType = env->GetStringUTFChars(type, 0);
	const char *pOwnerid = env->GetStringUTFChars(ownerid, 0);
	const char *pOwner = env->GetStringUTFChars(owner, 0);
	const char *pStart = env->GetStringUTFChars(start, 0);
	const char *pEnd = env->GetStringUTFChars(end, 0);
	const char *pTitle = env->GetStringUTFChars(title, 0);
	const char *pBody = env->GetStringUTFChars(body, 0);

	string strDescription = "description";
	string strJsonFiledes = "";
	//jstring FileTitle = "";
	//jstring Filefrom = "";
	//const char *pComment = env->GetStringUTFChars(comment, 0);
	showLog("Jni.DrService.SubmitLeave", "get jstring ok");


	// get array_list
	bool bHasAtt =  false;
	jclass cls_arraylist = env->GetObjectClass(attarray);
	jmethodID mth_arraylist_get  = env->GetMethodID(cls_arraylist, "get", "(I)Ljava/lang/Object;");
	jmethodID mth_arraylist_size = env-> GetMethodID(cls_arraylist, "size", "()I");
	jlong arraylen = env->CallIntMethod(attarray, mth_arraylist_size);
	Json::Value root;
	root = Json::arrayValue;
	for(long i=0; i< arraylen; i++){
		bHasAtt = true;
		showLog("Jni.DrService.SubmitEvent4Kids", "arraylen:%ld", arraylen);
		jobject obj_attitem = env->CallObjectMethod(attarray, mth_arraylist_get, i);
		jclass cls_attitem = env->GetObjectClass(obj_attitem);
		showLog("Jni.DrService.SubmitLeave", "get obj_attitem success");

		//pic description
		jfieldID fid_attitem_description = env->GetFieldID(cls_attitem, "description", "Ljava/lang/String;");
		showLog("Jni.DrService.SubmitLeave", "get fid_attitem_description success");
		jstring attitem_description = (jstring)env->GetObjectField(obj_attitem, fid_attitem_description);
		showLog("Jni.DrService.SubmitLeave", "get fid_attitem_description success");
		const char *pAttItemDesc = env->GetStringUTFChars(attitem_description, 0);
		showLog("Jni.DrService.SubmitLeave", "%s", pAttItemDesc);
		if(pAttItemDesc == NULL)
		{
			strDescription = "";
		}
		else
		{
			strDescription = pAttItemDesc;
		}
		showLog("Jni.DrService.SubmitEvent", "strFileDescription:%s", strDescription.c_str());

		//type
		jfieldID fid_attitem_type = env->GetFieldID(cls_attitem, "type", "Ljava/lang/String;");
		jstring attitem_type = (jstring)env->GetObjectField(obj_attitem, fid_attitem_type);
		const char *pAttItemtype = env->GetStringUTFChars(attitem_type, 0);
		string strtype = "";
		if(pAttItemtype!=NULL)
			strtype = pAttItemtype;
		showLog("Jni.DrService.SubmitLeave", "strType:%s", strtype.c_str());
		//item
		jfieldID fid_attitem_item = env->GetFieldID(cls_attitem, "item", "Ljava/lang/String;");
		jstring attitem_item = (jstring)env->GetObjectField(obj_attitem, fid_attitem_item);
		const char *pAttItem_Item = env->GetStringUTFChars(attitem_item, 0);
		string strItem = "";
		if(pAttItem_Item!=NULL)
			strItem = pAttItem_Item;
		showLog("Jni.DrService.SubmitLeave", "strItem:%s", strItem.c_str());

		jfieldID fid_attitem_id = env->GetFieldID(cls_attitem, "fileId", "Ljava/lang/String;");
		jstring attitem_id = (jstring)env->GetObjectField(obj_attitem, fid_attitem_id);
		const char *pAttItemId = env->GetStringUTFChars(attitem_id, 0);
		string strId = pAttItemId;
		showLog("Jni.DrService.SubmitLeave", "strId:%s", strId.c_str());

		jfieldID fid_attitem_ext = env->GetFieldID(cls_attitem, "fileType", "Ljava/lang/String;");
		jstring attitem_ext = (jstring)env->GetObjectField(obj_attitem, fid_attitem_ext);
		const char *pAttItemext = env->GetStringUTFChars(attitem_ext, 0);
		string strExt = pAttItemext;
		showLog("Jni.DrService.SubmitLeave", "strExt:%s", strExt.c_str());

		string strfile = pAttItemId;
		string strfileext = pAttItemext;
		//strfile += ".";
		//strfile += pAttItemType;
		showLog("Jni.DrService.SubmitLeave", "strfile:%s", strfile.c_str());

		jfieldID fid_attitem_data = env->GetFieldID(cls_attitem, "data", "[B");
		jbyteArray dataarray = (jbyteArray)env->GetObjectField(obj_attitem, fid_attitem_data);

		if(0 != dataarray) {
			showLog("Jni.DrService.SubmitEvent4Kids", "dataarray:%ld", dataarray);
			jbyte *data = env->GetByteArrayElements(dataarray, 0);
			showLog("Jni.DrService.SubmitEvent4Kids", "data:%ld", data);
			jsize datasize = env->GetArrayLength(dataarray);
			showLog("Jni.DrService.SubmitEvent4Kids", "datasize:%ld", datasize);
			gDrUrlConnection.addFile(strfile, strfileext,(char*)data, datasize);
			env->ReleaseByteArrayElements(dataarray, data, JNI_COMMIT);
		}



			Json::Value body;
			body[EVENTS_FILETITLE] = strDescription;
			body[EVENTS_FILEFROM] = "";
			body[EVENTS_TYPE] = strtype;
			body[EVENTS_ITEM] = strItem;

//			Json::FastWriter writer;
//			string filebody = writer.write(body);
//			showLog("Jni.DrService.SubmitEvent4Kids", "filebody:%s", filebody.c_str());
//			string fileKey = DrHttpClient_POST_FILE_PRE;
//			char cFileCount[10];
//			sprintf(cFileCount, "%ld", i);
//			fileKey += cFileCount;
			root.append(body);
//			root[strfile] = body;


		env->ReleaseStringUTFChars(attitem_id, pAttItemId);
		env->ReleaseStringUTFChars(attitem_ext, pAttItemext);
		env->ReleaseStringUTFChars(attitem_type, pAttItemtype);
		env->ReleaseStringUTFChars(attitem_item, pAttItem_Item);
	}
	string fileDesc = "";
	if(bHasAtt) {
		Json::FastWriter writer;
		fileDesc = writer.write(root);
	}
	showLog("Jni.DrService.SubmitLeave", "fileDesc:%s", fileDesc.c_str());

	gDrUrlConnection.setPost(true);
	//gDrUrlConnection.setKeepAlive(true);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETSUBMITLEAVE_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionKey);
	if(0 != strcmp(pType,""))
		gDrUrlConnection.addParam(EVENTS_TYPE, pType);
	if(0 != strcmp(pOwnerid,""))
		gDrUrlConnection.addParam(EVENTS_OWNERID, pOwnerid);
	if(0 != strcmp(pOwner,""))
		gDrUrlConnection.addParam(EVENTS_OWNER, pOwner);
	if(0 != strcmp(pStart,""))
		gDrUrlConnection.addParam(EVENTS_START, pStart);
	if(0 != strcmp(pEnd,""))
		gDrUrlConnection.addParam(EVENTS_END, pEnd);
	if(0 != strcmp(pTitle,""))
		gDrUrlConnection.addParam(EVENTS_TITLE, pTitle);
	if(0 != strcmp(pBody,""))
		gDrUrlConnection.addParam(GETSUBMITLEAVE_CONTENT, pBody);
	//if(0 != strcmp(,"")
	gDrUrlConnection.addParam(EVENTS_FILEDES,fileDesc);



//	if(0 != strcmp(pComment,""))
//	{
//		gDrUrlConnection.addParam(EVENTS_COMMENTS,pComment);
//	}

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "SubmitEvent4Kids");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionKey);
	env->ReleaseStringUTFChars(type, pType);
	env->ReleaseStringUTFChars(ownerid, pOwnerid);
	env->ReleaseStringUTFChars(owner, pOwner);
	env->ReleaseStringUTFChars(start, pStart);
	env->ReleaseStringUTFChars(end, pEnd);
	env->ReleaseStringUTFChars(title, pTitle);
	env->ReleaseStringUTFChars(body, pBody);
	return bFlag;
}

/*
 * 2.3.3.6.4 告假获取接收人列表接口
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetLeaveownerlist
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetLeaveownerlist
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETLEAVEOWNERLIST_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetLeaveownerlist");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	return bFlag;
}

/*
 * 2.3.3.1.12 获取班级标题类型
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetEventtitlelist
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetEventtitlelist
  (JNIEnv *env, jobject, jstring domain, jstring schoolid , jstring sessionkey, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pSchoolId = env->GetStringUTFChars(schoolid, 0);
	const char *pSessionkey = env->GetStringUTFChars(sessionkey, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(WEBAPI_PATH);
	gDrUrlConnection.setPath(GETEVENTTITLELIST_PATH);
	gDrUrlConnection.addParam(SCHOOLID, pSchoolId);
	gDrUrlConnection.addParam(SESSIONKEY, pSessionkey);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetEventtitlelist");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(schoolid, pSchoolId);
	env->ReleaseStringUTFChars(sessionkey, pSessionkey);
	return bFlag;
}

/*
 * 2.3.6.1 获取关联帐号列表
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni
 * Method:    GetRelationaccount
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_GetRelationaccount
  (JNIEnv *env, jobject, jstring domain, jstring appid , jstring tokenid, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pAppid = env->GetStringUTFChars(appid, 0);
	const char *pTokenid = env->GetStringUTFChars(tokenid, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(NETAPP_PATH);
	gDrUrlConnection.setPath(GETRELATIONACCOUNT_PATH);
	gDrUrlConnection.addParam(APPID, pAppid);
	gDrUrlConnection.addParam(REQUEST_TOKENID, pTokenid);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "GetRelationaccount");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(appid, pAppid);
	env->ReleaseStringUTFChars(tokenid, pTokenid);
	return bFlag;
}

/*
 * 2.3.6.2 添加关联帐号（http get方式）
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni_Addrelationaccount
 * Method:    Addrelationaccount
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_Addrelationaccount
  (JNIEnv *env, jobject, jstring domain,jstring account, jstring passwordMd5, jstring schoolkey, jstring tokenid, jobject callback)
{
	bool bFlag = false;
	const char *pDomain = env->GetStringUTFChars(domain, 0);
	const char *pAccount = env->GetStringUTFChars(account,0);
	const char *pPasswordMd5 = env->GetStringUTFChars(passwordMd5, 0);
	const char *pSchoolkey = env->GetStringUTFChars(schoolkey, 0);
	const char *pTokenid = env->GetStringUTFChars(tokenid, 0);

	gDrUrlConnection.setPost(false);
	gDrUrlConnection.setDomain(pDomain);
	gDrUrlConnection.setBasePath(NETAPP_PATH);
	gDrUrlConnection.setPath(ADDRELATIONACCOUNT_PATH);
	gDrUrlConnection.addParam(ADDRELATIONACCOUNT_ACCOUNT, pAccount);
	gDrUrlConnection.addParam(ADDRELATIONACCOUNT_PASSWORD, pPasswordMd5);
	gDrUrlConnection.addParam(SCHOOLKEY, pSchoolkey);
	gDrUrlConnection.addParam(REQUEST_TOKENID,pTokenid);

	long id = gDrUrlConnection.startRequest();
	if(-1 != id){
		insertCallbackObject(env, id, callback, "Addrelationaccount");
		bFlag = true;
	}

	env->ReleaseStringUTFChars(domain, pDomain);
	env->ReleaseStringUTFChars(account,pAccount);
	env->ReleaseStringUTFChars(passwordMd5,pPasswordMd5);
	env->ReleaseStringUTFChars(schoolkey,pSchoolkey);
	env->ReleaseStringUTFChars(tokenid,pTokenid);

	return bFlag;
}

/*
 * 2.3.6.3 删除关联帐号（http post方式）
 * Class:     com_drcom_drpalm_Tool_service_DrServiceJni_Delrelationaccount
 * Method:    Delrelationaccount
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
 */
 JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_Delrelationaccount
  (JNIEnv * env, jobject, jstring domain, jobject accountlist, jobject callback)
  {
    bool bFlag = false;

    const char *pDomain = env->GetStringUTFChars(domain,0);

    string account = "";
    string schoolkey = "";
    string tokenid = "";

    bool bHasObjs = false;
    jclass cls_arraylist = env->GetObjectClass(accountlist);
    jmethodID mth_arraylist_get  = env->GetMethodID(cls_arraylist, "get", "(I)Ljava/lang/Object;");
	jmethodID mth_arraylist_size = env-> GetMethodID(cls_arraylist, "size", "()I");
	jlong arraylen = env->CallIntMethod(accountlist, mth_arraylist_size);
	Json::Value root;
	root = Json::arrayValue;

	for(long i=0; i< arraylen; i++){
    	bHasObjs = true;
    	//item
        showLog("Jni.DrService.Delrelationaccount", "arraylen:%ld", arraylen);
        jobject obj_item = env->CallObjectMethod(accountlist, mth_arraylist_get, i);
        jclass cls_item = env->GetObjectClass(obj_item);
        showLog("Jni.DrService.Delrelationaccount", "get obj_attitem success");

        //account
		jfieldID fid_item_account = env->GetFieldID(cls_item, "pub_account", "Ljava/lang/String;");
		showLog("Jni.DrService.Delrelationaccount", "get fid_item_pub_account success");
		jstring item_account = (jstring)env->GetObjectField(obj_item, fid_item_account);
		showLog("Jni.DrService.Delrelationaccount", "get item_pub_account success");
		const char *pAttItemAccount = env->GetStringUTFChars(item_account, 0);
		showLog("Jni.DrService.Delrelationaccount", "%s", pAttItemAccount);
		if(pAttItemAccount == NULL)
		{
			account = "";
		}
		else
		{
			account = pAttItemAccount;
		}

        //schoolKey
        jfieldID fid_item_schoolkey = env->GetFieldID(cls_item,"schoolkey","Ljava/lang/String;");
        showLog("Jni.DrService.Delrelationaccount", "get fid_item_schoolKey success");
        jstring item_schoolkey = (jstring)env->GetObjectField(obj_item,fid_item_schoolkey);
        showLog("Jni.DrService.Delrelationaccount", "get item_schoolKey success");
        const char *pAttItemSchoolkey = env->GetStringUTFChars(item_schoolkey,0);
        showLog("Jni.DrService.Delrelationaccount", "%s", pAttItemSchoolkey);
        if(pAttItemSchoolkey == NULL)
        {
            schoolkey = "";
        }
        else
        {
            schoolkey = pAttItemSchoolkey;
        }

        //tokenid
        jfieldID fid_item_tokenid = env->GetFieldID(cls_item,"tokenid","Ljava/lang/String;");
        showLog("Jni.DrService.Delrelationaccount", "get fid_item_tokenid success");
        jstring item_tokenid = (jstring)env->GetObjectField(obj_item,fid_item_tokenid);
        showLog("Jni.DrService.Delrelationaccount", "get item_tokenid success");
        const char *pAttItemTokenid = env->GetStringUTFChars(item_tokenid,0);
        showLog("Jni.DrService.Delrelationaccount", "%s", pAttItemTokenid);
        if(pAttItemTokenid == NULL)
        {
            tokenid = "";
        }
        else
        {
            tokenid = pAttItemTokenid;
        }

        Json::Value body;

        body[ADDRELATIONACCOUNT_ACCOUNT] = account;
        body[SCHOOLKEY] = schoolkey;
        body[REQUEST_TOKENID] = tokenid;

        root.append(body);

        env->ReleaseStringUTFChars(item_account,pAttItemAccount);
        env->ReleaseStringUTFChars(item_schoolkey,pAttItemSchoolkey);
        env->ReleaseStringUTFChars(item_tokenid,pAttItemTokenid);

    }

    string param = "";

    if(bHasObjs) {
		Json::FastWriter writer;
		param = writer.write(root);
	}

    gDrUrlConnection.setPost(true);
    gDrUrlConnection.setDomain(pDomain);
    gDrUrlConnection.setBasePath(NETAPP_PATH);
    gDrUrlConnection.setPath(DELRELATIONACCOUNT_PATH);
    gDrUrlConnection.addParam(DELRELATIONACCOUNT_PARAM,param);

    long id = gDrUrlConnection.startRequest();
    if(-1 != id){
    	insertCallbackObject(env, id, callback, "Delrelationaccount");
    	bFlag = true;
    }

    env->ReleaseStringUTFChars(domain, pDomain);

    return bFlag;
  }

  /*
   * 2.3.1.12 获取帐号信息（http post方式）
   * Class:     com_drcom_drpalm_Tool_service_DrServiceJni_Getaccountsinfo
   * Method:    Getaccountsinfo
   * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/drcom/drpalm/Tool/service/DrServiceJniCallback;)Z
   */
   JNIEXPORT jboolean JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_Getaccountsinfo
    (JNIEnv * env, jobject, jstring domain, jstring tokenid, jobject accounts, jobject callback)
    {
        bool bFlag = false;

        const char *pDomain = env->GetStringUTFChars(domain,0);
        const char *pTokenid = env->GetStringUTFChars(tokenid,0);

        string account = "";
        string schoolId = "";

        bool bHasObjs = false;
        jclass cls_arraylist = env->GetObjectClass(accounts);
        jmethodID mth_arraylist_get  = env->GetMethodID(cls_arraylist, "get", "(I)Ljava/lang/Object;");
        jmethodID mth_arraylist_size = env-> GetMethodID(cls_arraylist, "size", "()I");
        jlong arraylen = env->CallIntMethod(accounts, mth_arraylist_size);
        Json::Value root;

        root = Json::arrayValue;

	    for(long i=0; i< arraylen; i++){
    	    bHasObjs = true;

    	    //item
            showLog("Jni.DrService.Getaccountsinfo", "arraylen:%ld", arraylen);
            jobject obj_item = env->CallObjectMethod(accounts, mth_arraylist_get, i);
            jclass cls_item = env->GetObjectClass(obj_item);
            showLog("Jni.DrService.Getaccountsinfo", "get obj_attitem success");

    	    //account
            jfieldID fid_item_account = env->GetFieldID(cls_item, "pub_account", "Ljava/lang/String;");
            showLog("Jni.DrService.Getaccountsinfo", "get fid_item_pub_account success");
            jstring item_account = (jstring)env->GetObjectField(obj_item, fid_item_account);
            showLog("Jni.DrService.Getaccountsinfo", "get item_pub_account success");
            const char *pAttItemAccount = env->GetStringUTFChars(item_account, 0);
            showLog("Jni.DrService.Getaccountsinfo", "%s", pAttItemAccount);
            if(pAttItemAccount == NULL)
            {
        	    account = "";
        	}
        	else
            {
        		account = pAttItemAccount;
            }

            //schoolid
            jfieldID fid_item_pub_schoolId = env->GetFieldID(cls_item,"pub_schoolId","Ljava/lang/String;");
            showLog("Jni.DrService.Getaccountsinfo", "get fid_item_pub_schoolId success");
            jstring item_pub_schoolId = (jstring)env->GetObjectField(obj_item,fid_item_pub_schoolId);
            showLog("Jni.DrService.Getaccountsinfo", "get item_pub_schoolId success");
            const char *pAttItemPub_schoolId = env->GetStringUTFChars(item_pub_schoolId,0);
            showLog("Jni.DrService.Getaccountsinfo", "%s", pAttItemPub_schoolId);
            if(pAttItemPub_schoolId == NULL)
            {
                schoolId = "";
            }
            else
            {
                schoolId = pAttItemPub_schoolId;
            }

            Json::Value body;

            body[ADDRELATIONACCOUNT_ACCOUNT] = account;
            body[SCHOOLID] = schoolId;

            root.append(body);

            env->ReleaseStringUTFChars(item_account,pAttItemAccount);
            env->ReleaseStringUTFChars(item_pub_schoolId,pAttItemPub_schoolId);
    	}

        string param = "";

        if(bHasObjs) {
		    Json::FastWriter writer;
		    param = writer.write(root);
	    }

	    gDrUrlConnection.setPost(true);
        gDrUrlConnection.setDomain(pDomain);
        gDrUrlConnection.setBasePath(WEBAPI_PATH);
        gDrUrlConnection.setPath(GETACCOUNTSINFO_PATH);
        gDrUrlConnection.addParam(REQUEST_TOKENID,pTokenid);
        gDrUrlConnection.addParam(GETACCOUNTSINFO_PARAM,param);

        long id = gDrUrlConnection.startRequest();
        if(-1 != id){
           insertCallbackObject(env, id, callback, "Getaccountsinfo");
           bFlag = true;
        }

        env->ReleaseStringUTFChars(domain, pDomain);
        env->ReleaseStringUTFChars(tokenid,pTokenid);

        return bFlag;
    }
/*
* Class:     com_drcom_drpalm_Tool_service_DrServiceJni
* Method:    AesEncrypt
* Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
*/
JNIEXPORT jstring JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_AesEncrypt
	(JNIEnv *env, jobject obj, jstring src, jstring key) {
	const char *pSrc = env->GetStringUTFChars(src, 0);
	const char *pKey = env->GetStringUTFChars(key, 0);

	string result = DrUrlConnection::AesEncrypt(pKey, pSrc);

	env->ReleaseStringUTFChars(src, pSrc);
	env->ReleaseStringUTFChars(key, pKey);

	return env->NewStringUTF(result.c_str());
}

/*
* Class:     com_drcom_drpalm_Tool_service_DrServiceJni
* Method:    AesDecrypt
* Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
*/
JNIEXPORT jstring JNICALL Java_com_drcom_drpalm_Tool_service_DrServiceJni_AesDecrypt
	(JNIEnv *env, jobject obj, jstring src, jstring key) {
	const char *pSrc = env->GetStringUTFChars(src, 0);
	const char *pKey = env->GetStringUTFChars(key, 0);

	string result = DrUrlConnection::AesDecrypt(pKey, pSrc);

	env->ReleaseStringUTFChars(src, pSrc);
	env->ReleaseStringUTFChars(key, pKey);

	return env->NewStringUTF(result.c_str());
}
