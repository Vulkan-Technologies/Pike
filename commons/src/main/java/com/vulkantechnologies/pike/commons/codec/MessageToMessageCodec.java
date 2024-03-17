package com.vulkantechnologies.pike.commons.codec;

import com.vulkantechnologies.pike.commons.codec.decoder.MessageToMessageDecoder;
import com.vulkantechnologies.pike.commons.codec.encoder.MessageToMessageEncoder;

public interface MessageToMessageCodec<T, V> extends MessageToMessageEncoder<T, V>, MessageToMessageDecoder<V, T> {
}
