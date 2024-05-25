package com.vulkantechnologies.pike.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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
            this.connection = this.connectionInitializer.initialize(clientChannel);

            this.connection.pipeline().fireChannelActive();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open client channel", e);
        }

        this.initialized.set(true);
    }

    public void start() {
        Check.stateCondition(!initialized.get(), "Client not initialized");
        Check.stateCondition(started.get(), "Client already started");

        this.started.set(true);

        // Listen for packets
        new Thread(() -> {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (started.get()) {
                if (!clientChannel.isOpen() || !clientChannel.isConnected())
                    return;

                buffer.clear();
                try {
                    int read = clientChannel.read(buffer);
                    if (read == -1) {
                        connection.disconnect();
                        return;
                    } else if (read == 0) {
                        continue;
                    }

                    // Prepare buffer for reading
                    buffer.flip();

                    // Pass data through pipeline
                    connection.pipeline().fireChannelRead(buffer);
                } catch (IOException e) {
                    this.connection.pipeline().fireExceptionCaught(e);
                    return;
                }
            }
        }, "pike-client").start();

        // Send Packet
        this.connection.sendPacket(new ServerPacket1("Hello, server!"));

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
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        client.stop();
    }
}
