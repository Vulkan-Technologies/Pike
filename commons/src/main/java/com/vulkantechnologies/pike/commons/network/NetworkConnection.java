package com.vulkantechnologies.pike.commons.network;

import java.net.SocketAddress;
import java.util.UUID;

import com.vulkantechnologies.pike.commons.network.channel.pipeline.ChannelPipeline;
import com.vulkantechnologies.pike.commons.packet.Packet;
import com.vulkantechnologies.pike.commons.protocol.ProtocolVersion;

public interface NetworkConnection<P extends Packet> {

    UUID uniqueId();

    // Compression
    void enableCompression();

    void disableCompression();

    boolean isCompressionEnabled();

    // Encryption
    void enableEncryption();

    void disableEncryption();

    boolean isEncrypted();

    // Packet handling
    void sendPacket(P packet);

    // Pipeline
    ChannelPipeline pipeline();

    // Connection
    void disconnect();

    boolean connected();

    ConnectionState connectionState();

    void connectionState(ConnectionState state);

    ProtocolVersion protocolVersion();

    SocketAddress remoteAddress();

}
