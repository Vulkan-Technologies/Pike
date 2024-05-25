package com.vulkantechnologies.pike.client.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.UUID;

import com.vulkantechnologies.pike.commons.network.AbstractNetworkConnection;
import com.vulkantechnologies.pike.commons.network.channel.ChannelHandler;
import com.vulkantechnologies.pike.commons.network.channel.handler.encoder.ByteToByteEncoder;
import com.vulkantechnologies.pike.commons.network.channel.handler.encoder.PacketToByteEncoder;
import com.vulkantechnologies.pike.commons.network.channel.pipeline.ChannelPipeline;
import com.vulkantechnologies.pike.commons.packet.ServerboundPacket;

public class ServerConnection extends AbstractNetworkConnection<ServerboundPacket> {


    public ServerConnection(UUID uniqueId, SocketChannel channel, ChannelPipeline pipeline) {
        super(uniqueId, channel, pipeline);
    }

    @Override
    public void sendPacket(ServerboundPacket packet) {
        LinkedList<ChannelHandler> handlers = pipeline.handlers();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        for (ChannelHandler handler : handlers) {
            if (handler instanceof PacketToByteEncoder<?>) {
                PacketToByteEncoder<ServerboundPacket> packetToByteEncoder = (PacketToByteEncoder<ServerboundPacket>) handler;
                packetToByteEncoder.encode(buffer, packet);
            } else if (handler instanceof ByteToByteEncoder) {
                ByteToByteEncoder byteToByteEncoder = (ByteToByteEncoder) handler;
                buffer = byteToByteEncoder.encode(buffer);
            }
        }
        try {
            buffer.flip();
            this.channel.write(buffer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send packet", e);
        }
    }
}
