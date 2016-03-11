package pazuzu.web;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/api/dockerfile")
public class DockerfileResource {
    @GET
    @Produces("text/plain")
    public String generateDockerfile(@QueryParam("feature") List<String> features) {
        throw new UnsupportedOperationException();
    }
}
