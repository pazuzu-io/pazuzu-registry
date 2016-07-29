package org.zalando.pazuzu.exception;


import javax.ws.rs.core.Response.Status;

public class FeatureDuplicateException extends ServiceException {

    private static final String CODE = "feature_duplicate";
    private static final String TITLE = "Feature with this name already exists";

    public FeatureDuplicateException() {
        super(Status.BAD_REQUEST, CODE, TITLE);
    }

}
