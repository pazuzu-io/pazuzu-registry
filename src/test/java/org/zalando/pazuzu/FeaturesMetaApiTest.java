package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.http.HttpMethod.GET;

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
    public void listFeaturesMetaShouldNotReturnAnErrorForUnmatchedNames() throws Exception {
        // given
        createNewFeature(4);
        // when
        String missingName = "feature-5";
        ResponseEntity<List<FeatureMetaDto>> result = template.exchange(url(featuresMetaUrl + "?name={name}"),
                GET, null, new ParameterizedTypeReference<List<FeatureMetaDto>>() {
                }, NAME + "4," + missingName);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().size()).isEqualTo(1);
        assertThat(result.getBody().stream().map(FeatureMetaDto::getName)).containsOnly(NAME + 4);
    }

    @Test
    public void listFeaturesShouldReturnsFeaturesThatMatchesTheNamePartially() throws Exception {
        // given
        createFeature(new FeatureDto("java"));
        createFeature(new FeatureDto("java-node"));
        createFeature(new FeatureDto("node"));
        createFeature(new FeatureDto("node-mongo"));
        createFeature(new FeatureDto("python"));
        // when
        ResponseEntity<List<FeatureMetaDto>> jaResult = template.exchange(url(featuresMetaUrl + "?name=ja"),
                GET, null, new ParameterizedTypeReference<List<FeatureMetaDto>>() {
                });
        // then
        assertThat(jaResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jaResult.getBody().size()).isEqualTo(2);
        assertThat(jaResult.getBody().stream().map(FeatureMetaDto::getName)).containsOnly("java", "java-node");
        // when
        ResponseEntity<List<FeatureMetaDto>> nodResult = template.exchange(url(featuresMetaUrl + "?name=nod"),
                GET, null, new ParameterizedTypeReference<List<FeatureMetaDto>>() {
                });
        // then
        assertThat(nodResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(nodResult.getBody().size()).isEqualTo(3);
        assertThat(nodResult.getBody().stream().map(FeatureMetaDto::getName))
                .containsOnly("node", "java-node", "node-mongo");
    }

    @Test
    public void listFeaturesShouldReturnsEmptyArrayIfNothingMatched() throws Exception {
        // given
        createFeature(new FeatureDto("java"));
        createFeature(new FeatureDto("java-node"));
        // when
        ResponseEntity<List<FeatureMetaDto>> result = template.exchange(url(featuresMetaUrl + "?name=rails"),
                GET, null, new ParameterizedTypeReference<List<FeatureMetaDto>>() {
                });
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().size()).isEqualTo(0);
    }

    @Test
    public void listFeaturesShouldReturnsFeaturesThatMatchesTheNameIgnoringTheCase() throws Exception {
        // given
        createFeature(new FeatureDto("java"));
        createFeature(new FeatureDto("java-node"));
        // when
        ResponseEntity<List<FeatureMetaDto>> jaResult = template.exchange(url(featuresMetaUrl + "?name=JaV"),
                GET, null, new ParameterizedTypeReference<List<FeatureMetaDto>>() {
                });
        // then
        assertThat(jaResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jaResult.getBody().size()).isEqualTo(2);
        assertThat(jaResult.getBody().stream().map(FeatureMetaDto::getName)).containsOnly("java", "java-node");
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
