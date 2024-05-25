package com.vulkantechnologies.pike.commons.network;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.UUID;

import com.vulkantechnologies.pike.commons.network.channel.pipeline.ChannelPipeline;
import com.vulkantechnologies.pike.commons.network.channel.pipeline.DefaultChannelPipeline;
import com.vulkantechnologies.pike.commons.packet.Packet;
import com.vulkantechnologies.pike.commons.protocol.ProtocolVersion;
import com.vulkantechnologies.pike.commons.utils.Check;

public abstract class AbstractNetworkConnection<P extends Packet> implements NetworkConnection<P> {


    protected final UUID uniqueId;
    protected final SocketChannel channel;
    protected final SocketAddress remoteAddress;
    protected ProtocolVersion protocolVersion;
    protected volatile boolean online;
    protected volatile ConnectionState connectionState;
    protected ChannelPipeline pipeline;

    // Compression
    private boolean compressionEnabled;

    // Encryption
    private boolean encrypted;

    public AbstractNetworkConnection(UUID uniqueId, SocketChannel channel) {
        this(uniqueId, channel, new DefaultChannelPipeline(channel));
    }

    public AbstractNetworkConnection(UUID uniqueId, SocketChannel channel, ChannelPipeline pipeline) {
        Check.notNull(uniqueId, "uniqueId");
        Check.notNull(channel, "channel");

        this.uniqueId = uniqueId;
        this.channel = channel;
        this.pipeline = pipeline;
        this.remoteAddress = channel.socket().getRemoteSocketAddress();
        this.online = true;
        this.connectionState = ConnectionState.HANDSHAKE;
    }

    @Override
    public UUID uniqueId() {
        return this.uniqueId;
    }

    @Override
    public void enableCompression() {
        this.compressionEnabled = true;
    }

    @Override
    public void disableCompression() {
        this.compressionEnabled = false;
    }

    @Override
    public boolean isCompressionEnabled() {
        return this.compressionEnabled;
    }

    @Override
    public void enableEncryption() {
        this.encrypted = true;
    }

    @Override
    public void disableEncryption() {
        this.encrypted = false;
    }

    @Override
    public boolean isEncrypted() {
        return this.encrypted;
    }

    @Override
    public void disconnect() {
        try {
            this.channel.close();
        } catch (IOException ignored) {
        }
        this.online = false;
    }

    @Override
    public boolean connected() {
        return this.online;
    }

    @Override
    public ConnectionState connectionState() {
        return this.connectionState;
    }

    @Override
    public void connectionState(ConnectionState state) {
        this.connectionState = state;
    }

    @Override
    public ProtocolVersion protocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public SocketAddress remoteAddress() {
        return this.remoteAddress;
    }

    @Override
    public ChannelPipeline pipeline() {
        return pipeline;
    }
}
