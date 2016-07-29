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
import org.zalando.pazuzu.feature.FeatureFullDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PazuzuAppLauncher.class)
@WebIntegrationTest(randomPort = true)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:cleanDatabase.sql")
public abstract class AbstractComponentTest {

    protected final String featuresUrl = "/api/features";
    protected final TestRestTemplate template = new TestRestTemplate();
    protected final ObjectMapper mapper = new ObjectMapper();
    @Value("${local.server.port}")
    private int port;

    protected String url(String... paths) {
        return "http://127.0.0.1:" + port + Strings.join(paths).with("/");
    }

    protected ResponseEntity<FeatureFullDto> createFeature(String name, String dockerData, String testInstruction, String description, String... dependencies) throws JsonProcessingException {
        final ResponseEntity<FeatureFullDto> response = createFeatureUnchecked(FeatureFullDto.class, name, dockerData, testInstruction, description, dependencies);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response;
    }

    protected <T> ResponseEntity<T> createFeatureUnchecked(Class<T> clazz, String name, String dockerData, String testInstruction, String description, String... dependencies) throws JsonProcessingException {
        Map<String, Object> map = getFeaturePropertiesMap(name, dockerData, testInstruction, description, dependencies);

        return template.postForEntity(url(featuresUrl), new HttpEntity<>(mapper.writeValueAsString(map),
                contentType(MediaType.APPLICATION_JSON)), clazz);
    }

    protected HttpHeaders contentType(MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        return headers;
    }

    protected Map<String, Object> getFeaturePropertiesMap(String name, String dockerData, String testInstruction, String description, String... dependencies) {
        Map<String, Object> map = new HashMap<>();
        if (null != name) {
            map.put("name", name);
        }
        if (null != dockerData) {
            map.put("docker_data", dockerData);
        }
        if (null != testInstruction) {
            map.put("test_instruction", testInstruction);
        }
        if (null != description) {
            map.put("description", description);
        }
        map.put("dependencies", Arrays.asList(dependencies));

        return map;
    }
}
