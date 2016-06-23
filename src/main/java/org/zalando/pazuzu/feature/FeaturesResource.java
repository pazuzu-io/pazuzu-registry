package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.pazuzu.exception.ServiceException;
import org.zalando.pazuzu.feature.file.FileDto;
import org.zalando.pazuzu.feature.file.FileLinkService;
import org.zalando.pazuzu.security.Roles;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/features")
public class FeaturesResource {

    private static final String X_TOTAL_COUNT = "X-Total-Count";
    private static final Integer TOPOLOGICAL_SORT = 1;
    private final FeatureService featureService;
    private final FileLinkService fileLinkService;

    @Autowired
    public FeaturesResource(FeatureService featureService, FileLinkService fileLinkService) {
        this.featureService = featureService;
        this.fileLinkService = fileLinkService;
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FeatureDto> listFeatures(
            @RequestParam(required = false, name = "name") String[] featureNames,
            @RequestParam(required = false, name = "sorted") Integer sorting,
            @RequestParam(required = false, name = "offset") Integer offset,
            @RequestParam(required = false, name = "limit") Integer limit,
            HttpServletResponse response)
            throws ServiceException {
        if (featureNames == null) {
            if (offset != null && limit != null) {
                FeaturesWithTotalCount<FeatureDto> featuresTotalCount =
                        featureService.getFeaturesWithTotalCount(offset, limit, FeatureDto::ofShort);
                response.setHeader(X_TOTAL_COUNT, Long.toString(featuresTotalCount.getTotalCount()));
                response.setHeader("Access-Control-Expose-Headers", X_TOTAL_COUNT);
                return featuresTotalCount.getFeatures();
            } else {
                return featureService.listFeatures("", FeatureDto::ofShort);
            }
        }
        Set<Feature> featureSet = featureService.loadFeatures(Arrays.stream(featureNames).collect(Collectors.toList()));
        if (sorting != null && sorting.equals(TOPOLOGICAL_SORT)) {
            List<Feature> features = featureService.getSortedFeatures(featureSet);
            return features.stream().map(FeatureDto::ofShort).collect(Collectors.toList());
        }
        return featureSet.stream().map(FeatureDto::ofShort).collect(Collectors.toList());
    }

    @RolesAllowed({Roles.USER})
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FeatureFullDto> createFeature(@RequestBody FeatureToCreateDto value, UriComponentsBuilder uriBuilder) throws ServiceException {
        FeatureFullDto feature = featureService.createFeature(
                value.getName(), value.getDockerData(), value.getTestInstruction(), value.getDescription(),
                value.getDependencies(), value.getTags(), FeatureFullDto::makeFull);

        return ResponseEntity
                .created(uriBuilder.path("/api/features/{featureName}").buildAndExpand(feature.getName()).toUri())
                .body(feature);
    }

    @RolesAllowed({Roles.ADMIN})
    @RequestMapping(value = "/{featureName}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public FeatureFullDto updateFeature(@PathVariable String featureName, @RequestBody FeatureToCreateDto value) throws ServiceException {
        return featureService.updateFeature(featureName, value.getName(), value.getDockerData(), value.getTestInstruction(), value.getDescription(), value.getDependencies(), FeatureFullDto::makeFull);
    }

    @RolesAllowed({Roles.ADMIN})
    @RequestMapping(value = "/{featureName}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteFeature(@PathVariable String featureName) throws ServiceException {
        featureService.deleteFeature(featureName);
        return ResponseEntity.noContent().build();
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    @RequestMapping(value = "/{featureName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public FeatureFullDto getFeature(@PathVariable String featureName) throws ServiceException {
        return featureService.getFeature(featureName, FeatureFullDto::makeFull);
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    @RequestMapping(value = "/search/{featureName}", method = RequestMethod.GET)
    public List<FeatureDto> searchFeature(@PathVariable String featureName) throws ServiceException {
        return featureService.listFeatures(featureName, FeatureDto::ofShort);
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    @RequestMapping(value = "/{featureName}/files", method = RequestMethod.GET)
    public List<FileDto> listLinkedFiles(@PathVariable String featureName) {
        // TODO (error reporting) Return 404 if feature is not found
        return fileLinkService.getFeatureFiles(featureName)
                .stream().map(FileDto::fromFile)
                .collect(Collectors.toList());
    }

    @RolesAllowed({Roles.ADMIN})
    @RequestMapping(value = "/{featureName}/approve", method = RequestMethod.PUT)
    public ResponseEntity<Void> approveFeature(@PathVariable String featureName) throws ServiceException {
        featureService.approveFeature(featureName);
        return ResponseEntity.noContent().build();
    }
}
