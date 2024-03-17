package com.vulkantechnologies.pike.commons.codec.decoder;

import java.nio.ByteBuffer;

public interface ByteToMessageDecoder<T> {

    T decode(ByteBuffer in);

}
