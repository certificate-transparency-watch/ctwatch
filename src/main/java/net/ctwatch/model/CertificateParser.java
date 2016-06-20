package net.ctwatch.model;

import com.google.common.base.Throwables;
import com.google.common.collect.Range;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CertificateParser {
    public static Certificate fromJREClass(X509Certificate x509Certificate) {
        JcaX509CertificateHolder x509CertificateHolder = null;
        try {
            x509CertificateHolder = new JcaX509CertificateHolder(x509Certificate);
        } catch (CertificateEncodingException e) {
            Throwables.propagate(e);
        }

        LocalDateTime before = LocalDateTime.ofInstant(Instant.ofEpochMilli(x509Certificate.getNotBefore().getTime()), ZoneId.systemDefault());
        LocalDateTime after = LocalDateTime.ofInstant(Instant.ofEpochMilli(x509Certificate.getNotAfter().getTime()), ZoneId.systemDefault());
        return Certificate.create(
                commonName(x509CertificateHolder),
                san(x509CertificateHolder),
                Range.open(before, after),
                x509Certificate.getIssuerDN().toString(),
                x509CertificateHolder.getSerialNumber()
        );
    }

    private static Optional<String> commonName(JcaX509CertificateHolder c) {
        X500Name x500name = c.getSubject();
        return Arrays.asList(x500name.getRDNs(BCStyle.CN))
                .stream()
                .findFirst()
                .map(cn -> IETFUtils.valueToString(cn.getFirst().getValue()));
    }

    private static Set<String> san(JcaX509CertificateHolder c) {
        Set<String> result = new HashSet<>();
        GeneralNames gns = GeneralNames.fromExtensions(c.getExtensions(), Extension.subjectAlternativeName);
        if (gns != null) {
            GeneralName[] names = gns.getNames();
            for (int k = 0; k < names.length; k++) {
                String title = "";
                if (names[k].getTagNo() == GeneralName.dNSName) {
                    title = "dNSName";
                    result.add(names[k].getName().toString());
                } else if (names[k].getTagNo() == GeneralName.iPAddress) {
                    title = "iPAddress";
                    names[k].toASN1Object();
                } else if (names[k].getTagNo() == GeneralName.otherName) {
                    title = "otherName";
                }
            }
        }
        return result;
    }
}
