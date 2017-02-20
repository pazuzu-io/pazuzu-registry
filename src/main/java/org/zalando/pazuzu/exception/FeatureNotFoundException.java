package org.zalando.pazuzu.exception;


import javax.ws.rs.core.Response.Status;

public class FeatureNotFoundException extends ServiceException {

    private static final String CODE = "feature_not_found";
    private static final String TITLE = "Feature was not found";

    public FeatureNotFoundException() {
        super(Status.NOT_FOUND, CODE, TITLE);
    }

    public FeatureNotFoundException(String detail) {
        super(Status.NOT_FOUND, CODE, TITLE, detail);
    }

}
