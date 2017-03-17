package org.zalando.pazuzu.exception;


import javax.ws.rs.core.Response.Status;

public class FeatureVersionEmptyException extends ServiceException {

    private static final String CODE = "feature_version_empty";
    private static final String TITLE = "Feature version is empty";

    public FeatureVersionEmptyException() {
        super(Status.BAD_REQUEST, CODE, TITLE);
    }

}
