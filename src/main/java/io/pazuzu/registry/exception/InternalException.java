package io.pazuzu.registry.exception;

import org.zalando.problem.Status;

public class InternalException extends ServiceException {

    private static final Status STATUS = Status.INTERNAL_SERVER_ERROR;

    private static final String CODE = "internal_server_error";

    private static final String TITLE = "Internal server error";

    public InternalException() {
        super(STATUS, CODE, TITLE);
    }
}
