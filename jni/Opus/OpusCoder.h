#include "opus.h"
#include "opus_types.h"
#include "opus_multistream.h"

class OpusCoder {

public :
	OpusCoder();
	~OpusCoder();

	int opusEncode(void *src, void *out, int length);
	int opusDecoder(void *src, void *out, int lenght);
private :
	OpusEncoder *encoder;
	OpusDecoder *decoder;

};
