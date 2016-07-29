package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.zalando.pazuzu.exception.ErrorDto;
import org.zalando.pazuzu.feature.FeatureDto;
import org.zalando.pazuzu.feature.FeatureFullDto;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;

public class FeatureApiTest extends AbstractComponentTest {

    @Test
    public void retrievingFeaturesShouldReturnEmptyListWhenNoFeaturesAreStored() throws Exception {
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl), List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEmpty();
    }

    @Test
    public void createFeatureShouldReturnCreatedFeature() throws Exception {
        ResponseEntity<FeatureFullDto> result = createFeature("Test", "Test Data", "test something", "The test feature");

        FeatureFullDto resultFeature = result.getBody();
        assertThat(resultFeature.getName()).isEqualTo("Test");
        assertThat(resultFeature.getTestInstruction()).isEqualTo("test something");
        assertThat(resultFeature.getDescription()).isEqualTo("The test feature");
        assertThat(resultFeature.getDockerData()).isEqualTo("Test Data");
        assertThat(resultFeature.getDependencies()).isEmpty();
    }

    @Test
    public void createFeatureShouldFailOnWrongNameNull() throws Exception {
        final ResponseEntity<ErrorDto> error = createFeatureUnchecked(ErrorDto.class, null, null, null, null);
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error.getBody().getCode()).isEqualTo("feature_name_empty");
    }

    @Test
    public void createFeatureShouldFailOnWrongNameEmpty() throws Exception {
        final ResponseEntity<ErrorDto> error = createFeatureUnchecked(ErrorDto.class, "", null, null, null);
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error.getBody().getCode()).isEqualTo("feature_name_empty");
    }

    @Test
    public void badRequestForInvalidJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> error = template.postForEntity(url(featuresUrl), new HttpEntity<>("{json crap}", headers), Map.class);
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(error.getBody().keySet()).containsExactly("code", "message", "detailed_message");
        assertThat(error.getBody().get("code")).isEqualTo("json_not_parsable");
    }

    @Test
    public void createdFeatureShouldBeRetrievableAfterwards() throws Exception {
        ResponseEntity<FeatureFullDto> createdResult = createFeature("Test 2", "Test Data 2", "something to test2", "Test 2 desc");

        ResponseEntity<FeatureFullDto> result = template.getForEntity(createdResult.getHeaders().getLocation(), FeatureFullDto.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        FeatureFullDto resultFeature = result.getBody();
        assertThat(resultFeature.getName()).isEqualTo("Test 2");
        assertThat(resultFeature.getDockerData()).isEqualTo("Test Data 2");
        assertThat(resultFeature.getTestInstruction()).isEqualTo("something to test2");
        assertThat(resultFeature.getDescription()).isEqualTo("Test 2 desc");
        assertThat(resultFeature.getDependencies()).isEmpty();
        assertThat(resultFeature.isApproved()).isFalse();
    }

    @Test
    public void returnsNotFoundWhenTryingToRetrieveNonExistingFeature() throws Exception {
        ResponseEntity<String> result = template.getForEntity(url("/api/features/non_existing"), String.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        Map json = mapper.readValue(result.getBody(), Map.class);
        assertThat(json.get("code")).isEqualTo("feature_not_found");
        assertThat(json.get("message")).isNotNull();
    }

    @Test
    public void deleteFeatureProperly() throws JsonProcessingException {
        createFeature("Feature", "some data", "something to test2", "Feature desc");

        ResponseEntity<Void> response = template.exchange(url(featuresUrl + "/Feature"), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<List> result = template.getForEntity(url(featuresUrl), List.class);
        assertThat(result.getBody()).isEmpty();
    }

    @Test
    public void updateFeature() throws JsonProcessingException {
        createFeature("Feature1", "dockerData Feature1", "testInstruction Feature1", "description Feature1");
        createFeature("Feature2", "dockerData Feature2", "testInstruction Feature2", "description Feature2");
        createFeature("Feature3", null, null, null);

        final Map<String, Object> updateRequest = getFeaturePropertiesMap(null, "dockerData Feature3", "testInstruction Feature3", "description Feature3", "Feature1", "Feature2");
        ResponseEntity<FeatureFullDto> putResponse = template.exchange(url(featuresUrl + "/Feature3"), PUT,
                new HttpEntity<>(mapper.writeValueAsString(updateRequest), contentType(MediaType.APPLICATION_JSON)), FeatureFullDto.class);
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

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
        ResponseEntity<Void> response = template.exchange(url(featuresUrl + "/NotExistingFeature"), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void badRequestWhenDeletingStillReferencedFeature() throws JsonProcessingException {
        createFeature("Feature", "some data", "something to test2", "Feature desc");
        createFeature("FeatureWithDependency", "some data", "something to test2", "Feature desc", "Feature");

        ResponseEntity<Void> response = template.exchange(url(featuresUrl + "/Feature"), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testGetSortedFeaturesSuccess() throws JsonProcessingException {
        createFeature("test-feature-1", "docker-data-1", "test-instruction-1", "desc-1");
        createFeature("test-feature-2", "docker-data-2", "test-instruction-2", "desc-2");
        createFeature("test-feature-3", "docker-data-3", "test-instruction-3", "desc-3", "test-feature-2");

        ResponseEntity<List> result = template.getForEntity(url(featuresUrl), List.class, "sorted", 1, "name", "test-feature-1,test-feature2");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(3);
    }

    @Test
    public void testFeatureSearchSuccess() throws JsonProcessingException {
        createFeature("test-feature-1", "docker-data-1", "test-instruction-1", "desc-1");
        createFeature("test-feature-2", "docker-data-2", "test-instruction-2", "desc-2");
        createFeature("feature-3", "docker-data-3", "test-instruction-3", "desc-3");
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl + "/search/test"), List.class);
        assertThat(result.getStatusCode().is2xxSuccessful());
        assertThat(result.getBody()).hasSize(2);
    }

    @Test
    public void testFeatureSearchEmptyResult() throws JsonProcessingException {
        createFeature("test-feature-1", "docker-data-1", "test-instruction-1", "desc-1");
        createFeature("test-feature-2", "docker-data-2", "test-instruction-2", "desc-2");
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl + "/search/foo"), List.class);
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
