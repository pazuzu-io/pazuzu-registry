package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.springframework.http.*;
import org.zalando.pazuzu.exception.FeatureNotFoundException;
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
        createNewFeature(1);
        // when
        ResponseEntity<Map> result = template.getForEntity(url(featuresMetaUrl + "?name={name}"),
                Map.class, NAME + "1,feature-2");
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertEqualErrors(new FeatureNotFoundException("Feature missing: feature-2"), result.getBody());
    }

    @Test
    public void testGetFeaturesMetaSuccess() throws JsonProcessingException {
        // given
        createNewFeature(2);
        createNewFeature(3);
        // when
        ResponseEntity<List> result = template.getForEntity(url(featuresMetaUrl), List.class);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(2);
    }

    @Test
    public void testGetFeatureMetaSuccess() throws JsonProcessingException {
        // given
        int id = 5;
        createNewFeature(id);
        // when
        ResponseEntity<FeatureMetaDto> result = template.getForEntity(url(featuresMetaUrl, NAME + id), FeatureMetaDto.class);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        FeatureMetaDto resultMeta = result.getBody();
        assertThat(resultMeta).isEqualToIgnoringGivenFields(
                new FeatureMetaDto().setName(NAME + id).setDescription(DESCRIPTION + id).setAuthor(AUTHOR + id),
                "updatedAt"
        );
    }

}
