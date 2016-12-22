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
import org.zalando.pazuzu.feature.FeatureDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PazuzuAppLauncher.class)
@WebIntegrationTest(randomPort = true)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")
public abstract class AbstractComponentTest {

    protected static final String NAME = "feature-", DESCRIPTION = "feature-description-", SNIPPET = "feature-snippet-",
            TEST_SNIPPET = "feature-test-snippet-";

    protected final String featuresUrl = "/api/features";
    protected final TestRestTemplate template = new TestRestTemplate();
    protected final ObjectMapper mapper = new ObjectMapper();
    @Value("${local.server.port}")
    private int port;

    protected String url(String... paths) {
        return "http://127.0.0.1:" + port + Strings.join(paths).with("/");
    }

    protected ResponseEntity<FeatureDto> createFeature(int id, int... dependencies) throws JsonProcessingException {
        return createFeature(
                NAME + id, DESCRIPTION + id, SNIPPET + id, TEST_SNIPPET + id,
                Arrays.stream(dependencies).mapToObj(i -> NAME + i).toArray(String[]::new)
        );
    }

    protected ResponseEntity<FeatureDto> createFeature(String name, String description, String snippet, String testSnippet, String... dependencies) throws JsonProcessingException {
        final ResponseEntity<FeatureDto> response = createFeatureUnchecked(FeatureDto.class, name, description, snippet, testSnippet, dependencies);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response;
    }

    protected <T> ResponseEntity<T> createFeatureUnchecked(Class<T> type, String name, String description, String snippet, String testSnippet, String... dependencies) throws JsonProcessingException {
        Map<String, Object> map = getFeaturePropertiesMap(name, description, snippet, testSnippet, dependencies);

        return template.postForEntity(url(featuresUrl), new HttpEntity<>(mapper.writeValueAsString(map),
                contentType(MediaType.APPLICATION_JSON)), type);
    }

    protected HttpHeaders contentType(MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        return headers;
    }

    protected Map<String, Object> getFeaturePropertiesMap(String name, String description, String snippet, String testSnippet, String... dependencies) {
        Map<String, Object> map = new HashMap<>();
        HashMap<String, Object> meta = new HashMap<>();
        map.put("meta", meta);
        if (null != name) {
            meta.put("name", name);
        }
        if (null != description) {
            meta.put("description", description);
        }
        if (null != snippet) {
            map.put("snippet", snippet);
        }
        if (null != testSnippet) {
            map.put("test_snippet", testSnippet);
        }
        meta.put("dependencies", Arrays.asList(dependencies));
        return map;
    }
}
