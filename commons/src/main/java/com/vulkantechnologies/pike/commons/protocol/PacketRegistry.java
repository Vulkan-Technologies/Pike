package com.vulkantechnologies.pike.commons.protocol;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.vulkantechnologies.pike.commons.packet.Packet;

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;

public class PacketRegistry {

    private static final Short2ObjectOpenHashMap<Packet> REGISTRY = new Short2ObjectOpenHashMap<>();

    public static void register(int id, Packet packet) {
        REGISTRY.put((short) id, packet);
    }

    public static void unregister(int id) {
        REGISTRY.remove((short) id);
    }

    public static @NotNull Optional<Packet> get(int id) {
        return Optional.ofNullable(REGISTRY.get((short) id));
    }

    public static int getId(Packet packet) throws IllegalArgumentException {
        return REGISTRY.short2ObjectEntrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(packet))
                .mapToInt(Short2ObjectMap.Entry::getShortKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Packet not registered"));
    }


}
