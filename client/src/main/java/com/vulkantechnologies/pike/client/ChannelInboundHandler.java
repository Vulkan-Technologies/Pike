package com.vulkantechnologies.pike.client;

import java.nio.channels.SocketChannel;

import com.vulkantechnologies.pike.commons.ClientPacket1;
import com.vulkantechnologies.pike.commons.packet.Packet;

public class ChannelInboundHandler implements com.vulkantechnologies.pike.commons.network.channel.ChannelInboundHandler {


    @Override
    public void channelActive(SocketChannel channel) {
        System.out.println("Channel active");
    }

    @Override
    public void channelInactive(SocketChannel channel) {
        System.out.println("Channel inactive");
    }

    @Override
    public void channelRead(SocketChannel channel, Packet message) {
        System.out.println("Packet received!");
        if (message instanceof ClientPacket1) {
            System.out.println("Packet is instance of ClientPacket1: " + ((ClientPacket1) message).message());
        }
    }

    @Override
    public void handlerAdded(SocketChannel channel) {
        System.out.println("Handler added");
    }

    @Override
    public void handlerRemoved(SocketChannel channel) {
        System.out.println("Handler removed");
    }

    @Override
    public void exceptionCaught(SocketChannel channel, Throwable cause) {
        cause.printStackTrace();
    }
}
