package io.pazuzu.registry.exception;

import org.zalando.problem.Status;

public class FeatureNotFoundException extends ServiceException {

    private static final Status STATUS = Status.NOT_FOUND;
    private static final String CODE = "feature_not_found";
    private static final String TITLE = "Feature was not found";

    public FeatureNotFoundException() {
        super(STATUS, CODE, TITLE);
    }

    public FeatureNotFoundException(String detail) {
        super(STATUS, CODE, TITLE, detail);
    }

}
