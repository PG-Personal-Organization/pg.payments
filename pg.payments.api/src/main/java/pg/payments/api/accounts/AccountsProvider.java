package pg.payments.api.accounts;

import lombok.NonNull;

import java.util.Optional;

public interface AccountsProvider {
    Optional<AccountModel> getAccount(@NonNull String accountNumber);
}
