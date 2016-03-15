package pazuzu.web;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import pazuzu.service.FeatureService;
import pazuzu.service.ServiceException;

@Path("/api/dockerfile")
public class DockerfileResource {

    private final FeatureService featureService;

    @Inject
    public DockerfileResource(FeatureService featureService) {
        this.featureService = featureService;
    }

    @GET
    @Produces("text/plain")
    public String generateDockerfile(@QueryParam("feature") List<String> features) throws ServiceException {
        return featureService.generateDockerFile(features);
    }
}
