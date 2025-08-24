package pg.payments.application.payments.management.exception;

import pg.accounts.domain.AccountViewUsage;

public class AccountNotAccessibleByUser extends RuntimeException {
    public AccountNotAccessibleByUser(final String accountNumber, final String userId, final AccountViewUsage usage) {
        super("Account with number " + accountNumber + " is not accessible by user " + userId + " for usage " + usage);
    }
}
