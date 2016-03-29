package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.zalando.pazuzu.container.ContainerFullDto;
import org.zalando.pazuzu.exception.ErrorDto;
import org.zalando.pazuzu.feature.FeatureDto;

public class ContainerApiTest extends AbstractComponentTest {

    @Test
    public void retrievingContainersShouldReturnEmptyListWhenNoContainersAreStored() {
        ResponseEntity<List> result = template.getForEntity(url(containersUrl), List.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEmpty();
    }

    @Test
    public void createContainerShouldReturnCreatedContainer() throws Exception {
        createFeature("Feature 1", "some data");
        createFeature("Feature 2", "some data");
        createFeature("Feature 3", "some data");

        ContainerFullDto resultContainer = createContainer("Container 1", "Feature 1", "Feature 2", "Feature 3").getBody();

        assertThat(resultContainer.getName()).isEqualTo("Container 1");
        assertThat(resultContainer.getFeatures()).extracting(FeatureDto::getName).containsOnly("Feature 1", "Feature 2", "Feature 3");
    }

    @Test
    public void createContainerShouldFailOnWrongNameNull() throws Exception {
        final ResponseEntity<ErrorDto> error = createContainerUnchecked(ErrorDto.class, null);
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error.getBody().getCode()).isEqualTo("name");
    }

    @Test
    public void createContainerShouldFailOnWrongNameEmpty() throws Exception {
        final ResponseEntity<ErrorDto> error = createContainerUnchecked(ErrorDto.class, "");
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error.getBody().getCode()).isEqualTo("name");
    }

    @Test
    public void createdContainerShouldBeRetrievableAfterwards() throws Exception {
        createFeature("Feature 4", "some data");
        createFeature("Feature 5", "some data");
        createFeature("Feature 6", "some data");

        ResponseEntity<ContainerFullDto> createdResult = createContainer("Container 2", "Feature 4", "Feature 5", "Feature 6");

        ResponseEntity<ContainerFullDto> retrievedContainer = template.getForEntity(createdResult.getHeaders().getLocation(), ContainerFullDto.class);
        assertThat(retrievedContainer.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(retrievedContainer.getBody().getName()).isEqualTo("Container 2");
        assertThat(retrievedContainer.getBody().getFeatures()).extracting(FeatureDto::getName).containsOnly("Feature 4", "Feature 5", "Feature 6");
    }

    @Test
    public void badRequestWhenJsonIsIncorrect() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Container 2");
        map.put("features", "StringInsteadOfList");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> response = template.postForEntity(url(containersUrl),
                new HttpEntity<>(mapper.writeValueAsString(map), headers), Map.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        final Map errorDto = response.getBody();
        assertThat(errorDto.keySet()).containsExactly("code", "message");
        assertThat(errorDto.get("code")).isEqualTo("json");
    }

    @Test
    public void badRequestWhenFeaturesAreNotExistingNewContainerIsReferencing() throws Exception {
        ResponseEntity<ErrorDto> response = createContainerUnchecked(ErrorDto.class, "Container 2", "NotExistingFeature");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo("features_not_found");
    }

    @Test
    public void deleteContainerProperly() throws JsonProcessingException {
        createFeature("Feature", "some data");
        createContainer("Container", "Feature");

        template.delete(url(containersUrl + "/Container"));

        ResponseEntity<List> result = template.getForEntity(url(containersUrl), List.class);
        assertThat(result.getBody()).isEmpty();
    }

    @Test
    public void notFoundWhenDeletingNotExistingContainer() throws JsonProcessingException {
        ResponseEntity<Void> response = template.exchange(url(containersUrl + "/NotExistingContainer"), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void addFeatureToExistingContainer() throws JsonProcessingException {
        createFeature("Feature", "some data");
        createContainer("Container", "Feature");
        createFeature("Other Feature", "some data");

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Other Feature");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ContainerFullDto> response = template.postForEntity(url(containersUrl + "/Container/features"),
                new HttpEntity<>(mapper.writeValueAsString(map), headers), ContainerFullDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Container");
        assertThat(response.getBody().getFeatures()).extracting(FeatureDto::getName).containsOnly("Feature", "Other Feature");
    }

    @Test
    public void notFoundWhenDeletingFeatureOnNotExistingContainer() throws JsonProcessingException {
        createFeature("Feature", "some data");
        createContainer("Container", "Feature");

        ResponseEntity<Void> response = template.exchange(url(containersUrl + "/NotExistingContainer/features/Feature"),
                HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void notFoundWhenDeletingNotExistingFeatureOnContainer() throws JsonProcessingException {
        createFeature("Feature", "some data");
        createContainer("Container", "Feature");

        ResponseEntity<Void> response = template.exchange(url(containersUrl + "/Container/features/NotExistingFeature"),
                HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testCrapShouldLeadToCorrectMessage() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> error = template.postForEntity(url(containersUrl), new HttpEntity<>("{json crap}", headers), Map.class);
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(error.getBody().keySet()).containsExactly("code", "message");
        assertThat(error.getBody().get("code")).isEqualTo("json");
    }
}
