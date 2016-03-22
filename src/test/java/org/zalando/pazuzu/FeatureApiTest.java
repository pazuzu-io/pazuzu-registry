package org.zalando.pazuzu;


import org.junit.Test;
import org.springframework.http.*;
import org.zalando.pazuzu.feature.FeatureFullDto;
import org.zalando.pazuzu.feature.FeatureToCreateDto;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FeatureApiTest extends AbstractComponentTest {

    private final String featuresUrl = "/api/features";

    @Test
    public void retrievingFeaturesShouldReturnEmptyListWhenNoFeaturesAreStored() throws Exception {
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl), List.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(0, result.getBody().size());
    }

    @Test
    public void createFeatureShouldReturnCreatedFeature() throws Exception {
        final FeatureToCreateDto dto = new FeatureToCreateDto();
        dto.setName("Test");
        dto.setDockerData("Test Data");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<FeatureFullDto> result = template.postForEntity(url(featuresUrl), new HttpEntity<>(dto, headers), FeatureFullDto.class);
        assertEquals(201, result.getStatusCode().value());

        FeatureFullDto resultFeature = result.getBody();
        assertEquals(dto.getName(), resultFeature.getName());
        assertEquals(dto.getDockerData(), resultFeature.getDockerData());
        assertEquals(0, dto.getDependencies().size());
    }

    @Test
    public void createdFeatureShouldBeRetrievableAfterwards() throws Exception {
        final FeatureToCreateDto dto = new FeatureToCreateDto();
        dto.setName("Test 2");
        dto.setDockerData("Test Data 2");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<FeatureFullDto> createdResult = template.postForEntity(url(featuresUrl), new HttpEntity<>(dto, headers), FeatureFullDto.class);

        ResponseEntity<FeatureFullDto> result = template.getForEntity(createdResult.getHeaders().getLocation(), FeatureFullDto.class);
        assertEquals(200, result.getStatusCode().value());

        FeatureFullDto resultFeature = result.getBody();
        assertEquals(dto.getName(), resultFeature.getName());
        assertEquals(dto.getDockerData(), resultFeature.getDockerData());
        assertEquals(0, dto.getDependencies().size());
    }
}
