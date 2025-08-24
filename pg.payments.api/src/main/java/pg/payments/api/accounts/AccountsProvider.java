package pg.payments.api.accounts;

import lombok.NonNull;
import pg.accounts.api.model.AccountView;
import pg.accounts.domain.AccountViewUsage;

import java.util.Optional;

public interface AccountsProvider {
    Optional<AccountView> getAccount(@NonNull String accountNumber, AccountViewUsage usage);
}
