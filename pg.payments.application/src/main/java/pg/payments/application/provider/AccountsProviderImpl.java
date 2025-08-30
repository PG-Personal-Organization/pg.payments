package pg.payments.application.provider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pg.accounts.api.AccountQuery;
import pg.accounts.api.AccountView;
import pg.accounts.domain.AccountViewUsage;
import pg.context.auth.api.context.provider.ContextProvider;
import pg.context.auth.domain.context.UserContext;
import pg.lib.remote.cqrs.executors.RemoteCqrsModuleServiceExecutor;
import pg.payments.api.accounts.AccountsProvider;
import pg.payments.application.payments.management.exception.AccountNotAccessibleByUser;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountsProviderImpl implements AccountsProvider {
    private final RemoteCqrsModuleServiceExecutor serviceExecutor;
    private final ContextProvider contextProvider;

    @Override
    public Optional<AccountView> getAccount(final @NonNull String accountNumber, final AccountViewUsage usage) {
        var query = AccountQuery.of(accountNumber, usage);
        var result = serviceExecutor.execute(query, "accounts");

        if (!result.getFound()) {
            return Optional.empty();
        }

        if (!result.getHasAccess()) {
            final Optional<UserContext> userContext = contextProvider.tryToGetUserContext();
            throw new AccountNotAccessibleByUser(accountNumber, userContext.map(UserContext::getUserId).orElseThrow(), usage);
        }

        return Optional.of(result.getAccount());
    }
}
