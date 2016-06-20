package net.ctwatch.http;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class WebsiteResource {
    @GET
    public String frontpage() {
        return "<h1>2016-06-20: Under construction</h1> Atom feed will soon be back at the same URL https://api.ctwatch.net/domain/ietf.org";
    }
}
