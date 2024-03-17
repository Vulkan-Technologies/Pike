package com.vulkantechnologies.pike.commons.codec.impl.compression;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;

import com.vulkantechnologies.pike.commons.codec.encoder.ByteToByteEncoder;
import com.vulkantechnologies.pike.commons.utils.binary.BinaryWriter;

import lombok.Data;

@Data
public class CompressionEncoder implements ByteToByteEncoder {

    private final int threshold;
    private final Deflater deflater = new Deflater();

    @Override
    public void encode(ByteBuffer in, ByteBuffer out) {
        int packetLength = in.remaining();
        boolean needsCompression = packetLength >= threshold;

        try (BinaryWriter writer = new BinaryWriter(out)) {
            // Write the uncompressed size
            writer.writeVarInt(packetLength);

            // If the packet is small enough, we don't need to compress it
            if (!needsCompression) {
                writer.write(in);
                return;
            }

            // Compress
            byte[] uncompressedData = new byte[packetLength];
            in.get(uncompressedData);
            deflater.setInput(uncompressedData);
            deflater.finish();
            while (!deflater.finished()) {
                byte[] buffer = new byte[1024];
                int count = deflater.deflate(buffer);
                out.put(buffer, 0, count);
            }
            deflater.reset();

        } catch (IOException e) {
            throw new RuntimeException("Error while writing to buffer", e);
        }
    }
}
