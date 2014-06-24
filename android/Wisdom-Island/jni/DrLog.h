/*
 * File         : DrLog.h
 * Date         : 2012-07-02
 * Author       : Kingsley Yau
 * Copyright    : City Hotspot Co., Ltd.
 * Description  : DrPalm DrLog include
 * PS:if you dont want to get log in file, only mark the macro definition(PRINT_JNI_LOG)
 */
#ifndef _INC_DRLOG_
#define _INC_DRLOG_

#define PRINT_JNI_LOG

#include <android/log.h>
#include <string.h>

#define showLog(tag, format, ...) __android_log_print(ANDROID_LOG_INFO, tag, format, ## __VA_ARGS__);
class DrLog{
public:

};
#endif
