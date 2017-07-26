package io.pazuzu.registry.exception;


import java.net.URI;

import javax.ws.rs.core.Response.StatusType;

import org.zalando.problem.ThrowableProblem;

public class ServiceException extends ThrowableProblem {

    private final String ERROR_BASE_URL = "http://pazuzu.io/error/";

    private final URI type;
    private final String title;
    private final StatusType status;
    private final String detail;

    public ServiceException(StatusType status, String code, String title) {
        this(status, code, title, null);
    }

    public ServiceException(StatusType status, String code, String title, String detail) {
        this.status = status;
        this.type = URI.create(ERROR_BASE_URL + code);
        this.title = title;
        this.detail = detail;
    }

    @Override
    public URI getType() {
        return type;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public StatusType getStatus() {
        return status;
    }

    @Override
    public String getDetail() {
        return detail;
    }
}
