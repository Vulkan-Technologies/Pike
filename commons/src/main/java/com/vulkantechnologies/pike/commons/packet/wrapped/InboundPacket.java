package com.vulkantechnologies.pike.commons.packet.wrapped;

import java.nio.ByteBuffer;

import lombok.Data;

@Data
public class InboundPacket {

    private final int id;
    private final ByteBuffer byteBuffer;
}
