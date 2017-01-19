package org.zalando.pazuzu;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureList;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ResolvedFeatureApiTest extends AbstractComponentTest {

    @Test
    public void retrievingResolvedFeatureWithoutNameShouldResultInError() throws Exception {
        // when
        ResponseEntity<Object> result = template.getForEntity(url(featuresUrl + "?resolve=true"), Object.class);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void retrievingResolvedFeatureWithNonExistingNameShouldResultInError() throws Exception {
        // when
        ResponseEntity<Map> result = template.getForEntity(url(featuresUrl + "?resolve=true&names={name}"),
                Map.class, NAME + 1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).containsOnly(
                entry("type", "http://pazuzu.io/error/feature_not_found"),
                entry("title", "Feature was not found"),
                entry("detail", "Feature missing: " + NAME + 1),
                entry("status", HttpStatus.NOT_FOUND.value()));
    }


    @Test
    public void retrievingResolvedFeatureShouldReturnFeaturesWithAllItsDependencies() throws Exception {
        createNewFeature(1);
        createNewFeature(2);
        createNewFeature(3, 1);
        createNewFeature(4, 3);
        createNewFeature(5, 2);
        ResponseEntity<FeatureList> result = template.getForEntity(url(featuresUrl + "?resolve=true&names={name}"),
                FeatureList.class, NAME + "1," + NAME + 4);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Feature> resolvedFeature = result.getBody().getFeatures();
        assertThat(resolvedFeature.size()).isEqualTo(3);
        List<String> featureNames = (resolvedFeature).stream()
                .map(this::featureToName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        assertThat(featureNames).containsOnly(NAME + 1, NAME + 3, NAME + 4);

    }

    @Test
    public void retrievingResolvedFeaturesWithTheSameDependenciesShouldNotReturnDuplicates() throws Exception {
        createNewFeature(11);
        createNewFeature(12, 11);
        createNewFeature(13, 11);
        ResponseEntity<FeatureList> result = template.getForEntity(url(featuresUrl + "?resolve=true&names={name}"),
                FeatureList.class, NAME + "12," + NAME + 13);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> featureNames = (result.getBody().getFeatures()).stream()
                .map(this::featureToName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        assertThat(featureNames.size()).isEqualTo(3);
        assertThat(featureNames).containsOnly(NAME + 11, NAME + 12, NAME + 13);

    }


    private Optional<String> featureToName(Feature feature) {
        if (feature.getMeta() != null)
            return Optional.of(feature.getMeta().getName());
        return Optional.empty();
    }

}
