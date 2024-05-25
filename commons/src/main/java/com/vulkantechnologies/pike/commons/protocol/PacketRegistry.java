package com.vulkantechnologies.pike.commons.protocol;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.vulkantechnologies.pike.commons.ClientPacket1;
import com.vulkantechnologies.pike.commons.ServerPacket1;
import com.vulkantechnologies.pike.commons.packet.Packet;

public class PacketRegistry {

    private static final Map<Short, Packet> REGISTRY = new HashMap<>();

    private static void register(int id, Packet packet) {
        REGISTRY.put((short) id, packet);
    }

    public static @Nullable Packet get(short id) {
        return REGISTRY.get(id);
    }

    public static short getId(@NotNull Packet packet) {
        return REGISTRY.entrySet()
                .stream()
                .filter(packetEntry -> packetEntry.getValue().getClass().equals(packet.getClass()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse((short) -1);
    }

    static {
        register(0x01, new ServerPacket1());
        register(0x02, new ClientPacket1());
    }


}
