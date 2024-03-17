package com.vulkantechnologies.pike.commons.codec.impl.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.vulkantechnologies.pike.commons.codec.decoder.ByteToMessageDecoder;
import com.vulkantechnologies.pike.commons.packet.wrapped.InboundPacket;
import com.vulkantechnologies.pike.commons.utils.binary.BinaryReader;

public class PacketDecoder implements ByteToMessageDecoder<InboundPacket> {
    @Override
    public InboundPacket decode(ByteBuffer in) {
        if (in.remaining() <= 0)
            throw new IllegalArgumentException("Input buffer is empty");
        try (BinaryReader reader = new BinaryReader(in)) {
            int id = reader.readVarInt();
            return new InboundPacket(id, in);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading from buffer", e);
        }
    }
}
