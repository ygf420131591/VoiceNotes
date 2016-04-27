LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/include/
LOCAL_MODULE     := libOpus
LOCAL_SRC_FILES  := OpusCoder.cpp OpusCoderJNI.cpp
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -lopus -llog -ldl 
LOCAL_LDFLAGS += -L$(LOCAL_PATH)/../../lib

include $(BUILD_SHARED_LIBRARY)