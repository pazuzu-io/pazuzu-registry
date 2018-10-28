package io.pazuzu.registry.exception;

import org.zalando.problem.Status;

public class FeatureReferencedDeleteException extends ServiceException {
    private static final Status STATUS = Status.NOT_FOUND;
    private static final String CODE = "feature_not_deletable_due_to_references";
    private static final String TITLE = "Can't delete feature because it still has references";

    public FeatureReferencedDeleteException(String detail) {
        super(STATUS, CODE, TITLE, detail);
    }

}
