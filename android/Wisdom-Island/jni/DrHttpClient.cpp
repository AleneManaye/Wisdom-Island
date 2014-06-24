/*
 * File         : DrHttpClient.cpp
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrPalm DrHttpClient source
 */

#include "DrHttpClient.h"
#include "StringHandle.h"


DrHttpClient::DrHttpClient() {
	mIDrHttpClientCallback = NULL;
	for(long i=0; i<MAXTHREADCOUNT; i++){
		m_ThreadArray[i] = new DrHttpClientThread();
//		showLog("Jni.DrHttpClient.DrHttpClient", "new m_ThreadArray[%ld]", i);
	}
}

DrHttpClient::~DrHttpClient() {
	showLog("Jni.DrHttpClient.~DrHttpClient", "");
	for(long i=0; i<MAXTHREADCOUNT; i++){
		if(m_ThreadArray[i]){
			if(m_ThreadArray[i]->isRunning()){
				m_ThreadArray[i]->stop();
				showLog("Jni.DrHttpClient.~DrHttpClient", "stop m_ThreadArray[%ld]", i);
			}
			delete m_ThreadArray[i];
		}
	}
}
void DrHttpClient::setCallback(IDrHttpClientCallback *callback){
	mIDrHttpClientCallback = callback;
}
long DrHttpClient::httpPost(string strUrl, const char* data, long len, bool isKeepAlive){
	for(long i=0; i<MAXTHREADCOUNT; i++){
		if(m_ThreadArray[i]){
			if(!m_ThreadArray[i]->isRunning()){
				showLog("Jni.DrHttpClient.httpPost", "m_ThreadArray[%ld] use", i);
				HTTPCLIENTTHREADDATA thdata;
				thdata._url = strUrl;
				thdata._size = len;
				thdata._data = (char*)data;

				if(!isKeepAlive){
					m_ThreadArray[i]->setParams(this, HTTPPOST, thdata);

				}
				else{
					m_ThreadArray[i]->setParams(this, HTTPPOST_KEEP_ALIVE, thdata);
				}
				m_ThreadArray[i]->start();
				//delete thdata;
				return m_ThreadArray[i]->getThreadId();
			}
		}
	}
	return -1;
}

