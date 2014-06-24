MAIN_LOCAL_PATH := $(call my-dir)
include $(call all-subdir-makefiles)

#include $(LOCAL_PATH)/json/Android.mk

include $(CLEAR_VARS)
#$(call import-module,DrJson)
LOCAL_PATH := $(MAIN_LOCAL_PATH)
LOCAL_MODULE    := DrService
LOCAL_SRC_FILES := com_drcom_drpalm_Tool_service_DrServiceJni.cpp Arithmetic.cpp \
 DrCOMSocket.cpp DrThread.cpp DrHttpClient.cpp DrUrlConnection.cpp
LOCAL_LDLIBS 	:= -lssl -lcrypto -L$(SYSROOT)/usr/lib -llog
#LOCAL_CPPFLAGS += -std=c++0x
LOCAL_STATIC_LIBRARIES := DrJson polarssl
include $(BUILD_SHARED_LIBRARY) 

#include $(CLEAR_VARS)
##$(call import-module,DrJson)
#LOCAL_PATH := $(MAIN_LOCAL_PATH)
#LOCAL_MODULE    := DrPushService
#LOCAL_SRC_FILES := com_drcom_drpalm_Tool_service_DrPushServiceJni.cpp Arithmetic.cpp \
# DrCOMSocket.cpp DrThread.cpp DrHttpClient.cpp DrUrlConnection.cpp
#LOCAL_LDLIBS 	:= -lssl -lcrypto -L$(SYSROOT)/usr/lib -llog
##LOCAL_CPPFLAGS += -std=c++0x
#LOCAL_CFLAGS := -g
#LOCAL_STRIP_MODULE := false
#LOCAL_STATIC_LIBRARIES := DrJson
#include $(BUILD_SHARED_LIBRARY) 

#include $(CLEAR_VARS)
#LOCAL_MODULE    := DrCOMWS
#LOCAL_SRC_FILES := com_drcom_drpalm_DrCOMWS_Jni.cpp DrCOMSocket.cpp DrCOMAuth.cpp Arithmetic.cpp
#LOCAL_LDLIBS 	:= -lssl -lcrypto -L$(SYSROOT)/usr/lib -llog
#include $(BUILD_SHARED_LIBRARY) 