package com.vulkantechnologies.pike.commons.codec.impl.packet;

import java.nio.ByteBuffer;

import com.vulkantechnologies.pike.commons.codec.encoder.MessageToByteEncoder;
import com.vulkantechnologies.pike.commons.packet.Packet;
import com.vulkantechnologies.pike.commons.protocol.PacketRegistry;
import com.vulkantechnologies.pike.commons.utils.binary.BinaryWriter;

public class PacketEncoder implements MessageToByteEncoder<Packet> {
    @Override
    public void encode(Packet message, ByteBuffer out) throws Exception {
        int id = PacketRegistry.getId(message);

        try (BinaryWriter writer = new BinaryWriter(out)) {
            writer.writeVarInt(id);
            writer.write(message);
        }
    }
}
