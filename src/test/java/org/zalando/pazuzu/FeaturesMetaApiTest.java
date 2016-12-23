package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.springframework.http.*;
import org.zalando.pazuzu.feature.FeatureMetaDto;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class FeaturesMetaApiTest extends AbstractComponentTest {

    @Test
    public void retrievingFeaturesMetaShouldReturnEmptyListWhenNoFeaturesAreStored() throws Exception {
        // when
        ResponseEntity<List> result = template.getForEntity(url(featuresMetaUrl), List.class);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEmpty();
    }


    @Test
    public void returnsNotFoundWhenTryingToRetrieveNonExistingFeatureMeta() throws Exception {
        // when
        ResponseEntity<Map> result = template.getForEntity(url(featuresMetaUrl + "/non_existing"), Map.class);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).containsOnly(
                entry("type", "http://pazuzu.io/error/feature_not_found"),
                entry("title", "Feature was not found"),
                entry("detail", "Feature missing: non_existing"),
                entry("status", HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void returnsNotFoundWhenTryingToRetrieveMultipleNonExistingFeaturesMeta() throws Exception {
        // given
        createFeature(1);

        // when
        ResponseEntity<Map> result = template.getForEntity(url(featuresMetaUrl + "?name={name}"),
                Map.class, NAME + "1,feature-2");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).containsOnly(
                entry("type", "http://pazuzu.io/error/feature_not_found"),
                entry("title", "Feature was not found"),
                entry("detail", "Feature missing: feature-2"),
                entry("status", HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void testGetFeaturesMetaSuccess() throws JsonProcessingException {
        // given
        createFeature(2);
        createFeature(3);

        // when
        ResponseEntity<List> result = template.getForEntity(url(featuresMetaUrl), List.class);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(2);
    }

    @Test
    public void testGetFeatureMetaSuccess() throws JsonProcessingException {
        // given
        createFeature(5);

        // when
        ResponseEntity<FeatureMetaDto> result = template.getForEntity(url(featuresMetaUrl, NAME + 5), FeatureMetaDto.class);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        FeatureMetaDto resultMeta = result.getBody();
        assertThat(resultMeta).isEqualTo(new FeatureMetaDto().setName(NAME + 5).setDescription(DESCRIPTION + 5));
    }

}
