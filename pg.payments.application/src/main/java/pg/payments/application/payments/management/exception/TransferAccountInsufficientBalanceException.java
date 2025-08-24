package pg.payments.application.payments.management.exception;

import java.math.BigDecimal;

public class TransferAccountInsufficientBalanceException extends RuntimeException {
    public TransferAccountInsufficientBalanceException(final String accountNumber, final BigDecimal amount, final String currency) {
        super("Transfer account with number " + accountNumber + " does not have sufficient balance for the payment of " + amount.toEngineeringString() + currency);
    }
}
