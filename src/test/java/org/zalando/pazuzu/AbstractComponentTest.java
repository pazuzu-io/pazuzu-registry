package org.zalando.pazuzu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.pazuzu.container.ContainerFullDto;
import org.zalando.pazuzu.feature.FeatureFullDto;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PazuzuAppLauncher.class)
@WebIntegrationTest(randomPort = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class AbstractComponentTest {

    @Value("${local.server.port}")
    private int port;

    protected final String featuresUrl = "/api/features";
    protected final String containersUrl = "/api/containers";

    protected final TestRestTemplate template = new TestRestTemplate();
    protected final ObjectMapper mapper = new ObjectMapper();

    protected String url(String path) {
        return "http://127.0.0.1:" + port + path;
    }

    protected ResponseEntity<FeatureFullDto> createFeature(String name, String dockerData) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("docker_data", dockerData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<FeatureFullDto> response = template.postForEntity(url(featuresUrl), new HttpEntity<>(mapper.writeValueAsString(map), headers), FeatureFullDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response;
    }

    protected ResponseEntity<ContainerFullDto> createContainer(String name, String... features) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("features", features);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ContainerFullDto> response = template.postForEntity(url(containersUrl), new HttpEntity<>(mapper.writeValueAsString(map), headers), ContainerFullDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response;
    }
}