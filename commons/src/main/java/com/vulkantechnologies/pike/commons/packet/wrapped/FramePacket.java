package com.vulkantechnologies.pike.commons.packet.wrapped;

import java.nio.ByteBuffer;

import lombok.Data;

/**
 * Represents a packet which is already framed. (packet id+payload) + optional compression
 * Can be used if you want to send the exact same buffer to multiple clients without processing it more than once.
 */
@Data
public class FramePacket {

    private final ByteBuffer byteBuffer;
}
