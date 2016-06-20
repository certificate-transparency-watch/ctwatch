package net.ctwatch.logServerApi;

import com.google.auto.value.AutoValue;
import io.netty.buffer.ByteBuf;

import java.security.cert.CertificateException;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@AutoValue
public abstract class TimestampedEntry {
    public abstract LogEntryType logEntryType();
    public abstract X509Certificate certificate();

    public static TimestampedEntry parse(ByteBuf byteBuffer) {
        byteBuffer.readBytes(8);
        LogEntryType let = LogEntryType.parse(byteBuffer.readBytes(2));

        int sizeOfNextEntry = byteBuffer.readUnsignedMedium();
        X509Certificate cert = null;
        try {
            ByteBuf byteBuf = byteBuffer.readBytes(sizeOfNextEntry);
            byte[] array = byteBuf.array();
            cert = (X509Certificate) CertificateFactory.getInstance("x509").generateCertificate(new ByteArrayInputStream(array));
        } catch (CertificateException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        return new AutoValue_TimestampedEntry(let, cert);
    }
}
