package net.ctwatch.logServerApi;

import io.netty.buffer.ByteBuf;

public enum LogEntryType {
    X509_CERT,
    PRECERT_ENTRY;

    public static LogEntryType parse(ByteBuf buf) {
        switch (buf.readByte()) {
            case 0: return X509_CERT;
            case 1: return PRECERT_ENTRY;
            default: throw new IllegalArgumentException();
        }
    }
}
