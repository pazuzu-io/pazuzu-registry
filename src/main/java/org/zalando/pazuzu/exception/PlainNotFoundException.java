package org.zalando.pazuzu.exception;

/**
 * @see GlobalExceptionHandler#plainNotFoundException(org.zalando.pazuzu.exception.PlainNotFoundException)
 */
public class PlainNotFoundException extends NotFoundException {
    public PlainNotFoundException(String details) {
        super(details);
    }
}
