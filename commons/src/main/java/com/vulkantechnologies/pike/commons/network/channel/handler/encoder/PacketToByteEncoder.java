package com.vulkantechnologies.pike.commons.network.channel.handler.encoder;

import java.nio.ByteBuffer;

import com.vulkantechnologies.pike.commons.network.channel.ChannelHandler;
import com.vulkantechnologies.pike.commons.packet.Packet;

public interface PacketToByteEncoder<P extends Packet> extends ChannelHandler {

    void encode(ByteBuffer out, P message);
}
