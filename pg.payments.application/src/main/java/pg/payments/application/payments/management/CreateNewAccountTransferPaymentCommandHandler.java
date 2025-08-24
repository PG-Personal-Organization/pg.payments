package pg.payments.application.payments.management;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pg.lib.cqrs.command.CommandHandler;
import pg.payments.api.accounts.AccountModel;
import pg.payments.api.accounts.AccountsProvider;
import pg.payments.api.accounts.AccountsService;
import pg.payments.api.payments.management.CreateNewAccountTransferPaymentCommand;
import pg.payments.application.payments.management.exception.CreditedAccountNotExistException;
import pg.payments.application.payments.management.exception.TransferAccountInsufficientBalanceException;
import pg.payments.application.payments.management.exception.TransferAccountNotExistException;
import pg.payments.domain.management.AccountTransferDto;
import pg.payments.domain.management.PaymentsService;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CreateNewAccountTransferPaymentCommandHandler implements CommandHandler<CreateNewAccountTransferPaymentCommand, String> {
    private final PaymentsService paymentsService;
    private final AccountsProvider accountsProvider;
    private final AccountsService accountsService;

    @Override
    @Transactional
    public String handle(final CreateNewAccountTransferPaymentCommand command) {
        var creditedAccountNumber = command.getCreditedAccountNumber();
        var creditedAccount = accountsProvider.getAccount(creditedAccountNumber).orElseThrow(() -> new CreditedAccountNotExistException(creditedAccountNumber));

        var transferAccountNumber = command.getTransferAccountNumber();
        var transferAccount = accountsProvider.getAccount(transferAccountNumber).orElseThrow(() -> new TransferAccountNotExistException(transferAccountNumber));

        var transferAmount = command.getAmount();
        var currency = command.getCurrency();
        if (!hasTransferAccountSufficientBalance(transferAmount, currency, transferAccount)) {
            throw new TransferAccountInsufficientBalanceException(transferAccountNumber, transferAmount, currency);
        }
        accountsService.bookAccountBalance(transferAccount.getAccountId(), currency, transferAmount);

        var transferData = AccountTransferDto.builder()
                .creditedAccountId(creditedAccount.getAccountId())
                .creditedAccountNumber(creditedAccountNumber)
                .transferAccountId(transferAccount.getAccountId())
                .transferAccountNumber(transferAccountNumber)
                .build();
        return paymentsService.createNewAccountTransferPayment(command.getRecordId(), transferData, transferAmount, currency, command.getUserId());
    }

    private static boolean hasTransferAccountSufficientBalance(final BigDecimal amount, final String currency, final AccountModel transferAccount) {
        return transferAccount.getAvailableBalance(currency).subtract(transferAccount.getBookedBalance(currency)).compareTo(amount) < 0;
    }
}
