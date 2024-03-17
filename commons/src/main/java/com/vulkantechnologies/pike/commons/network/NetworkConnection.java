package com.vulkantechnologies.pike.commons.network;

import com.vulkantechnologies.pike.commons.packet.Packet;
import com.vulkantechnologies.pike.commons.protocol.ProtocolVersion;

public interface NetworkConnection<P extends Packet> {

    void enableCompression();

    void disableEncryption();

    void enableEncryption();

    void disableCompression();

    void sendPacket(P packet);

    void disconnect();

    boolean connected();

    boolean isCompressionEnabled();

    boolean isEncrypted();

    ProtocolVersion protocolVersion();

}
