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
import org.zalando.pazuzu.feature.FeatureDto;

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
    protected final String resolvedFeaturesUrl = "/api/resolved-features";
    protected final String featuresMetaUrl = "/api/features-meta";
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

    protected FeatureDto newFeature(int id, int... dependencies) throws JsonProcessingException {
        FeatureDto dto = new FeatureDto();
        dto.setSnippet(SNIPPET + id).setTestSnippet(TEST_SNIPPET + id);
        dto.getMeta().setName(NAME + id).setDescription(DESCRIPTION + id).setAuthor(AUTHOR + id);
        Arrays.stream(dependencies).mapToObj(i -> NAME + i).forEach(dto.getMeta().getDependencies()::add);
        return dto;
    }

    protected ResponseEntity<FeatureDto> createFeature(FeatureDto dto) throws JsonProcessingException {
        final ResponseEntity<FeatureDto> response = post(dto, FeatureDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response;
    }

    protected ResponseEntity<FeatureDto> createNewFeature(int id, int... dependencies) throws JsonProcessingException {
        return createFeature(newFeature(id, dependencies));
    }

    protected ResponseEntity<Map> createFeatureError(FeatureDto dto) throws JsonProcessingException {
        return post(dto, Map.class);
    }

    protected void assertEqualFeaturesIgnoreUpdatedAt(FeatureDto expected, FeatureDto actual) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "meta");
        assertThat(actual.getMeta()).isEqualToIgnoringGivenFields(expected.getMeta(), "updatedAt");
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
}
