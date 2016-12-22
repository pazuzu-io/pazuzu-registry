package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.pazuzu.exception.ServiceException;
import org.zalando.pazuzu.security.Roles;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/features")
public class FeaturesResource {

    private static final String X_TOTAL_COUNT = "X-Total-Count";
    private static final Integer TOPOLOGICAL_SORT = 1;
    private final FeatureService featureService;

    @Autowired
    public FeaturesResource(FeatureService featureService) {
        this.featureService = featureService;
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FeatureDto> listFeatures(
            @RequestParam(required = false, name = "name") String[] featureNames,
            @RequestParam(required = false, name = "sorting") Integer sorting,
            @RequestParam(required = false, name = "offset") Integer offset,
            @RequestParam(required = false, name = "limit") Integer limit,
            HttpServletResponse response)
            throws ServiceException {
        if (featureNames == null) {
            if (offset != null && limit != null) {
                FeaturesWithTotalCount<FeatureDto> featuresTotalCount =
                        featureService.getFeaturesWithTotalCount(offset, limit, FeatureDto::of);
                response.setHeader(X_TOTAL_COUNT, Long.toString(featuresTotalCount.getTotalCount()));
                response.setHeader("Access-Control-Expose-Headers", X_TOTAL_COUNT);
                return featuresTotalCount.getFeatures();
            } else {
                return featureService.listFeatures("", FeatureDto::of);
            }
        }
        Set<Feature> featureSet = featureService.loadFeatures(Arrays.stream(featureNames).collect(Collectors.toList()));
        if (sorting != null && sorting.equals(TOPOLOGICAL_SORT)) {
            List<Feature> features = featureService.getSortedFeatures(featureSet);
            return features.stream().map(FeatureDto::of).collect(Collectors.toList());
        }
        return featureSet.stream().map(FeatureDto::of).collect(Collectors.toList());
    }

    @RolesAllowed({Roles.USER})
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FeatureDto> createFeature(@RequestBody FeatureDto value, UriComponentsBuilder uriBuilder) throws ServiceException {
        FeatureDto feature = featureService.createFeature(
                value.getMeta().getName(), value.getSnippet(), value.getTestSnippet(), value.getMeta().getDescription(),
                value.getMeta().getDependencies(), FeatureDto::of);

        return ResponseEntity
                .created(uriBuilder.path("/api/features/{featureName}").buildAndExpand(feature.getMeta().getName()).toUri())
                .body(feature);
    }

    @RolesAllowed({Roles.ADMIN})
    @RequestMapping(value = "/{featureName}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public FeatureDto updateFeature(@PathVariable String featureName, @RequestBody FeatureDto value) throws ServiceException {
        return featureService.updateFeature(
                featureName, value.getMeta().getName(), value.getSnippet(), value.getTestSnippet(),
                value.getMeta().getDescription(), value.getMeta().getDependencies(), FeatureDto::of);
    }

    @RolesAllowed({Roles.ADMIN})
    @RequestMapping(value = "/{featureName}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteFeature(@PathVariable String featureName) throws ServiceException {
        featureService.deleteFeature(featureName);
        return ResponseEntity.noContent().build();
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    @RequestMapping(value = "/{featureName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public FeatureDto getFeature(@PathVariable String featureName) throws ServiceException {
        return featureService.getFeature(featureName, FeatureDto::of);
    }
}
