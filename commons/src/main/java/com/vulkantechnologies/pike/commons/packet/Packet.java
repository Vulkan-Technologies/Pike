package com.vulkantechnologies.pike.commons.packet;

public interface Packet {

    short id();

    int length();

    byte[] data();
}
