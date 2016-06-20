package net.ctwatch.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.Range;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@AutoValue
public abstract class Certificate {
    public abstract Optional<String> commonName();
    public abstract Set<String> subjectAlternativeNames();
    public abstract Range<LocalDateTime> validity();
    public abstract String issuerDistinguishedName();
    public abstract BigInteger serialNumber();

    public static Certificate create(Optional<String> commonName, Set<String> subjectAlternativeNames, Range<LocalDateTime> validity, String issuerDistinguishedName, BigInteger serialNumber) {
        return new AutoValue_Certificate(commonName, subjectAlternativeNames, validity, issuerDistinguishedName, serialNumber);
    }
}
