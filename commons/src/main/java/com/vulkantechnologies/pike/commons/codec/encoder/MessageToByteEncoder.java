package com.vulkantechnologies.pike.commons.codec.encoder;

import java.nio.ByteBuffer;

public interface MessageToByteEncoder<T> {

    void encode(T message, ByteBuffer out) throws Exception;
}
