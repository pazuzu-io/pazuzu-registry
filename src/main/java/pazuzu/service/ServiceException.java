package pazuzu.service;

public class ServiceException extends Exception {
    private final String code;

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static class NotFoundException extends ServiceException {
        public NotFoundException(String code, String message) {
            super(code, message);
        }
    }
}
