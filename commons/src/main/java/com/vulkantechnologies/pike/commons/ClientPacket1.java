package com.vulkantechnologies.pike.commons;

import org.jetbrains.annotations.NotNull;

import com.vulkantechnologies.pike.commons.packet.ClientboundPacket;
import com.vulkantechnologies.pike.commons.utils.binary.BinaryReader;
import com.vulkantechnologies.pike.commons.utils.binary.BinaryWriter;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ClientPacket1 implements ClientboundPacket {

    private String message;

    @Override
    public void read(@NotNull BinaryReader reader) {
        this.message = reader.readString();
    }

    @Override
    public void write(@NotNull BinaryWriter writer) {
        writer.writeString(this.message);
    }
}
