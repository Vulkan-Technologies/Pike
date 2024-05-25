package com.vulkantechnologies.pike.server.client;

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
import com.vulkantechnologies.pike.commons.packet.ClientboundPacket;
import com.vulkantechnologies.pike.server.Worker;

public class ClientConnection extends AbstractNetworkConnection<ClientboundPacket> {

    private final Worker worker;

    public ClientConnection(UUID uniqueId, SocketChannel channel, Worker worker, ChannelPipeline pipeline) {
        super(uniqueId, channel, pipeline);
        this.worker = worker;
    }

    @Override
    public void sendPacket(ClientboundPacket packet) {
        LinkedList<ChannelHandler> handlers = pipeline.handlers();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        for (ChannelHandler handler : handlers) {
            if (handler instanceof PacketToByteEncoder<?> packetToByteEncoder) {
                ((PacketToByteEncoder<ClientboundPacket>) packetToByteEncoder).encode(buffer, packet);
            } else if (handler instanceof ByteToByteEncoder byteToByteEncoder) {
                buffer = byteToByteEncoder.encode(buffer);
            }
        }
        try {
            this.channel.write(buffer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send packet", e);
        }
    }

    @Override
    public void disconnect() {
        super.disconnect();
        this.worker.disconnect(this, this.channel);
    }
}
