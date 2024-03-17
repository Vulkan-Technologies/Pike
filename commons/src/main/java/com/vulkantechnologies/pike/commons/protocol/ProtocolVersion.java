package com.vulkantechnologies.pike.commons.protocol;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.vulkantechnologies.pike.commons.utils.binary.BinaryReader;
import com.vulkantechnologies.pike.commons.utils.binary.BinaryWriter;
import com.vulkantechnologies.pike.commons.utils.binary.Writeable;

import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import lombok.Data;

@Data
public class ProtocolVersion implements Writeable {

    protected static final Short2ObjectOpenHashMap<ProtocolVersion> REGISTRY = new Short2ObjectOpenHashMap<>();

    private final int id;
    private final String name;
    private final List<ProtocolVersion> supported;

    public ProtocolVersion(int id, String name, ProtocolVersion... supported) {
        this.id = id;
        this.name = name;
        this.supported = Arrays.asList(supported);

        REGISTRY.put((short) id, this);
    }

    public boolean supports(ProtocolVersion version) {
        return this.id == version.id
               || this.supported.contains(version);
    }

    @Override
    public void write(@NotNull BinaryWriter writer) {
        writer.writeVarInt(id);
    }

    public static Optional<ProtocolVersion> of(int id) {
        return Optional.ofNullable(REGISTRY.get((short) id));
    }

    public static ProtocolVersion read(@NotNull BinaryReader reader) {
        return of(reader.readVarInt())
                .orElseThrow(() -> new IllegalArgumentException("Unknown protocol version"));
    }
}
