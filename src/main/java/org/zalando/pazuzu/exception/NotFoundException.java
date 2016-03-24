package org.zalando.pazuzu.exception;

public class NotFoundException extends ServiceException {
    public NotFoundException(String code, String message) {
        super(code, message);
    }
}
