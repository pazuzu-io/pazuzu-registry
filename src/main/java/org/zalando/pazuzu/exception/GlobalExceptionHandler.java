package org.zalando.pazuzu.exception;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger _log = Logger.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ErrorDto featureNotExistingException(BadRequestException exception) {
        return new ErrorDto(exception.getError(), exception.getDetailedMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ErrorDto notFoundException(NotFoundException exception) {
        return new ErrorDto(exception.getError(), exception.getDetailedMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ErrorDto failedToParseJsonException(HttpMessageNotReadableException exception) {
        return new ErrorDto(Error.BAD_JSON);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorDto exception(Exception exception) {
        if (_log.isErrorEnabled()) _log.error(exception.getMessage(), exception);
        return new ErrorDto(Error.INTERNAL_SERVER_ERROR);
    }
}
