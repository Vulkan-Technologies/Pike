package com.vulkantechnologies.pike.commons.network.channel.pipeline;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import org.jetbrains.annotations.Nullable;

import com.vulkantechnologies.pike.commons.network.channel.ChannelHandler;

public interface ChannelPipeline {

    ChannelPipeline addFirst(String name, ChannelHandler handler);

    ChannelPipeline addLast(String name, ChannelHandler handler);

    ChannelPipeline addBefore(String baseName, String name, ChannelHandler handler);

    ChannelPipeline addAfter(String baseName, String name, ChannelHandler handler);

    ChannelPipeline remove(String name);

    ChannelPipeline removeLast();

    ChannelPipeline removeFirst();

    ChannelPipeline replace(String oldName, String newName, ChannelHandler handler);

    @Nullable
    ChannelHandler get(String name);

    @Nullable
    ChannelHandler first();

    @Nullable
    ChannelHandler last();

    SocketChannel channel();

    void fireChannelRead(ByteBuffer buffer);

    void fireChannelInactive();

    void fireChannelActive();

    void fireExceptionCaught(Throwable cause);

    LinkedList<ChannelHandler> handlers();
}
