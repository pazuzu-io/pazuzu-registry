package org.zalando.pazuzu.exception;


public class Error {

    public final String code;
    public final String message;

    public Error(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
