package com.vulkantechnologies.pike.commons.network.channel.handler.decoder;

import java.nio.ByteBuffer;

import com.vulkantechnologies.pike.commons.network.channel.ChannelHandler;
import com.vulkantechnologies.pike.commons.packet.Packet;

public interface ByteToPacketDecoder<P extends Packet> extends ChannelHandler {

    P decode(ByteBuffer buffer);
}
