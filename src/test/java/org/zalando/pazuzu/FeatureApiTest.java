package org.zalando.pazuzu;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.pazuzu.feature.FeatureFullDto;

import java.util.List;

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
}
