package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.pazuzu.feature.FeatureFullDto;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FeatureApiTest extends AbstractComponentTest {

    @Test
    public void retrievingFeaturesShouldReturnEmptyListWhenNoFeaturesAreStored() throws Exception {
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl), List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEmpty();
    }

    @Test
    public void createFeatureShouldReturnCreatedFeature() throws Exception {
        ResponseEntity<FeatureFullDto> result = createFeature("Test", "Test Data");

        FeatureFullDto resultFeature = result.getBody();
        assertThat(resultFeature.getName()).isEqualTo("Test");
        assertThat(resultFeature.getDockerData()).isEqualTo("Test Data");
        assertThat(resultFeature.getDependencies()).isEmpty();
    }

    @Test
    public void createdFeatureShouldBeRetrievableAfterwards() throws Exception {
        ResponseEntity<FeatureFullDto> createdResult = createFeature("Test 2", "Test Data 2");

        ResponseEntity<FeatureFullDto> result = template.getForEntity(createdResult.getHeaders().getLocation(), FeatureFullDto.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        FeatureFullDto resultFeature = result.getBody();
        assertThat(resultFeature.getName()).isEqualTo("Test 2");
        assertThat(resultFeature.getDockerData()).isEqualTo("Test Data 2");
        assertThat(resultFeature.getDependencies()).isEmpty();
    }

    @Test
    public void returnsNotFoundWhenTryingToRetrieveNonExistingFeature() throws Exception {
        ResponseEntity<String> result = template.getForEntity(url("/api/features/non_existing"), String.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        Map json = mapper.readValue(result.getBody(), Map.class);
        assertThat(json.get("code")).isEqualTo("not_found");
        assertThat(json.get("message")).isNotNull();
    }

    @Test
    public void deleteFeatureProperly() throws JsonProcessingException {
        createFeature("Feature", "some data");

        template.delete(url(featuresUrl + "/Feature"));

        ResponseEntity<List> result = template.getForEntity(url(featuresUrl), List.class);
        assertThat(result.getBody()).isEmpty();
    }

    @Test
    public void notFoundWhenDeletingNotExistingFeature() throws JsonProcessingException {
        ResponseEntity<Void> response = template.exchange(url(featuresUrl + "/NotExistingFeature"), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void badRequestWhenDeletingStillReferencedFeature() throws JsonProcessingException {
        createFeature("Feature", "some data");
        createContainer("Container", "Feature");

        ResponseEntity<Void> response = template.exchange(url(featuresUrl + "/Feature"), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
