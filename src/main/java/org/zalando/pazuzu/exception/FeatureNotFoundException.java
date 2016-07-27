package org.zalando.pazuzu.exception;


import javax.ws.rs.core.Response.Status;
import java.util.Optional;

public class FeatureNotFoundException extends ServiceException {

    private static final String CODE = "feature_not_found";
    private static final String TITLE = "Feature was not found";

    public FeatureNotFoundException() {
        super(Status.NOT_FOUND, CODE, TITLE);
    }

    public FeatureNotFoundException(String details) {
        super(Status.NOT_FOUND, CODE, TITLE, Optional.ofNullable(details));
    }

}
