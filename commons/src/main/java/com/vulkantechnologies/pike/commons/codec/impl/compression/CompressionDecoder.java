package com.vulkantechnologies.pike.commons.codec.impl.compression;

import java.nio.ByteBuffer;
import java.util.zip.Inflater;

import com.vulkantechnologies.pike.commons.codec.decoder.ByteToByteDecoder;
import com.vulkantechnologies.pike.commons.utils.binary.BinaryReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompressionDecoder implements ByteToByteDecoder {

    private final int threshold;
    private final Inflater inflater = new Inflater();

    @Override
    public void decode(ByteBuffer in, ByteBuffer out) {
        if (in.remaining() == 0)
            throw new IllegalArgumentException("Input buffer is empty");
        try (BinaryReader reader = new BinaryReader(in)) {
            final int claimedUncompressedSize = reader.readVarInt();

            // No compression
            if (claimedUncompressedSize == 0) {
                out.put(in.slice());
                return;
            }

            // Check if the claimed uncompressed size is lower than the threshold
            if (claimedUncompressedSize < threshold)
                throw new IllegalArgumentException("Badly compressed packet - claimed uncompressed size is lower than threshold");

            // Read the compressed data
            final byte[] compressedData = new byte[in.remaining()];
            in.get(compressedData);

            // Decompress the data
            inflater.setInput(compressedData);
            final byte[] uncompressedData = new byte[claimedUncompressedSize];
            inflater.inflate(uncompressedData);
            inflater.reset();

            // Write the uncompressed data to the output buffer
            out.put(uncompressedData);
        } catch (Exception e) {
            throw new RuntimeException("Error while reading from buffer", e);
        }
    }
}
