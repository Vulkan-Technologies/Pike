package com.vulkantechnologies.pike.commons.network.channel.pipeline;

import org.jetbrains.annotations.ApiStatus;

import com.vulkantechnologies.pike.commons.network.channel.ChannelHandler;

import lombok.Data;

@Data
@ApiStatus.Internal
public class WrapperChannelHandler {

    private final String name;
    private final ChannelHandler handler;
}
