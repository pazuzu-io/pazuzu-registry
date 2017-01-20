package org.zalando.pazuzu;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiDiscoveryTest extends AbstractComponentTest {

    @Test
    public void swaggerApiDesciptionShouldBeAccessible() {
        ResponseEntity<String> response = template.getForEntity(url("/api"), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).startsWith("{\"swagger\":\"2.0\"");
    }
}
