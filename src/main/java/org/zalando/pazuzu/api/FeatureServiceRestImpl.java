package org.zalando.pazuzu.api;

import io.swagger.annotations.ApiParam;
import io.swagger.api.ApiApi;
import io.swagger.api.NotFoundException;
import io.swagger.model.Feature;
import io.swagger.model.FeatureMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.zalando.pazuzu.exception.FeatureNotFoundException;
import org.zalando.pazuzu.feature.FeatureService;
import org.zalando.pazuzu.feature.FeaturesWithTotalCount;
import org.zalando.pazuzu.security.Roles;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by hhueter on 16/01/2017.
 */
@RestController
public class FeatureServiceRestImpl implements ApiApi {
    private static final String X_TOTAL_COUNT = "X-Total-Count";
    private final FeatureService featureService;

    private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        }
    };

    @Autowired
    public FeatureServiceRestImpl(FeatureService featureService) {
        this.featureService = featureService;
    }

    @Override
    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public Callable<ResponseEntity<List<Feature>>> apiFeaturesGet(@ApiParam(value = "value, that must present in feature name.") @RequestParam(value = "names", required = false) List<String> names, @ApiParam(value = "the offset to start from.") @RequestParam(value = "offset", required = false) Integer offset, @ApiParam(value = "maximum number of features to return.") @RequestParam(value = "limit", required = false) Integer limit) throws NotFoundException {
        return () -> {
            List<Feature> features = listFeatures(names, offset, limit, FeatureServiceRestImpl::asDto);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
            ResponseEntity<List<Feature>> entity = new ResponseEntity<List<Feature>>(features, responseHeaders, HttpStatus.OK);
            return entity;
        };
    }

    @Override
    @RolesAllowed({Roles.USER})
    public Callable<ResponseEntity<Feature>> apiFeaturesPost(@ApiParam(value = "", required = true) @RequestBody Feature feature) throws NotFoundException {
        ServletUriComponentsBuilder servletUriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentContextPath();
        return () -> {
            Feature newFeature = featureService.createFeature(
                    feature.getMeta().getName(), feature.getMeta().getDescription(), feature.getMeta().getAuthor(),
                    feature.getSnippet(), feature.getTestSnippet(), feature.getMeta().getDependencies(), FeatureServiceRestImpl::asDto);
            URI uri = servletUriComponentsBuilder.path("/api/features/{featureName}").buildAndExpand(newFeature.getMeta().getName()).toUri();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
            responseHeaders.setLocation(uri);
            ResponseEntity<Feature> entity = new ResponseEntity<Feature>(newFeature, responseHeaders, HttpStatus.CREATED);
            return entity;
        };
    }

    @Override
    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public Callable<ResponseEntity<Feature>> apiFeaturesNameGet(@ApiParam(value = "the feature name.", required = true) @PathVariable("name") String name) throws NotFoundException {
        return () -> {
            Feature feature = featureService.getFeature(name, FeatureServiceRestImpl::asDto);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
            ResponseEntity<Feature> entity = new ResponseEntity<Feature>(feature, responseHeaders, HttpStatus.OK);
            return entity;
        };
    }

    @Override
    @RolesAllowed({Roles.ADMIN})
    public Callable<ResponseEntity<Feature>> apiFeaturesNamePut(@ApiParam(value = "the feature name.", required = true) @PathVariable("name") String name, @ApiParam(value = "", required = true) @RequestBody Feature feature) throws NotFoundException {
        return () -> {
            Feature featureDto = featureService.updateFeature(
                    name, feature.getMeta().getName(), feature.getMeta().getDescription(), feature.getMeta().getAuthor(),
                    feature.getSnippet(), feature.getTestSnippet(), feature.getMeta().getDependencies(), FeatureServiceRestImpl::asDto);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
            ResponseEntity<Feature> entity = new ResponseEntity<Feature>(featureDto, responseHeaders, HttpStatus.OK);
            return entity;
        };
    }

    @Override
    @RolesAllowed({Roles.ADMIN})
    public Callable<ResponseEntity<Void>> apiFeaturesNameDelete(@ApiParam(value = "the feature name.", required = true) @PathVariable("name") String name) throws NotFoundException {
        return () -> {
            featureService.deleteFeature(name);
            return ResponseEntity.noContent().build();
        };
    }

    @Override
    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public Callable<ResponseEntity<List<FeatureMeta>>> apiFeatureMetasGet(@ApiParam(value = "value, that must present in feature name.") @RequestParam(value = "name", required = false) List<String> name, @ApiParam(value = "the offset to start from.") @RequestParam(value = "offset", required = false) Integer offset, @ApiParam(value = "maximum number of features to return.") @RequestParam(value = "limit", required = false) Integer limit) throws NotFoundException {
        return () -> {
            List<FeatureMeta> features = listFeatures(name, offset, limit, FeatureServiceRestImpl::asMetaDto);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
            ResponseEntity<List<FeatureMeta>> entity = new ResponseEntity<List<FeatureMeta>>(features, responseHeaders, HttpStatus.OK);
            return entity;
        };
    }

    @Override
    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public Callable<ResponseEntity<FeatureMeta>> apiFeatureMetasNameGet(@ApiParam(value = "the feature name.", required = true) @PathVariable("name") String name) throws NotFoundException {
        return () -> {
            FeatureMeta feature = featureService.getFeature(name, FeatureServiceRestImpl::asMetaDto);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
            ResponseEntity<FeatureMeta> entity = new ResponseEntity<FeatureMeta>(feature, responseHeaders, HttpStatus.OK);
            return entity;
        };
    }

    @Override
    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public Callable<ResponseEntity<List<Feature>>> apiResolvedFeaturesGet(@ApiParam(value = "feature names.", required = true) @RequestParam(value = "names", required = true) List<String> names) throws NotFoundException {
        return () -> {
            if (names == null || names.isEmpty())
                throw new FeatureNotFoundException();
            List<Feature> features = featureService
                    .resolveFeatures(names)
                    .stream().map(FeatureServiceRestImpl::asDto).collect(Collectors.toList());
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
            ResponseEntity<List<Feature>> entity = new ResponseEntity<List<Feature>>(features, responseHeaders, HttpStatus.OK);
            return entity;
        };
    }

    private <T> Callable<T> withTransaction(Callable<T> callable) {
        return () -> callWithTransaction(callable);
    }

    @Transactional(rollbackOn = Throwable.class)
    private <T> T callWithTransaction(Callable<T> callable) throws Exception {
        return callable.call();
    }

    private <T> List<T> listFeatures(@ApiParam(value = "value, that must present in feature name.") @RequestParam(value = "names", required = false) List<String> names, @ApiParam(value = "the offset to start from.") @RequestParam(value = "offset", required = false) Integer offset, @ApiParam(value = "maximum number of features to return.") @RequestParam(value = "limit", required = false) Integer limit,
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
