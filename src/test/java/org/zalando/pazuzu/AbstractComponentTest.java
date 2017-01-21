package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Strings;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.pazuzu.exception.ServiceException;
import org.zalando.pazuzu.feature.FeatureStatus;
import org.zalando.pazuzu.model.Feature;
import org.zalando.pazuzu.model.FeatureMeta;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PazuzuAppLauncher.class)
@WebIntegrationTest(randomPort = true)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")
public abstract class AbstractComponentTest {

    protected static final String NAME = "feature-", DESCRIPTION = "feature-description-", AUTHOR = "feature-author-",
            SNIPPET = "feature-snippet-", TEST_SNIPPET = "feature-test-snippet-";

    protected final String featuresUrl = "/api/features";
    protected final String resolvedFeaturesUrl = "/api/dependencies";
    protected final TestRestTemplate template = new TestRestTemplate();
    protected final ObjectMapper mapper = new ObjectMapper();
    @Value("${local.server.port}")
    private int port;

    protected String url(String... paths) {
        return "http://127.0.0.1:" + port + Strings.join(paths).with("/");
    }

    protected HttpHeaders contentType(MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        return headers;
    }

    protected <T> ResponseEntity<T> post(Object body, Class<T> type) throws JsonProcessingException {
        return template.postForEntity(
                url(featuresUrl),
                new HttpEntity<>(mapper.writeValueAsString(body), contentType(MediaType.APPLICATION_JSON)),
                type
        );
    }

    protected void put(String name, Object body) throws JsonProcessingException {
        template.put(
                url(featuresUrl + "/{name}"),
                new HttpEntity<>(mapper.writeValueAsString(body), contentType(MediaType.APPLICATION_JSON)),
                name
        );
    }

    protected Feature newFeature(int id, int... dependencies) throws JsonProcessingException {
        Feature dto = new Feature();
        dto.setSnippet(SNIPPET + id);
        dto.setTestSnippet(TEST_SNIPPET + id);
        dto.setMeta(new FeatureMeta());
        dto.getMeta().setName(NAME + id);
        dto.getMeta().setDescription(DESCRIPTION + id);
        dto.getMeta().setAuthor(AUTHOR + id);
        dto.getMeta().setStatus(FeatureStatus.PENDING.jsonValue());
        Arrays.stream(dependencies).mapToObj(i -> NAME + i).forEach(dto.getMeta().getDependencies()::add);
        return dto;
    }

    protected ResponseEntity<Feature> createFeature(Feature dto) throws JsonProcessingException {
        final ResponseEntity<Feature> response = post(dto, Feature.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response;
    }

    protected void createAndAcceptFeature(Feature dto) throws JsonProcessingException {
        ResponseEntity<Feature> creationResponse = createFeature(dto);
        Feature feature = creationResponse.getBody();
        feature.getMeta().setStatus(FeatureStatus.APPROVED.jsonValue());
        put(feature.getMeta().getName(), feature);
    }

    protected ResponseEntity<Feature> createNewFeature(int id, int... dependencies) throws JsonProcessingException {
        return createFeature(newFeature(id, dependencies));
    }

    protected ResponseEntity<Map> createFeatureError(Feature dto) throws JsonProcessingException {
        return post(dto, Map.class);
    }

    protected void assertEqualFeaturesIgnoreUpdatedAt(Feature expected, Feature actual) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "meta");
        assertThat(actual.getMeta()).isEqualToIgnoringGivenFields(expected.getMeta(), "updatedAt", "createdAt");
    }

    protected void assertEqualErrors(ServiceException expected, Map actual) {
        if (expected.getDetail().isPresent())
            assertThat(actual).containsOnly(
                    entry("type", expected.getType().toString()),
                    entry("title", expected.getTitle()),
                    entry("detail", expected.getDetail().get()),
                    entry("status", expected.getStatus().getStatusCode()));
        else
            assertThat(actual).containsOnly(
                    entry("type", expected.getType().toString()),
                    entry("title", expected.getTitle()),
                    entry("status", expected.getStatus().getStatusCode()));
    }


    protected Feature newFeature(String featureName) {
        Feature ret = new Feature();
        ret.setMeta(new FeatureMeta());
        ret.getMeta().setName(featureName);
        return ret;
    }
}
