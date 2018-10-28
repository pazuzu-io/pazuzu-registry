package io.pazuzu.registry;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiDiscoveryTest extends AbstractComponentTest {

    @Test
    public void swaggerApiDesciptionShouldBeAccessible() {
        ResponseEntity<Map<String, Object>> response = template.exchange(url("/api"), HttpMethod.GET, null,
                new ParameterizedTypeReference<Map<String, Object>>() {
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("swagger")).isEqualTo("2.0");
    }
}
