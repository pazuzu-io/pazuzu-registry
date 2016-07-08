package org.zalando.pazuzu.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ErrorDto featureNotExistingException(BadRequestException exception) {
        LOG.error(exception.getMessage(), exception);
        return new ErrorDto(exception);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ErrorDto notFoundException(NotFoundException exception) {
        LOG.error(exception.getMessage(), exception);
        return new ErrorDto(exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ErrorDto failedToParseJsonException(HttpMessageNotReadableException exception) {
        LOG.error(exception.getMessage(), exception);
        return new ErrorDto(CommonErrors.BAD_JSON);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorDto exception(Exception exception) {
        LOG.error(exception.getMessage(), exception);
        return new ErrorDto(CommonErrors.INTERNAL_SERVER_ERROR);
    }
}
