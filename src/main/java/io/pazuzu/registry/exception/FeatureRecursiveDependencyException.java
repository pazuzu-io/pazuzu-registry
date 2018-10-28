package io.pazuzu.registry.exception;

import org.zalando.problem.Status;

public class FeatureRecursiveDependencyException extends ServiceException {
    private static final Status STATUS = Status.NOT_FOUND;
    private static final String CODE = "feature_has_recursive_dependency";
    private static final String TITLE = "Recursive dependencies found";

    public FeatureRecursiveDependencyException(String detail) {
        super(STATUS, CODE, TITLE, detail);
    }

}
