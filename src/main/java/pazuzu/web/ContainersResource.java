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
import pazuzu.model.Feature;
import pazuzu.web.dto.ContainerDto;
import pazuzu.web.dto.ContainerFullDto;
import pazuzu.web.dto.ContainerToCreateDto;
import pazuzu.web.dto.FeatureToAddDto;

@Path("/api/containers")
@Produces("application/json")
@Consumes("application/json")
public class ContainersResource {
    @GET
    @Path("/")
    public List<ContainerDto> listContainers(
            @QueryParam("name") @DefaultValue("") String name) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/")
    public ContainerFullDto createContainer(ContainerToCreateDto value) {
        throw new UnsupportedOperationException();
    }

    @PUT
    @Path("/{container_id}")
    public ContainerFullDto updateContainer(@PathParam("container_id") String containerName, ContainerToCreateDto value) {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{container_id}")
    public ContainerFullDto getContainer(@PathParam("container_id") String containerName) {
        throw new UnsupportedOperationException();
    }

    @DELETE
    @Path("/{container_id}")
    public void deleteContainer(@PathParam("container_id") String containerName) {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{container_id}/features")
    public List<Feature> getContainerFeatures(@PathParam("container_id") String containerName) {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/{container_id}/features")
    public ContainerFullDto addFeatureToContainer(@PathParam("container_id") String containerName, FeatureToAddDto feature) {
        throw new UnsupportedOperationException();
    }

    @DELETE
    @Path("/{container_id}/features/{feature_id}")
    public void deleteContainerFeature(@PathParam("container_id") String containerId, @PathParam("feature_id") String featureId) {
        throw new UnsupportedOperationException();
    }

    @GET
    @Path("/{container_id}/dockerfile")
    @Produces("text/plain")
    public String getDockerFile(@PathParam("container_id") String containerName) {
        throw new UnsupportedOperationException();
    }
}
