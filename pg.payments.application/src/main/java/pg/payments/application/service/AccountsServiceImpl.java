package pg.payments.application.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pg.accounts.api.booking.BookAccountBalanceCommand;
import pg.accounts.api.booking.ProcessAccountBalanceCommand;
import pg.accounts.api.booking.RelieveAccountBalanceCommand;
import pg.lib.remote.cqrs.executors.RemoteCqrsModuleServiceExecutor;
import pg.payments.api.accounts.AccountsService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountsServiceImpl implements AccountsService {
    private static final String ACCOUNTS_MODULE_NAME = "accounts";

    private final RemoteCqrsModuleServiceExecutor serviceExecutor;

    @Override
    public String bookAccountBalance(final @NonNull String accountId, final @NonNull String currency, final @NonNull BigDecimal amount) {
        var command = BookAccountBalanceCommand.of(accountId, currency, amount);
        return serviceExecutor.execute(command, ACCOUNTS_MODULE_NAME);
    }

    @Override
    public void processAccountBalanceBooking(final String accountId, final String bookingId) {
        var command = ProcessAccountBalanceCommand.of(accountId, bookingId);
        serviceExecutor.execute(command, ACCOUNTS_MODULE_NAME);
    }

    @Override
    public void relieveAccountBalanceBooking(final String accountId, final String bookingId) {
        var command = RelieveAccountBalanceCommand.of(accountId, bookingId);
        serviceExecutor.execute(command, ACCOUNTS_MODULE_NAME);
    }
}
