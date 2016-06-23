package org.zalando.pazuzu.assertion;

import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

public final class RestTemplateAssert {

    private RestTemplateAssert() {
    }

    public static void assertSuccess(ResponseEntity response) {
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    public static void assertCreated(ResponseEntity response) {
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    public static void assertNoContent(ResponseEntity response) {
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    public static void assertUnauthorized(ResponseEntity response) {
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
    }

    public static void assertForbidden(ResponseEntity response) {
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }
}
