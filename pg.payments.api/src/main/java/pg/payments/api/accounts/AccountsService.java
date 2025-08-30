package pg.payments.api.accounts;

import java.math.BigDecimal;

public interface AccountsService {
    String bookAccountBalance(String accountId, String currency, BigDecimal amount);

    void processAccountBalanceBooking(String accountId, String bookingId);

    void relieveAccountBalanceBooking(String accountId, String bookingId);
}
