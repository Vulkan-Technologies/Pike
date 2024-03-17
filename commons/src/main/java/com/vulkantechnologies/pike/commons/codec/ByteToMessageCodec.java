package com.vulkantechnologies.pike.commons.codec;

import com.vulkantechnologies.pike.commons.codec.decoder.ByteToMessageDecoder;
import com.vulkantechnologies.pike.commons.codec.encoder.MessageToByteEncoder;

public interface ByteToMessageCodec<T> extends MessageToByteEncoder<T>, ByteToMessageDecoder<T> {
}
