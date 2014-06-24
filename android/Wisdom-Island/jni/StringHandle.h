/*
 * File         : StringHandle.h
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrPalm StringHandle include
 */

#ifndef _INC_STRINGHANDLE_
#define _INC_STRINGHANDLE_

#include <ctype.h>
#include "DrLog.h"
#include "Arithmetic.h"
class StringHandle{
public:
	static inline char* strIstr(const char *haystack, const char *needle) {
	    if (!*needle) {
	    	return (char*)haystack;
	    }
	    for (; *haystack; ++haystack) {
	    	if (toupper(*haystack) == toupper(*needle)) {
	    		const char *h, *n;
	    		for (h = haystack, n = needle; *h && *n; ++h, ++n) {
	    			if (toupper(*h) != toupper(*n)) {
	    				break;
	    			}
	    		}
	    		if (!*n) {
	    			return (char*)haystack;
	    		}
	    	}
	    }
	    return 0;
	}
	static inline string findStringBetween(char* pData, char* pBegin, char* pEnd, char* pTmpBuffer, long iTmpLen) {
		string strRet = "";
		char *pC_Begin = NULL, *pC_End = NULL, *pRep = NULL;
		long iLen = DrCOM_BUFFER_256B;
		showLog("Jni.StringHandle.findStringBetween", "enterance");
		if (pTmpBuffer && iTmpLen > 0) {
			showLog("Jni.StringHandle.findStringBetween", "use pTmpBuffer");
			pRep = pTmpBuffer;
			iLen = iTmpLen;
		} else {
			showLog("Jni.StringHandle.findStringBetween", "new DrCOM_BUFFER_256B bfore");
			pRep = new char[DrCOM_BUFFER_256B];
			showLog("Jni.StringHandle.findStringBetween", "new DrCOM_BUFFER_256B after");
		}
		bzero(pRep, iLen);
		//showLog("Jni.StringHandle.findStringBetween", "pData:%s", pData);

		if ((pC_Begin = strIstr(pData, pBegin)) > 0) {
			//showLog("Jni.StringHandle.findStringBetween", "pBegin:%s", pBegin);
			//pC_Begin = pC_Begin + strlen(pBegin);
			if ((pC_End = strIstr(pC_Begin, pEnd)) > 0) {
				//showLog("Jni.StringHandle.findStringBetween", "pC_End:%s", pC_End);
				memcpy(pRep, pC_Begin, pC_End - pC_Begin);
				strRet = pRep;
			}
			//showLog("DrServiceJni:findStringBetween", "pC_Begin:%s pC_End:%s", pC_Begin, pC_End);
		}

		if (!pTmpBuffer || iTmpLen <= 0) {
			showLog("Jni.StringHandle.findStringBetween", "delete DrCOM_BUFFER_256B");
			delete pRep;
		}

		return strRet;
	}

	static inline string grantMD5(string strData) {
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
	static inline string trim(string strData) {
		return strData.erase(0, strData.find_first_not_of(" \t\r\n")).erase(strData.find_last_not_of(" \t\r\n") + 1);
	}
};

#endif


