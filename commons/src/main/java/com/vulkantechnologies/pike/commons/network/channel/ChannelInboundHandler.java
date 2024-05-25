package com.vulkantechnologies.pike.commons.network.channel;

import java.nio.channels.SocketChannel;

import com.vulkantechnologies.pike.commons.packet.Packet;

public interface ChannelInboundHandler extends ChannelHandler {

    void channelActive(SocketChannel channel);

    void channelInactive(SocketChannel channel);

    void channelRead(SocketChannel channel, Packet message);
}
