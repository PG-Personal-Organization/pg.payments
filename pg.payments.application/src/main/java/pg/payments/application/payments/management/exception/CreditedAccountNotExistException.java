package pg.payments.application.payments.management.exception;

public class CreditedAccountNotExistException extends RuntimeException {
    public CreditedAccountNotExistException(final String accountNumber) {
        super("Credited account with number " + accountNumber + " does not exist.");
    }
}
