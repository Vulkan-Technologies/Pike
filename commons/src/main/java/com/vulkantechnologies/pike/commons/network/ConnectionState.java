package com.vulkantechnologies.pike.commons.network;

import org.jetbrains.annotations.NotNull;

import com.vulkantechnologies.pike.commons.utils.binary.BinaryReader;
import com.vulkantechnologies.pike.commons.utils.binary.BinaryWriter;
import com.vulkantechnologies.pike.commons.utils.binary.Writeable;

public enum ConnectionState implements Writeable {
    UNKNOWN,
    HANDSHAKE,
    LOGIN,
    CONNECTED;

    @Override
    public void write(@NotNull BinaryWriter writer) {
        writer.writeByte((byte) ordinal());
    }

    public static ConnectionState read(@NotNull BinaryReader reader) {
        return values()[reader.readByte()];
    }
}
