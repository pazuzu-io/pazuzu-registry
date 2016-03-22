package pazuzu.web;

import pazuzu.service.FeatureService;
import pazuzu.service.ServiceException;
import pazuzu.web.dto.FeatureDto;
import pazuzu.web.dto.FeatureFullDto;
import pazuzu.web.dto.FeatureToCreateDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

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
    public Response createFeature(FeatureToCreateDto value, @Context UriInfo uriInfo) throws ServiceException {
        FeatureFullDto feature = featureService.createFeature(
                value.getName(), value.getDockerData(), value.getDependencies(), FeatureFullDto::makeFull);

        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(feature.getName()).build())
                .entity(feature)
                .build();
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
