package pazuzu.web;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import pazuzu.service.FeatureService;
import pazuzu.service.ServiceException;
import pazuzu.web.dto.FeatureDto;
import pazuzu.web.dto.FeatureFullDto;
import pazuzu.web.dto.FeatureToCreateDto;

@Path("/api/features")
@Produces("application/json")
@Consumes("application/json")
public class FeaturesResource {

    private final FeatureService featureService;

    @Inject
    public FeaturesResource(FeatureService featureService) {
        this.featureService = featureService;
    }

    @GET
    @Path("/")
    public List<FeatureDto> listFeatures(@QueryParam("name") @DefaultValue("") String name) {
        return featureService.listFeatures(name, FeatureDto::ofShort);
    }

    @POST
    @Path("/")
    public FeatureFullDto createFeature(FeatureToCreateDto value) throws ServiceException {
        return featureService.createFeature(
                value.getName(), value.getDockerData(), value.getDependencies(), FeatureFullDto::makeFull);
    }

    @PUT
    @Path("/{feature_id}")
    public FeatureFullDto updateFeature(@PathParam("feature_id") String featureId, FeatureToCreateDto value) throws ServiceException {
        return featureService.updateFeature(featureId, value.getName(), value.getDockerData(), value.getDependencies(), FeatureFullDto::makeFull);
    }

    @GET
    @Path("/{feature_id}")
    public FeatureFullDto getFeature(@PathParam("feature_id") String featureName) throws ServiceException {
        return featureService.getFeature(featureName, FeatureFullDto::makeFull);
    }

    @DELETE
    @Path("/{feature_id}")
    public void deleteFeature(@PathParam("feature_id") String featureName) throws ServiceException {
        featureService.deleteFeature(featureName);
    }

}
