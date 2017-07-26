package io.pazuzu.registry.exception;


import javax.ws.rs.core.Response;

public class FileNotFoundException extends ServiceException {

    private static final String CODE = "file_not_found";
    private static final String TITLE = "File not found";

    public FileNotFoundException(String detail) {
        super(Response.Status.NOT_FOUND, CODE, TITLE, detail);
    }

}
