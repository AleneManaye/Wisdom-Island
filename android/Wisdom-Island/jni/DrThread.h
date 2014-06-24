/*
 * File         : DrThread.h
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrPalm DrThread include
 */

#ifndef _INC_DRTHREAD_
#define _INC_DRTHREAD_
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include "DrMutex.h"
class DrThread
{
	public:
		DrThread();
		virtual ~DrThread();
		long start();
		void stop();
		void sleep(uint32_t msec);
		bool isRunning();
		void setRunning(bool isRunning);
		pthread_t getThreadId();
		pthread_t getSelfThreadId();
		DrMutex m_DrMutex;
	protected:
		virtual void onRun() = 0;

	private:
		bool m_isRunning;
		pthread_t m_pthread_t;
		static void *thread_proc_func(void *args);


};
#endif
