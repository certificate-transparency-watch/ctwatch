package net.ctwatch.logServerApi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.auto.value.AutoValue;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import static com.google.common.io.BaseEncoding.base64;

@AutoValue
public abstract class Merkle {
    public abstract int version();
    public abstract MerkleLeafType leafType();
    public abstract TimestampedEntry timestampedEntry();

    @JsonCreator
    public static Merkle create(String foo) {
        ByteBuf byteBuf = new UnpooledByteBufAllocator(false).heapBuffer();
        byteBuf.writeBytes(base64().decode(foo));
        return create(byteBuf);
    }

    public static Merkle create(ByteBuf byteBuffer) {
        int version = byteBuffer.readBytes(1).readUnsignedByte();
        MerkleLeafType mlt = MerkleLeafType.parse(byteBuffer.readBytes(1));

        TimestampedEntry timestampedEntry = TimestampedEntry.parse(byteBuffer);

        return Merkle.create(version, mlt, timestampedEntry);
    }

    public static Merkle create(int version, MerkleLeafType merkleLeafType, TimestampedEntry timestampedEntry) {
        return new AutoValue_Merkle(version, merkleLeafType, timestampedEntry);
    }
}
