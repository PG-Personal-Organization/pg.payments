package pg.payments.application.provider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pg.lib.remote.cqrs.executors.RemoteCqrsModuleServiceExecutor;
import pg.payments.api.accounts.AccountModel;
import pg.payments.api.accounts.AccountsProvider;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountsProviderImpl implements AccountsProvider {
    private final RemoteCqrsModuleServiceExecutor serviceExecutor;

    @Override
    public Optional<AccountModel> getAccount(final @NonNull String accountNumber) {
        // TODO call accounts query
        return Optional.empty();
    }

}
