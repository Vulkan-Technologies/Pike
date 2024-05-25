package com.vulkantechnologies.pike.commons.network;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.vulkantechnologies.pike.commons.packet.Packet;

public interface ConnectionInitializer<P extends Packet, C extends NetworkConnection<P>> {

    C initialize(SocketChannel channel) throws IOException;

}
