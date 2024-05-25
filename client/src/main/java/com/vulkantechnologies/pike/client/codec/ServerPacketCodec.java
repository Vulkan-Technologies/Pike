package com.vulkantechnologies.pike.client.codec;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.vulkantechnologies.pike.commons.network.channel.codec.PacketCodec;
import com.vulkantechnologies.pike.commons.packet.ClientboundPacket;
import com.vulkantechnologies.pike.commons.packet.Packet;
import com.vulkantechnologies.pike.commons.packet.ServerboundPacket;
import com.vulkantechnologies.pike.commons.protocol.PacketRegistry;
import com.vulkantechnologies.pike.commons.utils.binary.BinaryReader;
import com.vulkantechnologies.pike.commons.utils.binary.BinaryWriter;

public class ServerPacketCodec implements PacketCodec<ServerboundPacket, ClientboundPacket> {

    @Override
    public ClientboundPacket decode(ByteBuffer buffer) {
        if (buffer.remaining() <= 0)
            throw new IllegalArgumentException("Input buffer is empty");
        try (BinaryReader reader = new BinaryReader(buffer)) {
            int id = reader.readVarInt();
            Packet packet = PacketRegistry.get((short) id);
            if (packet == null)
                throw new IllegalArgumentException("Invalid packet id: " + id);
            if (!(packet instanceof ClientboundPacket))
                throw new IllegalArgumentException("Invalid packet type: " + packet.getClass().getSimpleName());
            packet.read(reader);
            return (ClientboundPacket) packet;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from buffer", e);
        }
    }

    @Override
    public void encode(ByteBuffer out, ServerboundPacket message) {
        short id = PacketRegistry.getId(message);

        try (BinaryWriter writer = new BinaryWriter(out)) {
            writer.writeVarInt(id);
            writer.write(message);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to buffer", e);
        }
    }

    @Override
    public void handlerAdded(SocketChannel channel) {

    }

    @Override
    public void handlerRemoved(SocketChannel channel) {

    }

    @Override
    public void exceptionCaught(SocketChannel channel, Throwable cause) {

    }
}
