package io.pazuzu.registry.exception;

import org.zalando.problem.Status;

public class FileNotFoundException extends ServiceException {

    private static final Status STATUS = Status.NOT_FOUND;
    private static final String CODE = "file_not_found";
    private static final String TITLE = "File not found";


    public FileNotFoundException(String msg) {
        super(STATUS, CODE, TITLE, msg);
    }
}
