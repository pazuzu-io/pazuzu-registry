package io.pazuzu.registry.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import io.pazuzu.registry.exception.FeatureNameEmptyException;
import io.pazuzu.registry.feature.FeatureService;
import io.pazuzu.registry.model.DependenciesList;
import io.pazuzu.registry.model.Feature;

/**
 * Created by hhueter on 20/01/2017.
 */
@Service
public class DependenciesServiceImpl {
    private final FeatureService featureService;

    @Autowired
    public DependenciesServiceImpl(FeatureService featureService) {
        this.featureService = featureService;
    }

    public ResponseEntity<DependenciesList> dependenciesGet(List<String> names) {
        List<Feature> features;
        if (names == null || names.isEmpty())
            throw new FeatureNameEmptyException();
        features = featureService
                .resolveFeatures(names)
                .stream().map(FeatureConverter::asDto).collect(Collectors.toList());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());

        DependenciesList ret = new DependenciesList();
        ret.setDependencies(features);
        ret.setRequestedFeatures(names);

        ResponseEntity<DependenciesList> entity = new ResponseEntity<DependenciesList>(ret, responseHeaders, HttpStatus.OK);
        return entity;
    }
}
