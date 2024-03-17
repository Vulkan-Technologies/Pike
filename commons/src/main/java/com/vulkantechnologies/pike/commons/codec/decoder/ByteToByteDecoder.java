package com.vulkantechnologies.pike.commons.codec.decoder;

import java.nio.ByteBuffer;

public interface ByteToByteDecoder {

    void decode(ByteBuffer in, ByteBuffer out);
}
