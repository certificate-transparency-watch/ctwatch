package net.ctwatch.http;

import com.google.common.net.InternetDomainName;
import net.ctwatch.db.Db;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.stream.XMLStreamException;

@Path("/")
@Produces(MediaType.APPLICATION_ATOM_XML)
public class ApiResource {
    private final Db db;

    public ApiResource(Db db) {
        this.db = db;
    }

    @GET
    @Path("/domain/{domain}")
    public String fetch(@PathParam("domain") String domain) throws XMLStreamException {
        InternetDomainName domainName = InternetDomainName.from(domain);
        if (domainName.isPublicSuffix()) {
            throw new IllegalArgumentException("Cannot request a whole domain.");
        } else {
            return new DomainAtomFeedGenerator().generateAtomFeed(domain, db.certsForDomain(domainName));
        }
    }
}
