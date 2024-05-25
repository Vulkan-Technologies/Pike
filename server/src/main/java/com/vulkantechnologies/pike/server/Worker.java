package com.vulkantechnologies.pike.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.vulkantechnologies.pike.commons.utils.Check;
import com.vulkantechnologies.pike.server.client.ClientConnection;
import com.vulkantechnologies.pike.server.client.ClientConnectionInitializer;

import lombok.Getter;

public class Worker extends Thread {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    @Getter
    private final Selector selector;
    private final PikeServer server;
    private final ClientConnectionInitializer connectionInitializer;
    private final Map<SocketChannel, ClientConnection> connections = new ConcurrentHashMap<>();

    public Worker(PikeServer server) {
        super("pike-worker-%d".formatted(COUNTER.getAndIncrement()));
        this.server = server;
        this.connectionInitializer = new ClientConnectionInitializer(this);

        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create worker selector", e);
        }
    }

    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (this.server.running()) {
            try {
                selector.select(key -> {
                    final SocketChannel channel = (SocketChannel) key.channel();
                    if (!channel.isOpen() || !key.isReadable())
                        return;

                    final ClientConnection connection = this.connections.get(channel);
                    if (connection == null) {
                        try {
                            channel.close();
                        } catch (IOException ignored) {
                        }
                        return;
                    }

                    // Read data
                    buffer.clear();
                    try {
                        int read = channel.read(buffer);
                        if (read == -1) {
                            connection.disconnect();
                            return;
                        }

                        // Prepare buffer for reading
                        buffer.flip();

                        // Pass data through pipeline
                        connection.pipeline().fireChannelRead(buffer);
                    } catch (IOException e) {
                        connection.disconnect();
                    }

                });
            } catch (IOException e) {
                throw new RuntimeException("Failed to select keys", e);
            }
        }
        System.out.println("Worker stopped");
    }

    public void tick() {
        this.selector.wakeup();
    }

    public void disconnect(ClientConnection connection, SocketChannel channel) {
        assert !connection.connected();
        assert Thread.currentThread() == this;

        this.connections.remove(channel);
        connection.pipeline().fireChannelInactive();

        // Close the channel
        if (channel.isOpen()) {
            try {
                channel.close();
            } catch (IOException ignored) {
            }
        }
    }

    public void acceptConnection(SocketChannel channel) throws IOException {
        ClientConnection connection = this.connectionInitializer.initialize(channel);

        Check.notNull(connection, "connection");
        this.connections.put(channel, connection);

        connection.pipeline().fireChannelActive();
    }

    public void close() {
        this.selector.wakeup();
        try {
            this.selector.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close worker selector", e);
        }
    }
}
