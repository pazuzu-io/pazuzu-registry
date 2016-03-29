package org.zalando.pazuzu.exception;


public class BadRequestException extends ServiceException {
    public BadRequestException(Error error) {
        super(error);
    }
}
