package pg.payments.application.payments.management.exception;

public class TransferAccountNotExistException extends RuntimeException {
    public TransferAccountNotExistException(final String accountNumber) {
        super("Transfer account with number " + accountNumber + " does not exist.");
    }
}
