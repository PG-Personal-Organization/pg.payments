package pg.payments.api.accounts;

import java.math.BigDecimal;

public interface AccountModel {
    String getAccountId();
    String getAccountNumber();
    BigDecimal getAvailableBalance(String currency);
    BigDecimal getBookedBalance(String currency);
}
