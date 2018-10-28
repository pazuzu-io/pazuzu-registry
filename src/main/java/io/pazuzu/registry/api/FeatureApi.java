package io.pazuzu.registry.api;

import io.pazuzu.registry.model.Feature;
import io.pazuzu.registry.model.FeatureList;
import io.pazuzu.registry.model.Review;
import org.springframework.http.ResponseEntity;

public interface FeatureApi {

    ResponseEntity<FeatureList> listFeatures(String q,
                                             String author,
                                             String fields,
                                             String status,
                                             Integer offset,
                                             Integer limit);

    ResponseEntity<Feature> getFeature(String name);

    ResponseEntity<Feature> createFeature(Feature feature);

    ResponseEntity<Review> reviewFeature(String name, Review review);
}
