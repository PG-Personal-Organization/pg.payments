package pg.payments.application.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pg.lib.remote.cqrs.executors.RemoteCqrsModuleServiceExecutor;
import pg.payments.api.accounts.AccountsService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountsServiceImpl implements AccountsService {
    private final RemoteCqrsModuleServiceExecutor serviceExecutor;

    @Override
    public void bookAccountBalance(final @NonNull String accountId, final @NonNull String currency, final @NonNull BigDecimal amount) {
        // TODO implement command usage from accounts-api
    }
}
