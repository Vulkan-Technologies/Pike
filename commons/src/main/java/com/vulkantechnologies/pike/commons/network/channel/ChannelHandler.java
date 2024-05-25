package com.vulkantechnologies.pike.commons.network.channel;

import java.nio.channels.SocketChannel;

public interface ChannelHandler {

    void handlerAdded(SocketChannel channel);

    void handlerRemoved(SocketChannel channel);

    void exceptionCaught(SocketChannel channel, Throwable cause);
}
