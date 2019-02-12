package io.pazuzu.registry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pazuzu.registry.assertion.RestTemplateAssert;
import io.pazuzu.registry.exception.ServiceException;
import io.pazuzu.registry.domain.Feature;
import org.assertj.core.util.Strings;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.http.HttpMethod.POST;

@RunWith(SpringRunner.class)
// Required due to https://github.com/spring-projects/spring-boot/issues/4424
@SpringBootTest(properties = {"management.port=0"}, webEnvironment = WebEnvironment.RANDOM_PORT, classes = PazuzuAppLauncher.class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")
@ActiveProfiles("test")
@Ignore
public abstract class AbstractComponentTest {

    protected static final String NAME = "feature-", DESCRIPTION = "feature-description-", AUTHOR = "feature-author-",
            SNIPPET = "feature-snippet-", TEST_SNIPPET = "feature-test-snippet-";

    protected final String featuresUrl = "/api/features";
    protected final String reviewPath = "reviews";
    protected final String resolvedFeaturesUrl = "/api/dependencies";
    protected final TestRestTemplate template = new TestRestTemplate();
    protected final ObjectMapper mapper = new ObjectMapper();
    @Value("${local.server.port}")
    private int webPort;
    @Value("${local.management.port}")
    private int managementPort;

    protected String url(String... paths) {
        return "http://127.0.0.1:" + webPort + Strings.join(paths).with("/");
    }

    protected HttpHeaders contentType(MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        return headers;
    }

    protected <T> ResponseEntity<T> post(Object body, ParameterizedTypeReference<T> type) throws JsonProcessingException {
        return template.exchange(
                url(featuresUrl),
                POST,
                new HttpEntity<>(mapper.writeValueAsString(body), contentType(MediaType.APPLICATION_JSON)),
                type
        );
    }

    protected <T> ResponseEntity<T> post(Object body, Class<T> type) throws JsonProcessingException {
        return template.exchange(
                url(featuresUrl),
                POST,
                new HttpEntity<>(mapper.writeValueAsString(body), contentType(MediaType.APPLICATION_JSON)),
                type
        );
    }

//    protected void put(String name, Review body) throws JsonProcessingException {
//        ResponseEntity<Object> approval = template.exchange(
//                url(featuresUrl + "/{name}/reviews"),
//                POST,
//                new HttpEntity<>(mapper.writeValueAsString(body), contentType(MediaType.APPLICATION_JSON)),
//                Object.class,
//                name
//        );
//        RestTemplateAssert.assertCreated(approval);
//    }

    protected Feature newFeature(int id, int... dependencies) throws JsonProcessingException {
        Feature dto = new Feature();
        dto.setName(NAME);
        dto.setDescription(DESCRIPTION);
        return dto;
    }

    protected ResponseEntity<Feature> createFeature(Feature dto) throws JsonProcessingException {
        final ResponseEntity<Feature> response = post(dto, Feature.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response;
    }
//
//    protected void createAndAcceptFeature(Feature dto) throws JsonProcessingException {
//        ResponseEntity<Feature> creationResponse = createFeature(dto);
//        Feature feature = creationResponse.getBody();
//        Review review = new Review();
//        review.setReviewStatus(Review.ReviewStatus.approved);
//        put(feature.getMeta().getName(), review);
//    }
//
    protected ResponseEntity<Feature> createNewFeature(int id, int... dependencies) throws JsonProcessingException {
        return createFeature(newFeature(id, dependencies));
    }

    protected ResponseEntity<Map<String, Object>> createFeatureError(Feature dto) throws JsonProcessingException {
        return post(dto, new ParameterizedTypeReference<Map<String, Object>>() {
        });
    }

//    protected void assertEqualFeaturesIgnoreFixedProps(Feature expected, Feature actual) {
//        assertThat(actual).isEqualToIgnoringGivenFields(expected, "meta");
//        assertThat(actual.getMeta()).isEqualToIgnoringGivenFields(expected.getMeta(), "updatedAt", "createdAt", "author");
//    }
//
    protected void assertEqualErrors(ServiceException expected, Map<String, Object> actual) {
        if (expected.getDetail() != null)
            assertThat(actual).containsOnly(
                    entry("type", expected.getType().toString()),
                    entry("title", expected.getTitle()),
                    entry("detail", expected.getDetail()),
                    entry("status", expected.getStatus().getStatusCode()));
        else
            assertThat(actual).containsOnly(
                    entry("type", expected.getType().toString()),
                    entry("title", expected.getTitle()),
                    entry("status", expected.getStatus().getStatusCode()));
    }


    protected Feature newFeature(String featureName) {
        Feature ret = new Feature();
        ret.setName(featureName);
        return ret;
    }
}
