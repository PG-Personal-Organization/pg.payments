package pg.payments.application.imports.parsing.exception;

public class UserContextNotAvailableException extends RuntimeException {
    public UserContextNotAvailableException() {
        super("User context is not available.");
    }
}
