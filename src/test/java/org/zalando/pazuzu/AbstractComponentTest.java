package org.zalando.pazuzu;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.pazuzu.feature.FeatureFullDto;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PazuzuAppLauncher.class)
@WebIntegrationTest(randomPort = true)
public abstract class AbstractComponentTest {

    @Value("${local.server.port}")
    private int port;

    protected final TestRestTemplate template = new TestRestTemplate();

    protected final ObjectMapper mapper = new ObjectMapper();

    protected String url(String path) {
        return "http://127.0.0.1:" + port + path;
    }

    protected void createFeature(String name, String dockerData) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("docker_data", dockerData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<FeatureFullDto> response = template.postForEntity(url("/api/features"), new HttpEntity<>(mapper.writeValueAsString(map), headers), FeatureFullDto.class);
        if (response.getStatusCode().value() != 201) {
            throw new Exception("could not create feature " + name);
        }
    }
}
