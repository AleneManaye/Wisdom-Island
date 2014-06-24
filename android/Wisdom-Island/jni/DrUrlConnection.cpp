/*
 * File         : DrUrlConnection.h
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrPalm DrUrlConnection src
 */

#include <stdio.h>
#include <stdlib.h>
using namespace std;

#include "DrUrlConnection.h"
#include "DrHttpPostBody.h"
#include "Arithmetic.h"
#include "polarssl/polarssl/aes.h"

DrUrlConnection::DrUrlConnection() {
	m_id = 0;
	m_wholeUrl = "";
	m_path = "";
	m_basepath = "";
	m_isKeepAlive = false;
	m_isPost = false;
	m_data = NULL;
	m_dataLen = 0;
	m_DrHttpClient.setCallback(this);
}

DrUrlConnection::~DrUrlConnection() {
	if(m_data){
		delete m_data;
		m_dataLen = 0;
	}
}
void DrUrlConnection::setCallback(JNICALLBACK callback){
	m_JniCallback = callback;

}

void DrUrlConnection::setKeepAlive(bool isKeepAlive){
	m_isKeepAlive = isKeepAlive;
}
void DrUrlConnection::setPost(bool isPost){
	m_isPost = isPost;
}
void DrUrlConnection::setBasePath(string basepath){
	m_basepath = basepath;
}
void DrUrlConnection::setPath(string path){
	m_path = path;
}
void DrUrlConnection::setDomain(string domain){
	m_domain = domain;
}

