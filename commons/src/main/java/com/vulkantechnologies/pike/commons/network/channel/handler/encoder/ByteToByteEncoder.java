package com.vulkantechnologies.pike.commons.network.channel.handler.encoder;

import java.nio.ByteBuffer;

import com.vulkantechnologies.pike.commons.network.channel.ChannelHandler;

public interface ByteToByteEncoder extends ChannelHandler {

    ByteBuffer encode(ByteBuffer in);
}
