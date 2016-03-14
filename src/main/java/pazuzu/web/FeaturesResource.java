package pazuzu.web;

import java.util.List;
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
import pazuzu.web.dto.FeatureDto;
import pazuzu.web.dto.FeatureFullDto;
import pazuzu.web.dto.FeatureToCreateDto;

@Path("/api/features")
@Produces("application/json")
@Consumes("application/json")
public class FeaturesResource {

    @GET
    @Path("/")
    public List<FeatureDto> listFeatures(
            @QueryParam("name") @DefaultValue("") String name) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/")
    public FeatureFullDto createFeature(FeatureToCreateDto value) {
        throw new UnsupportedOperationException();
    }

    @PUT
    @Path("/{feature_id}")
    public FeatureFullDto updateFeature(@PathParam("feature_id") String featureId, FeatureToCreateDto value) {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{feature_id}")
    public FeatureFullDto getFeature(@PathParam("feature_id") String featureName) {
        throw new UnsupportedOperationException();
    }

    @DELETE
    @Path("/{feature_id}")
    public void deleteFeature(@PathParam("feature_id") String featureName) {
        throw new UnsupportedOperationException();
    }

}
