package org.zalando.pazuzu.exception;


public class ServiceException extends RuntimeException {
    private final Error error;

    public ServiceException(Error error) {
        super(error.message);
        this.error = error;
    }

    public ServiceException(Error error, String detailedMessage) {
        super(detailedMessage);
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
