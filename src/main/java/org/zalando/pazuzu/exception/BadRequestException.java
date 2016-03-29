package org.zalando.pazuzu.exception;


public class BadRequestException extends ServiceException {
    public BadRequestException(String code, String message) {
        super(code, message);
    }
}
