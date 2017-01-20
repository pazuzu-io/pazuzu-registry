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
import org.zalando.pazuzu.feature.FeatureService;
import org.zalando.pazuzu.feature.FeatureStatus;
import org.zalando.pazuzu.feature.FeaturesWithTotalCount;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureList;
import org.zalando.pazuzu.model.FeatureMeta;
import org.zalando.pazuzu.security.Roles;

import javax.annotation.security.RolesAllowed;
import java.net.URI;
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
    public static final int DEFAULT_OFFSET = 0;
    private static final Integer DEFAULT_LIMIT = 50;
    private final FeatureService featureService;



    @Autowired
    public FeatureServiceImpl(FeatureService featureService) {
        this.featureService = featureService;
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public ResponseEntity<FeatureList> featuresGet(@RequestParam(value = "q", required = false) String q,
                                                   @RequestParam(value = "author", required = false) String author,
                                                   @RequestParam(value = "fields", required = false) String fields,
                                                   @RequestParam(value = "status", required = false) String status,
                                                   @RequestParam(value = "offset", required = false) Integer offset,
                                                   @RequestParam(value = "limit", required = false) Integer limit) {
        if (offset == null) {
            offset = DEFAULT_OFFSET;
        }
        if (limit == null) {
            limit = DEFAULT_LIMIT;
        }
        if (q != null) {
            q =q.trim();
        }
        if (author != null) {
            author = author.trim();
        }
        //TODO add validation base on role.
        //TODO add limitation of author if non admin and asking for non approved feature
        FeatureStatus featureStatus = null;
        if (status == null) {
            featureStatus = FeatureStatus.APPROVED;
        } else {
            featureStatus = FeatureStatus.fromJsonValue(status);
        }
        FeatureFields featureFields = FeatureFields.getFields(fields);
        Function<org.zalando.pazuzu.feature.Feature, Feature> converter = FeatureConverter.forFields(featureFields);
        FeaturesWithTotalCount<Feature> featuresWithTotalCount =
                featureService.searchFeatures(q, author, featureStatus, offset, limit, converter);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        FeatureList ret = new FeatureList();
        ret.setFeatures(featuresWithTotalCount.getFeatures());
        ret.setTotalCount((int) featuresWithTotalCount.getTotalCount());
        //TODO add link.
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
                feature.getSnippet(), feature.getTestSnippet(), feature.getMeta().getDependencies(), FeatureConverter::asDto);
        URI uri = servletUriComponentsBuilder.path("/api/features/{featureName}").buildAndExpand(newFeature.getMeta().getName()).toUri();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        responseHeaders.setLocation(uri);
        ResponseEntity<Feature> entity = new ResponseEntity<Feature>(newFeature, responseHeaders, HttpStatus.CREATED);
        return entity;
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public ResponseEntity<Feature> featuresNameGet(@PathVariable("name") String name) {
        Feature feature = featureService.getFeature(name, FeatureConverter::asDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        ResponseEntity<Feature> entity = new ResponseEntity<Feature>(feature, responseHeaders, HttpStatus.OK);
        return entity;
    }

    @RolesAllowed({Roles.ADMIN})
    public ResponseEntity<Feature> featuresNamePut(@PathVariable("name") String name, @RequestBody Feature feature) {
        Feature featureDto = featureService.updateFeature(
                name, feature.getMeta().getName(), feature.getMeta().getDescription(), feature.getMeta().getAuthor(),
                feature.getSnippet(), feature.getTestSnippet(), feature.getMeta().getDependencies(),
                FeatureStatus.fromJsonValue(feature.getMeta().getStatus()), FeatureConverter::asDto);
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

}
