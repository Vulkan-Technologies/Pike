package com.vulkantechnologies.pike.commons.codec.encoder;

public interface MessageToMessageEncoder<T, V> {

    void encode(T in, V out);
}
