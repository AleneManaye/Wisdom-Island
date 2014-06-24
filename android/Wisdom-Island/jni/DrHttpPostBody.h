/*
 * File         : DrHttpPostBody.h
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrPalm DrHttpPostBody include
 */

#ifndef _INC_DRHTTPPOSTBODY_
#define _INC_DRHTTPPOSTBODY_

#include "DrHttpClientDefine.h"
#include "DrLog.h"
#define DATACAPACITY_64K 65535
#define BUFSIZE_4K 4096
#define FILETYPECOUNT 64
class DrHttpPostBody{
private:
	char *m_data;
	long m_dataSize;
	long m_dataCapacity;
protected:
	static string MIME_MapTable[][2];
public:

	DrHttpPostBody(){
		m_dataCapacity = DATACAPACITY_64K;
		m_data = new char[m_dataCapacity];
		m_dataSize = 0;
	};
	~DrHttpPostBody(){
		if(m_data){
			delete[] m_data;
			m_data = NULL;
		}
		m_dataSize = 0;
	};
	void checkBufferEnough(long size = BUFSIZE_4K){
		showLog("Jni.DrHttpPostBody.checkBufferEnough", "m_dataSize:%ld,m_dataCapacity:%ld", m_dataSize, m_dataCapacity);
		if(size + m_dataSize > m_dataCapacity){
			m_dataCapacity *= 2;
			char *p = new char[m_dataCapacity];
			memcpy(p, m_data, m_dataSize);
			delete m_data;
			m_data = p;
			showLog("Jni.DrHttpPostBody.checkBufferEnough", "m_dataSize:%ld,m_dataCapacity:%ld", m_dataSize, m_dataCapacity);
		}
	}
	void addString(string key, string value){
		/*
		 * Sample:
		 * Content-Disposition: form-data; name="key"
		 * Content-Type:multipart/mixed, boundary=-----FILED7RdP4AaL6Md158c9
		 */
		checkBufferEnough();
		string strValue = "";
		strValue += "--";
		strValue += DrHttpClient_POST_BOUNDARY_DEF;
		strValue += "\r\n";
		strValue += DrHttpClient_POST_CONTENT_DISPOSITION;
		strValue += " name=\"";
		strValue += key;
		strValue += "\"\r\n";
		strValue += DrHttpClient_HTTP_CONTENT_TYPE;
		strValue +=	DrHttpClient_POST_TEXT_PLAIN;
		strValue += DrHttpClient_HTTP_CHARSET_UTF8;
		strValue += "\r\n";
		strValue += DrHttpClient_HTTP_CONTENT_TRANS_8_BIT;
		strValue += "\r\n";
		strValue += "\r\n";
		strValue += value;
		strValue += "\r\n";

		memcpy(m_data + m_dataSize, strValue.c_str(), strlen(strValue.c_str()));
		m_dataSize += strlen(strValue.c_str());
		showLog("Jni.DrHttpPostBody.addString", "strValue.size:%ld ", strValue.size());

	};
	void addFile(string key, string filename, const char *buf, long len){
		/*
		 * Sample:
		 * Content-Disposition: form-data; name="key"
		 * Content-Type:multipart/mixed, boundary=-----FILED7RdP4AaL6Md158c9
		 *
		 * -----FILED7RdP4AaL6Md158c9
		 * Content-Disposition: attachment; filename ="filename"
		 */
		/*
		 * Sample:
		 * Content-Disposition: form-data; name="key" filename="a123.jpg"
		 * Content-Type:application/octet-stream"
		 * Content-Transfer-Encoding: binary
		 *
		 *
		 * buf
		 */
		showLog("Jni.DrHttpPostBody.checkBufferEnough", "key:%s, filename:%s buf(%ld):%ld ", key.c_str(), filename.c_str(), len, buf);
		checkBufferEnough(BUFSIZE_4K + len);
		string strValue = "";
		strValue += "--";
		strValue += DrHttpClient_POST_BOUNDARY_DEF;
		strValue += "\r\n";
		strValue += DrHttpClient_POST_CONTENT_DISPOSITION;
		strValue += "name=\"";
		strValue += key;
		strValue += "\";";
		strValue += "filename=\"";
		strValue += filename;
		strValue += "\"\r\n";

		strValue += DrHttpClient_HTTP_CONTENT_TYPE;
		strValue += getContentType(filename);//DrHttpClient_POST_APPLICATION;
		strValue += "\r\n";

		strValue += DrHttpClient_HTTP_CONTENT_TRANS_BINARY;
		strValue += "\r\n";
		strValue += "\r\n";

		showLog("Jni.DrHttpPostBody.addFile", "strValue:%s", strValue.c_str());
		memcpy(m_data + m_dataSize, strValue.c_str(), strlen(strValue.c_str()));
		m_dataSize += strlen(strValue.c_str());

		showLog("Jni.DrHttpPostBody.addFile", "buf(%ld):%s, m_dataSize:%ld", len, buf, m_dataSize);
		memcpy(m_data + m_dataSize, buf, len);
		m_dataSize += len;

		showLog("Jni.DrHttpPostBody.addFile", "buf(%ld):%s, m_dataSize:%ld", len, buf, m_dataSize);

		strValue = "\r\n--";
		strValue += DrHttpClient_POST_BOUNDARY_DEF;
		strValue += "--\r\n";
		memcpy(m_data + m_dataSize, strValue.c_str(), strlen(strValue.c_str()));
		m_dataSize += strlen(strValue.c_str());

	}
	void addData(char *buf, long len){
		checkBufferEnough(BUFSIZE_4K + len);
		memcpy(m_data + m_dataSize, buf, len);
			m_dataSize += len;
	}
	const char* getData(){
		 return m_data;
	};
	long getSize(){
		return m_dataSize;
	}
private:
	string getContentType(string strFileName){
		strFileName = strFileName.substr(strFileName.find('.'));
		string strValue = "application/octet-stream";
    	for(long i=0;i< FILETYPECOUNT; i++){
    		if(0 == strFileName.compare(MIME_MapTable[i][0]))
    			strValue = MIME_MapTable[i][1];
    	}
		return strValue;
	}
	//在MIME和文件类型的匹配表中找到对应的MIME类型。

};
string DrHttpPostBody::MIME_MapTable[][2] = {
				//{后缀名， MIME类型}
				{".3gp", "video/3gpp"},
				{".apk", "application/vnd.android.package-archive"},
				{".asf", "video/x-ms-asf"},
				{".avi", "video/x-msvideo"},
				{".bin", "application/octet-stream"},
				{".bmp", "image/bmp"},
				{".c", "text/plain"},
				{".class", "application/octet-stream"},
				{".conf", "text/plain"},
				{".cpp", "text/plain"},
				{".doc", "application/msword"},
				{".exe", "application/octet-stream"},
				{".gif", "image/gif"},
				{".gtar", "application/x-gtar"},
				{".gz", "application/x-gzip"},
				{".h", "text/plain"},
				{".htm", "text/html"},
				{".html", "text/html"},
				{".jar", "application/java-archive"},
				{".java", "text/plain"},
				{".jpeg", "image/jpeg"},
				{".jpg", "image/jpeg"},
				{".js", "application/x-javascript"},
				{".log", "text/plain"},
				{".m3u", "audio/x-mpegurl"},
				{".m4a", "audio/mp4a-latm"},
				{".m4b", "audio/mp4a-latm"},
				{".m4p", "audio/mp4a-latm"},
				{".m4u", "video/vnd.mpegurl"},
				{".m4v", "video/x-m4v"},
				{".mov", "video/quicktime"},
				{".mp2", "audio/x-mpeg"},
				{".mp3", "audio/x-mpeg"},
				{".mp4", "video/mp4"},
				{".mpc", "application/vnd.mpohun.certificate"},
				{".mpe", "video/mpeg"},
				{".mpeg", "video/mpeg"},
				{".mpg", "video/mpeg"},
				{".mpg4", "video/mp4"},
				{".mpga", "audio/mpeg"},
				{".msg", "application/vnd.ms-outlook"},
				{".ogg", "audio/ogg"},
				{".pdf", "application/pdf"},
				{".png", "image/png"},
				{".pps", "application/vnd.ms-powerpoint"},
				{".ppt", "application/vnd.ms-powerpoint"},
				{".prop", "text/plain"},
				{".rar", "application/x-rar-compressed"},
				{".rc", "text/plain"},
				{".rmvb", "audio/x-pn-realaudio"},
				{".rtf", "application/rtf"},
				{".sh", "text/plain"},
				{".tar", "application/x-tar"},
				{".tgz", "application/x-compressed"},
				{".txt", "text/plain"},
				{".wav", "audio/x-wav"},
				{".wma", "audio/x-ms-wma"},
				{".wmv", "audio/x-ms-wmv"},
				{".wps", "application/vnd.ms-works"},
				//{".xml", "text/xml"},
				{".xml", "text/plain"},
				{".z", "application/x-compress"},
				{".zip", "application/zip"},
				{"", "*/*"}
};
#endif
