package org.zalando.pazuzu.exception;

/**
 * Created by pzalunin on 29/07/16.
 */
public class InternalServerError extends RuntimeException {
    public InternalServerError(String message) {
        super(message);
    }

    public InternalServerError(String message, Throwable cause) {
        super(message, cause);
    }
}
