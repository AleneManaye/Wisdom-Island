# Copyright (C) 2014 The DrSniffer Project
# polarssl Module Makefile
#
# Created on: 2014年2月14日
# Author: Kingsley Yau
# Email: Kingsleyyau@gmail.com
#

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

define all-c-files-under
$(wildcard $1/*.c)
endef

LOCAL_MODULE := polarssl

LOCAL_MODULE_FILENAME := libpolarssl

REAL_PATH := $(LOCAL_PATH)#$(realpath $(LOCAL_PATH))
LOCAL_SRC_FILES := $(notdir $(call all-c-files-under, $(REAL_PATH)))

include $(BUILD_STATIC_LIBRARY) 
