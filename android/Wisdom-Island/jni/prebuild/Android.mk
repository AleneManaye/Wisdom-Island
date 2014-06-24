# Copyright (C) 2014 The DrSniffer Project
# other library Module Makefile
#
# Created on: 2014年2月14日
# Author: Kingsley Yau
# Email: Kingsleyyau@gmail.com
#

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := DrPushService
LOCAL_SRC_FILES := libDrPushService.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := iomx-gingerbread
LOCAL_SRC_FILES := libiomx-gingerbread.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := libiomx-hc
LOCAL_SRC_FILES := libiomx-hc.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := libiomx-ics
LOCAL_SRC_FILES := libiomx-ics.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := libvlcjni
LOCAL_SRC_FILES := libvlcjni.so
include $(PREBUILT_SHARED_LIBRARY)