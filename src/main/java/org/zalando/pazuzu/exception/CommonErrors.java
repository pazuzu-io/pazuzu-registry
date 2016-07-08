package org.zalando.pazuzu.exception;

public class CommonErrors {
    public static final Error INTERNAL_SERVER_ERROR = new Error("internal_server_error", "An internal server error has occurred");
    public static final Error BAD_JSON = new Error("json_not_parsable", "Failed to parse incoming json");
    public static final Error CONTAINER_NOT_FOUND = new Error("container_not_found", "Container was not found");
    public static final Error ITEM_NOT_FOUND = new Error("item_not_found", "Item not found");
}
