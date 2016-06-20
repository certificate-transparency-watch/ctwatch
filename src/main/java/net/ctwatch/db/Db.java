package net.ctwatch.db;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.net.InternetDomainName;
import net.ctwatch.model.Certificate;
import net.ctwatch.model.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class Db {
    private static Logger LOG = LoggerFactory.getLogger(Db.class);

    SetMultimap<String, Certificate> db = HashMultimap.create();
    SetMultimap<Certificate, LogEntry> cert2LogEntry = HashMultimap.create();

    public void writeLogEntry(LogEntry logEntry) {
        if (logEntry.index() % 10000 == 0)
            LOG.info("Writing log index " + logEntry.index());

        Certificate certificate = logEntry.certificate();

        if(certificate.commonName().isPresent())
            db.put(certificate.commonName().get(), certificate);

        certificate.subjectAlternativeNames().forEach(san -> {
            db.put(san, certificate);
        });

        cert2LogEntry.put(certificate, logEntry);
    }

    public Set<LogEntry> certsForDomain(InternetDomainName domain) {
        return db.get(domain.toString())
                .stream()
                .flatMap(cert -> cert2LogEntry.get(cert).stream())
                .collect(Collectors.toSet());
    }
}
