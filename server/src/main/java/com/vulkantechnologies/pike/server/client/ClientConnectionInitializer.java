package com.vulkantechnologies.pike.server.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.UUID;

import com.vulkantechnologies.pike.commons.network.ConnectionInitializer;
import com.vulkantechnologies.pike.commons.packet.ClientboundPacket;
import com.vulkantechnologies.pike.server.ChannelInboundHandler;
import com.vulkantechnologies.pike.server.PikeServer;
import com.vulkantechnologies.pike.server.ServerOptions;
import com.vulkantechnologies.pike.server.Worker;
import com.vulkantechnologies.pike.server.codec.ClientPacketCodec;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientConnectionInitializer implements ConnectionInitializer<ClientboundPacket, ClientConnection> {

    private final PikeServer server;
    private final Worker worker;

    @Override
    public ClientConnection initialize(SocketChannel channel) throws IOException {
        final ServerOptions options = server.options();
        final UUID uniqueId = UUID.randomUUID();


        // Configure channel
        channel.configureBlocking(false);
        channel.register(worker.selector(), SelectionKey.OP_READ);

        // Set socket options
        if (channel.getLocalAddress() instanceof InetSocketAddress) {
            Socket socket = channel.socket();
            socket.setSendBufferSize(options.sendBufferSize());
            socket.setReceiveBufferSize(options.receiveBufferSize());
            socket.setTcpNoDelay(options.tcpNoDelay());
            socket.setSoTimeout(options.timeout());
        }

        // Create connection
        ClientConnection connection = new ClientConnection(uniqueId, channel, worker);

        // Setup pipeline
        connection.pipeline()
                .addFirst("packet_codec", new ClientPacketCodec())
                .addLast("inbound", new ChannelInboundHandler(connection));

        return connection;
    }
}
