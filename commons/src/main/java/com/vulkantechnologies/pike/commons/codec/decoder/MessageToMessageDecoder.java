package com.vulkantechnologies.pike.commons.codec.decoder;

public interface MessageToMessageDecoder<T, V> {

    V decode(T in);

}
