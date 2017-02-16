package org.zalando.pazuzu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Fail.fail;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.zalando.pazuzu.assertion.RestTemplateAssert.assertCreated;

import java.net.URI;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.zalando.pazuzu.exception.FeatureDuplicateException;
import org.zalando.pazuzu.exception.FeatureNameEmptyException;
import org.zalando.pazuzu.exception.FeatureNotFoundException;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureList;
import org.zalando.pazuzu.model.FeatureMeta;
import org.zalando.pazuzu.model.Review;

import com.fasterxml.jackson.core.JsonProcessingException;

public class FeatureApiTest extends AbstractComponentTest {

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
        assertEqualFeaturesIgnoreFixedProps(newFeature(id), result.getBody());
    }

    @Test
    public void createFeatureShouldFailOnWrongNameNull() throws Exception {
        // when
        Feature feature = new Feature();
        feature.setSnippet(SNIPPET + 1);
        final ResponseEntity<Map<String, Object>> error = createFeatureError(feature);
        // then
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertEqualErrors(new FeatureNameEmptyException(), error.getBody());
    }

    @Test
    public void createFeatureShouldFailOnWrongNameEmpty() throws Exception {
        // when
        Feature dto = newFeature(1);
        dto.getMeta().setName("");
        final ResponseEntity<Map<String, Object>> error = createFeatureError(dto);
        // then
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertEqualErrors(new FeatureNameEmptyException(), error.getBody());
    }

    @Test
    public void badRequestForInvalidJson() {
        // when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map<String, Object>> error = template.exchange(url(featuresUrl), POST, new HttpEntity<>("{json crap}", headers), new ParameterizedTypeReference<Map<String, Object>>() {});
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
        assertEqualFeaturesIgnoreFixedProps(newFeature(2), result.getBody());
    }

    @Test
    public void createdFeatureShouldHaveStatusPending() throws Exception {
        // given
        ResponseEntity<Feature> createdResult = createNewFeature(2);
        // when
        ResponseEntity<Feature> result = template.getForEntity(createdResult.getHeaders().getLocation(), Feature.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        // then
        assertThat(result.getBody().getMeta().getStatus()).isEqualTo(FeatureMeta.StatusEnum.pending);
    }

    @Test
    public void testShouldFailOnDuplicateFeatureCreation() throws Exception {
        // given
        int id = 3;
        createNewFeature(id);
        // when
        ResponseEntity<Map<String, Object>> error = createFeatureError(newFeature(id));
        // then
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertEqualErrors(
                new FeatureDuplicateException("Feature with name " + NAME + id + " already exists"), error.getBody()
        );
    }

    @Test
    public void testShouldFailOnDuplicateFeatureCreationIgnoreCase() throws Exception {
        // given
        int id = 3;
        createNewFeature(id);
        // when
        Feature duplicateFeature = newFeature(id);
        duplicateFeature.getMeta().setName(duplicateFeature.getMeta().getName().toUpperCase());
        ResponseEntity<Map<String, Object>> error = createFeatureError(duplicateFeature);
        // then
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertEqualErrors(
                new FeatureDuplicateException("Feature with name " + NAME + id + " already exists"), error.getBody()
        );
    }

    @Test
    public void returnsNotFoundWhenTryingToRetrieveNonExistingFeature() throws Exception {
        // when
        ResponseEntity<Map<String, Object>> result = template.exchange(url("/api/features/non_existing"), GET, null, new ParameterizedTypeReference<Map<String, Object>>() {});
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
    public void listFeaturesShouldReturnsPageLinks() throws Exception {
        // given
        IntStream.range(0, 100)
                .forEach(i -> {
                    try {
                        createAndAcceptFeature(newFeature("java-" + i));
                        createAndAcceptFeature(newFeature("java-node" + i));
                        createAndAcceptFeature(newFeature("test-" + i));
                    } catch (JsonProcessingException e) {
                        fail("unable to proper set up features", e);
                    }

                });

        //when
        ResponseEntity<FeatureList> result0_10 = template.exchange(url(featuresUrl + "?limit=10"),
                GET, null, FeatureList.class);

        //then
        assertThat(result0_10.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result0_10.getBody().getFeatures().size()).isEqualTo(10);
        assertThat(result0_10.getBody().getTotalCount()).isEqualTo(300);
        assertThat(result0_10.getBody().getLinks().getNext()).isNotNull();
        assertThat(result0_10.getBody().getLinks().getPrev()).isNull();
        URI next0_10 = URI.create(result0_10.getBody().getLinks().getNext().getHref());
        assertThat(next0_10.isAbsolute()).isFalse();
        assertThat(next0_10.getPath()).isEqualTo("/api/features");
        assertThat(next0_10.getQuery()).contains("limit=10");
        assertThat(next0_10.getQuery()).contains("offset=10");
        System.out.println(result0_10.getBody().getLinks());

        //when (follow the link)
        ResponseEntity<FeatureList> result10_10 = template.exchange(url(result0_10.getBody()
                        .getLinks().getNext().getHref()),
                GET, null, FeatureList.class);

        //then
        System.out.println(result10_10.getBody().getLinks());
        assertThat(result10_10.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result10_10.getBody().getFeatures().size()).isEqualTo(10);
        assertThat(result10_10.getBody().getTotalCount()).isEqualTo(300);
        assertThat(result10_10.getBody().getLinks().getNext()).isNotNull();
        assertThat(result10_10.getBody().getLinks().getPrev()).isNotNull();
        URI next10_10 = URI.create(result10_10.getBody().getLinks().getNext().getHref());
        assertThat(next10_10.isAbsolute()).isFalse();
        assertThat(next10_10.getPath()).isEqualTo("/api/features");
        assertThat(next10_10.getQuery()).contains("limit=10");
        assertThat(next10_10.getQuery()).contains("offset=20");
        URI prev10_10 = URI.create(result10_10.getBody().getLinks().getPrev().getHref());
        assertThat(prev10_10.isAbsolute()).isFalse();
        assertThat(prev10_10.getPath()).isEqualTo("/api/features");
        assertThat(prev10_10.getQuery()).contains("limit=10");
        assertThat(prev10_10.getQuery()).contains("offset=0");

        //when
        ResponseEntity<FeatureList> result_test_90_10 = template.exchange(
                url(featuresUrl + "?q=test&limit=10&offset=90"),
                GET, null, FeatureList.class);

        //then
        System.out.println(result_test_90_10.getBody().getLinks());
        assertThat(result_test_90_10.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result_test_90_10.getBody().getFeatures().size()).isEqualTo(10);
        assertThat(result_test_90_10.getBody().getTotalCount()).isEqualTo(100);
        assertThat(result_test_90_10.getBody().getLinks().getNext()).isNull();
        assertThat(result_test_90_10.getBody().getLinks().getPrev()).isNotNull();
        URI prevtest_90_10 = URI.create(result_test_90_10.getBody().getLinks().getPrev().getHref());
        assertThat(prevtest_90_10.isAbsolute()).isFalse();
        assertThat(prevtest_90_10.getPath()).isEqualTo("/api/features");
        assertThat(prevtest_90_10.getQuery()).contains("limit=10");
        assertThat(prevtest_90_10.getQuery()).contains("offset=80");
        assertThat(prevtest_90_10.getQuery()).contains("q=test");
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
    public void creatingAReviewWithStateApprovedShouldChangeStateOfFeature() throws Exception {
        //given
        ResponseEntity<Feature> createdResult = createFeature(newFeature("test"));

        //when
        Review review = new Review();
        review.setReviewStatus(Review.ReviewStatusEnum.approved);
        ResponseEntity<Review> exchange = template.exchange(url(featuresUrl, "test", "reviews"),
                POST, new HttpEntity<>(review), Review.class);

        //then
        assertCreated(exchange);

        ResponseEntity<Feature> result = template.getForEntity(createdResult.getHeaders().getLocation(), Feature.class);
        assertThat(result.getBody().getMeta().getStatus()).isEqualTo(FeatureMeta.StatusEnum.approved);
    }

    @Test
    public void creatingAReviewWithStateDeclinedShouldChangeStateOfFeature() throws Exception {
        //given
        ResponseEntity<Feature> createdResult = createFeature(newFeature("test"));

        //when
        Review review = new Review();
        review.setReviewStatus(Review.ReviewStatusEnum.declined);
        ResponseEntity<Review> exchange = template.exchange(url(featuresUrl, "test", "reviews"),
                POST, new HttpEntity<>(review), Review.class);

        //then
        assertCreated(exchange);

        ResponseEntity<Feature> result = template.getForEntity(createdResult.getHeaders().getLocation(), Feature.class);
        assertThat(result.getBody().getMeta().getStatus()).isEqualTo(FeatureMeta.StatusEnum.declined);
    }
}