void DrUrlConnection::addParam(string key, string value){
	URLPARAMMAP::iterator it = m_mapParam.find(key);
	if(m_mapParam.end() != it){
		it->second = value;
	}
	else{
		m_mapParam.insert(URLPARAMMAP::value_type(key, value));
		showLog("Jni.DrUrlConnection.addParam", "key:%s value:%s", key.c_str(), value.c_str());
	}
}
void DrUrlConnection::addFile(string key,string fileext, char* data, long len){
	URLFILEMAP::iterator it = m_mapFile.find(key);
	if(m_mapFile.end() != it){

	}
	else{
		FILESTRUCT file(data, len,fileext);
		m_mapFile.insert(URLFILEMAP::value_type(key, file));
		showLog("Jni.DrUrlConnection.addFile", "key:%s data:%ld len:%ld", key.c_str(), data, len);
	}
}
void DrUrlConnection::setData(char *buf, long len){
	m_data = buf;
	m_dataLen = len;
	showLog("Jni.DrUrlConnection.setData", "m_dataLen:%ld len:%ld", m_dataLen, len);
}
void DrUrlConnection::clearFile(){

}
void DrUrlConnection::clearParam(){
	m_wholeUrl = "";
	m_path = "";
	m_basepath = "";
	m_isKeepAlive = false;
	m_isPost = false;
	m_mapParam.clear();
	m_mapFile.clear();
	m_data = NULL;
	m_dataLen = 0;
}
long DrUrlConnection::startRequest(){
	showLog("Jni.DrUrlConnection.startRequest", "startRequest");
	long id = -1;
	long filecount = 0;
	char cFileCount[10];


	basic_string<char>::size_type index = m_domain.find(HTTP_URL_START, 0);
	if( string::npos == index ){
		m_wholeUrl = HTTP_URL_START;
	}
	m_wholeUrl += m_domain;
	m_wholeUrl += "/";
	if("" != m_basepath){
		m_wholeUrl += m_basepath;
		m_wholeUrl += "/";
	}
	m_wholeUrl += m_path;
	URLPARAMMAP::iterator it;
	URLFILEMAP::iterator itfile;

	if(m_isPost){
		DrHttpPostBody postbody;
		if(m_data){
			showLog("Jni.DrUrlConnection.addData", "postbody.addData:%s", m_data);
			postbody.addData(m_data, m_dataLen);
		}
		else{
			for(it = m_mapParam.begin(); it != m_mapParam.end(); it++){
				postbody.addString(it->first, it->second);
			}
			for(itfile = m_mapFile.begin(); itfile != m_mapFile.end(); itfile++){
//				string fileKey = DrHttpClient_POST_FILE_PRE;
//				sprintf(cFileCount, "%ld", filecount++);
//				fileKey += cFileCount;
				string fileKey = itfile->first;
				FILESTRUCT file(itfile->second._data, itfile->second._size,itfile->second._fileext);
				string fileName = fileKey;
				fileName += ".";
				fileName += itfile->second._fileext;
				showLog("Jni.DrUrlConnection.addFile", "postbody.addFile fileKey:%s, fileName:%s data:%ld len:%ld", fileKey.c_str(), fileName.c_str(), file._data, file._size);
				postbody.addFile(fileKey, fileName, file._data, file._size);

			}
		}
		//string body = postbody.getData();
		showLog("Jni.DrUrlConnection.startRequest", "threadid:%l postbody.getData(%ld)", id, postbody.getSize());
		id = httpPost(m_wholeUrl, (char*)postbody.getData(), postbody.getSize(), m_isKeepAlive);
	}
	else{
		for(it = m_mapParam.begin(); it != m_mapParam.end(); it++){
			if(it == m_mapParam.begin())
				m_wholeUrl += "?";
			else
				m_wholeUrl += "&";
			m_wholeUrl += it->first;
			m_wholeUrl += "=";
			m_wholeUrl += it->second;
		}
		translate(m_wholeUrl);
		id = httpGet(m_wholeUrl);
		showLog("Jni.DrUrlConnection.startRequest", "m_wholeUrl:%s threadid:%l", m_wholeUrl.c_str(), id);
	}
	clearParam();
	return id;
}
long DrUrlConnection::httpGet(string strUrl){
	return m_DrHttpClient.httpGet(strUrl);
}
long DrUrlConnection::httpPost(string strUrl, char* data, long len, bool isKeepAlive){
	return m_DrHttpClient.httpPost(strUrl, data, len, isKeepAlive);
}
void DrUrlConnection::onSuccess(unsigned char* buf, long len, long iThreadId, bool bBackToView){
	m_JniCallback.onSuccess(buf, len, iThreadId, bBackToView);
}
void DrUrlConnection::onError(unsigned char* buf, long len, long iThreadId, bool bBackToView){
	m_JniCallback.onError(buf, len, iThreadId, bBackToView);
}
void DrUrlConnection::onReceiveData(unsigned char* buf, long len, long iThreadId, bool bBackToView){
	m_JniCallback.onReceiveData(buf, len, iThreadId, bBackToView);
}
void DrUrlConnection::onWriteLog(unsigned char* buf, long len, long iThreadId){
	m_JniCallback.onWriteLog(buf, len, iThreadId);
}
bool DrUrlConnection::stopConnection(string strUrl){
	string wholeUrl = "";
	basic_string<char>::size_type index = strUrl.find(HTTP_URL_START, 0);
		if( string::npos == index ){
			wholeUrl = HTTP_URL_START;
	}
	wholeUrl += strUrl;
	wholeUrl += "/";
	return m_DrHttpClient.stopConnection(wholeUrl);
}
void DrUrlConnection::translate(string &str){
	string ret = "";
	const char* ptr = str.c_str();
	for(long i =0; i< str.length(); i++){
		if(ptr[i] == ' '){
			ret += "+";
		}
		else{
			ret += ptr[i];
		}
	}
	str = ret;
}
string DrUrlConnection::AesEncrypt(string initKey, string src) {
	std::string strRet;
	aes_context aes_key;
	char iv[16] = {0};
	char in[16] = {0};
	unsigned char* out;
	unsigned char temp[16] = {0};

	unsigned int k = 16;
	unsigned int c = src.length() / k;
	unsigned int d = src.length() % k;
	int j=0;
	int count=0;

	// 加密密钥补位，防止内存读取内存错误
	unsigned char key[17] = {0};
	memcpy(key, initKey.c_str(),(initKey.length()>16) ? 16: initKey.length());

	// 使用传入的密钥，转换获取aes_key(128.192.256对应加密16，24，32位)
	if(aes_setkey_enc(&aes_key, key, 128) < 0)
	{
		return "";
	}

	out = new unsigned char[(c + 1) * k];

	// 分块加密，先对整块加密
	for(j = 0;j<c;j++){
		memcpy(in, src.c_str()+k*j, k);
		aes_crypt_ecb(&aes_key, 1, (unsigned char*)in, out+k*j);
		count+=k;
	}

	// AES补位，明文为16整数倍时，尾部默认补16个字节，每个为16，当明文不是16的整数倍时，取余后补足16位补位数值为16-currentCount
	if( d>=0 ){
		memcpy(in,src.c_str()+k*j,d);
		for(int i = d; i < k; i ++)
		{
			char p = 16-d;
			in[i] = p;
		}
		aes_crypt_ecb(&aes_key, 1, (unsigned char*)in, temp);
		memcpy(out+count, temp, k);
		count+=k;
	}

	strRet = "";
	for(int i = 0; i<count ;i++) {
		char c[2];
		sprintf(c, "%x", out[i]);
		strRet += c;
	}
	delete[] out;
	return strRet;
}
string DrUrlConnection::AesDecrypt(string initKey, string src) {
	string strRet = "";
	aes_context aes_key;
	char iv[16] = {0};
	char in[16] = {0};
	char* out;
	unsigned char temp[16] = {0};

	char *dst = new char[src.length() + 1];
	Arithmetic::HexToAscii(src.c_str(), src.length(), dst);

	unsigned int k = 16;
	unsigned int c = (src.length() / 2) / k;
	unsigned int d = (src.length() / 2) % k;
	int j=0;
	int count=0;

	// 加密密钥补位，防止内存读取内存错误
	unsigned char key[17] = {0};
	memcpy(key, initKey.c_str(),(initKey.length()>16) ? 16: initKey.length());

	//使用传入的密钥，转换获取aes_key(128.192.256对应加密16，24，32位)
	if(aes_setkey_dec(&aes_key, key, 128) < 0)
	{
		return "";
	}

	out = new char[c * k];
	memset(out, '\0', c * k);
	//分快加密，先对整块加密
	for(j = 0;j<c;j++){
		memcpy(in, dst+k*j, k);
		aes_crypt_ecb(&aes_key, AES_DECRYPT, (unsigned char *)in, (unsigned char *)out+k*j);
		count+=k;
	}

	int index = c*k;
	char i = out[c * k - 1];
	out[c * k - i] = '\0';

	strRet = out;
	delete[] dst;

	return strRet;
}
