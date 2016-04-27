#include "OpusCoder.h"
#include <android/log.h>
#include <cstddef>
#include <stdlib.h>

#define SAMPLE_RATE 16000
#define BIT_RATE 16000
#define CHANNEL_NUM 1
#define BIT_PER_SAMPLE 16


OpusCoder::OpusCoder() {

	opus_int32 skip = 0;
	int err = 0;

	encoder = opus_encoder_create(SAMPLE_RATE, CHANNEL_NUM, OPUS_APPLICATION_VOIP, &err);
	if (err != OPUS_OK) {
		encoder = NULL;
	}
	opus_encoder_ctl(encoder, OPUS_SET_BANDWIDTH(OPUS_BANDWIDTH_WIDEBAND));
	opus_encoder_ctl(encoder, OPUS_SET_BITRATE(BIT_RATE));
	opus_encoder_ctl(encoder, OPUS_SET_VBR(1));
	opus_encoder_ctl(encoder, OPUS_SET_COMPLEXITY(10));
	opus_encoder_ctl(encoder, OPUS_SET_INBAND_FEC(0));
	opus_encoder_ctl(encoder, OPUS_SET_FORCE_CHANNELS(OPUS_AUTO));
	opus_encoder_ctl(encoder, OPUS_SET_DTX(0));
	opus_encoder_ctl(encoder, OPUS_SET_PACKET_LOSS_PERC(0));
	opus_encoder_ctl(encoder, OPUS_GET_LOOKAHEAD(&skip));
	opus_encoder_ctl(encoder, OPUS_SET_LSB_DEPTH(16));

	decoder = opus_decoder_create(SAMPLE_RATE, CHANNEL_NUM, &err);
	if (err != OPUS_OK) {
		decoder = NULL;
	}
}

OpusCoder::~OpusCoder() {
	opus_encoder_destroy(encoder);
	opus_decoder_destroy(decoder);
}

int OpusCoder::opusEncode(void *src, void *out, int length) {

	opus_int16 *frame = (opus_int16 *) src;
	unsigned char *output = (unsigned char *)out;

	int nbytes = opus_encode(encoder, frame, length / 2, output, 200);
	 __android_log_print(ANDROID_LOG_INFO, "opus", "opusEncode %d", nbytes);
//	opusDecoder(output, nbytes);

	 return nbytes;
}

int OpusCoder::opusDecoder(void *src, void *out, int length) {

//	opus_int16 *output = (opus_int16 *)malloc(sizeof(opus_int16) * 1000);
	opus_int16 *output = (opus_int16 *)out;
	unsigned char *frame = (unsigned char *)src;

	int nbytes = opus_decode(decoder, frame, length, output, 320, 0);
	 __android_log_print(ANDROID_LOG_INFO, "opus", "opusEncode %d", nbytes);
	 return nbytes * sizeof(opus_int16);
//	free(output);
}

