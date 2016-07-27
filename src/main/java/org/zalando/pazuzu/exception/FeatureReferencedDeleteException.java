package org.zalando.pazuzu.exception;


import javax.ws.rs.core.Response.Status;
import java.util.Optional;

public class FeatureReferencedDeleteException extends ServiceException {

    private static final String CODE = "feature_not_deletable_due_to_references";
    private static final String TITLE = "Can't delete feature because it still has references";

    public FeatureReferencedDeleteException(String detail) {
        super(Status.BAD_REQUEST, CODE, TITLE, Optional.ofNullable(detail));
    }

}
