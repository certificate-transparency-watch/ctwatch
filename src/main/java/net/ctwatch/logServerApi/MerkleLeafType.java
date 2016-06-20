package net.ctwatch.logServerApi;

import io.netty.buffer.ByteBuf;

public enum MerkleLeafType {
    TIMESTAMPED_ENTRY;

    public static MerkleLeafType parse(ByteBuf byteBuffer) {
        short i = byteBuffer.readUnsignedByte();
        if (i == 0)
            return TIMESTAMPED_ENTRY;
        else {
            System.out.println(i);
            throw new IllegalArgumentException();
        }

    }
}
