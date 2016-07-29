package org.zalando.pazuzu.exception;


import org.zalando.problem.ThrowableProblem;

import javax.ws.rs.core.Response.StatusType;
import java.net.URI;
import java.util.Optional;

import static java.util.Optional.empty;

public class ServiceException extends ThrowableProblem {

    private final String ERROR_BASE_URL = "http://pazuzu.io/error/";

    private final URI type;
    private final String title;
    private final StatusType status;
    private final Optional<String> detail;

    public ServiceException(StatusType status, String code, String title) {
        this(status, code, title, empty());
    }

    public ServiceException(StatusType status, String code, String title, Optional<String> detail) {
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
    public Optional<String> getDetail() {
        return detail;
    }
}
