package org.zalando.pazuzu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.zalando.pazuzu.exception.FeatureNameEmptyException;
import org.zalando.pazuzu.exception.FeatureNotFoundException;
import org.zalando.pazuzu.feature.FeatureService;
import org.zalando.pazuzu.feature.FeaturesWithTotalCount;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureList;
import org.zalando.pazuzu.model.FeatureMeta;
import org.zalando.pazuzu.security.Roles;

import javax.annotation.security.RolesAllowed;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by hhueter on 16/01/2017.
 */
@Service
public class FeatureServiceImpl {
    private static final String X_TOTAL_COUNT = "X-Total-Count";
    private final FeatureService featureService;

    private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        }
    };

    @Autowired
    public FeatureServiceImpl(FeatureService featureService) {
        this.featureService = featureService;
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public ResponseEntity<FeatureList> featuresGet(@RequestParam(value = "q", required = false) String q, @RequestParam(value = "author", required = false) String author, @RequestParam(value = "fields", required = false) String fields, @RequestParam(value = "status", required = false) String status, @RequestParam(value = "offset", required = false) Integer offset, @RequestParam(value = "limit", required = false) Integer limit) {
        List<Feature> features;
        if (q != null && q.trim().length() > 0)
            features = listFeatures(Collections.singletonList(q), offset, limit, FeatureServiceImpl::asDto);
        else
            features = listFeatures(Collections.emptyList(), offset, limit, FeatureServiceImpl::asDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        FeatureList ret = new FeatureList();
        ret.setFeatures(features);
        ResponseEntity<FeatureList> entity = new ResponseEntity<FeatureList>(ret, responseHeaders, HttpStatus.OK);
        return entity;
    }

    @RolesAllowed({Roles.USER})
    public ResponseEntity<Feature> featuresPost(@RequestBody Feature feature) {
        if (feature.getMeta() == null)
            feature.setMeta(new FeatureMeta());
        ServletUriComponentsBuilder servletUriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentContextPath();
        Feature newFeature = featureService.createFeature(
                feature.getMeta().getName(), feature.getMeta().getDescription(), feature.getMeta().getAuthor(),
                feature.getSnippet(), feature.getTestSnippet(), feature.getMeta().getDependencies(), FeatureServiceImpl::asDto);
        URI uri = servletUriComponentsBuilder.path("/api/features/{featureName}").buildAndExpand(newFeature.getMeta().getName()).toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        responseHeaders.setLocation(uri);
        ResponseEntity<Feature> entity = new ResponseEntity<Feature>(newFeature, responseHeaders, HttpStatus.CREATED);
        return entity;
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public ResponseEntity<Feature> featuresNameGet(@PathVariable("name") String name) {
        Feature feature = featureService.getFeature(name, FeatureServiceImpl::asDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        ResponseEntity<Feature> entity = new ResponseEntity<Feature>(feature, responseHeaders, HttpStatus.OK);
        return entity;
    }

    @RolesAllowed({Roles.ADMIN})
    public ResponseEntity<Feature> featuresNamePut(@PathVariable("name") String name, @RequestBody Feature feature) {
        Feature featureDto = featureService.updateFeature(
                name, feature.getMeta().getName(), feature.getMeta().getDescription(), feature.getMeta().getAuthor(),
                feature.getSnippet(), feature.getTestSnippet(), feature.getMeta().getDependencies(), FeatureServiceImpl::asDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        ResponseEntity<Feature> entity = new ResponseEntity<Feature>(featureDto, responseHeaders, HttpStatus.OK);
        return entity;
    }

    @RolesAllowed({Roles.ADMIN})
    public ResponseEntity<Void> featuresNameDelete(@PathVariable("name") String name) {
        featureService.deleteFeature(name);
        return ResponseEntity.noContent().build();
    }

    private <T> List<T> listFeatures(List<String> names, Integer offset, Integer limit,
                                     Function<org.zalando.pazuzu.feature.Feature, T> converter) {
        List<T> features;
        if (names == null || names.isEmpty()) {
            if (offset != null && limit != null) {
                FeaturesWithTotalCount<T> featuresTotalCount =
                        featureService.getFeaturesWithTotalCount(offset, limit, converter);
                features = featuresTotalCount.getFeatures();
            } else {
                features = featureService.listFeatures("", converter);
            }
        } else {
            features = featureService.searchFeatures(
                    names.stream().map(s -> s.split(",")).flatMap(Arrays::stream).collect(Collectors.toList()),
                    converter);
        }
        return features;
    }

    static Feature asDto(org.zalando.pazuzu.feature.Feature feature) {
        Feature dto = new Feature();
        dto.setMeta(asMetaDto(feature));
        dto.setSnippet(feature.getSnippet());
        dto.setTestSnippet(feature.getTestSnippet());
        return dto;
    }

    static FeatureMeta asMetaDto(org.zalando.pazuzu.feature.Feature feature) {
        FeatureMeta dto = new FeatureMeta();
        dto.setName(feature.getName());
        dto.setDescription(feature.getDescription());
        dto.setAuthor(feature.getAuthor());
        dto.setUpdatedAt(dateFormat.get().format(feature.getUpdatedAt()));
        dto.setDependencies(
                feature.getDependencies().stream().map(
                        org.zalando.pazuzu.feature.Feature::getName).collect(Collectors.toList()));
        return dto;
    }
}
