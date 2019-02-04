package io.pazuzu.registry.api;

import io.pazuzu.registry.feature.FeatureService;
import io.pazuzu.registry.feature.FeatureStatus;
import io.pazuzu.registry.feature.FeaturesPage;
import io.pazuzu.registry.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class FeatureApiService {
    public static final int DEFAULT_OFFSET = 0;
    private static final Integer DEFAULT_LIMIT = 50;
//    private final FeatureService featureService;


//    @Autowired
//    public FeatureApiService(FeatureService featureService) {
//        this.featureService = featureService;
//    }
//
//    public ResponseEntity<FeatureList> listFeatures(String q, String author, String fields, String status, Integer offset, Integer limit) {
//        //TODO add validation base on role.
//        //TODO add limitation of author if non admin and asking for non approved feature
//        FeatureStatus featureStatus = (status == null)
//                ? FeatureStatus.APPROVED
//                : FeatureStatus.fromJsonValue(status);
//        FeatureFields featureFields = FeatureFields.getFields(fields);
//        Function<io.pazuzu.registry.domain.Feature, Feature> converter = FeatureConverter.forFields(featureFields);
//        FeaturesPage<?, Feature> featuresPage =
//                featureService.searchFeatures(sanitizeQuery(q), sanitizeQuery(author), featureStatus,
//                        sanitizeOffset(offset), sanitizeLimit(limit), converter);
//
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
//        FeatureList ret = new FeatureList();
//        ret.setFeatures(featuresPage.getContent());
//        ret.setTotalCount((int) featuresPage.getTotalElements());
//        addLinks(ret, featuresPage, sanitizeOffset(offset), sanitizeLimit(limit));
//        return new ResponseEntity<>(ret, responseHeaders, HttpStatus.OK);
//    }
//
//
//    private Integer sanitizeLimit(Integer limit) {
//        return limit == null ? DEFAULT_LIMIT : limit;
//    }
//
//    private Integer sanitizeOffset(Integer offset) {
//        return offset == null ? DEFAULT_OFFSET : offset;
//    }
//
//    private String sanitizeQuery(String q) {
//        return q == null ? null : q.trim();
//    }
//
//    private void addLinks(FeatureList features, FeaturesPage<?, Feature> page, Integer offset, Integer limit) {
//        FeatureListLinks links = new FeatureListLinks();
//        URI relative = ServletUriComponentsBuilder.fromCurrentContextPath().replacePath("").build().toUri();
//        if (page.hasNext()) {
//            Link next = new Link();
//            next.setHref("/" + relative.relativize(ServletUriComponentsBuilder.fromCurrentRequest()
//                    .replaceQueryParam("offset", offset + limit)
//                    .build().toUri()).toString());
//            links.setNext(next);
//        }
//        if (page.hasPrevious()) {
//            Link previous = new Link();
//            previous.setHref("/" + relative.relativize(ServletUriComponentsBuilder.fromCurrentRequest()
//                    .replaceQueryParam("offset", offset - limit)
//                    .build().toUri()).toString());
//            links.setPrev(previous);
//        }
//        features.setLinks(links);
//    }
//
//    public ResponseEntity<Feature> createFeature(Feature feature) {
//        if (feature.getMeta() == null)
//            feature.setMeta(new FeatureMeta());
//        ServletUriComponentsBuilder servletUriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentContextPath();
//        Feature newFeature = featureService.createFeature(
//                feature.getMeta().getName(), feature.getMeta().getDescription(), getAuthenticatedUserName(),
//                feature.getSnippet(), feature.getTestSnippet(), feature.getMeta().getDependencies(), FeatureConverter::asDto);
//        URI uri = servletUriComponentsBuilder.path("/api/features/{featureName}").buildAndExpand(newFeature.getMeta().getName()).toUri();
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
//        responseHeaders.setLocation(uri);
//        return new ResponseEntity<>(newFeature, responseHeaders, HttpStatus.CREATED);
//    }
//
//    private String getAuthenticatedUserName() {
//        return SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null ?
//                SecurityContextHolder.getContext().getAuthentication().getName() : "anonymous";
//    }
//
//    public ResponseEntity<Review> reviewFeature(String name, Review review) {
//        io.pazuzu.registry.domain.Feature feature = featureService.getFeature(name, t -> t);
//        Review newReview = featureService.updateFeature(feature.getName(), feature.getName(),
//                feature.getDescription(), feature.getAuthor(), feature.getSnippet(),
//                feature.getTestSnippet(), feature.getDependencies().stream().map(io.pazuzu.registry.feature.Feature::getName).collect(Collectors.toList()),
//                FeatureStatus.fromJsonValue(review.getReviewStatus().name()),
//                FeatureConverter::asReviewDto);
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
//        return new ResponseEntity<>(newReview, responseHeaders, HttpStatus.CREATED);
//    }
//
//    public ResponseEntity<Feature> getFeature(String name) {
//        Feature feature = featureService.getFeature(name, FeatureConverter::asDto);
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
//        return new ResponseEntity<>(feature, responseHeaders, HttpStatus.OK);
//    }
}
