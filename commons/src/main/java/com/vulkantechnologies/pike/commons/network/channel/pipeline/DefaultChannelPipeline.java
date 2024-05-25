package com.vulkantechnologies.pike.commons.network.channel.pipeline;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import org.jetbrains.annotations.Nullable;

import com.vulkantechnologies.pike.commons.network.channel.ChannelHandler;
import com.vulkantechnologies.pike.commons.network.channel.ChannelInboundHandler;
import com.vulkantechnologies.pike.commons.network.channel.handler.decoder.ByteToByteDecoder;
import com.vulkantechnologies.pike.commons.network.channel.handler.decoder.ByteToPacketDecoder;
import com.vulkantechnologies.pike.commons.packet.Packet;

import lombok.Data;

@Data
public class DefaultChannelPipeline implements ChannelPipeline {

    private final SocketChannel channel;
    private final LinkedList<WrapperChannelHandler> handlers = new LinkedList<>();

    @Override
    public ChannelPipeline addFirst(String name, ChannelHandler handler) {
        this.handlers.addFirst(new WrapperChannelHandler(name, handler));
        handler.handlerAdded(this.channel);
        return this;
    }

    @Override
    public ChannelPipeline addLast(String name, ChannelHandler handler) {
        this.handlers.addLast(new WrapperChannelHandler(name, handler));
        handler.handlerAdded(this.channel);
        return this;
    }

    @Override
    public ChannelPipeline addBefore(String baseName, String name, ChannelHandler handler) {
        int index = indexOf(baseName);
        if (index == -1)
            throw new IllegalArgumentException(String.format("No handler with name %s found", baseName));
        this.handlers.add(index, new WrapperChannelHandler(name, handler));
        handler.handlerAdded(this.channel);
        return this;
    }

    @Override
    public ChannelPipeline addAfter(String baseName, String name, ChannelHandler handler) {
        int index = indexOf(baseName);
        if (index == -1)
            throw new IllegalArgumentException(String.format("No handler with name %s found", baseName));
        this.handlers.add(index + 1, new WrapperChannelHandler(name, handler));
        handler.handlerAdded(this.channel);
        return this;
    }

    @Override
    public ChannelPipeline remove(String name) {
        int index = indexOf(name);
        if (index == -1)
            throw new IllegalArgumentException(String.format("No handler with name %s found", name));
        WrapperChannelHandler handler = this.handlers.remove(index);
        if (handler != null)
            handler.handler().handlerRemoved(this.channel);
        return this;
    }

    @Override
    public ChannelPipeline removeLast() {
        WrapperChannelHandler wrapperChannelHandler = this.handlers.removeLast();
        if (wrapperChannelHandler != null)
            wrapperChannelHandler.handler().handlerRemoved(this.channel);
        return this;
    }

    @Override
    public ChannelPipeline removeFirst() {
        WrapperChannelHandler wrapperChannelHandler = this.handlers.removeFirst();
        if (wrapperChannelHandler != null)
            wrapperChannelHandler.handler().handlerRemoved(this.channel);
        return this;
    }

    @Override
    public ChannelPipeline replace(String oldName, String newName, ChannelHandler handler) {
        int index = indexOf(oldName);
        if (index == -1)
            throw new IllegalArgumentException(String.format("No handler with name %s found", oldName));

        WrapperChannelHandler oldHandler = this.handlers.get(index);
        if (oldHandler != null)
            oldHandler.handler().handlerRemoved(this.channel);

        this.handlers.set(index, new WrapperChannelHandler(newName, handler));
        handler.handlerAdded(this.channel);
        return this;
    }

    @Override
    public @Nullable ChannelHandler get(String name) {
        return this.handlers.stream()
                .filter(handler -> handler.name().equals(name))
                .findFirst()
                .map(WrapperChannelHandler::handler)
                .orElse(null);
    }

    @Override
    public @Nullable ChannelHandler first() {
        WrapperChannelHandler handler = this.handlers.getFirst();
        return handler == null ? null : handler.handler();
    }

    @Override
    public @Nullable ChannelHandler last() {
        WrapperChannelHandler handler = this.handlers.getLast();
        return handler == null ? null : handler.handler();
    }

    @Override
    public void fireChannelRead(ByteBuffer buffer) {
        Packet packet = null;
        for (ChannelHandler handler : handlers()) {
            if (handler instanceof ByteToByteDecoder) {
                try {
                    ByteToByteDecoder decoder = (ByteToByteDecoder) handler;
                    ByteBuffer out = ByteBuffer.allocate(1024);
                    decoder.decode(buffer, out);
                    buffer = out;
                } catch (Exception e) {
                    handler.exceptionCaught(this.channel, e);
                }
            } else if (handler instanceof ByteToPacketDecoder) {
                try {
                    ByteToPacketDecoder<?> decoder = (ByteToPacketDecoder<?>) handler;
                    packet = decoder.decode(buffer);
                    if (packet == null)
                        System.out.println("Packet is null");
                } catch (Exception e) {
                    handler.exceptionCaught(this.channel, e);
                }
            }
        }

        if (packet == null) {
            fireExceptionCaught(new IllegalStateException("No packet was decoded"));
            return;
        }

        Packet finalPacket = packet;
        this.handlers.stream()
                .map(WrapperChannelHandler::handler)
                .filter(handler -> handler instanceof ChannelInboundHandler)
                .map(ChannelInboundHandler.class::cast)
                .forEach(handler -> handler.channelRead(this.channel, finalPacket));
    }

    @Override
    public void fireChannelInactive() {
        this.handlers
                .stream()
                .map(WrapperChannelHandler::handler)
                .forEach(handler -> {
                    if (handler instanceof ChannelInboundHandler)
                        ((ChannelInboundHandler) handler).channelInactive(this.channel);
                    handler.handlerRemoved(this.channel);
                });
    }

    @Override
    public void fireChannelActive() {
        this.handlers
                .stream()
                .map(WrapperChannelHandler::handler)
                .filter(handler -> handler instanceof ChannelInboundHandler)
                .map(ChannelInboundHandler.class::cast)
                .forEach(handler -> handler.channelActive(this.channel));
    }

    @Override
    public void fireExceptionCaught(Throwable cause) {
        this.handlers
                .stream()
                .map(WrapperChannelHandler::handler)
                .forEach(handler -> handler.exceptionCaught(this.channel, cause));
    }

    public LinkedList<ChannelHandler> handlers() {
        return this.handlers.stream()
                .map(WrapperChannelHandler::handler)
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
    }

    private int indexOf(String name) {
        for (int i = 0; i < this.handlers.size(); i++) {
            if (this.handlers.get(i).name().equals(name)) {
                return i;
            }
        }
        return -1;
    }

}
