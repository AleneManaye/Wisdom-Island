/*
 * File         : DrMutex.h
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrPalm DrMutex include
 */

#ifndef _INC_DRMUTEX_
#define _INC_DRMUTEX_
#include <pthread.h>
#include "DrLog.h"
using namespace std;
class DrMutex
{
	public:
	DrMutex(){
		initlock();
	}
	~DrMutex(){
		desrtoylock();
	}
	long trylock(){
		return pthread_mutex_trylock(&m_Mutex);
	}
	long lock(const char* ptag = "", long iThreadId = -1){
		showLog("Jni.DrMutex.lock", "%ld, tag=%s, thread=%ld", &m_Mutex, (ptag == NULL) ? "" : ptag, iThreadId);
		return pthread_mutex_lock(&m_Mutex);
	}
	long unlock(const char* ptag = "", long iThreadId = -1){
		showLog("Jni.DrMutex.unlock", "%ld, tag=%s, thread=%ld", &m_Mutex, (ptag == NULL) ? "" : ptag, iThreadId);
		return pthread_mutex_unlock(&m_Mutex);
	}
	protected:
	long initlock(){
		//m_Mutex = PTHREAD_MUTEX_INITIALIZER;
		showLog("Jni.DrMutex.initlock", "pthread_mutex_init");
		return pthread_mutex_init(&m_Mutex, NULL);
	}
	long desrtoylock(){
		return pthread_mutex_destroy(&m_Mutex);
	}
	private:
	pthread_mutex_t m_Mutex;
	pthread_mutexattr_t m_Mutexattr;
};
#endif
