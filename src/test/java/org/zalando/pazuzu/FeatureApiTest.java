package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.springframework.http.*;
import org.zalando.pazuzu.feature.FeatureDto;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpMethod.PUT;

public class FeatureApiTest extends AbstractComponentTest {

    @Test
    public void retrievingFeaturesShouldReturnEmptyListWhenNoFeaturesAreStored() throws Exception {
        // when
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl), List.class);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEmpty();
    }

    @Test
    public void createFeatureShouldReturnCreatedFeature() throws Exception {
        // when
        int id = 1;
        ResponseEntity<FeatureDto> result = createFeature(id);
        // then
        assertFeatureEquals(id, result.getBody());
    }

    private void assertFeatureEquals(int id, FeatureDto result) {

        assertThat(result.getMeta().getName()).isEqualTo(NAME + id);
        assertThat(result.getMeta().getDescription()).isEqualTo(DESCRIPTION + id);
        assertThat(result.getSnippet()).isEqualTo(SNIPPET + id);
        assertThat(result.getTestSnippet()).isEqualTo(TEST_SNIPPET + id);
        assertThat(result.getMeta().getDependencies()).isEmpty();
    }

    @Test
    public void createFeatureShouldFailOnWrongNameNull() throws Exception {
        // when
        final ResponseEntity<Map> error = createFeatureUnchecked(Map.class, null, null, null, null);
        // then
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error.getBody()).containsOnly(
                entry("type", "http://pazuzu.io/error/feature_name_empty"),
                entry("title", "Feature name is empty"),
                entry("status", HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void createFeatureShouldFailOnWrongNameEmpty() throws Exception {
        // when
        final ResponseEntity<Map> error = createFeatureUnchecked(Map.class, "", null, null, null);

        // then
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error.getBody()).containsOnly(
                entry("type", "http://pazuzu.io/error/feature_name_empty"),
                entry("title", "Feature name is empty"),
                entry("status", HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void badRequestForInvalidJson() {
        // when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map> error = template.postForEntity(url(featuresUrl), new HttpEntity<>("{json crap}", headers), Map.class);

        // then
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error.getBody())
                .containsOnlyKeys("title", "status", "detail")
                .contains(entry("title", "Bad Request"),
                        entry("status", HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void createdFeatureShouldBeRetrievableAfterwards() throws Exception {
        // given
        ResponseEntity<FeatureDto> createdResult = createFeature(2);

        // when
        ResponseEntity<FeatureDto> result = template.getForEntity(createdResult.getHeaders().getLocation(), FeatureDto.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        // then
        assertFeatureEquals(2, result.getBody());
    }

    @Test
    public void testShouldFailOnDuplicateFeatureCreation() throws Exception {
        // given
        createFeature(3);

        // when
        ResponseEntity<Map> secondCreationResult = createFeatureUnchecked(Map.class, NAME + 3, DESCRIPTION + 3, SNIPPET + 3, TEST_SNIPPET + 3);
        assertThat(secondCreationResult.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // then
        assertThat(secondCreationResult.getBody())
                .containsOnlyKeys("type", "title", "status", "detail")
                .containsOnly(
                        entry("type", "http://pazuzu.io/error/feature_duplicate"),
                        entry("title", "Feature with this name already exists"),
                        entry("detail", "Feature with name feature-3 already exists"),
                        entry("status", HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void returnsNotFoundWhenTryingToRetrieveNonExistingFeature() throws Exception {
        // when
        ResponseEntity<Map> result = template.getForEntity(url("/api/features/non_existing"), Map.class);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).containsOnly(
                entry("type", "http://pazuzu.io/error/feature_not_found"),
                entry("title", "Feature was not found"),
                entry("detail", "Feature missing: non_existing"),
                entry("status", HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void returnsNotFoundWhenTryingToRetrieveMultipleNonExistingFeatures() throws Exception {
        // given
        createFeature(4);

        // when
        ResponseEntity<Map> result = template.getForEntity(url(featuresUrl + "?names={name}"),
                Map.class, NAME + "4,feature-5");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).containsOnly(
                entry("type", "http://pazuzu.io/error/feature_not_found"),
                entry("title", "Feature was not found"),
                entry("detail", "Feature missing: feature-5"),
                entry("status", HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void deleteFeatureProperly() throws JsonProcessingException {
        // given
        createFeature(5);

        // when
        ResponseEntity<Void> response = template.exchange(url(featuresUrl + "/" + NAME + 5), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl), List.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isEmpty();
    }

    @Test
    public void updateFeature() throws JsonProcessingException {
        // given
        createFeature(6);
        createFeature(7);
        createFeature(NAME + 8, null, null, null);

        // when
        final Map<String, Object> updateRequest = getFeaturePropertiesMap(null, DESCRIPTION + 8, SNIPPET + 8, TEST_SNIPPET + 8, NAME + 6, NAME + 7);
        ResponseEntity<FeatureDto> putResponse = template.exchange(url(featuresUrl + "/" + NAME + 8), PUT,
                new HttpEntity<>(mapper.writeValueAsString(updateRequest), contentType(MediaType.APPLICATION_JSON)), FeatureDto.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // then
        FeatureDto expectedResponse = new FeatureDto();
        expectedResponse.setSnippet(SNIPPET + 8);
        expectedResponse.setTestSnippet(TEST_SNIPPET + 8);
        expectedResponse.getMeta().setDescription(DESCRIPTION + 8);
        expectedResponse.getMeta().setName(NAME + 8);
        expectedResponse.getMeta().getDependencies().add(NAME + 6);
        expectedResponse.getMeta().getDependencies().add(NAME + 7);

        assertThat(putResponse.getBody()).isEqualToIgnoringGivenFields(expectedResponse, "meta");
        assertThat(putResponse.getBody().getMeta()).isEqualToIgnoringGivenFields(expectedResponse.getMeta(), "updatedAt");

        ResponseEntity<FeatureDto> getResponse = template.getForEntity(url(featuresUrl + "/" + NAME + 8), FeatureDto.class);
        assertThat(putResponse.getBody()).isEqualToIgnoringGivenFields(expectedResponse, "meta");
        assertThat(putResponse.getBody().getMeta()).isEqualToIgnoringGivenFields(expectedResponse.getMeta(), "updatedAt");
    }

    @Test
    public void notFoundWhenDeletingNotExistingFeature() throws JsonProcessingException {
        /// when
        ResponseEntity<Map> response = template.exchange(url(featuresUrl + "/NotExistingFeature"), HttpMethod.DELETE, HttpEntity.EMPTY, Map.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsOnly(
                entry("type", "http://pazuzu.io/error/feature_not_found"),
                entry("title", "Feature was not found"),
                entry("detail", "Feature missing: NotExistingFeature"),
                entry("status", HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void badRequestWhenDeletingStillReferencedFeature() throws JsonProcessingException {
        // given
        createFeature(9);
        createFeature(10, 9);

        // when
        ResponseEntity<Map> response = template.exchange(url(featuresUrl + "/" + NAME + 9), HttpMethod.DELETE, HttpEntity.EMPTY, Map.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsOnly(
                entry("type", "http://pazuzu.io/error/feature_not_deletable_due_to_references"),
                entry("title", "Can't delete feature because it still has references"),
                entry("detail", "Can't delete feature because it is referenced from other feature(s): feature-10"),
                entry("status", HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void testGetSortedFeaturesSuccess() throws JsonProcessingException {
        // given
        createFeature(11);
        createFeature(12);
        createFeature(13, 12);

        // when
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl), List.class, "sorted", 1, "name", NAME + 11 + "," + NAME + 12);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(3);
    }

    private FeatureDto findFeatureByName(List<FeatureDto> collection, String name) {
        return collection.stream().filter(f -> name.equals(f.getMeta().getName())).findFirst().get();
    }
}
