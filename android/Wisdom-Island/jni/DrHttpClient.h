/*
 * File         : DrHttpClient.h
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrPalm DrHttpClient include
 */

#ifndef _INC_DRHTTPCLIENT_
#define _INC_DRHTTPCLIENT_

#include <map>
#include "DrCOMSocket.h"
#include "DrHttpClientDefine.h"
#include "DrHttpClient.h"
#include "DrThread.h"
#include "DrLog.h"
#include "DrMutex.h"

using namespace std;
#define MAXTHREADCOUNT 5
#define MAXSOCKETCOUNT 5
typedef struct _HttpContectionStatus{
	tcpSocket *_pSocket;
	bool _isReceiving;
	_HttpContectionStatus(){
		_isReceiving = false;
	}
	_HttpContectionStatus & operator=(const _HttpContectionStatus & obj){
		_isReceiving = obj._isReceiving;
		_pSocket = obj._pSocket;
		return *this;
	}
	~_HttpContectionStatus(){

	}
}HTTPCONTECTIONSTATUS;
typedef enum _HttpClientThreadType{
	HTTPGET = 0,
	HTTPPOST = 1,
	HTTPPOST_KEEP_ALIVE = 2
}HTTPCLIENTTHREADTYPE;
typedef struct _HttpClientThreadData{
	string _url;
	char* _data;
	long _size;
	_HttpClientThreadData(){
		_url = "";
		_data = NULL;
		_size = 0;
		showLog("Jni.HttpClientThreadData","A HttpClientThreadData create!");
	}
	_HttpClientThreadData &operator=(const _HttpClientThreadData & obj){
		showLog("Jni.HttpClientThreadData","A HttpClientThreadData operator=");
		copy(obj);
		return *this;
	}
	_HttpClientThreadData(const _HttpClientThreadData & obj){
		copy(obj);
	}
	~_HttpClientThreadData(){
		free();
		showLog("Jni.HttpClientThreadData","A HttpClientThreadData delete!");
	}
private:
	void free(){
		if(_data){
			//showLog("Jni.HttpClientThreadData","delete _data:%s",_data);
			_data = NULL;
		}
		_size = 0;
	}
	void copy(const _HttpClientThreadData &obj){
		_url = obj._url;
		_data = obj._data;
		_size = obj._size;
		showLog("Jni.HttpClientThreadData","A HttpClientThreadData copied!");
	}
}HTTPCLIENTTHREADDATA;
typedef std::map<string, HTTPCONTECTIONSTATUS> HTTPSOCKETMAP;
class IDrHttpClientCallback{
public:
	virtual void onReceiveData(unsigned char* buf, long len, long iThreadId, bool bBackToView) = 0;
	virtual void onSuccess(unsigned char* buf, long len, long iThreadId, bool bBackToView) = 0;
	virtual void onError(unsigned char* buf, long len, long iThreadId, bool bBackToView) = 0;
	virtual void onWriteLog(unsigned char* buf, long len, long iThreadId) = 0;
};
class DrHttpClientThread;
class DrHttpClient
{
	public:
		DrHttpClient();
		~DrHttpClient();

		void setCallback(IDrHttpClientCallback *callback);
		long httpPost(string strUrl,const char* data, long len, bool isKeepAlive = false);
		long httpGet(string strUrl);
		long singleHttpPost(string strUrl,const char* data, long len, long iThreadId, bool isKeepAlive = false);
		long singleHttpGet(string strUrl, long iThreadId);
		bool stopConnection(string strUrl);
	protected:
		long httpHandle(tcpSocket* ptSocket, long& iHttpCode, long& iContentLen, long iThreadId, bool isKeepAlive = false, string strUrl = "");
	private:
		string m_strUrl;
		string m_strBody;
		char m_cUrlBuffer[DrHttpClient_BUFFER_64K];
		DrMutex m_DrMutex;
		HTTPSOCKETMAP m_SocketMap;
		IDrHttpClientCallback *mIDrHttpClientCallback;
		DrHttpClientThread *m_ThreadArray[MAXTHREADCOUNT];

		long getUrlPort(string url, string &newUrl, string &port, string &param);
};

class DrHttpClientThread : public DrThread{
public:
	DrHttpClientThread(){
		m_pDrHttpClient = NULL;
		m_type = HTTPGET;
		m_Param._data = NULL;
		m_Param._size = 0;
	};
	virtual ~DrHttpClientThread(){
		m_pDrHttpClient = NULL;
		clearParams();
	};
	void setParams(DrHttpClient *pDrHttpClient, HTTPCLIENTTHREADTYPE type,const HTTPCLIENTTHREADDATA &param){
		m_DrMutex.lock();
		m_pDrHttpClient = pDrHttpClient;
		m_type = type;
		m_Param._url = param._url;
		m_Param._size = param._size;
		if(m_Param._data){
			delete[] m_Param._data;
			m_Param._data = NULL;
		}
		if(m_Param._size > 0){
			m_Param._data = new char[m_Param._size];
			memcpy(m_Param._data, param._data, param._size);
		}
		m_DrMutex.unlock();

	}
	void clearParams(){
		m_DrMutex.lock();
		if(m_Param._data){
			delete[] m_Param._data;
			m_Param._data = NULL;
		}
		m_Param._size = 0;
		m_DrMutex.unlock();
	}
protected:
	void onRun(){
		showLog("Jni.DrHttpClientThread.onRun","");

		if(m_pDrHttpClient){
			//HTTPCLIENTTHREADDATA *data = (HTTPCLIENTTHREADDATA*)m_Param;
			if(m_Param._url == ""){
				showLog("Jni.DrHttpClientThread", "no url found");
				return;
			}
			switch(m_type){
			case HTTPGET:{
				showLog("Jni.DrHttpClientThread.HTTPGET", "getThreadId:%ld", getSelfThreadId());
				m_pDrHttpClient->singleHttpGet(m_Param._url, getSelfThreadId());
			}break;
			case HTTPPOST:{
				showLog("Jni.DrHttpClientThread.HTTPPOST", "getThreadId:%ld", getSelfThreadId());
				m_pDrHttpClient->singleHttpPost(m_Param._url, m_Param._data, m_Param._size, getSelfThreadId());
			}break;
			case HTTPPOST_KEEP_ALIVE:{
				showLog("Jni.DrHttpClientThread.HTTPPOSTKEEPALIVE", "getThreadId:%ld", getSelfThreadId());
				m_pDrHttpClient->singleHttpPost(m_Param._url, m_Param._data, m_Param._size, getSelfThreadId(), true);
			}break;
			}
		}
		clearParams();

	}
private:
	DrHttpClient *m_pDrHttpClient;
	HTTPCLIENTTHREADTYPE m_type;
	HTTPCLIENTTHREADDATA m_Param;

};
#endif
