package org.zalando.pazuzu.exception;


import javax.ws.rs.core.Response.Status;

public class FeatureNameEmptyException extends ServiceException {

    private static final String CODE = "feature_name_empty";
    private static final String TITLE = "Feature name is empty";

    public FeatureNameEmptyException() {
        super(Status.BAD_REQUEST, CODE, TITLE);
    }

}
