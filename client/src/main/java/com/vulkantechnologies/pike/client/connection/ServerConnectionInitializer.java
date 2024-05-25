package com.vulkantechnologies.pike.client.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

import com.vulkantechnologies.pike.client.codec.ServerPacketCodec;
import com.vulkantechnologies.pike.commons.network.ConnectionInitializer;
import com.vulkantechnologies.pike.commons.network.channel.pipeline.ChannelPipeline;
import com.vulkantechnologies.pike.commons.network.channel.pipeline.DefaultChannelPipeline;
import com.vulkantechnologies.pike.commons.packet.ServerboundPacket;

public class ServerConnectionInitializer implements ConnectionInitializer<ServerboundPacket, ServerConnection> {

    @Override
    public ServerConnection initialize(SocketChannel channel) throws IOException {
        UUID uniqueId = UUID.randomUUID();

        // Pipeline
        ChannelPipeline pipeline = new DefaultChannelPipeline(channel);
        pipeline.addFirst("packet_codec", new ServerPacketCodec());


        return new ServerConnection(uniqueId, channel, pipeline);
    }
}
