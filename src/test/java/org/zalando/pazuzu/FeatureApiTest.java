package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.zalando.pazuzu.exception.*;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureList;
import org.zalando.pazuzu.model.FeatureMeta;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;

public class FeatureApiTest extends AbstractComponentTest {
    private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        }
    };

    @Test
    public void retrievingFeaturesShouldReturnEmptyListWhenNoFeaturesAreStored() throws Exception {
        // when
        ResponseEntity<FeatureList> result = template.getForEntity(url(featuresUrl), FeatureList.class);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures()).isEmpty();
    }

    @Test
    public void createFeatureShouldReturnCreatedFeature() throws Exception {
        // when
        int id = 1;
        ResponseEntity<Feature> result = createNewFeature(id);
        // then
        assertEqualFeaturesIgnoreUpdatedAt(newFeature(id), result.getBody());
    }

    @Test
    public void createFeatureShouldFailOnWrongNameNull() throws Exception {
        // when
        Feature feature = new Feature();
        feature.setSnippet(SNIPPET + 1);
        final ResponseEntity<Map> error = createFeatureError(feature);
        // then
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertEqualErrors(new FeatureNameEmptyException(), error.getBody());
    }

    @Test
    public void createFeatureShouldFailOnWrongNameEmpty() throws Exception {
        // when
        Feature dto = newFeature(1);
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
        ResponseEntity<Feature> createdResult = createNewFeature(2);
        // when
        ResponseEntity<Feature> result = template.getForEntity(createdResult.getHeaders().getLocation(), Feature.class);
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
    public void listFeaturesShouldNotReturnAnErrorForUnmatchedNames() throws Exception {
        // given
        createAndAcceptFeature(newFeature(4));
        // when
        String missingName = "feature-5";
        ResponseEntity<FeatureList> result = template.exchange(url(featuresUrl + "?q={name}"),
                GET, null, FeatureList.class, missingName);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures().size()).isEqualTo(0);
    }

    @Test
    public void listFeaturesShouldReturnsFeaturesThatMatchesTheNamePartially() throws Exception {
        // given
        createAndAcceptFeature(newFeature("java"));
        createAndAcceptFeature(newFeature("java-node"));
        createAndAcceptFeature(newFeature("node"));
        createAndAcceptFeature(newFeature("node-mongo"));
        createAndAcceptFeature(newFeature("python"));
        // when
        ResponseEntity<FeatureList> jaResult = template.exchange(url(featuresUrl + "?q=ja"),
                GET, null, FeatureList.class);
        // then
        assertThat(jaResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jaResult.getBody().getFeatures().size()).isEqualTo(2);
        assertThat(jaResult.getBody().getFeatures().stream().map(Feature::getMeta).map(FeatureMeta::getName))
                .containsOnly("java", "java-node");
        // when
        ResponseEntity<FeatureList> nodResult = template.exchange(url(featuresUrl + "?q=nod"),
                GET, null, FeatureList.class);
        // then
        assertThat(nodResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(nodResult.getBody().getFeatures().size()).isEqualTo(3);
        assertThat(nodResult.getBody().getFeatures().stream().map(Feature::getMeta).map(FeatureMeta::getName))
                .containsOnly("node", "java-node", "node-mongo");
    }

    @Test
    public void listFeaturesShouldReturnsEmptyArrayIfNothingMatched() throws Exception {
        // given
        createFeature(newFeature("java"));
        createFeature(newFeature("java-node"));
        // when
        ResponseEntity<FeatureList> result = template.exchange(url(featuresUrl + "?q=rails"),
                GET, null, FeatureList.class);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures().size()).isEqualTo(0);
    }

    @Test
    public void listFeaturesShouldReturnsFeaturesThatMatchesTheNameIgnoringTheCase() throws Exception {
        // given
        createAndAcceptFeature(newFeature("java"));
        createAndAcceptFeature(newFeature("java-node"));
        // when
        ResponseEntity<FeatureList> jaResult = template.exchange(url(featuresUrl + "?q=JaV"),
                GET, null, FeatureList.class);
        // then
        assertThat(jaResult.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jaResult.getBody().getFeatures().size()).isEqualTo(2);
        assertThat(jaResult.getBody().getFeatures().stream().map(Feature::getMeta).map(FeatureMeta::getName))
                .containsOnly("java", "java-node");
    }

    @Test
    public void deleteFeatureProperly() throws JsonProcessingException {
        // given
        createNewFeature(5);
        // when
        ResponseEntity<Void> response = template.exchange(url(featuresUrl + "/" + NAME + 5), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        ResponseEntity<FeatureList> result = template.getForEntity(url(featuresUrl), FeatureList.class);
        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody().getFeatures()).isEmpty();
    }

    @Test
    public void updateFeature() throws JsonProcessingException, InterruptedException, ParseException {
        // given
        createNewFeature(6);
        createNewFeature(7);
        Feature toBeUpdated = new Feature();
        toBeUpdated.setMeta(new FeatureMeta());
        toBeUpdated.getMeta().setName(NAME + 8);
        ResponseEntity<Feature> feature = createFeature(toBeUpdated);
        String beforeUpdatedAtString = feature.getBody().getMeta().getUpdatedAt();
        Date beforeUpdate = (Date) dateFormat.get().parse(beforeUpdatedAtString);
        // The Api does not return milliseconds so we need to wait to see changes in the seconds.
        Thread.sleep(2000);
        // when
        ResponseEntity<Feature> putResponse = template.exchange(
                url(featuresUrl + "/" + toBeUpdated.getMeta().getName()),
                PUT,
                new HttpEntity<>(mapper.writeValueAsString(newFeature(8, 6, 7)), contentType(MediaType.APPLICATION_JSON)),
                Feature.class
        );
        // then
        assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Feature expected = newFeature(8, 6, 7);
        String afterUpdatedAtString = putResponse.getBody().getMeta().getUpdatedAt();
        Date afterUpdatedAt = dateFormat.get().parse(afterUpdatedAtString);
        assertThat(afterUpdatedAt).isAfter(beforeUpdate);
        assertEqualFeaturesIgnoreUpdatedAt(expected, putResponse.getBody());
        ResponseEntity<Feature> getResponse = template.getForEntity(url(featuresUrl + "/" + NAME + 8), Feature.class);
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
}
