package org.zalando.pazuzu;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ResolvedFeatureApiTest extends AbstractComponentTest {

    @Test
    public void retrievingResolvedFeatureWithoutNameShouldResultInError() throws Exception {
        // when
        ResponseEntity<Object> result = template.getForEntity(url(resolvedFeaturesUrl), Object.class);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void retrievingResolvedFeatureShouldReturnFeaturesWithAllItsDependencies() throws Exception {
        createFeature(1);
        createFeature(2);
        createFeature(3, 1);
        createFeature(4, 3);
        createFeature(5, 2);
        ResponseEntity<List> result = template.getForEntity(url(resolvedFeaturesUrl + "?name={name}"),
                List.class, NAME + "1," + NAME + 4);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        List resolvedFeature = result.getBody();
        assertThat(resolvedFeature.size()).isEqualTo(3);
        List<String> featureNames = ((List<Object>) resolvedFeature).stream()
                .map(this::featureToName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        assertThat(featureNames).containsOnly(NAME + 1, NAME + 3, NAME + 4);

    }

    @Test
    public void retrievingResolvedFeaturesWithTheSameDependenciesShouldNotReturnDuplicates() throws Exception {
        createFeature(11);
        createFeature(12, 11);
        createFeature(13, 11);
        ResponseEntity<List> result = template.getForEntity(url(resolvedFeaturesUrl + "?name={name}"),
                List.class, NAME + "12," + NAME + 13);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> featureNames = ((List<Object>) result.getBody()).stream()
                .map(this::featureToName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        assertThat(featureNames.size()).isEqualTo(3);
        assertThat(featureNames).containsOnly(NAME + 11, NAME + 12, NAME + 13);

    }


    private Optional<String> featureToName(Object feature) {
        if (feature instanceof Map) {
            Object meta = ((Map) feature).get("meta");
            if (meta != null && meta instanceof Map)
                return Optional.ofNullable(((Map) meta).get("name").toString());
        }
        return Optional.empty();
    }

}
