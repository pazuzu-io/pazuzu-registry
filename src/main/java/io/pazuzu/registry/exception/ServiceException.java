package io.pazuzu.registry.exception;


import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

import java.net.URI;

public class ServiceException extends ThrowableProblem {

    private final String ERROR_BASE_URL = "http://pazuzu.io/error/";

    private final URI type;
    private final String title;
    private final Status status;
    private final String detail;


    public ServiceException(Status status, String code, String title) {
        this(status, code, title, null);
    }

    public ServiceException(Status status, String code, String title, String detail) {
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
