package org.zalando.pazuzu.exception;


public class FeatureNotExistingException extends ServiceException {
    public FeatureNotExistingException(String code, String message) {
        super(code, message);
    }
}
