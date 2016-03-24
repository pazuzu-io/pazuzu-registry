package org.zalando.pazuzu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FeatureNotExistingException.class)
    @ResponseBody
    public ErrorDto featureNotExistingException(FeatureNotExistingException exception) {
        return new ErrorDto(exception.getCode(), exception.getMessage());
    }
}
