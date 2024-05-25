package com.vulkantechnologies.pike.client.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

import com.vulkantechnologies.pike.client.ChannelInboundHandler;
import com.vulkantechnologies.pike.client.codec.ServerPacketCodec;
import com.vulkantechnologies.pike.commons.network.ConnectionInitializer;
import com.vulkantechnologies.pike.commons.packet.ServerboundPacket;

public class ServerConnectionInitializer implements ConnectionInitializer<ServerboundPacket, ServerConnection> {

    @Override
    public ServerConnection initialize(SocketChannel channel) throws IOException {
        UUID uniqueId = UUID.randomUUID();

        // Create connection
        ServerConnection connection = new ServerConnection(uniqueId, channel);

        // Pipeline
        connection.pipeline()
                .addFirst("packet_codec", new ServerPacketCodec())
                .addLast("handler", new ChannelInboundHandler());

        return connection;
    }
}
