package com.vulkantechnologies.pike.commons.network.channel.handler.decoder;

import java.nio.ByteBuffer;

import com.vulkantechnologies.pike.commons.network.channel.ChannelHandler;

public interface ByteToByteDecoder extends ChannelHandler {

    void decode(ByteBuffer input, ByteBuffer output);
}
