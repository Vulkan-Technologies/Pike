package com.vulkantechnologies.pike.server.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.UUID;

import com.vulkantechnologies.pike.commons.network.ConnectionInitializer;
import com.vulkantechnologies.pike.commons.network.channel.pipeline.ChannelPipeline;
import com.vulkantechnologies.pike.commons.network.channel.pipeline.DefaultChannelPipeline;
import com.vulkantechnologies.pike.commons.packet.ClientboundPacket;
import com.vulkantechnologies.pike.server.ChannelInboundHandler;
import com.vulkantechnologies.pike.server.Worker;
import com.vulkantechnologies.pike.server.codec.ClientPacketCodec;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientConnectionInitializer implements ConnectionInitializer<ClientboundPacket, ClientConnection> {

    private final Worker worker;

    @Override
    public ClientConnection initialize(SocketChannel channel) throws IOException {
        UUID uniqueId = UUID.randomUUID();

        // Configure channel
        channel.configureBlocking(false);
        channel.register(worker.selector(), SelectionKey.OP_READ);

        // Set socket options
        if (channel.getLocalAddress() instanceof InetSocketAddress) {
            Socket socket = channel.socket();
            socket.setSendBufferSize(262_143);
            socket.setReceiveBufferSize(32_767);
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(30_000);
        }

        // Create pipeline
        ChannelPipeline pipeline = new DefaultChannelPipeline(channel);
        pipeline.addFirst("packet_codec", new ClientPacketCodec())
                .addLast("inbound", new ChannelInboundHandler());

        // Create connection
        return new ClientConnection(uniqueId, channel, worker, pipeline);
    }
}
