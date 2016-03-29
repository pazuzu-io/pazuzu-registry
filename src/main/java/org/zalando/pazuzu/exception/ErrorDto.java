package org.zalando.pazuzu.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorDto {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    public ErrorDto(Error error) {
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public ErrorDto() {
    }

    public ErrorDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
