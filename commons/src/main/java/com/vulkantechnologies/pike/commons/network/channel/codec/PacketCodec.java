package com.vulkantechnologies.pike.commons.network.channel.codec;

import com.vulkantechnologies.pike.commons.network.channel.handler.decoder.ByteToPacketDecoder;
import com.vulkantechnologies.pike.commons.network.channel.handler.encoder.PacketToByteEncoder;
import com.vulkantechnologies.pike.commons.packet.Packet;

public interface PacketCodec<P1 extends Packet, P2 extends Packet> extends PacketToByteEncoder<P1>, ByteToPacketDecoder<P2> {
}
