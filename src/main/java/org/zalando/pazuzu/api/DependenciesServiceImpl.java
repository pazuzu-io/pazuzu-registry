package org.zalando.pazuzu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zalando.pazuzu.exception.FeatureNameEmptyException;
import org.zalando.pazuzu.feature.FeatureService;
import org.zalando.pazuzu.model.DependenciesList;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hhueter on 20/01/2017.
 */
@Service
public class DependenciesServiceImpl {
    private final FeatureService featureService;

    private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        }
    };

    @Autowired
    public DependenciesServiceImpl(FeatureService featureService) {
        this.featureService = featureService;
    }

    public ResponseEntity<DependenciesList> dependenciesGet(@RequestParam(value = "names") List<String> names) {
        List<Feature> features;
            if (names == null || names.isEmpty())
                throw new FeatureNameEmptyException();
            features = featureService
                    .resolveFeatures(names)
                    .stream().map(FeatureServiceImpl::asDto).collect(Collectors.toList());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());

        DependenciesList ret = new DependenciesList();
        ret.setDepedencies(features);
        ret.setRequestedFeatures(names);

        ResponseEntity<DependenciesList> entity = new ResponseEntity<DependenciesList>(ret, responseHeaders, HttpStatus.OK);
        return entity;
    }
}
