package org.zalando.pazuzu;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.*;
import org.zalando.pazuzu.container.ContainerFullDto;
import org.zalando.pazuzu.container.ContainerToCreateDto;
import org.zalando.pazuzu.feature.FeatureDto;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class ContainerApiTest extends AbstractComponentTest {

    private final String containersUrl = "/api/containers";

    @Test
    public void retrievingContainersShouldReturnEmptyListWhenNoContainersAreStored() {
        ResponseEntity<List> result = template.getForEntity(url(containersUrl), List.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void createContainerShouldReturnCreatedContainer() throws Exception {
        createFeature("Feature 1", "some data");
        createFeature("Feature 2", "some data");
        createFeature("Feature 3", "some data");

        final ContainerToCreateDto dto = new ContainerToCreateDto();
        dto.setName("Container 1");
        dto.setFeatures(Arrays.asList("Feature 1", "Feature 2", "Feature 3"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ContainerFullDto> result = template.postForEntity(url(containersUrl), new HttpEntity<>(dto, headers), ContainerFullDto.class);
        assertEquals(201, result.getStatusCode().value());

        ContainerFullDto resultContainer = result.getBody();
        assertThat(resultContainer.getName()).isEqualTo(dto.getName());
        assertThat(resultContainer.getFeatures()).extracting(FeatureDto::getName).containsOnly(dto.getFeatures().toArray(new String[0]));
    }

    @Test
    public void createdContainerShouldBeRetrievableAfterwards() throws Exception {
        createFeature("Feature 4", "some data");
        createFeature("Feature 5", "some data");
        createFeature("Feature 6", "some data");

        final ContainerToCreateDto dto = new ContainerToCreateDto();
        dto.setName("Container 2");
        dto.setFeatures(Arrays.asList("Feature 4", "Feature 5", "Feature 6"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ContainerFullDto> createdResult = template.postForEntity(url(containersUrl), new HttpEntity<>(dto, headers), ContainerFullDto.class);

        ResponseEntity<ContainerFullDto> result = template.getForEntity(createdResult.getHeaders().getLocation(), ContainerFullDto.class);
        assertEquals(200, result.getStatusCode().value());

        ContainerFullDto resultContainer = result.getBody();
        assertThat(resultContainer.getName()).isEqualTo(dto.getName());
        assertThat(resultContainer.getFeatures()).extracting(FeatureDto::getName).containsOnly(dto.getFeatures().toArray(new String[0]));
    }

    @Test @Ignore
    public void badRequestWhenFeaturesAreNotExistingNewContainerIsReferencing() throws Exception {
    }
}
