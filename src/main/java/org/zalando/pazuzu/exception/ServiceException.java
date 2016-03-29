package org.zalando.pazuzu.exception;


public class ServiceException extends Exception {
    private Error error;
    private String detailedMessage;

    public ServiceException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

    public ServiceException(Error error, String detailedMessage) {
        this.error = error;
        this.detailedMessage = detailedMessage;
    }

    public Error getError() {
        return error;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }
}
