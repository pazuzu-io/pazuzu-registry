package org.zalando.pazuzu.feature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.pazuzu.exception.ServiceException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/features")
public class FeaturesResource {

    private final FeatureService featureService;

    @Autowired
    public FeaturesResource(FeatureService featureService) {
        this.featureService = featureService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FeatureDto> listFeatures(@RequestParam(required = false, defaultValue = "") String name) {
        return featureService.listFeatures(name, FeatureDto::ofShort);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FeatureFullDto> createFeature(@RequestBody FeatureToCreateDto value, UriComponentsBuilder uriBuilder) throws ServiceException {
        FeatureFullDto feature = featureService.createFeature(
                value.getName(), value.getDockerData(), value.getDependencies(), FeatureFullDto::makeFull);

        return ResponseEntity
                .created(uriBuilder.path("/api/features/{featureName}").buildAndExpand(feature.getName()).toUri())
                .body(feature);
    }

    @RequestMapping(value = "/{featureName}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public FeatureFullDto updateFeature(@PathVariable String featureName, FeatureToCreateDto value) throws ServiceException {
        return featureService.updateFeature(featureName, value.getName(), value.getDockerData(), value.getDependencies(), FeatureFullDto::makeFull);
    }

    @RequestMapping(value = "/{featureName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public FeatureFullDto getFeature(@PathVariable String featureName) throws ServiceException {
        return featureService.getFeature(featureName, FeatureFullDto::makeFull);
    }

    @RequestMapping(value = "/{featureName}", method = RequestMethod.DELETE)
    public void deleteFeature(@PathVariable String featureName) throws ServiceException {
        featureService.deleteFeature(featureName);
    }
}
