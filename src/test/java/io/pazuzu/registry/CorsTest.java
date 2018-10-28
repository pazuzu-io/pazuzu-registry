package io.pazuzu.registry;

import org.junit.Test;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class CorsTest extends AbstractComponentTest {

    @Test
    public void testShouldSuccessfullyReturnCorsHeadersForValidOrigin() {
        // given
        String validOrigin = "http://localhost:8080";
        HttpEntity<Void> entity = createHttpEntityWithOrigin(validOrigin);

        // when
        ResponseEntity<Void> response = template.exchange(url("/api/features"), HttpMethod.GET, entity, Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getAccessControlAllowOrigin()).isEqualTo(validOrigin);
    }

    @Test
    public void testShouldReturnAccessForbiddenForInvalidOrigin() {
        // given
        String invalidOrigin = "http://omg-this-is-not-a-valid-origin:8080";
        HttpEntity<Void> entity = createHttpEntityWithOrigin(invalidOrigin);

        // when
        ResponseEntity<Void> response = template.exchange(url("/api/features"), HttpMethod.GET, entity, Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpEntity<Void> createHttpEntityWithOrigin(String origin) {
        HttpHeaders headers = new HttpHeaders();
        headers.setOrigin(origin);
        return new HttpEntity<>(headers);
    }
}
