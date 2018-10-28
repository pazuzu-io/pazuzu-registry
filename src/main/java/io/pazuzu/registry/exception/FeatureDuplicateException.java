package io.pazuzu.registry.exception;

import org.zalando.problem.Status;

public class FeatureDuplicateException extends ServiceException {

    private static final Status STATUS = Status.BAD_REQUEST;
    private static final String CODE = "feature_duplicate";
    private static final String TITLE = "Feature with this name already exists";

    public FeatureDuplicateException(String detail) {
        super(STATUS, CODE, TITLE, detail);
    }

}
