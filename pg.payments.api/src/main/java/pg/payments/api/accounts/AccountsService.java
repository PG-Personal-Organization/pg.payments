package pg.payments.api.accounts;

import java.math.BigDecimal;

public interface AccountsService {
    void bookAccountBalance(String accountId, String currency, BigDecimal amount);
}
