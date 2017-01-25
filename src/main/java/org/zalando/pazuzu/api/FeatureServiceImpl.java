package org.zalando.pazuzu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.zalando.pazuzu.feature.FeatureService;
import org.zalando.pazuzu.feature.FeatureStatus;
import org.zalando.pazuzu.feature.FeaturesWithTotalCount;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureList;
import org.zalando.pazuzu.model.FeatureMeta;
import org.zalando.pazuzu.model.Review;
import org.zalando.pazuzu.security.Roles;

import javax.annotation.security.RolesAllowed;
import java.net.URI;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by hhueter on 16/01/2017.
 */
@Service
public class FeatureServiceImpl {
    public static final int DEFAULT_OFFSET = 0;
    private static final Integer DEFAULT_LIMIT = 50;
    private final FeatureService featureService;


    @Autowired
    public FeatureServiceImpl(FeatureService featureService) {
        this.featureService = featureService;
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public ResponseEntity<FeatureList> featuresGet(String q, String author, String fields, String status, Integer offset, Integer limit) {
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
                featureService.searchFeatures(sanitizeQuery(q), sanitizeQuery(author), featureStatus,
                        sanitizeOffset(offset), sanitizeLimit(limit), converter);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        FeatureList ret = new FeatureList();
        ret.setFeatures(featuresWithTotalCount.getFeatures());
        ret.setTotalCount((int) featuresWithTotalCount.getTotalCount());
        //TODO add link.
        ResponseEntity<FeatureList> entity = new ResponseEntity<FeatureList>(ret, responseHeaders, HttpStatus.OK);
        return entity;
    }

    private Integer sanitizeLimit(Integer limit) {
        return (limit == null ? DEFAULT_LIMIT: limit);
    }

    private Integer sanitizeOffset(Integer offset) {
        return (offset == null ? DEFAULT_OFFSET: offset);
    }

    private String sanitizeQuery(String q) {
        return (q == null ? null : q.trim());
    }


    @RolesAllowed({Roles.USER})
    public ResponseEntity<Feature> featuresPost(Feature feature) {
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

    @RolesAllowed({Roles.USER})
    public ResponseEntity<Review> featuresNameReviewsPost(String name, Review review) {
        org.zalando.pazuzu.feature.Feature feature = featureService.getFeature(name, t -> t);
        Review newReview = featureService.updateFeature(feature.getName(), feature.getName(),
                feature.getDescription(), feature.getAuthor(), feature.getSnippet(),
                feature.getTestSnippet(), feature.getDependencies().stream().map(f -> f.getName()).collect(Collectors.toList()),
                FeatureStatus.fromJsonValue(review.getReviewStatus().name()),
                FeatureConverter::asReviewDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        return new ResponseEntity<Review>(newReview, responseHeaders, HttpStatus.CREATED);
    }

    @RolesAllowed({Roles.ANONYMOUS, Roles.USER})
    public ResponseEntity<Feature> featuresNameGet(String name) {
        Feature feature = featureService.getFeature(name, FeatureConverter::asDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        ResponseEntity<Feature> entity = new ResponseEntity<Feature>(feature, responseHeaders, HttpStatus.OK);
        return entity;
    }
}
