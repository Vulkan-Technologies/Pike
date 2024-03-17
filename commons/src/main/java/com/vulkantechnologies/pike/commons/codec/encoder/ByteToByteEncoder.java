package com.vulkantechnologies.pike.commons.codec.encoder;

import java.nio.ByteBuffer;

public interface ByteToByteEncoder {

    void encode(ByteBuffer in, ByteBuffer out);
}
