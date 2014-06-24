/*
 * File         : DrJniCallback.h
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrPalm DrJniCallback include
 */
#ifndef _INC_DRJNICALLBACK_
#define _INC_DRJNICALLBACK_
typedef void (*OnSuccess)(unsigned char* buf, long len, long iThreadId, bool bBackToView);
typedef void (*OnError)(unsigned char* buf, long len, long iThreadId, bool bBackToView);
typedef void (*OnReceiveData)(unsigned char* buf, long len, long iThreadId, bool bBackToView);
typedef void (*OnWriteLog)(unsigned char* buf, long len, long iThreadId);
//typedef pthread_t (*CreateThreadCallback)(const char *name, void(*start)(void *),
//		void *arg);
typedef struct _JniCallback{
	OnSuccess onSuccess;
	OnError onError;
	OnReceiveData onReceiveData;
	OnWriteLog onWriteLog;
}JNICALLBACK;
#endif
