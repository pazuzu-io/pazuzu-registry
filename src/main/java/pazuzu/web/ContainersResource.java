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
import pazuzu.model.Feature;
import pazuzu.service.ContainerService;
import pazuzu.service.ServiceException;
import pazuzu.web.dto.ContainerDto;
import pazuzu.web.dto.ContainerFullDto;
import pazuzu.web.dto.ContainerToCreateDto;
import pazuzu.web.dto.FeatureToAddDto;

@Path("/api/containers")
@Produces("application/json")
@Consumes("application/json")
public class ContainersResource {
    private final ContainerService containerService;

    @Inject
    public ContainersResource(ContainerService containerService) {
        this.containerService = containerService;
    }

    @GET
    @Path("/")
    public List<ContainerDto> listContainers(
            @QueryParam("name") @DefaultValue("") String name) {
        return containerService.listContainers(name, ContainerDto::ofShort);
    }

    @POST
    @Path("/")
    public ContainerFullDto createContainer(ContainerToCreateDto value) throws ServiceException {
        return containerService.createContainer(value.getName(), value.getFeatures(), ContainerFullDto::buildFull);
    }

    @PUT
    @Path("/{container_id}")
    public ContainerFullDto updateContainer(@PathParam("container_id") String containerName, ContainerToCreateDto value) throws ServiceException {
        return containerService.updateContainer(containerName, value.getName(), value.getFeatures(), ContainerFullDto::buildFull);
    }

    @GET
    @Path("/{container_id}")
    public ContainerFullDto getContainer(@PathParam("container_id") String containerName) {
        return containerService.getContainer(containerName, ContainerFullDto::buildFull);
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
