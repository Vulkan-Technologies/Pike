package com.vulkantechnologies.pike.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NonBlocking;

import com.vulkantechnologies.pike.commons.utils.Check;

import lombok.Getter;

public class PikeServer {

    private final List<Worker> workers;
    private final InetSocketAddress address;
    @Getter
    private final ServerOptions options;
    private Selector selector;
    private ServerSocketChannel server;
    private int workerIndex;

    // Server state
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);

    public PikeServer(ServerOptions options) {
        this.options = options;
        this.address = InetSocketAddress.createUnresolved(options.host(), options.port());

        // Workers
        Worker[] workers = new Worker[this.options.workers()];
        Arrays.setAll(workers, value -> new Worker(this));
        this.workers = List.of(workers);
    }

    public void init() {
        Check.stateCondition(initialized.get(), "Server already initialized");
        Check.stateCondition(started.get(), "Server already started");

        try {
            this.selector = Selector.open();
            this.server = ServerSocketChannel.open();
            this.server.bind(address);
            this.server.configureBlocking(false);
            this.server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize server", e);
        }


        this.initialized.set(true);
    }

    public void start() {
        Check.stateCondition(!initialized.get(), "Server not initialized");
        Check.stateCondition(started.get(), "Server already started");
        this.started.set(true);

        // Workers
        this.workers.forEach(Thread::start);

        // Main loop
        new Thread(() -> {
            while (this.started.get()) {
                try {
                    this.selector.select(key -> {
                        if (!key.isAcceptable())
                            return;

                        try {
                            final Worker worker = findWorker();
                            final SocketChannel channel = this.server.accept();

                            worker.acceptConnection(channel);
                            System.out.printf("Accepted connection from %s%n", channel.getRemoteAddress());
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to accept connection", e);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException("Failed to accept connection", e);
                }
            }
            System.out.println("Server stopped");
        }, "pike-entrypoint").start();
    }

    public @Blocking void blockingLoop(Runnable runnable) {
        while (this.started.get()) {
            this.workers.forEach(Worker::tick);
            runnable.run();

            try {
                Thread.sleep(1_000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public @NonBlocking void nonBlockingLoop() {
        new Thread(() -> {
            while (this.started.get()) {
                this.workers.forEach(Worker::tick);

                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException ignored) {
                }
            }
        }, "pike-loop").start();
    }

    public void stop() {
        Check.stateCondition(initialized.get(), "Server not initialized");
        Check.stateCondition(started.compareAndSet(true, false), "Server not started");

        try {
            if (this.server != null)
                this.server.close();
            this.selector.wakeup();
            this.selector.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to stop server", e);
        }
        this.workers.forEach(Worker::close);
        this.started.set(false);
        this.initialized.set(false);
    }

    public boolean running() {
        return this.started.get();
    }

    private Worker findWorker() {
        this.workerIndex = ++workerIndex % this.options.workers();
        return workers.get(workerIndex);
    }

    public static void main(String[] args) throws IOException {
        PikeServer server = new PikeServer(ServerOptions.builder()
                .timeout(30_000)
                .tcpNoDelay(true)
                .receiveBufferSize(32_767)
                .sendBufferSize(262_143)
                .host("localhost")
                .port(25565)
                .build());
        server.init();
        server.start();
        server.blockingLoop(() -> {
        });
        server.stop();
    }
}
