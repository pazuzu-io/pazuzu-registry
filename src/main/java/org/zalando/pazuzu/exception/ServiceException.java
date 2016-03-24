package org.zalando.pazuzu.exception;

public class ServiceException extends Exception {
    private final String code;

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
