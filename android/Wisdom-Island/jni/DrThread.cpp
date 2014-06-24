/*
 * File         : DrThread.cpp
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrPalm DrThread src
 */
#include "DrThread.h"
#include <time.h>
#include "DrLog.h"
DrThread::DrThread() {
	m_pthread_t = -1;
	m_isRunning = false;
}
DrThread::~DrThread() {
	stop();
}
void DrThread::setRunning(bool isRunning){
	m_isRunning = isRunning;
	showLog("Jni.DrThread.setRunning", "threadid:%ld,isRunning:%ld", m_pthread_t, m_isRunning);
}
void* DrThread::thread_proc_func(void *args){

	DrThread *pDrThread = (DrThread*)args;
	if(pDrThread){
		showLog("Jni.DrThread.thread_proc_func", "pDrThread->onRun");
		pDrThread->onRun();
		pDrThread->m_DrMutex.lock();
		pDrThread->setRunning(false);
		pDrThread->m_DrMutex.unlock();
	}
	showLog("Jni.DrThread.thread_proc_func", "thread exit succeed");
	return (void*)0;
}
long DrThread::start(){
	m_DrMutex.lock();
	long ret = pthread_create(&m_pthread_t, NULL, &thread_proc_func, (void*)this);
	if(0 != ret){
		showLog("Jni.DrThread.start", "start thread:%ld error!", strerror(ret));
		m_pthread_t = -1;
	}
	else{
		setRunning(true);
		showLog("Jni.DrThread.start", "start thread:%ld ret:%ld succeed!", m_pthread_t, ret);
	}
	m_DrMutex.unlock();
	return m_pthread_t;
}
void DrThread::stop(){
	m_DrMutex.lock();
	if(-1 != m_pthread_t){
		//pthread_cancel(&m_pthread_t);
		showLog("Jni.DrThread.stop", "wait for stop thread:%ld!", m_pthread_t);
		if(0 != pthread_join(m_pthread_t, NULL)){
			showLog("Jni.DrThread.stop", "wait for thread:%ld exit error!", m_pthread_t);
		}
		else{
			showLog("Jni.DrThread.stop", "stop thread:%ld suc!", m_pthread_t);
		}
		m_pthread_t = -1;
		setRunning(false);
	}
	m_DrMutex.unlock();
}
void DrThread::sleep(uint32_t msec){
	//usleep(100);
//	struct timespect st;
//	st.tv_sec = msec / 1000;
//	st.tv_nsec = (msec % 1000) * 1000 * 1000;
//	if(-1 == nanosleep(&st, NULL)){
//		showLog("Jni.DrThread.sleep", "thread sleep error!");
//	}
}
pthread_t DrThread::getThreadId(){
	return m_pthread_t;
}
pthread_t DrThread::getSelfThreadId() {
	return pthread_self();
}
bool DrThread::isRunning(){
	return m_isRunning;
}
