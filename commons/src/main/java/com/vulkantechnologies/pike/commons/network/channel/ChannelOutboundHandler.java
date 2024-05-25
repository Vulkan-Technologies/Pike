package com.vulkantechnologies.pike.commons.network.channel;

import java.nio.ByteBuffer;

public interface ChannelOutboundHandler extends ChannelHandler {

    void write(ByteBuffer byteBuffer) throws Exception;

}