long DrHttpClient::singleHttpPost(string strUrl, const char* data, long len, long iThreadId, bool isKeepAlive){
	showLog("Jni.DrHttpClient.singleHttpPost","strUrl:%s", strUrl.c_str());
	//showLog("Jni.DrHttpClient.singleHttpPost","data:%s", data);
	//tcpSocket tSocket;
	tcpSocket *pTcpSocket;// = &tSocket;
	long iRet = -1;
	long iHttpCode = 0, iContentLen = 0, iPort = 80;
	string strNewUrl= "", strPort= "", strParam= "", strUrlWithPort = "";
	string retContent = "";
	char cHttpBodyBuffer[DrHttpClient_BUFFER_64K];
	//showLog("Jni.DrHttpClient.singleHttpPost","cHttpBodyBuffer");
	// get url
    if(-1 == getUrlPort(strUrl, strNewUrl, strPort, strParam)){
    	showLog("Jni.DrHttpClient.singleHttpPost", "getUrlPort:error");
    	return iRet;
    }
    // get tcpsocket
    if(isKeepAlive){
    	bool bRecving = false;
    	m_DrMutex.lock();
        HTTPSOCKETMAP::iterator mit = m_SocketMap.find(strUrl);
        showLog("Jni.DrHttpClient.singleHttpPost", "strUrl:%s", strUrl.c_str());
        if(m_SocketMap.end() != mit){
        	/* find socket has been connected*/
        	showLog("Jni.DrHttpClient.singleHttpPost", "m_SocketMap find:%s", strUrl.c_str());
        	pTcpSocket = (*mit).second._pSocket;
        	if(pTcpSocket) {
        		// add for log
#ifdef PRINT_JNI_LOG
        		if(mIDrHttpClientCallback) {
        			char logBuffer[DrHttpClient_BUFFER_2K];
        			memset(logBuffer, '\0', DrHttpClient_BUFFER_2K);
        			sprintf(logBuffer, "%s\r\nuse a keepalive socket:%ld iThreadId:%ld\r\n%s", "Jni.DrHttpClient.singleHttpPost", pTcpSocket->getSocketId(), iThreadId, LOG_SEP);
        			mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
        		}
#endif
            	showLog("Jni.DrHttpClient.singleHttpPost", "url:%s,isRecv:%ld", (mit->first).c_str(), (mit->second)._isReceiving);
                if((mit->second)._isReceiving){
                	showLog("Jni.DrHttpClient.singleHttpPost", "this socket:%ld is receiving!", pTcpSocket->getSocketId());
                	bRecving = true;
                }
                else{
                	(mit->second)._isReceiving = true;
                	showLog("Jni.DrHttpClient.singleHttpPost", "use a keepalive socket:%ld", pTcpSocket->getSocketId());
                }
        	}
        }
        else{
        	/* Connect and save socket*/
        	pTcpSocket = new tcpSocket();
        	HTTPCONTECTIONSTATUS status;
        	status._pSocket = pTcpSocket;
        	status._isReceiving = true;
        	m_SocketMap.insert(HTTPSOCKETMAP::value_type(strUrl,status));
        	showLog("Jni.DrHttpClient.singleHttpPost", "insert a new socket:%ld", pTcpSocket->getSocketId());

        }
        m_DrMutex.unlock();
        if(bRecving)
        	return iRet;
    }
    else{
    	pTcpSocket = new tcpSocket();
    }

    // deal with new url
    strUrlWithPort = strNewUrl;
    if("" != strPort){
    	strUrlWithPort += ":" + strPort;
    	iPort = atoi(strPort.c_str());
    }
    if("" == strParam){
    	strParam = "/";
    }
    // connect to server
    if(-1 == pTcpSocket->getSocketId()){
        if(pTcpSocket->Connect(strNewUrl, iPort) != 1) {
        	showLog("Jni.DrHttpClient.singleHttpPost", "Connect:error strNewUrl:%s:%ld", strNewUrl.c_str(), iPort);
        	if(isKeepAlive){
        		/* connect fail remove socket from map*/
            	m_DrMutex.lock();
            	HTTPSOCKETMAP::iterator it = m_SocketMap.find(strUrl);
            	if(it != m_SocketMap.end()){
            		//HTTPCONTECTIONSTATUS pIt = (HTTPCONTECTIONSTATUS)it->second;
            		showLog("Jni.DrHttpClient.httpHandle", "connect fail m_SocketMap.erase:%s", (it->first).c_str());
            		m_SocketMap.erase(it);
            	}
            	m_DrMutex.unlock();
        	}

        	if(mIDrHttpClientCallback)
        		mIDrHttpClientCallback->onError((unsigned char*)HTTP_CONNECTION_ERROR, strlen(HTTP_CONNECTION_ERROR), iThreadId, true);

        }
    }
    if(-1 != pTcpSocket->getSocketId()){
    	showLog("Jni.DrHttpClient.singleHttpPost", "Tcp Connected:succeed");
    	memset(cHttpBodyBuffer, '\0', DrHttpClient_BUFFER_64K );
        if(isKeepAlive){
        	showLog("Jni.DrHttpClient.httpPost", "Http Connect:Keep-alive");
        	sprintf(cHttpBodyBuffer, DrHttpClient_POST, strParam.c_str(), \
        			DrHttpClient_HTTP_LANGUALE_CN, \
        			DrHttpClient_POST_BOUNDARY_DEF, \
        			len, strUrlWithPort.c_str(), \
        			DrHttpClient_POST_CONTENT_KEEPALIVE);
        }
        else{
        	showLog("Jni.DrHttpClient.singleHttpPost", "Http Connection:Close");
        	sprintf(cHttpBodyBuffer, DrHttpClient_POST, strParam.c_str(), \
        			DrHttpClient_HTTP_LANGUALE_CN, \
        			DrHttpClient_POST_BOUNDARY_DEF, \
        			len, strUrlWithPort.c_str(), \
        			DrHttpClient_POST_CONTENT_CLOSE);
        }

    	long dataSize = strlen(cHttpBodyBuffer) + len;
    	char *dataSend = new char[dataSize];
    	memset(dataSend, 0, dataSize);
    	memcpy(dataSend, cHttpBodyBuffer, strlen(cHttpBodyBuffer));
    	if(data)
    		memcpy(dataSend + strlen(cHttpBodyBuffer), data , len);

    	showLog("Jni.DrHttpClient.singleHttpPost","dataSize:%ld", dataSize);

		// add for log
#ifdef PRINT_JNI_LOG
		if(mIDrHttpClientCallback) {
			char logBuffer[dataSize + 1024];
			memset(logBuffer, '\0', dataSize + 1024);
			sprintf(logBuffer, "%s:(%ld)\r\n%s\r\n%s", "Jni.DrHttpClient.singleHttpPost", dataSize, dataSend, LOG_SEP);
			mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
		}
#endif

    	if (pTcpSocket->SendData((char*)dataSend, dataSize) == dataSize){
    		showLog("Jni.DrHttpClient.singleHttpPost", "data sent(%ld):%s", dataSize, dataSend);
    		long ret = httpHandle(pTcpSocket, iHttpCode, iContentLen, iThreadId, isKeepAlive, strUrl);
    		iRet = 1;
    		if(1 != ret){
    			bool bNeedToCallback = true;
	        	if(pTcpSocket){
	        		showLog("Jni.DrHttpClient.singleHttpPost", "client close socket iThreadId:%ld", iThreadId);
	        		if(pTcpSocket->getSocketId() == -1) {
	        			bNeedToCallback = false;
	        		}
	        		pTcpSocket->Close();
	        		delete pTcpSocket;
	        		pTcpSocket = NULL;
	        	}
	        	if(isKeepAlive){
#ifdef PRINT_JNI_LOG
	            	if(mIDrHttpClientCallback) {
	    				char logBuffer[DrHttpClient_BUFFER_1K + 1024];
	    				memset(logBuffer, '\0', DrHttpClient_BUFFER_1K + 1024);
	    				sprintf(logBuffer, "%s\r\nclient network error thread:%ld bIsNeedToCallback:%ld\r\n%s", "Jni.DrHttpClient.singleHttpPost", iThreadId, bNeedToCallback, LOG_SEP);
	    				mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
	    			}
#endif
					showLog("Jni.DrHttpClient.singleHttpPost", "client network error socket");
				   	if(mIDrHttpClientCallback)
				   		mIDrHttpClientCallback->onError((unsigned char*)HTTP_KEPPALIVE_REQUEST_ERROR, strlen(HTTP_KEPPALIVE_REQUEST_ERROR), iThreadId, bNeedToCallback);
	        	}

    		}
    		else{
				if(!isKeepAlive){
		        	if(pTcpSocket){
		        		showLog("Jni.DrHttpClient.singleHttpPost", "client close socket:%ld iThreadId:%ld", pTcpSocket->getSocketId(), iThreadId);
		        		pTcpSocket->Close();
		        		delete pTcpSocket;
		        		pTcpSocket = NULL;
		        	}
		        }
    		}
    	}
    	else{
    		bool bIsNeedToCallback = true;
        	if(isKeepAlive){
        		/* send data fail remove socket from map*/
            	m_DrMutex.lock();
            	HTTPSOCKETMAP::iterator it = m_SocketMap.find(strUrl);
            	if(it != m_SocketMap.end()){
            		//HTTPCONTECTIONSTATUS pIt = (HTTPCONTECTIONSTATUS)it->second;
            		if(pTcpSocket && it->second._pSocket){
            			if(pTcpSocket->getSocketId() == it->second._pSocket->getSocketId()) {
            				showLog("Jni.DrHttpClient.singleHttpPost", "Send data error m_SocketMap.erase:%s", (it->first).c_str());
                    		m_SocketMap.erase(it);
            			}
                		else {
                			showLog("Jni.DrHttpClient.singleHttpPost", "ptSocket(%ld) is not equal with it->second._pSocket(%ld)", pTcpSocket->getSocketId(), it->second._pSocket->getSocketId());
                			bIsNeedToCallback = false;
                		}
            		}
            		else {
            			showLog("Jni.DrHttpClient.singleHttpPost", "ptSocket or it->second._pSocket is null");
            			bIsNeedToCallback = false;
            		}
            	}
            	m_DrMutex.unlock();
        	}

        	if(pTcpSocket){
#ifdef PRINT_JNI_LOG
		if(mIDrHttpClientCallback) {
			char logBuffer[4096];
			memset(logBuffer, '\0', 4096);
			sprintf(logBuffer, "%s\r\n(socket:)URL:%s has been close!\r\n%s", "Jni.DrHttpClient.singleHttpPost", pTcpSocket->getSocketId(), strUrl.c_str(), LOG_SEP);
			mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
		}
#endif
        		showLog("Jni.DrHttpClient.singleHttpPost", "Send data error close socket iThreadId:%ld", iThreadId);
        		pTcpSocket->Close();
        		delete pTcpSocket;
        		pTcpSocket = NULL;
        	}

    		if(mIDrHttpClientCallback)
    		   	mIDrHttpClientCallback->onError((unsigned char*)HTTP_SEND_REQUEST_ERROR, strlen(HTTP_CONNECTION_ERROR), iThreadId, bIsNeedToCallback);
    	}
    	if(dataSend){
    		delete[] dataSend;
    	}
    }
	return iRet;
}
long DrHttpClient::httpGet(string strUrl){
	long iThreadId = -1;
	for(long i=0; i<MAXTHREADCOUNT; i++){
		if(m_ThreadArray[i]){
			if(!m_ThreadArray[i]->isRunning()){
				showLog("Jni.DrHttpClient.httpGet", "m_ThreadArray[%ld] use", i);
				HTTPCLIENTTHREADDATA thdata;
				thdata._url = strUrl;
				m_ThreadArray[i]->setParams(this, HTTPGET, thdata);
				return m_ThreadArray[i]->start();
				//return m_ThreadArray[i]->getThreadId();
			}
		}
	}
	return iThreadId;
}
long DrHttpClient::singleHttpGet(string strUrl, long iThreadId){
	showLog("Jni.DrHttpClient.singleHttpGet","strUrl:%s", strUrl.c_str());
	tcpSocket tSocket;
	//tcpSocket *ptSocket = new tcpSocket();
	long iRet = -1;
	long iHttpCode = 0, iContentLen = 0, iPort = 80;
	string strNewUrl="", strPort="", strParam="", strUrlWithPort;
	string retContent = "";
	char cHttpBodyBuffer[DrHttpClient_BUFFER_64K];
	bzero(cHttpBodyBuffer, sizeof(cHttpBodyBuffer));
	if(-1 == getUrlPort(strUrl, strNewUrl, strPort, strParam)){
		showLog("Jni.DrHttpClient.httpGet", "getUrlPort:error");
		return iRet;
	}
	strUrlWithPort = strNewUrl;
	if("" != strPort){
		strUrlWithPort += ":" + strPort;
		iPort = atoi(strPort.c_str());
	}
	if("" == strParam){
		strParam = "/";
	}
	showLog("Jni.DrHttpClient.httpGet", "strNewUrl:%s iPort:%i strParam:%s", strNewUrl.c_str(), iPort, strParam.c_str());
	if (tSocket.Connect(strNewUrl,iPort) == 1) {
		sprintf(cHttpBodyBuffer, DrHttpClient_GET, strParam.c_str(), \
				DrHttpClient_HTTP_LANGUALE_CN, \
				strUrlWithPort.c_str());
		showLog("Jni.DrHttpClient.httpGet", "cHttpBodyBuffer:\r\n%s", cHttpBodyBuffer);
#ifdef PRINT_JNI_LOG
		if(mIDrHttpClientCallback) {
			char logBuffer[DrHttpClient_BUFFER_1K + strUrl.length() + 1024];
			memset(logBuffer, '\0', DrHttpClient_BUFFER_1K + strUrl.length() + 1024);
			sprintf(logBuffer, "%s:(socket:%ld thread:%ld)\r\n%s\r\n%s", "Jni.DrHttpClient.httpGet", tSocket.getSocketId(), iThreadId, strUrl.c_str(), LOG_SEP);
			mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
		}
#endif
		if (tSocket.SendData(cHttpBodyBuffer, strlen(cHttpBodyBuffer)) == strlen(cHttpBodyBuffer)) {
			bzero(cHttpBodyBuffer, sizeof(cHttpBodyBuffer));
			//if (httpHandle(&tSocket, iHttpCode, iContentLen, (char*)cHttpBodyBuffer) == 1) {
			if (httpHandle(&tSocket, iHttpCode, iContentLen, iThreadId) != -1) {
			    showLog("Jni.DrHttpClient.httpGet", "iThreadId:%ld", iThreadId);
				iRet = 1;
			}
		}
		else{
			if(mIDrHttpClientCallback)
				mIDrHttpClientCallback->onError((unsigned char*)HTTP_SEND_REQUEST_ERROR, strlen(HTTP_SEND_REQUEST_ERROR), iThreadId, true);
		}
	}
	else{
		showLog("Jni.DrHttpClient.httpGet", "httpGet_Connect Fail:%s", strUrl.c_str());
		if(mIDrHttpClientCallback)
			mIDrHttpClientCallback->onError((unsigned char*)HTTP_CONNECTION_ERROR, strlen(HTTP_CONNECTION_ERROR), iThreadId, true);
	}
	tSocket.Close();

	return iRet;
}
long DrHttpClient::httpHandle(tcpSocket* ptSocket, long& iHttpCode, long& iContentLen, long iThreadId, bool isKeepAlive, string strUrl) {
	showLog("Jni.DrHttpClient.httpHandle", "ptSocket:%ld,iThreadId:%ld",ptSocket->getSocketId(), iThreadId);
	iHttpCode = 0;
	iContentLen = 0;
	long iRet = -1;
	long iCheckTimeout = 0;
	long iCheckBodyTimeout = 0;
	long iTimedelay = 1000;
	bool bHeaderOK = true;
	long iRLen = 0, iCurBufferlen = DrHttpClient_BUFFER_64K, iBufferLen = DrHttpClient_BUFFER_4K;
	char cRep[DrHttpClient_BUFFER_256B] = {'\0'};
	char *pHttpEnd;
	char pHttpBody[iBufferLen];
	char *pHttpRetBody;
	pHttpRetBody =  new char[iCurBufferlen];
	memset(pHttpRetBody, 0, iCurBufferlen);
	pHttpEnd = pHttpRetBody;
	showLog("Jni.DrHttpClient.httpHandle", "pHttpRetBody = new char[iCurBufferlen]");

	while (true && ptSocket) {
		memset(pHttpBody, '\0', iBufferLen);
		long iLen = ptSocket->RecvData(pHttpBody, iBufferLen, false);

		// add for log
		showLog("Jni.DrHttpClient.httpHandle", "pHttpBody:(len:%ld socket:%ld thread:%ld):\r\n%s", iBufferLen, ptSocket->getSocketId(), iThreadId, pHttpBody);

		if (iLen == -1) {
			// add for log
#ifdef PRINT_JNI_LOG
			if(mIDrHttpClientCallback) {
				char logBuffer[DrHttpClient_BUFFER_4K];
				memset(logBuffer, '\0', DrHttpClient_BUFFER_4K);
				sprintf(logBuffer, "%s:(socket:%ld thread:%ld)\r\n%s\r\n%s", "Jni.DrHttpClient.httpHandle", ptSocket->getSocketId(), iThreadId, "Recv HttpHeader Data length == -1 then break!", LOG_SEP);
				mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
			}
#endif
			showLog("Jni.DrHttpClient.httpHandle", "Recv HttpHeader Data length == -1 then break!");
			break;
		}
		if (iLen > 0) {
#ifdef PRINT_JNI_LOG
		if(mIDrHttpClientCallback) {
			char logBuffer[DrHttpClient_BUFFER_4K + 1024];
			memset(logBuffer, '\0', DrHttpClient_BUFFER_4K + 1024);
			sprintf(logBuffer, "%s:(len:%ld socket:%ld thread:%ld)\r\n%s\r\n%s", "Jni.DrHttpClient.httpHandle", iLen, ptSocket->getSocketId(), iThreadId, pHttpBody, LOG_SEP);
			mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
		}
#endif
			if(iRLen + iLen > iCurBufferlen){
				iCurBufferlen *= 2;
				char *pNewBuffer = new char[iCurBufferlen];
				memcpy(pNewBuffer, pHttpRetBody, iRLen);
				delete[] pHttpRetBody;
				pHttpRetBody = pNewBuffer;
				if(pHttpRetBody){
					showLog("Jni.DrHttpClient.httpHandle", "pHttpRetBody is not enough,new pHttpRetBody size is:%ld", iCurBufferlen);
				}
			}
			memcpy(pHttpRetBody + iRLen, pHttpBody, iLen);
			iRLen += iLen;
		}
		//find http end "\r\n\r\n"
		pHttpEnd = StringHandle::strIstr(pHttpRetBody, DrHttpClient_HTTP_END);
		if (pHttpEnd) {
			//get http reponse code
			bzero(cRep, sizeof(cRep));
			memcpy(cRep, pHttpRetBody + 9, 3);
			iHttpCode = atoi(cRep);
			showLog("Jni.DrHttpClient.httpHandle", "iHttpCode:%i", iHttpCode);
			//get content length begin
			//showLog("Jni.DrHttpClient.httpHandle", "pHttpRetBody:%s", pHttpRetBody);
			char *pContent_Length = StringHandle::strIstr(pHttpRetBody, DrHttpClient_HTTP_CON_LEN);
			//showLog("Jni.DrHttpClient.httpHandle", "pContent_Length:%s", pContent_Length);
			if(pContent_Length){
				char *pContent_Length_End = StringHandle::strIstr(pContent_Length, DrHttpClient_HTTP_LINE_END);
				//showLog("Jni.DrHttpClient.httpHandle", "pContent_Length_End:%s", pContent_Length_End);
				pContent_Length += strlen(DrHttpClient_HTTP_CON_LEN);
				//showLog("Jni.DrHttpClient.httpHandle", "pContent_Length:%s pContent_Length_End:%s", pContent_Length, pContent_Length_End);
				string strContentLength = StringHandle::findStringBetween(pHttpRetBody, (char*)pContent_Length, (char*)pContent_Length_End, cRep, sizeof(cRep));
				iContentLen = atoi(strContentLength.c_str());
				showLog("Jni.DrHttpClient.httpHandle", "iContentLen:%i", iContentLen);
			}
			else{
				showLog("Jni.DrHttpClient.httpHandle", "no iContentLen", iContentLen);
			}
			break;
		}
		else{
			showLog("Jni.DrHttpClient.httpHandle", "Still not find \\r\\n\\r\\n");
		}

		// 5绉���朵�榻�ttp澶�涓诲����
		showLog("Jni.DrHttpClient.httpHandle", "header iCheckTimeout:%ld", iCheckTimeout);
		if(5 < iCheckTimeout++){
			//ptSocket->Close();
			// add for log
#ifdef PRINT_JNI_LOG
			if(mIDrHttpClientCallback) {
				char logBuffer[DrHttpClient_BUFFER_1K];
				memset(logBuffer, '\0', DrHttpClient_BUFFER_1K);
				sprintf(logBuffer, "%s:(socket:%ld thread:%ld)\r\n%s\r\n%s", "Jni.DrHttpClient.httpHandle", ptSocket->getSocketId(), iThreadId, "Recv No data for 5 seconds from server, then break the connection!", LOG_SEP);
				mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
			}
#endif
			iRet = -1;
			break;
		}
	}

	long iHeaderLen = 0;
	iHeaderLen = (pHttpEnd - pHttpRetBody) + strlen(DrHttpClient_HTTP_END);
	showLog("Jni.DrHttpClient.httpHandle", "iRLen:%ld, HeaderLen:%ld",iRLen, iHeaderLen);
	if (iHeaderLen > 0 && (iRLen - iHeaderLen) >= 0) {
		showLog("Jni.DrHttpClient.httpHandle", "(iRLen - iHeaderLen) >= 0");
		iRLen = iRLen - iHeaderLen;
		if( 0 < iRLen ){
			memcpy(pHttpRetBody, pHttpRetBody + iHeaderLen, iRLen);
		}
		bzero(pHttpRetBody + iRLen, iHeaderLen);

		if(iRLen == iContentLen && iContentLen > 0) {
			// if already receive all data, callback at once
			showLog("Jni.DrHttpClient.httpHandle", "iRLen == iContentLen");
			iRet = 1;
			bHeaderOK = false;
		}
		//showLog("Jni.DrHttpClient.httpHandle", "Recv First Body data pHttpBody(%ld):%s", iRLen, pHttpBody);
	} else {
		iRLen = 0;
		bHeaderOK = false;
		showLog("Jni.DrHttpClient.httpHandle", "Recv Header error");
	}

	if(isKeepAlive){
		iTimedelay = 1000;
	}
	while(bHeaderOK && ptSocket){
		memset(pHttpBody, 0, sizeof(pHttpBody));

		long iLen = ptSocket->RecvData(pHttpBody, iBufferLen, false, iTimedelay);
		showLog("Jni.DrHttpClient.httpHandle", "Recv Body data pHttpBody:%s", pHttpBody);
		if(ptSocket->getSocketId() == -1) {
			showLog("Jni.DrHttpClient.httpHandle", "ptSocket->getSocketId() == -1 then break!");
			iRet = 0;
			break;
		}
		if (iLen == -1) {
			showLog("Jni.DrHttpClient.httpHandle", "RecvData length == -1 then break!");
			iRet = 0;
			break;
		}
		if (iLen > 0) {
			if(iRLen + iLen > iCurBufferlen){
				iCurBufferlen *= 2;
				char* pNewBuffer = new char[iCurBufferlen];
				memcpy(pNewBuffer, pHttpRetBody, iRLen);
				delete[] pHttpRetBody;
				pHttpRetBody = pNewBuffer;
			}
			memcpy(pHttpRetBody + iRLen, pHttpBody, iLen);
			iRLen += iLen;
			showLog("Jni.DrHttpClient.httpHandle", "ptSocket->RecvData iRLen:%ld", iRLen);
		}
		if(isKeepAlive){
			if(mIDrHttpClientCallback)
				mIDrHttpClientCallback->onReceiveData((unsigned char*)pHttpBody, iLen, iThreadId, true);
		}
			// add for log
#ifdef PRINT_JNI_LOG
			if(iLen > 0) {
				if(mIDrHttpClientCallback) {
					char logBuffer[DrHttpClient_BUFFER_4K + 1024];
					memset(logBuffer, '\0', DrHttpClient_BUFFER_4K + 1024);
				sprintf(logBuffer, "%s:(len:%ld socket:%ld thread:%ld)\r\n%s\r\n%s", "Jni.DrHttpClient.httpHandle", iLen, ptSocket->getSocketId(), iThreadId, pHttpBody, LOG_SEP);
					mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
				}
			}
#endif
		if (iContentLen > 0){
			showLog("Jni.DrHttpClient.httpHandle", "iRLen:%ld iContentLen:%ld", iRLen, iContentLen);
			if (iRLen >= iContentLen) {
				iRet = 1;
				showLog("Jni.DrHttpClient.httpHandle", "iRLen >= iContentLen then break!");
				break;
			}
		}
		else{
			showLog("Jni.DrHttpClient.httpHandle", "keepalive RecvData iLen:%ld", iLen);
		}
		if(!isKeepAlive && iLen == 0) {
			showLog("Jni.DrHttpClient.httpHandle", "body iCheckBodyTimeout:%ld", iCheckBodyTimeout);
			if(10 < iCheckBodyTimeout++){
				iRet = 0;
				showLog("Jni.DrHttpClient.httpHandle", "Recv No body data for 10 times from server, then break the connection!");
#ifdef PRINT_JNI_LOG
				if(mIDrHttpClientCallback) {
					char logBuffer[DrHttpClient_BUFFER_2K];
					memset(logBuffer, '\0', DrHttpClient_BUFFER_2K);
					sprintf(logBuffer, "%s:(socket:%ld thread:%ld)\r\n%s\r\n%s", "Jni.DrHttpClient.httpHandle", ptSocket->getSocketId(), iThreadId, "Recv No body data for 10 times from server, then break the connection!", LOG_SEP);
					mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
				}
#endif
				break;
			}
		}
		else {
			iCheckBodyTimeout = 0;
		}
	}
	long ret = iRet;
	//m_DrMutex.lock();
	if(-1 != ret){
		//HTTPSOCKETMAP::iterator it = m_SocketMap.find(strUrl);
		bool bIsNeedToCallback = true;
		if(0 == ret){
			/* server close*/
        	if(ptSocket){
        		showLog("Jni.DrHttpClient.httpHandle", "client close socket iThreadId:%ld", iThreadId);
        	}
        	if(isKeepAlive){
        		/* remove url which is connected*/
        		m_DrMutex.lock();
        		HTTPSOCKETMAP::iterator it = m_SocketMap.find(strUrl);
    			if(it != m_SocketMap.end()){
    				if(it->second._pSocket && ptSocket) {
    					showLog("Jni.DrHttpClient.httpHandle", "m_SocketMap.erase:%s", (it->first).c_str());
    					if(it->second._pSocket->getSocketId() == ptSocket->getSocketId()) {
    	            		m_SocketMap.erase(it);
    					}
    					else {
    						bIsNeedToCallback = false;
    					}
    				}
    				else {
    					bIsNeedToCallback = false;
    				}
    			}
    			m_DrMutex.unlock();
        	}
        	else {
        		/* callback to view*/
#ifdef PRINT_JNI_LOG
            	if(mIDrHttpClientCallback) {
    				char logBuffer[DrHttpClient_BUFFER_1K + 1024];
    				memset(logBuffer, '\0', DrHttpClient_BUFFER_1K + 1024);
    				sprintf(logBuffer, "%s:(socket:%ld thread:%ld bIsNeedToCallback:%ld)\r\nserver close!\r\n%s", "Jni.DrHttpClient.httpHandle", ptSocket->getSocketId(), iThreadId, bIsNeedToCallback, LOG_SEP);
    				mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
    			}
#endif
        		if(mIDrHttpClientCallback){
        			//showLog("Jni.DrHttpClient.httpHandle", "mIDrHttpClientCallback:onSuccess");
        			showLog("Jni.DrHttpClient.httpHandle", "mIDrHttpClientCallback:onSuccess iRLen:%ld", iRLen);
        			mIDrHttpClientCallback->onSuccess((unsigned char*)(pHttpRetBody), iRLen, iThreadId, bIsNeedToCallback);
        		}
        	}
		}
		else if( 1 == ret){
			/* server keep alive*/
			showLog("Jni.DrHttpClient.httpHandle", "server do not close socket iThreadId:%ld", iThreadId);
			if(isKeepAlive){
				/* mark connected url is recving*/
				m_DrMutex.lock();
				HTTPSOCKETMAP::iterator it = m_SocketMap.find(strUrl);
		        if(it != m_SocketMap.end()){
		        	if(it->second._pSocket && ptSocket) {
		        		if(it->second._pSocket->getSocketId() == ptSocket->getSocketId()) {
				        	it->second._isReceiving = false;
				        	showLog("Jni.DrHttpClient.httpHandle", "url:%s socket:%ld can recv again,iThreadId:%ld",(it->first).c_str(), ptSocket->getSocketId(), iThreadId);
#ifdef PRINT_JNI_LOG
				        	if(mIDrHttpClientCallback) {
								char logBuffer[DrHttpClient_BUFFER_2K + 1024];
								memset(logBuffer, '\0', DrHttpClient_BUFFER_2K + 1024);
								sprintf(logBuffer, "%s:(socket:%ld thread:%ld)\r\nurl:%s socket can recv again.\r\n%s", "Jni.DrHttpClient.httpHandle", ptSocket->getSocketId(), iThreadId, (it->first).c_str(), LOG_SEP);
								mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
							}
#endif
		        		}
		        	}
		        }
		        m_DrMutex.unlock();
			}
			else{
	        	if(ptSocket){
	        		showLog("Jni.DrHttpClient.httpHandle", "client close socket:%ld iThreadId:%ld", ptSocket->getSocketId(), iThreadId);
	        	}
	        }
#ifdef PRINT_JNI_LOG
        	if(mIDrHttpClientCallback) {
				char logBuffer[DrHttpClient_BUFFER_2K];
				memset(logBuffer, '\0', DrHttpClient_BUFFER_2K);
				sprintf(logBuffer, "%s:(socket:%ld thread:%ld bIsNeedToCallback:%ld)\r\nserver keep alive\r\n%s", "Jni.DrHttpClient.httpHandle", ptSocket->getSocketId(), iThreadId, bIsNeedToCallback, LOG_SEP);
				mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), iThreadId);
			}
#endif
    		/* callback to view*/
    		if(mIDrHttpClientCallback){
    			showLog("Jni.DrHttpClient.httpHandle", "mIDrHttpClientCallback:onSuccess");
    			mIDrHttpClientCallback->onSuccess((unsigned char*)(pHttpRetBody), iRLen, iThreadId, bIsNeedToCallback);
    		}
		}
	}
	else{
		bool bIsNeedToCallback = true;
		if(isKeepAlive){
			/* header error*/
        	if(ptSocket){
        		showLog("Jni.DrHttpClient.httpHandle", "client close socket:%ld iThreadId:%ld", ptSocket->getSocketId(), iThreadId);
        	}
			/* remove url which is connected*/
        	m_DrMutex.lock();
			HTTPSOCKETMAP::iterator it = m_SocketMap.find(strUrl);
			if(it != m_SocketMap.end()){
				if(it->second._pSocket && ptSocket) {
					if(it->second._pSocket->getSocketId() == ptSocket->getSocketId()) {
						showLog("Jni.DrHttpClient.httpHandle", "m_SocketMap.erase:%s", (it->first).c_str());
			        	m_SocketMap.erase(it);
					}
	        		else {
	        			bIsNeedToCallback = false;
	        		}
				}
				else {
        			showLog("Jni.DrHttpClient.singleHttpPost", "ptSocket or it->second._pSocket is null");
        			bIsNeedToCallback = false;
				}
			}
			m_DrMutex.unlock();
		}
		/* callback to view*/
		if(mIDrHttpClientCallback){
			showLog("Jni.DrHttpClient.httpHandle", "mIDrHttpClientCallback:onError");
			mIDrHttpClientCallback->onError((unsigned char*)HTTP_HEADER_ERROR, strlen(HTTP_HEADER_ERROR), iThreadId, bIsNeedToCallback);
		}
	}
	//m_DrMutex.unlock();
	delete[] pHttpRetBody;
	showLog("Jni.DrHttpClient.httpHandle", "delete pHttpRetBody!");
	return iRet;
}

