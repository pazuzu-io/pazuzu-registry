package io.pazuzu.registry.exception;

import org.zalando.problem.Status;

public class FeatureNameEmptyException extends ServiceException {

    private static final Status STATUS = Status.BAD_REQUEST;
    private static final String CODE = "feature_name_empty";
    private static final String TITLE = "Feature name is empty";

    public FeatureNameEmptyException() {
        super(STATUS, CODE, TITLE);
    }

}
