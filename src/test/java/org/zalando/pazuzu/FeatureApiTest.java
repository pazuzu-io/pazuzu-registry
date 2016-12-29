package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.springframework.http.*;
import org.zalando.pazuzu.exception.*;
import org.zalando.pazuzu.feature.FeatureDto;

import java.util.Date;
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
        ResponseEntity<FeatureDto> result = createNewFeature(id);
        // then
        assertEqualFeaturesIgnoreUpdatedAt(newFeature(id), result.getBody());
    }

    @Test
    public void createFeatureShouldFailOnWrongNameNull() throws Exception {
        // when
        final ResponseEntity<Map> error = createFeatureError(new FeatureDto().setSnippet(SNIPPET + 1));
        // then
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertEqualErrors(new FeatureNameEmptyException(), error.getBody());
    }

    @Test
    public void createFeatureShouldFailOnWrongNameEmpty() throws Exception {
        // when
        FeatureDto dto = newFeature(1);
        dto.getMeta().setName("");
        final ResponseEntity<Map> error = createFeatureError(dto);
        // then
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertEqualErrors(new FeatureNameEmptyException(), error.getBody());
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
                .contains(entry("title", "Bad Request"), entry("status", HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void createdFeatureShouldBeRetrievableAfterwards() throws Exception {
        // given
        ResponseEntity<FeatureDto> createdResult = createNewFeature(2);
        // when
        ResponseEntity<FeatureDto> result = template.getForEntity(createdResult.getHeaders().getLocation(), FeatureDto.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        // then
        assertEqualFeaturesIgnoreUpdatedAt(newFeature(2), result.getBody());
    }

    @Test
    public void testShouldFailOnDuplicateFeatureCreation() throws Exception {
        // given
        int id = 3;
        createNewFeature(id);
        // when
        ResponseEntity<Map> error = createFeatureError(newFeature(id));
        // then
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertEqualErrors(
                new FeatureDuplicateException("Feature with name " + NAME + id + " already exists"), error.getBody()
        );
    }

    @Test
    public void returnsNotFoundWhenTryingToRetrieveNonExistingFeature() throws Exception {
        // when
        ResponseEntity<Map> result = template.getForEntity(url("/api/features/non_existing"), Map.class);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertEqualErrors(new FeatureNotFoundException("Feature missing: non_existing"), result.getBody());
    }

    @Test
    public void returnsNotFoundWhenTryingToRetrieveMultipleNonExistingFeatures() throws Exception {
        // given
        createNewFeature(4);
        // when
        String missingName = "feature-5";
        ResponseEntity<Map> error = template.getForEntity(url(featuresUrl + "?names={name}"),
                Map.class, NAME + "4," + missingName);
        // then
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertEqualErrors(new FeatureNotFoundException("Feature missing: " + missingName), error.getBody());
    }

    @Test
    public void deleteFeatureProperly() throws JsonProcessingException {
        // given
        createNewFeature(5);
        // when
        ResponseEntity<Void> response = template.exchange(url(featuresUrl + "/" + NAME + 5), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        ResponseEntity<List> result = template.getForEntity(url(featuresUrl), List.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isEmpty();
    }

    @Test
    public void updateFeature() throws JsonProcessingException, InterruptedException {
        // given
        createNewFeature(6);
        createNewFeature(7);
        FeatureDto toBeUpdated = new FeatureDto();
        toBeUpdated.getMeta().setName(NAME + 8);
        Date beforeUpdate = createFeature(toBeUpdated).getBody().getMeta().getUpdatedAt();
        // The Api does not return milliseconds so we need to wait to see changes in the seconds.
        Thread.sleep(2000);
        // when
        ResponseEntity<FeatureDto> putResponse = template.exchange(
                url(featuresUrl + "/" + toBeUpdated.getMeta().getName()),
                PUT,
                new HttpEntity<>(mapper.writeValueAsString(newFeature(8, 6, 7)), contentType(MediaType.APPLICATION_JSON)),
                FeatureDto.class
        );
        // then
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        FeatureDto expected = newFeature(8, 6, 7);
        assertThat(putResponse.getBody().getMeta().getUpdatedAt()).isAfter(beforeUpdate);
        assertEqualFeaturesIgnoreUpdatedAt(expected, putResponse.getBody());
        ResponseEntity<FeatureDto> getResponse = template.getForEntity(url(featuresUrl + "/" + NAME + 8), FeatureDto.class);
        assertEqualFeaturesIgnoreUpdatedAt(expected, getResponse.getBody());
    }

    @Test
    public void notFoundWhenDeletingNotExistingFeature() throws JsonProcessingException {
        /// when
        ResponseEntity<Map> response = template.exchange(url(featuresUrl + "/NotExistingFeature"), HttpMethod.DELETE, HttpEntity.EMPTY, Map.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertEqualErrors(new FeatureNotFoundException("Feature missing: NotExistingFeature"), response.getBody());
    }

    @Test
    public void badRequestWhenDeletingStillReferencedFeature() throws JsonProcessingException {
        // given
        createNewFeature(9);
        createNewFeature(10, 9);
        // when
        ResponseEntity<Map> response = template.exchange(url(featuresUrl + "/" + NAME + 9), HttpMethod.DELETE, HttpEntity.EMPTY, Map.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertEqualErrors(new FeatureReferencedDeleteException("Can't delete feature because it is referenced from other feature(s): feature-10"), response.getBody());
    }

    @Test
    public void testGetSortedFeaturesSuccess() throws JsonProcessingException {
        // given
        createNewFeature(11);
        createNewFeature(12);
        createNewFeature(13, 12);
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
