package net.ctwatch.http;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import net.ctwatch.model.Certificate;
import net.ctwatch.model.LogEntry;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DomainAtomFeedGenerator {
    public String generateAtomFeed(String domain, Set<LogEntry> logEntries) throws XMLStreamException {
        return document(x ->
                el("feed", new ImmutableMap.Builder()
                                .put("xmlns", "http://www.w3.org/2005/Atom")
                                .put("xmlns:dc", "http://purl.org/dc/elements/1.1/")
                                .build(),
                        el("author",
                                el("name",
                                        t("Certificate Transparency Watch"))),
                        el("id",
                                t("https://ctwatch.net/domain/" + domain)),
                        el("title",
                                t("Certificates for " + domain)),
                        collection(logEntries, (le, x2) -> {
                                    String str = "https://ct.googleapis.com/aviator/ct/v1/get-entries?start" + le.index() + "&end=" + (le.index() + 1);
                                    Certificate cert = le.certificate();
                                    String title = cert.commonName() + "by " + cert.issuerDistinguishedName() + " with serial number " + cert.serialNumber();
                                    el("entry",
                                            el("id", t(str)),
                                            el("link", ImmutableMap.of("href", str)),
                                            el("content", ImmutableMap.of("type", "xhtml"),
                                                    el("h2", t(title)),
                                                    map(new ImmutableMap.Builder()
                                                            .put("Common Name", cert.commonName().toString())
                                                            //TODO .put("Subject", "foo")
                                                            .put("Subject Alternative Names", cert.subjectAlternativeNames().toString())
                                                            .put("Expiration Date", cert.validity().upperEndpoint().toString())
                                                            .put("Issuer", cert.issuerDistinguishedName().toString())
                                                            .put("Serial Number", cert.serialNumber().toString())
                                                            .put("Log Server", "https://ct.googleapis.com/avaiator")
                                                            .put("Log Server Index", String.valueOf(le.index()))
                                                            // TODO .put("Link to entry in log server", "")
                                                            .build())
                                            )
                                    ).accept(x2);
                                }
                        )).accept(x));
    }

    private static <T> Consumer<XMLStreamWriter> collection(Collection<T> c, BiConsumer<T, XMLStreamWriter> inner) {
        return x -> {
            c.forEach(e -> inner.accept(e, x));
        };
    }

    private static <T> Consumer<XMLStreamWriter> map(Map<String, String> m) {
        return el("table", x ->
            m.forEach((k, v) -> {
                el("tr",
                        el("td", t(k)),
                        el("td", t(v))
                ).accept(x);
            })
        );
    }

    private static String document(Consumer<XMLStreamWriter>... inner) throws XMLStreamException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        StringWriter stream = new StringWriter();
        XMLStreamWriter xml = xmlOutputFactory.createXMLStreamWriter(stream);
        xml.writeStartDocument();
        Arrays.asList(inner).forEach(c -> c.accept(xml));
        xml.writeEndDocument();
        return stream.toString();
    }

    private static Consumer<XMLStreamWriter> el(String name, Map<String, String> attributes, Consumer<XMLStreamWriter>... inner) {
        return xmlStreamWriter -> {
            try {
                xmlStreamWriter.writeStartElement(name);
                attributes.forEach((k,v) -> attribute(k, v).accept(xmlStreamWriter));
                Arrays.asList(inner).forEach(i -> {
                    i.accept(xmlStreamWriter);
                });
                xmlStreamWriter.writeEndElement();
            } catch (XMLStreamException e) {
                Throwables.propagate(e);
            }
        };
    }

    private static Consumer<XMLStreamWriter> el(String name, Consumer<XMLStreamWriter>... inner) {
        return el(name, Collections.EMPTY_MAP, inner);
    }

    private static Consumer<XMLStreamWriter> attribute(String name, String value) {
        return xmlStreamWriter -> {
            try {
                xmlStreamWriter.writeAttribute(name, value);
            } catch (XMLStreamException e) {
                Throwables.propagate(e);
            }
        };
    }



    public static Consumer<XMLStreamWriter> t(String text) {
        return xmlStreamWriter -> {
            try {
                xmlStreamWriter.writeCharacters(text);
            } catch (XMLStreamException e) {
                Throwables.propagate(e);
            }
        };
    }

    public static <T> List<T> l(T... t){
        return Arrays.asList(t);
    }

}
