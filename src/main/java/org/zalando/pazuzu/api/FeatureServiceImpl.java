package org.zalando.pazuzu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.zalando.pazuzu.exception.FeatureNotFoundException;
import org.zalando.pazuzu.feature.FeatureService;
import org.zalando.pazuzu.feature.FeaturesWithTotalCount;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureMeta;
import org.zalando.pazuzu.security.Roles;

import javax.annotation.security.RolesAllowed;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    public ResponseEntity<List<Feature>> apiFeaturesGet(List<String> names, Integer offset, Integer limit) {
        List<Feature> features = listFeatures(names, offset, limit, FeatureServiceImpl::asDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        ResponseEntity<List<Feature>> entity = new ResponseEntity<List<Feature>>(features, responseHeaders, HttpStatus.OK);
        return entity;
    }

    @RolesAllowed({Roles.USER})
    public ResponseEntity<Feature> apiFeaturesPost(Feature feature) {
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
    public ResponseEntity<Feature> apiFeaturesNameGet(String name) {
        Feature feature = featureService.getFeature(name, FeatureServiceImpl::asDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        ResponseEntity<Feature> entity = new ResponseEntity<Feature>(feature, responseHeaders, HttpStatus.OK);
        return entity;
    }

    @RolesAllowed({Roles.ADMIN})
    public ResponseEntity<Feature> apiFeaturesNamePut(String name, Feature feature) {
        Feature featureDto = featureService.updateFeature(
                name, feature.getMeta().getName(), feature.getMeta().getDescription(), feature.getMeta().getAuthor(),
                feature.getSnippet(), feature.getTestSnippet(), feature.getMeta().getDependencies(), FeatureServiceImpl::asDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        ResponseEntity<Feature> entity = new ResponseEntity<Feature>(featureDto, responseHeaders, HttpStatus.OK);
        return entity;
    }

    @RolesAllowed({Roles.ADMIN})
    public ResponseEntity<Void> apiFeaturesNameDelete(String name) {
        featureService.deleteFeature(name);
        return ResponseEntity.noContent().build();
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public ResponseEntity<List<FeatureMeta>> apiFeatureMetasGet(List<String> name, Integer offset, Integer limit) {
        List<FeatureMeta> features = listFeatures(name, offset, limit, FeatureServiceImpl::asMetaDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        ResponseEntity<List<FeatureMeta>> entity = new ResponseEntity<List<FeatureMeta>>(features, responseHeaders, HttpStatus.OK);
        return entity;
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public ResponseEntity<FeatureMeta> apiFeatureMetasNameGet(String name) {
        FeatureMeta feature = featureService.getFeature(name, FeatureServiceImpl::asMetaDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        ResponseEntity<FeatureMeta> entity = new ResponseEntity<FeatureMeta>(feature, responseHeaders, HttpStatus.OK);
        return entity;
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public ResponseEntity<List<Feature>> apiResolvedFeaturesGet(List<String> names) {
        if (names == null || names.isEmpty())
            throw new FeatureNotFoundException();
        List<Feature> features = featureService
                .resolveFeatures(names)
                .stream().map(FeatureServiceImpl::asDto).collect(Collectors.toList());
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        ResponseEntity<List<Feature>> entity = new ResponseEntity<List<Feature>>(features, responseHeaders, HttpStatus.OK);
        return entity;
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

    private static Feature asDto(org.zalando.pazuzu.feature.Feature feature) {
        Feature dto = new Feature();
        dto.setMeta(asMetaDto(feature));
        dto.setSnippet(feature.getSnippet());
        dto.setTestSnippet(feature.getTestSnippet());
        return dto;
    }

    private static FeatureMeta asMetaDto(org.zalando.pazuzu.feature.Feature feature) {
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
