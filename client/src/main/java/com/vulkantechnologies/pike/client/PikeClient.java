package com.vulkantechnologies.pike.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vulkantechnologies.pike.client.connection.ServerConnection;
import com.vulkantechnologies.pike.client.connection.ServerConnectionInitializer;
import com.vulkantechnologies.pike.commons.ServerPacket1;
import com.vulkantechnologies.pike.commons.network.ConnectionInitializer;
import com.vulkantechnologies.pike.commons.packet.ServerboundPacket;
import com.vulkantechnologies.pike.commons.utils.Check;

import lombok.Getter;

public class PikeClient {

    private final InetSocketAddress address;
    private SocketChannel clientChannel;
    @Getter
    private ServerConnection connection;
    private final ConnectionInitializer<ServerboundPacket, ServerConnection> connectionInitializer;

    // Client state
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);

    public PikeClient(InetSocketAddress address) {
        this(address, new ServerConnectionInitializer());
    }

    public PikeClient(InetSocketAddress address, ConnectionInitializer<ServerboundPacket, ServerConnection> connectionInitializer) {
        this.address = address;
        this.connectionInitializer = connectionInitializer;
    }

    public void init() {
        Check.stateCondition(initialized.get(), "Client already initialized");
        Check.stateCondition(started.get(), "Client already started");

        try {
            this.clientChannel = SocketChannel.open(this.address);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open client channel", e);
        }

        this.initialized.set(true);
    }

    public void start() {
        Check.stateCondition(!initialized.get(), "Client not initialized");
        Check.stateCondition(started.get(), "Client already started");

        // Send messages
        try {
            this.connection = this.connectionInitializer.initialize(clientChannel);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize connection", e);
        }
        this.connection.sendPacket(new ServerPacket1("Hello, server!"));

        this.started.set(true);
    }

    public void stop() {
        Check.stateCondition(!initialized.get(), "Client not initialized");
        Check.stateCondition(!started.get(), "Client not started");

        try {
            clientChannel.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close client channel", e);
        }

        this.started.set(false);
        this.initialized.set(false);
    }

    public static void main(String[] args) {
        PikeClient client = new PikeClient(new InetSocketAddress("localhost", 25565));
        client.init();
        client.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        client.stop();
    }
}
