package io.pazuzu.registry.exception;


import javax.ws.rs.core.Response.Status;

public class FeatureRecursiveDependencyException extends ServiceException {

    private static final String CODE = "feature_has_recursive_dependency";
    private static final String TITLE = "Recursive dependencies found";

    public FeatureRecursiveDependencyException(String detail) {
        super(Status.BAD_REQUEST, CODE, TITLE, detail);
    }

}