long DrHttpClient::getUrlPort(string url, string &newUrl, string &port, string &param){
//	showLog("Jni.DrHttpClient.getUrlPort", "before new tempBuf url:%s", url.c_str());
	char *tempBuf = new char[DrHttpClient_BUFFER_4K];
	long ret = -1;
	const char *portEnd = "/";
	const char *portStart = ":";
	char *end;
	char *host = StringHandle::strIstr(url.c_str(), HTTP_URL_START);
//	showLog("Jni.DrHttpClient.getUrlPort", "host before:%s", host);
	if(host){
		host += sizeof(HTTP_URL_START) - 1;
	}
	else{
		host = (char*)url.c_str();
	}
	//showLog("Jni.DrHttpClient.getUrlPort", "host after:%s", host);
	char *sep = StringHandle::strIstr(host, portStart);
	if (sep) {
		for (end = sep + 1; *end; ++end) {
			if (toupper(*end) > '9' || toupper(*end) < '0') {
//				showLog("Jni.DrHttpClient.getUrlPort", "url(%x):%s", url.c_str(),url.c_str());
//				showLog("Jni.DrHttpClient.getUrlPort", "host(%x):%s", host, host);
//				showLog("Jni.DrHttpClient.getUrlPort", "sep(%x):%s", sep, sep);
				newUrl = StringHandle::findStringBetween((char*)url.c_str(), (char*)host, (char*)sep, tempBuf, DrHttpClient_BUFFER_4K);
//				showLog("Jni.DrHttpClient.getUrlPort", "newUrl:%s", newUrl.c_str());
				port = StringHandle::findStringBetween((char*)url.c_str(), (char*)sep + 1, (char*)end, tempBuf, DrHttpClient_BUFFER_4K);
//				showLog("Jni.DrHttpClient.getUrlPort", "port:%s", port.c_str());
				if(NULL != end){
					param = end;
				}
//				showLog("Jni.DrHttpClient.getUrlPort", "newUrl:%s port:%s", newUrl.c_str(), port.c_str());
				ret = 1;
				break;
			}
		}
	}
	else{
		for (end = host; *end; ++end) {
			if (toupper(*end) == toupper(*portStart)) {
				newUrl = StringHandle::findStringBetween((char*)url.c_str(), (char*)host, (char*)end, NULL, 0);
				if(NULL != end){
					param = end;
				}
				ret = 1;
				break;
			}
		}
		showLog("Jni.DrHttpClient.getUrlPort", "newUrl:%s no port found!",newUrl.c_str());
	}
	showLog("Jni.DrHttpClient.getUrlPort", "ret:%ld", ret);
	if(tempBuf)
		delete[] tempBuf;
	return ret;
}
bool DrHttpClient::stopConnection(string strUrl){
	/* remove url which is connected*/
	showLog("Jni.DrHttpClient stopConnection", "strUrl=%s", strUrl.c_str());
	bool bFlag = false;
	m_DrMutex.lock();
	HTTPSOCKETMAP::iterator it = m_SocketMap.find(strUrl);
	if(it != m_SocketMap.end()){
		tcpSocket *pSocket = it->second._pSocket;
		if(pSocket){
//#ifdef PRINT_JNI_LOG
//			if(mIDrHttpClientCallback) {
//				char logBuffer[DrHttpClient_BUFFER_2K];
//				memset(logBuffer, '\0', DrHttpClient_BUFFER_2K);
//				sprintf(logBuffer, "%s:\r\nstopConnection close socket:%ld _isReceiving:%ld\r\n%s", "Jni.DrHttpClient.stopConnection", pSocket->getSocketId(), (it->second)._isReceiving, LOG_SEP);
//				mIDrHttpClientCallback->onWriteLog((unsigned char*)logBuffer, strlen(logBuffer), -1);
//			}
//#endif
			pSocket->Close();
			bFlag = true;
			showLog("Jni.DrHttpClient stopConnection", "_isReceiving:%ld", (it->second)._isReceiving);
			if((it->second)._isReceiving == false) {
				delete pSocket;
				pSocket = NULL;
			}
		}
    	m_SocketMap.erase(it);
    	showLog("Jni.DrHttpClient.stopConnection", "stopConnection m_SocketMap.erase:%s", (it->first).c_str());
	}
	m_DrMutex.unlock();
	showLog("Jni.DrHttpClient stopConnection", "bFlag:%ld", bFlag);
	return bFlag;
}
