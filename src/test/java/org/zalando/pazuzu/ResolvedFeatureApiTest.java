package org.zalando.pazuzu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.pazuzu.model.DependenciesList;
import org.zalando.pazuzu.model.Feature;

public class ResolvedFeatureApiTest extends AbstractComponentTest {

    @Test
    public void retrievingResolvedFeatureWithoutNameShouldResultInError() throws Exception {
        // when
        ResponseEntity<Object> result = template.getForEntity(url(resolvedFeaturesUrl), Object.class);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void retrievingResolvedFeatureWithNonExistingNameShouldResultInError() throws Exception {
        // when
        ResponseEntity<Map<String, Object>> result = template.exchange(url(resolvedFeaturesUrl + "?names={name}"),
                HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
                }, NAME + 1);
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
        ResponseEntity<DependenciesList> result = template.getForEntity(url(resolvedFeaturesUrl + "?names={name}"),
                DependenciesList.class, NAME + "1," + NAME + 4);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Feature> resolvedFeature = result.getBody().getDependencies();
        assertThat(resolvedFeature.size()).isEqualTo(3);
        List<String> featureNames = (resolvedFeature).stream()
                .map(this::featureToName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        assertThat(featureNames).containsOnly(NAME + 1 + ":1", NAME + 3 + ":1", NAME + 4 + ":1");

    }

    @Test
    public void retrievingResolvedFeaturesWithTheSameDependenciesShouldNotReturnDuplicates() throws Exception {
        createNewFeature(11);
        createNewFeature(12, 11);
        createNewFeature(13, 11);
        ResponseEntity<DependenciesList> result = template.getForEntity(url(resolvedFeaturesUrl + "?names={name}"),
                DependenciesList.class, NAME + "12," + NAME + 13);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> featureNames = (result.getBody().getDependencies()).stream()
                .map(this::featureToName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        assertThat(featureNames.size()).isEqualTo(3);
        assertThat(featureNames).containsOnly(NAME + 11 + ":1", NAME + 12 + ":1", NAME + 13 + ":1");

    }

    @Test
    public void retrievingResolvedFeaturesShouldIgnoreCase() throws Exception {
        createNewFeature(11);
        ResponseEntity<DependenciesList> result = template.getForEntity(url(resolvedFeaturesUrl + "?names={name}"),
                DependenciesList.class, NAME.toUpperCase() + "11");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> featureNames = (result.getBody().getDependencies()).stream()
                .map(this::featureToName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        assertThat(featureNames.size()).isEqualTo(1);
        assertThat(featureNames).containsOnly(NAME + 11 + ":1");

    }

    private Optional<String> featureToName(Feature feature) {
        if (feature.getMeta() != null)
            return Optional.of(feature.getMeta().getName());
        return Optional.empty();
    }

}
