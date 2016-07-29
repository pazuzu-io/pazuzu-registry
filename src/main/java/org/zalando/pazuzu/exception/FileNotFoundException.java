package org.zalando.pazuzu.exception;


import javax.ws.rs.core.Response;
import java.util.Optional;

public class FileNotFoundException extends ServiceException {

    private static final String CODE = "file_not_found";
    private static final String TITLE = "File not found";

    public FileNotFoundException(String details) {
        super(Response.Status.NOT_FOUND, CODE, TITLE, Optional.ofNullable(details));
    }

}
