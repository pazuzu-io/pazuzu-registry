package org.zalando.pazuzu;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.pazuzu.container.ContainerFullDto;
import org.zalando.pazuzu.feature.FeatureDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    @Ignore
    public void badRequestWhenFeaturesAreNotExistingNewContainerIsReferencing() throws Exception {
    }
}
