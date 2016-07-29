package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.zalando.pazuzu.feature.FeatureDto;
import org.zalando.pazuzu.feature.FeatureFullDto;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpMethod.GET;

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
        ResponseEntity<FeatureFullDto> result = createFeature("Test", "Test Data", "test something", "The test feature");

        // then
        FeatureFullDto resultFeature = result.getBody();
        assertThat(resultFeature.getName()).isEqualTo("Test");
        assertThat(resultFeature.getTestInstruction()).isEqualTo("test something");
        assertThat(resultFeature.getDescription()).isEqualTo("The test feature");
        assertThat(resultFeature.getDockerData()).isEqualTo("Test Data");
        assertThat(resultFeature.getDependencies()).isEmpty();
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
        ResponseEntity<FeatureFullDto> createdResult = createFeature("Test 2", "Test Data 2", "something to test2", "Test 2 desc");

        // when
        ResponseEntity<FeatureFullDto> result = template.getForEntity(createdResult.getHeaders().getLocation(), FeatureFullDto.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        // then
        FeatureFullDto resultFeature = result.getBody();
        assertThat(resultFeature.getName()).isEqualTo("Test 2");
        assertThat(resultFeature.getDockerData()).isEqualTo("Test Data 2");
        assertThat(resultFeature.getTestInstruction()).isEqualTo("something to test2");
        assertThat(resultFeature.getDescription()).isEqualTo("Test 2 desc");
        assertThat(resultFeature.getDependencies()).isEmpty();
        assertThat(resultFeature.isApproved()).isFalse();
    }

    @Test
    public void testShouldFailOnDuplicateFeatureCreation() throws Exception {
        // given
        createFeature("Test 1000", "Test Data 2", "something to test2", "Test 2 desc");

        // when
        ResponseEntity<Map> secondCreationResult = createFeatureUnchecked(Map.class, "Test 1000", "test", "test", "test");
        assertThat(secondCreationResult.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // then
        assertThat(secondCreationResult.getBody())
                .containsOnlyKeys("type","title", "status", "detail")
                .containsOnly(
                        entry("type", "http://pazuzu.io/error/feature_duplicate"),
                        entry("title", "Feature with this name already exists"),
                        entry("detail", "Feature with name Test 1000 already exists"),
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
        createFeature("FeatureA", "Test Data 2", "something to test2", "Test 2 desc");

        // when
        ResponseEntity<Map> result = template.getForEntity(url(featuresUrl + "?name={name}"),
                Map.class, "FeatureA,FeatureB");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).containsOnly(
                   entry("type", "http://pazuzu.io/error/feature_not_found"),
                   entry("title", "Feature was not found"),
                   entry("detail", "Feature missing: FeatureB"),
                   entry("status", HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void deleteFeatureProperly() throws JsonProcessingException {
        // given
        createFeature("Feature", "some data", "something to test2", "Feature desc");

        // when
        ResponseEntity<Void> response = template.exchange(url(featuresUrl + "/Feature"), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl), List.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isEmpty();
    }

    @Test
    public void updateFeature() throws JsonProcessingException {
        // given
        createFeature("Feature1", "dockerData Feature1", "testInstruction Feature1", "description Feature1");
        createFeature("Feature2", "dockerData Feature2", "testInstruction Feature2", "description Feature2");
        createFeature("Feature3", null, null, null);

        // when
        final Map<String, Object> updateRequest = getFeaturePropertiesMap(null, "dockerData Feature3", "testInstruction Feature3", "description Feature3", "Feature1", "Feature2");
        ResponseEntity<FeatureFullDto> putResponse = template.exchange(url(featuresUrl + "/Feature3"), PUT,
                new HttpEntity<>(mapper.writeValueAsString(updateRequest), contentType(MediaType.APPLICATION_JSON)), FeatureFullDto.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // then
        FeatureFullDto expectedResponse = new FeatureFullDto();
        expectedResponse.setDockerData("dockerData Feature3");
        expectedResponse.setTestInstruction("testInstruction Feature3");
        expectedResponse.setDescription("description Feature3");
        expectedResponse.setName("Feature3");
        expectedResponse.getDependencies().add(FeatureDto.populate("Feature1", "dockerData Feature1", "testInstruction Feature1", "description Feature1", true));
        expectedResponse.getDependencies().add(FeatureDto.populate("Feature2", "dockerData Feature2", "testInstruction Feature2", "description Feature2", true));

        assertThat(putResponse.getBody()).isEqualToComparingFieldByField(expectedResponse);

        ResponseEntity<FeatureFullDto> getResponse = template.getForEntity(url(featuresUrl + "/Feature3"), FeatureFullDto.class);
        assertThat(getResponse.getBody()).isEqualToComparingFieldByField(expectedResponse);
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
        createFeature("Feature", "some data", "something to test2", "Feature desc");
        createFeature("FeatureWithDependency", "some data", "something to test2", "Feature desc", "Feature");

        // when
        ResponseEntity<Map> response = template.exchange(url(featuresUrl + "/Feature"), HttpMethod.DELETE, HttpEntity.EMPTY, Map.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsOnly(
                           entry("type", "http://pazuzu.io/error/feature_not_deletable_due_to_references"),
                           entry("title", "Can't delete feature because it still has references"),
                           entry("detail", "Can't delete feature because it is referenced from other feature(s): FeatureWithDependency"),
                           entry("status", HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void testGetSortedFeaturesSuccess() throws JsonProcessingException {
        // given
        createFeature("test-feature-1", "docker-data-1", "test-instruction-1", "desc-1");
        createFeature("test-feature-2", "docker-data-2", "test-instruction-2", "desc-2");
        createFeature("test-feature-3", "docker-data-3", "test-instruction-3", "desc-3", "test-feature-2");

        // when
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl), List.class, "sorted", 1, "name", "test-feature-1,test-feature2");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(3);
    }

    @Test
    public void testFeatureSearchSuccess() throws JsonProcessingException {
        // given
        createFeature("test-feature-1", "docker-data-1", "test-instruction-1", "desc-1");
        createFeature("test-feature-2", "docker-data-2", "test-instruction-2", "desc-2");
        createFeature("feature-3", "docker-data-3", "test-instruction-3", "desc-3");

        // when
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl + "/search/test"), List.class);

        // then
        assertThat(result.getStatusCode().is2xxSuccessful());
        assertThat(result.getBody()).hasSize(2);
    }

    @Test
    public void testFeatureSearchEmptyResult() throws JsonProcessingException {
        // given
        createFeature("test-feature-1", "docker-data-1", "test-instruction-1", "desc-1");
        createFeature("test-feature-2", "docker-data-2", "test-instruction-2", "desc-2");

        // when
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl + "/search/foo"), List.class);

        // then
        assertThat(result.getStatusCode().is2xxSuccessful());
        assertThat(result.getBody()).hasSize(0);
    }

    @Test
    public void shouldApproveFeature() throws Exception {
        createFeature("test-feature-1", "docker-data-1", "test-instruction-1", "desc-1");
        createFeature("test-feature-2", "docker-data-2", "test-instruction-2", "desc-2");
        template.exchange(url(featuresUrl) + "/test-feature-1/approve",
                PUT, null, new ParameterizedTypeReference<List<FeatureDto>>() {});
        ResponseEntity<List<FeatureDto>> result =
                template.exchange(url(featuresUrl), GET, null, new ParameterizedTypeReference<List<FeatureDto>>() {});
        assertThat(findFeatureByName(result.getBody(), "test-feature-1").isApproved()).isTrue();
        assertThat(findFeatureByName(result.getBody(), "test-feature-2").isApproved()).isFalse();
    }

    private FeatureDto findFeatureByName(List<FeatureDto> collection, String name) {
        return collection.stream().filter(f -> name.equals(f.getName())).findFirst().get();
    }
}
