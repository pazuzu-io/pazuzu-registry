package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.pazuzu.exception.ServiceException;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/features")
public class FeaturesResource {

    private static final Integer TOPOLOGICAL_SORT = 1;
    private final FeatureService featureService;

    @Autowired
    public FeaturesResource(FeatureService featureService) {
        this.featureService = featureService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FeatureDto> listFeatures(
            @RequestParam(required = false, name = "name") String[] featureNames,
            @RequestParam(required = false, name = "sorted") Integer sorting)
            throws ServiceException {
        if (featureNames == null) {
            return featureService.listFeatures("", FeatureDto::ofShort);
        }
        Set<Feature> featureSet = featureService.loadFeatures(Arrays.stream(featureNames).collect(Collectors.toList()));
        if (sorting != null && sorting.equals(TOPOLOGICAL_SORT)) {
            List<Feature> features = featureService.getSortedFeatures(featureSet);
            return features.stream().map(FeatureDto::ofShort).collect(Collectors.toList());
        }
        return featureSet.stream().map(FeatureDto::ofShort).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FeatureFullDto> createFeature(@RequestBody FeatureToCreateDto value, UriComponentsBuilder uriBuilder) throws ServiceException {
        FeatureFullDto feature = featureService.createFeature(
                value.getName(), value.getDockerData(), value.getTestInstruction(), value.getDependencies(), FeatureFullDto::makeFull);

        return ResponseEntity
                .created(uriBuilder.path("/api/features/{featureName}").buildAndExpand(feature.getName()).toUri())
                .body(feature);
    }

    @RequestMapping(value = "/{featureName}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public FeatureFullDto updateFeature(@PathVariable String featureName, @RequestBody FeatureToCreateDto value) throws ServiceException {
        return featureService.updateFeature(featureName, value.getName(), value.getDockerData(), value.getTestInstruction(), value.getDependencies(), FeatureFullDto::makeFull);
    }

    @RequestMapping(value = "/{featureName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public FeatureFullDto getFeature(@PathVariable String featureName) throws ServiceException {
        return featureService.getFeature(featureName, FeatureFullDto::makeFull);
    }

    @RequestMapping(value = "/{featureName}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteFeature(@PathVariable String featureName) throws ServiceException {
        featureService.deleteFeature(featureName);
        return ResponseEntity.noContent().build();
    }
}
