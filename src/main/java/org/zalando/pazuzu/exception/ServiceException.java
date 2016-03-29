package org.zalando.pazuzu.exception;


public class ServiceException extends Exception {
    private Error error;

    public ServiceException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
