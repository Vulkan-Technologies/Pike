package com.vulkantechnologies.pike.server;

import lombok.Builder;

@Builder
public record ServerOptions(
        int workers,
        String host,
        int port,
        boolean tcpNoDelay,
        int timeout,
        int sendBufferSize,
        int receiveBufferSize,
        int compressionThreshold) {
}
