#include <jni.h>
#include <OpusCoder.h>
#include "android/log.h"

OpusCoder *opusCoder;
#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT jint JNICALL Java_com_voicenotes_manages_voiceengine_VoiceEngine_opusEncodeJNI(JNIEnv * env , jobject jobj, jbyteArray src, jbyteArray out, jint length)
{
	if (opusCoder == 0) {
		opusCoder = new OpusCoder();
	}
	jbyte *srcByte = env->GetByteArrayElements(src, 0);
	jbyte *outByte = env->GetByteArrayElements(out, 0);

	jint result = opusCoder->opusEncode((unsigned char *)srcByte, (unsigned char *)outByte, length);

	env->SetByteArrayRegion(out, 0, result, outByte);

	env->ReleaseByteArrayElements(src, srcByte, 0);
	env->ReleaseByteArrayElements(out, outByte, 0);
	return result;
}

JNIEXPORT jint JNICALL Java_com_voicenotes_manages_voiceengine_VoiceEngine_opusDecodeJNI(JNIEnv * env , jobject jobj, jbyteArray src, jbyteArray out, jint length)
{
	if (opusCoder == 0) {
		opusCoder = new OpusCoder();
	}
	jbyte *srcByte = env->GetByteArrayElements(src, 0);
	jbyte *outByte = env->GetByteArrayElements(out, 0);

	jint result = opusCoder->opusDecoder((unsigned char *)srcByte, (unsigned char *)outByte, length);
	env->SetByteArrayRegion(out, 0, result, outByte);

	env->ReleaseByteArrayElements(out, outByte, 0);
	env->ReleaseByteArrayElements(src, srcByte, 0);
	return result;
}


#ifdef __cplusplus
}
#endif
